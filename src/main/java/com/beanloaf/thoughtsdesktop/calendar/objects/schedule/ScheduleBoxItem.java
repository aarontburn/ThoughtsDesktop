package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;

public class ScheduleBoxItem extends AnchorPane {
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
        updateScheduleNameLabel();
        scheduleNameLabel.setStyle("-fx-font-size: 18px");
        this.getChildren().add(ThoughtsHelper.setAnchor(scheduleNameLabel, 16, null, 16, null));

        startDateLabel = new Label();
        updateStartDateLabelText();
        startDateLabel.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(startDateLabel, 40, null, 48, null));


        endDateLabel = new Label();
        updateEndDateLabelText();
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
        randomizeColorButton.getStyleClass().add("calendarButton");
        randomizeColorButton.setOnAction(e -> {
            final String newColor = CH.getRandomColor();

            data.setDisplayColor(newColor);

            colorLabel.setStyle(
                    "-fx-border-color: black; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-background-color: " + newColor);

            for (final String uid : canvasClass.getUidList()) {
                final BasicEvent event = canvasClass.getEvent(uid);
                event.setDisplayColor(newColor);
            }

            main.getCanvasICalHandler().cacheCanvasEventsToJson();

        });
        colorHBox.getChildren().add(randomizeColorButton);



        final Button editButton = new Button("Edit/View");
        editButton.getStyleClass().add("calendarButton");
        editButton.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(editButton, 135, 16, null, 16));
        editButton.setOnAction(e -> main.swapOverlay(CalendarMain.Overlays.SCHEDULE, data));


        final Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("calendarButton");
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

    public void updateScheduleNameLabel() {
        scheduleNameLabel.setText(data.getScheduleName().isEmpty() ? "<Untitled Schedule>" : data.getScheduleName());
    }

    public void updateStartDateLabelText() {
        startDateLabel.setText(data.getStartDate() == null ? "" : "Start Date: " + data.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }

    public void updateEndDateLabelText() {
        endDateLabel.setText(data.getEndDate() == null ? "" : "End Date: " + data.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }

    public String getScheduleId() {
        return data.getId();
    }

    public void setHidden(final boolean isHidden) {
        hideCheckBox.setSelected(isHidden);
    }

}
