package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CalendarJSONHandler;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarDay;
import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarMonth;
import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.notes.views.ThoughtsView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CalendarView extends ThoughtsView {

    private final GridPane calendarFrame; // (7 x 5)

    private final Map<Pair<Month, Integer>, CalendarMonth> activeMonths = new ConcurrentHashMap<>(); // key is Pair<Month, Year> (as an integer)

    private final List<Runnable> queuedTasks = Collections.synchronizedList(new ArrayList<>());

    private CalendarMonth currentMonth;
    private CalendarDay selectedDay;
    private DayEvent selectedEvent;

    public CalendarJSONHandler calendarJson;


    /*  Header  */
    private final Label calendarMonthYearLabel;
    private final Label calendarNextMonthButton, calendarPrevMonthButton;
    private final Button calendarTestButton;


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
    private final Button calendarSmallSaveEventButton, calendarSmallEditButton, calendarSmallDeleteButton;
    private final ComboBox<String> calendarSmallAMPMSelector;
    private final HBox calendarSmallFinalTime, calendarSmallTimeFields;
    private final Label calendarSmallFinalTimeLabel;
    private final CheckBox calendarSmallProgressCheckBox;
    private boolean calendarSmallProgressCheckBoxReady = true;


    /* Popup */
    private AnchorPane popupWindow;
    private ComboBox<String> calendarAMPMSelector, calendarRecurringTypeSelector;
    private Label calendarClosePopup;


    public CalendarView(final MainApplication main) {
        super(main);
        calendarJson = new CalendarJSONHandler(this);


        calendarFrame = (GridPane) findNodeByID("calendarFrame");

        /*  Header  */
        calendarMonthYearLabel = (Label) findNodeByID("calendarMonthYearLabel");
        calendarNextMonthButton = (Label) findNodeByID("calendarNextMonthButton");
        calendarPrevMonthButton = (Label) findNodeByID("calendarPrevMonthButton");
        calendarTestButton = (Button) findNodeByID("calendarTestButton");

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
        calendarSmallDeleteButton = (Button) findNodeByID("calendarSmallDeleteButton");
        calendarSmallAMPMSelector = (ComboBox<String>) findNodeByID("calendarSmallAMPMSelector");
        calendarSmallFinalTime = (HBox) findNodeByID("calendarSmallFinalTime");
        calendarSmallTimeFields = (HBox) findNodeByID("calendarSmallTimeFields");
        calendarSmallFinalTimeLabel = (Label) findNodeByID("calendarSmallFinalTimeLabel");
        calendarSmallProgressCheckBox = (CheckBox) findNodeByID("calendarSmallProgressCheckBox");


        /*  Popup   */
        calendarAMPMSelector = (ComboBox<String>) findNodeByID("calendarAMPMSelector");
        calendarRecurringTypeSelector = (ComboBox<String>) findNodeByID("calendarRecurringTypeSelector");
        calendarClosePopup = (Label) findNodeByID("calendarClosePopup");

        attachEvents();

        final LocalDate now = LocalDate.now();
        final CalendarMonth cMonth = activeMonths.get(new Pair<>(now.getMonth(), now.getYear()));

        currentMonth = cMonth != null ? cMonth : new CalendarMonth(now.getMonth(), this);


        changeMonth(currentMonth);

    }

    public void onOpen() {
        calendarFrame.requestFocus();
    }

    private void attachEvents() {
        /*  Header  */
        calendarNextMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getNextMonth()));
        calendarPrevMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getPreviousMonth()));
        calendarTestButton.setOnAction(e -> {
            Logger.log(activeMonths);

        });





        /*  Small New Event*/
        calendarSmallEventFields.setVisible(false);

        calendarSmallProgressCheckBox.selectedProperty().addListener((observableValue, aBoolean, isChecked) -> {
            calendarSmallProgressCheckBox.setText(isChecked ? "Completed" : "In-progress");



            if (calendarSmallProgressCheckBoxReady) selectedEvent.setCompleted(isChecked, true);
        });


        calendarNewEventButton.setOnMouseClicked(e -> {
            selectEvent(addEvent(selectedDay.getYear(), selectedDay.getMonth(), selectedDay.getDay(), "New Event", "", null), true);
        });

        calendarSmallSaveEventButton.setVisible(false);
        calendarSmallSaveEventButton.setOnAction(e -> {
            if (selectedEvent == null) return;
            saveEvent(selectedEvent);

            selectDay(selectedDay);
            selectEvent(selectedEvent, false);
        });

        calendarSmallDeleteButton.setOnAction(e -> {
            if (selectedEvent == null) return;
            deleteEvent(selectedEvent);
        });







        calendarSmallHourInput.textProperty().addListener((observableValue, s, value) -> {
            if (!value.matches("\\d*") || value.isEmpty()) {
                calendarSmallHourInput.setText(value.replaceAll("\\D", ""));
                return;
            }

            try {
                final int hour = Integer.parseInt(value);
                calendarSmallAMPMSelector.setDisable(hour > 12);


            } catch (NumberFormatException e) {
                Logger.log("Failed to convert " + value + " to an integer.");
            }

            if (value.length() >= 2) calendarSmallMinuteInput.requestFocus();
        });

        calendarSmallMinuteInput.textProperty().addListener((observableValue, s, value) -> {
            if (!value.matches("\\d*")) {
                calendarSmallMinuteInput.setText(value.replaceAll("\\D", ""));
            }
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
        if (currentMonth.getNumDaysWithEvents() == 0) {
            activeMonths.remove(new Pair<>(currentMonth.getMonth(), currentMonth.getYear()));
        }

        final CalendarMonth newMonth = activeMonths.get(new Pair<>(month.getMonth(), month.getYear()));
        currentMonth = newMonth != null ? newMonth : month;


        activeMonths.put(new Pair<>(month.getMonth(), month.getYear()), currentMonth);

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
                        Logger.log("adding a new calendar day for " + currentMonth.getMonth() + " " + day);

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

            synchronized (queuedTasks) {
                for (final Runnable runnable : queuedTasks) {
                    runnable.run();
                }
                queuedTasks.clear();
            }
        });


    }


    public DayEvent addEvent(final LocalDate date, final DayEvent event) {
        final Month month = date.getMonth();
        final Integer year = date.getYear();
        final int day = date.getDayOfMonth();


        final Pair<Month, Integer> monthYear = new Pair<>(month, year);

        CalendarMonth activeMonth = activeMonths.get(monthYear);
        if (activeMonth == null) {
            activeMonth = new CalendarMonth(month, year, this);
            activeMonths.put(monthYear, activeMonth);
        }

        if (day > activeMonth.getMonthLength()) throw new IllegalArgumentException("Day out of bounds. " + day);

        activeMonth.getDay(day).addEvent(event);


        return event;


    }

    private DayEvent addEvent(final int year, final Month month, final int day, final String eventName, final String desc, final String time) {
        final Pair<Month, Integer> monthYear = new Pair<>(month, year);

        CalendarMonth activeMonth = activeMonths.get(monthYear);
        if (activeMonth == null) {
            activeMonth = new CalendarMonth(month, year, this);
            activeMonths.put(monthYear, activeMonth);
        }

        if (day < 0 || day > activeMonth.getMonthLength())
            throw new IllegalArgumentException("Day out of bounds. " + day);

        final CalendarDay calendarDay = activeMonth.getDay(day);


        final DayEvent event = new DayEvent(LocalDate.of(calendarDay.getYear(), calendarDay.getMonth(), calendarDay.getDay()), eventName, this);
        event.setDescription(desc);

        if (time == null || !time.contains(":")) {
            event.setTime(null);
        } else {
            final String[] splitTime = time.split(":");
            event.setTime(Integer.parseInt(splitTime[0]), Integer.parseInt(splitTime[1]));
        }

        calendarJson.addEventToJson(event);

        calendarDay.addEvent(event);

        return event;

    }


    public void selectDay(final CalendarDay day) {
        this.selectedDay = day;

        calendarDayLabel.setText(ThoughtsHelper.toCamelCase(day.getMonth().toString()) + " " + day.getDay()
                + ThoughtsHelper.getNumberSuffix(day.getDay()) + ", " + day.getYear());

        calendarSmallEventFields.setVisible(false);

        calendarEventBox.getChildren().clear();


        for (final DayEvent dayEvent : day.getEvents()) {
            final DayEvent clone = new DayEvent(dayEvent, this);
            calendarEventBox.getChildren().add(clone);

            dayEvent.setClone(clone);

        }

    }

    public void selectEvent(DayEvent event, final boolean editable) {
        event.getStyleClass().add("selected-label");
        if (selectedEvent != null) selectedEvent.getStyleClass().remove("selected-label");
        selectedEvent = event;

        if (event.isClone) event = event.getClone();

        calendarSmallEventFields.setVisible(true);

        toggleSmallEventFields(editable);
        calendarSmallSaveEventButton.setVisible(editable);
        calendarSmallEditButton.setVisible(!editable);


        calendarSmallEventTitleInput.setText(event.getEventTitle());
        calendarSmallDatePicker.setValue(LocalDate.of(event.getDate().getYear(), event.getDate().getMonth(), event.getDate().getDayOfMonth()));

        calendarSmallProgressCheckBoxReady = false;
        calendarSmallProgressCheckBox.setSelected(event.isCompleted());
        calendarSmallProgressCheckBoxReady = true;

        final LocalTime time = event.getTime();

        if (time != null) {
            final String formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a"));
            final boolean isAm = formattedTime.contains("AM");


            int hour = time.getHour();
            if (hour > 12 && !isAm) hour = hour - 12;


            calendarSmallHourInput.setText(String.valueOf(hour));
            calendarSmallMinuteInput.setText(String.valueOf(time.getMinute()));

            calendarSmallAMPMSelector.getSelectionModel().select(isAm ? "AM" : "PM");

        } else {
            calendarSmallHourInput.setText("");
            calendarSmallMinuteInput.setText("");
        }





        calendarSmallFinalTimeLabel.setText(time == null ? "" : "@ " + time.format(DateTimeFormatter.ofPattern("h:mm a")));


        calendarSmallEventDescriptionInput.setText(event.getDescription());
    }

    private void toggleSmallEventFields(final boolean isEnabled) {
        final boolean isDisabled = !isEnabled;
        calendarSmallEventTitleInput.setDisable(isDisabled);
        calendarSmallDatePicker.setDisable(isDisabled);


        final ObservableList<String> styles = calendarSmallDatePicker.getStyleClass();
        final String disableDatePickerStyle = "non-editable-date-picker";


        if (isDisabled) {
            if (!styles.contains(disableDatePickerStyle)) styles.add(disableDatePickerStyle);
        } else {
            while (styles.contains(disableDatePickerStyle)) styles.remove(disableDatePickerStyle);
        }

        calendarSmallTimeFields.setVisible(isEnabled);
        calendarSmallFinalTime.setVisible(isDisabled);

        calendarSmallAMPMSelector.setDisable(isDisabled);
        calendarSmallEventDescriptionInput.setDisable(isDisabled);

    }

    public void saveEvent(DayEvent event) {
        if (event.isClone) event = event.getClone();

        event.setEventTitle(calendarSmallEventTitleInput.getText());
        event.setTime(calendarSmallHourInput.getText(), calendarSmallMinuteInput.getText(), calendarSmallAMPMSelector.getSelectionModel().getSelectedItem());
        event.setDescription(calendarSmallEventDescriptionInput.getText());

        final LocalTime time = event.getTime();
        calendarSmallFinalTimeLabel.setText(time == null ? "" : "@ " + time.format(DateTimeFormatter.ofPattern("h:mm a")));

        calendarSmallSaveEventButton.setVisible(false);
        toggleSmallEventFields(false);

        calendarJson.addEventToJson(event);

    }

    private void deleteEvent(final DayEvent event) {
        calendarJson.removeEventFromJson(selectedEvent);


        final Month month = event.getDate().getMonth();
        final Integer year = event.getDate().getYear();
        final int day = event.getDate().getDayOfMonth();

        final CalendarMonth calendarMonth = activeMonths.get(new Pair<>(month, year));

        if (calendarMonth == null)
            throw new RuntimeException("Could not find month to delete event " + event.getEventTitle() + " from.");



        calendarMonth.getDay(day).removeEvent(event);
        selectDay(selectedDay);



    }


}
