package com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children.MonthView;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children.WeekView;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.time.LocalDate;
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
    private Button weekViewButton, monthViewButton;


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
    }

    private void attachEvents() {
        weekViewButton.setOnAction(e -> {
            swapRightPanel(Layouts.WEEK);
            final Pair<LocalDate, LocalDate> startEndRange = weekView.getDateRange(getMain().getCalendarHandler().getSelectedDay().getDate());

            this.setHeaderText(String.format("Week (%s - %s)",
                    startEndRange.getKey().format(DateTimeFormatter.ofPattern("M/d/yyyy")),
                    startEndRange.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy"))));
        });
        monthViewButton.setOnAction(e -> {
            swapRightPanel(Layouts.MONTH);

            this.setHeaderText(ThoughtsHelper.toCamelCase(getMain().getCalendarHandler().getCurrentMonth().getMonth().toString())
                    + ", " + getMain().getCalendarHandler().getCurrentMonth().getYear());
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

    public void setHeaderText(final String text) {
        Platform.runLater(() -> calendarTitleLabel.setText(text));
    }

    public void swapRightPanel(final Layouts swapToLayout) {
        if (swapToLayout == null) throw new IllegalArgumentException("swapToLayout cannot be null");

        for (final Layouts layout : layoutMap.keySet()) {
            layoutMap.get(layout).setVisible(false);
        }

        layoutMap.get(swapToLayout).setVisible(true);
        currentLayout = swapToLayout;
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
