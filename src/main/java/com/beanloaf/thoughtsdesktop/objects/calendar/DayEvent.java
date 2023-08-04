package com.beanloaf.thoughtsdesktop.objects.calendar;

import javafx.scene.control.Label;

public class DayEvent {

    private String eventName;

    private Label label;

    public DayEvent(final String eventName) {
        label = new Label(eventName);

        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }

    public Label getLabel() {
        return this.label;
    }





}
