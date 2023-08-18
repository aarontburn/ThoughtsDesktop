package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.SchedulePopup;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Schedule extends GridPane {



    private final SchedulePopup view;


    private final Map<Weekday, CheckBox> checkBoxMap = new HashMap<>();

    private final List<Weekday> weekdays = new ArrayList<>();

    private String scheduleName = "";
    private LocalTime startTime;
    private LocalTime endTime;
    private String description = "";

    private String id;

    private final Label displayText;

    private final List<ScheduleEvent> references = new ArrayList<>();


    public Schedule(final SchedulePopup view, final String scheduleName, final String id) {
        super();
        this.view = view;
        this.scheduleName = scheduleName;

        this.id = id;


        this.getStyleClass().add("schedule");


        displayText = new Label(scheduleName);
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
                    view.addScheduleToWeekView(weekday, this);

                    if (!weekdays.contains(weekday)) weekdays.add(weekday);
                }
                else {
                    view.removeScheduleFromWeekView(weekday, this);

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

    public Schedule(final SchedulePopup view, final String scheduleName) {
        this(view, scheduleName, UUID.randomUUID().toString());

    }

    public void doClick() {
        view.setInputFields(this);


    }

    public void setScheduleName(final String newName) {
        this.scheduleName = newName;
        displayText.setText(newName);

        for (final ScheduleEvent scheduleEvent : references) {
            scheduleEvent.setText(newName);

        }
    }

    public String getScheduleName() {
        return this.scheduleName;
    }

    public void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    public String getDescription() {
        return this.description;
    }


    public void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
    }


    public void setStartTime(final String hourString, final String minuteString, final String period) {
        setStartTime(CH.validateStringIntoTime(hourString, minuteString, period));
    }



    public void setEndTime(final LocalTime endTime) {
        this.endTime = endTime;
    }


    public void setEndTime(final String hourString, final String minuteString, final String period) {
        setEndTime(CH.validateStringIntoTime(hourString, minuteString, period));
    }



    public LocalTime getStartTime() {
        return this.startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public String getScheduleId() {
        return this.id;
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

    public void addReference(final ScheduleEvent event) {
        this.references.add(event);
    }

    public void removeReference(final ScheduleEvent event) {
        this.references.remove(event);
    }


    public List<String> getWeekdays() {
        final List<String> stringList = new ArrayList<>();

        Collections.sort(weekdays);
        for (final Weekday weekday : weekdays) {
            stringList.add(weekday.name());
        }

        return stringList;

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Schedule schedule = (Schedule) o;
        return this.id.equals(schedule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleName, startTime, description, id);
    }
}
