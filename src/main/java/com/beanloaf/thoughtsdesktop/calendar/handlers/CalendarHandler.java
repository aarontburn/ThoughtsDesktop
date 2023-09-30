package com.beanloaf.thoughtsdesktop.calendar.handlers;

import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarDay;
import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarMonth;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CalendarHandler {

    private final CalendarMain main;
    private final Map<Pair<Month, Year>, CalendarMonth> activeMonths = new ConcurrentHashMap<>();
    private final Map<String, List<BasicEvent>> canvasEventsMap = new ConcurrentHashMap<>();
    private CalendarMonth currentMonth;
    private CalendarDay selectedDay;
    private BasicEvent selectedEvent;


    public CalendarHandler(final CalendarMain main) {
        this.main = main;

        final LocalDate now = LocalDate.now();
        this.currentMonth = getMonth(now.getMonth(), now.getYear());
    }


    public CalendarDay getDay(final LocalDate date) {
        final Pair<Month, Year> monthYear = new Pair<>(date.getMonth(), Year.of(date.getYear()));

        CalendarMonth month = activeMonths.get(monthYear);
        if (month == null) {
            month = new CalendarMonth(date.getMonth(), date.getYear(), main);
            activeMonths.put(monthYear, month);
        }

        return month.getDay(date.getDayOfMonth());
    }

    public CalendarMonth getMonth(final Month month, final int year) {
        final Pair<Month, Year> monthYear = new Pair<>(month, Year.of(year));
        CalendarMonth m = activeMonths.get(monthYear);

        if (m == null) {
            m = new CalendarMonth(month, year, main);
            activeMonths.put(monthYear, m);
        }

        return m;
    }

    public CalendarMonth getMonth(final LocalDate date) {
        return getMonth(date.getMonth(), date.getYear());
    }

    public void removeInactiveMonths() {
        for (final Pair<Month, Year> key : activeMonths.keySet()) {
            final CalendarMonth calendarMonth = activeMonths.get(key);

            if (calendarMonth.getNumDaysWithEvents() == 0) {
                activeMonths.remove(key);
            }
        }
    }

    public void setCurrentMonth(final Month month, final int year) {
        this.currentMonth = getMonth(month, year);
    }

    public CalendarMonth getCurrentMonth() {
        return this.currentMonth;
    }

    public CalendarDay getSelectedDay() {
        return this.selectedDay;
    }

    public void setSelectedDay(final CalendarDay day) {
        this.selectedDay = day;
    }

    public BasicEvent getSelectedEvent() {
        return this.selectedEvent;
    }

    public void setSelectedEvent(final BasicEvent event) {
        this.selectedEvent = event;
    }

    public List<BasicEvent> getAllCanvasEvents() {
        final List<BasicEvent> canvasEventList = new ArrayList<>();
        for (final List<BasicEvent> l : canvasEventsMap.values()) {
            canvasEventList.addAll(l);
        }

        return canvasEventList;
    }

    public void addCanvasEvent(final String className, final BasicEvent event) {
        this.canvasEventsMap.computeIfAbsent(className, k -> new ArrayList<>()).add(event);
    }

    public List<BasicEvent> getCanvasEventsByClass(final String className) {
        return canvasEventsMap.get(className);
    }


}
