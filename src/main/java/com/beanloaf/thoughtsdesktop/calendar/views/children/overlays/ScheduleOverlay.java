package com.beanloaf.thoughtsdesktop.calendar.views.children.overlays;


import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleListItem;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
    private final List<Runnable> queuedTasks = new ArrayList<>();
    private final Map<String, ScheduleListItem> eventMap = new HashMap<>(); //uid, scheduleListItem
    private ScheduleListItem selectedScheduleListItem;
    /*  Header  */
    private TextField scheduleNameInput;
    private DatePicker scheduleStartDate, scheduleEndDate;
    private Label closeButton;


    /*  ------  */
    /*  Week View   */
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
            if (selectedScheduleListItem == null) {
                return;
            }
            eventMap.remove(selectedScheduleListItem.getEvent().getId());
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


        final Map<Weekday, Map<String, BasicEvent>> scheduleMap = data.getScheduleEventList();
        for (final Weekday weekday : scheduleMap.keySet()) {

            final Map<String, BasicEvent> uidEventMap = scheduleMap.get(weekday);
            for (final String uid : uidEventMap.keySet()) {

                final BasicEvent event = uidEventMap.get(uid);
                ScheduleListItem listItem = eventMap.get(uid);
                if (listItem == null) {
                    listItem = new ScheduleListItem(this, event);
                    eventMap.put(uid, listItem);
                    addScheduleEventToListView(listItem);
                }

                listItem.setChecked(weekday, true);

            }
        }


    }

    public void setInputFields(final ScheduleListItem scheduleListItem) {
        if (scheduleListItem == null) {
            throw new IllegalArgumentException("ScheduleListItem cannot be null.");
        }


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

        eventMap.put(scheduleListItem.getEvent().getId(), scheduleListItem);
        scheduleEventList.getChildren().add(scheduleListItem);
    }





    private void saveScheduleEvent() {
        if (selectedScheduleListItem == null) {
            return;
        }

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
        data.setEvents(new ArrayList<>(eventMap.values()));

        main.getJsonHandler().writeScheduleData(data);
        main.swapOverlay(CalendarMain.Overlays.CALENDAR);
        new Thread(() -> main.getRightPanel().getMonthView().updateSchedule(data, oldStartDate, oldEndDate)).start();

    }


}
