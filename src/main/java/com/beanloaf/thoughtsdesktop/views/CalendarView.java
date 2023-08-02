package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class CalendarView extends ThoughtsView {

    private Label calendarHomeButton;

    public CalendarView(final MainApplication main) {
        super(main);

        calendarHomeButton = (Label) findNodeByID("calendarHomeButton");
        calendarHomeButton.setOnMouseClicked(e -> main.homeView.swapLayouts(HomeView.Layouts.HOME));

    }


}
