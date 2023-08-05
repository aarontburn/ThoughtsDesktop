package com.beanloaf.thoughtsdesktop.objects.calendar;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.Label;

public class DayEvent extends Label {

    public final static double DEFAULT_HEIGHT = 19;

    private String eventName;


    // TODO: Generate unique eventID

    private boolean isEventVisible;

    public DayEvent(final String eventName) {
        this(eventName, true);
    }


    public DayEvent(final String eventName, final boolean isVisible) {
        super(eventName);
        this.isEventVisible = isVisible;

        this.setMaxWidth(Double.MAX_VALUE);
        this.eventName = eventName;
        this.setOnMouseClicked(e -> Logger.log("Event \"" + eventName + "\" was pressed."));

    }

    public String getEventName() {
        return this.eventName;
    }


    public boolean isEventVisible() {
        return this.isEventVisible;
    }

    public void setEventVisibility(final boolean isVisible) {
        this.isEventVisible = isVisible;
    }


}
