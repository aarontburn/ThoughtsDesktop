package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;

import java.time.LocalDate;
import java.util.*;

public class ScheduleData {

    private final CalendarView view;
    private String scheduleName;
    private LocalDate startDate, endDate;

    private String id;

    private final List<Schedule> scheduleList = new ArrayList<>();

    public ScheduleData(final CalendarView view) {
        this(view, UUID.randomUUID().toString());


    }

    public ScheduleData(final CalendarView view, final String id) {
        this.view = view;
        this.id = id;
    }

    public void addEvent(final Schedule event) {
        scheduleList.add(event);
    }

    public void save() {
        view.calendarJson.saveScheduleData(this);
    }

    public List<Schedule> getScheduleList() {
        return this.scheduleList;
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



}
