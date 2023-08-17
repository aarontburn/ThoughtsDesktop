package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ScheduleData {

    private final CalendarView view;
    private String scheduleName;
    private LocalDate startDate, endDate;

    private String id;

    private final Map<Weekday, List<ScheduleEvent>> scheduleMap = new HashMap<>();

    public ScheduleData(final CalendarView view) {
        this(view, UUID.randomUUID().toString());


    }

    public ScheduleData(final CalendarView view, final String id) {
        this.view = view;
        this.id = id;
    }

    public void addEvent(final Weekday weekday, final ScheduleEvent event) {
        List<ScheduleEvent> eventList = scheduleMap.computeIfAbsent(weekday, k -> new ArrayList<>());

        eventList.add(event);
    }

    public void save() {
        view.calendarJson.saveScheduleData(this);

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
