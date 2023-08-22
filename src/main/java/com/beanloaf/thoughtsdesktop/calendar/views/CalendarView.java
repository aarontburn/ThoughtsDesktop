package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CalendarJSONHandler;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarDay;
import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarMonth;
import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.notes.views.ThoughtsView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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


    private final Map<Pair<Month, Integer>, CalendarMonth> activeMonths = new ConcurrentHashMap<>(); // key is Pair<Month, Year> (as an integer)
    public final List<Runnable> queuedTasks = Collections.synchronizedList(new ArrayList<>());

    private CalendarMonth currentMonth;
    private CalendarDay selectedDay;
    private DayEvent selectedEvent;
    public final CalendarJSONHandler calendarJson;
    public final CalendarPopup popup;


    private GridPane calendarFrame; // (7 x 5)

    /*  Header  */
    private Label calendarMonthYearLabel;
    private Label calendarNextMonthButton, calendarPrevMonthButton;
    private Button calendarTestButton, calendarNewScheduleButton;


    /*  Event Box   */
    private VBox calendarEventBox;
    private Label calendarDayLabel;
    private Button calendarNewEventButton;


    /*  Schedule Box    */
    public VBox calendarScheduleBox;


    /*  Small Event Input */
    private AnchorPane calendarSmallEventFields;
    private TextField calendarSmallEventTitleInput;
    private DatePicker calendarSmallDatePicker;
    private TextField calendarSmallHourInputFrom, calendarSmallMinuteInputFrom, calendarSmallHourInputTo, calendarSmallMinuteInputTo;
    private TextArea calendarSmallEventDescriptionInput;
    private Button calendarSmallSaveEventButton, calendarSmallEditButton, calendarSmallDeleteButton;
    private ComboBox<String> calendarSmallAMPMSelectorFrom, calendarSmallAMPMSelectorTo;
    private HBox calendarSmallStartTimeFields, calendarSmallEndTimeFields;
    private Label calendarSmallFinalStartTimeLabel, calendarSmallFinalEndTimeLabel;
    private CheckBox calendarSmallProgressCheckBox;
    private boolean calendarSmallProgressCheckBoxReady = true;





    public CalendarView(final MainApplication main) {
        super(main);
        calendarJson = new CalendarJSONHandler(this);



        locateNodes();
        attachEvents();

        popup = new CalendarPopup(this);

        final LocalDate now = LocalDate.now();
        final CalendarMonth cMonth = activeMonths.get(new Pair<>(now.getMonth(), now.getYear()));

        currentMonth = cMonth != null ? cMonth : new CalendarMonth(now.getMonth(), this);


        changeMonth(currentMonth);

    }

    private void locateNodes() {
        calendarFrame = (GridPane) findNodeById("calendarFrame");

        /*  Header  */
        calendarMonthYearLabel = (Label) findNodeById("calendarMonthYearLabel");
        calendarNextMonthButton = (Label) findNodeById("calendarNextMonthButton");
        calendarPrevMonthButton = (Label) findNodeById("calendarPrevMonthButton");
        calendarTestButton = (Button) findNodeById("calendarTestButton");
        calendarNewScheduleButton = (Button) findNodeById("calendarNewScheduleButton");

        /*  Event Box   */
        calendarEventBox = (VBox) findNodeById("calendarEventBox");
        calendarDayLabel = (Label) findNodeById("calendarDayLabel");
        calendarNewEventButton = (Button) findNodeById("calendarNewEventButton");

        /*  Schedule Box    */
        calendarScheduleBox = (VBox) findNodeById("calendarScheduleBox");


        /*  Small Event Input   */
        calendarSmallEventFields = (AnchorPane) findNodeById("calendarSmallEventFields");
        calendarSmallEventTitleInput = (TextField) findNodeById("calendarSmallEventTitleInput");
        calendarSmallDatePicker = (DatePicker) findNodeById("calendarSmallDatePicker");
        calendarSmallHourInputFrom = CH.setNumbersOnlyTextField((TextField) findNodeById("calendarSmallHourInputFrom"));
        calendarSmallMinuteInputFrom = CH.setNumbersOnlyTextField((TextField) findNodeById("calendarSmallMinuteInputFrom"));
        calendarSmallHourInputTo = CH.setNumbersOnlyTextField((TextField) findNodeById("calendarSmallHourInputTo"));
        calendarSmallMinuteInputTo = CH.setNumbersOnlyTextField((TextField) findNodeById("calendarSmallMinuteInputTo"));
        calendarSmallEventDescriptionInput = (TextArea) findNodeById("calendarSmallEventDescriptionInput");
        calendarSmallSaveEventButton = (Button) findNodeById("calendarSmallSaveEventButton");
        calendarSmallEditButton = (Button) findNodeById("calendarSmallEditButton");
        calendarSmallDeleteButton = (Button) findNodeById("calendarSmallDeleteButton");
        calendarSmallAMPMSelectorFrom = CH.setAMPMComboBox((ComboBox<String>) findNodeById("calendarSmallAMPMSelectorFrom"));
        calendarSmallAMPMSelectorTo = CH.setAMPMComboBox((ComboBox<String>) findNodeById("calendarSmallAMPMSelectorTo"));


        calendarSmallEndTimeFields = (HBox) findNodeById("calendarSmallEndTimeFields");
        calendarSmallStartTimeFields = (HBox) findNodeById("calendarSmallStartTimeFields");

        calendarSmallFinalStartTimeLabel = (Label) findNodeById("calendarSmallFinalStartTimeLabel");
        calendarSmallFinalEndTimeLabel = (Label) findNodeById("calendarSmallFinalEndTimeLabel");

        calendarSmallProgressCheckBox = (CheckBox) findNodeById("calendarSmallProgressCheckBox");



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

        calendarNewScheduleButton.setOnAction(e -> popup.displaySchedule(new ScheduleData()));





        /*  Small New Event*/
        calendarSmallEventFields.setVisible(false);

        calendarSmallProgressCheckBox.selectedProperty().addListener((observableValue, aBoolean, isChecked) -> {
            calendarSmallProgressCheckBox.setText(isChecked ? "Completed" : "In-progress");


            if (calendarSmallProgressCheckBoxReady) selectedEvent.setCompleted(isChecked, true);
        });


        calendarNewEventButton.setOnMouseClicked(e -> {
            if (selectedDay != null)
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

        calendarSmallHourInputFrom.textProperty().addListener((observableValue, s, value) -> {
            if (value.isEmpty()) return;

            try {
                final int hour = Integer.parseInt(value);
                calendarSmallAMPMSelectorFrom.setDisable(hour > 12);

            } catch (NumberFormatException e) {
                Logger.log("Failed to convert " + value + " to an integer.");
            }

            if (value.length() >= 2) calendarSmallMinuteInputFrom.requestFocus();
        });

        calendarSmallMinuteInputFrom.textProperty().addListener((observableValue, s, value) -> {
            if (value.length() >= 2) calendarSmallHourInputTo.requestFocus();
        });


        calendarSmallHourInputTo.textProperty().addListener(((observableValue, s, value) -> {
            if (value.isEmpty()) return;

            try {
                final int hour = Integer.parseInt(value);
                calendarSmallAMPMSelectorTo.setDisable(hour > 12);

            } catch (NumberFormatException e) {
                Logger.log("Failed to convert " + value + " to an integer.");
            }

            if (value.length() >= 2) calendarSmallMinuteInputTo.requestFocus();
        }));


        calendarSmallMinuteInputTo.textProperty().addListener((observableValue, s, value) -> {
            if (value.length() >= 2) calendarSmallEventDescriptionInput.requestFocus();
        });



        calendarSmallEditButton.setOnAction(e -> {
            toggleSmallEventFields(true);
            calendarSmallSaveEventButton.setVisible(true);
        });



        calendarScheduleBox.getChildren().clear();
    }







    private void changeMonth(final CalendarMonth month) {
        for (final Pair<Month, Integer> key : activeMonths.keySet()) {
            final CalendarMonth calendarMonth = activeMonths.get(key);

            if (calendarMonth.getNumDaysWithEvents() == 0) {
                activeMonths.remove(key);
            }

        }


        final Pair<Month, Integer> prevMonthYear = new Pair<>(month.getPreviousMonth().getMonth(), month.getPreviousMonth().getYear());
        final Pair<Month, Integer> monthYear = new Pair<>(month.getMonth(), month.getYear());
        final Pair<Month, Integer> nextMonthYear = new Pair<>(month.getNextMonth().getMonth(), month.getNextMonth().getYear());


        CalendarMonth prevMonth = activeMonths.get(prevMonthYear);
        prevMonth = prevMonth != null ? prevMonth : month.getPreviousMonth();
        activeMonths.put(prevMonthYear, prevMonth);


        CalendarMonth nextMonth = activeMonths.get(nextMonthYear);
        nextMonth = nextMonth != null ? nextMonth : month.getNextMonth();
        activeMonths.put(nextMonthYear, nextMonth);


        final CalendarMonth newMonth = activeMonths.get(monthYear);
        currentMonth = newMonth != null ? newMonth : month;
        activeMonths.put(monthYear, currentMonth);






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
                    if (calendarDay == null) {
                        calendarDay = new CalendarDay(prevMonth.getYear(), prevMonth.getMonth(), prevDays, this);
                    }
                    prevDays++;
                    calendarFrame.add(calendarDay, col, row);


                } else if (day >= monthLength) { // after the last day of the month
                    CalendarDay calendarDay = nextMonth.getDay(overflowDays);
                    if (calendarDay == null) {
                        calendarDay = new CalendarDay(nextMonth.getYear(), nextMonth.getMonth(), overflowDays, this);
                    }
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
            event.setStartTime(null);
        } else {
            final String[] splitTime = time.split(":");
            event.setStartTime(Integer.parseInt(splitTime[0]), Integer.parseInt(splitTime[1]));
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
        event.getStyleClass().add("selected-day-event");
        if (selectedEvent != null) selectedEvent.getStyleClass().remove("selected-day-event");
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

        final LocalTime startTime = event.getStartTime();
        final LocalTime endTime = event.getEndTime();

        if (startTime != null) {
            final String formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("h:mm a"));
            final boolean startIsAM = formattedStartTime.contains("AM");

            calendarSmallHourInputFrom.setText(startTime.format(DateTimeFormatter.ofPattern("hh")));
            calendarSmallMinuteInputFrom.setText(startTime.format(DateTimeFormatter.ofPattern("mm")));
            calendarSmallAMPMSelectorFrom.getSelectionModel().select(startIsAM ? "AM" : "PM");

        } else {
            calendarSmallHourInputFrom.setText("");
            calendarSmallMinuteInputFrom.setText("");
        }

        if (endTime != null) {
            final String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("h:mm a"));
            final boolean endIsAM = formattedEndTime.contains("AM");

            calendarSmallHourInputTo.setText(endTime.format(DateTimeFormatter.ofPattern("hh")));
            calendarSmallMinuteInputTo.setText(endTime.format(DateTimeFormatter.ofPattern("mm")));
            calendarSmallAMPMSelectorTo.getSelectionModel().select(endIsAM ? "AM" : "PM");

        } else {
            calendarSmallHourInputTo.setText("");
            calendarSmallMinuteInputTo.setText("");
        }


        calendarSmallFinalStartTimeLabel.setText(startTime == null ? "" : "@ " + startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        calendarSmallFinalEndTimeLabel.setText(endTime == null ? "" : "till " + endTime.format(DateTimeFormatter.ofPattern("h:mm a")));


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

        calendarSmallStartTimeFields.setVisible(isEnabled);
        calendarSmallFinalStartTimeLabel.setVisible(isDisabled);

        calendarSmallEndTimeFields.setVisible(isEnabled);
        calendarSmallFinalEndTimeLabel.setVisible(isDisabled);

        calendarSmallAMPMSelectorFrom.setDisable(isDisabled);
        calendarSmallEventDescriptionInput.setDisable(isDisabled);

    }

    public void saveEvent(DayEvent event) {
        if (event.isClone) event = event.getClone();

        event.setEventTitle(calendarSmallEventTitleInput.getText());
        event.setDate(calendarSmallDatePicker.getValue());

        event.setStartTime(calendarSmallHourInputFrom.getText(), calendarSmallMinuteInputFrom.getText(), calendarSmallAMPMSelectorFrom.getSelectionModel().getSelectedItem());
        event.setEndTime(calendarSmallHourInputTo.getText(), calendarSmallMinuteInputTo.getText(), calendarSmallAMPMSelectorTo.getSelectionModel().getSelectedItem());
        event.setDescription(calendarSmallEventDescriptionInput.getText());

        final LocalTime startTime = event.getStartTime();
        final LocalTime endTime = event.getStartTime();
        calendarSmallFinalStartTimeLabel.setText(startTime == null ? "" : "@ " + startTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        calendarSmallFinalEndTimeLabel.setText(endTime == null ? "" : "till " + endTime.format(DateTimeFormatter.ofPattern("h:mm a")));

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
