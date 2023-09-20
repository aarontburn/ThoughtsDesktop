package com.beanloaf.thoughtsdesktop.calendar.enums;


public enum Weekday {

    SUNDAY("Su", "Sun", 0),
    MONDAY("M", "Mon", 1),
    TUESDAY("Tu", "Tue", 2),
    WEDNESDAY("W", "Wed", 3),
    THURSDAY("Th", "Thu", 4),
    FRIDAY("F", "Fri", 5),
    SATURDAY("Sa", "Sat", 6);

    private final String shortAbbreviation;
    private final String longAbbreviation;
    private final int dayOfWeek;

    Weekday(final String shortAbbreviation, final String longAbbreviation, final int dayOfWeek) {
        this.shortAbbreviation = shortAbbreviation;
        this.longAbbreviation = longAbbreviation;
        this.dayOfWeek = dayOfWeek;
    }

    public String getShortAbbreviation() {
        return this.shortAbbreviation;
    }

    public String getLongAbbreviation() {
        return this.longAbbreviation;
    }

    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    public static Weekday getWeekdayByString(String s) {
        s = s.toUpperCase();

        for (final Weekday weekday : Weekday.values()) {
            if (weekday.name().equals(s)) return weekday;
        }

        throw new IllegalArgumentException("Invalid weekday passed: " + s);
    }

    public static Weekday getWeekdayByDayOfWeek(final int dayOfWeek) {
        if (dayOfWeek == 7) return SUNDAY;
        if (dayOfWeek < 0 || dayOfWeek > 6)
            throw new IndexOutOfBoundsException("Day of week out of bounds 0 - 6: " + dayOfWeek);

        for (final Weekday weekday : Weekday.values()) {
            if (weekday.dayOfWeek == dayOfWeek) return weekday;
        }

        throw new IllegalArgumentException("How did you get here? " + dayOfWeek);
    }
}
