package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarDay;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarMonth;
import com.beanloaf.thoughtsdesktop.objects.calendar.DayEvent;
import javafx.application.Platform;
import javafx.scene.control.*;
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


    private final Label calendarMonthYearLabel;
    private final Label calendarNextMonthButton, calendarPrevMonthButton;


    /*  Event Box   */
    private final VBox calendarEventBox;
    private final Label calendarDayLabel;
    private final Button calendarNewEventButton;

    /*  New Event Input */
    private final TextField calendarEventTitleInput;
    private final DatePicker calendarDatePicker;
    private final TextField calendarFromHourInput, calendarFromMinuteInput;
    private final TextField calendarToHourInput, calendarToMinuteInput;
    private final TextArea calendarEventDescriptionInput;

    private final ToggleButton calendarFromAMToggle, calendarFromPMToggle;
    private final ToggleButton calendarToAMToggle, calendarToPMToggle;
    private final Button calendarSaveEventButton;


    private CalendarDay selectedDay;
    private DayEvent selectedEvent;

    private boolean buildingMonth;


    public CalendarView(final MainApplication main) {
        super(main);


        calendarFrame = (GridPane) findNodeByID("calendarFrame");

        calendarMonthYearLabel = (Label) findNodeByID("calendarMonthYearLabel");

        calendarNextMonthButton = (Label) findNodeByID("calendarNextMonthButton");
        calendarPrevMonthButton = (Label) findNodeByID("calendarPrevMonthButton");

        calendarEventBox = (VBox) findNodeByID("calendarEventBox");
        calendarDayLabel = (Label) findNodeByID("calendarDayLabel");
        calendarNewEventButton = (Button) findNodeByID("calendarNewEventButton");


        calendarEventTitleInput = (TextField) findNodeByID("calendarEventTitleInput");
        calendarDatePicker = (DatePicker) findNodeByID("calendarDatePicker");
        calendarFromHourInput = (TextField) findNodeByID("calendarFromHourInput");
        calendarFromMinuteInput = (TextField) findNodeByID("calendarFromMinuteInput");
        calendarToHourInput = (TextField) findNodeByID("calendarToHourInput");
        calendarToMinuteInput = (TextField) findNodeByID("calendarToMinuteInput");
        calendarEventDescriptionInput = (TextArea) findNodeByID("calendarEventDescriptionInput");

        calendarFromAMToggle = (ToggleButton) findNodeByID("calendarFromAMToggle");
        calendarFromPMToggle = (ToggleButton) findNodeByID("calendarFromPMToggle");

        calendarToAMToggle = (ToggleButton) findNodeByID("calendarToAMToggle");
        calendarToPMToggle = (ToggleButton) findNodeByID("calendarToPMToggle");

        calendarSaveEventButton = (Button) findNodeByID("calendarSaveEventButton");


        attachEvents();
        currentMonth = new CalendarMonth(LocalDate.now().getMonth());

        createCalendarGUI();


        // registerEvents();


        final CalendarDay today = currentMonth.getDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        if (today == null) {
            queuedTasks.add(() -> {
                final CalendarDay queuedToday = currentMonth.getDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                queuedToday.onClick();
            });
        } else {
            today.onClick();
        }




    }

    private void attachEvents() {
        calendarNextMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getNextMonth()));

        calendarPrevMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getPreviousMonth()));

        calendarNewEventButton.setOnMouseClicked(e -> {
            addEvent(selectedDay.getDay());
        });

        final ToggleGroup fromGroup = new ToggleGroup();
        calendarFromAMToggle.setToggleGroup(fromGroup);
        calendarFromPMToggle.setToggleGroup(fromGroup);

        calendarFromAMToggle.setSelected(true);


        final ToggleGroup toGroup = new ToggleGroup();
        calendarToAMToggle.setToggleGroup(toGroup);
        calendarToPMToggle.setToggleGroup(toGroup);

        calendarToAMToggle.setSelected(true);


        calendarSaveEventButton.setOnAction(e -> {
            if (selectedEvent == null) return;

            selectedEvent.setEventName(calendarEventTitleInput.getText());
            selectedEvent.setStartTime(calendarFromHourInput.getText(), calendarFromMinuteInput.getText());
            selectedEvent.setEndTime(calendarToHourInput.getText(), calendarToMinuteInput.getText());
            selectedEvent.setDescription(calendarEventDescriptionInput.getText());



        });



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

        buildingMonth = true;
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
            buildingMonth = false;
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

    public void populateEventBox(final CalendarDay day) {
        this.selectedDay = day;

        calendarDayLabel.setText(ThoughtsHelper.toCamelCase(day.getMonth().toString()) + " " + day.getDay() + ThoughtsHelper.getNumberSuffix(day.getDay()) + ", " + day.getYear());

        calendarEventBox.getChildren().clear();


        for (final DayEvent dayEvent : day.getEvents()) {
            calendarEventBox.getChildren().add(new DayEvent(dayEvent, this));

        }

    }

    public void selectEvent(final DayEvent event) {
        event.getStyleClass().add("selected-label");
        if (selectedEvent != null) selectedEvent.getStyleClass().remove("selected-label");
        selectedEvent = event;


        calendarEventTitleInput.setText(event.getEventName());
        calendarDatePicker.setValue(LocalDate.of(event.getCalendarDay().getYear(), event.getCalendarDay().getMonth(), event.getCalendarDay().getDay()));

        final LocalTime startTime = event.getStartTime();
        calendarFromHourInput.setText(startTime == null ? "" : String.valueOf(startTime.getHour()));
        calendarFromMinuteInput.setText(startTime == null ? "" : String.valueOf(startTime.getMinute()));

        final LocalTime endTime = event.getEndTime();
        calendarToHourInput.setText(endTime == null ? "" : String.valueOf(endTime.getHour()));
        calendarToMinuteInput.setText(endTime == null ? "" : String.valueOf(endTime.getMinute()));

        calendarEventDescriptionInput.setText(event.getDescription());


    }


}
