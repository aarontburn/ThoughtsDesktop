package com.beanloaf.thoughtsdesktop.calendar.views.children.overlays;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.TimeGroupView;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventOverlay {

    private final CalendarMain main;
    private final BasicEvent event;


    private RepeatTab currentRepeatMode;

    public enum RepeatTab {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    /*  Components  */
    private TextField eventTitleInput;
    private DatePicker eventDatePicker;
    private TextArea eventDescInput;
    private TimeGroupView eventHourFrom, eventHourTo;
    private Button saveEventButton;


    private Node repeatPane;
    private CheckBox repeatCheckBox;
    private Map<RepeatTab, Node> repeatTabMap = new HashMap<>();
    private Label closeButton;


    private ComboBox<String> repeatTypeComboBox;

    /*  Daily   */
    private TextField dailyNumInput;

    /*  ---------   */
    /*  Weekly  */
    private TextField weeklyNumInput;
    private FlowPane weeklyDayOfWeekPane;

    /*  ---------   */
    /*  Monthly  */
    private HBox monthlyDayHBox, monthlyWeekdayHBox;
    private TextField monthlyNumInput, monthlyDayInput;
    private RadioButton monthlyDayRadioButton, monthlyWeekdayRadioButton;
    private ComboBox<String> monthlyWeekdayNumComboBox, monthlyWeekdayComboBox;
    private Label monthlySuffixLabel;
    /*  ---------   */
    /*  Yearly      */
    private HBox yearlyDayHBox, yearlyWeekdayHBox;
    private TextField yearlyNumInput, yearlyDayInput;
    private Label yearlySuffixLabel;
    private RadioButton yearlyDayRadioButton, yearlyWeekdayRadioButton;
    private ComboBox<String> yearlyMonthComboBox, yearlyWeekdayNumComboBox, yearlyWeekdayComboBox;

    /*  ---------   */

    private static final String URL = "https://google.com";

    public EventOverlay(final CalendarMain main, final BasicEvent event) {
        this.main = main;
        this.event = event;

        locateNodes();
        attachEvents();

        repeatPane.setDisable(true);
        swapRepeatTab(RepeatTab.DAILY);

        startup();





    }

    private void startup() {
        eventTitleInput.setText(event.getTitle());
        eventDatePicker.setValue(event.getStartDate());
        eventDescInput.setText(event.getDescription());
        eventHourFrom.setTime(event.getStartTime());
        eventHourTo.setTime(event.getEndTime());


    }


    private Node findNodeById(final String nodeId) {
        return main.findNodeById(nodeId);
    }

    private void locateNodes() {
        closeButton = (Label) findNodeById("closeEventOverlayButton");
        saveEventButton = (Button) findNodeById("saveEventButton");

        eventTitleInput = (TextField) findNodeById("eventTitleInput");
        eventDatePicker = (DatePicker) findNodeById("eventDatePicker");
        eventDescInput = (TextArea) findNodeById("eventDescInput");

        eventHourFrom = new TimeGroupView(
                (TextField) findNodeById("eventHourInputFrom"),
                (TextField) findNodeById("eventMinuteInputFrom"),
                (ComboBox<String>) findNodeById("eventAMPMSelectorFrom"));

        eventHourTo = new TimeGroupView(
                (TextField) findNodeById("eventHourInputTo"),
                (TextField) findNodeById("eventMinuteInputTo"),
                (ComboBox<String>) findNodeById("eventAMPMSelectorTo"));


        repeatCheckBox = (CheckBox) findNodeById("repeatCheckBox");
        repeatPane = findNodeById("repeatPane");
        repeatTabMap.put(RepeatTab.DAILY, findNodeById("dailyRepeatPane"));
        repeatTabMap.put(RepeatTab.WEEKLY, findNodeById("weeklyRepeatPane"));
        repeatTabMap.put(RepeatTab.MONTHLY, findNodeById("monthlyRepeatPane"));
        repeatTabMap.put(RepeatTab.YEARLY, findNodeById("yearlyRepeatPane"));


        repeatTypeComboBox = CH.setStringComboBoxValues(
                (ComboBox<String>) findNodeById("repeatTypeComboBox"),
                "Daily", "Weekly", "Monthly", "Yearly");


        /*  Daily   */
        dailyNumInput = (TextField) findNodeById("dailyNumInput");

        /*  ---------   */
        /*  Weekly  */
        weeklyNumInput = (TextField) findNodeById("weeklyNumInput");
        weeklyDayOfWeekPane = (FlowPane) findNodeById("weeklyDayOfWeekPane");

        /*  ---------   */
        /*  Monthly  */
        monthlyDayHBox = (HBox) findNodeById("monthlyDayHBox");
        monthlyWeekdayHBox = (HBox) findNodeById("monthlyWeekdayHBox");
        monthlyNumInput = CH.setNumbersOnlyTextField((TextField) findNodeById("monthlyNumInput"));
        monthlyDayInput = CH.setNumbersOnlyTextField((TextField) findNodeById("monthlyDayInput"));
        monthlySuffixLabel = (Label) findNodeById("monthlySuffixLabel");

        final ToggleGroup monthlyToggleGroup = new ToggleGroup();
        monthlyDayRadioButton = (RadioButton) findNodeById("monthlyDayRadioButton");
        monthlyDayRadioButton.setToggleGroup(monthlyToggleGroup);
        monthlyWeekdayRadioButton = (RadioButton) findNodeById("monthlyWeekdayRadioButton");
        monthlyWeekdayRadioButton.setToggleGroup(monthlyToggleGroup);


        monthlyWeekdayNumComboBox = CH.setStringComboBoxValues(
                (ComboBox<String>) findNodeById("monthlyWeekdayNumComboBox"),
                "first", "second", "third", "fourth", "last");
        monthlyWeekdayComboBox = CH.setStringComboBoxValues(
                (ComboBox<String>) findNodeById("monthlyWeekdayComboBox"),
                Weekday.getFullWeekdayNames().toArray(new String[0]));
        /*  ---------   */
        /*  Yearly      */
        yearlyDayHBox = (HBox) findNodeById("yearlyDayHBox");
        yearlyWeekdayHBox = (HBox) findNodeById("yearlyWeekdayHBox");
        yearlyNumInput = CH.setNumbersOnlyTextField((TextField) findNodeById("yearlyNumInput"));
        yearlyDayInput = CH.setNumbersOnlyTextField((TextField) findNodeById("yearlyDayInput"));

        final ToggleGroup yearlyToggleGroup = new ToggleGroup();
        yearlyDayRadioButton = (RadioButton) findNodeById("yearlyDayRadioButton");
        yearlyDayRadioButton.setToggleGroup(yearlyToggleGroup);
        yearlyWeekdayRadioButton = (RadioButton) findNodeById("yearlyWeekdayRadioButton");
        yearlyWeekdayRadioButton.setToggleGroup(yearlyToggleGroup);

        yearlySuffixLabel = (Label) findNodeById("yearlySuffixLabel");

        final List<String> monthList = new ArrayList<>();
        for (final Month month : Month.values()) {
            monthList.add(ThoughtsHelper.toCamelCase(month.toString()));
        }

        yearlyMonthComboBox = CH.setStringComboBoxValues((ComboBox<String>) findNodeById("yearlyMonthComboBox"),
                monthList.toArray(new String[0]));
        yearlyWeekdayNumComboBox = CH.setStringComboBoxValues((ComboBox<String>) findNodeById("yearlyWeekdayNumComboBox"),
                "first", "second", "third", "fourth", "last");
        yearlyWeekdayComboBox = CH.setStringComboBoxValues((ComboBox<String>) findNodeById("yearlyWeekdayComboBox"),
                Weekday.getFullWeekdayNames().toArray(new String[0]));
        /*  ---------   */


    }

    private void attachEvents() {
        closeButton.setOnMouseClicked(e -> main.swapOverlay(CalendarMain.Overlays.CALENDAR));

        saveEventButton.setOnAction(e -> {
            main.getRightPanel().getMonthView().saveEvent(event, getEventInputFields());
            main.swapOverlay(CalendarMain.Overlays.CALENDAR);
        });

        repeatCheckBox.selectedProperty().addListener((observableValue, aBoolean, isChecked) ->
                repeatPane.setDisable(!isChecked));

        repeatTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, swappedTo) -> {
            if (swappedTo != null) {
                swapRepeatTab(RepeatTab.valueOf(swappedTo.toUpperCase()));
            }
        });
        /*  Daily   */

        /*  ---------   */
        /*  Weekly  */

        /*  ---------   */
        /*  Monthly  */
        monthlyDayRadioButton.setSelected(true);
        monthlyDayHBox.setOnMouseClicked(e -> monthlyDayRadioButton.setSelected(true));
        monthlyWeekdayHBox.setOnMouseClicked(e -> monthlyWeekdayRadioButton.setSelected(true));

        monthlyDayInput.textProperty().addListener((observableValue, s, text) -> {
            if (!text.isEmpty()) {
                monthlySuffixLabel.setText(ThoughtsHelper.getNumberSuffix(Integer.parseInt(text)));
            }
        });


        /*  ---------   */
        /*  Yearly      */
        yearlyDayRadioButton.setSelected(true);
        yearlyDayHBox.setOnMouseClicked(e -> yearlyDayRadioButton.setSelected(true));
        yearlyWeekdayHBox.setOnMouseClicked(e -> yearlyWeekdayRadioButton.setSelected(true));
        /*  ---------   */


    }

    public void swapRepeatTab(final RepeatTab visibleTab) {
        for (final RepeatTab tab : repeatTabMap.keySet()) {
            repeatTabMap.get(tab).setVisible(false);
        }
        repeatTabMap.get(visibleTab).setVisible(true);
        currentRepeatMode = visibleTab;
    }

    public BasicEvent getEventInputFields() {
        final BasicEvent event = new BasicEvent();

        event.setTitle(eventTitleInput.getText());
        event.setStartDate(eventDatePicker.getValue());
        event.setStartTime(eventHourFrom.getTime());
        event.setEndTime(eventHourTo.getTime());
        event.setDescription(eventDescInput.getText());

        return event;


    }


}
