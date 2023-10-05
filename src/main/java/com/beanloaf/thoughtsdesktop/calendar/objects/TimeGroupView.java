package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.robot.Robot;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeGroupView {

    private final TextField hourInput, minuteInput;
    private final ComboBox<String> periodSelector;

    private boolean ready;

    public TimeGroupView(final TextField hourInput, final TextField minuteInput, final ComboBox<String> periodSelector) {

        this.hourInput = CH.setNumbersOnlyTextField(hourInput);
        this.minuteInput = CH.setNumbersOnlyTextField(minuteInput);
        this.periodSelector = CH.setAMPMComboBox(periodSelector);


        attachEvents();
    }

    private void attachEvents() {
        hourInput.textProperty().addListener((observableValue, s, value) -> {
            if (value.isEmpty()) {
                return;
            }

            try {
                final int hour = Integer.parseInt(value);
                this.periodSelector.setDisable(hour > 12);

            } catch (NumberFormatException e) {
                Logger.log("Failed to convert " + value + " to an integer.");
            }

            if (value.length() >= 2) {
                minuteInput.requestFocus();
            }
        });

        minuteInput.textProperty().addListener((observableValue, s, value) -> {
            if (value.length() >= 2 && ready) {
                final Robot robot = new Robot();

                robot.keyPress(KeyCode.TAB);
                robot.keyPress(KeyCode.TAB);
            }
        });

    }

    public void setTime(final LocalTime time) {
        if (time == null) {
            hourInput.setText("");
            minuteInput.setText("");
            periodSelector.getSelectionModel().select("PM");
            return;
        }

        hourInput.setText(time.format(DateTimeFormatter.ofPattern("hh")));

        ready = false;
        minuteInput.setText(time.format(DateTimeFormatter.ofPattern("mm")));
        ready = true;

        periodSelector.getSelectionModel().select(time.format(DateTimeFormatter.ofPattern("a")));
    }


    public void setDisabled(final boolean isDisabled) {
        hourInput.setDisable(isDisabled);
        minuteInput.setDisable(isDisabled);
        periodSelector.setDisable(isDisabled);
    }

    public LocalTime getTime() {
        return CH.validateStringIntoTime(hourInput.getText(), minuteInput.getText(), periodSelector.getSelectionModel().getSelectedItem());
    }


}
