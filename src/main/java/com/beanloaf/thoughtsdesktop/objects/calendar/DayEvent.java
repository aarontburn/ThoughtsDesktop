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

    private LocalTime startTime;
    private LocalTime endTime;
    private String description;




    // TODO: Generate unique eventID

    public DayEvent(final DayEvent dayEvent, final CalendarView view) {
        this(dayEvent.getCalendarDay(), dayEvent.getEventName(), view);
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

        this.setOnMouseClicked(e -> {
            this.view.selectEvent(this);

            Logger.log("Event \"" + this.eventName + "\" was pressed.");
        });

    }

    private void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(final int hour, final int minute) {
        try {
            setStartTime(LocalTime.of(hour, minute));
        } catch (DateTimeException e) {
            this.startTime = null;
        }
    }

    public void setStartTime(final String hourString, final String minuteString) {
        try {
            final int hour = Integer.parseInt(hourString);
            final int minute = Integer.parseInt(minuteString);
            setStartTime(hour, minute);

        } catch (NumberFormatException e) {
            this.startTime = null;
        }


    }

    private void setEndTime(final LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setEndTime(final int hour, final int minute) {
        try {
            setEndTime(LocalTime.of(hour, minute));
        } catch (DateTimeException e) {
            this.endTime = null;
        }
    }

    public void setEndTime(final String hourString, final String minuteString) {
        try {
            final int hour = Integer.parseInt(hourString);
            final int minute = Integer.parseInt(minuteString);

            setEndTime(hour, minute);

        } catch (NumberFormatException e) {
            this.endTime = null;
        }

    }



    public void setEventName(final String eventName) {
        this.eventName = eventName;
        this.setText(eventName);
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setDate(final CalendarDay day) {
        this.day = day;
    }




    public String getEventName() {
        return this.eventName;
    }

    public CalendarDay getCalendarDay() {
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






}
