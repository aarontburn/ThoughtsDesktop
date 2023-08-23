package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DayEvent extends Label {

    public final static String DAY_EVENT_ID = "dayEvent";

    private final CalendarView view;

    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String eventTitle;
    private String description;
    private final String eventID;


    private boolean isCompleted;

    private DayEvent clone;
    public boolean isClone;


    // Cloning constructor, used to tie the eventbox object to the one in the grid
    public DayEvent(final DayEvent clone, final CalendarView view) {
        this(clone.getDate(), clone.getEventTitle(), view);


        this.isClone = true;
        this.clone = clone;
        this.startTime = clone.startTime;
        this.endTime = clone.endTime;
        this.description = clone.description;
        this.isCompleted = clone.isCompleted;

        this.setText(getDisplayTime(startTime) + eventTitle);
    }


    // Constructor for creating a NEW event
    public DayEvent(final LocalDate day, final String eventName, final CalendarView view) {
        this(day, eventName, UUID.randomUUID().toString(), view);
    }


    // Constructor for reading an EXISTING event from file
    public DayEvent(final LocalDate day, final String eventTitle, final String eventID, final CalendarView view) {
        super(eventTitle, new ImageView(new Image(String.valueOf(MainApplication.class.getResource("icons/calendar-small-page.png")), 17.5, 17.5, true, true)));

        this.view = view;
        this.day = day;


        final Tooltip tooltip = new Tooltip(eventTitle);
        tooltip.setShowDelay(Duration.seconds(0.5));
        this.setTooltip(tooltip);

        this.getStyleClass().add("day-event");
        this.setMaxWidth(Double.MAX_VALUE);
        this.eventTitle = eventTitle;
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


    public void onClick() {
        this.view.selectEvent(this, false);

        Logger.log("Event \"" + this.eventTitle + "\" was pressed.");
    }


    public void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
        final String newText = getDisplayTime(startTime) + eventTitle;
        setText(newText);
        if (this.clone != null) {
            clone.startTime = startTime;
            clone.setText(newText);
        }

    }


    public void setStartTime(final int hour, final int minute) {
        try {
            setStartTime(LocalTime.of(hour, minute));
        } catch (DateTimeException e) {
            this.startTime = null;
        }
    }

    public void setStartTime(final String hourString, final String minuteString, final String period) {
        setStartTime(CH.validateStringIntoTime(hourString, minuteString, period));
    }



    public void setEndTime(final LocalTime endTime) {
        this.endTime = endTime;
        if (this.clone != null) {
            clone.startTime = endTime;
        }

    }

    public void setEndTime(final String hourString, final String minuteString, final String period) {
        setEndTime(CH.validateStringIntoTime(hourString, minuteString, period));
    }




    public void setEventTitle(final String eventTitle) {
        this.eventTitle = eventTitle;
        this.setText(eventTitle);

        if (this.clone != null) {
            clone.eventTitle = eventTitle;
            clone.setText(eventTitle);
        }
    }

    public void setDescription(final String description) {
        this.description = description;

        if (this.clone != null) clone.description = description;
    }

    public void setDate(final LocalDate day) {
        this.day = day;

        if (this.clone != null) clone.day = day;
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


    public String getEventTitle() {
        return this.eventTitle;
    }

    public LocalDate getDate() {
        return this.day;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public String getDescription() {
        return this.description;
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


//    @Override
//    public String toString() {
//        return "Year: " + day.getYear() + " Month: " + day.getMonth() + " Day: " + day.getDayOfMonth() + " Desc: " + description + " Time: " + time;
//
//    }

    @Override
    public boolean equals(final Object other) {
        if (other.getClass() != this.getClass()) return false;

        return this.eventID.equals(((DayEvent) other).getEventID());

    }


}
