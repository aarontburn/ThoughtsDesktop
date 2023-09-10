package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class EventBoxLabel extends Label {

    private String eventTitle;

    private final Tooltip tooltip;


    public EventBoxLabel(final String eventTitle) {
        super(eventTitle);

        this.eventTitle = eventTitle;

        tooltip = new Tooltip(eventTitle);
        tooltip.setShowDelay(Duration.seconds(0.5));
        this.setTooltip(tooltip);


        this.getStyleClass().add("day-event");
        this.setMaxWidth(Double.MAX_VALUE);

        this.setOnMouseClicked(e -> onClick());
    }

    public Tooltip getToolTip() {
        return this.tooltip;
    }

    public void setGraphic(final ImageView graphic) {
        super.setGraphic(graphic);
    }

    public String getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(final String title) {
        this.eventTitle = title;
    }

    public void onClick() {
        Logger.log("EventBoxLabel " + eventTitle + " clicked.");
    }








}
