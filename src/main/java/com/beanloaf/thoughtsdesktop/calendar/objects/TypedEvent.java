package com.beanloaf.thoughtsdesktop.calendar.objects;

public interface TypedEvent {

    enum Types {
        BASIC, DAY, SCHEDULE, CANVAS

    }

    Types getEventType();



}
