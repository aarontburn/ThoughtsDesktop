package com.beanloaf.thoughtsdesktop.objects.calendar;


import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;

public class CalendarMonth {

    private final Month month;

    private final LocalDate monthInfo;

    public CalendarMonth(final Month month) {
        this.month = month;


        monthInfo = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), month, 1);
    }

    public int getStartingDayOfWeek() {
       return monthInfo.getDayOfWeek().getValue();

    }

    public int getMonthLength() {
        return monthInfo.lengthOfMonth();

    }







}
