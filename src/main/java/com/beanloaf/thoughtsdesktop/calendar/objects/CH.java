package com.beanloaf.thoughtsdesktop.calendar.objects;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

public class CH {


    private static final Random rand = new Random();
    private static final int MAX_RGB = 128;

    private CH() {

    }


    public static ComboBox<String> setStringComboBoxValues(final ComboBox<String> comboBox, String... values) {
        comboBox.getItems().clear();
        comboBox.getItems().addAll(values);
        Platform.runLater(() -> comboBox.getSelectionModel().select(values[0]));
        return comboBox;
    }

    public static ComboBox<String> setAMPMComboBox(final ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        comboBox.getItems().addAll("AM", "PM");
        Platform.runLater(() -> comboBox.getSelectionModel().select("PM"));
        return comboBox;
    }

    public static TextField setNumbersOnlyTextField(final TextField textField) {
        textField.textProperty().addListener((observableValue, s, value) -> {
            if (!value.matches("\\d*")) {
                textField.setText(value.replaceAll("\\D", ""));
            }

        });

        return textField;
    }


    public static LocalTime validateStringIntoTime(final String hourString, final String minuteString, final String period) {
        try {
            int hour = Integer.parseInt(hourString);
            final int minute = minuteString.isEmpty() ? 0 : Integer.parseInt(minuteString);

            if (!(period.equals("AM") || period.equals("PM"))) {
                throw new IllegalArgumentException("Period needs to be AM or PM: " + period);
            }

            final boolean isPM = period.equals("PM");

            if (hour == 12 && !isPM) {
                hour = 0;
            } else if (hour < 12 && isPM) {
                hour += 12;
            }

            if (hour >= 24) {
                return null;
            }


            return LocalTime.of(hour, minute);

        } catch (NumberFormatException | DateTimeException e) {
            return null;
        }

    }


    /**
     * Generates a random DARK color, with a max value of any RGB of 128.
     * */
    public static String getRandomColor() {
        return String.format("rgb(%s, %s, %s)", rand.nextInt(MAX_RGB), rand.nextInt(MAX_RGB), rand.nextInt(MAX_RGB));
    }
}
