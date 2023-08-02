package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GlobalHeaderView extends ThoughtsView {


    private final Label headerHomeButton, headerCalendarButton, headerNotesButton, headerSettingsButton;

    private AnchorPane test;

    public GlobalHeaderView(final MainApplication main) {
        super(main);

        // TODO: None of the mouse events are getting triggerd

        test = (AnchorPane) findNodeByID("test");
        test.setOnMouseClicked(e -> Logger.log("test"));


        headerHomeButton = (Label) findNodeByID("headerHomeButton");
        headerHomeButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.HOME));

        headerCalendarButton = (Label) findNodeByID("headerCalendarButton");
        headerCalendarButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.CALENDAR));


        headerNotesButton = (Label) findNodeByID("headerNotesButton");
        headerNotesButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.NOTES));

        headerSettingsButton = (Label) findNodeByID("headerSettingsButton");
        headerHomeButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().targetEvent(MainApplication.class, Properties.Actions.OPEN_HOME_SETTINGS));

    }





}
