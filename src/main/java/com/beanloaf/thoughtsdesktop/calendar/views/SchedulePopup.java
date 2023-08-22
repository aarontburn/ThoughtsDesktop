package com.beanloaf.thoughtsdesktop.calendar.views;


import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleCalendarDay;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleListItem;
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
    private ScheduleListItem selectedScheduleListItem;


    private final List<Runnable> queuedTasks = new ArrayList<>();

    private final Map<Weekday, ScheduleCalendarDay> dayMap = new HashMap<>();
    private final List<ScheduleListItem> eventList = new ArrayList<>();


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
    private Button scheduleNewEventButton, scheduleDeleteEventButton;
    private AnchorPane scheduleEventBox;
    private VBox scheduleEventList;
    private boolean scheduleEventListReady;


    /*  ----------  */


    // Loading data into the popup
    public SchedulePopup(final CalendarView view, final ScheduleData data) {
        this.view = view;
        this.data = data;


        findNodes();
        attachEvents();
        createGUI();

        startup();

        scheduleEventList.getChildren().clear();


    }

    private Node findNodeByID(final String nodeID) {
        return view.findNodeById(nodeID);
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
        scheduleEventDescriptionInput = (TextArea) findNodeByID("scheduleEventDescriptionInput");

        scheduleHourInputFrom = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleHourInputFrom"));
        scheduleMinuteInputFrom = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleMinuteInputFrom"));
        scheduleHourInputTo = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleHourInputTo"));
        scheduleMinuteInputTo = CH.setNumbersOnlyTextField((TextField) findNodeByID("scheduleMinuteInputTo"));

        scheduleAMPMSelectorFrom = CH.setAMPMComboBox((ComboBox<String>) findNodeByID("scheduleAMPMSelectorFrom"));
        scheduleAMPMSelectorTo = CH.setAMPMComboBox((ComboBox<String>) findNodeByID("scheduleAMPMSelectorTo"));

        scheduleSaveEventButton = (Button) findNodeByID("scheduleSaveEventButton");
        scheduleDeleteEventButton = (Button) findNodeByID("scheduleDeleteEventButton");

        /*  Day of the week */
        scheduleEventBox = (AnchorPane) findNodeByID("scheduleEventBox");
        scheduleNewEventButton = (Button) findNodeByID("scheduleNewEventButton");
        scheduleEventList = (VBox) findNodeByID("scheduleEventList");
        scheduleSaveScheduleButton = (Button) findNodeByID("scheduleSaveScheduleButton");
    }

    private void attachEvents() {
        scheduleNewEventButton.setOnAction(e -> addScheduleEventToListView(new ScheduleListItem(this, "New Scheduled Event")));


        scheduleSaveEventButton.setOnAction(e -> {
            if (selectedScheduleListItem == null) return;

            selectedScheduleListItem.setScheduleName(scheduleEventTitleInput.getText());
            selectedScheduleListItem.setDescription(scheduleEventDescriptionInput.getText());

            selectedScheduleListItem.setStartTime(scheduleHourInputFrom.getText(), scheduleMinuteInputFrom.getText(), scheduleAMPMSelectorFrom.getSelectionModel().getSelectedItem());

        });

        scheduleDeleteEventButton.setOnAction(e -> {
            if (selectedScheduleListItem == null) return;

            for (final Weekday weekday : dayMap.keySet()) {
                final ScheduleCalendarDay day = dayMap.get(weekday);
                day.removeScheduleEventFromDay(selectedScheduleListItem);
            }

            eventList.remove(selectedScheduleListItem);
            scheduleEventList.getChildren().remove(selectedScheduleListItem);
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

            scheduleEventListReady = true;

            synchronized (queuedTasks) {
                for (final Runnable r : queuedTasks) {
                    r.run();
                }
            }


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

    private void startup() {
        scheduleNameInput.setText(data.getScheduleName());
        scheduleStartDate.setValue(data.getStartDate());
        scheduleEndDate.setValue(data.getEndDate());

        for (final ScheduleEvent event : data.getScheduleEventList()) {
            final ScheduleListItem listItem = new ScheduleListItem(this, event);

            for (final Weekday weekday : event.getWeekdays()) {
                listItem.setChecked(weekday, true);
            }

            addScheduleEventToListView(listItem);
        }
    }

    public void setInputFields(final ScheduleListItem scheduleListItem) {
        this.selectedScheduleListItem = scheduleListItem;

        scheduleEventTitleInput.setText(scheduleListItem.getScheduleName());

        if (scheduleListItem.getStartTime() == null) {
            scheduleHourInputFrom.setText("");
            scheduleMinuteInputFrom.setText("");
        } else {
            scheduleHourInputFrom.setText(scheduleListItem.getStartTime().format(DateTimeFormatter.ofPattern("hh")));
            scheduleMinuteInputFrom.setText(scheduleListItem.getStartTime().format(DateTimeFormatter.ofPattern("mm")));
            scheduleAMPMSelectorFrom.getSelectionModel().select(scheduleListItem.getStartTime().format(DateTimeFormatter.ofPattern("a")));
        }


        if (scheduleListItem.getEndTime() == null) {
            scheduleHourInputTo.setText("");
            scheduleMinuteInputTo.setText("");
        } else {
            scheduleHourInputTo.setText(scheduleListItem.getEndTime().format(DateTimeFormatter.ofPattern("hh")));
            scheduleMinuteInputTo.setText(scheduleListItem.getEndTime().format(DateTimeFormatter.ofPattern("mm")));
            scheduleAMPMSelectorTo.getSelectionModel().select(scheduleListItem.getStartTime().format(DateTimeFormatter.ofPattern("a")));

        }

        scheduleEventDescriptionInput.setText(scheduleListItem.getDescription());

    }

    public void addScheduleEventToListView(final ScheduleListItem scheduleListItem) {
        if (!scheduleEventListReady) {
            queuedTasks.add(() -> addScheduleEventToListView(scheduleListItem));
            return;
        }

        eventList.add(scheduleListItem);

        scheduleEventList.getChildren().add(scheduleListItem);
    }

    public void addScheduleEventToDay(final Weekday weekday, final ScheduleListItem scheduleListItem) {
        dayMap.get(weekday).addScheduleEventToDay(scheduleListItem);
    }

    public void removeScheduleFromDay(final Weekday weekday, final ScheduleListItem scheduleListItem) {
        dayMap.get(weekday).removeScheduleEventFromDay(scheduleListItem);
    }

    private void saveScheduleData() {
        data.setScheduleName(scheduleNameInput.getText());
        data.setStartDate(scheduleStartDate.getValue());
        data.setEndDate(scheduleEndDate.getValue());

        data.setEvents(eventList);




        for (final ScheduleListItem scheduleListItem : eventList) {
            data.addEvent(scheduleListItem.getEvent());
            scheduleListItem.getEvent().removeAllWeekdays();
        }



        for (final Weekday weekday : dayMap.keySet()) {
            final ScheduleCalendarDay day = dayMap.get(weekday);

            for (final ScheduleEvent event : day.getScheduleEventList()) {
                if (data.getScheduleEventList().contains(event)) {
                    data.getEvent(event.getId()).addWeekday(weekday);
                }
            }
        }


        view.calendarJson.writeScheduleData(data);

    }


}
