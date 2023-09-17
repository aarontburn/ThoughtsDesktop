package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ICal extends AnchorPane{


    /*  Components */
    private TextField nameTextField, urlTextField;

    public ICal() {

        this.setStyle("-fx-padding: 0 0 32 0;");

        final Label iCalNameLabel = new Label("iCal Name");
        this.getChildren().add(ThoughtsHelper.setAnchor(iCalNameLabel, 4, null, 8, null));

        final Label iCalUrlLabel = new Label("iCal URL");
        this.getChildren().add(ThoughtsHelper.setAnchor(iCalUrlLabel, 84, null, 64, null));

        nameTextField = new TextField();
        this.getChildren().add(ThoughtsHelper.setAnchor(nameTextField, 36, null, 8, 8));

        urlTextField = new TextField();
        this.getChildren().add(ThoughtsHelper.setAnchor(urlTextField, 120, null, 64, 8));
    }







}

