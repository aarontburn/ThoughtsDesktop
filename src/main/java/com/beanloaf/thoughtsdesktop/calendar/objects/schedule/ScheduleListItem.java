package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;
import com.beanloaf.thoughtsdesktop.calendar.views.children.overlays.ScheduleOverlay;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScheduleListItem extends GridPane {


    private final ScheduleOverlay tab;
    private final Map<Weekday, CheckBox> checkBoxMap = new HashMap<>();
    private final List<Weekday> weekdays = new ArrayList<>();
    private final BasicEvent event;
    private final Label displayText;
    private final AnchorPane repeatSettingsPane;
    private final List<ScheduleLabel> references = new ArrayList<>();


    private final Map<RepeatTab, Node> repeatTabMap = new HashMap<>();
    private RepeatTab currentRepeatMode;

    public enum RepeatTab {
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        YEARLY("Yearly");

        private final String name;

        RepeatTab(final String stringName) {
            this.name = stringName;
        }


    }

    public ScheduleListItem(final ScheduleOverlay tab, final String scheduleName) {
        this(tab, scheduleName, UUID.randomUUID().toString());

    }

    public ScheduleListItem(final ScheduleOverlay tab, final String scheduleName, final String id) {
        this(tab, new BasicEvent(scheduleName).setId(id).setEventType(TypedEvent.Types.SCHEDULE));
    }


    public ScheduleListItem(final ScheduleOverlay tab, final BasicEvent event) {
        super();
        this.tab = tab;
        this.event = event;

        this.setGridLinesVisible(true);
        this.getStyleClass().add("schedule");


        displayText = new Label(event.getTitle());
        displayText.setStyle("-fx-font-family: Lato; -fx-font-size: 18; -fx-padding: 0 5 0 2");
        displayText.setAlignment(Pos.TOP_LEFT);
        displayText.setWrapText(true);
        displayText.setMaxHeight(10000);
        displayText.setMaxWidth(10000);
        this.add(displayText, 0, 0, 1, 2);


        final HBox repeatTypeHBox = new HBox(8);
        repeatTypeHBox.setAlignment(Pos.CENTER_LEFT);
        this.add(repeatTypeHBox, 1, 0);


        repeatTypeHBox.getChildren().add(CH.setNodeStyle(new Label("Repeat"), "-fx-font-family: Lato; -fx-font-size: 18;"));
        final ComboBox<String> repeatTypeComboBox = CH.setStringComboBoxValues(new ComboBox<>(), RepeatTab.WEEKLY.name, RepeatTab.MONTHLY.name, RepeatTab.YEARLY.name);
        repeatTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, swappedTo) -> {
            if (swappedTo != null) {
                swapRepeatTab(RepeatTab.valueOf(swappedTo.toUpperCase()));
            }
        });
        repeatTypeHBox.getChildren().add(repeatTypeComboBox);
        repeatTypeHBox.getChildren().add(CH.setNodeStyle(new Label("every"), "-fx-font-family: Lato; -fx-font-size: 18;"));


        final TextField repeatSpacingTextField = new TextField("1");
        repeatSpacingTextField.setPrefWidth(45);
        repeatTypeHBox.getChildren().add(repeatSpacingTextField);

        repeatTypeHBox.getChildren().add(CH.setNodeStyle(new Label("week(s)."), "-fx-font-family: Lato; -fx-font-size: 18;"));


        final ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setPercentWidth(35);
        leftCol.setMinWidth(10);
        leftCol.setHgrow(Priority.SOMETIMES);

        final ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setPercentWidth(65);
        rightCol.setMinWidth(10);
        rightCol.setHgrow(Priority.SOMETIMES);

        getColumnConstraints().add(leftCol);
        getColumnConstraints().add(rightCol);


        repeatSettingsPane = new AnchorPane();
        this.add(repeatSettingsPane, 1, 1);
        createTabs();

        swapRepeatTab(RepeatTab.WEEKLY);


        this.setOnMouseClicked(e -> doClick());

    }

    private void swapRepeatTab(final RepeatTab tab) {
        for (final RepeatTab t : repeatTabMap.keySet()) {
            repeatTabMap.get(t).setVisible(false);
            repeatTabMap.get(t).setManaged(false);
        }

        currentRepeatMode = tab;
        repeatTabMap.get(tab).setVisible(true);
        repeatTabMap.get(tab).setManaged(true);

    }


    private void createTabs() {
        /* Week Tab */
        final AnchorPane weekPane = new AnchorPane();
        weekPane.setStyle("-fx-border-color: blue;");
        repeatTabMap.put(RepeatTab.WEEKLY, weekPane);
        repeatSettingsPane.getChildren().add(ThoughtsHelper.setAnchor(weekPane, 0, 0, 0, 0));

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
                    if (!weekdays.contains(weekday)) {
                        weekdays.add(weekday);
                    }
                } else {
                    tab.removeScheduleFromDay(weekday, this);
                    while (weekdays.contains(weekday)) {
                        weekdays.remove(weekday);
                    }
                }

            });


            checkBoxMap.put(weekday, checkBox);

            weekdayPane.add(checkBox, i, 1);
            weekdayPane.getColumnConstraints().add(weekendColumnConstraints);
        }
        weekPane.getChildren().add(ThoughtsHelper.setAnchor(weekdayPane, 0, 0, 0, 0));

        final RowConstraints firstRow = new RowConstraints();
        firstRow.percentHeightProperty().setValue(40);
        weekdayPane.getRowConstraints().add(firstRow);

        final RowConstraints secondRow = new RowConstraints();
        secondRow.percentHeightProperty().setValue(60);
        weekdayPane.getRowConstraints().add(secondRow);

        /* Monthly */
        final AnchorPane monthPane = new AnchorPane();
        monthPane.setStyle("-fx-border-color: green;");
        repeatTabMap.put(RepeatTab.MONTHLY, monthPane);
        repeatSettingsPane.getChildren().add(ThoughtsHelper.setAnchor(monthPane, 0, 0, 0, 0));

        final ToggleGroup monthlyToggleGroup = new ToggleGroup();



        final HBox monthlyDayHBox = new HBox(8);
        monthlyDayHBox.setAlignment(Pos.CENTER_LEFT);
        monthPane.getChildren().add(ThoughtsHelper.setAnchor(monthlyDayHBox, 4, null, 8, null));

        final RadioButton monthlyDayRadioButton = new RadioButton();
        monthlyDayRadioButton.setToggleGroup(monthlyToggleGroup);
        monthlyDayRadioButton.setStyle("-fx-font-size: 10;");

        monthlyDayHBox.getChildren().add(monthlyDayRadioButton);

        monthlyDayHBox.getChildren().add(CH.setNodeStyle(new Label("On the"), "-fx-font-family: Lato; -fx-font-size: 18;"));
        final TextField monthlyDayTextField = new TextField("1");
        monthlyDayTextField.setPrefWidth(45);
        monthlyDayHBox.getChildren().add(monthlyDayTextField);




        final HBox monthlyWeekdayHBox = new HBox(8);
        monthlyWeekdayHBox.setAlignment(Pos.CENTER_LEFT);
        monthPane.getChildren().add(ThoughtsHelper.setAnchor(monthlyWeekdayHBox, 40, 4, 8, null));

        final RadioButton monthlyWeekdayRadioButton = new RadioButton();
        monthlyWeekdayRadioButton.setToggleGroup(monthlyToggleGroup);
        monthlyWeekdayRadioButton.setStyle("-fx-font-size: 10;");
        monthlyWeekdayHBox.getChildren().add(monthlyWeekdayRadioButton);

        monthlyWeekdayHBox.getChildren().add(CH.setNodeStyle(new Label("On the"), "-fx-font-family: Lato; -fx-font-size: 18;"));

        final ComboBox<String> weekNumSelector = CH.setStringComboBoxValues(new ComboBox<>(),
                "first", "second", "third", "fourth", "last");
        weekNumSelector.setPrefWidth(100);
        monthlyWeekdayHBox.getChildren().add(weekNumSelector);

        final ComboBox<String> weekDaySelector = CH.setStringComboBoxValues(new ComboBox<>(),
                Weekday.getFullWeekdayNames().toArray(new String[0]));
        weekDaySelector.setPrefWidth(100);
        monthlyWeekdayHBox.getChildren().add(weekDaySelector);



        /*  Yearly  */
        final AnchorPane yearPane = new AnchorPane();
        yearPane.setStyle("-fx-border-color: red;");
        repeatTabMap.put(RepeatTab.YEARLY, yearPane);
        repeatSettingsPane.getChildren().add(ThoughtsHelper.setAnchor(yearPane, 0, 0, 0, 0));

    }


    public void doClick() {
        tab.setInputFields(this);
    }

    public BasicEvent getEvent() {
        return this.event;
    }

    public List<Weekday> getWeekdays() {
        return this.weekdays;
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

    public static class ScheduleLabel extends Label {

        private final ScheduleListItem scheduleListItem;
        private final Tooltip tooltip;


        public ScheduleLabel(final ScheduleListItem scheduleListItem) {
            super("");
            setGraphic(new ImageView(new Image(String.valueOf(MainApplication.class.getResource("icons/schedule-icon.png")), 17.5, 17.5, true, true)));
            this.scheduleListItem = scheduleListItem;

            this.getStyleClass().add("day-event");
            this.setStyle("-fx-border-color: -fx-gray-0;");
            this.setMaxWidth(Double.MAX_VALUE);


            tooltip = new Tooltip();
            tooltip.setShowDelay(Duration.seconds(0.5));
            tooltip.setText(scheduleListItem.getScheduleEventName());
            this.setTooltip(tooltip);

            updateText();
            this.setOnMouseClicked(e -> onClick());

        }

        public void onClick() {
            Logger.log("Schedule \"" + this.scheduleListItem.getScheduleEventName() + "\" was pressed.");
            scheduleListItem.doClick();
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
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            final ScheduleLabel that = (ScheduleLabel) other;
            return getScheduleId().equals(that.getScheduleId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(scheduleListItem);
        }


    }


}
