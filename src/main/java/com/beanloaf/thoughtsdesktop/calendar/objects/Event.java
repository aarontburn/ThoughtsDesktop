package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {

    private String title;
    private String description;

    private LocalTime startTime;
    private LocalTime endTime;

    private LocalDate startDate;
    private LocalDate endDate;

    private Weekday weekday;



    private boolean isComplete;

    private DayEvent linkedDayEvent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public Event(final String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }


    public boolean isComplete() {
        return isComplete;
    }

    public Event setCompleted(boolean complete) {
        isComplete = complete;
        return this;
    }
    public String getDescription() {
        return description;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;

    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public Event setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;

    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Event setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;

    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Event setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Event setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;

    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Event setWeekday(Weekday weekday) {
        this.weekday = weekday;
        return this;
    }

    public Event setLinkedDayEvent(final DayEvent dayEvent) {
        this.linkedDayEvent = dayEvent;
        return this;
    }

    public DayEvent getLinkedDayEvent() {
        return this.linkedDayEvent;
    }


}
