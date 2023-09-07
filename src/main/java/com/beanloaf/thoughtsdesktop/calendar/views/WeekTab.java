package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.calendar.objects.Tab;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class WeekTab extends Tab {

    private GridPane weekGrid;

    public WeekTab(final CalendarView view, final TabController tabController) {
        super(view, tabController);
        locateNodes();
        attachEvents();
        createGUI();

    }


    @Override
    protected void locateNodes() {
        weekGrid = (GridPane) findNodeById("weekGrid");



    }

    @Override
    protected void attachEvents() {

    }

    @Override
    protected void createGUI() {
        weekGrid.
        for(int i = 0; i < 10; i++) {
            weekGrid.addRow(i, new Label("test"));
        }
    }
}
