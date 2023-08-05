package com.beanloaf.thoughtsdesktop.objects.calendar;

import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.*;

public class CalendarDay extends AnchorPane {

    private final VBox eventContainer;

    private final List<DayEvent> eventList = new ArrayList<>();

    private boolean isReady = false;

    private final List<Runnable> unreadyQueue = new ArrayList<>();


    public CalendarDay(final int dayNum) {
        super();

        ThoughtsHelper.setAnchor(this, 0.0, 0.0, 0.0, 0.0);
        this.getStyleClass().add("calendar-day");


        final ScrollPane scrollPane = new ScrollPane();

        this.getChildren().add(ThoughtsHelper.setAnchor(scrollPane, 0.0, 0.0, 0.0, 0.0));


        eventContainer = new VBox();
        eventContainer.getStyleClass().add("events");
        eventContainer.setMinHeight(0);
        scrollPane.setContent(eventContainer);

        this.getChildren().add(ThoughtsHelper.setAnchor(new Label(Integer.toString(dayNum)), 0.0, null, null, 8.0));


//        eventContainer.heightProperty().addListener((observableValue, number, newValue) -> {
//            if (!isReady) {
//                isReady = true;
//
//                for (final Runnable runnable : unreadyQueue) {
//                    runnable.run();
//                }
//            }
//
//            double height = eventContainer.getPadding().getTop();
//
//            final Queue<DayEvent> notVisibleEvents = new LinkedList<>();
//
//            for (final DayEvent event : eventList) {
//                if (event.isEventVisible()) {
//                    height += event.getHeight() == 0.0 ? DayEvent.DEFAULT_HEIGHT : event.getHeight();
//
//                } else {
//                    notVisibleEvents.add(event);
//                }
//            }
//
//            if (height >= newValue.doubleValue() && eventContainer.getChildren().size() > 0) {
//                while (height >= newValue.doubleValue()) {
//                    final DayEvent removedItem = (DayEvent) eventContainer.getChildren().remove(eventContainer.getChildren().size() - 1);
//                    removedItem.setEventVisibility(false);
//                    height -= DayEvent.DEFAULT_HEIGHT;
//                }
//
//            } else {
//                while (notVisibleEvents.size() > 0 && height > 0 && height + DayEvent.DEFAULT_HEIGHT < newValue.doubleValue()) {
//                    final DayEvent eventToAdd = notVisibleEvents.remove();
//                    eventToAdd.setEventVisibility(true);
//                    eventContainer.getChildren().add(eventToAdd);
//
//                    height += DayEvent.DEFAULT_HEIGHT;
//
//
//                }
//            }
//
//        });


    }

    public void addEvent(final String eventName) {
//        if (!isReady) {
//            unreadyQueue.add(() -> addEvent(eventName));
//            return;
//        }
//
//        final DayEvent eventLabel = new DayEvent(eventName);
//
//        double height = eventContainer.getPadding().getTop();
//
//        for (final DayEvent event : eventList) {
//            if (event.isEventVisible()) {
//                height += event.getHeight() == 0.0 ? DayEvent.DEFAULT_HEIGHT : event.getHeight();
//            }
//        }
//
//        eventList.add(eventLabel);
//
//        if (height > 0 && height + DayEvent.DEFAULT_HEIGHT < eventContainer.getHeight()) {
//            eventLabel.setEventVisibility(true);
//            eventContainer.getChildren().add(eventLabel);
//
//        } else {
//            eventLabel.setEventVisibility(false);
//        }

        final DayEvent eventLabel = new DayEvent(eventName);
        eventList.add(eventLabel);

        eventContainer.getChildren().add(eventLabel);

    }

    public void removeEvent(final String eventName) {


    }


}
