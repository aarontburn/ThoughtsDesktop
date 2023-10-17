package com.beanloaf.thoughtsdesktop.calendar.objects.schedule;

import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;
import com.beanloaf.thoughtsdesktop.calendar.views.children.overlays.ScheduleOverlay;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class ScheduleListItem extends GridPane {
    private final static String TEXT_STYLE = "-fx-font-family: Lato; -fx-font-size: 18;";
    private final static String RADIO_BUTTON_STYLE = "-fx-font-size: 10;";


    private final ScheduleOverlay tab;
    private final Map<Weekday, CheckBox> checkBoxMap = new HashMap<>();
    private final List<Weekday> weekdays = new ArrayList<>();
    private final BasicEvent event;
    private final Label displayText, displayRepeatMode;
    private final AnchorPane repeatSettingsPane;


    private final Map<RepeatTab, Node> repeatTabMap = new HashMap<>();
    private RepeatTab currentRepeatMode;

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

        this.getStyleClass().add("schedule");
        this.setVgap(5);


        displayText = new Label(event.getTitle());
        displayText.setStyle(TEXT_STYLE + " -fx-padding: 0 5 0 2");
        displayText.setAlignment(Pos.TOP_LEFT);
        displayText.setWrapText(true);
        displayText.setMaxHeight(10000);
        displayText.setMaxWidth(10000);
        this.add(displayText, 0, 0, 1, 2);


        final HBox repeatTypeHBox = new HBox(8);
        repeatTypeHBox.setStyle("-fx-padding: 5 0 0 0");
        repeatTypeHBox.setAlignment(Pos.CENTER_LEFT);
        this.add(repeatTypeHBox, 1, 0);
        repeatTypeHBox.getChildren().add(CH.setNodeStyle(new Label("Repeat"), TEXT_STYLE));

        final ComboBox<String> repeatTypeComboBox = CH.setStringComboBoxValues(new ComboBox<>(), RepeatTab.WEEKLY.name, RepeatTab.MONTHLY.name, RepeatTab.YEARLY.name);
        repeatTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, swappedTo) -> {
            if (swappedTo != null) {
                swapRepeatTab(RepeatTab.valueOf(swappedTo.toUpperCase()));
            }
        });
        repeatTypeComboBox.setPrefHeight(32);
        repeatTypeHBox.getChildren().add(repeatTypeComboBox);
        repeatTypeHBox.getChildren().add(CH.setNodeStyle(new Label("every"), TEXT_STYLE));


        final TextField repeatSpacingTextField = new TextField("1");
        repeatSpacingTextField.setPrefWidth(45);
        repeatTypeHBox.getChildren().add(repeatSpacingTextField);

        displayRepeatMode = (Label) CH.setNodeStyle(new Label("week(s)."), TEXT_STYLE);
        repeatTypeHBox.getChildren().add(displayRepeatMode);


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


        switch (tab) {
            case WEEKLY ->  displayRepeatMode.setText("week(s).");
            case MONTHLY ->  displayRepeatMode.setText("month(s).");
            case YEARLY ->  displayRepeatMode.setText("year(s).");
            default -> throw new IllegalArgumentException("Invalid tab passed: " + tab);

        }



    }

    private void createTabs() {
        /* Week Tab */
        final AnchorPane weekPane = new AnchorPane();
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
                    if (!weekdays.contains(weekday)) {
                        weekdays.add(weekday);
                    }
                } else {
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
        repeatTabMap.put(RepeatTab.MONTHLY, monthPane);
        repeatSettingsPane.getChildren().add(ThoughtsHelper.setAnchor(monthPane, 0, 0, 0, 0));

        final ToggleGroup monthlyToggleGroup = new ToggleGroup();
        final HBox monthlyDayHBox = new HBox(8);
        monthlyDayHBox.setAlignment(Pos.CENTER_LEFT);
        monthPane.getChildren().add(ThoughtsHelper.setAnchor(monthlyDayHBox, 4, null, 8, null));

        final RadioButton monthlyDayRadioButton = new RadioButton();
        monthlyDayRadioButton.setSelected(true);
        monthlyDayRadioButton.setToggleGroup(monthlyToggleGroup);
        monthlyDayRadioButton.setStyle(RADIO_BUTTON_STYLE);

        monthlyDayHBox.getChildren().add(monthlyDayRadioButton);

        monthlyDayHBox.getChildren().add(CH.setNodeStyle(new Label("On the"), TEXT_STYLE));
        final TextField monthlyDayTextField = new TextField("1");
        monthlyDayTextField.setPrefWidth(45);
        monthlyDayHBox.getChildren().add(monthlyDayTextField);


        final HBox monthlyWeekdayHBox = new HBox(8);
        monthlyWeekdayHBox.setAlignment(Pos.CENTER_LEFT);
        monthPane.getChildren().add(ThoughtsHelper.setAnchor(monthlyWeekdayHBox, 40, 4, 8, null));

        final RadioButton monthlyWeekdayRadioButton = new RadioButton();
        monthlyWeekdayRadioButton.setToggleGroup(monthlyToggleGroup);
        monthlyWeekdayRadioButton.setStyle(RADIO_BUTTON_STYLE);
        monthlyWeekdayHBox.getChildren().add(monthlyWeekdayRadioButton);

        monthlyWeekdayHBox.getChildren().add(CH.setNodeStyle(new Label("On the"), TEXT_STYLE));

        final ComboBox<String> monthWeekNumSelector = CH.setStringComboBoxValues(new ComboBox<>(),
                "first", "second", "third", "fourth", "last");
        monthWeekNumSelector.setPrefWidth(100);
        monthWeekNumSelector.setPrefHeight(32);

        monthlyWeekdayHBox.getChildren().add(monthWeekNumSelector);

        final ComboBox<String> monthWeekDaySelector = CH.setStringComboBoxValues(new ComboBox<>(),
                Weekday.getFullWeekdayNames().toArray(new String[0]));
        monthWeekDaySelector.setPrefHeight(32);

        monthWeekDaySelector.setPrefWidth(100);
        monthlyWeekdayHBox.getChildren().add(monthWeekDaySelector);



        /*  Yearly  */
        final AnchorPane yearPane = new AnchorPane();
        repeatTabMap.put(RepeatTab.YEARLY, yearPane);
        repeatSettingsPane.getChildren().add(ThoughtsHelper.setAnchor(yearPane, 0, 0, 0, 0));

        final HBox yearlyMonthHBox = new HBox(8);
        yearlyMonthHBox.setAlignment(Pos.CENTER_LEFT);
        yearPane.getChildren().add(ThoughtsHelper.setAnchor(yearlyMonthHBox, 4, null, 8, null));
        yearlyMonthHBox.getChildren().add(CH.setNodeStyle(new Label("In"), TEXT_STYLE));


        final List<String> monthList = new ArrayList<>();
        for (final Month month : Month.values()) {
            monthList.add(ThoughtsHelper.toCamelCase(month.toString()));
        }
        final ComboBox<String> yearMonthSelector = CH.setStringComboBoxValues(new ComboBox<>(),
                monthList.toArray(new String[0]));
        yearMonthSelector.setPrefWidth(150);
        yearMonthSelector.setPrefHeight(32);

        yearlyMonthHBox.getChildren().add(yearMonthSelector);


        final ToggleGroup yearlyToggleGroup = new ToggleGroup();
        final HBox yearlyDayHBox = new HBox(8);
        yearlyDayHBox.setAlignment(Pos.CENTER_LEFT);
        yearPane.getChildren().add(ThoughtsHelper.setAnchor(yearlyDayHBox, 40, null, 8, null));

        final RadioButton yearlyDayRadioButton = new RadioButton();
        yearlyDayRadioButton.setSelected(true);
        yearlyDayRadioButton.setToggleGroup(yearlyToggleGroup);
        yearlyDayRadioButton.setStyle(RADIO_BUTTON_STYLE);
        yearlyDayHBox.getChildren().add(yearlyDayRadioButton);

        yearlyDayHBox.getChildren().add(CH.setNodeStyle(new Label("On the"), TEXT_STYLE));
        final TextField yearlyDayTextField = new TextField("1");
        yearlyDayTextField.setPrefWidth(45);
        yearlyDayHBox.getChildren().add(yearlyDayTextField);


        final HBox yearlyWeekdayHBox = new HBox(8);
        yearlyWeekdayHBox.setAlignment(Pos.CENTER_LEFT);
        yearPane.getChildren().add(ThoughtsHelper.setAnchor(yearlyWeekdayHBox, 76, 4, 8, null));

        final RadioButton yearlyWeekdayRadioButton = new RadioButton();
        yearlyWeekdayRadioButton.setToggleGroup(yearlyToggleGroup);
        yearlyWeekdayRadioButton.setStyle(RADIO_BUTTON_STYLE);
        yearlyWeekdayHBox.getChildren().add(yearlyWeekdayRadioButton);

        yearlyWeekdayHBox.getChildren().add(CH.setNodeStyle(new Label("On the"), TEXT_STYLE));

        final ComboBox<String> yearWeekNumSelector = CH.setStringComboBoxValues(new ComboBox<>(),
                "first", "second", "third", "fourth", "last");
        yearWeekNumSelector.setPrefWidth(100);
        yearWeekNumSelector.setPrefHeight(32);

        yearlyWeekdayHBox.getChildren().add(yearWeekNumSelector);

        final ComboBox<String> yearWeekDaySelector = CH.setStringComboBoxValues(new ComboBox<>(),
                Weekday.getFullWeekdayNames().toArray(new String[0]));
        yearWeekDaySelector.setPrefWidth(100);
        yearWeekDaySelector.setPrefHeight(32);

        yearlyWeekdayHBox.getChildren().add(yearWeekDaySelector);


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

    public String getScheduleEventName() {
        return event.getTitle();
    }

    public void setScheduleEventName(final String newName) {
        event.setTitle(newName);
        displayText.setText(newName);

    }

    public String getDescription() {
        return event.getDescription();
    }

    public void setDescription(final String newDescription) {
        event.setDescription(newDescription);
    }

    public LocalTime getStartTime() {
        return event.getStartTime();
    }

    public void setStartTime(final LocalTime startTime) {
        this.event.setStartTime(startTime);
    }

    public LocalTime getEndTime() {
        return event.getEndTime();
    }

    public void setEndTime(final LocalTime endTime) {
        this.event.setEndTime(endTime);
    }

    @Override
    public String toString() {
        return "ScheduleListItem {" +
                "weekdays=" + weekdays +
                ", event=" + event +
                '}';
    }


    public enum RepeatTab {
        WEEKLY("weekly"),
        MONTHLY("monthly"),
        YEARLY("yearly");

        private final String name;

        RepeatTab(final String stringName) {
            this.name = stringName;
        }


    }



}
