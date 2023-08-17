package com.beanloaf.thoughtsdesktop.calendar.objects;

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
}
