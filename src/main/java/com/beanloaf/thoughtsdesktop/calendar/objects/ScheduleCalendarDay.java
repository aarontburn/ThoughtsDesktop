package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.SchedulePopup;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCalendarDay extends AnchorPane {

    private final VBox scheduleContainer;
    private final List<ScheduleEvent> scheduleList = new ArrayList<>();

    private final Weekday weekday;


    public ScheduleCalendarDay(final Weekday weekday) {
        super();
        this.weekday = weekday;
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

    public void addSchedule(final Schedule schedule) {
        final ScheduleEvent event = new ScheduleEvent(schedule, weekday);

        scheduleList.add(event);
        schedule.addReference(event);

        scheduleContainer.getChildren().add(event);
    }

    public void removeSchedule(final Schedule schedule) {
        final ScheduleEvent event = new ScheduleEvent(schedule, weekday);

        scheduleList.remove(event);
        schedule.removeReference(event);


        scheduleContainer.getChildren().remove(event);
    }

    public List<ScheduleEvent> getScheduleList() {
        return this.scheduleList;
    }

    public Weekday getWeekday() {
        return this.weekday;
    }

}
