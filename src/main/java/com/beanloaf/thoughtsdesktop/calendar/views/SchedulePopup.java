package com.beanloaf.thoughtsdesktop.calendar.views;


import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarDay;
import com.beanloaf.thoughtsdesktop.calendar.objects.ScheduleCalendarDay;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;


public class SchedulePopup {

    private final CalendarView view;


    /*  Header  */
    private TextField scheduleNameInput;
    private DatePicker scheduleStartDate, scheduleEndDate;


    /*  ------  */
    /*  Week View   */
    private GridPane scheduleWeekGrid;


    /*  ------  */
    /*  Input Fields    */
    private TextField scheduleEventTitleInput, scheduleHourInput, scheduleMinuteInput;
    private TextArea scheduleEventDescriptionInput;
    private ComboBox<String> scheduleAMPMSelector;
    private Button scheduleSaveButton;


    /*  ---------   */
    /*  Day of the week */
    private Button scheduleNewEventButton;
    private VBox scheduleEventList;
    private ScrollPane scheduleEventScrollPane;

    /*  ----------  */


    public SchedulePopup(final CalendarView view) {
        this.view = view;

        findNodes();
        attachEvents();
        createGUI();

    }

    private Node findNodeByID(final String nodeID) {
        return view.findNodeByID(nodeID);
    }

    private void findNodes() {
        /*  Header  */
        scheduleNameInput = (TextField) findNodeByID("scheduleNameInput");
        scheduleStartDate = (DatePicker) findNodeByID("scheduleStartDate");
        scheduleEndDate = (DatePicker) findNodeByID("scheduleEndDate");


        /*  Week View   */
        scheduleWeekGrid = (GridPane) findNodeByID("scheduleWeekGrid");


        /*  Input Fields    */
        scheduleEventTitleInput = (TextField) findNodeByID("scheduleEventTitleInput");
        scheduleHourInput = (TextField) findNodeByID("scheduleHourInput");
        scheduleMinuteInput = (TextField) findNodeByID("scheduleMinuteInput");
        scheduleEventDescriptionInput = (TextArea) findNodeByID("scheduleEventDescriptionInput");
        scheduleAMPMSelector = (ComboBox<String>) findNodeByID("scheduleAMPMSelector");
        scheduleSaveButton = (Button) findNodeByID("scheduleSaveButton");


        /*  Day of the week */
        scheduleNewEventButton = (Button) findNodeByID("scheduleNewEventButton");
        scheduleEventList = (VBox) findNodeByID("scheduleEventList");
        scheduleEventScrollPane = (ScrollPane) findNodeByID("scheduleEventScrollPane");

    }

    private void attachEvents() {
        scheduleEventScrollPane.skinProperty().addListener((observableValue, skin, t1) -> {
            final StackPane stackPane = (StackPane) scheduleEventScrollPane.lookup("ScrollPane .viewport");
            stackPane.setCache(false);
        });

    }

    private void createGUI() {
        final List<Node> nodesToRemove = new ArrayList<>();

        for (final Node node : scheduleWeekGrid.getChildren()) {
            if (node.getClass() != Label.class) nodesToRemove.add(node);
        }

        scheduleWeekGrid.getChildren().removeAll(nodesToRemove);

        for (int i = 0; i < scheduleWeekGrid.getColumnCount(); i++) {
            scheduleWeekGrid.add(new ScheduleCalendarDay(i), i, 1);
        }


    }







}
