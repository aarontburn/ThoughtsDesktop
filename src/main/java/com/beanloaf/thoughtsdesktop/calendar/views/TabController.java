package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.calendar.objects.Tab;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.HashMap;
import java.util.Map;

public class TabController {

    private final CalendarView view;
    private final Map<Tabs, Node> tabMap = new HashMap<>();
    private ScheduleTab scheduleTab;
    private WeekTab weekTab;
    private Label closeButton;
    public TabController(final CalendarView view) {
        this.view = view;

        locateNodes();
        attachEvents();

        swapTabs(Tabs.WEEK);

        displayWeekView();
    }

    private void locateNodes() {
        closeButton = (Label) findNodeById("closeButton");

        tabMap.put(Tabs.SCHEDULE, findNodeById("newScheduleTab"));
        tabMap.put(Tabs.CALENDAR, findNodeById("calendarTab"));
        tabMap.put(Tabs.WEEK, findNodeById("weekTab"));

    }

    private void attachEvents() {
        closeButton.setOnMouseClicked(e -> swapTabs(Tabs.CALENDAR));
    }

    private Node findNodeById(final String nodeId) {
        return view.findNodeById(nodeId);
    }

    public void swapTabs(final Tabs visiblePopup) {
        for (final Tabs tabs : tabMap.keySet()) {
            tabMap.get(tabs).setVisible(false);
        }
        if (visiblePopup != null) tabMap.get(visiblePopup).setVisible(true);

    }

    public void displaySchedule(final ScheduleData scheduleData) {
        scheduleTab = new ScheduleTab(view, this, scheduleData);

        swapTabs(Tabs.SCHEDULE);



    }

    public void displayWeekView() {
        weekTab = new WeekTab(view, this);
        swapTabs(Tabs.WEEK);



    }


    public enum Tabs {
        CALENDAR, SCHEDULE, WEEK;
    }

}
