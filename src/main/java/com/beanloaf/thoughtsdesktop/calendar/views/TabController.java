package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;

public class TabController {

    private final CalendarView view;
    private final Map<Tabs, Node> tabMap = new HashMap<>();
    private ScheduleTab scheduleTab;

    public TabController(final CalendarView view) {
        this.view = view;

        locateNodes();
        swapTabs(Tabs.CALENDAR);
    }

    private void locateNodes() {

        tabMap.put(Tabs.SCHEDULE, findNodeById("newScheduleTab"));
        tabMap.put(Tabs.CALENDAR, findNodeById("calendarTab"));

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

    public enum Tabs {
        CALENDAR, SCHEDULE
    }

}
