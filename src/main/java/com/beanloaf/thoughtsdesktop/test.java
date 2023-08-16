package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.handlers.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class test {


    public static void main(String[] args) {
        Logger.log(LocalTime.now().format(DateTimeFormatter.ofPattern("H")));



    }






}
