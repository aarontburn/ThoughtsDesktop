package com.beanloaf.thoughtsdesktop.calendar.handlers;

import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CalendarJSONHandler {

    private final CalendarView view;

    private JSONObject root;
    private final Map<LocalDate, DayEvent> eventMap = new ConcurrentHashMap<>();

    public CalendarJSONHandler(final CalendarView view) {
        this.view = view;

        try {
            TC.Directories.CALENDAR_PATH.createNewFile();
            this.root = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(TC.Directories.CALENDAR_PATH.toPath())));

            if (root == null) root = new JSONObject();

        } catch (Exception e) {
            TC.Directories.CALENDAR_PATH.delete();
            try {
                TC.Directories.CALENDAR_PATH.createNewFile();
            } catch (Exception error) {
                Logger.log(e);
            }
            this.root = new JSONObject();
        }

        readCalendarJson();

    }

    private void readCalendarJson() {
        try {
            for (final Object o : root.keySet()) {
                final String year = (String) o;

                final JSONObject yearBranch = (JSONObject) root.get(year);
                for (final Object m : yearBranch.keySet()) {
                    final String month = (String) m;

                    final JSONObject monthBranch = (JSONObject) yearBranch.get(month);
                    for (final Object d : monthBranch.keySet()) {
                        final String dayNum = (String) d;

                        final JSONObject dayBranch = (JSONObject) monthBranch.get(dayNum);
                        for (final Object e : dayBranch.keySet()) {
                            final String eventID = (String) e;
                            Logger.log("reading " + eventID);
                            final JSONObject eventBranch = (JSONObject) dayBranch.get(eventID);

                            final String eventTitle = (String) eventBranch.get("Title");
                            final String description = (String) eventBranch.get("Description");
                            final String time = (String) eventBranch.get("Time");
                            final Boolean isCompleted = (Boolean) eventBranch.get("Completed");


                            final LocalDate eventDate = LocalDate.of(Integer.parseInt(year), Month.valueOf(month.toUpperCase(Locale.ENGLISH)), Integer.parseInt(dayNum));

                            final DayEvent event = new DayEvent(eventDate, eventTitle, eventID, view);
                            event.setDescription(description);
                            event.setCompleted(isCompleted != null ? isCompleted : false, false);

                            if (time == null || !time.contains(":")) {
                                event.setTime(null);
                            } else {
                                final String[] splitTime = time.split(":");
                                event.setTime(Integer.parseInt(splitTime[0]), Integer.parseInt(splitTime[1]));
                            }

                            this.eventMap.put(eventDate, event);

                        }
                    }
                }
            }


        } catch (Exception e) {
            // Delete and rerun?
            Logger.log(e);
        }


        for (final LocalDate date : eventMap.keySet()) {
            view.addEvent(date, eventMap.get(date));
        }

    }


    public void addEventToJson(final DayEvent event) {

        new Thread(() -> {
            final String year = String.valueOf(event.getDate().getYear());
            final String month = event.getDate().getMonth().toString();
            final String day = String.valueOf(event.getDate().getDayOfMonth());
            final boolean completed = event.isCompleted();


            JSONObject yearBranch = (JSONObject) root.get(year);

            if (yearBranch == null) {
                yearBranch = new JSONObject();
                root.put(year, yearBranch);
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

            JSONObject eventBranch = (JSONObject) dayBranch.get(event.getEventID());

            if (eventBranch == null) {
                eventBranch = new JSONObject();

                dayBranch.put(event.getEventID(), eventBranch);
            }

            final LocalTime time = event.getTime();


            eventBranch.put("Title", event.getEventTitle());
            eventBranch.put("Description", event.getDescription());
            eventBranch.put("Time", time != null ? time.getHour() + ":" + time.getMinute() : "");
            eventBranch.put("Completed", completed);

            saveJson();
        }).start();


    }

    public void removeEventFromJson(final DayEvent event) {
        new Thread(() -> {
            final String year = String.valueOf(event.getDate().getYear());
            final String month = event.getDate().getMonth().toString();
            final String day = String.valueOf(event.getDate().getDayOfMonth());


            JSONObject yearBranch = (JSONObject) root.get(year);
            JSONObject monthBranch = (JSONObject) yearBranch.get(month);
            JSONObject dayBranch = (JSONObject) monthBranch.get(day);

            dayBranch.remove(event.getEventID());

            if (dayBranch.size() == 0) {
                monthBranch.remove(day);
            }
            if (monthBranch.size() == 0) {
                yearBranch.remove(month);
            }

            if (yearBranch.size() == 0) {
                root.remove(year);
            }


            saveJson();

        }).start();
    }


    public void saveJson() {
        Logger.log("Saving calendar.json");
        try {
            TC.Directories.CALENDAR_PATH.createNewFile();

            try (FileOutputStream fWriter = new FileOutputStream(TC.Directories.CALENDAR_PATH)) {
                fWriter.write(root.toString().getBytes());
            }
        } catch (Exception e) {
            Logger.log(e);
        }


    }

    @Override
    public String toString() {
        return root.toString();
    }


}
