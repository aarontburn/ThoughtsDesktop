package com.beanloaf.thoughtsdesktop.calendar.objects;

import java.util.Random;

public class Colors {


    private final static String[] COLORS = new String[]{
            "green", "black", "blue", "navy", "indigo",
            "darkblue", "darkgreen", "darkred", "midnightblue", "navy",
            "purple"};

    private Colors() {

    }

    public static String getRandomColor() {
        return COLORS[new Random().nextInt(COLORS.length)];
    }

}
