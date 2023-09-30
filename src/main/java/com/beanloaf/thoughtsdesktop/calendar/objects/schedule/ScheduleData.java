package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.EventLabel;
import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;
import com.beanloaf.thoughtsdesktop.handlers.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ScheduleData implements TypedEvent {


    private final String id;
    private final Map<Weekday, Map<String, BasicEvent>> scheduleEventMap = new HashMap<>();
    private final Map<BasicEvent, Set<Weekday>> weekdaysByEventMap = new HashMap<>();
    private final List<EventLabel> references = new ArrayList<>();
    private final Map<String, Set<LocalDate>> completedDatesByUid = new HashMap<>();
    private String scheduleName = "";
    private String displayColor;
    private LocalDate startDate, endDate;


    public ScheduleData() {
        this(UUID.randomUUID().toString());
    }

    public ScheduleData(final String id) {
        this.id = id;
    }

    public void addEvent(final Weekday weekday, final BasicEvent event) {
        scheduleEventMap.computeIfAbsent(weekday, k -> new HashMap<>()).put(event.getId(), event);
        weekdaysByEventMap.computeIfAbsent(event, k -> new HashSet<>()).add(weekday);
    }

    public void addEvent(final String weekdayString, final BasicEvent event) {
        addEvent(Weekday.valueOf(weekdayString), event);
    }

    public void setEvents(final List<ScheduleListItem> listItems) {
        scheduleEventMap.clear();
        weekdaysByEventMap.clear();
        for (final ScheduleListItem l : listItems) {
            for (final Weekday weekday : l.getWeekdays()) {
                l.getEvent().setStartDate(getStartDate());
                addEvent(weekday, l.getEvent());
            }
        }
    }


    public Map<Weekday, Map<String, BasicEvent>> getScheduleEventList() {
        return this.scheduleEventMap;
    }

    public Map<BasicEvent, Set<Weekday>> getWeekdaysByEventMap() {
        return this.weekdaysByEventMap;
    }


    public String getId() {
        return this.id;
    }

    public String getScheduleName() {
        return this.scheduleName;
    }

    public void setScheduleName(final String name) {
        this.scheduleName = name;

        for (final EventLabel reference : references) {
            reference.updateEventTitle(name);
        }
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(final LocalDate date) {
        this.startDate = date;
        for (final EventLabel reference : references) {
            reference.updateStartDate(date);
        }
    }

    public void setStartDate(final String stringDate) {
        try {
            setStartDate(LocalDate.parse(stringDate));
        } catch (DateTimeParseException e) {
            setStartDate((LocalDate) null);
        }
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(final LocalDate date) {
        this.endDate = date;
        for (final EventLabel reference : references) {
            reference.updateEndDate(date);
        }
    }

    public void setEndDate(final String stringDate) {
        try {
            setEndDate(LocalDate.parse(stringDate));
        } catch (DateTimeParseException e) {
            setEndDate((LocalDate) null);
        }
    }

    public void addReference(final ScheduleBoxItem reference) {
        this.references.add(reference);
    }

    public String getDisplayColor() {
        if (this.displayColor == null) {
            this.displayColor = CH.getRandomColor();
        }
        return this.displayColor;
    }

    public void setDisplayColor(final String color) {
        this.displayColor = color;
    }

    public void setCompletedDay(final BasicEvent event, final boolean isComplete) {
        setCompletedDay(event.getEventSource().getId(), event.getStartDate(), isComplete);
    }

    public void setCompletedDay(final String eventUid, final LocalDate date, final boolean isComplete) {
        if (isComplete) {
            completedDatesByUid.computeIfAbsent(eventUid, k -> new HashSet<>()).add(date);
        } else {
            completedDatesByUid.computeIfAbsent(eventUid, k -> new HashSet<>()).remove(date);
        }
        Logger.log(scheduleName + ": " +  completedDatesByUid.get(eventUid));
    }

    public Set<LocalDate> getCompletedDatesByUid(final String uid) {
        return this.completedDatesByUid.get(uid);
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
