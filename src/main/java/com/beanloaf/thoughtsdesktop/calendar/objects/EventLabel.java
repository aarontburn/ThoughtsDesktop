package com.beanloaf.thoughtsdesktop.calendar.objects;

import java.time.LocalDate;
import java.time.LocalTime;


/**
 * General class to apply to any visual component that has a reference to an event.
 * */
public interface EventLabel {

    void updateEventTitle(final String title);
    void updateDescription(final String description);
    void updateStartDate(final LocalDate date);
    void updateEndDate(final LocalDate date);
    void updateStartTime(final LocalTime time);
    void updateEndTime(final LocalTime time);
    void updateCompletion(final boolean isComplete);
    void updateDisplayColor(final String color);


}
