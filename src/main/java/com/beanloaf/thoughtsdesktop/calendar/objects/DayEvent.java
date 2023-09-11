package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DayEvent extends EventBoxLabel {

    public final static String DAY_EVENT_ID = "dayEvent";

    private final CalendarView view;


    private final Event event;
    private final String eventID;

    private boolean isCompleted;
    public boolean isReference;
    public final boolean isScheduleEvent;

    private final List<DayEvent> references = new ArrayList<>();



    // Cloning constructor, used to tie the eventbox object to the one in the grid
    public DayEvent(final DayEvent reference, final CalendarView view) {
        this(reference.getDate(), reference.getEventTitle(), view, reference.isScheduleEvent);


        this.isReference = true;
        this.references.add(reference);

        event.setStartTime(reference.event.getStartTime());
        event.setEndTime(reference.event.getEndTime());
        event.setDescription(reference.event.getDescription());

        this.isCompleted = reference.isCompleted;

        this.setText(getDisplayTime(event.getStartTime()) + event.getTitle());
    }


    // Constructor for creating a NEW event
    public DayEvent(final LocalDate day, final String eventName, final CalendarView view, final boolean isScheduleEvent) {
        this(day, eventName, UUID.randomUUID().toString(), view, isScheduleEvent);
    }


    // Constructor for reading an EXISTING event from file
    public DayEvent(final LocalDate day, final String eventTitle, final String eventID, final CalendarView view, final boolean isScheduleEvent) {
        super(eventTitle);
        setGraphic(new ImageView(new Image(String.valueOf(MainApplication.class.getResource(isScheduleEvent ? "icons/schedule-icon.png" : "icons/calendar-small-page.png")), 17.5, 17.5, true, true)));

        this.view = view;
        event = new Event(eventTitle);
        event.setStartDate(day);

        this.isScheduleEvent = isScheduleEvent;

        getToolTip().textProperty().bindBidirectional(this.textProperty());
        this.setId(DAY_EVENT_ID);
        this.eventID = eventID;


        this.getChildren().addListener((ListChangeListener<Node>) change -> {
            for (final Node node : getChildren()) {
                node.setId(DAY_EVENT_ID);
                if (node.getClass().getSimpleName().equals("LabeledText")) {
                    final Text text = (Text) node;
                    text.setStrikethrough(isCompleted);
                }
            }
        });

        this.setOnMouseClicked(e -> onClick());

    }


    @Override
    public void onClick() {
        this.view.selectEvent(this, false);
        Logger.log("Event \"" + this.event.getTitle() + "\" was pressed.");
    }


    public void setStartTime(final LocalTime startTime) {
        event.setStartTime(startTime);
        final String newText = getDisplayTime(startTime) + event.getTitle();
        setText(newText);

        for (final DayEvent dayEvent : references) {
            dayEvent.event.setStartTime(startTime);
            dayEvent.setText(newText);
        }

    }


    public void setEndTime(final LocalTime endTime) {
        event.setEndTime(endTime);

        for (final DayEvent dayEvent : references) {
            dayEvent.event.setEndTime(endTime);
        }

    }

    @Override
    public void setEventTitle(final String eventTitle) {
        event.setTitle(eventTitle);

        this.setText(event.getTitle());

        for (final DayEvent dayEvent : references) {
            dayEvent.event.setTitle(eventTitle);
            dayEvent.setText(eventTitle);
        }
    }

    public void setDescription(final String description) {
        event.setDescription(description);

        for (final DayEvent dayEvent : references) {
            dayEvent.event.setDescription(description);
        }
    }

    public void setDate(final LocalDate day) {
        event.setStartDate(day);

        for (final DayEvent dayEvent : references) {
            dayEvent.event.setStartDate(day);
        }
    }

    public void setCompleted(final boolean isCompleted, final boolean save) {
        this.isCompleted = isCompleted;


        for (final DayEvent dayEvent : references) {
            dayEvent.isCompleted = isCompleted;
            for (final Node node : dayEvent.getChildren()) {
                if (node.getClass().getSimpleName().equals("LabeledText")) {
                    final Text text = (Text) node;
                    text.setStrikethrough(isCompleted);
                }
            }
        }

        if (getChildren().size() > 0) {
            for (final Node node : getChildren()) {
                if (node.getClass().getSimpleName().equals("LabeledText")) {
                    final Text text = (Text) node;
                    text.setStrikethrough(isCompleted);
                }
            }

        }


        if (save) view.saveEvent(this);
    }

    public void addReference(final DayEvent reference) {
        this.references.add(reference);
    }


    public List<DayEvent> getReferences() {
        return this.references;
    }


    @Override
    public String getEventTitle() {
        return this.event.getTitle();
    }

    public LocalDate getDate() {
        return this.event.getStartDate();
    }

    public LocalTime getStartTime() {
        return this.event.getStartTime();
    }

    public LocalTime getEndTime() {
        return this.event.getEndTime();
    }

    public String getDescription() {
        return this.event.getDescription();
    }

    public String getEventID() {
        return this.eventID;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public String getDisplayTime(final LocalTime time) {
        String formattedTime = "";
        if (time != null) {
            formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a")) + " | ";
            if (formattedTime.contains("AM")) {
                formattedTime = formattedTime.replace(" AM", "a");
            } else {
                formattedTime = formattedTime.replace(" PM", "p");

            }
        }
        return formattedTime;
    }


    @Override
    public boolean equals(final Object other) {
        if (other.getClass() != this.getClass()) return false;

        return this.eventID.equals(((DayEvent) other).getEventID());

    }

}
