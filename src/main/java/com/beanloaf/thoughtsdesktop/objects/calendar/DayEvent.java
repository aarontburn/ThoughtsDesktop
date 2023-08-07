package com.beanloaf.thoughtsdesktop.objects.calendar;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.views.CalendarView;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class DayEvent extends Label {

    public final static String ID = "dayEvent";

    private final CalendarView view;

    private String eventName;




    // TODO: Generate unique eventID

    public DayEvent(final DayEvent dayEvent, final CalendarView view) {
        this(dayEvent.getEventName(), view);
    }


    public DayEvent(final String eventName, final CalendarView view) {
        super(eventName);
        this.view = view;


        this.setMaxWidth(Double.MAX_VALUE);
        this.eventName = eventName;
        this.setId(ID);

        this.getChildren().addListener((ListChangeListener<Node>) change -> {
            getChildren().get(0).setId(ID);
        });

        this.setOnMouseClicked(e -> {
            view.selectEvent(this);

            Logger.log("Event \"" + eventName + "\" was pressed.");
        });

    }

    public String getEventName() {
        return this.eventName;
    }






}
