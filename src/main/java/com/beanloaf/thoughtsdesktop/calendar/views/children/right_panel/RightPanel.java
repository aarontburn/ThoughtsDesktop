package com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel;

import com.beanloaf.thoughtsdesktop.calendar.objects.CalendarMonth;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children.MonthView;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children.WeekView;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RightPanel {

    private final CalendarMain main;

    public enum Layouts {
        MONTH, WEEK
    }

    private Layouts currentLayout = Layouts.MONTH;
    private final Map<Layouts, Node> layoutMap = new HashMap<>();


    private final MonthView monthView;
    private final WeekView weekView;


    /*  Header Components  */
    private Label calendarTitleLabel, calendarNextButton, calendarPrevButton;
    private Button weekViewButton, monthViewButton, currentButton;


    public RightPanel(final CalendarMain main) {
        this.main = main;

        layoutMap.put(Layouts.MONTH, findNodeById("monthView"));
        layoutMap.put(Layouts.WEEK, findNodeById("weekView"));

        locateNodes();
        attachEvents();

        this.monthView = new MonthView(this);
        this.weekView = new WeekView(this);
    }

    public Node findNodeById(final String nodeId) {
        return main.findNodeById(nodeId);
    }

    public CalendarMain getMain() {
        return this.main;
    }

    private void locateNodes() {
        calendarTitleLabel = (Label) findNodeById("calendarTitleLabel");
        calendarNextButton = (Label) findNodeById("calendarNextButton");
        calendarPrevButton = (Label) findNodeById("calendarPrevButton");

        weekViewButton = (Button) findNodeById("weekViewButton");
        monthViewButton = (Button) findNodeById("monthViewButton");
        currentButton = (Button) findNodeById("currentButton");
    }

    private void attachEvents() {
        currentButton.setOnAction(e -> {
            final LocalDate now = LocalDate.now();

            weekView.changeWeek(now);
            monthView.changeMonth(now);

            updateHeaderText();
        });


        weekViewButton.setOnAction(e -> {
            weekView.changeWeek(getMain().getCalendarHandler().getSelectedDay().getDate());
            swapRightPanel(Layouts.WEEK);
        });
        monthViewButton.setOnAction(e -> {
            swapRightPanel(Layouts.MONTH);
        });


        calendarNextButton.setOnMouseClicked(e -> {
            if (currentLayout == Layouts.MONTH) {
                this.getMonthView().changeMonth(getMain().getCalendarHandler().getCurrentMonth().getNextMonth());
            } else if (currentLayout == RightPanel.Layouts.WEEK) {
                this.getWeekView().changeToNextWeek();
            }
        });

        calendarPrevButton.setOnMouseClicked(e -> {
            if (currentLayout == Layouts.MONTH) {
                this.getMonthView().changeMonth(getMain().getCalendarHandler().getCurrentMonth().getPreviousMonth());
            } else if (currentLayout == Layouts.WEEK) {
                this.getWeekView().changeToPrevWeek();
            }
        });
    }

    public void updateHeaderText() {
        if (currentLayout == Layouts.MONTH) {
            final CalendarMonth calendarMonth = getMain().getCalendarHandler().getCurrentMonth();
            this.setHeaderText(ThoughtsHelper.toCamelCase(calendarMonth.getMonth().toString()) + ", " + calendarMonth.getYear());


        } else if (currentLayout == Layouts.WEEK) {
            final Pair<LocalDate, LocalDate> startEndRange = weekView.getDateRange(getMain().getCalendarHandler().getSelectedDay().getDate());
            this.setHeaderText(String.format("Week (%s - %s)",
                    startEndRange.getKey().format(DateTimeFormatter.ofPattern("M/d/yyyy")),
                    startEndRange.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy"))));
        }

    }

    public void setHeaderText(final String text) {
        Platform.runLater(() -> calendarTitleLabel.setText(text));
    }

    public void swapRightPanel(final Layouts swapToLayout) {
        if (swapToLayout == null) throw new IllegalArgumentException("swapToLayout cannot be null");
        currentLayout = swapToLayout;
        for (final Layouts layout : layoutMap.keySet()) {
            layoutMap.get(layout).setVisible(false);
        }
        updateHeaderText();

        layoutMap.get(swapToLayout).setVisible(true);
    }

    public MonthView getMonthView() {
        return this.monthView;
    }

    public WeekView getWeekView() {
        return this.weekView;
    }

    public Layouts getCurrentLayout() {
        return this.currentLayout;
    }


}
