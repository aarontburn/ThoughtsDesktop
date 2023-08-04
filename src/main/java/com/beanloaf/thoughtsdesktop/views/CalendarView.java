package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.objects.calendar.CalendarDay;
import javafx.scene.layout.GridPane;

public class CalendarView extends ThoughtsView {

    private final GridPane calendarFrame; // (7 x 5)

    public CalendarView(final MainApplication main) {
        super(main);


        calendarFrame = (GridPane) findNodeByID("calendarFrame");


        createCalendarGUI();

    }


    private void createCalendarGUI() {
        int day = 1;


        CalendarDay firstDay = null;



        for (int row = 0; row < calendarFrame.getRowCount(); row++) {
            for (int col = 0; col < calendarFrame.getColumnCount(); col++) {
                final CalendarDay calendarDay = new CalendarDay(day);

                if (day == 1) firstDay = calendarDay;

                calendarFrame.add(calendarDay, col, row);
                day++;

            }
        }


        firstDay.addEvent("test");
        firstDay.addEvent("test");
        firstDay.addEvent("test");
//        firstDay.addEvent("test");
//        firstDay.addEvent("test");
//        firstDay.addEvent("test");
//        firstDay.addEvent("test");



    }


}
