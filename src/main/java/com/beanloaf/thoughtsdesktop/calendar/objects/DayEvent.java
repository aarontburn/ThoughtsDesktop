package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.calendar.views.MonthView;
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

public class DayEvent extends EventBoxLabel implements EventLabel {

    public final static String DAY_EVENT_ID = "dayEvent";

    private final MonthView view;


    private Event event;
    public boolean isReference;
    public final boolean isScheduleEvent;

    private final List<EventLabel> references = new ArrayList<>();


    // Cloning constructor, used to tie the eventbox object to the one in the grid
    public DayEvent(final DayEvent reference, final MonthView view) {
        this(reference.getDate(), reference.getEventTitle(), view, reference.isScheduleEvent);

        this.isReference = true;

        reference.addReference(this);
        this.references.add(reference);

        event.setStartTime(reference.event.getStartTime());
        event.setEndTime(reference.event.getEndTime());
        event.setDescription(reference.event.getDescription());
        event.setId(reference.getEventID());
        event.setCompleted(reference.event.isComplete());

        this.setText(getDisplayTime(event.getStartTime()) + event.getTitle());
    }


    // Constructor for creating a NEW event
    public DayEvent(final LocalDate day, final String eventName, final MonthView view, final boolean isScheduleEvent) {
        this(day, eventName, UUID.randomUUID().toString(), view, isScheduleEvent);
    }


    // Constructor for reading an EXISTING event from file
    public DayEvent(final LocalDate day, final String eventTitle, final String eventID,
                    final MonthView view, final boolean isScheduleEvent) {
        super(eventTitle);
        setGraphic(new ImageView(new Image(String.valueOf(MainApplication.class.getResource(isScheduleEvent ? "icons/schedule-icon.png" : "icons/calendar-small-page.png")), 17.5, 17.5, true, true)));

        this.view = view;
        event = new Event(eventTitle);
        event.setStartDate(day);

        this.isScheduleEvent = isScheduleEvent;

        getToolTip().textProperty().bindBidirectional(this.textProperty());
        this.setId(DAY_EVENT_ID);
        this.event.setId(eventID);


        this.getChildren().addListener((ListChangeListener<Node>) change -> {
            for (final Node node : getChildren()) {
                node.setId(DAY_EVENT_ID);
                if (node.getClass().getSimpleName().equals("LabeledText")) {
                    final Text text = (Text) node;
                    text.setStrikethrough(this.event.isComplete());
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


    public void setEventTitle(final String eventTitle) {
        this.updateEventTitle(eventTitle);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateEventTitle(eventTitle);
        }
    }

    public void setDescription(final String description) {
        this.updateDescription(description);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateDescription(description);
        }
    }

    public void setStartDate(final LocalDate date) {
        this.updateStartDate(date);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateStartDate(date);
        }
    }

    public void setEndDate(final LocalDate date) {
        // This should not be used.

        this.updateEndDate(date);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateEndDate(date);
        }
    }


    public void setStartTime(final LocalTime startTime) {
        this.updateStartTime(startTime);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateStartTime(startTime);
        }
    }

    public void setEndTime(final LocalTime endTime) {
        this.updateEndTime(endTime);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateEndTime(endTime);
        }

    }


    public void setCompleted(final boolean isCompleted, final boolean save) {
        updateCompletion(isCompleted);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateCompletion(isCompleted);
        }

        if (save) view.saveEvent(this);
    }

    public void addReference(final EventLabel reference) {
        this.references.add(reference);
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
        return this.event.getId();
    }

    public boolean isCompleted() {
        return this.event.isComplete();
    }

    public static String getDisplayTime(final LocalTime time) {
        String formattedTime = "";
        if (time != null) {
            formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a")).replace(" AM", "a").replace(" PM", "p") + " | ";
        }
        return formattedTime;
    }


    @Override
    public boolean equals(final Object other) {
        if (other.getClass() != this.getClass()) return false;

        return this.event.getId().equals(((DayEvent) other).getEventID());

    }

    @Override
    public void updateEventTitle(String eventTitle) {
        event.setTitle(eventTitle);
        this.setText(getDisplayTime(getStartTime()) + eventTitle);
    }

    @Override
    public void updateDescription(String description) {
        this.event.setDescription(description);
    }

    @Override
    public void updateStartDate(LocalDate date) {
        this.event.setStartDate(date);
    }

    @Override
    public void updateEndDate(LocalDate date) {
        // This should not be used.
        this.event.setEndDate(date);

    }

    @Override
    public void updateStartTime(LocalTime time) {
        this.event.setStartTime(time);
        this.setText(getDisplayTime(time) + event.getTitle());
    }

    @Override
    public void updateEndTime(LocalTime time) {
        this.event.setEndTime(time);
    }

    @Override
    public void updateCompletion(boolean isComplete) {
        this.event.setCompleted(isComplete);

        for (final Node node : this.getChildren()) {
            if (node.getClass().getSimpleName().equals("LabeledText")) {
                final Text text = (Text) node;
                text.setStrikethrough(isComplete);
            }
        }
    }
}
