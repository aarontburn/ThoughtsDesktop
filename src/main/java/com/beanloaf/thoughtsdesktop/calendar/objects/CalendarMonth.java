package com.beanloaf.thoughtsdesktop.calendar.objects;


import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class CalendarMonth {

    private final CalendarMain main;
    private final Month month;

    private final Integer year;

    private final LocalDate monthInfo;


    private final Map<Integer, CalendarDay> monthMap = new HashMap<>();

    private int daysWithEvents;



    public CalendarMonth(final Month month, final Integer year, final CalendarMain main) {
        this.month = month;
        this.year = year;
        this.main = main;
        monthInfo = LocalDate.of(year, month, 1);

    }

    public void addDay(final int day, final CalendarDay calendarDay) {
        this.monthMap.put(day, calendarDay);
    }

    public CalendarDay getDay(final int day) {
        if (day > getMonthLength()) {
            throw new IllegalArgumentException("Day is greater than month length: Day: " + day + " Month Length: " + getMonthLength());
        }


        CalendarDay calendarDay = monthMap.get(day);

        if (calendarDay == null) {
            calendarDay = new CalendarDay(getYear(), getMonth(), day, main);
            monthMap.put(day, calendarDay);
        }

        return calendarDay;
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
        return month.getValue() == 12 ? new CalendarMonth(Month.JANUARY, year + 1, main) : new CalendarMonth(Month.of(month.getValue() + 1), year, main);
    }

    public CalendarMonth getPreviousMonth() {
        return month.getValue() == 1 ? new CalendarMonth(Month.DECEMBER, year - 1, main) : new CalendarMonth(Month.of(month.getValue() - 1), year, main);
    }

    @Override
    public String toString() {
        return this.month + " : " + this.year;
    }





}
