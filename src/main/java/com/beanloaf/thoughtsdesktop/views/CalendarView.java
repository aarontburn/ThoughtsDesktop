package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class CalendarView {

    private final MainApplication main;

    private Label calendarHomeButton;

    public CalendarView(final MainApplication main) {
        this.main = main;

        calendarHomeButton = (Label) findNodeById("calendarHomeButton");
        calendarHomeButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.HOME));

    }

    private Node findNodeById(final String nodeID) {
        return main.findNodeByID(nodeID);
    }

}
