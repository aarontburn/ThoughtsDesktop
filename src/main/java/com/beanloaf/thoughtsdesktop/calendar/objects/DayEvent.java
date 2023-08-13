package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DayEvent extends Label {

    public final static String DAY_EVENT_ID = "dayEvent";

    private final CalendarView view;

    private LocalDate day;
    private LocalTime time;
    private String eventTitle;
    private String description;
    private final String eventID;


    private boolean isCompleted;


    private DayEvent clone;

    public boolean isClone;


    // Cloning constructor, used to tie the eventbox object to the one in the grid
    public DayEvent(final DayEvent dayEvent, final CalendarView view) {
        this(dayEvent.getDate(), dayEvent.getEventTitle(), view);

        this.isClone = true;
        this.clone = dayEvent;
        this.time = dayEvent.time;
        this.description = dayEvent.description;
        this.isCompleted = dayEvent.isCompleted;

        this.setText(getDisplayTime(time) + eventTitle);
    }


    // Constructor for creating a NEW event
    public DayEvent(final LocalDate day, final String eventName, final CalendarView view) {
        this(day, eventName, UUID.randomUUID().toString(), view);
    }


    // Constructor for reading an EXISTING event from file
    public DayEvent(final LocalDate day, final String eventTitle, final String eventID, final CalendarView view) {
        super(eventTitle);
        this.view = view;
        this.day = day;

        this.setMaxWidth(Double.MAX_VALUE);
        this.eventTitle = eventTitle;
        this.setId(DAY_EVENT_ID);

        this.eventID = eventID;


        this.getChildren().addListener((ListChangeListener<Node>) change -> {
            getChildren().get(0).setId(DAY_EVENT_ID);
            ((Text) getChildren().get(0)).setStrikethrough(isCompleted);
        });

        this.setOnMouseClicked(e -> onClick());

    }


    public void onClick() {
        this.view.selectEvent(this, false);

        Logger.log("Event \"" + this.eventTitle + "\" was pressed.");
    }


    public void setTime(final LocalTime time) {
        this.time = time;


        final String newText = getDisplayTime(time) + eventTitle;

        setText(newText);

        if (this.clone != null) {
            clone.time = time;
            clone.setText(newText);
        }

    }


    public void setTime(final int hour, final int minute) {
        try {
            setTime(LocalTime.of(hour, minute));
        } catch (DateTimeException e) {
            this.time = null;
        }

    }

    public void setTime(final String hourString, final String minuteString, final String period) {
        try {
            int hour = Integer.parseInt(hourString);
            final int minute = Integer.parseInt(minuteString);

            if (!(period.equals("AM") || period.equals("PM")))
                throw new IllegalArgumentException("Period needs to be AM or PM: " + period);

            final boolean isPM = period.equals("PM");

            if (hour == 12 && !isPM) {
                hour = 0;
            } else if (hour < 12 && isPM) {
                hour += 12;
            }

            if (hour >= 24) hour = 0; // since military time goes from 0 to 23

            setTime(hour, minute);

        } catch (NumberFormatException e) {
            this.time = null;
        }


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
            ((Text) clone.getChildren().get(0)).setStrikethrough(isCompleted);
        }

        if (getChildren().size() > 0) ((Text) getChildren().get(0)).setStrikethrough(isCompleted);


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

    public LocalTime getTime() {
        return this.time;
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


    @Override
    public String toString() {
        return "Year: " + day.getYear() + " Month: " + day.getMonth() + " Day: " + day.getDayOfMonth() + " Desc: " + description + " Time: " + time;

    }

    @Override
    public boolean equals(final Object other) {
        if (other.getClass() != this.getClass()) return false;

        return this.eventID.equals(((DayEvent) other).getEventID());

    }




}
