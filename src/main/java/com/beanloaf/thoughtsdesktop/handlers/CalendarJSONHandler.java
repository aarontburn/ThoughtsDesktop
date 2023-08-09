package com.beanloaf.thoughtsdesktop.handlers;

import com.beanloaf.thoughtsdesktop.objects.calendar.DayEvent;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.util.Pair;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.Month;
import java.util.Set;

public class CalendarJSONHandler {

    private JSONObject data;

    public CalendarJSONHandler() {
        try {
            TC.Directories.CALENDAR_PATH.createNewFile();
            this.data = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(TC.Directories.CALENDAR_PATH.toPath())));

            if (data == null) data = new JSONObject();

        } catch (Exception e) {
            TC.Directories.CALENDAR_PATH.delete();
            try {
                TC.Directories.CALENDAR_PATH.createNewFile();
            } catch (Exception error) {
                Logger.log(e);
            }

            this.data = new JSONObject();
        }


    }

    public boolean validJson() {
        return data != null;

    }

    public JSONObject getBranch(final String branchName) {
        return (JSONObject) data.get(branchName);
    }

    public Set getKeys() {
        return data.keySet();
    }

    public JSONObject getJson() {
        return this.data;
    }

    public void addEvent(DayEvent event) {

        final String year = String.valueOf(event.getCalendarDay().getYear());
        final String month = event.getCalendarDay().getMonth().toString();
        final String day = String.valueOf(event.getCalendarDay().getDay());


        JSONObject yearBranch = (JSONObject) data.get(year);

        if (yearBranch == null) {
            yearBranch = new JSONObject();
            data.put(year, yearBranch);
        }

        JSONObject monthBranch = (JSONObject) yearBranch.get(month);

        if (monthBranch == null) {
            monthBranch = new JSONObject();
            yearBranch.put(month, monthBranch);
        }

        JSONObject dayBranch = (JSONObject) monthBranch.get(day);

        if (dayBranch == null) {
            dayBranch = new JSONObject();
            monthBranch.put(day, dayBranch);
        }

        JSONObject eventBranch = (JSONObject) dayBranch.get(event.getEventName());

        if (eventBranch == null) {
            eventBranch = new JSONObject();

            dayBranch.put(event.getEventName(), eventBranch);
        }

        eventBranch.put("Description", event.getDescription());

        final LocalTime time = event.getTime();


        eventBranch.put("Time", time != null ? time.getHour() + ":" + time.getMinute() : "");

        saveJson();

    }


    public void saveJson() {

        Logger.log("saving json");
        try {
            TC.Directories.CALENDAR_PATH.createNewFile();

            try (FileOutputStream fWriter = new FileOutputStream(TC.Directories.CALENDAR_PATH)) {
                fWriter.write(data.toString().getBytes());
            }
        } catch (Exception e) {
            Logger.log(e);
        }



    }

    @Override
    public String toString() {
        return data.toString();
    }









}
