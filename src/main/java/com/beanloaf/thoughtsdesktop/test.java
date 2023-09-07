package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.calendar.objects.Weekday;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.res.TC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class test {




    public static void main(String[] args) {

        final Year year = Year.of(2023);

        Logger.log(year);



    }






}
