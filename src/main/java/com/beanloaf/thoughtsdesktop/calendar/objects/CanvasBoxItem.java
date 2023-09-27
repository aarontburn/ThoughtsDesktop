package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.enums.Keys;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class CanvasBoxItem extends AnchorPane {

    private final CalendarMain main;

    private CanvasClass canvasClass;


    private final Label classNameLabel, colorLabel;
    private final Button randomizeColorButton;
    private final CheckBox hideCheckBox;

    public CanvasBoxItem(final CalendarMain main, final CanvasClass canvasClass) {
        this.main = main;
        this.canvasClass = canvasClass;



        this.getStyleClass().add("schedule-box-item");


        classNameLabel = new Label(
                canvasClass.getClassName().equals(Keys.EVENTS.name()) ? "Personal Events" : canvasClass.getClassName());




        classNameLabel.setStyle("-fx-font-size: 18");
        this.getChildren().add(ThoughtsHelper.setAnchor(classNameLabel, 16, null, 16, null));


        final HBox colorHBox = new HBox(16);
        this.getChildren().add(ThoughtsHelper.setAnchor(colorHBox, 48, null, 32, null));

        final Label colorTextLabel = new Label("Color: ");
        colorTextLabel.setStyle("-fx-font-size: 16;");
        colorHBox.getChildren().add(colorTextLabel);

        colorLabel = new Label();
        colorLabel.setPrefSize(24, 24);
        colorLabel.setStyle("-fx-background-radius: 5; -fx-background-color: " + canvasClass.getColor());
        colorHBox.getChildren().add(colorLabel);

        randomizeColorButton = new Button("Randomize Color");
        randomizeColorButton.getStyleClass().add("calendarButton");
        colorHBox.getChildren().add(randomizeColorButton);




        final HBox hideHBox = new HBox(8);
        this.getChildren().add(ThoughtsHelper.setAnchor(hideHBox, 80, 16, 32, null));
        hideCheckBox = new CheckBox();
        hideHBox.getChildren().add(hideCheckBox);


        final Label hideLabel = new Label("Hide");
        hideLabel.setStyle("-fx-font-size: 16");
        hideHBox.getChildren().add(hideLabel);


        attachEvents();

    }

    public CanvasClass getCanvasClass() {
        return this.canvasClass;
    }

    private void attachEvents() {
        hideCheckBox.setOnAction(e -> {

        });

        randomizeColorButton.setOnAction(e -> {
            final String newColor = CH.getRandomColor();

            canvasClass.setColor(newColor);

            colorLabel.setStyle("-fx-background-radius: 5; -fx-background-color: " + newColor);

            for (final String uid : canvasClass.getUidList()) {
                final BasicEvent event = canvasClass.getEvent(uid);
                event.setDisplayColor(newColor);
            }

            main.getCanvasICalHandler().cacheCanvasEventsToJson();

        });
    }


    public void setCanvasClass(final CanvasClass canvasClass) {
        this.canvasClass = canvasClass;
        colorLabel.setStyle("-fx-background-radius: 5; -fx-background-color: " + canvasClass.getColor());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanvasBoxItem that = (CanvasBoxItem) o;
        return Objects.equals(canvasClass.getClassName(), that.canvasClass.getClassName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(canvasClass);
    }
}
