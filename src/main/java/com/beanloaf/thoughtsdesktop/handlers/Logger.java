package com.beanloaf.thoughtsdesktop.handlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {


    // TODO: dump log output to a visual console or file

    public static void log(final Object text) {
        if (text instanceof Exception) {
            logException((Exception) text);
            return;
        }

        System.out.println(getTimeStamp() + text.toString());

    }

    public static void logException(final Exception e) {
        System.err.println(getTimeStamp() + e.getClass() + ": " + e.getLocalizedMessage());
        for (StackTraceElement traceElement : e.getStackTrace())
            System.err.println("\tat " + traceElement);

    }

    private static String getTimeStamp() {
        return "[" + new SimpleDateFormat("yyyy/MM/dd | HH:mm:ss").format(Calendar.getInstance().getTime()) + "] ";
    }
}
