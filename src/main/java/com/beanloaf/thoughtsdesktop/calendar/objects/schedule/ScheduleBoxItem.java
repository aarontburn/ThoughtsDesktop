package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ScheduleBoxItem extends AnchorPane {

    private final ScheduleData data;

    public ScheduleBoxItem(final CalendarView view, final ScheduleData data) {
        super();

        this.data = data;

        this.setStyle("-fx-border-color: blue;");

        final Label scheduleNameLabel = new Label(data.getScheduleName().isEmpty() ? "<Untitled Schedule>" : data.getScheduleName());
        scheduleNameLabel.setStyle("-fx-font-size: 18px");
        this.getChildren().add(ThoughtsHelper.setAnchor(scheduleNameLabel, 16, null, 16, null));


        final Label startDateLabel = new Label("Start Date: " + data.getStartDate());
        startDateLabel.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(startDateLabel, 40, null, 48, null));


        final Label endDateLabel = new Label("End Date: " + data.getEndDate());
        endDateLabel.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(endDateLabel, 64, null, 48, null));


        final Button editButton = new Button("Edit/View");
        editButton.getStyleClass().add("calendarButton");
        editButton.setStyle("-fx-font-size: 16");
        this.getChildren().add(ThoughtsHelper.setAnchor(editButton, null, 16, null, 16));

        editButton.setOnAction(e -> {
            view.popup.displaySchedule(data);

        });


    }


}
