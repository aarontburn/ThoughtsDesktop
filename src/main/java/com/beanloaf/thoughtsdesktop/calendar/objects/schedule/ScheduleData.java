package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;

import java.time.LocalDate;
import java.util.*;

public class ScheduleData implements TypedEvent {


    private String scheduleName = "";
    private String displayColor;
    private LocalDate startDate, endDate;
    private final String id;

    private final Map<String, ScheduleEvent> scheduleEventMap = new HashMap<>();

    private final List<ScheduleBoxItem> references = new ArrayList<>();


    public ScheduleData() {
        this(UUID.randomUUID().toString());
    }

    public ScheduleData(final String id) {
        this.id = id;
    }

    public void addEvent(final ScheduleEvent event) {
        scheduleEventMap.put(event.getId(), event);
    }

    public void setEvents(final List<ScheduleListItem> listItems) {
        scheduleEventMap.clear();

        for (final ScheduleListItem l : listItems) {
            scheduleEventMap.put(l.getEvent().getId(), l.getEvent());
        }

    }

    public ScheduleEvent getEvent(final String eventId) {
        return scheduleEventMap.get(eventId);
    }


    public List<ScheduleEvent> getScheduleEventList() {
        return new ArrayList<>(this.scheduleEventMap.values());
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


    public void addReference(final ScheduleBoxItem reference) {
        this.references.add(reference);
    }

    public void setScheduleName(final String name) {
        this.scheduleName = name;

        for (final ScheduleBoxItem reference : references) {
            reference.updateScheduleNameLabel();
        }
    }

    public void setStartDate(final LocalDate date) {
        this.startDate = date;
        for (final ScheduleBoxItem reference : references) {
            reference.updateStartDateLabelText();
        }
    }

    public void setEndDate(final LocalDate date) {
        this.endDate = date;
        for (final ScheduleBoxItem reference : references) {
            reference.updateEndDateLabelText();
        }
    }


    public void setDisplayColor(final String color) {
        this.displayColor = color;
    }

    public String getDisplayColor() {
        return this.displayColor;
    }


    @Override
    public String toString() {
        return "ScheduleData{" +
                ", Schedule Name: '" + scheduleName + '\'' +
                ", Start Date: " + startDate +
                ", End Date: " + endDate +
                ", ID: '" + id + '\'' +
                ", Schedule Event List: " + scheduleEventMap +
                '}';
    }

    @Override
    public Types getEventType() {
        return Types.SCHEDULE;
    }
}
