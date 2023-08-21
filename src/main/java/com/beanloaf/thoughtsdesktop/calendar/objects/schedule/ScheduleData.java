package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;

import java.time.LocalDate;
import java.util.*;

public class ScheduleData {


    private String scheduleName = "";
    private LocalDate startDate, endDate;

    private final String id;

    private final List<ScheduleEvent> scheduleEventList = new ArrayList<>();

    public ScheduleData() {
        this(UUID.randomUUID().toString());


    }

    public ScheduleData(final String id) {
        this.id = id;
    }

    public void addEvent(final ScheduleEvent event) {
        scheduleEventList.add(event);
    }


    public List<ScheduleEvent> getScheduleEventList() {
        return this.scheduleEventList;
    }


    public String getId() {
        return this.id;
    }

    public String getScheduleName() {
        return this.scheduleName;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }


    public void setScheduleName(final String name) {
        this.scheduleName = name;
    }

    public void setStartDate(final LocalDate date) {
        this.startDate = date;
    }

    public void setEndDate(final LocalDate date) {
        this.endDate = date;
    }

    @Override
    public String toString() {
        return "ScheduleData{" +
                ", Schedule Name: '" + scheduleName + '\'' +
                ", Start Date: " + startDate +
                ", End Date: " + endDate +
                ", ID: '" + id + '\'' +
                ", Schedule Event List: " + scheduleEventList +
                '}';
    }
}
