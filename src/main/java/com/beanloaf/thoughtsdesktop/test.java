package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.handlers.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

public class test {




    public static void main(String[] args) {
        final LocalDate date = LocalDate.of(2023, Month.SEPTEMBER, 4);

        LocalDate start = date.minusDays(date.getDayOfWeek().getValue());
        LocalDate end = start.plusDays(6);



//        Logger.log(LocalDate.of(2023, Month.SEPTEMBER, 4).getDayOfWeek().getValue());

        Logger.log(LocalTime.of(23, 34).isAfter(LocalTime.of(23, 30)));


    }






}
