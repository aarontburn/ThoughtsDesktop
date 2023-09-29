package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BasicEvent implements TypedEvent, EventLabel {

    private String title;
    private String description;
    private String id;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private Weekday weekday;
    private boolean isComplete;
    private Types eventType = Types.BASIC;
    private String color;
    private String altText;


    private final List<EventLabel> referenceList = new ArrayList<>();


    public BasicEvent() {

    }

    public BasicEvent(final String title) {
        this.title = title;
    }

    // sets a one way reference from the parent basic event to this. used for schedules.
    public BasicEvent(final BasicEvent event) {
        event.addReference(this);

        this.title = event.getTitle();
        this.id = event.getId();
        this.description = event.getDescription();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.isComplete = event.isCompleted();
        this.color = event.getDisplayColor();
        this.eventType = event.getEventType();
        this.altText = event.getAltText();

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


    public boolean isCompleted() {
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

    public BasicEvent setStartTime(final String stringTime) {
        try {
            return setStartTime(stringTime == null ? null : LocalTime.parse(stringTime));
        } catch (DateTimeParseException e) {
            return setStartTime((LocalTime) null);
        }
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

    public BasicEvent setEndTime(final String stringTime) {
        try {
            return setEndTime(stringTime == null ? null : LocalTime.parse(stringTime));
        } catch (DateTimeParseException e) {
            return setEndTime((LocalTime) null);
        }
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

    public BasicEvent setStartDate(final String stringDate) {
        try {
            return setStartDate(stringDate == null ? null : LocalDate.parse(stringDate));
        } catch (DateTimeParseException e) {
            return setStartDate((LocalDate) null);
        }
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

    public BasicEvent setEndDate(final String stringDate) {
        try {
            return setEndDate(stringDate == null ? null : LocalDate.parse(stringDate));
        } catch (DateTimeParseException e) {
            return setEndDate((LocalDate) null);
        }
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public BasicEvent setWeekday(Weekday weekday) {
        this.weekday = weekday;

        return this;
    }


    public BasicEvent setDisplayColor(String color) {
        if (color == null) {
            color = CH.getRandomColor();
        }

        this.color = color;
        for (final EventLabel ref : referenceList) {
            ref.updateDisplayColor(color);
        }
        return this;
    }

    public String getDisplayColor() {
        if (this.color == null) {
            setDisplayColor(CH.getRandomColor());
        }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicEvent event = (BasicEvent) o;
        return isComplete == event.isComplete && Objects.equals(title, event.title) && Objects.equals(description, event.description) && Objects.equals(startTime, event.startTime) && Objects.equals(endTime, event.endTime) && Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate) && weekday == event.weekday && eventType == event.eventType && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, startTime, endTime, startDate, endDate, weekday, isComplete, eventType, id);
    }


    @Override
    public void updateEventTitle(String title) {
        setTitle(title);
    }

    @Override
    public void updateDescription(String description) {
        setDescription(description);
    }

    @Override
    public void updateStartDate(LocalDate date) {
        setStartDate(date);
    }

    @Override
    public void updateEndDate(LocalDate date) {
        setEndDate(date);
    }

    @Override
    public void updateStartTime(LocalTime time) {
        setStartTime(time);
    }

    @Override
    public void updateEndTime(LocalTime time) {
        setEndTime(time);
    }

    @Override
    public void updateCompletion(boolean isComplete) {
        setCompleted(isComplete);
    }

    @Override
    public void updateDisplayColor(String color) {
        setDisplayColor(color);
    }
}
