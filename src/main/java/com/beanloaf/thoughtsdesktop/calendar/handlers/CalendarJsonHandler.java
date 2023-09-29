package com.beanloaf.thoughtsdesktop.calendar.handlers;

import com.beanloaf.thoughtsdesktop.calendar.enums.Keys;
import com.beanloaf.thoughtsdesktop.calendar.enums.Weekday;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.res.TC;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("unchecked")
public class CalendarJsonHandler {


    private final CalendarMain main;
    private final Map<LocalDate, List<DayEvent>> eventMap = new ConcurrentHashMap<>();
    private final List<ScheduleData> scheduleDataList = new ArrayList<>();
    private JSONObject root;

    public CalendarJsonHandler(final CalendarMain m) {
        this.main = m;

        TC.Directories.CALENDAR_PATH.mkdir();
        TC.Directories.CALENDAR_SCHEDULES_PATH.mkdir();

        try {
            TC.Directories.CALENDAR_DATA_FILE.createNewFile();
            this.root = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(TC.Directories.CALENDAR_DATA_FILE.toPath())));

            if (root == null) {
                root = new JSONObject();
            }

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
            final JSONHelper rootHelper = new JSONHelper(root);

            for (final Object o : rootHelper.getKeys()) {
                final String year = (String) o;

                final JSONHelper yearBranch = rootHelper.getBranch(year);
                for (final Object m : yearBranch.getKeys()) {
                    final String month = (String) m;

                    final JSONHelper monthBranch = yearBranch.getBranch(month);
                    for (final Object d : monthBranch.getKeys()) {
                        final String dayNum = (String) d;

                        final JSONHelper dayBranch = monthBranch.getBranch(dayNum);
                        for (final Object e : dayBranch.getKeys()) {
                            final String eventID = (String) e;

                            final JSONHelper json = dayBranch.getBranch(eventID);

                            final String eventTitle = json.getString(Keys.TITLE);
                            final String description = json.getString(Keys.DESCRIPTION);
                            final String startTime = json.getString(Keys.START_TIME);
                            final String endTime = json.getString(Keys.END_TIME);
                            final Boolean isCompleted = json.getBoolean(Keys.COMPLETED);
                            final String displayColor = json.getString(Keys.DISPLAY_COLOR) == null ? CH.getRandomColor() : json.getString(Keys.DISPLAY_COLOR);


                            final LocalDate eventDate = LocalDate.of(Integer.parseInt(year), Month.valueOf(month.toUpperCase(Locale.ENGLISH)), Integer.parseInt(dayNum));

                            final DayEvent event = new DayEvent(eventDate, eventTitle, eventID, main, TypedEvent.Types.DAY);
                            event.setDescription(description);
                            event.setCompleted(isCompleted != null ? isCompleted : false, false);
                            event.setDisplayColor(displayColor);
                            event.setStartTime(startTime);
                            event.setEndTime(endTime);

                            eventMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);

                        }
                    }
                }
            }


        } catch (Exception e) {
            // Delete and rerun?
            Logger.log(e);
        }


    }

    public Map<LocalDate, List<DayEvent>> getEventMap() {
        return this.eventMap;
    }


    public void addEventToJson(final BasicEvent event) {
        if (event.getEventType() != TypedEvent.Types.DAY) {
            return;
        }

        new Thread(() -> {
            final String year = String.valueOf(event.getStartDate().getYear());
            final String month = event.getStartDate().getMonth().toString();
            final String day = String.valueOf(event.getStartDate().getDayOfMonth());
            final boolean completed = event.isComplete();


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

            JSONObject eventBranch = (JSONObject) dayBranch.get(event.getId());

            if (eventBranch == null) {
                eventBranch = new JSONObject();

                dayBranch.put(event.getId(), eventBranch);
            }

            final LocalTime startTime = event.getStartTime();
            final LocalTime endTime = event.getEndTime();
            eventBranch.clear();

            eventBranch.put(Keys.TITLE, event.getTitle());
            eventBranch.put(Keys.DESCRIPTION, event.getDescription());
            eventBranch.put(Keys.START_TIME, startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
            eventBranch.put(Keys.END_TIME, endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
            eventBranch.put(Keys.DISPLAY_COLOR, event.getDisplayColor() == null ? CH.getRandomColor() : event.getDisplayColor());
            eventBranch.put(Keys.COMPLETED, completed);

            saveCalendarJSON();
        }).start();


    }

    public void removeEventFromJson(final BasicEvent event, final LocalDate date) {

        new Thread(() -> {
            final String year = String.valueOf(event.getStartDate().getYear());
            final String month = event.getStartDate().getMonth().toString();
            final String day = String.valueOf(date.getDayOfMonth());

            JSONObject yearBranch = (JSONObject) root.get(year);
            JSONObject monthBranch = (JSONObject) yearBranch.get(month);
            JSONObject dayBranch = (JSONObject) monthBranch.get(day);

            dayBranch.remove(event.getId());

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

        if (scheduleFiles == null) {
            throw new RuntimeException("scheduledFiles is null");
        }

        for (final File file : scheduleFiles) {
            try {
                final JSONHelper scheduleRoot = new JSONHelper((JSONObject) JSONValue.parse(new String(Files.readAllBytes(file.toPath()))));

                final String scheduleName = scheduleRoot.getString(Keys.SCHEDULE_NAME);
                final String startDate = scheduleRoot.getString(Keys.START_DATE);
                final String endDate = scheduleRoot.getString(Keys.END_DATE);
                final String scheduleId = file.getName().replace(".json", "");
                final String displayColor = scheduleRoot.getString(Keys.DISPLAY_COLOR);
                if (!scheduleId.equals(scheduleRoot.getString(Keys.ID))) {
                    Logger.log("WARNING: ID of " + scheduleName + " does not match file name: " + file.getName());
                }

                final JSONHelper schedules = scheduleRoot.getBranch(Keys.SCHEDULE_EVENTS.toString());

                final ScheduleData scheduleData = new ScheduleData(scheduleId);
                scheduleData.setScheduleName(scheduleName);
                scheduleData.setDisplayColor(displayColor == null ? CH.getRandomColor() : displayColor);
                scheduleData.setStartDate(startDate);
                scheduleData.setEndDate(endDate);


                for (final Object idObject : schedules.getKeys()) {
                    final JSONHelper eventJson = schedules.getBranch(idObject);

                    final String scheduleEventID = (String) idObject;
                    final String scheduleEventName = eventJson.getString(Keys.EVENT_NAME);

                    final String scheduleEventDescription = eventJson.getString(Keys.DESCRIPTION);
                    final String scheduleEventStartTime = eventJson.getString(Keys.START_TIME);
                    final String scheduleEventEndTime = eventJson.getString(Keys.END_TIME);

                    final Object[] a = ((JSONArray) JSONValue.parse(eventJson.getString(Keys.DAYS))).toArray();
                    final String[] scheduleEventWeekdayStrings = Arrays.copyOf(a, a.length, String[].class);

                    final BasicEvent event = new BasicEvent(scheduleEventName);
                    event.setId(scheduleEventID);
                    event.setDescription(scheduleEventDescription);
                    event.setDisplayColor(displayColor);

                    event.setStartDate(startDate);
                    event.setStartTime(scheduleEventStartTime);
                    event.setEndTime(scheduleEventEndTime);


                    for (final String weekday : scheduleEventWeekdayStrings) {
                        scheduleData.addEvent(weekday, event);
                    }

                }
                scheduleDataList.add(scheduleData);

            } catch (Exception e) {
                Logger.log(e);
            }


        }

    }

    public List<ScheduleData> getScheduleDataList() {
        return this.scheduleDataList;
    }

    public void writeScheduleData(final ScheduleData data) {
        Logger.log("Saving schedule: " + data.getScheduleName());

        TC.Directories.CALENDAR_SCHEDULES_PATH.mkdir();
        final File scheduleFile = new File(TC.Directories.CALENDAR_SCHEDULES_PATH, data.getId() + ".json");
        new Thread(() -> {
            try {
                scheduleFile.createNewFile();

                final JSONObject json = new JSONObject();

                json.put(Keys.SCHEDULE_NAME, data.getScheduleName());
                json.put(Keys.START_DATE, data.getStartDate() != null ? data.getStartDate().toString() : "");
                json.put(Keys.END_DATE, data.getEndDate() != null ? data.getEndDate().toString() : "");
                json.put(Keys.ID, data.getId());
                json.put(Keys.DISPLAY_COLOR, data.getDisplayColor());

                final JSONObject scheduleEventBranch = new JSONObject();
                json.put(Keys.SCHEDULE_EVENTS, scheduleEventBranch);

                final Map<Weekday, Map<String, BasicEvent>> map = data.getScheduleEventList();
                final List<Weekday> weekdays = new ArrayList<>(data.getScheduleEventList().keySet());

                final List<String> weekdayStringList = new ArrayList<>();
                Collections.sort(weekdays);
                for (final Weekday weekday : weekdays) {
                    weekdayStringList.add(weekday.name());
                }

                for (final Map<String, BasicEvent> uidEventMap : map.values()) {
                    for (final String uid : uidEventMap.keySet()) {
                        final BasicEvent event = uidEventMap.get(uid);

                        final JSONObject eventBranch = new JSONObject();
                        scheduleEventBranch.put(uid, eventBranch);

                        final LocalTime startTime = event.getStartTime();
                        final LocalTime endTime = event.getEndTime();

                        eventBranch.put(Keys.EVENT_NAME, event.getTitle());
                        eventBranch.put(Keys.DAYS, JSONArray.toJSONString(weekdayStringList));
                        eventBranch.put(Keys.START_TIME, startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                        eventBranch.put(Keys.END_TIME, endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                        eventBranch.put(Keys.DESCRIPTION, event.getDescription());
                        eventBranch.put(Keys.DISPLAY_COLOR, event.getDisplayColor() == null ? CH.getRandomColor() : event.getDisplayColor());
                    }
                }

                try (FileOutputStream fWriter = new FileOutputStream(scheduleFile)) {
                    fWriter.write(json.toString().getBytes());
                }

            } catch (Exception e) {
                Logger.log(e);
            }
        }).start();


    }

    @Override
    public String toString() {
        return root.toString();
    }


}
