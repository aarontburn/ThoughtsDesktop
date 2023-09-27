package com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleBoxItem;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleEvent;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.calendar.views.children.left_panel.LeftPanel;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.RightPanel;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MonthView {

    private final RightPanel rightPanel;
    private final CalendarMain main;


    public final List<Runnable> queuedTasks = Collections.synchronizedList(new ArrayList<>());

    private GridPane calendarFrame; // (7 x 5)


    public MonthView(final RightPanel rightPanel) {
        this.rightPanel = rightPanel;
        this.main = rightPanel.getMain();

        locateNodes();
        attachEvents();


        changeMonth(this.main.getCalendarHandler().getCurrentMonth());
        main.getLeftPanel().swapLeftPanel(LeftPanel.LeftLayouts.EVENTS);

    }

    private Node findNodeById(final String nodeId) {
        return rightPanel.findNodeById(nodeId);
    }

    private void locateNodes() {
        calendarFrame = (GridPane) findNodeById("calendarFrame");

    }

    public void startupMonthView() {
        this.main.getCalendarHandler().getDay(LocalDate.now()).onClick();
    }

    public void onOpen() {
        calendarFrame.requestFocus();
    }

    private void attachEvents() {

    }


    public void changeMonth(final LocalDate date) {
        changeMonth(main.getCalendarHandler().getMonth(date.getMonth(), date.getYear()));
    }

    public void changeMonth(final CalendarMonth month) {
        this.main.getCalendarHandler().removeInactiveMonths();
        this.main.getCalendarHandler().setCurrentMonth(month.getMonth(), month.getYear());
        createCalendarGUI();
    }


    private void createCalendarGUI() {
        final CalendarMonth currentMonth = this.main.getCalendarHandler().getCurrentMonth();

        if (rightPanel.getCurrentLayout() == RightPanel.RightLayouts.MONTH) {
            rightPanel.setHeaderText(ThoughtsHelper.toCamelCase(currentMonth.getMonth().toString()) + " " + currentMonth.getYear());
        }

        final int monthLength = currentMonth.getMonthLength();

        final CalendarMonth prevMonth = this.main.getCalendarHandler().getMonth(currentMonth.getPreviousMonth().getMonth(), currentMonth.getPreviousMonth().getYear());
        final CalendarMonth nextMonth = this.main.getCalendarHandler().getMonth(currentMonth.getNextMonth().getMonth(), currentMonth.getNextMonth().getYear());

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
                        calendarDay = new CalendarDay(prevMonth.getYear(), prevMonth.getMonth(), prevDays, main);
                    }
                    calendarDay.checkIsToday();
                    prevDays++;
                    calendarFrame.add(calendarDay, col, row);


                } else if (day >= monthLength) { // after the last day of the month
                    CalendarDay calendarDay = nextMonth.getDay(overflowDays);
                    if (calendarDay == null) {
                        calendarDay = new CalendarDay(nextMonth.getYear(), nextMonth.getMonth(), overflowDays, main);
                    }
                    calendarDay.checkIsToday();

                    overflowDays++;
                    calendarFrame.add(calendarDay, col, row);


                } else { // normal month
                    day++;

                    CalendarDay calendarDay = currentMonth.getDay(day);
                    if (calendarDay == null) {
                        calendarDay = new CalendarDay(currentMonth.getYear(), currentMonth.getMonth(), day, main);
                        currentMonth.addDay(day, calendarDay);
                    }
                    calendarDay.checkIsToday();
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

    public void hideSchedule(final ScheduleData data, final boolean isHidden) {
        if (isHidden) {
            if (data.getStartDate() != null && data.getEndDate() != null) {
                final long daysBetween = ChronoUnit.DAYS.between(data.getStartDate(), data.getEndDate()) + 1;

                LocalDate date = data.getStartDate();
                for (int i = 0; i < daysBetween; i++) {
                    final CalendarDay day = this.main.getCalendarHandler().getDay(LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()));
                    for (final DayEvent event : day.getEvents()) {
                        if (data.getId() != null && data.getId().equals(event.getEventID())) {
                            Platform.runLater(() -> day.removeEvent(event));
                        }

                    }
                    date = date.plusDays(1);
                }
            }
        } else {
            addScheduleToCalendarDay(data);
        }

        if (main.getRightPanel().getCurrentLayout() == RightPanel.RightLayouts.WEEK) {
            Platform.runLater(() -> main.getRightPanel().getWeekView().refreshWeek());
        }

    }


    public void updateSchedule(final ScheduleData data, final LocalDate oldStartDate, final LocalDate oldEndDate) {
        if (oldStartDate != null && oldEndDate != null) {
            final long daysBetween = ChronoUnit.DAYS.between(oldStartDate, oldEndDate) + 1;

            LocalDate date = oldStartDate;
            for (int i = 0; i < daysBetween; i++) {
                final CalendarDay day = this.main.getCalendarHandler().getDay(LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()));
                for (final DayEvent event : day.getEvents()) {
                    if (data.getId() != null && data.getId().equals(event.getEventID())) {
                        Platform.runLater(() -> day.removeEvent(event));
                    }

                }
                date = date.plusDays(1);

            }
        }


        boolean boxExists = false;
        for (final Node node : main.getLeftPanel().getScheduleBoxChildren()) {
            if (node.getClass() != ScheduleBoxItem.class) continue;
            final ScheduleBoxItem scheduleBoxItem = (ScheduleBoxItem) node;
            if (scheduleBoxItem.getScheduleId().equals(data.getId())) {
                boxExists = true;
                scheduleBoxItem.setHidden(false);
                break;
            }

        }

        if (!boxExists) main.getLeftPanel().addScheduleBoxItem(new ScheduleBoxItem(this.main, data));


        addScheduleToCalendarDay(data);

    }


    public void deleteSchedule(final ScheduleBoxItem scheduleBoxItem) {
        hideSchedule(scheduleBoxItem.getScheduleData(), true);
        main.getLeftPanel().deleteSchedule(scheduleBoxItem);
        new File(TC.Directories.CALENDAR_SCHEDULES_PATH, scheduleBoxItem.getScheduleId() + ".json").delete();

    }

    public void addScheduleToCalendarDay(final ScheduleData schedule) {
        LocalDate startDate = schedule.getStartDate();
        final LocalDate endDate = schedule.getEndDate();


        if (startDate == null || endDate == null) {
            Logger.log("Needs to have a startdate and enddate. This needs to be changed to not have this issue.");
            return;
        }

        final long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        for (int i = 0; i < daysBetween; i++) {
            for (final ScheduleEvent scheduleEvent : schedule.getScheduleEventList()) {
                boolean isCorrectDay = false;

                for (final Weekday weekday : scheduleEvent.getWeekdays()) {
                    if (weekday.getDayOfWeek() == startDate.getDayOfWeek().getValue() || (weekday.getDayOfWeek() == 0 && startDate.getDayOfWeek().getValue() == 7)) {
                        isCorrectDay = true;
                        break;
                    }
                }

                if (!isCorrectDay) {
                    continue;
                }


                final DayEvent dayEvent = new DayEvent(startDate, scheduleEvent.getScheduleEventName(), schedule.getId(), main, TypedEvent.Types.SCHEDULE, schedule.getScheduleName());
                dayEvent.setDescription(scheduleEvent.getDescription());
                dayEvent.setStartTime(scheduleEvent.getStartTime());
                dayEvent.setEndTime(scheduleEvent.getEndTime());
                dayEvent.setDisplayColor(scheduleEvent.getDisplayColor());

                final LocalDate finalStartDate = startDate;
                Platform.runLater(() -> addEventToCalendarDay(finalStartDate, dayEvent));

            }
            startDate = startDate.plusDays(1);
        }


    }

    public void addCanvasEventsToCalendar(final Map<String, CanvasClass> newCanvasEvents) {
        for (final DayEvent storedCanvasEvent : main.getCalendarHandler().getCanvasEvents()) {
            main.getCalendarHandler().getDay(storedCanvasEvent.getDate()).removeEvent(storedCanvasEvent);
        }

        main.getCalendarHandler().getCanvasEvents().clear();

        for (final CanvasClass canvasClass : newCanvasEvents.values()) {
            for (final BasicEvent event : canvasClass.getEvents()) {
                final DayEvent e = new DayEvent(event, main);

                addEventToCalendarDay(event.getStartDate(), e);
                main.getCalendarHandler().addCanvasEvent(e);
            }

        }
    }


    public DayEvent addEventToCalendarDay(final LocalDate date, final DayEvent event) {
        this.main.getCalendarHandler().getMonth(date).getDay(date.getDayOfMonth()).addEvent(event);
        return event;
    }

    public DayEvent addNewEventToCalendarDay(final LocalDate date) {
        final DayEvent event = new DayEvent(date, "New Event", main, TypedEvent.Types.DAY);
        event.setDescription("");
        event.setStartTime(null);
        this.main.getJsonHandler().addEventToJson(event);
        this.main.getCalendarHandler().getDay(date).addEvent(event);

        return event;

    }


    public void selectDay(final CalendarDay day, final boolean hideEventFields) {
        if (this.main.getCalendarHandler().getSelectedDay() != null) {
            this.main.getCalendarHandler().getSelectedDay().setStyle("");
        }
        this.main.getCalendarHandler().setSelectedDay(day);
        day.setStyle("-fx-border-color: white; -fx-border-radius: 5;");


        Platform.runLater(() -> {
            main.getLeftPanel().setEventFieldsVisibility(!hideEventFields);
            main.getLeftPanel().clearEventBox();
            main.getLeftPanel().setDateLabel(day.getDate());


            final List<DayEvent> cloneList = new ArrayList<>();
            for (final DayEvent dayEvent : day.getEvents()) {
                cloneList.add(new DayEvent(dayEvent, main));
            }

            main.getLeftPanel().addEventToEventBox(cloneList.toArray(new DayEvent[0]));
        });


    }

    public void selectDay(final LocalDate date, final boolean hideEventFields) {
        final CalendarMonth month = this.main.getCalendarHandler().getMonth(date.getMonth(), date.getYear());
        selectDay(month.getDay(date.getDayOfMonth()), hideEventFields);
    }


    public void selectEvent(final DayEvent event, final boolean editable) {
        selectEvent(event.getEvent(), editable);

        event.getStyleClass().add("selected-day-event");
        if (main.getCalendarHandler().getSelectedEvent() != null) {
            main.getCalendarHandler().getSelectedEvent().getStyleClass().remove("selected-day-event");
        }
        main.getCalendarHandler().setSelectedEvent(event);
    }

    public void selectEvent(final BasicEvent event, final boolean editable) {
        selectDay(event.getStartDate(), false);

        selectDay(event.getStartDate(), false);
        main.getLeftPanel().swapLeftPanel(LeftPanel.LeftLayouts.EVENTS);
        main.getLeftPanel().onSelectEvent(event, editable);


    }


    public void saveEvent(final DayEvent event, final BasicEvent inputFields) {
        event.setEventTitle(inputFields.getTitle());
        event.setDescription(inputFields.getDescription());

        final LocalDate oldDate = event.getDate();
        if (oldDate != null && !inputFields.getStartDate().isEqual(oldDate)) {
            deleteEvent(event, oldDate);
            event.setStartDate(inputFields.getStartDate());
            selectEvent(addEventToCalendarDay(event.getDate(), event), false);
        } else {
            event.setStartDate(inputFields.getStartDate());
        }

        event.setStartTime(inputFields.getStartTime());
        if (inputFields.getStartTime() != null) {
            try {
                event.setEndTime(inputFields.getStartTime().isBefore(inputFields.getEndTime()) ? inputFields.getEndTime() : null);
            } catch (Exception e) {
                event.setEndTime(null);
            }
        } else {
            event.setEndTime(inputFields.getEndTime());
        }

        main.getLeftPanel().setFinalStartEndTimeLabel(event);
        main.getLeftPanel().toggleSmallEventFields(false);

        if (event.getEventType() == TypedEvent.Types.CANVAS) {
            this.main.getCanvasICalHandler().cacheCanvasEventsToJson();
        } else {
            this.main.getJsonHandler().addEventToJson(event);
        }

        if (rightPanel.getCurrentLayout() == RightPanel.RightLayouts.WEEK) {
            rightPanel.getWeekView().refreshWeek();
        }

        main.getCalendarHandler().getDay(event.getDate()).sortEvents();
        main.getLeftPanel().sortEventBox();


    }

    public void deleteEvent(final DayEvent event) {
        deleteEvent(event, event.getDate());
    }

    public void deleteEvent(final DayEvent event, final LocalDate oldDate) {
        this.main.getJsonHandler().removeEventFromJson(main.getCalendarHandler().getSelectedEvent(), oldDate);

        final Month month = event.getDate().getMonth();
        final int year = event.getDate().getYear();
        final int day = event.getDate().getDayOfMonth();

        final CalendarMonth calendarMonth = this.main.getCalendarHandler().getMonth(month, year);

        calendarMonth.getDay(day).removeEvent(event);
        selectDay(this.main.getCalendarHandler().getSelectedDay(), true);

        if (rightPanel.getCurrentLayout() == RightPanel.RightLayouts.WEEK) {
            rightPanel.getWeekView().refreshWeek();
        }


    }


}
