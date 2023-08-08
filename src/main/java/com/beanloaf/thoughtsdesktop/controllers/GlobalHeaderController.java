package com.beanloaf.thoughtsdesktop.controllers;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GlobalHeaderController implements ThoughtsChangeListener {

    private static final String SELECTED_LABEL = "selected-label";

    private final MainApplication main;

    @FXML
    public Label headerCalendarButton;

    @FXML
    public Label headerHomeButton;

    @FXML
    public Label headerNotesButton;

    @FXML
    public Label headerSettingsButton;

    @FXML
    private Label headerUserName;

    @FXML
    private Label headerDateTime;

    private Label[] labelLists;


    public GlobalHeaderController() {
        this.main = ThoughtsHelper.getInstance().getMain();
        ThoughtsHelper.getInstance().addController(this);
        ThoughtsHelper.getInstance().addListener(this);
    }


    @FXML
    public void initialize() {
        labelLists = new Label[]{headerHomeButton, headerCalendarButton, headerNotesButton, headerSettingsButton};

        headerHomeButton.setOnMouseClicked(e -> main.swapLayouts(MainApplication.Layouts.HOME));
        headerCalendarButton.setOnMouseClicked(e -> main.swapLayouts(MainApplication.Layouts.CALENDAR));
        headerNotesButton.setOnMouseClicked(e -> main.swapLayouts(MainApplication.Layouts.NOTES));
        headerSettingsButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().targetEvent(MainApplication.class, Properties.Actions.OPEN_HOME_SETTINGS));

    }

    public void setSelectedTab(final Label selectedTab) {
        for (final Label label : labelLists) {
            label.getStyleClass().remove(SELECTED_LABEL);
        }
        selectedTab.getStyleClass().add(SELECTED_LABEL);
    }



    public void setDateTime(final String date, final String time) {
        this.headerDateTime.setText(date + ", " + time);

    }

    public void setUserDisplayName(final String userDisplayName) {
        this.headerUserName.setText(userDisplayName);
    }


    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case Properties.Data.LOG_IN_SUCCESS -> {
                final ThoughtUser user = (ThoughtUser) eventValue;

                if (user == null) throw new IllegalArgumentException("User cannot be null.");


                setUserDisplayName(user.displayName());
            }
            case Properties.Actions.SIGN_OUT -> setUserDisplayName("");
        }
    }
}
