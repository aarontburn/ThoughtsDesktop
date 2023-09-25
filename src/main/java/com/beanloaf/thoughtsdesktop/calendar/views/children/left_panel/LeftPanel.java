package com.beanloaf.thoughtsdesktop.calendar.views.children.left_panel;

import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleBoxItem;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LeftPanel {

    private final CalendarMain main;


    private LeftLayouts currentLayout;
    public enum LeftLayouts {
        EVENTS, SCHEDULES
    }

    private final Map<LeftLayouts, AnchorPane> layoutMap = new HashMap<>();



    /*  Components */
    /*  Schedule Box    */
    private Button calendarNewScheduleButton;
    public VBox calendarScheduleBox;


    private Button calendarEventsButton, calendarScheduleButton;

    /*  Event Box   */
    private VBox calendarEventBox;
    private Label calendarDayLabel;
    private Button calendarNewEventButton;


    /*  Small Event Input */
    private Node calendarEventFields;
    private TextField calendarEventTitleInput;
    private DatePicker calendarDatePicker;
    private TimeGroupView calendarTimeStart, calendarTimeEnd;
    private TextArea calendarDescriptionInput;
    private Button calendarSaveEventButton, calendarEditButton, calendarDeleteButton;
    private HBox calendarStartTimeFields, calendarEndTimeFields;
    private Label calendarFinalStartTimeLabel, calendarFinalEndTimeLabel,
            calendarDescriptionLabel, calendarTimeLabel, calendarSourceLabel;
    private CheckBox calendarProgressCheckBox;
    private Label expandEventButton;




    public LeftPanel(final CalendarMain main) {
        this.main = main;

        layoutMap.put(LeftLayouts.EVENTS, (AnchorPane) findNodeById("calendarLeftEventPanel"));
        layoutMap.put(LeftLayouts.SCHEDULES, (AnchorPane) findNodeById("calendarLeftSchedulePanel"));

        locateNodes();
        attachEvents();

        swapLeftPanel(LeftLayouts.EVENTS);
        Platform.runLater(() -> calendarScheduleBox.getChildren().clear());

    }

    @SuppressWarnings("unchecked")
    private void locateNodes() {
        /*  Left Panel  */
        calendarEventsButton = (Button) findNodeById("calendarEventsButton");
        calendarScheduleButton = (Button) findNodeById("calendarScheduleButton");


        /*  Schedule Box    */
        calendarScheduleBox = (VBox) findNodeById("calendarScheduleBox");
        calendarNewScheduleButton = (Button) findNodeById("calendarNewScheduleButton");

        /*  Event Box   */
        calendarEventBox = (VBox) findNodeById("calendarEventBox");
        calendarDayLabel = (Label) findNodeById("calendarDayLabel");
        calendarNewEventButton = (Button) findNodeById("calendarNewEventButton");


        /*  Event Input   */
        calendarDescriptionLabel = (Label) findNodeById("calendarDescriptionLabel");
        calendarTimeLabel = (Label) findNodeById("calendarTimeLabel");
        calendarSourceLabel = (Label) findNodeById("calendarSourceLabel");

        expandEventButton = (Label) findNodeById("expandEventButton");
        calendarEventFields = findNodeById("calendarEventFields");
        calendarEventTitleInput = (TextField) findNodeById("calendarEventTitleInput");
        calendarDatePicker = (DatePicker) findNodeById("calendarDatePicker");

        calendarTimeStart = new TimeGroupView(
                (TextField) findNodeById("calendarHourInputFrom"),
                (TextField) findNodeById("calendarMinuteInputFrom"),
                (ComboBox<String>) findNodeById("calendarAMPMSelectorFrom"));

        calendarTimeEnd = new TimeGroupView(
                (TextField) findNodeById("calendarHourInputTo"),
                (TextField) findNodeById("calendarMinuteInputTo"),
                (ComboBox<String>) findNodeById("calendarAMPMSelectorTo"));


        calendarDescriptionInput = (TextArea) findNodeById("calendarDescriptionInput");
        calendarSaveEventButton = (Button) findNodeById("calendarSaveEventButton");
        calendarEditButton = (Button) findNodeById("calendarEditButton");
        calendarDeleteButton = (Button) findNodeById("calendarDeleteButton");

        calendarStartTimeFields = (HBox) findNodeById("calendarStartTimeFields");
        calendarEndTimeFields = (HBox) findNodeById("calendarEndTimeFields");
        calendarFinalStartTimeLabel = (Label) findNodeById("calendarFinalStartTimeLabel");
        calendarFinalEndTimeLabel = (Label) findNodeById("calendarFinalEndTimeLabel");
        calendarProgressCheckBox = (CheckBox) findNodeById("calendarProgressCheckBox");
    }


    private void resizeDescriptionBox(final boolean isWrapping, final boolean toTop) {
        if (isWrapping) {
            ThoughtsHelper.setAnchor(
                    calendarDescriptionInput, 313,
                    AnchorPane.getBottomAnchor(calendarDescriptionInput),
                    AnchorPane.getLeftAnchor(calendarDescriptionInput),
                    AnchorPane.getRightAnchor(calendarDescriptionInput));

            ThoughtsHelper.setAnchor(
                    calendarDescriptionLabel, 290,
                    AnchorPane.getBottomAnchor(calendarDescriptionLabel),
                    AnchorPane.getLeftAnchor(calendarDescriptionLabel),
                    AnchorPane.getRightAnchor(calendarDescriptionLabel));

        } else {
            ThoughtsHelper.setAnchor(
                    calendarDescriptionInput, toTop ? 212 : 276,
                    AnchorPane.getBottomAnchor(calendarDescriptionInput),
                    AnchorPane.getLeftAnchor(calendarDescriptionInput),
                    AnchorPane.getRightAnchor(calendarDescriptionInput));

            ThoughtsHelper.setAnchor(
                    calendarDescriptionLabel, toTop ? 188 : 253,
                    AnchorPane.getBottomAnchor(calendarDescriptionLabel),
                    AnchorPane.getLeftAnchor(calendarDescriptionLabel),
                    AnchorPane.getRightAnchor(calendarDescriptionLabel));
        }

    }

    private void setDescriptionBoxHeight() {
        if (!calendarEndTimeFields.isVisible()
                && calendarFinalEndTimeLabel.getText().isEmpty()
                && calendarFinalStartTimeLabel.getText().isEmpty()) {

            calendarTimeLabel.setVisible(false);
            resizeDescriptionBox(false, true);


        } else {
            calendarTimeLabel.setVisible(true);
            if (!calendarEndTimeFields.isVisible()) {  // 448 is when wrapping occurs when editing, 280 when not editing
                resizeDescriptionBox(layoutMap.get(LeftLayouts.EVENTS).getWidth() < 280, false); // wrapping for final label
            } else {
                resizeDescriptionBox(layoutMap.get(LeftLayouts.EVENTS).getWidth() < 448, false); // wrapping for input fields
            }


        }
    }

    private void attachEvents() {

        calendarEndTimeFields.visibleProperty().addListener((observableValue, aBoolean, isVisible) -> setDescriptionBoxHeight());
        layoutMap.get(LeftLayouts.EVENTS).widthProperty().addListener((observableValue, number, width) -> setDescriptionBoxHeight());


        calendarNewScheduleButton.setOnAction(e -> this.main.swapOverlay(CalendarMain.Overlays.SCHEDULE, new ScheduleData()));


        /*  Left Panel  */
        calendarEventsButton.setOnAction(e -> main.getLeftPanel().swapLeftPanel(LeftPanel.LeftLayouts.EVENTS));
        calendarScheduleButton.setOnAction(e -> main.getLeftPanel().swapLeftPanel(LeftPanel.LeftLayouts.SCHEDULES));



        calendarNewEventButton.setOnMouseClicked(e -> {
            final CalendarDay selectedDay = this.main.getCalendarHandler().getSelectedDay();
            if (selectedDay != null)
                main.getRightPanel().getMonthView().selectEvent(main.getRightPanel().getMonthView().addNewEventToCalendarDay(selectedDay.getDate()), true);
        });

        /*  Small New Event*/
        expandEventButton.setOnMouseClicked(e -> {
            main.swapOverlay(CalendarMain.Overlays.EVENT, main.getCalendarHandler().getSelectedEvent());

        });

        calendarEventFields.setVisible(false);

        calendarProgressCheckBox.selectedProperty().addListener((observableValue, aBoolean, isChecked) ->
                calendarProgressCheckBox.setText(isChecked ? "Completed" : "In-progress"));

        calendarProgressCheckBox.setOnAction(e -> main.getCalendarHandler().getSelectedEvent().setCompleted(calendarProgressCheckBox.isSelected(), true));


        calendarSaveEventButton.setVisible(false);
        calendarSaveEventButton.setOnAction(e -> {
            if (main.getCalendarHandler().getSelectedEvent() == null) {
                return;
            }
            main.getRightPanel().getMonthView().saveEvent(main.getCalendarHandler().getSelectedEvent(), main.getLeftPanel().getEventInputFields());
            main.getRightPanel().getMonthView().selectEvent(main.getCalendarHandler().getSelectedEvent(), false);
        });

        calendarDeleteButton.setOnAction(e -> {
            if (main.getCalendarHandler().getSelectedEvent() == null) {
                return;
            }
            main.getRightPanel().getMonthView().deleteEvent(main.getCalendarHandler().getSelectedEvent());
        });


        calendarEditButton.setOnAction(e -> {
            toggleSmallEventFields(true);
            calendarSaveEventButton.setVisible(true);
        });

    }

    public void toggleSmallEventFields(final boolean isEnabled) {
        final boolean isDisabled = !isEnabled;
        calendarEventTitleInput.setDisable(isDisabled);
        calendarDatePicker.setDisable(isDisabled);
        calendarSaveEventButton.setVisible(isEnabled);


        final ObservableList<String> styles = calendarDatePicker.getStyleClass();
        final String disableDatePickerStyle = "non-editable-date-picker";


        if (isDisabled) {
            if (!styles.contains(disableDatePickerStyle)) {
                styles.add(disableDatePickerStyle);
            }
        } else {
            while (styles.contains(disableDatePickerStyle)) {
                styles.remove(disableDatePickerStyle);
            }
        }

        calendarStartTimeFields.setVisible(isEnabled);
        calendarEndTimeFields.setVisible(isEnabled);

        calendarFinalStartTimeLabel.setVisible(isDisabled);
        calendarFinalEndTimeLabel.setVisible(isDisabled);

        calendarDescriptionInput.setEditable(isEnabled);

    }


    public CalendarMain getMain() {
        return this.main;
    }

    public Node findNodeById(final String nodeId) {
        return main.findNodeById(nodeId);
    }


    public void swapLeftPanel(final LeftLayouts swapToLayout) {
        if (swapToLayout == null) {
            throw new IllegalArgumentException("swapToLayout cannot be null");
        }

        for (final LeftLayouts layout : layoutMap.keySet()) {
            layoutMap.get(layout).setVisible(false);
        }
        currentLayout = swapToLayout;
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


    public BasicEvent getEventInputFields() {
        final BasicEvent event = new BasicEvent();

        event.setTitle(calendarEventTitleInput.getText());
        event.setStartDate(calendarDatePicker.getValue());
        event.setStartTime(calendarTimeStart.getTime());
        event.setEndTime(calendarTimeEnd.getTime());
        event.setDescription(calendarDescriptionInput.getText());

        return event;
    }

    public void setFinalStartEndTimeLabel(final DayEvent event) {
        final LocalTime startTime = event.getStartTime();
        final LocalTime endTime = event.getStartTime();
        calendarFinalStartTimeLabel.setText(startTime == null ? "" : "@ " + startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        calendarFinalEndTimeLabel.setText(endTime == null ? "" : "till " + endTime.format(DateTimeFormatter.ofPattern("h:mm a")));
    }

    public void onSelectEvent(final DayEvent event, final boolean editable) {
        calendarEventFields.setVisible(true);
        toggleSmallEventFields(editable);

        calendarSaveEventButton.setVisible(editable);
        calendarEditButton.setVisible(!editable);
        calendarDeleteButton.setDisable(event.getEventType() != TypedEvent.Types.DAY);
        calendarEditButton.setDisable(event.getEventType() != TypedEvent.Types.DAY);


        calendarEventTitleInput.setText(event.getEventTitle());
        calendarDescriptionInput.setText(event.getDescription());
        calendarDatePicker.setValue(LocalDate.of(event.getDate().getYear(), event.getDate().getMonth(), event.getDate().getDayOfMonth()));
        calendarProgressCheckBox.setSelected(event.isCompleted());
        final LocalTime startTime = event.getStartTime();
        final LocalTime endTime = event.getEndTime();
        calendarTimeStart.setTime(startTime);
        calendarTimeEnd.setTime(endTime);
        calendarFinalStartTimeLabel.setText(startTime == null ? "" : "@ " + startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        calendarFinalEndTimeLabel.setText(endTime == null ? "" : "till " + endTime.format(DateTimeFormatter.ofPattern("h:mm a")));



        if (event.getEventType() == TypedEvent.Types.CANVAS) {
            calendarSourceLabel.setText("Canvas: " + event.getEvent().getAltText());
        } else if (event.getEventType() == TypedEvent.Types.SCHEDULE) {
            calendarSourceLabel.setText(event.getAltText());
        } else {
            calendarSourceLabel.setText("Calendar");
        }


        setDescriptionBoxHeight();
    }

    public void setEventFieldsVisibility(final boolean isVisible) {
        this.calendarEventFields.setVisible(isVisible);
    }

    public ObservableList<Node> getScheduleBoxChildren() {
        return this.calendarScheduleBox.getChildrenUnmodifiable();
    }

    public void addScheduleBoxItem(final ScheduleBoxItem item) {
        Platform.runLater(() -> this.calendarScheduleBox.getChildren().add(item));
    }

    public void deleteSchedule(final ScheduleBoxItem item) {
        this.calendarScheduleBox.getChildren().remove(item);

    }

    public LeftLayouts getCurrentLayout() {
        return this.currentLayout;
    }










}
