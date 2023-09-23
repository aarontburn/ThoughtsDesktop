package com.beanloaf.thoughtsdesktop.calendar.views.children.overlays;


import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleCalendarDay;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleListItem;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScheduleOverlay {

    private final CalendarMain main;

    private final ScheduleData data;
    private ScheduleListItem selectedScheduleListItem;


    private final List<Runnable> queuedTasks = new ArrayList<>();

    private final Map<Weekday, ScheduleCalendarDay> weekdayMap = new HashMap<>();
    private final List<ScheduleListItem> eventList = new ArrayList<>();


    /*  Header  */
    private TextField scheduleNameInput;
    private DatePicker scheduleStartDate, scheduleEndDate;
    private Label closeButton;



    /*  ------  */
    /*  Week View   */
    private GridPane scheduleWeekGrid;


    /*  ------  */
    private TimeGroupView scheduleTimeFrom, scheduleTimeTo;
    private TextField scheduleEventTitleInput;
    private TextArea scheduleEventDescriptionInput;
    private Button scheduleSaveEventButton, scheduleSaveScheduleButton;


    /*  ---------   */
    /*  Day of the week */
    private Button scheduleNewEventButton, scheduleDeleteEventButton;
    private AnchorPane scheduleEventBox;
    private VBox scheduleEventList;
    private boolean scheduleEventListReady;


    /*  ----------  */


    // Loading data into the tab
    public ScheduleOverlay(final CalendarMain main, final ScheduleData data) {
        this.main = main;

        this.data = data;


        locateNodes();
        attachEvents();
        createGUI();

        startup();

        scheduleEventList.getChildren().clear();
    }

    private Node findNodeById(final String nodeId) {
        return main.findNodeById(nodeId);
    }


    @SuppressWarnings("unchecked")
    public void locateNodes() {
        /*  Header  */
        scheduleNameInput = (TextField) findNodeById("scheduleNameInput");
        scheduleStartDate = (DatePicker) findNodeById("scheduleStartDate");
        scheduleEndDate = (DatePicker) findNodeById("scheduleEndDate");
        closeButton = (Label) findNodeById("closeScheduleOverlayButton");



        /*  Week View   */
        scheduleWeekGrid = (GridPane) findNodeById("scheduleWeekGrid");


        /*  Input Fields    */
        scheduleEventTitleInput = (TextField) findNodeById("scheduleEventTitleInput");
        scheduleEventDescriptionInput = (TextArea) findNodeById("scheduleEventDescriptionInput");


        final TextField scheduleHourInputFrom = (TextField) findNodeById("scheduleHourInputFrom");
        final TextField scheduleMinuteInputFrom = (TextField) findNodeById("scheduleMinuteInputFrom");
        final ComboBox<String> scheduleAMPMSelectorFrom = (ComboBox<String>) findNodeById("scheduleAMPMSelectorFrom");
        scheduleTimeFrom = new TimeGroupView(scheduleHourInputFrom, scheduleMinuteInputFrom, scheduleAMPMSelectorFrom);


        final TextField scheduleHourInputTo = CH.setNumbersOnlyTextField((TextField) findNodeById("scheduleHourInputTo"));
        final TextField scheduleMinuteInputTo = CH.setNumbersOnlyTextField((TextField) findNodeById("scheduleMinuteInputTo"));
        final ComboBox<String> scheduleAMPMSelectorTo = CH.setAMPMComboBox((ComboBox<String>) findNodeById("scheduleAMPMSelectorTo"));
        scheduleTimeTo = new TimeGroupView(scheduleHourInputTo, scheduleMinuteInputTo, scheduleAMPMSelectorTo);


        scheduleSaveEventButton = (Button) findNodeById("scheduleSaveEventButton");
        scheduleDeleteEventButton = (Button) findNodeById("scheduleDeleteEventButton");

        /*  Day of the week */
        scheduleEventBox = (AnchorPane) findNodeById("scheduleEventBox");
        scheduleNewEventButton = (Button) findNodeById("scheduleNewEventButton");
        scheduleEventList = (VBox) findNodeById("scheduleEventList");
        scheduleSaveScheduleButton = (Button) findNodeById("scheduleSaveScheduleButton");
    }

    public void attachEvents() {
        closeButton.setOnMouseClicked(e -> main.swapOverlay(CalendarMain.Overlays.CALENDAR));


        scheduleNewEventButton.setOnAction(e -> {
            final ScheduleListItem scheduleListItem = new ScheduleListItem(this, "New Scheduled Event");

            addScheduleEventToListView(scheduleListItem);
            setInputFields(scheduleListItem);
        });


        scheduleSaveEventButton.setOnAction(e -> saveScheduleEvent());

        scheduleDeleteEventButton.setOnAction(e -> {
            if (selectedScheduleListItem == null) return;

            for (final Weekday weekday : weekdayMap.keySet()) {
                final ScheduleCalendarDay day = weekdayMap.get(weekday);
                day.removeScheduleEventFromDay(selectedScheduleListItem);
            }

            eventList.remove(selectedScheduleListItem);
            scheduleEventList.getChildren().remove(selectedScheduleListItem);
        });


        scheduleSaveScheduleButton.setOnAction(e -> saveScheduleData());
    }

    protected void createGUI() {
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

            final ScheduleCalendarDay day = new ScheduleCalendarDay();
            weekdayMap.put(weekday, day);
            scheduleWeekGrid.add(day, i, 1);
        }


    }

    private void startup() {

        scheduleNameInput.setText(data.getScheduleName());
        scheduleStartDate.setValue(data.getStartDate());
        scheduleEndDate.setValue(data.getEndDate());


        scheduleEventTitleInput.setText("");
        scheduleEventDescriptionInput.setText("");

        scheduleTimeFrom.setTime(null);
        scheduleTimeTo.setTime(null);

        scheduleEventTitleInput.setDisable(true);
        scheduleEventDescriptionInput.setDisable(true);
        scheduleTimeFrom.setDisabled(true);
        scheduleTimeTo.setDisabled(true);


        for (final ScheduleEvent event : data.getScheduleEventList()) {
            final ScheduleListItem listItem = new ScheduleListItem(this, event);

            for (final Weekday weekday : event.getWeekdays()) {
                listItem.setChecked(weekday, true);
            }

            addScheduleEventToListView(listItem);
        }
    }

    public void setInputFields(final ScheduleListItem scheduleListItem) {
        if (scheduleListItem == null) throw new IllegalArgumentException("ScheduleListItem cannot be null.");


        scheduleEventTitleInput.setDisable(false);
        scheduleEventDescriptionInput.setDisable(false);
        scheduleTimeFrom.setDisabled(false);
        scheduleTimeTo.setDisabled(false);


        this.selectedScheduleListItem = scheduleListItem;


        scheduleEventTitleInput.setText(scheduleListItem.getScheduleEventName());


        scheduleTimeFrom.setTime(scheduleListItem.getStartTime());
        scheduleTimeTo.setTime(scheduleListItem.getEndTime());

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
        weekdayMap.get(weekday).addScheduleEventToDay(scheduleListItem);
    }

    public void removeScheduleFromDay(final Weekday weekday, final ScheduleListItem scheduleListItem) {
        weekdayMap.get(weekday).removeScheduleEventFromDay(scheduleListItem);
    }

    private void saveScheduleEvent() {
        if (selectedScheduleListItem == null) return;

        selectedScheduleListItem.setScheduleEventName(scheduleEventTitleInput.getText());
        selectedScheduleListItem.setDescription(scheduleEventDescriptionInput.getText());
        selectedScheduleListItem.setStartTime(scheduleTimeFrom.getTime());
        selectedScheduleListItem.setEndTime(scheduleTimeTo.getTime());
    }

    private void saveScheduleData() {
        final LocalDate oldStartDate = data.getStartDate();
        final LocalDate oldEndDate = data.getEndDate();


        data.setScheduleName(scheduleNameInput.getText());
        data.setStartDate(scheduleStartDate.getValue());
        data.setEndDate(scheduleEndDate.getValue());

        data.setEvents(eventList);

        for (final ScheduleListItem scheduleListItem : eventList) {
            data.addEvent(scheduleListItem.getEvent());
            scheduleListItem.getEvent().removeAllWeekdays();

        }

        for (final Weekday weekday : weekdayMap.keySet()) {
            final ScheduleCalendarDay day = weekdayMap.get(weekday);

            for (final ScheduleEvent event : day.getScheduleEventList()) {

                if (data.getScheduleEventList().contains(event)) {
                    data.getEvent(event.getId()).addWeekday(weekday);
                }

            }
        }


        main.getJsonHandler().writeScheduleData(data);
        main.swapOverlay(CalendarMain.Overlays.CALENDAR);
        new Thread(() -> main.getRightPanel().getMonthView().updateSchedule(data, oldStartDate, oldEndDate)).start();

    }


}
