package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarDay;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarMonth;
import com.google.common.base.CaseFormat;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class CalendarView extends ThoughtsView {

    private final GridPane calendarFrame; // (7 x 5)

    private final Map<Pair<Month, Integer>, CalendarMonth> activeMonths = new HashMap<>();

    private final List<Runnable> queuedTasks = Collections.synchronizedList(new ArrayList<>());

    private CalendarMonth currentMonth;


    private final Label calendarMonthYearLabel;
    private final Label calendarNextMonthButton, calendarPrevMonthButton;




    public CalendarView(final MainApplication main) {
        super(main);


        calendarFrame = (GridPane) findNodeByID("calendarFrame");

        calendarMonthYearLabel = (Label) findNodeByID("calendarMonthYearLabel");

        calendarNextMonthButton = (Label) findNodeByID("calendarNextMonthButton");
        calendarPrevMonthButton = (Label) findNodeByID("calendarPrevMonthButton");



        attachEvents();
        currentMonth = new CalendarMonth(LocalDate.now().getMonth());

        createCalendarGUI();


        int testDay = 0;
        for (int i = 1; i < 500; i++) {
            testDay++;

            if (testDay > currentMonth.getMonthLength()) testDay = 1;
            addEvent(testDay, "test " + i);

        }


    }

    private void attachEvents() {
        calendarNextMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getNextMonth()));

        calendarPrevMonthButton.setOnMouseClicked(e -> changeMonth(currentMonth.getPreviousMonth()));
    }
    
    private void changeMonth(final CalendarMonth month) {
        if (currentMonth.getNumDaysWithEvents() > 0) {
            Logger.log("saving " + currentMonth.getMonth() + " " + currentMonth.getYear());

            activeMonths.put(new Pair<>(currentMonth.getMonth(), currentMonth.getYear()), currentMonth);
        }

        final CalendarMonth newMonth = activeMonths.get(new Pair<>(month.getMonth(), month.getYear()));
        if (newMonth != null) {
            Logger.log(activeMonths);

        }

        currentMonth = newMonth == null ? month : newMonth;
        createCalendarGUI();
        
        
    }


    private void createCalendarGUI() {
        Platform.runLater(() -> {
            for (int i = calendarFrame.getChildren().size() - 1; i > -1; i--) {
                calendarFrame.getChildren().remove(i);
            }

            this.calendarMonthYearLabel.setText(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, currentMonth.getMonth().toString()) + ", " + currentMonth.getYear());

            final int monthLength = currentMonth.getMonthLength();

            int row = 0;
            int col = 0;
            int day = 0;
            for (int i = 0; i < calendarFrame.getColumnCount() * calendarFrame.getRowCount(); i++) {


                if ((row == 0 && col < currentMonth.getStartingDayOfWeek()) || day >= monthLength) {
                    final CalendarDay calendarDay = new CalendarDay(null, null);
                    calendarFrame.add(calendarDay, col, row);
                } else {
                    day++;


                    CalendarDay calendarDay = currentMonth.getDay(day);
                    if (calendarDay == null) {
                        calendarDay = new CalendarDay(currentMonth.getMonth(), day);
                        currentMonth.addDay(day, calendarDay);
                    }

                    try {
                        calendarFrame.add(calendarDay, col, row);
                    } catch (Exception e) {
                        Logger.log(calendarDay);

                    }
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


    private void addEvent(final int dayNum, final String eventName) {


        Platform.runLater(() -> {
            if (dayNum < 0 || dayNum > currentMonth.getMonthLength())
                throw new IllegalArgumentException("Day out of bounds.");

            final CalendarDay day = currentMonth.getDay(dayNum);

            if (day == null) {
                queuedTasks.add(() -> addEvent(dayNum, eventName));
                return;
            }

            day.addEvent(eventName);
        });

    }


}
