package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.calendar.objects.Tab;
import com.beanloaf.thoughtsdesktop.calendar.objects.Weekday;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class WeekTab extends Tab {

    private GridPane weekGrid;
    private AnchorPane weekPane;

    public WeekTab(final CalendarView view, final TabController tabController) {
        super(view, tabController);
        locateNodes();
        attachEvents();
        createGUI();

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

        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.skinProperty().addListener((observableValue, skin, t1) -> {
            StackPane stackPane = (StackPane) scrollPane.lookup("ScrollPane .viewport");
            stackPane.setCache(false);
        });
        weekPane.getChildren().add(ThoughtsHelper.setAnchor(scrollPane, 114, 16, 16, 16));


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

        LocalTime time = LocalTime.of(6, 0);

        final int rowCount = 36;
        for (int row = 0; row < rowCount; row++) {
            final RowConstraints con = new RowConstraints();
            con.setPercentHeight(100.0 / rowCount);
            con.setValignment(VPos.TOP);
            weekGrid.getRowConstraints().add(con);


            Label label = new Label();
            if (row % 2 == 0) {
                label.setText(time.format(DateTimeFormatter.ofPattern("h:mm a")));
                time = time.plusHours(1);
            }
            label.setStyle("-fx-font-size: 14");


            weekGrid.add(label, 0, row);
        }


        addEventToDay(Weekday.WEDNESDAY, LocalTime.of(7, 35), LocalTime.of(13, 35), "test");
    }


    public void addEventToDay(final Weekday weekday, LocalTime startTime,
                              LocalTime endTime, final String displayText) {

        final AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-border-color: red; -fx-background-color: red");

        startTime = startTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(30 * (startTime.getMinute() / 30));
        endTime = endTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(30 * (endTime.getMinute() / 30));

        final int columnIndex = weekday.getDayOfWeek() + 1;
        final int rowIndex = (int) (ChronoUnit.MINUTES.between(LocalTime.of(6, 0), startTime) / 30);
        final int columnSpan = 1; // this shouldn't change
        final int rowSpan = (int) (ChronoUnit.MINUTES.between(startTime, endTime) / 30);

        Logger.log("rowIndex: " + rowIndex + " rowSpan: " + rowSpan + " | ");

        weekGrid.add(pane, columnIndex, rowIndex, columnSpan, rowSpan);


        final Label label = new Label(displayText);

        pane.getChildren().add(ThoughtsHelper.setAnchor(label, 16,  null, 0, 0));


    }

    // row 0 is 6, 1 is 6:30, 2 is 7
}
