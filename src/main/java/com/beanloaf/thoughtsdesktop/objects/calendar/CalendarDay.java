package com.beanloaf.thoughtsdesktop.objects.calendar;

import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.*;

public class CalendarDay extends AnchorPane {

    private final VBox eventContainer;

    private final List<DayEvent> eventList = new ArrayList<>();


    public CalendarDay(final Integer dayNum) {
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


        this.getChildren().add(ThoughtsHelper.setAnchor(new Label(dayNum != null ? Integer.toString(dayNum) : "☺"), 4.0, null, null, 10.0));


    }

    public void addEvent(final String eventName) {
        final DayEvent eventLabel = new DayEvent(eventName);
        eventList.add(eventLabel);

        eventContainer.getChildren().add(eventLabel);

    }

    public void removeEvent(final String eventName) {


    }


}
