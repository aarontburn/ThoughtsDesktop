package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.EventBoxLabel;
import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.views.children.overlays.ScheduleOverlay;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScheduleListItem extends GridPane {


    private final ScheduleOverlay tab;
    private final Map<Weekday, CheckBox> checkBoxMap = new HashMap<>();
    private final List<Weekday> weekdays = new ArrayList<>();
    private final BasicEvent event;
    private final Label displayText;
    private final List<ScheduleLabel> references = new ArrayList<>();

    public ScheduleListItem(final ScheduleOverlay tab, final String scheduleName) {
        this(tab, scheduleName, UUID.randomUUID().toString());

    }

    public ScheduleListItem(final ScheduleOverlay tab, final String scheduleName, final String id) {
        this(tab, new BasicEvent(scheduleName).setId(id));
    }


    public ScheduleListItem(final ScheduleOverlay tab, final BasicEvent event) {
        super();
        this.tab = tab;
        this.event = event;

        this.getStyleClass().add("schedule");


        displayText = new Label(event.getTitle());
        displayText.setStyle("-fx-font-family: Lato; -fx-font-size: 18;");
        this.add(displayText, 0, 0);

        final ColumnConstraints weekendColumnConstraints = new ColumnConstraints();
        weekendColumnConstraints.setPrefWidth(100);
        weekendColumnConstraints.setMinWidth(10);
        weekendColumnConstraints.setHgrow(Priority.SOMETIMES);

        final GridPane weekdayPane = new GridPane();
        for (int i = 0; i < Weekday.values().length; i++) {
            final Weekday weekday = Weekday.values()[i];


            final Label eventLabel = new Label(weekday.getShortAbbreviation());
            eventLabel.setAlignment(Pos.CENTER);
            eventLabel.maxWidthProperty().setValue(10000);
            weekdayPane.add(eventLabel, i, 0);


            final CheckBox checkBox = new CheckBox();
            checkBox.setAlignment(Pos.CENTER);
            checkBox.maxWidthProperty().setValue(10000);

            checkBox.selectedProperty().addListener((observableValue, aBoolean, isChecked) -> {

                if (isChecked) {
                    tab.addScheduleEventToDay(weekday, this);
                    if (!weekdays.contains(weekday)) weekdays.add(weekday);
                } else {
                    tab.removeScheduleFromDay(weekday, this);
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
        tab.setInputFields(this);
    }

    public BasicEvent getEvent() {
        return this.event;
    }


    public void setChecked(final Weekday weekday, final boolean isChecked) {
        this.checkBoxMap.get(weekday).setSelected(isChecked);
    }

    public void setScheduleEventName(final String newName) {
        event.setTitle(newName);
        displayText.setText(newName);

        for (final ScheduleLabel scheduleLabel : references) {
            scheduleLabel.updateText();
        }
    }

    public String getScheduleEventName() {
        return event.getTitle();
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

    public static class ScheduleLabel extends EventBoxLabel {

        private final ScheduleListItem scheduleListItem;


        public ScheduleLabel(final ScheduleListItem scheduleListItem) {
            super("");
            setGraphic(new ImageView(new Image(String.valueOf(MainApplication.class.getResource("icons/schedule-icon.png")), 17.5, 17.5, true, true)));
            this.scheduleListItem = scheduleListItem;


            getToolTip().setText(scheduleListItem.getScheduleEventName());
            updateText();
        }

        @Override
        public void onClick() {
            Logger.log("Schedule \"" + this.scheduleListItem.getScheduleEventName() + "\" was pressed.");
            scheduleListItem.doClick();
        }

        public void updateText() {
            final String displayText = getDisplayTime(scheduleListItem.getStartTime()) + scheduleListItem.getScheduleEventName();

            this.setText(displayText);
            getToolTip().setText(displayText);
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
