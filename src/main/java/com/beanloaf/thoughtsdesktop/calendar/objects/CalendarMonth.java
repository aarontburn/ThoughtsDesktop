package com.beanloaf.thoughtsdesktop.calendar.objects;


import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class CalendarMonth {

    private final CalendarView view;
    private final Month month;

    private final Integer year;

    private final LocalDate monthInfo;


    private final Map<Integer, CalendarDay> monthMap = new HashMap<>();

    private int daysWithEvents;



    public CalendarMonth(final Month month, final CalendarView view) {
        this(month, Calendar.getInstance().get(Calendar.YEAR), view);
    }

    public CalendarMonth(final Month month, final Integer year, final CalendarView view) {
        this.month = month;
        this.year = year;
        this.view = view;
        monthInfo = LocalDate.of(year, month, 1);

    }

    public void addDay(final int day, final CalendarDay calendarDay) {
        this.monthMap.put(day, calendarDay);
    }

    public CalendarDay getDay(final int day) {
        CalendarDay calendarDay = monthMap.get(day);

        if (calendarDay == null) {
            calendarDay = new CalendarDay(getYear(), getMonth(), day, view);
            monthMap.put(day, calendarDay);
        }

        return this.monthMap.get(day);
    }

    public int getNumDaysWithEvents() {
        for (final CalendarDay day : monthMap.values()) {
            if (day.getEvents().length > 0) daysWithEvents++;
        }

        return this.daysWithEvents;
    }

    public Month getMonth() {
        return this.month;
    }

    public Integer getYear() {
        return this.year;
    }

    public int getStartingDayOfWeek() {
       return monthInfo.getDayOfWeek().getValue();

    }

    public int getMonthLength() {
        return monthInfo.lengthOfMonth();

    }

    public CalendarMonth getNextMonth() {
        return month.getValue() == 12 ? new CalendarMonth(Month.JANUARY, year + 1, view) : new CalendarMonth(Month.of(month.getValue() + 1), year, view);
    }

    public CalendarMonth getPreviousMonth() {
        return month.getValue() == 1 ? new CalendarMonth(Month.DECEMBER, year - 1, view) : new CalendarMonth(Month.of(month.getValue() - 1), year, view);
    }

    @Override
    public String toString() {
        return this.month + " : " + this.year;
    }





}
