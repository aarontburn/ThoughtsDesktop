package com.beanloaf.thoughtsdesktop.calendar.views;


import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SchedulePopup {


    private final CalendarView view;

    private final ScheduleData data;

    private Schedule selectedSchedule;

    private Map<Weekday, ScheduleCalendarDay> dayMap = new HashMap<>();


    /*  Header  */
    private TextField scheduleNameInput;
    private DatePicker scheduleStartDate, scheduleEndDate;


    /*  ------  */
    /*  Week View   */
    private GridPane scheduleWeekGrid;


    /*  ------  */
    /*  Input Fields    */
    private TextField scheduleEventTitleInput, scheduleHourInputFrom, scheduleMinuteInputFrom, scheduleHourInputTo, scheduleMinuteInputTo;
    private TextArea scheduleEventDescriptionInput;
    private ComboBox<String> scheduleAMPMSelectorFrom, scheduleAMPMSelectorTo;
    private Button scheduleSaveEventButton, scheduleSaveScheduleButton;


    /*  ---------   */
    /*  Day of the week */
    private Button scheduleNewEventButton;
    private AnchorPane scheduleEventBox;
    private VBox scheduleEventList;

    /*  ----------  */


    public SchedulePopup(final CalendarView view) {
        this(view, new ScheduleData(view));
    }


    // Loading data into the popup
    public SchedulePopup(final CalendarView view, final ScheduleData data) {
        this.view = view;
        this.data = data;


        findNodes();
        attachEvents();
        createGUI();

        scheduleEventList.getChildren().clear();


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
        scheduleHourInputFrom = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleHourInputFrom"));
        scheduleMinuteInputFrom = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleMinuteInputFrom"));
        scheduleHourInputTo = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleHourInputTo"));
        scheduleMinuteInputTo = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleMinuteInputTo"));

        scheduleEventDescriptionInput = (TextArea) findNodeByID("scheduleEventDescriptionInput");
        scheduleAMPMSelectorFrom = CH.setAMPMComboBox((ComboBox<String>) findNodeByID("scheduleAMPMSelectorFrom"));
        scheduleAMPMSelectorTo = CH.setAMPMComboBox((ComboBox<String>) findNodeByID("scheduleAMPMSelectorTo"));
        scheduleSaveEventButton = (Button) findNodeByID("scheduleSaveEventButton");


        /*  Day of the week */
        scheduleEventBox = (AnchorPane) findNodeByID("scheduleEventBox");
        scheduleNewEventButton = (Button) findNodeByID("scheduleNewEventButton");
        scheduleEventList = (VBox) findNodeByID("scheduleEventList");
        scheduleSaveScheduleButton = (Button) findNodeByID("scheduleSaveScheduleButton");
    }

    private void attachEvents() {
        scheduleNewEventButton.setOnAction(e -> scheduleEventList.getChildren().add(new Schedule(this, "New Scheduled Event")));

        scheduleSaveEventButton.setOnAction(e -> {
            if (selectedSchedule == null) return;

            selectedSchedule.setScheduleName(scheduleEventTitleInput.getText());
            selectedSchedule.setDescription(scheduleEventDescriptionInput.getText());

            selectedSchedule.setTime(scheduleHourInputFrom.getText(), scheduleMinuteInputFrom.getText(), scheduleAMPMSelectorFrom.getSelectionModel().getSelectedItem());

        });

        CH.setAMPMComboBox(scheduleAMPMSelectorFrom);

        scheduleSaveScheduleButton.setOnAction(e -> saveScheduleData());
    }

    private void createGUI() {
        scheduleEventList = new VBox();
        scheduleEventList.setSpacing(5);

        final ScrollPane pane = (ScrollPane) ThoughtsHelper.setAnchor(new ScrollPane(), 32.0, 48.0, 0.0, 0.0);
        pane.skinProperty().addListener((observableValue, skin, t1) -> {
            final StackPane stackPane = (StackPane) pane.lookup("ScrollPane .viewport");
            stackPane.setCache(false);
        });
        pane.setFitToWidth(true);

        pane.setContent(scheduleEventList);

        scheduleEventBox.getChildren().add(pane);


        final List<Node> nodesToRemove = new ArrayList<>();

        for (final Node node : scheduleWeekGrid.getChildren()) {
            if (node.getClass() != Label.class) nodesToRemove.add(node);
        }

        scheduleWeekGrid.getChildren().removeAll(nodesToRemove);

        for (int i = 0; i < scheduleWeekGrid.getColumnCount(); i++) {
            final Weekday weekday = Weekday.values()[i];

            final ScheduleCalendarDay day = new ScheduleCalendarDay(weekday);
            dayMap.put(weekday, day);
            scheduleWeekGrid.add(day, i, 1);
        }


    }

    public void setInputFields(final Schedule schedule) {
        this.selectedSchedule = schedule;

        scheduleEventTitleInput.setText(schedule.getScheduleName());

        if (schedule.getTime() == null) {
            scheduleHourInputFrom.setText("");
            scheduleMinuteInputFrom.setText("");
        } else {
            scheduleHourInputFrom.setText(schedule.getTime().format(DateTimeFormatter.ofPattern("hh")));
            scheduleMinuteInputFrom.setText(schedule.getTime().format(DateTimeFormatter.ofPattern("mm")));
        }

        scheduleEventDescriptionInput.setText(schedule.getDescription());

    }

    public void addScheduleToWeekView(final Weekday weekday, final Schedule schedule) {
        final ScheduleCalendarDay day = dayMap.get(weekday);

        day.addSchedule(schedule);
    }

    public void removeScheduleFromWeekView(final Weekday weekday, final Schedule schedule) {
        final ScheduleCalendarDay day = dayMap.get(weekday);

        day.removeSchedule(schedule);
    }

    private void saveScheduleData() {
        data.setScheduleName(scheduleNameInput.getText());
        data.setStartDate(scheduleStartDate.getValue());
        data.setEndDate(scheduleEndDate.getValue());


        for (final ScheduleCalendarDay day : dayMap.values()) {
            for (final ScheduleEvent event : day.getScheduleList()) {
                data.addEvent(day.getWeekday(), event);
            }
        }

        data.save();

    }


}
