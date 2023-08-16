package com.beanloaf.thoughtsdesktop.calendar.objects;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CH {


    private CH() {
    }


    public static ComboBox<String> setAMPMComboBox(final ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        comboBox.getItems().addAll("AM", "PM");
        comboBox.getSelectionModel().select("PM");

        return comboBox;
    }

    public static TextField setNumbersOnlyTextField(final TextField textField) {
        textField.textProperty().addListener((observableValue, s, value) -> {
            if (!value.matches("\\d*")) textField.setText(value.replaceAll("\\D", ""));

        });


        return textField;
    }


    //TODO : Validate time here


}
