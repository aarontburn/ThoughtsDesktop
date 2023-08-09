package com.beanloaf.thoughtsdesktop.objects.calendar;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.views.CalendarView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.time.DateTimeException;
import java.time.LocalTime;

public class DayEvent extends Label {

    public final static String ID = "dayEvent";

    private final CalendarView view;

    private CalendarDay day;
    private String eventName;

    private LocalTime time;
    private String description;


    private DayEvent clone;




    // TODO: Generate unique eventID

    public DayEvent(final DayEvent dayEvent, final CalendarView view) {
        this(dayEvent.getCalendarDay(), dayEvent.getEventName(), view);


        this.clone = dayEvent;
        this.time = dayEvent.time;
        this.description = dayEvent.description;

    }




    public DayEvent(final CalendarDay day, final String eventName, final CalendarView view) {
        super(eventName);
        this.view = view;
        this.day = day;


        this.setMaxWidth(Double.MAX_VALUE);
        this.eventName = eventName;
        this.setId(ID);

        this.getChildren().addListener((ListChangeListener<Node>) change -> {
            getChildren().get(0).setId(ID);
        });

        this.setOnMouseClicked(e -> onClick());

    }

    public void onClick() {
        this.view.selectEvent(this, false);

        Logger.log("Event \"" + this.eventName + "\" was pressed.");

    }


    public void save() {
        view.calendarJson.addEvent(this);


    }

    public void setTime(final LocalTime time) {
        this.time = time;
        if (this.clone != null) setTimeClone(time);

        save();
    }



    public void setTime(final int hour, final int minute) {
        try {
            setTime(LocalTime.of(hour, minute));
        } catch (DateTimeException e) {
            this.time = null;
        }
        save();

    }

    public void setTime(final String hourString, final String minuteString, final String period) {
        try {
            int hour = Integer.parseInt(hourString);
            final int minute = Integer.parseInt(minuteString);

            if (!(period.equals("AM") || period.equals("PM"))) throw new IllegalArgumentException("Period needs to be AM or PM: " + period);

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




    public void setEventName(final String eventName) {
        this.eventName = eventName;
        this.setText(eventName);

        if (this.clone != null) setEventNameClone(eventName);
        save();


    }

    public void setDescription(final String description) {
        this.description = description;

        if (this.clone != null) setDescriptionClone(description);
        save();


    }

    public void setDate(final CalendarDay day) {
        this.day = day;
        if (this.clone != null) setDateClone(day);
        save();


    }

    public void setClone(final DayEvent clone) {
        this.clone = clone;
    }



    public void setEventNameClone(final String eventName) {
        clone.eventName = eventName;
        clone.setText(eventName);
    }

    public void setDescriptionClone(final String description) {
        clone.description = description;

    }

    public void setDateClone(final CalendarDay day) {
        clone.day = day;

    }

    private void setTimeClone(final LocalTime time) {
        clone.time = time;
    }








    public String getEventName() {
        return this.eventName;
    }

    public CalendarDay getCalendarDay() {
        return this.day;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public String getDescription() {
        return this.description;
    }


    @Override
    public String toString() {
        return "Year: " + day.getYear() + " Month: " + day.getMonth() + " Day: " + day.getDay() + " Desc: " + description + " Time: " + time;

    }




}
