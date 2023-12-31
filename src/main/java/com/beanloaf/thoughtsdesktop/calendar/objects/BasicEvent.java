package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BasicEvent implements TypedEvent {

    private String title;
    private String description;
    private String id;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private Weekday weekday;
    private boolean isComplete;
    private DayEvent linkedDayEvent;
    private Types eventType = Types.BASIC;
    private String color;
    private String altText;


    private List<EventLabel> referenceList = new ArrayList<>();


    public BasicEvent() {

    }

    public BasicEvent(final String title) {
        this.title = title;
    }


    public String getId() {
        return id;
    }

    public BasicEvent setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public BasicEvent setTitle(String title) {
        this.title = title;
        return this;
    }


    public boolean isComplete() {
        return isComplete;
    }

    public BasicEvent setCompleted(boolean complete) {
        isComplete = complete;

        for (final EventLabel ref : referenceList) {
            ref.updateCompletion(complete);
        }

        return this;
    }

    public String getDescription() {
        return description;
    }

    public BasicEvent setDescription(String description) {
        this.description = description;

        for (final EventLabel ref : referenceList) {
            ref.updateDescription(description);
        }
        return this;

    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public BasicEvent setStartTime(LocalTime startTime) {
        this.startTime = startTime;

        for (final EventLabel ref : referenceList) {
            ref.updateStartTime(startTime);
        }
        return this;

    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public BasicEvent setEndTime(LocalTime endTime) {
        this.endTime = endTime;

        for (final EventLabel ref : referenceList) {
            ref.updateEndTime(endTime);
        }
        return this;

    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public BasicEvent setStartDate(LocalDate startDate) {
        this.startDate = startDate;

        for (final EventLabel ref : referenceList) {
            ref.updateStartDate(startDate);
        }
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BasicEvent setEndDate(LocalDate endDate) {
        this.endDate = endDate;

        for (final EventLabel ref : referenceList) {
            ref.updateEndDate(endDate);
        }
        return this;

    }

    public Weekday getWeekday() {
        return weekday;
    }

    public BasicEvent setWeekday(Weekday weekday) {
        this.weekday = weekday;

        return this;
    }

    public BasicEvent setLinkedDayEvent(final DayEvent dayEvent) {
        this.linkedDayEvent = dayEvent;
        return this;
    }

    public DayEvent getLinkedDayEvent() {
        return this.linkedDayEvent;
    }

    public BasicEvent setDisplayColor(final String color) {
        this.color = color;
        for (final EventLabel ref : referenceList) {
            ref.updateDisplayColor(color);
        }
        return this;
    }

    public String getDisplayColor() {
        return this.color;
    }


    @Override
    public Types getEventType() {
        return this.eventType;
    }

    public BasicEvent setEventType(final Types type) {
        this.eventType = type;
        return this;
    }


    public BasicEvent setAltText(final String altText) {
        this.altText = altText;
        return this;
    }

    public String getAltText() {
        return this.altText;
    }


    public BasicEvent addReference(final EventLabel reference) {
        this.referenceList.add(reference);

        return this;
    }

    public List<EventLabel> getReferences() {
        return this.referenceList;
    }

    @Override
    public String toString() {
        return "Event {" +
                "title: '" + title + '\'' +
                ", description: '" + description + '\'' +
                ", startTime: " + startTime +
                ", endTime: " + endTime +
                ", startDate: " + startDate +
                ", endDate: " + endDate +
                ", weekday: " + weekday +
                ", isComplete: " + isComplete +
                ", id: '" + id + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicEvent event = (BasicEvent) o;
        return isComplete == event.isComplete && Objects.equals(title, event.title) && Objects.equals(description, event.description) && Objects.equals(startTime, event.startTime) && Objects.equals(endTime, event.endTime) && Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate) && weekday == event.weekday && eventType == event.eventType && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, startTime, endTime, startDate, endDate, weekday, isComplete, eventType, id);
    }


}
