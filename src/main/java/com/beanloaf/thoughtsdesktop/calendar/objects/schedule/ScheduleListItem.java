package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.views.SchedulePopup;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScheduleListItem extends GridPane {



    private final SchedulePopup popup;


    private final Map<Weekday, CheckBox> checkBoxMap = new HashMap<>();

    private final List<Weekday> weekdays = new ArrayList<>();

    private final ScheduleEvent event;

    private final Label displayText;

    private final List<ScheduleLabel> references = new ArrayList<>();



    public ScheduleListItem(final SchedulePopup popup, final String scheduleName) {
        this(popup, scheduleName, UUID.randomUUID().toString());

    }

    public ScheduleListItem(final SchedulePopup popup, final String scheduleName, final String id) {
        this(popup, new ScheduleEvent(scheduleName, id));
    }


    public ScheduleListItem(final SchedulePopup popup, final ScheduleEvent event) {
        super();
        this.popup = popup;
        this.event = event;

        this.getStyleClass().add("schedule");


        displayText = new Label(event.getScheduleEventName());
        displayText.setStyle("-fx-font-family: Lato; -fx-font-size: 18;");
        this.add(displayText, 0, 0);

        final ColumnConstraints weekendColumnConstraints = new ColumnConstraints();
        weekendColumnConstraints.setPrefWidth(100);
        weekendColumnConstraints.setMinWidth(10);
        weekendColumnConstraints.setHgrow(Priority.SOMETIMES);

        final GridPane weekdayPane = new GridPane();
        for (int i = 0; i < Weekday.values().length; i++) {
            final Weekday weekday = Weekday.values()[i];


            final Label eventLabel = new Label(weekday.getAbbreviation());
            eventLabel.setAlignment(Pos.CENTER);
            eventLabel.maxWidthProperty().setValue(10000);
            weekdayPane.add(eventLabel, i, 0);


            final CheckBox checkBox = new CheckBox();
            checkBox.setAlignment(Pos.CENTER);
            checkBox.maxWidthProperty().setValue(10000);

            checkBox.selectedProperty().addListener((observableValue, aBoolean, isChecked) -> {

                if (isChecked) {
                    popup.addScheduleEventToDay(weekday, this);
                    if (!weekdays.contains(weekday)) weekdays.add(weekday);
                }
                else {
                    popup.removeScheduleFromDay(weekday, this);
                    while (weekdays.contains(weekday)) weekdays.remove(weekday);
                }

            });


            checkBoxMap.put(weekday, checkBox);

            weekdayPane.add(checkBox, i, 1);
            weekdayPane.getColumnConstraints().add(weekendColumnConstraints);
        }
        this.add(weekdayPane, 1, 0);

        final RowConstraints firstRow = new RowConstraints();
        firstRow.percentHeightProperty().setValue(40);
        weekdayPane.getRowConstraints().add(firstRow);

        final RowConstraints secondRow = new RowConstraints();
        secondRow.percentHeightProperty().setValue(60);
        weekdayPane.getRowConstraints().add(secondRow);


        final ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50);
        columnConstraints.setMinWidth(10);
        columnConstraints.setHgrow(Priority.SOMETIMES);

        getColumnConstraints().add(columnConstraints);
        getColumnConstraints().add(columnConstraints);


        this.setOnMouseClicked(e -> doClick());

    }




    public void doClick() {
        popup.setInputFields(this);
    }

    public ScheduleEvent getEvent() {
        return this.event;
    }


    public void setChecked(final Weekday weekday, final boolean isChecked) {
        this.checkBoxMap.get(weekday).setSelected(isChecked);
    }

    public void setScheduleEventName(final String newName) {
        event.setScheduleEventName(newName);
        displayText.setText(newName);


        for (final ScheduleLabel scheduleLabel : references) {
            scheduleLabel.updateText();
        }
    }

    public String getScheduleEventName() {
        return event.getScheduleEventName();
    }

    public void setDescription(final String newDescription) {
        event.setDescription(newDescription);
    }

    public String getDescription() {
        return event.getDescription();
    }


    public void setStartTime(final LocalTime startTime) {
        this.event.setStartTime(startTime);
    }

    public void setEndTime(final LocalTime endTime) {
        this.event.setEndTime(endTime);
    }


    public LocalTime getStartTime() {
        return event.getStartTime();
    }

    public LocalTime getEndTime() {
        return event.getEndTime();
    }




    public void addReference(final ScheduleLabel event) {
        this.references.add(event);
    }

    public void removeReference(final ScheduleLabel event) {
        this.references.remove(event);
    }

    public ScheduleLabel getLabel() {
        return new ScheduleLabel(this);
    }




    @Override
    public String toString() {
        return "ScheduleListItem {" +
                "weekdays=" + weekdays +
                ", event=" + event +
                '}';
    }

    public static class ScheduleLabel extends Label {

        private final ScheduleListItem scheduleListItem;

        private final Tooltip tooltip;

        public ScheduleLabel(final ScheduleListItem scheduleListItem) {
            super("", new ImageView(new Image(String.valueOf(MainApplication.class.getResource("icons/schedule-icon.png")), 17.5, 17.5, true, true)));

            this.scheduleListItem = scheduleListItem;

            this.getStyleClass().add("day-event");
            this.setMaxWidth(Double.MAX_VALUE);

            tooltip = new Tooltip(scheduleListItem.getScheduleEventName());
            tooltip.setShowDelay(Duration.seconds(0.5));
            this.setTooltip(tooltip);

            updateText();


            this.setOnMouseClicked(e -> {
                Logger.log("Schedule \"" + this.scheduleListItem.getScheduleEventName() + "\" was pressed.");
                scheduleListItem.doClick();
            });
        }

        public void updateText() {
            final String displayText = getDisplayTime(scheduleListItem.getStartTime()) + scheduleListItem.getScheduleEventName();

            this.setText(displayText);
            tooltip.setText(displayText);
        }



        public String getDisplayTime(final LocalTime time) {
            String formattedTime = "";
            if (time != null) {
                formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a")) + " | ";
                if (formattedTime.contains("AM")) {
                    formattedTime = formattedTime.replace(" AM", "a");
                } else {
                    formattedTime = formattedTime.replace(" PM", "p");

                }
            }
            return formattedTime;
        }

        public String getScheduleId() {
            return scheduleListItem.getEvent().getId();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            final ScheduleLabel that = (ScheduleLabel) other;
            return getScheduleId().equals(that.getScheduleId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(scheduleListItem);
        }
    }



}
