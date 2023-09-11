package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.calendar.handlers.Calendar;
import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeekView {
    private static final int DEFAULT_START_HOUR = 6;
    private static final int DEFAULT_END_HOUR = 24;

    private final CalendarView view;


    public static int START_HOUR = DEFAULT_START_HOUR;
    public static int END_HOUR = DEFAULT_END_HOUR;

    private LocalDate startDate;
    private LocalDate endDate;

    public GridPane weekGrid, allDayEventGrid;
    private AnchorPane weekPane;
    private ScrollPane scrollPane;

    private final Calendar calendar;

    private final List<Event> weekEventList = new ArrayList<>();
    private final Map<Weekday, VBox> allDayEventMap = new ConcurrentHashMap<>();

    public WeekView(final CalendarView view) {
        this.view = view;
        this.calendar = view.calendar;

        locateNodes();
        createGUI();

        changeWeek(calendar.getSelectedDay().getDate());
    }

    public Pair<LocalDate, LocalDate> getDateRange(final LocalDate date) {
        final LocalDate start = date.minusDays(date.getDayOfWeek().getValue());
        final LocalDate end = start.plusDays(6);

        return new Pair<>(start, end);

    }


    private Node findNodeById(final String nodeId) {
        return view.findNodeById(nodeId);
    }

    protected void locateNodes() {
        weekPane = (AnchorPane) findNodeById("weekView");

        allDayEventGrid = (GridPane) findNodeById("allDayEventGrid");
    }


    public void changeToNextWeek() {
        changeWeek(endDate.plusDays(2));
    }

    public void changeToPrevWeek() {
        changeWeek(startDate.minusDays(2));
    }

    protected void createGUI() {
        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.skinProperty().addListener((observableValue, skin, t1) -> {
            final StackPane stackPane = (StackPane) scrollPane.lookup("ScrollPane .viewport");
            stackPane.setCache(false);
        });
        weekPane.getChildren().add(ThoughtsHelper.setAnchor(scrollPane, 114, 200, 16, 16));

        createGrid();


        for (int i = 0; i < 7; i++) {
            final ScrollPane pane = new ScrollPane();
            pane.getStyleClass().add("edge-to-edge");
            pane.fitToWidthProperty().set(true);
            pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pane.skinProperty().addListener((observableValue, skin, t1) -> {
                final StackPane stackPane = (StackPane) pane.lookup("ScrollPane .viewport");
                stackPane.setCache(false);
            });
            allDayEventGrid.add(pane, i + 1, 0);

            final VBox eventContainer = new VBox();
            eventContainer.getStyleClass().add("events");
            eventContainer.setMinHeight(0);
            eventContainer.setSpacing(2);

            pane.setContent(eventContainer);

            allDayEventMap.put(Weekday.getWeekdayByDayOfWeek(i), eventContainer);
        }


    }

    public void createGrid() {
        if (weekGrid != null) {
            weekPane.getChildren().remove(weekGrid);
        }


        weekGrid = new GridPane();
        weekGrid.setGridLinesVisible(true);

        scrollPane.setContent(weekGrid);

        final int columnCount = 8;
        weekGrid.setOnMouseClicked(e -> {
            final double cellSize = weekGrid.getWidth() / columnCount;
            final double x = e.getPickResult().getIntersectedPoint().getX();
            int cellNum = (int) (x / cellSize);
            cellNum = Math.max(0, Math.min(columnCount - 1, cellNum));
            if (cellNum == 0) return;
            view.selectDay(calendar.getDay(startDate.plusDays(cellNum - 1)));
        });


        for (int column = 0; column < columnCount; column++) {
            final ColumnConstraints con = new ColumnConstraints();
            con.setPercentWidth(100.0 / columnCount);
            con.setHalignment(HPos.CENTER);
            weekGrid.getColumnConstraints().add(con);
            weekGrid.add(new Text(), column, 0);
        }

        LocalTime time = LocalTime.of(START_HOUR, 0);

        final int rowCount = (END_HOUR - START_HOUR) * 2;
        for (int row = 0; row < rowCount; row++) {
            final RowConstraints con = new RowConstraints();
            con.setPercentHeight(100.0 / rowCount);
            con.setMinHeight(30);
            con.setValignment(VPos.TOP);
            weekGrid.getRowConstraints().add(con);


            final Label label = new Label();
            if (row % 2 == 0) {
                label.setText(time.format(DateTimeFormatter.ofPattern("h:mm a")));
                time = time.plusHours(1);
            }
            label.setStyle("-fx-font-size: 14");


            weekGrid.add(label, 0, row);
        }

    }

    public void changeWeek(final LocalDate date) {
        START_HOUR = DEFAULT_START_HOUR;
        END_HOUR = DEFAULT_END_HOUR;


        weekEventList.clear();
        for (final Weekday weekday : allDayEventMap.keySet()) {
            allDayEventMap.get(weekday).getChildren().clear();
        }


        final Pair<LocalDate, LocalDate> startEndRange = getDateRange(date);

        this.startDate = startEndRange.getKey();
        this.endDate = startEndRange.getValue();

        final long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        view.header.setTitleText(String.format("Week (%s - %s)", startDate.format(DateTimeFormatter.ofPattern("M/d/yyyy")), endDate.format(DateTimeFormatter.ofPattern("M/d/yyyy"))));
        createGrid();


        LocalDate d = startDate;
        for (int i = 0; i < daysBetween; i++) {
            final CalendarDay day = calendar.getDay(d);

            for (final DayEvent dayEvent : day.getEvents()) {
                final Weekday weekday = Weekday.getWeekdayByDayOfWeek(day.getDate().getDayOfWeek().getValue());

                final Event event = new Event(dayEvent.getEventTitle())
                        .setStartTime(dayEvent.getStartTime())
                        .setEndTime(dayEvent.getEndTime())
                        .setDescription(dayEvent.getDescription())
                        .setWeekday(weekday)
                        .setStartDate(dayEvent.getDate());

                if (dayEvent.getStartTime() == null) {
                    allDayEventMap.get(weekday).getChildren().add(new DayEvent(dayEvent, view));
                    this.weekEventList.add(event);
                    continue;
                }

                addEventToDay(event);


            }
            d = d.plusDays(1);
        }

    }

    public void addEventToDay(final Event event) {
        this.weekEventList.add(event);

        final WeekBlock weekBlock = new WeekBlock(view, event);

        if (adjustBounds()) {
            createGrid();
            for (final Event e : weekEventList) {
                if (e.getStartTime() == null) continue;
                final WeekBlock block = new WeekBlock(view, e);
                weekGrid.add(block, block.getWeekday().getDayOfWeek() + 1, block.getStartIndex(), 1, block.getSpan());
            }
        } else {
            final int columnIndex = weekBlock.getWeekday().getDayOfWeek() + 1;
            final int rowIndex = weekBlock.getStartIndex();
            final int columnSpan = 1; // this shouldn't change
            final int rowSpan = weekBlock.getSpan();

            weekGrid.add(weekBlock, columnIndex, rowIndex, columnSpan, rowSpan);

        }

    }

    private boolean adjustBounds() {
        final int oldStartHour = START_HOUR;
        final int oldEndHour = END_HOUR;

        LocalTime minTime = null;
        LocalTime maxTime = null;

        for (final Event e : weekEventList) {
            if (e.getStartTime() == null) continue;

            final WeekBlock block = new WeekBlock(view, e);

            if (minTime == null) {
                minTime = block.getStartTime();
            } else {
                if (block.getStartTime().isBefore(minTime)) minTime = block.getStartTime();
                if (block.getEndTime().isBefore(minTime)) minTime = block.getEndTime();
            }

            if (maxTime == null) {
                maxTime = block.getEndTime();
            } else {
                if (block.getEndTime().isAfter(maxTime)) maxTime = block.getEndTime();
                if (block.getStartTime().isAfter(maxTime)) maxTime = block.getStartTime();
            }

        }

        if (minTime != null) START_HOUR = minTime.getHour() - 1;
        if (maxTime != null) END_HOUR = maxTime.getHour() + 1;

        if (START_HOUR < 0) START_HOUR = 0;
        if (END_HOUR > 24) END_HOUR = 24;


        return oldStartHour != START_HOUR || oldEndHour != END_HOUR;

    }
}
