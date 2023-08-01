package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.handlers.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class test {


    public static void main(String[] args) {




        while (true) {

            System.out.println(LocalTime.now().get(ChronoField.MILLI_OF_SECOND));

        }

    }

    public static class Clock {



        private ScheduledExecutorService scheduler;

        public Clock() {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        public void start() {
            // Schedule the task to update and display time every second

            System.out.println(getInitialDelay());
            scheduler.scheduleAtFixedRate(this::updateAndDisplayTime, getInitialDelay(), 1, TimeUnit.SECONDS);
        }

        private void updateAndDisplayTime() {
            LocalTime currentTime = LocalTime.now();
            Logger.log("Current time: " + currentTime);
        }

        public void stop() {
            // Shutdown the scheduler when you want to stop the clock
            scheduler.shutdown();
        }

        private long getInitialDelay() {
            LocalTime currentTime = LocalTime.now();
            int secondsUntilNextMinute = 60 - currentTime.getSecond();
            return secondsUntilNextMinute * 1000; // Convert to milliseconds
        }


    }


}
