package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.EventLabel;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ScheduleBoxItem extends AnchorPane implements EventLabel {
    public static final int PREF_WIDTH = 400;


    private final ScheduleData data;
    private final Label scheduleNameLabel, startDateLabel, endDateLabel, colorLabel;
    private final CheckBox hideCheckBox;
    private final Button randomizeColorButton;

    public ScheduleBoxItem(final CalendarMain main, final ScheduleData data) {
        super();

        this.data = data;
        data.addReference(this);


        this.setPrefHeight(150);
        this.getStyleClass().add("schedule-box-item");

        scheduleNameLabel = new Label();
        updateEventTitle(data.getScheduleName());
        scheduleNameLabel.setStyle("-fx-font-size: 18px");
        this.getChildren().add(ThoughtsHelper.setAnchor(scheduleNameLabel, 16, null, 16, null));

        startDateLabel = new Label();
        updateStartDate(data.getStartDate());
        startDateLabel.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(startDateLabel, 40, null, 48, null));


        endDateLabel = new Label();
        updateEndDate(data.getEndDate());
        endDateLabel.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(endDateLabel, 64, null, 48, null));


        final HBox colorHBox = new HBox(16);
        colorHBox.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().add(ThoughtsHelper.setAnchor(colorHBox, 88, null, 48, null));

        final Label colorTextLabel = new Label("Color: ");
        colorTextLabel.setStyle("-fx-font-size: 16;");
        colorHBox.getChildren().add(colorTextLabel);

        colorLabel = new Label();
        colorLabel.setPrefSize(24, 24);
        colorLabel.setStyle(
                "-fx-border-color: black; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-background-color: " + data.getDisplayColor());
        colorHBox.getChildren().add(colorLabel);

        randomizeColorButton = new Button("Randomize Color");
        randomizeColorButton.getStyleClass().add("calendar-button");
        randomizeColorButton.setOnAction(e -> {
            final String newColor = CH.getRandomColor();
            data.setDisplayColor(newColor);
            colorLabel.setStyle(
                    "-fx-border-color: black; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-background-color: " + newColor);


            final Map<Weekday, Map<String, BasicEvent>> map = data.getScheduleEventList();
            for (final Map<String, BasicEvent> uidEventMap : map.values()) {
                for (final String uid : uidEventMap.keySet()) {
                    final BasicEvent event = uidEventMap.get(uid);
                    event.setDisplayColor(newColor);
                }
            }
            main.getJsonHandler().writeScheduleData(data);

        });
        colorHBox.getChildren().add(randomizeColorButton);



        final Button editButton = new Button("Edit/View");
        editButton.getStyleClass().add("calendar-button");
        editButton.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(editButton, 135, 16, null, 16));
        editButton.setOnAction(e -> main.swapOverlay(CalendarMain.Overlays.SCHEDULE, data));


        final Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("calendar-button");
        deleteButton.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(deleteButton, 135, 16, 16, null));
        deleteButton.setOnAction(e -> main.getRightPanel().getMonthView().deleteSchedule(this));


        hideCheckBox = new CheckBox();
        hideCheckBox.setStyle("-fx-font-family: Lato;");
        hideCheckBox.setText("Hide");
        hideCheckBox.setOnAction(e -> main.getRightPanel().getMonthView().hideSchedule(data, hideCheckBox.isSelected()));
        this.getChildren().add(ThoughtsHelper.setAnchor(hideCheckBox, 135, 16, 96, null));
    }

    public ScheduleData getScheduleData() {
        return this.data;
    }

    public String getScheduleId() {
        return data.getId();
    }

    public void setHidden(final boolean isHidden) {
        hideCheckBox.setSelected(isHidden);
    }

    @Override
    public void updateEventTitle(String title) {
        scheduleNameLabel.setText(data.getScheduleName().isEmpty() ? "<Untitled Schedule>" : data.getScheduleName());
    }

    @Override
    public void updateDescription(String description) {

    }

    @Override
    public void updateStartDate(LocalDate date) {
        startDateLabel.setText(data.getStartDate() == null ? "" : "Start Date: " + data.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }

    @Override
    public void updateEndDate(LocalDate date) {
        endDateLabel.setText(data.getEndDate() == null ? "" : "End Date: " + data.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

    }

    @Override
    public void updateStartTime(LocalTime time) {

    }

    @Override
    public void updateEndTime(LocalTime time) {

    }

    @Override
    public void updateCompletion(boolean isComplete) {

    }

    @Override
    public void updateDisplayColor(String color) {

    }
}
