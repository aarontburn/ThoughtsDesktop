package com.beanloaf.thoughtsdesktop.global_views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeView implements ThoughtsChangeListener {




    private final MainApplication main;


    public Clock clock;


    private final Label homeUserLabel, homeDateLabel, homeShortDateLabel, homeTimeLabel, homeMilitaryTimeLabel;



    public HomeView(final MainApplication main) {
        this.main = main;



        ThoughtsHelper.getInstance().addListener(this);



        homeUserLabel = (Label) findNodeByID("homeUserLabel");
        homeDateLabel = (Label) findNodeByID("homeDateLabel");
        homeShortDateLabel = (Label) findNodeByID("homeShortDateLabel");
        homeTimeLabel = (Label) findNodeByID("homeTimeLabel");
        homeMilitaryTimeLabel = (Label) findNodeByID("homeMilitaryTimeLabel");

        start();

    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case Properties.Data.LOG_IN_SUCCESS -> {
                final ThoughtUser user = (ThoughtUser) eventValue;

                if (user == null) throw new IllegalArgumentException("User cannot be null.");


                homeUserLabel.setText("Welcome back, " + user.displayName() + ".");
            }
            case Properties.Actions.SIGN_OUT -> homeUserLabel.setText("");
        }
    }

    private void start() {
        if (main.firebaseHandler != null && main.firebaseHandler.user != null) {
            homeUserLabel.setText("Welcome back, " + main.firebaseHandler.user.displayName() + ".");
        } else {
            homeUserLabel.setText("");
        }


        homeDateLabel.setText(getFullDate());
        homeShortDateLabel.setText("(" + getShortDate() + ")");
        clock = new Clock();
        clock.start();


    }

    private String getFullDate() {
        final Date date = new Date();

        final int day = Calendar.getInstance().get(Calendar.DATE);


        return new SimpleDateFormat("EEEE, MMMM d'" + ThoughtsHelper.getNumberSuffix(day) + "', yyyy").format(date);
    }

    private String getShortDate() {
        return new SimpleDateFormat("M/d/yyyy").format(new Date());
    }


    private Node findNodeByID(final String nodeID) {
        return main.findNodeById(nodeID);
    }





    public class Clock {

        private final ScheduledExecutorService scheduler;

        private final DateTimeFormatter standardTimeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");

        private final DateTimeFormatter militaryTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        public Clock() {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        public void start() {
            updateAndDisplayTime();
            scheduler.scheduleAtFixedRate(this::updateAndDisplayTime, 1000 - LocalTime.now().get(ChronoField.MILLI_OF_SECOND), 1000, TimeUnit.MILLISECONDS);
        }

        private void updateAndDisplayTime() {
            Platform.runLater(() -> {
                final LocalTime currentTime = LocalTime.now();

                homeTimeLabel.setText(currentTime.format(standardTimeFormatter));
                homeMilitaryTimeLabel.setText(currentTime.format(militaryTimeFormatter));
                homeDateLabel.setText(getFullDate());
                homeShortDateLabel.setText("(" + getShortDate() + ")");

                main.headerController.setDateTime(getShortDate(), currentTime.format(standardTimeFormatter));

            });
        }

        public void stop() {
            scheduler.shutdownNow();

        }


    }


}
