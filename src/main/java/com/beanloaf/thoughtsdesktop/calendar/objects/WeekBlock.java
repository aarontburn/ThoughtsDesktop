package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.WeekTab;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class WeekBlock extends VBox {

    private final Event event;


    public WeekBlock(final Weekday weekday, final LocalTime startTime, final LocalTime endTime, final String eventName, final String description) {
        super();

        this.event = new Event(eventName);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setWeekday( weekday);
        event.setDescription(description);


        /*
         * Minimum height for each box is 28 units
         * */

        final String[] colors = new String[]{"green", "black", "blue", "navy", "indigo"};
        String color = colors[new Random().nextInt(colors.length)];

        this.setStyle("-fx-background-color: " + (endTime != null ? color + "" : String.format("linear-gradient(%s, %s)", color, "rgb(60, 63 , 65)")) + ";"
                + " -fx-border-color: derive(" + color + ", +50%); -fx-border-insets: 4; -fx-border-radius: 5;"
                + " -fx-border-style: solid solid " + (endTime != null ? " solid" : "none") + " solid;"
                + " -fx-background-radius: 3;");

        this.setOnMouseClicked(e -> Logger.log("WeekBlock: " + eventName + " pressed."));

        final Label nameLabel = new Label(eventName);
        nameLabel.setMinHeight(0);

        nameLabel.setStyle("-fx-font-size: 18; -fx-padding: 0 0 0 8; -fx-background-color: transparent;");
        nameLabel.setUnderline(true);
        this.getChildren().add(nameLabel);

        final int span = getSpan();

        if (span > 1) {
            final Label timeLabel = new Label(startTime.format(DateTimeFormatter.ofPattern("h:mm a")) + (endTime == null ? "" : " - " + endTime.format(DateTimeFormatter.ofPattern("h:mm a"))));
            timeLabel.setMinHeight(0);
            timeLabel.setStyle("-fx-font-size: 14; -fx-padding: 0 0 0 8; -fx-background-color: transparent;");
            this.getChildren().add(timeLabel);

            final Label descLabel = new Label(description);
            descLabel.setMinHeight(0);
            descLabel.setStyle("-fx-font-size: 14; -fx-padding: 0 0 0 8; -fx-background-color: transparent;");
            this.getChildren().add(descLabel);
        }


    }

    public String getEventName() {
        return event.getTitle();
    }

    public void setEventName(String eventName) {
        this.event.setTitle(eventName);
    }

    public String getDescription() {
        return event.getDescription();
    }

    public void setDescription(String description) {
        this.event.setDescription(description);
    }

    public LocalTime getStartTime() {
        return event.getStartTime();
    }

    public void setStartTime(LocalTime startTime) {
        this.event.setStartTime(startTime);
    }

    public LocalTime getEndTime() {
        return this.event.getEndTime() == null ? this.event.getStartTime().plusHours(1) : this.event.getEndTime();
    }

    public void setEndTime(LocalTime endTime) {
        this.event.setEndTime(endTime);
    }

    public Weekday getWeekday() {
        return this.event.getWeekday();
    }

    private final static int ROUND_TO_LAST = 30;

    public int getStartIndex() {
        final LocalTime roundedTime = event.getStartTime().truncatedTo(ChronoUnit.HOURS).plusMinutes(ROUND_TO_LAST * (event.getStartTime().getMinute() / ROUND_TO_LAST));
        return (int) (ChronoUnit.MINUTES.between(LocalTime.of(WeekTab.START_HOUR, 0), roundedTime) / ROUND_TO_LAST);
    }

    public int getSpan() {
        final LocalTime maxTime = LocalTime.of(23, 30);

        if (event.getStartTime() == null) {
            return 0;
        }

        if (event.getStartTime().equals(maxTime) || event.getStartTime().isAfter(maxTime)) {
            return 1;
        }
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
}
