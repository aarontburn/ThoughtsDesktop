package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarDay;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

public class CalendarView extends ThoughtsView {

    private final GridPane calendarFrame; // (7 x 5)

    private Map<Integer, CalendarDay> monthMap = new HashMap<>();


    public CalendarView(final MainApplication main) {
        super(main);


        calendarFrame = (GridPane) findNodeByID("calendarFrame");


        createCalendarGUI();


        int testDay = 0;
        for (int i = 1; i < 500; i++) {
            testDay++;

            if (testDay > 35) testDay = 1;
            addEvent(testDay, "test " + i);


        }



    }


    private void createCalendarGUI() {
        int day = 1;




        for (int row = 0; row < calendarFrame.getRowCount(); row++) {
            for (int col = 0; col < calendarFrame.getColumnCount(); col++) {
                final CalendarDay calendarDay = new CalendarDay(day);
                monthMap.put(day, calendarDay);
                calendarFrame.add(calendarDay, col, row);
                day++;

            }
        }


    }


    private void addEvent(final int dayNum, final String eventName) {
        if (dayNum < 0 || dayNum > 35) throw new IllegalArgumentException("Day cannot exceed month max"); // TODO: change upper bound to final day of month

        final CalendarDay day = monthMap.get(dayNum);

        if (day == null) throw new RuntimeException("Returned day is null. Somehow");

        day.addEvent(eventName);

    }


}
