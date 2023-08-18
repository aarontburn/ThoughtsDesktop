package com.beanloaf.thoughtsdesktop.calendar.handlers;

import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CalendarJSONHandler {


    private final CalendarView view;

    private JSONObject root;
    private final Map<LocalDate, List<DayEvent>> eventMap = new ConcurrentHashMap<>();

    private final List<ScheduleData> scheduleDataList = new ArrayList<>();

    public CalendarJSONHandler(final CalendarView view) {
        this.view = view;

        TC.Directories.CALENDAR_PATH.mkdir();
        TC.Directories.CALENDAR_SCHEDULES_PATH.mkdir();

        try {
            TC.Directories.CALENDAR_DATA_FILE.createNewFile();
            this.root = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(TC.Directories.CALENDAR_DATA_FILE.toPath())));

            if (root == null) root = new JSONObject();

        } catch (Exception e) {
            TC.Directories.CALENDAR_DATA_FILE.delete();
            try {
                TC.Directories.CALENDAR_DATA_FILE.createNewFile();
            } catch (Exception error) {
                Logger.log(e);
            }
            this.root = new JSONObject();
        }

        readCalendarJson();
        readSchedules();
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
                            final JSONObject eventBranch = (JSONObject) dayBranch.get(eventID);

                            final String eventTitle = (String) eventBranch.get(Keys.TITLE);
                            final String description = (String) eventBranch.get(Keys.DESCRIPTION);
                            final String startTime = (String) eventBranch.get(Keys.START_TIME);
                            final String endTime = (String) eventBranch.get(Keys.END_TIME);
                            final Boolean isCompleted = (Boolean) eventBranch.get(Keys.COMPLETED);


                            final LocalDate eventDate = LocalDate.of(Integer.parseInt(year), Month.valueOf(month.toUpperCase(Locale.ENGLISH)), Integer.parseInt(dayNum));

                            final DayEvent event = new DayEvent(eventDate, eventTitle, eventID, view);
                            event.setDescription(description);
                            event.setCompleted(isCompleted != null ? isCompleted : false, false);

                            if (startTime == null) {
                                event.setStartTime(null);
                            } else {
                                try {
                                    final LocalTime parsedStartTime = LocalTime.parse(startTime);
                                    event.setStartTime(parsedStartTime);
                                } catch (DateTimeParseException parseException) {
                                    event.setStartTime(null);
                                }
                            }


                            if (endTime == null) {
                                event.setEndTime(null);
                            } else {
                                try {
                                    final LocalTime parsedEndTime = LocalTime.parse(endTime);
                                    event.setEndTime(parsedEndTime);
                                } catch (DateTimeParseException parseException) {
                                    event.setEndTime(null);
                                }
                            }


                            List<DayEvent> dayEventList = eventMap.get(eventDate);
                            if (dayEventList == null) {
                                dayEventList = new ArrayList<>();
                                this.eventMap.put(eventDate, dayEventList);
                            }
                            dayEventList.add(event);

                        }
                    }
                }
            }


        } catch (Exception e) {
            // Delete and rerun?
            Logger.log(e);
        }

        for (final LocalDate date : eventMap.keySet()) {
            for (final DayEvent event : eventMap.get(date)) {
                view.addEvent(date, event);
            }
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

            final LocalTime startTime = event.getStartTime();
            final LocalTime endTime = event.getEndTime();


            eventBranch.put(Keys.TITLE, event.getEventTitle());
            eventBranch.put(Keys.DESCRIPTION, event.getDescription());
            eventBranch.put(Keys.START_TIME, startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
            eventBranch.put(Keys.END_TIME, endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
            eventBranch.put(Keys.COMPLETED, completed);

            saveCalendarJSON();
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


            saveCalendarJSON();

        }).start();
    }


    public void saveCalendarJSON() {
        Logger.log("Saving calendar.json");
        try {
            TC.Directories.CALENDAR_DATA_FILE.createNewFile();

            try (FileOutputStream fWriter = new FileOutputStream(TC.Directories.CALENDAR_DATA_FILE)) {
                fWriter.write(root.toString().getBytes());
            }
        } catch (Exception e) {
            Logger.log(e);
        }

    }


    private void readSchedules() {
        final File[] scheduleFiles = TC.Directories.CALENDAR_SCHEDULES_PATH.listFiles();

        if (scheduleFiles == null) throw new RuntimeException("scheduledFiles is null");


        for (final File file : scheduleFiles) {
            try {
                final JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(file.toPath())));
                final JSONHelper scheduleRoot = new JSONHelper(jsonObject);


                final String scheduleName = scheduleRoot.get(Keys.SCHEDULE_NAME);
                final String startDate = scheduleRoot.get(Keys.START_DATE);
                final String endDate = scheduleRoot.get(Keys.END_DATE);
                final String scheduleId = scheduleRoot.get(Keys.ID);
                final JSONObject schedules = (JSONObject) jsonObject.get(Keys.SCHEDULE_EVENTS);


                for (final Object idObject : schedules.keySet()) {
                    final JSONObject eventDetails = (JSONObject) schedules.get(idObject);
                    final JSONHelper eventJson = new JSONHelper(eventDetails);


                    final String scheduleEventID = (String) idObject;
                    final String description = eventJson.get(Keys.DESCRIPTION);
                    final String eventName = eventJson.get(Keys.EVENT_NAME);
                    final String startTime = eventJson.get(Keys.START_TIME);
                    final String endTime = eventJson.get(Keys.END_TIME);

                    final Object[] a = ((JSONArray) JSONValue.parse((String) eventDetails.get(Keys.DAYS))).toArray();
                    final String[] weekdayStringArray = Arrays.copyOf(a, a.length, String[].class);

                    final Schedule schedule = new Schedule(view.p)


                }





            } catch (Exception e) {
                Logger.log(e);
            }


        }
    }

    public void saveScheduleData(final ScheduleData data) {
        Logger.log("Saving schedule " + data.getScheduleName());

        TC.Directories.CALENDAR_SCHEDULES_PATH.mkdir();
        final File scheduleFile = new File(TC.Directories.CALENDAR_SCHEDULES_PATH, data.getId() + ".json");


        try {
            scheduleFile.createNewFile();

            final JSONObject json = new JSONObject();

            json.put(Keys.SCHEDULE_NAME, data.getScheduleName());
            json.put(Keys.START_DATE, data.getStartDate() != null ? data.getStartDate().toString() : "");
            json.put(Keys.END_DATE, data.getEndDate() != null ? data.getEndDate().toString() : "");
            json.put(Keys.ID, data.getId());


            final JSONObject scheduleEventBranch = new JSONObject();
            json.put(Keys.SCHEDULE_EVENTS, scheduleEventBranch);

            for (final Schedule schedule : data.getScheduleList()) {
                final JSONObject eventBranch = new JSONObject();
                scheduleEventBranch.put(schedule.getScheduleId(), eventBranch);


                final LocalTime startTime = schedule.getStartTime();
                final LocalTime endTime = schedule.getEndTime();

                eventBranch.put(Keys.EVENT_NAME, schedule.getScheduleName());
                eventBranch.put(Keys.DAYS, JSONArray.toJSONString(schedule.getWeekdays()));
                eventBranch.put(Keys.START_TIME, startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                eventBranch.put(Keys.END_TIME, endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                eventBranch.put(Keys.DESCRIPTION, schedule.getDescription());
            }

            try (FileOutputStream fWriter = new FileOutputStream(scheduleFile)) {
                fWriter.write(json.toString().getBytes());
            }
        } catch (Exception e) {
            Logger.log(e);
        }

    }

    @Override
    public String toString() {
        return root.toString();
    }


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
        TITLE;


        @Override
        public String toString() {
            final StringBuilder keyName = new StringBuilder();

            for (final String s : name().split("_")) {
                keyName.append(ThoughtsHelper.toCamelCase(s)).append(" ");
            }

            return keyName.toString().trim();

        }

    }

    public class JSONHelper {

        public JSONObject json;

        public JSONHelper(final JSONObject obj) {
            this.json = obj;
        }

        public String get(final Keys key){
            return (String) json.get(key);
        }



    }

}
