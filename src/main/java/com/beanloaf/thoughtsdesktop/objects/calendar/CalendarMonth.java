package com.beanloaf.thoughtsdesktop.objects.calendar;


import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class CalendarMonth {

    private final Month month;

    private final Integer year;

    private final LocalDate monthInfo;


    private Map<Integer, CalendarDay> monthMap = new HashMap<>();

    private int daysWithEvents;



    public CalendarMonth(final Month month) {
        this(month, Calendar.getInstance().get(Calendar.YEAR));
    }

    public CalendarMonth(final Month month, final Integer year) {
        this.month = month;
        this.year = year;

        monthInfo = LocalDate.of(year, month, 1);

    }

    public void addDay(final int day, final CalendarDay calendarDay) {
        this.monthMap.put(day, calendarDay);
    }

    public CalendarDay getDay(final int day) {
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
        return month.getValue() == 12 ? new CalendarMonth(Month.JANUARY, year + 1) : new CalendarMonth(Month.of(month.getValue() + 1), year);
    }

    public CalendarMonth getPreviousMonth() {
        return month.getValue() == 1 ? new CalendarMonth(Month.DECEMBER, year - 1) : new CalendarMonth(Month.of(month.getValue() - 1), year);
    }

    @Override
    public String toString() {
        return this.month + " : " + this.year;
    }





}
