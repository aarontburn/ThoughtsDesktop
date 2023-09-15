package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarRightHeader {

    private final MonthView view;


    public MonthView.RightLayouts currentLayout = MonthView.RightLayouts.MONTH;


    /*  Components  */
    private Label calendarTitleLabel, calendarNextButton, calendarPrevButton;
    private Button weekViewButton, monthViewButton;


    public CalendarRightHeader(final MonthView view) {
        this.view = view;

        locateNodes();
        attachEvents();
    }

    private Node findNodeById(final String nodeId) {
        return view.findNodeById(nodeId);
    }

    private void locateNodes() {
        calendarTitleLabel = (Label) findNodeById("calendarTitleLabel");
        calendarNextButton = (Label) findNodeById("calendarNextButton");
        calendarPrevButton = (Label) findNodeById("calendarPrevButton");

        weekViewButton = (Button) findNodeById("weekViewButton");
        monthViewButton = (Button) findNodeById("monthViewButton");
    }

    private void attachEvents() {
        weekViewButton.setOnAction(e -> {
            view.swapRightPanel(MonthView.RightLayouts.WEEK);
            if (currentLayout != MonthView.RightLayouts.WEEK) view.weekView.changeWeek(view.calendar.getSelectedDay().getDate());
            currentLayout = MonthView.RightLayouts.WEEK;

            final Pair<LocalDate, LocalDate> startEndRange = view.weekView.getDateRange(view.calendar.getSelectedDay().getDate());

            setTitleText(String.format("Week (%s - %s)",
                    startEndRange.getKey().format(DateTimeFormatter.ofPattern("M/d/yyyy")), startEndRange.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy"))));
        });
        monthViewButton.setOnAction(e -> {
            view.swapRightPanel(MonthView.RightLayouts.MONTH);
            currentLayout = MonthView.RightLayouts.MONTH;

            setTitleText(ThoughtsHelper.toCamelCase(view.calendar.getCurrentMonth().getMonth().toString()) + ", " + view.calendar.getCurrentMonth().getYear());
        });


        calendarNextButton.setOnMouseClicked(e -> {
            if (currentLayout == MonthView.RightLayouts.MONTH) {
                view.changeMonth(view.calendar.getCurrentMonth().getNextMonth());
            } else if (currentLayout == MonthView.RightLayouts.WEEK) {
                view.weekView.changeToNextWeek();
            }
        });

        calendarPrevButton.setOnMouseClicked(e -> {
            if (currentLayout == MonthView.RightLayouts.MONTH) {
                view.changeMonth(view.calendar.getCurrentMonth().getPreviousMonth());
            } else if (currentLayout == MonthView.RightLayouts.WEEK) {
                view.weekView.changeToPrevWeek();
            }
        });

    }

    public void setTitleText(final String text) {
        calendarTitleLabel.setText(text);
    }

}
