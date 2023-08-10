package com.beanloaf.thoughtsdesktop.objects.calendar;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.views.CalendarView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class DayEvent extends Label {

    public final static String DAY_EVENT_ID = "dayEvent";

    private final CalendarView view;

    private LocalDate day;
    private LocalTime time;

    private String eventTitle;

    private String description;


    private DayEvent clone;

    private final String eventID;

    public boolean isClone;


    // TODO: Generate unique eventID


    // Cloning constructor
    public DayEvent(final DayEvent dayEvent, final CalendarView view) {
        this(dayEvent.getDate(), dayEvent.getEventTitle(), view);

        this.isClone = true;
        this.clone = dayEvent;
        this.time = dayEvent.time;
        this.description = dayEvent.description;

        if (time != null) {
            this.setText(time.getHour() + ":" + time.getMinute() + " | " + eventTitle);
        } else {
            this.setText(eventTitle);
        }

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


        this.getChildren().addListener((ListChangeListener<Node>) change -> getChildren().get(0).setId(DAY_EVENT_ID));

        this.setOnMouseClicked(e -> onClick());

    }


    public void onClick() {
        this.view.selectEvent(this, false);

        Logger.log("Event \"" + this.eventTitle + "\" was pressed.");
    }



    public void setTime(final LocalTime time) {
        this.time = time;


        if (time != null) {
            setText(time.getHour() + ":" + time.getMinute() + " | " + eventTitle);
        } else {
            setText(eventTitle);
        }

        if (this.clone != null) setTimeClone(time);

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



        if (this.clone != null) setEventNameClone(eventTitle);




    }

    public void setDescription(final String description) {
        this.description = description;

        if (this.clone != null) setDescriptionClone(description);


    }

    public void setDate(final LocalDate day) {
        this.day = day;
        if (this.clone != null) setDateClone(day);


    }

    public void setClone(final DayEvent clone) {
        this.clone = clone;
    }


    public DayEvent getClone() {
        return this.clone;
    }

    public void setEventNameClone(final String eventName) {
        clone.eventTitle = eventName;
        clone.setText(eventName);
    }

    public void setDescriptionClone(final String description) {
        clone.description = description;

    }

    public void setDateClone(final LocalDate day) {
        clone.day = day;

    }

    private void setTimeClone(final LocalTime time) {
        clone.time = time;
        if (time != null) {
            clone.setText(time.getHour() + ":" + time.getMinute() + " | " + eventTitle);
        } else {
            clone.setText(eventTitle);
        }
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
