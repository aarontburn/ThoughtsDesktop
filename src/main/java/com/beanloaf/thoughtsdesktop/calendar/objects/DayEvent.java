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
import java.util.UUID;

public class DayEvent extends EventBoxLabel {

    public final static String DAY_EVENT_ID = "dayEvent";

    private final CalendarView view;


    private final Event event;

    private final String eventID;

    private boolean isCompleted;

    private DayEvent clone;
    public boolean isClone;

    public final boolean isScheduleEvent;


    // Cloning constructor, used to tie the eventbox object to the one in the grid
    public DayEvent(final DayEvent clone, final CalendarView view) {
        this(clone.getDate(), clone.getEventTitle(), view, clone.isScheduleEvent);


        this.isClone = true;
        this.clone = clone;

        event.setStartTime(clone.event.getStartTime());
        event.setEndTime(clone.event.getEndTime());
        event.setDescription(clone.event.getDescription());

        this.isCompleted = clone.isCompleted;

        this.setText(getDisplayTime(event.getStartTime()) + event.getDescription());
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
        if (this.clone != null) {
            clone.event.setStartTime(startTime);
            clone.setText(newText);
        }

    }


    public void setEndTime(final LocalTime endTime) {
        event.setEndTime(endTime);
        if (this.clone != null) {
            clone.event.setEndTime(endTime);
        }

    }

    @Override
    public void setEventTitle(final String eventTitle) {
        event.setTitle(eventTitle);
        this.setText(eventTitle);

        if (this.clone != null) {
            clone.event.setTitle(eventTitle);
            clone.setText(eventTitle);
        }
    }

    public void setDescription(final String description) {
        event.setDescription(description);

        if (this.clone != null) clone.event.setDescription(description);
    }

    public void setDate(final LocalDate day) {
        event.setStartDate(day);

        if (this.clone != null) clone.event.setStartDate(day);
    }

    public void setCompleted(final boolean isCompleted, final boolean save) {
        this.isCompleted = isCompleted;
        if (this.clone != null) {
            clone.isCompleted = isCompleted;

            for (final Node node : clone.getChildren()) {
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

    public void setClone(final DayEvent clone) {
        this.clone = clone;
    }


    public DayEvent getClone() {
        return this.clone;
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
