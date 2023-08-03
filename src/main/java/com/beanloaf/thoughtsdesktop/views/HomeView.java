package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.controllers.GlobalHeaderController;
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
    private final VBox notepadFXML, calendarFXML;

    private final Label homeUserLabel, homeDateLabel, homeShortDateLabel, homeTimeLabel, homeMilitaryTimeLabel;

    private final GlobalHeaderController headerController;


    public HomeView(final MainApplication main) {
        this.main = main;

        ThoughtsHelper.getInstance().addListener(this);

        headerController = (GlobalHeaderController) ThoughtsHelper.getInstance().getController(GlobalHeaderController.class);




        homeRoot = (AnchorPane) findNodeByID("homeRoot");

        notepadFXML = (VBox) findNodeByID("notepadFXML");
        calendarFXML = (VBox) findNodeByID("calendarFXML");


        homeUserLabel = (Label) findNodeByID("homeUserLabel");
        homeDateLabel = (Label) findNodeByID("homeDateLabel");
        homeShortDateLabel = (Label) findNodeByID("homeShortDateLabel");
        homeTimeLabel = (Label) findNodeByID("homeTimeLabel");
        homeMilitaryTimeLabel = (Label) findNodeByID("homeMilitaryTimeLabel");


        attachEvents();
        start();

    }

    private void attachEvents() {

        notepadFXML.setVisible(false);
        calendarFXML.setVisible(false);


    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case Properties.Data.LOG_IN_SUCCESS -> {
                final ThoughtUser user = (ThoughtUser) eventValue;

                if (user == null) {
                    throw new IllegalArgumentException("User cannot be null.");
                }

                homeUserLabel.setText("Welcome back, " + user.displayName() + ".");
                headerController.setUserDisplayName(user.displayName());
            }
            case Properties.Actions.SIGN_OUT -> {
                homeUserLabel.setText("");
                headerController.setUserDisplayName("");

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

                if (main.textView == null) main.textView = new TextView(main);
                if (main.listView == null) {
                    main.listView = new ListView(main);
                    main.startup();
                }

                homeRoot.setVisible(false);
                calendarFXML.setVisible(false);
                notepadFXML.setVisible(true);
            }
            case HOME -> {

                // We could turn off the clock here to save resources, but it probably doesn't save too much.

                homeRoot.setVisible(true);
                notepadFXML.setVisible(false);
                calendarFXML.setVisible(false);

            }

            case CALENDAR -> {
                if (main.calendarView == null) main.calendarView = new CalendarView(main);

                homeRoot.setVisible(false);
                notepadFXML.setVisible(false);

                calendarFXML.setVisible(true);
            }

            default -> throw new IllegalArgumentException("Not sure how you got here. Illegal enum passed: " + layout);

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

                headerController.setDateTime(getShortDate(), currentTime.format(standardTimeFormatter) );

            });
        }

        public void stop() {
            scheduler.shutdownNow();

        }



    }


}
