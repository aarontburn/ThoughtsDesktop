package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCalendarDay extends AnchorPane {

    private final VBox scheduleContainer;
    private final List<ScheduleEvent> scheduleEventList = new ArrayList<>();



    public ScheduleCalendarDay() {
        super();


        ThoughtsHelper.setAnchor(this, 0.0, 0.0, 0.0, 0.0);
        this.getStyleClass().add("calendar-day");


        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("calendar-day");
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.fitToWidthProperty().set(true);

        scrollPane.skinProperty().addListener((observableValue, skin, t1) -> {
            StackPane stackPane = (StackPane) scrollPane.lookup("ScrollPane .viewport");
            stackPane.setCache(false);
        });
        this.getChildren().add(ThoughtsHelper.setAnchor(scrollPane, 0.0, 0.0, 0.0, 0.0));


        scheduleContainer = new VBox();
        scheduleContainer.getStyleClass().add("events");
        scheduleContainer.setMinHeight(0);
        scheduleContainer.setSpacing(3);
        scrollPane.setContent(scheduleContainer);

    }

    public void addScheduleEventToDay(final ScheduleListItem scheduleListItem) {
        scheduleEventList.add(scheduleListItem.getEvent());

        final ScheduleListItem.ScheduleLabel label = scheduleListItem.getLabel();

        scheduleListItem.addReference(label);
        scheduleContainer.getChildren().add(label);

    }

    public void removeScheduleEventFromDay(final ScheduleListItem scheduleListItem) {
        scheduleEventList.remove(scheduleListItem.getEvent());

        final ScheduleListItem.ScheduleLabel label = scheduleListItem.getLabel();

        scheduleListItem.removeReference(label);
        scheduleContainer.getChildren().remove(label);


    }

    public List<ScheduleEvent> getScheduleEventList() {
        return this.scheduleEventList;
    }




}
