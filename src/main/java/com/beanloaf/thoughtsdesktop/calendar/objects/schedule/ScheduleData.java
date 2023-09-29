package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;
import com.beanloaf.thoughtsdesktop.handlers.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ScheduleData implements TypedEvent {


    private String scheduleName = "";
    private String displayColor;
    private LocalDate startDate, endDate;
    private final String id;

    private final Map<Weekday, Map<String, BasicEvent>> scheduleEventMap = new HashMap<>();
    private final List<ScheduleBoxItem> references = new ArrayList<>();


    public ScheduleData() {
        this(UUID.randomUUID().toString());
    }

    public ScheduleData(final String id) {
        this.id = id;
    }

    public void addEvent(final Weekday weekday, final BasicEvent event) {
        scheduleEventMap.computeIfAbsent(weekday, k -> new HashMap<>()).put(event.getId(), event);
    }

    public void addEvent(final String weekdayString, final BasicEvent event) {
        addEvent(Weekday.valueOf(weekdayString), event);
    }

    public void setEvents(final List<ScheduleListItem> listItems) {
        scheduleEventMap.clear();

        for (final ScheduleListItem l : listItems) {
            for (final Weekday weekday : l.getWeekdays()) {
                addEvent(weekday, l.getEvent());
            }
        }
    }

    public BasicEvent getEvent(final String eventId) {
        for (final Map<String, BasicEvent> uidEventMap : scheduleEventMap.values()) {
            if (uidEventMap.get(eventId) != null) {
                return uidEventMap.get(eventId);
            }
        }

        Logger.log("Error: Could not find event with uid: " + eventId + " from ScheduleData: " + scheduleName);
        return null;
    }


    public Map<Weekday, Map<String, BasicEvent>> getScheduleEventList() {
        return this.scheduleEventMap;
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

    public void setStartDate(final String stringDate) {
        try {
            setStartDate(LocalDate.parse(stringDate));
        } catch (DateTimeParseException e) {
            setStartDate((LocalDate) null);
        }
    }

    public void setEndDate(final LocalDate date) {
        this.endDate = date;
        for (final ScheduleBoxItem reference : references) {
            reference.updateEndDateLabelText();
        }
    }

    public void setEndDate(final String stringDate) {
        try {
            setEndDate(LocalDate.parse(stringDate));
        } catch (DateTimeParseException e) {
            setEndDate((LocalDate) null);
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
