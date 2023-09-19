package com.beanloaf.thoughtsdesktop.calendar.enums;

import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;

public enum Keys {
    EVENT_NAME,
    DAYS,
    START_TIME,
    END_TIME,
    DESCRIPTION,
    COMPLETED,
    SCHEDULE_NAME,
    START_DATE,
    END_DATE,
    ID,
    SCHEDULE_EVENTS,
    TITLE,
    DISPLAY_COLOR,
    ;


    @Override
    public String toString() {
        final StringBuilder keyName = new StringBuilder();

        for (final String s : name().split("_")) {
            keyName.append(ThoughtsHelper.toCamelCase(s)).append(" ");
        }

        return keyName.toString().trim();

    }

}