package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCalendarDay extends AnchorPane {

    private final VBox eventContainer;
    private final List<DayEvent> eventList = new ArrayList<>();



    public ScheduleCalendarDay(final int weekday) {
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


        eventContainer = new VBox();
        eventContainer.getStyleClass().add("events");
        eventContainer.setMinHeight(0);
        scrollPane.setContent(eventContainer);


        final Label dateLabel = new Label(Integer.toString(weekday));
        this.getChildren().add(ThoughtsHelper.setAnchor(dateLabel, 4.0, null, null, 12.0));

    }

    public DayEvent addEvent(final DayEvent event) {
        eventList.add(event);
        eventContainer.getChildren().add(event);
        return event;

    }


}
