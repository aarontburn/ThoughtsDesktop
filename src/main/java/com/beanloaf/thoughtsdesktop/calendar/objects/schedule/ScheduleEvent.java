package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.Weekday;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ScheduleEvent {



    private final List<Weekday> weekdays = new ArrayList<>();

    private String scheduleEventName = "";
    private LocalTime startTime;
    private LocalTime endTime;
    private String description = "";

    private String id;


    public ScheduleEvent(final String scheduleEventName, final String id) {
        this.scheduleEventName = scheduleEventName;
        this.id = id;
    }

    public void setScheduleEventName(final String newName) {
        this.scheduleEventName = newName;
    }

    public String getScheduleEventName() {
        return this.scheduleEventName;
    }

    public void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    public String getDescription() {
        return this.description;
    }


    public void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
    }


    public void setStartTime(final String hourString, final String minuteString, final String period) {
        setStartTime(CH.validateStringIntoTime(hourString, minuteString, period));
    }



    public void setEndTime(final LocalTime endTime) {
        this.endTime = endTime;
    }


    public void setEndTime(final String hourString, final String minuteString, final String period) {
        setEndTime(CH.validateStringIntoTime(hourString, minuteString, period));
    }



    public LocalTime getStartTime() {
        return this.startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public String getId() {
        return this.id;
    }

    public void addWeekday(final String weekdayString) {
        final Weekday weekday = Weekday.getWeekdayByString(weekdayString);

        if (!this.weekdays.contains(weekday)) this.weekdays.add(Weekday.getWeekdayByString(weekdayString));
    }

    public List<String> getWeekdays() {
        final List<String> stringList = new ArrayList<>();

        Collections.sort(weekdays);
        for (final Weekday weekday : weekdays) {
            stringList.add(weekday.name());
        }

        return stringList;

    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final ScheduleEvent event = (ScheduleEvent) other;
        return Objects.equals(weekdays, event.weekdays) && Objects.equals(scheduleEventName, event.scheduleEventName) && Objects.equals(startTime, event.startTime) && Objects.equals(endTime, event.endTime) && Objects.equals(description, event.description) && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekdays, scheduleEventName, startTime, endTime, description, id);
    }

    @Override
    public String toString() {
        return "ScheduleEvent{" +
                "weekdays=" + weekdays +
                ", scheduleEventName='" + scheduleEventName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", description='" + description + '\'' +
                ", eventid='" + id + '\'' +
                '}';
    }
}
