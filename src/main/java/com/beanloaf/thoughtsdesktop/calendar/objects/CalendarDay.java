package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CalendarDay extends AnchorPane {

    private final CalendarMain main;

    private final LocalDate date;

    private final VBox eventContainer;
    private final ScrollPane scrollPane;
    private final Text dateText;
    private final List<DayEvent> eventList = new ArrayList<>();


    public CalendarDay(final Integer year, final Month month, final Integer day, final CalendarMain main) {
        super();
        this.main = main;


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
            final Node node = (Node) e.getTarget();

            if (node.getId() == null) { // clicking on nothing but the box itself or the date label
                onClick();
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
        main.getRightPanel().getMonthView().selectDay(this, true);
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

    public void sortEvents() {
        FXCollections.sort(eventContainer.getChildren(), DayEvent.getDayEventComparator());

    }

    public void addEvent(final DayEvent event) {
        eventList.add(event);
        Platform.runLater(() -> {
            eventContainer.getChildren().add(event);
            sortEvents();
        });
    }

    public void removeEvent(final DayEvent event) {
        Node nodeToRemove = null;
        for (final DayEvent dayEvent : eventList) {
            if (dayEvent.getEventID().equals(event.getEventID())) {
                nodeToRemove = dayEvent;
            }
        }

        eventList.remove(nodeToRemove);

        final Node finalNodeToRemove = nodeToRemove;
        Platform.runLater(() -> eventContainer.getChildren().remove(finalNodeToRemove));
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarDay that = (CalendarDay) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
