package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.enums.Keys;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class CanvasBoxItem extends AnchorPane {

    private final CalendarMain main;
    private final Label classNameLabel, colorLabel;
    private final Button randomizeColorButton;
    private final CheckBox hideCheckBox;
    private CanvasClass canvasClass;

    public CanvasBoxItem(final CalendarMain main, final CanvasClass canvasClass) {
        this.main = main;
        this.canvasClass = canvasClass;


        this.getStyleClass().add("schedule-box-item");


        classNameLabel = new Label(
                canvasClass.getClassName().equals(Keys.EVENTS.name()) ? "Personal Events" : canvasClass.getClassName());


        classNameLabel.setStyle("-fx-font-size: 18");
        this.getChildren().add(ThoughtsHelper.setAnchor(classNameLabel, 16, null, 16, null));


        final HBox colorHBox = new HBox(16);
        colorHBox.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().add(ThoughtsHelper.setAnchor(colorHBox, 40, null, 32, null));

        final Label colorTextLabel = new Label("Color: ");
        colorTextLabel.setStyle("-fx-font-size: 16;");
        colorHBox.getChildren().add(colorTextLabel);

        colorLabel = new Label();
        colorLabel.setPrefSize(24, 24);
        colorLabel.setStyle(
                "-fx-border-color: black; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-background-color: " + canvasClass.getColor());
        colorHBox.getChildren().add(colorLabel);

        randomizeColorButton = new Button("Randomize Color");
        randomizeColorButton.getStyleClass().add("calendar-button");
        colorHBox.getChildren().add(randomizeColorButton);


        final HBox hideHBox = new HBox(8);
        this.getChildren().add(ThoughtsHelper.setAnchor(hideHBox, 76    , 16, 32, null));
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

    public void setCanvasClass(final CanvasClass canvasClass) {
        this.canvasClass = canvasClass;
        colorLabel.setStyle(
                "-fx-border-color: black; " +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5; " +
                        "-fx-background-color: " + canvasClass.getColor());
    }

    private void attachEvents() {
        hideCheckBox.setOnAction(e -> {
            canvasClass.setHidden(hideCheckBox.isSelected());
            main.getRightPanel().getMonthView().hideCanvasEventsByClass(canvasClass, hideCheckBox.isSelected());
        });

        randomizeColorButton.setOnAction(e -> {
            final String newColor = CH.getRandomColor();

            canvasClass.setColor(newColor);

            colorLabel.setStyle(
                    "-fx-border-color: black; " +
                            "-fx-border-radius: 5; " +
                            "-fx-background-radius: 5; " +
                            "-fx-background-color: " + newColor);

            for (final String uid : canvasClass.getUidList()) {
                final BasicEvent event = canvasClass.getEvent(uid);
                event.setDisplayColor(newColor);
            }

            main.getCanvasICalHandler().cacheCanvasEventsToJson();

        });
    }


    public void hideCanvasEvents(final boolean isHidden) {
        hideCheckBox.setSelected(isHidden);
        canvasClass.setHidden(isHidden);
        main.getRightPanel().getMonthView().hideCanvasEventsByClass(canvasClass, isHidden);

    }


    public CheckBox getHideCheckBox() {
        return this.hideCheckBox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CanvasBoxItem that = (CanvasBoxItem) o;
        return Objects.equals(canvasClass.getClassName(), that.canvasClass.getClassName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(canvasClass);
    }
}
