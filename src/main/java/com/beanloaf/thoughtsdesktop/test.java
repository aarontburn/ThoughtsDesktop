package com.beanloaf.thoughtsdesktop;


import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.handlers.Logger;

import java.time.LocalDate;
import java.time.Month;

public class test {


    public static void main(String[] args) {

        LocalDate d = LocalDate.of(2023, Month.SEPTEMBER, 27);
        final int eventStartingWeekday = d.getDayOfWeek().getValue();


        final Weekday weekday = Weekday.SUNDAY;

        int startDateOffset = weekday.getDayOfWeek() - (eventStartingWeekday == 7 ? 0 : eventStartingWeekday);
        if (startDateOffset < 0) {
            startDateOffset += 7;
        }


        System.out.println(startDateOffset);






    }


}
