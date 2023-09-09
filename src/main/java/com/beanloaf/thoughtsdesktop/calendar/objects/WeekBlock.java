package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.WeekTab;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class WeekBlock extends VBox {

    private String eventName;
    private String description;


    private LocalTime startTime;
    private LocalTime endTime;
    private Weekday weekday;


    public WeekBlock(final Weekday weekday, @NotNull final LocalTime startTime, @NotNull final LocalTime endTime, final String eventName, final String description) {
        super();

        this.weekday = weekday;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventName = eventName;
        this.description = description;

        /*
        * Minimum height for each box is 28 units
        * */

        final String[] colors = new String[]{"green", "black", "blue", "red", "navy", "indigo"};
        this.setStyle(String.format("-fx-background-color: %s", colors[new Random().nextInt(colors.length)]));

        final Label nameLabel = new Label(eventName);
        nameLabel.setMinHeight(0);
        nameLabel.setStyle("-fx-font-size: 18; -fx-padding: 0 0 0 8; -fx-background-color: transparent;");
        nameLabel.setUnderline(true);
        this.getChildren().add(nameLabel);

        final int span = getSpan();

        if (span > 1) {
            final Label timeLabel = new Label(startTime.format(DateTimeFormatter.ofPattern("h:mm a")) + " - " + endTime.format(DateTimeFormatter.ofPattern("h:mm a")));
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
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }


    private final static int ROUND_TO_LAST = 30;
    public int getStartIndex() {
        final LocalTime roundedTime = startTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(ROUND_TO_LAST * (startTime.getMinute() / ROUND_TO_LAST));
        return (int) (ChronoUnit.MINUTES.between(LocalTime.of(WeekTab.START_HOUR, 0), roundedTime) / ROUND_TO_LAST);
    }

    public int getSpan() {
        final LocalTime roundedStartTime = startTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(ROUND_TO_LAST * (startTime.getMinute() / ROUND_TO_LAST));
        final LocalTime roundedEndTime = endTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(ROUND_TO_LAST * (endTime.getMinute() / ROUND_TO_LAST));
        final int span = (int) (ChronoUnit.MINUTES.between(roundedStartTime, roundedEndTime) / ROUND_TO_LAST);

        return span == 0 ? 1 : span;
    }


    @Override
    public String toString() {
        return "WeekBlock {" +
                "eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", weekday=" + weekday +
                '}';
    }
}
