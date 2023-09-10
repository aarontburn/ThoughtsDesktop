package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class CalendarDay extends AnchorPane {

    private final CalendarView view;

    private final LocalDate date;

    private final VBox eventContainer;
    private final ScrollPane scrollPane;
    private final Text dateText;
    private final List<DayEvent> eventList = new ArrayList<>();


    public CalendarDay(final Integer year, final Month month, final Integer day, final CalendarView view) {
        super();
        this.view = view;


        date = day == null ? null : LocalDate.of(year, month, day);


        ThoughtsHelper.setAnchor(this, 0.0, 0.0, 0.0, 0.0);
        this.getStyleClass().add("calendar-day");

        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("calendar-day");
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.skinProperty().addListener((observableValue, skin, t1) -> {
            final StackPane stackPane = (StackPane) scrollPane.lookup("ScrollPane .viewport");
            stackPane.setCache(false);
        });
        this.getChildren().add(ThoughtsHelper.setAnchor(scrollPane, 0.0, 0.0, 0.0, 0.0));


        eventContainer = new VBox();
        eventContainer.getStyleClass().add("events");
        eventContainer.setMinHeight(0);
        eventContainer.setSpacing(2);
        scrollPane.setContent(eventContainer);


        // Triggers when a day event is clicked
        this.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                onClick();
                final Node node = (Node) e.getTarget();

//                Logger.log(node.getId());


                if (node.getId() != null && node.getId().equals(DayEvent.DAY_EVENT_ID)) {
                    if (node.getClass() == DayEvent.class) {
                        ((DayEvent) node).onClick();
                    } else {
                        ((DayEvent) node.getParent()).onClick();

                    }
                }

            }


        });




        dateText = new Text();

        if (day != null && day == 1) {
            dateText.setText(ThoughtsHelper.toCamelCase(month.toString()) + " " + day);
        } else {
            dateText.setText(day != null ? Integer.toString(day) : "");
        }


        dateText.setTextAlignment(TextAlignment.RIGHT);
        dateText.getStyleClass().add("calendar-date-label");
        dateText.setPickOnBounds(false);
        this.getChildren().add(ThoughtsHelper.setAnchor(dateText, 4, null, null, 12));


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


    public LocalDate getDate() {
        return this.date;
    }

    public void addEvent(final DayEvent event) {
        eventList.add(event);
        eventContainer.getChildren().add(event);

    }

    public void removeEvent(final DayEvent event) {
        eventList.remove(event);
        eventContainer.getChildren().remove(event);
    }

    public void checkIsToday() {

        final LocalDate now = LocalDate.now();

        final String style = "-fx-border-color: rgb(41, 163, 211); -fx-border-radius: 5;";

        if (date.isEqual(now)) {
            scrollPane.setStyle(style);
            this.setStyle(style);

            dateText.setText("(Today) " + (getDay() == 1 ? ThoughtsHelper.toCamelCase(getMonth().toString()) : "") + getDay());
        }
    }


    public DayEvent[] getEvents() {
        return eventList.toArray(new DayEvent[0]);
    }



}
