package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;

public enum Weekday {

    SUNDAY("Su"),
    MONDAY("M"),
    TUESDAY("Tu"),
    WEDNESDAY("W"),
    THURSDAY("Th"),
    FRIDAY("F"),
    SATURDAY("Sa");

    private final String abbreviation;

    Weekday(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public static Weekday getWeekdayByString(String s) {
        s = s.toUpperCase();

        for (final Weekday weekday : Weekday.values()) {
            if (weekday.name().equals(s)) return weekday;
        }

        throw new IllegalArgumentException("Invalid weekday passed: " + s);

    }



}
