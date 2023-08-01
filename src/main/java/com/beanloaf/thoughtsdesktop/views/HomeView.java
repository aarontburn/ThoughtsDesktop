package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import com.beanloaf.thoughtsdesktop.changeListener.Properties;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeView implements ThoughtsChangeListener {


    public enum Layouts {
        HOME, CALENDAR, NOTES

    }

    private final MainApplication main;

    public Clock clock;

    private final AnchorPane homeRoot;
    private final VBox notepadFXML;
    private final Button homeNotesButton;


    private final Label homeUserLabel, homeDateLabel, homeShortDateLabel, homeTimeLabel, homeMilitaryTimeLabel;


    public HomeView(final MainApplication main) {
        this.main = main;

        ThoughtsHelper.getInstance().addListener(this);


        homeRoot = (AnchorPane) findNodeByID("homeRoot");
        homeNotesButton = (Button) findNodeByID("homeNotesButton");
        notepadFXML = (VBox) findNodeByID("notepadFXML");
        notepadFXML.setVisible(false);


        homeNotesButton.setOnMouseClicked(e -> swapLayouts(Layouts.NOTES));


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
                homeUserLabel.setText("Welcome back, " + ((ThoughtUser) eventValue).displayName() + ".");
            }
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

        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int day = cal.get(Calendar.DATE);

        if (!((day > 10) && (day < 19))) {
            return switch (day % 10) {
                case 1 -> new SimpleDateFormat("EEEE, MMMM d'st', yyyy").format(date);
                case 2 -> new SimpleDateFormat("EEEE, MMMM d'nd', yyyy").format(date);
                case 3 -> new SimpleDateFormat("EEEE, MMMM d'rd', yyyy").format(date);
                default -> new SimpleDateFormat("EEEE, MMMM d'th', yyyy").format(date);
            };
        }

        return new SimpleDateFormat("EEEE, MMMM d'th', yyyy").format(date);
    }

    private String getShortDate() {
        return new SimpleDateFormat("M/d/yyyy").format(new Date());
    }


    private Node findNodeByID(final String nodeID) {
        return main.findNodeByID(nodeID);
    }

    public void swapLayouts(final Layouts layout) {
        switch (layout) {
            case NOTES -> {
                if (main.listView == null) {
                    main.listView = new ListView(main);
                    main.startup();
                }
                if (main.textView == null) main.textView = new TextView(main);


                homeRoot.setVisible(false);
                notepadFXML.setVisible(true);
            }
            case HOME -> {
                homeRoot.setVisible(true);
                notepadFXML.setVisible(false);
            }

            case CALENDAR -> {

            }

            default -> throw new IllegalArgumentException("Not sure how you got here. Illegal enum passed" + layout);

        }


    }

    public class Clock {

        private final ScheduledExecutorService scheduler;

        private final DateTimeFormatter standardTimeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a") ;

        private final DateTimeFormatter militaryTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss") ;

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

            });
        }

        public void stop() {
            scheduler.shutdownNow();

        }



    }


}
