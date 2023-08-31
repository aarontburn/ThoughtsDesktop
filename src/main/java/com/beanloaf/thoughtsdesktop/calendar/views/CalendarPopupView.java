package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.Map;

public class CalendarPopupView {

    private final CalendarView view;


    private SchedulePopup schedulePopup;

    public enum Popups {
        NEW_EVENT, SCHEDULE;

    }


    /* Popup */
    private AnchorPane popupWindow;
    private AnchorPane newEventPopupPane, schedulePopupPane;
    private final Map<Popups, AnchorPane> popupMap = new HashMap<>();
    private Label calendarClosePopup;


    public CalendarPopupView(final CalendarView view) {
        this.view = view;

        locateNodes();
        attachEvents();

        hidePopup();
    }

    private void locateNodes() {
        /*  Popup   */
        popupWindow = (AnchorPane) findNodeById("popupWindow");
        newEventPopupPane = (AnchorPane) findNodeById("newEventPopup");
        schedulePopupPane = (AnchorPane) findNodeById("schedulePopup");
        calendarClosePopup = (Label) findNodeById("calendarClosePopup");


        popupMap.put(Popups.NEW_EVENT, newEventPopupPane);
        popupMap.put(Popups.SCHEDULE, schedulePopupPane);
    }

    private void attachEvents() {
        calendarClosePopup.setOnMouseClicked(e -> hidePopup());


    }

    public void resizePopupHeight(final double newHeight) {
        if (popupWindow == null) return;

        final double anchor = newHeight * 1 / 7;

        AnchorPane.setTopAnchor(popupWindow, anchor);
        AnchorPane.setBottomAnchor(popupWindow, anchor);
    }

    public void resizePopupWidth(final double newWidth) {
        try {
            if (popupWindow == null) return;


            final double anchor = newWidth * 1 / 6;

            AnchorPane.setLeftAnchor(popupWindow, anchor);
            AnchorPane.setRightAnchor(popupWindow, anchor);
        } catch (Exception e) {
            Logger.log(e);
        }

    }


    private Node findNodeById(final String nodeId) {
        return view.findNodeById(nodeId);
    }

    public void swapAndDisplayPopup(final Popups visiblePopup) {
        swapPopup(visiblePopup);
        displayPopup();
    }

    public void swapPopup(final Popups visiblePopup) {
        for (final Popups popups : popupMap.keySet()) {
            popupMap.get(popups).setVisible(false);
        }
        if (visiblePopup != null) popupMap.get(visiblePopup).setVisible(true);
        
    }

    public void hidePopup() {
        popupWindow.setVisible(false);
    }

    public void displayPopup() {
        popupWindow.setVisible(true);
    }



    public void displaySchedule(final ScheduleData scheduleData) {
        schedulePopup = new SchedulePopup(view, scheduleData);


        swapAndDisplayPopup(Popups.SCHEDULE);
    }

}
