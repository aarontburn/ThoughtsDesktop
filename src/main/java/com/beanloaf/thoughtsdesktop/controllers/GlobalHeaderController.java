package com.beanloaf.thoughtsdesktop.controllers;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.views.HomeView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GlobalHeaderController {

    private MainApplication main;

    @FXML
    private Label headerCalendarButton;

    @FXML
    private Label headerHomeButton;

    @FXML
    private Label headerNotesButton;

    @FXML
    private Label headerSettingsButton;

    @FXML
    private Label headerUserName;

    @FXML
    private Label headerDateTime;




    @FXML
    public void initialize() {

        this.main = ThoughtsHelper.getInstance().getMain();
        ThoughtsHelper.getInstance().addController(this);


        headerHomeButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.HOME));

        headerCalendarButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.CALENDAR));
        headerNotesButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.NOTES));
        headerSettingsButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().targetEvent(MainApplication.class, Properties.Actions.OPEN_HOME_SETTINGS));

    }

    public void setDateTime(final String date, final String time) {
        this.headerDateTime.setText(date + ", " + time);

    }

    public void setUserDisplayName(final String userDisplayName) {
        this.headerUserName.setText(userDisplayName);
    }





}
