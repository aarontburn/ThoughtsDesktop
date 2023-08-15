package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class CalendarDay extends AnchorPane {

    private final CalendarView view;

    private final LocalDate date;

    private final VBox eventContainer;

    private final List<DayEvent> eventList = new ArrayList<>();


    public CalendarDay(final Integer year, final Month month, final Integer day, final CalendarView view) {
        super();
        this.view = view;


        date = day == null ? null : LocalDate.of(year, month, day);


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


        // Triggers when a day event is clicked
        this.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                onClick();
                final Node node = (Node) e.getTarget();

                if (node.getId() != null && node.getId().equals(DayEvent.DAY_EVENT_ID)) {
                    if (node.getClass() == DayEvent.class) {
                        ((DayEvent) node).onClick();
                    } else {
                        ((DayEvent) node.getParent()).onClick();

                    }
                }

            }


        });




        final Label dateLabel = new Label(day != null ? Integer.toString(day) : "");
        this.getChildren().add(ThoughtsHelper.setAnchor(dateLabel, 4.0, null, null, 12.0));


    }

    public void onClick() {
        view.selectDay(this);

    }

    public Integer getDay() {
        return this.date.getDayOfMonth();
    }

    public Month getMonth() {
        return this.date.getMonth();
    }

    public Integer getYear() {
        return this.date.getYear();
    }


    public DayEvent addEvent(final DayEvent event) {
        eventList.add(event);
        eventContainer.getChildren().add(event);
        return event;

    }

    public void removeEvent(final DayEvent event) {


        eventList.remove(event);


        eventContainer.getChildren().remove(event);



    }


    public DayEvent[] getEvents() {
        return eventList.toArray(new DayEvent[0]);
    }



}
