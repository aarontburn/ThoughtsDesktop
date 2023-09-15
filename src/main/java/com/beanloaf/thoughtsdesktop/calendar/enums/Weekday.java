package com.beanloaf.thoughtsdesktop.calendar.enums;


public enum Weekday {

    SUNDAY("Su", 0),
    MONDAY("M", 1),
    TUESDAY("Tu", 2),
    WEDNESDAY("W", 3),
    THURSDAY("Th", 4),
    FRIDAY("F", 5),
    SATURDAY("Sa", 6);

    private final String abbreviation;
    private final int dayOfWeek;

    Weekday(final String abbreviation, final int dayOfWeek) {
        this.abbreviation = abbreviation;
        this.dayOfWeek = dayOfWeek;
    }

    public String getAbbreviation() {
        return this.abbreviation;
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
        if (dayOfWeek < 0 || dayOfWeek > 6) throw new IndexOutOfBoundsException("Day of week out of bounds 0 - 6: " + dayOfWeek);

        for (final Weekday weekday : Weekday.values()) {
            if (weekday.dayOfWeek == dayOfWeek) return weekday;
        }

        throw new IllegalArgumentException("How did you get here? " + dayOfWeek);
    }
}
