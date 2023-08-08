package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarDay;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarMonth;
import com.beanloaf.thoughtsdesktop.objects.calendar.DayEvent;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class CalendarView extends ThoughtsView {

    private final GridPane calendarFrame; // (7 x 5)

    private final Map<Pair<Month, Integer>, CalendarMonth> activeMonths = new HashMap<>();

    private final List<Runnable> queuedTasks = Collections.synchronizedList(new ArrayList<>());

    private CalendarMonth currentMonth;


    /*  Header  */
    private final Label calendarMonthYearLabel;
    private final Label calendarNextMonthButton, calendarPrevMonthButton;


    /*  Event Box   */
    private final VBox calendarEventBox;
    private final Label calendarDayLabel;
    private final Button calendarNewEventButton;

    /*  Small Event Input */
    private final AnchorPane calendarSmallEventFields;
    private final TextField calendarSmallEventTitleInput;
    private final DatePicker calendarSmallDatePicker;
    private final TextField calendarSmallHourInput, calendarSmallMinuteInput;
    private final TextArea calendarSmallEventDescriptionInput;
    private final Button calendarSmallSaveEventButton, calendarSmallEditButton;
    private final ComboBox<String> calendarSmallAMPMSelector;


    private CalendarDay selectedDay;
    private DayEvent selectedEvent;


    /* Popup */
    private AnchorPane popupWindow;
    private ComboBox<String> calendarAMPMSelector, calendarRecurringTypeSelector;
    private Label calendarClosePopup;


    public CalendarView(final MainApplication main) {
        super(main);


        calendarFrame = (GridPane) findNodeByID("calendarFrame");

        /*  Header  */
        calendarMonthYearLabel = (Label) findNodeByID("calendarMonthYearLabel");
        calendarNextMonthButton = (Label) findNodeByID("calendarNextMonthButton");
        calendarPrevMonthButton = (Label) findNodeByID("calendarPrevMonthButton");

        /*  Event Box   */
        calendarEventBox = (VBox) findNodeByID("calendarEventBox");
        calendarDayLabel = (Label) findNodeByID("calendarDayLabel");
        calendarNewEventButton = (Button) findNodeByID("calendarNewEventButton");


        /*  Small Event Input   */
        calendarSmallEventFields = (AnchorPane) findNodeByID("calendarSmallEventFields");
        calendarSmallEventTitleInput = (TextField) findNodeByID("calendarSmallEventTitleInput");
        calendarSmallDatePicker = (DatePicker) findNodeByID("calendarSmallDatePicker");
        calendarSmallHourInput = (TextField) findNodeByID("calendarSmallHourInput");
        calendarSmallMinuteInput = (TextField) findNodeByID("calendarSmallMinuteInput");
        calendarSmallEventDescriptionInput = (TextArea) findNodeByID("calendarSmallEventDescriptionInput");
        calendarSmallSaveEventButton = (Button) findNodeByID("calendarSmallSaveEventButton");
        calendarSmallEditButton = (Button) findNodeByID("calendarSmallEditButton");
        calendarSmallAMPMSelector = (ComboBox<String>) findNodeByID("calendarSmallAMPMSelector");


        /*  Popup   */
        calendarAMPMSelector = (ComboBox<String>) findNodeByID("calendarAMPMSelector");
        calendarRecurringTypeSelector = (ComboBox<String>) findNodeByID("calendarRecurringTypeSelector");
        calendarClosePopup = (Label) findNodeByID("calendarClosePopup");

        attachEvents();
        currentMonth = new CalendarMonth(LocalDate.now().getMonth());

        createCalendarGUI();


        // registerEvents();


    }

    public void onOpen() {
        calendarFrame.requestFocus();
    }

    private void attachEvents() {
        /*  Header  */
        calendarNextMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getNextMonth()));

        calendarPrevMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getPreviousMonth()));

        calendarNewEventButton.setOnMouseClicked(e -> {
            addEvent(selectedDay.getDay());
        });


        /*  Small New Event*/
        calendarSmallEventFields.setVisible(false);


        calendarSmallSaveEventButton.setVisible(false);
        calendarSmallSaveEventButton.setOnAction(e -> {
            if (selectedEvent == null) return;

            selectedEvent.setEventName(calendarSmallEventTitleInput.getText());
            selectedEvent.setTime(calendarSmallHourInput.getText(), calendarSmallMinuteInput.getText());
            selectedEvent.setDescription(calendarSmallEventDescriptionInput.getText());


            calendarSmallSaveEventButton.setVisible(false);
            toggleSmallEventFields(false);

        });

        calendarSmallHourInput.textProperty().addListener((observableValue, s, newValue) -> {
            if (newValue.length() >= 2) calendarSmallMinuteInput.requestFocus();
        });

        calendarSmallAMPMSelector.getItems().clear();
        calendarSmallAMPMSelector.getItems().addAll("AM", "PM");
        calendarSmallAMPMSelector.getSelectionModel().select("PM");


        calendarSmallEditButton.setOnAction(e -> {
            toggleSmallEventFields(true);
            calendarSmallSaveEventButton.setVisible(true);
        });


        /*  Popup   */
        calendarAMPMSelector.getItems().clear();
        calendarAMPMSelector.getItems().addAll("AM", "PM");
        calendarAMPMSelector.getSelectionModel().select("PM");

        calendarRecurringTypeSelector.getItems().clear();
        calendarRecurringTypeSelector.getItems().addAll("Day", "Week", "Month", "Year");
        calendarRecurringTypeSelector.getSelectionModel().select("Week");

        calendarClosePopup.setOnMouseClicked(e -> {
            popupWindow.setVisible(false);
        });


        createPopup();

    }

    private void createPopup() {

        popupWindow = (AnchorPane) findNodeByID("popupWindow");


        // TODO: Drag popup window feature?
//        popupWindow.setOnMouseDragged(e -> {
//            Logger.log("X: " + e.getX() + " Y: " + e.getY());
//        });
//
//        popupWindow.setOnMouseClicked(e -> {
//            Logger.log("X: " + e.getX() + " Y: " + e.getY());
//
//        });





    }

    public void resizePopupHeight(final double newHeight) {
        if (popupWindow == null) return;

        final double anchor = newHeight * 1 / 6;

        AnchorPane.setTopAnchor(popupWindow, anchor);
        AnchorPane.setBottomAnchor(popupWindow, anchor);
    }

    public void resizePopupWidth(final double newWidth) {
        try {
            if (popupWindow == null) return;


            final double anchor = newWidth * 1 / 5;

            AnchorPane.setLeftAnchor(popupWindow, anchor);
            AnchorPane.setRightAnchor(popupWindow, anchor);
        } catch (Exception e) {
            Logger.log(e);
        }

    }

    private void changeMonth(final CalendarMonth month) {
        if (currentMonth.getNumDaysWithEvents() > 0) {
            Logger.log("saving " + currentMonth.getMonth() + " " + currentMonth.getYear());

            activeMonths.put(new Pair<>(currentMonth.getMonth(), currentMonth.getYear()), currentMonth);
        }

        final CalendarMonth newMonth = activeMonths.get(new Pair<>(month.getMonth(), month.getYear()));

        currentMonth = newMonth == null ? month : newMonth;
        createCalendarGUI();


    }


    private void createCalendarGUI() {
        this.calendarMonthYearLabel.setText(ThoughtsHelper.toCamelCase(currentMonth.getMonth().toString()) + ", " + currentMonth.getYear());
        final int monthLength = currentMonth.getMonthLength();


        CalendarMonth p = activeMonths.get(new Pair<>(currentMonth.getPreviousMonth().getMonth(), currentMonth.getPreviousMonth().getYear()));
        final CalendarMonth prevMonth = p != null ? p : currentMonth.getPreviousMonth();

        CalendarMonth n = activeMonths.get(new Pair<>(currentMonth.getNextMonth().getMonth(), currentMonth.getNextMonth().getYear()));
        final CalendarMonth nextMonth = n != null ? n : currentMonth.getNextMonth();

        Platform.runLater(() -> {
            calendarFrame.getChildren().clear();

            int row = 0;
            int col = 0;
            int day = 0;

            int overflowDays = 1;

            int prevDays = currentMonth.getPreviousMonth().getMonthLength() - (currentMonth.getStartingDayOfWeek() - 1);

            for (int i = 0; i < calendarFrame.getColumnCount() * calendarFrame.getRowCount(); i++) {

                if ((row == 0 && col < currentMonth.getStartingDayOfWeek())) { // first row, before the first day of the month
                    CalendarDay calendarDay = prevMonth.getDay(prevDays);
                    if (calendarDay == null)
                        calendarDay = new CalendarDay(prevMonth.getYear(), prevMonth.getMonth(), prevDays, this);
                    prevDays++;
                    calendarFrame.add(calendarDay, col, row);


                } else if (day >= monthLength) { // after the last day of the month
                    CalendarDay calendarDay = nextMonth.getDay(overflowDays);
                    if (calendarDay == null)
                        calendarDay = new CalendarDay(nextMonth.getYear(), nextMonth.getMonth(), overflowDays, this);
                    overflowDays++;
                    calendarFrame.add(calendarDay, col, row);


                } else { // normal month
                    day++;

                    CalendarDay calendarDay = currentMonth.getDay(day);
                    if (calendarDay == null) {
                        calendarDay = new CalendarDay(currentMonth.getYear(), currentMonth.getMonth(), day, this);
                        currentMonth.addDay(day, calendarDay);
                    }
                    calendarFrame.add(calendarDay, col, row);
                }


                col++;

                if (col % 7 == 0) {
                    row++;
                    col = 0;
                }

            }

            if (!queuedTasks.isEmpty()) {
                synchronized (queuedTasks) {
                    for (final Runnable runnable : queuedTasks) {
                        runnable.run();
                    }
                }
            }
            queuedTasks.clear();
        });


    }


    private void addEvent(final int day) {

        Platform.runLater(() -> {
            if (day < 0 || day > currentMonth.getMonthLength())
                throw new IllegalArgumentException("Day out of bounds. " + day);

            final CalendarDay calendarDay = currentMonth.getDay(day);

            if (calendarDay == null) {
                queuedTasks.add(() -> addEvent(day));
                return;
            }

            selectEvent(calendarDay.addEvent("New Event"));
        });

    }

    public void selectDay(final CalendarDay day) {
        this.selectedDay = day;

        calendarDayLabel.setText(ThoughtsHelper.toCamelCase(day.getMonth().toString()) + " " + day.getDay()
                + ThoughtsHelper.getNumberSuffix(day.getDay()) + ", " + day.getYear());

        calendarSmallEventFields.setVisible(false);



        calendarEventBox.getChildren().clear();


        for (final DayEvent dayEvent : day.getEvents()) {
            calendarEventBox.getChildren().add(new DayEvent(dayEvent, this));

        }

    }

    public void selectEvent(final DayEvent event) {
        event.getStyleClass().add("selected-label");
        if (selectedEvent != null) selectedEvent.getStyleClass().remove("selected-label");
        selectedEvent = event;

        calendarSmallEventFields.setVisible(true);

        toggleSmallEventFields(false);
        calendarSmallSaveEventButton.setVisible(false);

        calendarSmallEventTitleInput.setText(event.getEventName());
        calendarSmallDatePicker.setValue(LocalDate.of(event.getCalendarDay().getYear(), event.getCalendarDay().getMonth(), event.getCalendarDay().getDay()));

        final LocalTime time = event.getTime();
        calendarSmallHourInput.setText(time == null ? "" : String.valueOf(time.getHour()));
        calendarSmallMinuteInput.setText(time == null ? "" : String.valueOf(time.getMinute()));

        calendarSmallEventDescriptionInput.setText(event.getDescription());
    }

    private void toggleSmallEventFields(final boolean isEnabled) {
        final boolean isDisabled = !isEnabled;
        calendarSmallEventTitleInput.setDisable(isDisabled);
        calendarSmallDatePicker.setDisable(isDisabled);
        calendarSmallHourInput.setDisable(isDisabled);
        calendarSmallMinuteInput.setDisable(isDisabled);
        calendarSmallAMPMSelector.setDisable(isDisabled);
        calendarSmallEventDescriptionInput.setDisable(isDisabled);

    }


}
