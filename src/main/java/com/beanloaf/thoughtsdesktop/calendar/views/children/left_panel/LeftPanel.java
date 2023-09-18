package com.beanloaf.thoughtsdesktop.calendar.views.children.left_panel;

import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LeftPanel {

    private final CalendarMain main;

    public enum LeftLayouts {
        EVENTS, SCHEDULES
    }

    private LeftLayouts currentLayout = LeftLayouts.EVENTS;
    private final Map<LeftLayouts, Node> layoutMap = new HashMap<>();



    /*  Components  */


    /*  Event Box   */
    private VBox calendarEventBox;
    private Label calendarDayLabel;




    public LeftPanel(final CalendarMain main) {
        this.main = main;

        layoutMap.put(LeftLayouts.EVENTS, findNodeById("calendarLeftEventPanel"));
        layoutMap.put(LeftLayouts.SCHEDULES, findNodeById("calendarLeftSchedulePanel"));

        locateNodes();
        attachEvents();
    }

    private void locateNodes() {
        /*  Event Box   */
        calendarEventBox = (VBox) findNodeById("calendarEventBox");
        calendarDayLabel = (Label) findNodeById("calendarDayLabel");

    }

    private void attachEvents() {


    }


    public CalendarMain getMain() {
        return this.main;
    }

    public Node findNodeById(final String nodeId) {
        return main.findNodeById(nodeId);
    }


    public void swapLeftPanel(final LeftLayouts swapToLayout) {
        if (swapToLayout == null) throw new IllegalArgumentException("swapToLayout cannot be null");

        currentLayout = swapToLayout;
        for (final LeftLayouts layout : layoutMap.keySet()) {
            layoutMap.get(layout).setVisible(false);
        }

        layoutMap.get(swapToLayout).setVisible(true);
    }

    public void clearEventBox() {
        calendarEventBox.getChildren().clear();
    }

    public void setDateLabel(final LocalDate date) {
        calendarDayLabel.setText(ThoughtsHelper.toCamelCase(date.getMonth().toString()) + " " + date.getDayOfMonth() + ThoughtsHelper.getNumberSuffix(date.getDayOfMonth()) + ", " + date.getYear());
    }

    public void addEventToEventBox(final Node[] event) {
        calendarEventBox.getChildren().addAll(event);
        sortEventBox();
    }

    public void sortEventBox() {
        FXCollections.sort(calendarEventBox.getChildren(), DayEvent.getDayEventComparator());
    }








}
