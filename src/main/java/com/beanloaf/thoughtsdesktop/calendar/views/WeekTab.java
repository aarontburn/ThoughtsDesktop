package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.calendar.objects.Tab;
import com.beanloaf.thoughtsdesktop.calendar.objects.WeekBlock;
import com.beanloaf.thoughtsdesktop.calendar.objects.Weekday;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.lang.reflect.Method;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class WeekTab extends Tab {

    public static int START_HOUR = 6;
    public static int END_HOUR = 24;

    public GridPane weekGrid;
    private AnchorPane weekPane;
    private ScrollPane scrollPane;


    private final List<WeekBlock> weekBlockList = new ArrayList<>();

    public WeekTab(final CalendarView view, final TabController tabController) {
        super(view, tabController);
        locateNodes();
        attachEvents();
        createGUI();


        addEventToDay(Weekday.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 20), "TCSS 360 A", "CP 325");
        addEventToDay(Weekday.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 20), "TCSS 360 A", "CP 325");
        addEventToDay(Weekday.FRIDAY, LocalTime.of(11, 0), LocalTime.of(12, 20), "TCSS 360 A", "CP 325");

        addEventToDay(Weekday.MONDAY, LocalTime.of(15, 40), LocalTime.of(17, 40), "TCSS 372 A", "MLG 311");
        addEventToDay(Weekday.WEDNESDAY, LocalTime.of(15, 40), LocalTime.of(17, 40), "TCSS 372 A", "MLG 311");

        addEventToDay(Weekday.WEDNESDAY, LocalTime.of(13, 30), LocalTime.of(15, 30), "TCSS 380 A", "MLG 110");


        addEventToDay(Weekday.WEDNESDAY, LocalTime.of(2, 30), LocalTime.of(4, 30), "test", "desc");
        addEventToDay(Weekday.WEDNESDAY, LocalTime.of(22, 30), LocalTime.of(23, 30), "test", "desc");
    }

    @Override
    protected void locateNodes() {
        weekPane = (AnchorPane) findNodeById("weekTab");
    }

    @Override
    protected void attachEvents() {

    }

    @Override
    protected void createGUI() {
        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.skinProperty().addListener((observableValue, skin, t1) -> {
            StackPane stackPane = (StackPane) scrollPane.lookup("ScrollPane .viewport");
            stackPane.setCache(false);
        });
        weekPane.getChildren().add(ThoughtsHelper.setAnchor(scrollPane, 114, 16, 16, 16));

        createGrid();

    }

    public void createGrid() {
        if (weekGrid != null) {
            weekPane.getChildren().remove(weekGrid);
        }

        weekGrid = new GridPane();
        weekGrid.setGridLinesVisible(true);
        scrollPane.setContent(weekGrid);

        final int columnCount = 8;
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


    public void addEventToDay(final Weekday weekday, final LocalTime startTime,
                              final LocalTime endTime, final String displayText, final String description) {
        addEventToDay(new WeekBlock(weekday, startTime, endTime, displayText, description));
    }

    public void addEventToDay(final WeekBlock weekBlock) {
        this.weekBlockList.add(weekBlock);

        if (adjustBounds()) {
            createGrid();
            for (final WeekBlock block : weekBlockList) {
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

        for (final WeekBlock block : weekBlockList) {
            if (minTime == null || block.getStartTime().isBefore(minTime)) minTime = block.getStartTime();
            if (maxTime == null || block.getEndTime().isAfter(maxTime)) maxTime = block.getEndTime();
        }

        if (minTime != null) START_HOUR = minTime.getHour() - 1;
        if (maxTime != null) END_HOUR = maxTime.getHour() + 1;

        return oldStartHour != START_HOUR || oldEndHour != END_HOUR;

    }
}
