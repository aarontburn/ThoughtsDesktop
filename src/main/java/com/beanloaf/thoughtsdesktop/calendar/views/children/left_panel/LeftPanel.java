package com.beanloaf.thoughtsdesktop.calendar.views.children.left_panel;

import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleBoxItem;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
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
    private Node calendarSmallEventFields;
    private TextField calendarSmallEventTitleInput;
    private DatePicker calendarSmallDatePicker;
    private TimeGroupView calendarSmallTimeStart, calendarSmallTimeEnd;
    private TextArea calendarSmallEventDescriptionInput;
    private Button calendarSmallSaveEventButton, calendarSmallEditButton, calendarSmallDeleteButton;
    private HBox calendarSmallStartTimeFields, calendarSmallEndTimeFields;
    private Label calendarSmallFinalStartTimeLabel, calendarSmallFinalEndTimeLabel, calendarDescriptionLabel, calendarTimeLabel;
    private CheckBox calendarSmallProgressCheckBox;
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


        /*  Small Event Input   */
        calendarDescriptionLabel = (Label) findNodeById("calendarDescriptionLabel");
        calendarTimeLabel = (Label) findNodeById("calendarTimeLabel");

        expandEventButton = (Label) findNodeById("expandEventButton");
        calendarSmallEventFields = findNodeById("calendarSmallEventFields");
        calendarSmallEventTitleInput = (TextField) findNodeById("calendarSmallEventTitleInput");
        calendarSmallDatePicker = (DatePicker) findNodeById("calendarSmallDatePicker");

        calendarSmallTimeStart = new TimeGroupView(
                (TextField) findNodeById("calendarSmallHourInputFrom"),
                (TextField) findNodeById("calendarSmallMinuteInputFrom"),
                (ComboBox<String>) findNodeById("calendarSmallAMPMSelectorFrom"));

        calendarSmallTimeEnd = new TimeGroupView(
                (TextField) findNodeById("calendarSmallHourInputTo"),
                (TextField) findNodeById("calendarSmallMinuteInputTo"),
                (ComboBox<String>) findNodeById("calendarSmallAMPMSelectorTo"));


        calendarSmallEventDescriptionInput = (TextArea) findNodeById("calendarSmallEventDescriptionInput");
        calendarSmallSaveEventButton = (Button) findNodeById("calendarSmallSaveEventButton");
        calendarSmallEditButton = (Button) findNodeById("calendarSmallEditButton");
        calendarSmallDeleteButton = (Button) findNodeById("calendarSmallDeleteButton");


        calendarSmallEndTimeFields = (HBox) findNodeById("calendarSmallEndTimeFields");
        calendarSmallStartTimeFields = (HBox) findNodeById("calendarSmallStartTimeFields");
        calendarSmallFinalStartTimeLabel = (Label) findNodeById("calendarSmallFinalStartTimeLabel");
        calendarSmallFinalEndTimeLabel = (Label) findNodeById("calendarSmallFinalEndTimeLabel");
        calendarSmallProgressCheckBox = (CheckBox) findNodeById("calendarSmallProgressCheckBox");
    }


    private void resizeDescriptionBox(final boolean isWrapping, final boolean toTop) {
        if (isWrapping) {
            ThoughtsHelper.setAnchor(
                    calendarSmallEventDescriptionInput, 313,
                    AnchorPane.getBottomAnchor(calendarSmallEventDescriptionInput),
                    AnchorPane.getLeftAnchor(calendarSmallEventDescriptionInput),
                    AnchorPane.getRightAnchor(calendarSmallEventDescriptionInput));

            ThoughtsHelper.setAnchor(
                    calendarDescriptionLabel, 290,
                    AnchorPane.getBottomAnchor(calendarDescriptionLabel),
                    AnchorPane.getLeftAnchor(calendarDescriptionLabel),
                    AnchorPane.getRightAnchor(calendarDescriptionLabel));

        } else {
            ThoughtsHelper.setAnchor(
                    calendarSmallEventDescriptionInput, toTop ? 212 : 276,
                    AnchorPane.getBottomAnchor(calendarSmallEventDescriptionInput),
                    AnchorPane.getLeftAnchor(calendarSmallEventDescriptionInput),
                    AnchorPane.getRightAnchor(calendarSmallEventDescriptionInput));

            ThoughtsHelper.setAnchor(
                    calendarDescriptionLabel, toTop ? 188 : 253,
                    AnchorPane.getBottomAnchor(calendarDescriptionLabel),
                    AnchorPane.getLeftAnchor(calendarDescriptionLabel),
                    AnchorPane.getRightAnchor(calendarDescriptionLabel));
        }

    }

    private void setDescriptionBoxHeight() {
        if (!calendarSmallEndTimeFields.isVisible()
                && calendarSmallFinalEndTimeLabel.getText().isEmpty()
                && calendarSmallFinalStartTimeLabel.getText().isEmpty()) {

            calendarTimeLabel.setVisible(false);
            resizeDescriptionBox(false, true);


        } else {
            calendarTimeLabel.setVisible(true);
            if (!calendarSmallEndTimeFields.isVisible()) {  // 448 is when wrapping occurs when editing, 280 when not editing
                resizeDescriptionBox(layoutMap.get(LeftLayouts.EVENTS).getWidth() < 280, false); // wrapping for final label
            } else {
                resizeDescriptionBox(layoutMap.get(LeftLayouts.EVENTS).getWidth() < 448, false); // wrapping for input fields
            }

        }
    }

    private void attachEvents() {

        calendarSmallEndTimeFields.visibleProperty().addListener((observableValue, aBoolean, isVisible) -> setDescriptionBoxHeight());
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

        calendarSmallEventFields.setVisible(false);

        calendarSmallProgressCheckBox.selectedProperty().addListener((observableValue, aBoolean, isChecked) ->
                calendarSmallProgressCheckBox.setText(isChecked ? "Completed" : "In-progress"));

        calendarSmallProgressCheckBox.setOnAction(e -> main.getCalendarHandler().getSelectedEvent().setCompleted(calendarSmallProgressCheckBox.isSelected(), true));


        calendarSmallSaveEventButton.setVisible(false);
        calendarSmallSaveEventButton.setOnAction(e -> {
            if (main.getCalendarHandler().getSelectedEvent() == null) {
                return;
            }
            main.getRightPanel().getMonthView().saveEvent(main.getCalendarHandler().getSelectedEvent(), main.getLeftPanel().getEventInputFields());
            main.getRightPanel().getMonthView().selectEvent(main.getCalendarHandler().getSelectedEvent(), false);
        });

        calendarSmallDeleteButton.setOnAction(e -> {
            if (main.getCalendarHandler().getSelectedEvent() == null) {
                return;
            }
            main.getRightPanel().getMonthView().deleteEvent(main.getCalendarHandler().getSelectedEvent());
        });


        calendarSmallEditButton.setOnAction(e -> {
            toggleSmallEventFields(true);
            calendarSmallSaveEventButton.setVisible(true);
        });

    }

    public void toggleSmallEventFields(final boolean isEnabled) {
        final boolean isDisabled = !isEnabled;
        calendarSmallEventTitleInput.setDisable(isDisabled);
        calendarSmallDatePicker.setDisable(isDisabled);
        calendarSmallSaveEventButton.setVisible(isEnabled);


        final ObservableList<String> styles = calendarSmallDatePicker.getStyleClass();
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

        calendarSmallStartTimeFields.setVisible(isEnabled);
        calendarSmallEndTimeFields.setVisible(isEnabled);

        calendarSmallFinalStartTimeLabel.setVisible(isDisabled);
        calendarSmallFinalEndTimeLabel.setVisible(isDisabled);

        calendarSmallEventDescriptionInput.setEditable(isEnabled);

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

        event.setTitle(calendarSmallEventTitleInput.getText());
        event.setStartDate(calendarSmallDatePicker.getValue());
        event.setStartTime(calendarSmallTimeStart.getTime());
        event.setEndTime(calendarSmallTimeEnd.getTime());
        event.setDescription(calendarSmallEventDescriptionInput.getText());

        return event;
    }

    public void setFinalStartEndTimeLabel(final DayEvent event) {
        final LocalTime startTime = event.getStartTime();
        final LocalTime endTime = event.getStartTime();
        calendarSmallFinalStartTimeLabel.setText(startTime == null ? "" : "@ " + startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        calendarSmallFinalEndTimeLabel.setText(endTime == null ? "" : "till " + endTime.format(DateTimeFormatter.ofPattern("h:mm a")));
    }

    public void onSelectEvent(final DayEvent event, final boolean editable) {
        calendarSmallEventFields.setVisible(true);
        toggleSmallEventFields(editable);

        calendarSmallSaveEventButton.setVisible(editable);
        calendarSmallEditButton.setVisible(!editable);
        calendarSmallDeleteButton.setDisable(event.getEventType() != TypedEvent.Types.DAY);
        calendarSmallEditButton.setDisable(event.getEventType() != TypedEvent.Types.DAY);


        calendarSmallEventTitleInput.setText(event.getEventTitle());
        calendarSmallEventDescriptionInput.setText(event.getDescription());
        calendarSmallDatePicker.setValue(LocalDate.of(event.getDate().getYear(), event.getDate().getMonth(), event.getDate().getDayOfMonth()));
        calendarSmallProgressCheckBox.setSelected(event.isCompleted());
        final LocalTime startTime = event.getStartTime();
        final LocalTime endTime = event.getEndTime();
        calendarSmallTimeStart.setTime(startTime);
        calendarSmallTimeEnd.setTime(endTime);
        calendarSmallFinalStartTimeLabel.setText(startTime == null ? "" : "@ " + startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        calendarSmallFinalEndTimeLabel.setText(endTime == null ? "" : "till " + endTime.format(DateTimeFormatter.ofPattern("h:mm a")));


        setDescriptionBoxHeight();
    }

    public void setEventFieldsVisibility(final boolean isVisible) {
        this.calendarSmallEventFields.setVisible(isVisible);
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
