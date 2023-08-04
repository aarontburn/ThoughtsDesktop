package com.beanloaf.thoughtsdesktop.objects.calendar;

import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CalendarDay extends AnchorPane {

    private final VBox eventContainer;

    private final List<DayEvent> eventList = new ArrayList<>();


    public CalendarDay(final int dayNum) {
        super();

        ThoughtsHelper.setAnchor(this, 0.0, 0.0, 0.0, 0.0);
        this.getStyleClass().add("calendar-day");

        eventContainer = new VBox();
        eventContainer.getStyleClass().add("events");
        this.getChildren().add(ThoughtsHelper.setAnchor(eventContainer, 0.0, 0.0, 0.0, 8.0));


        this.getChildren().add(ThoughtsHelper.setAnchor(new Label(Integer.toString(dayNum)), 0.0, null, null, 8.0));



        if (dayNum == 1) {

            eventContainer.heightProperty().addListener((observableValue, number, newValue) -> {
                double height = 12; // starts at 12 since default top padding is 12
                for (final DayEvent day : eventList) {
                    height += day.getLabel().getHeight();
                }


                if (height >= newValue.doubleValue() && eventContainer.getChildren().size() > 0) {
                    eventContainer.getChildren().remove(eventContainer.getChildren().size() - 1);

                }

                Logger.log("VBox: " + newValue + " Height: " + eventContainer.heightProperty().get());

//                Logger.log("Parent: " + this.getHeight() + " VBox: " + newValue + " Event: " + height);
            });

        }


    }

    public void addEvent(final String eventName) {
        final DayEvent eventLabel = new DayEvent(eventName);

        eventList.add(eventLabel);
        eventContainer.getChildren().add(eventLabel.getLabel());
    }


}
