package com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.RightPanel;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.application.Platform;
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

    private final RightPanel rightPanel;


    public static int START_HOUR = DEFAULT_START_HOUR;
    public static int END_HOUR = DEFAULT_END_HOUR;

    private LocalDate startDate;
    private LocalDate endDate;

    public GridPane weekGrid, allDayEventGrid;
    private AnchorPane weekPane;
    private ScrollPane scrollPane;


    private final List<Event> weekEventList = new ArrayList<>();
    private final Map<Weekday, VBox> allDayEventMap = new ConcurrentHashMap<>();

    public WeekView(final RightPanel rightPanel) {
        this.rightPanel = rightPanel;
        locateNodes();
        createGUI();
    }

    public RightPanel getParent() {
        return this.rightPanel;
    }


    public Pair<LocalDate, LocalDate> getDateRange(final LocalDate date) {
        final LocalDate start = date.minusDays(date.getDayOfWeek().getValue() == 7 ? 0 : date.getDayOfWeek().getValue());
        final LocalDate end = start.plusDays(6);

        return new Pair<>(start, end);
    }


    private Node findNodeById(final String nodeId) {
        return rightPanel.findNodeById(nodeId);
    }

    private void locateNodes() {
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
        Platform.runLater(() -> {
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
        });


    }

    public void createGrid() {

        if (weekGrid != null) {
            weekPane.getChildren().remove(weekGrid);
        }


        final LocalTime now = LocalTime.now();

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
            rightPanel.getMonthView().selectDay(rightPanel.getMain().getCalendarHandler().getDay(startDate.plusDays(cellNum - 1)), true);
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

                if (time.getHour() == now.getHour()) {
                    // TODO: do something to make the hour stand out
                }

                time = time.plusHours(1);
            }
            label.setStyle("-fx-font-size: 14");


            weekGrid.add(label, 0, row);
        }

        scrollPane.setVvalue(0);

    }

    public void refreshWeek() {
        changeWeek(startDate);

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

        if (rightPanel.getCurrentLayout() == RightPanel.Layouts.WEEK) {
            rightPanel.setHeaderText(String.format("Week (%s - %s)",
                    startEndRange.getKey().format(DateTimeFormatter.ofPattern("M/d/yyyy")),
                    startEndRange.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy"))));
        }


        LocalDate d = startDate;
        for (int i = 0; i < daysBetween; i++) {
            final CalendarDay day = rightPanel.getMain().getCalendarHandler().getDay(d);

            for (final DayEvent dayEvent : day.getEvents()) {
                final Weekday weekday = Weekday.getWeekdayByDayOfWeek(day.getDate().getDayOfWeek().getValue());

                /*
                TODO: instead of creating new event, all day events could all share the same reference of the same event. This may
                    improve memory and speed.
                */

                final Event event = new Event(dayEvent.getEventTitle())
                        .setStartTime(dayEvent.getStartTime())
                        .setEndTime(dayEvent.getEndTime())
                        .setDescription(dayEvent.getDescription())
                        .setWeekday(weekday)
                        .setStartDate(dayEvent.getDate())
                        .setLinkedDayEvent(dayEvent)
                        .setCompleted(dayEvent.isCompleted());

                this.weekEventList.add(event);
                if (dayEvent.getStartTime() == null) {
                    allDayEventMap.get(weekday).getChildren().add(new DayEvent(dayEvent, rightPanel.getMain()));
                }

            }
            d = d.plusDays(1);
        }

        adjustBounds();
        createGrid();

        for (final Event e : weekEventList) {
            if (e.getStartTime() == null) continue;
            final WeekBlock block = new WeekBlock(this, e);
            weekGrid.add(block, block.getWeekday().getDayOfWeek() + 1, block.getStartIndex(), 1, block.getSpan());
        }


    }

    private void adjustBounds() {
        LocalTime minTime = null;
        LocalTime maxTime = null;

        for (final Event event : weekEventList) {
            final LocalTime startTime = event.getStartTime();

            if (startTime == null) {
                continue;
            }

            LocalTime endTime = event.getEndTime();
            if (endTime == null) {
                endTime = startTime.plusHours(1);
            }


            if (minTime == null) {
                minTime = startTime;
            } else {
                if (startTime.isBefore(minTime)) {
                    minTime = startTime;
                }

                if (endTime.isBefore(minTime)) {
                    minTime = endTime;
                }
            }

            if (maxTime == null) {
                maxTime = endTime;
            } else {
                if (endTime.isAfter(maxTime)) {
                    maxTime = endTime;
                }

                if (startTime.isAfter(maxTime)) {
                    maxTime = startTime;
                }
            }

        }

        if (minTime != null) START_HOUR = minTime.getHour() - 2;
        if (maxTime != null) END_HOUR = maxTime.getHour() + 2;

        if (START_HOUR < 0) START_HOUR = 0;
        if (END_HOUR > 24) END_HOUR = 24;
    }
}
