package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.views.SchedulePopup;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.Objects;

public class ScheduleEvent extends Label {


    private final Schedule schedule;
    private final Weekday weekday;

    public ScheduleEvent(final Schedule schedule, final Weekday weekday) {
        super(schedule.getScheduleName());

        this.schedule = schedule;
        this.weekday = weekday;


        this.getStyleClass().add("day-event");
        this.setMaxWidth(Double.MAX_VALUE);

        final Tooltip tooltip = new Tooltip(schedule.getScheduleName());
        tooltip.setShowDelay(Duration.seconds(0.5));
        this.setTooltip(tooltip);



        this.setOnMouseClicked(e -> {
            Logger.log("Schedule \"" + this.schedule.getScheduleName() + "\" was pressed.");


            schedule.doClick();
        });

    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleEvent that = (ScheduleEvent) o;
        return Objects.equals(schedule, that.schedule) && Objects.equals(weekday, that.weekday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schedule);
    }
}
