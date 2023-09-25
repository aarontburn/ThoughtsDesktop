package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children.WeekView;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class WeekBlock extends VBox implements EventLabel {




    private final WeekView weekView;
    private final BasicEvent event;

    private String color;


    /*  Components  */
    private final Label nameLabel;
    private Label timeLabel;
    private Label descLabel;


    /*
     * Minimum height for each box is 28 units
     * */
    public WeekBlock(final WeekView weekView, final BasicEvent event) {
        super();

        this.event = event;
        this.weekView = weekView;
        this.color = event.getLinkedDayEvent().getDisplayColor();
        event.getLinkedDayEvent().addReference(this);


        this.setStyle(this.getCss());
        this.setOnMouseClicked(e -> {
            weekView.getParent().getMonthView().selectEvent(event.getLinkedDayEvent(), false);
            Logger.log("WeekBlock: " + event.getTitle() + " pressed.");
        });


        nameLabel = new Label(DayEvent.getDisplayTime(event.getStartTime()) + event.getTitle());
        nameLabel.setMinHeight(0);
        nameLabel.setStyle("-fx-font-size: 18; -fx-padding: 0 0 0 4; -fx-background-color: transparent;");
        nameLabel.setUnderline(true);
        this.getChildren().add(nameLabel);

        final Tooltip tooltip = new Tooltip();
        tooltip.setStyle("-fx-font-size: 15");
        tooltip.setShowDelay(Duration.seconds(0.5));
        tooltip.textProperty().bindBidirectional(nameLabel.textProperty());
        Tooltip.install(this, tooltip);

        final int span = getSpan();

        if (span > 1) {
            timeLabel = new Label(event.getStartTime().format(DateTimeFormatter.ofPattern("h:mm a")) + (event.getEndTime() == null ? "" : " - " + event.getEndTime().format(DateTimeFormatter.ofPattern("h:mm a"))));
            timeLabel.setMinHeight(0);
            timeLabel.setStyle("-fx-font-size: 14; -fx-padding: 0 0 0 4; -fx-background-color: transparent;");
            this.getChildren().add(timeLabel);

            descLabel = new Label(event.getDescription());
            descLabel.setMinHeight(0);
            descLabel.setStyle("-fx-font-size: 14; -fx-padding: 0 0 0 4; -fx-background-color: transparent;");
            this.getChildren().add(descLabel);
        }


    }

    private String getCss() {
        final String color = event.isComplete() ? "rgb(75, 75, 75)" : this.color;

        return "-fx-background-color: " + (event.getEndTime() != null ? color + "" : String.format("linear-gradient(%s, %s)", color, "rgb(60, 63 , 65)")) + ";"
                + " -fx-border-color: derive(" + color + ", +50%); -fx-border-insets: 4; -fx-border-radius: 5;"
                + " -fx-border-style: solid solid " + (event.getEndTime() != null ? " solid" : "none") + " solid;"
                + " -fx-background-radius: 3;";

    }

    public String getEventName() {
        return event.getTitle();
    }

    public String getDescription() {
        return event.getDescription();
    }

    public Weekday getWeekday() {
        return this.event.getWeekday();
    }

    private final static int ROUND_TO_LAST = 30;

    public int getStartIndex() {
        final LocalTime roundedTime = event.getStartTime().truncatedTo(ChronoUnit.HOURS).plusMinutes(ROUND_TO_LAST * (event.getStartTime().getMinute() / ROUND_TO_LAST));
        return (int) (ChronoUnit.MINUTES.between(LocalTime.of(WeekView.START_HOUR, 0), roundedTime) / ROUND_TO_LAST);
    }

    public int getSpan() {
        final LocalTime maxTime = LocalTime.of(23, 30);

        if (event.getStartTime() == null) return 0;
        if (event.getStartTime().equals(maxTime) || event.getStartTime().isAfter(maxTime)) return 1;
        if (event.getEndTime() == null) return 2;


        final LocalTime roundedStartTime = event.getStartTime().truncatedTo(ChronoUnit.HOURS).plusMinutes(ROUND_TO_LAST * (event.getStartTime().getMinute() / ROUND_TO_LAST));
        final LocalTime roundedEndTime = event.getEndTime().truncatedTo(ChronoUnit.HOURS).plusMinutes(ROUND_TO_LAST * (event.getEndTime().getMinute() / ROUND_TO_LAST));
        final int span = (int) (ChronoUnit.MINUTES.between(roundedStartTime, roundedEndTime) / ROUND_TO_LAST);

        return span == 0 ? 1 : span;
    }


    @Override
    public String toString() {
        return "WeekBlock {" +
                "eventName='" + event.getTitle() + '\'' +
                ", description='" + event.getDescription() + '\'' +
                ", startTime=" + event.getStartTime() +
                ", endTime=" + event.getEndTime() +
                ", weekday=" + event.getWeekday() +
                '}';
    }

    @Override
    public void updateEventTitle(String title) {
        event.setTitle(title);
        this.nameLabel.setText(DayEvent.getDisplayTime(event.getStartTime()) + title);
    }

    @Override
    public void updateDescription(String description) {
        if (this.descLabel != null) {
            this.descLabel.setText(description);
        }

    }

    @Override
    public void updateStartDate(LocalDate date) {
        if (event.getStartDate().isEqual(date)) return;

        event.setStartDate(date);
        weekView.refreshWeek();

    }

    @Override
    public void updateEndDate(LocalDate date) {
        // This should not be used.
        event.setEndDate(date);

    }

    @Override
    public void updateStartTime(LocalTime time) {
        event.setStartTime(time);
        this.nameLabel.setText(DayEvent.getDisplayTime(time) + event.getTitle());

        if (this.timeLabel != null) {
            this.timeLabel.setText(event.getStartTime().format(DateTimeFormatter.ofPattern("h:mm a")) + (event.getEndTime() == null ? "" : " - " + event.getEndTime().format(DateTimeFormatter.ofPattern("h:mm a"))));
        }
    }

    @Override
    public void updateEndTime(LocalTime time) {
        event.setEndTime(time);

        if (this.timeLabel != null) {
            this.timeLabel.setText(event.getStartTime().format(DateTimeFormatter.ofPattern("h:mm a")) + (event.getEndTime() == null ? "" : " - " + event.getEndTime().format(DateTimeFormatter.ofPattern("h:mm a"))));
        }
    }

    @Override
    public void updateCompletion(boolean isComplete) {
        event.setCompleted(isComplete);
        this.setStyle(getCss());


    }

    @Override
    public void updateDisplayColor(String color) {
        this.color = event.getLinkedDayEvent().getDisplayColor();
        this.setStyle(this.getCss());

    }
}
