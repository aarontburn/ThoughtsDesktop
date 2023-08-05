package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarDay;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarMonth;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CalendarView extends ThoughtsView {

    private final GridPane calendarFrame; // (7 x 5)

    private Map<Integer, CalendarDay> monthMap = new HashMap<>();

    private CalendarMonth currentMonth;


    public CalendarView(final MainApplication main) {
        super(main);


        calendarFrame = (GridPane) findNodeByID("calendarFrame");


        currentMonth = new CalendarMonth(LocalDate.now().getMonth());

        createCalendarGUI();


//        int testDay = 0;
//        for (int i = 1; i < 270; i++) {
//            testDay++;
//
//            if (testDay > currentMonth.getMonthLength()) testDay = 1;
//            addEvent(testDay, "test " + i);
//
//        }


    }


    private void createCalendarGUI() {

        final int monthLength = currentMonth.getMonthLength();

        int row = 0;
        int col = 0;
        int day = 0;
        for (int i = 0; i < calendarFrame.getColumnCount() * calendarFrame.getRowCount(); i++) {



            if ((row == 0 && col < currentMonth.getStartingDayOfWeek()) || day > monthLength) {
                final CalendarDay calendarDay = new CalendarDay(null);
                calendarFrame.add(calendarDay, col, row);
            } else {
                day++;

                final CalendarDay calendarDay = new CalendarDay(day);
                monthMap.put(day, calendarDay);

                calendarFrame.add(calendarDay, col, row);
            }


            col++;

            if (col % 7 == 0) {
                row++;
                col = 0;
            }

        }


//        build: {
//            for (int row = 0; row < calendarFrame.getRowCount(); row++) {
//                for (int col = 0; col < calendarFrame.getColumnCount(); col++) {
//                    day++;
//
//                    if (day == 1) col = currentMonth.getStartingDayOfWeek();
//
//                    final CalendarDay calendarDay = new CalendarDay(day);
//                    monthMap.put(day, calendarDay);
//                    calendarFrame.add(calendarDay, col, row);
//
//                    if (day == monthLength) {
//                        break build;
//                    }
//
//                }
//            }
//        }


    }


    private void addEvent(final int dayNum, final String eventName) {
        if (dayNum < 0 || dayNum > 35)
            throw new IllegalArgumentException("Day cannot exceed month max"); // TODO: change upper bound to final day of month

        final CalendarDay day = monthMap.get(dayNum);

        if (day == null) throw new RuntimeException("Returned day is null. Somehow. Passed in day of: " + dayNum);

        day.addEvent(eventName);

    }


}
