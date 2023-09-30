package com.beanloaf.thoughtsdesktop.calendar.objects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasClass {

    private String className;
    private String color;
    private final Map<String, BasicEvent> eventList = new HashMap<>();

    private LocalDate earliestDate;
    private LocalDate latestDate;

    private boolean isHidden;


    public CanvasClass(final String className, final String color) {
        this.className = className;
        this.color = color;
    }


    public void addEvent(final BasicEvent event) {
        this.eventList.put(event.getId(), event);

        if (earliestDate == null || event.getStartDate().isBefore(earliestDate)) {
            earliestDate = event.getStartDate();
        }

        if (latestDate == null || event.getStartDate().isAfter(latestDate)) {
            latestDate = event.getStartDate();
        }

    }

    public BasicEvent getEvent(final String uid) {
        return eventList.get(uid);
    }

    public List<String> getUidList() {
        return new ArrayList<>(eventList.keySet());
    }

    public List<BasicEvent> getEvents() {
        return new ArrayList<>(eventList.values());
    }


    public String getClassName() {
        return this.className;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(final String color) {
        this.color = color;
    }


    public LocalDate getStartDate() {
        return this.earliestDate;
    }

    public LocalDate getEndDate() {
        return this.latestDate;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public void setHidden(final boolean isHidden) {
        this.isHidden = isHidden;
    }









}
