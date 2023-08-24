package com.beanloaf.thoughtsdesktop.calendar.handlers;

import com.beanloaf.thoughtsdesktop.calendar.objects.*;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleBoxItem;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleEvent;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
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


                            final LocalDate eventDate = LocalDate.of(Integer.parseInt(year), Month.valueOf(month.toUpperCase(Locale.ENGLISH)), Integer.parseInt(dayNum));

                            final DayEvent event = new DayEvent(eventDate, eventTitle, eventID, view, false);
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
                view.addEventToCalendarDay(date, event);
            }
        }

    }


    public void addEventToJson(final DayEvent event) {
        if (event.isScheduleEvent) {
            Logger.log("Attempted to record a scheduleEvent to json: " + event.getEventTitle());
            return;
        }


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
                final JSONHelper scheduleRoot = new JSONHelper((JSONObject) JSONValue.parse(new String(Files.readAllBytes(file.toPath()))));

                final String scheduleName = scheduleRoot.getString(Keys.SCHEDULE_NAME);
                final String startDate = scheduleRoot.getString(Keys.START_DATE);
                final String endDate = scheduleRoot.getString(Keys.END_DATE);
                final String scheduleId = scheduleRoot.getString(Keys.ID);
                final JSONHelper schedules = scheduleRoot.getBranch(Keys.SCHEDULE_EVENTS.toString());

                final ScheduleData scheduleData = new ScheduleData(scheduleId);
                scheduleData.setScheduleName(scheduleName);

                if (startDate == null) {
                    scheduleData.setStartDate(null);
                } else {
                    try {
                        scheduleData.setStartDate(LocalDate.parse(startDate));
                    } catch (DateTimeParseException parseException) {
                        scheduleData.setStartDate(null);
                    }
                }

                if (endDate == null) {
                    scheduleData.setEndDate(null);
                } else {
                    try {
                        scheduleData.setEndDate(LocalDate.parse(endDate));
                    } catch (DateTimeParseException parseException) {
                        scheduleData.setEndDate(null);
                    }
                }


                for (final Object idObject : schedules.getKeys()) {
                    final JSONHelper eventJson = schedules.getBranch(idObject);

                    final String scheduleEventID = (String) idObject;
                    final String scheduleEventName = eventJson.getString(Keys.EVENT_NAME);

                    final String scheduleEventDescription = eventJson.getString(Keys.DESCRIPTION);
                    final String scheduleEventStartTime = eventJson.getString(Keys.START_TIME);
                    final String scheduleEventEndTime = eventJson.getString(Keys.END_TIME);

                    final Object[] a = ((JSONArray) JSONValue.parse(eventJson.getString(Keys.DAYS))).toArray();
                    final String[] scheduleEventWeekdayStrings = Arrays.copyOf(a, a.length, String[].class);

                    final ScheduleEvent scheduleEvent = new ScheduleEvent(scheduleEventName, scheduleEventID);
                    scheduleEvent.setDescription(scheduleEventDescription);

                    for (final String weekday : scheduleEventWeekdayStrings) {
                        scheduleEvent.addWeekday(weekday);
                    }

                    if (scheduleEventStartTime == null) {
                        scheduleEvent.setStartTime(null);
                    } else {
                        try {
                            scheduleEvent.setStartTime(LocalTime.parse(scheduleEventStartTime));
                        } catch (DateTimeParseException parseException) {
                            scheduleEvent.setStartTime(null);
                        }
                    }

                    if (scheduleEventEndTime == null) {
                        scheduleEvent.setEndTime(null);
                    } else {
                        try {
                            scheduleEvent.setEndTime(LocalTime.parse(scheduleEventEndTime));
                        } catch (DateTimeParseException parseException) {
                            scheduleEvent.setEndTime(null);
                        }
                    }

                    scheduleData.addEvent(scheduleEvent);


                }
                scheduleDataList.add(scheduleData);


            } catch (Exception e) {
                Logger.log(e);
            }


        }

        for (final ScheduleData data : scheduleDataList) {
            if (view.calendarScheduleBox == null) {
                view.queuedTasks.add(() -> view.calendarScheduleBox.getChildren().add(new ScheduleBoxItem(view, data)));
                continue;
            }

            view.calendarScheduleBox.getChildren().add(new ScheduleBoxItem(view, data));
        }


        for (final ScheduleData data : scheduleDataList) {
            view.addScheduleToCalendarDay(data);
        }

    }

    public void writeScheduleData(final ScheduleData data) {
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

            for (final ScheduleEvent schedule : data.getScheduleEventList()) {
                final JSONObject eventBranch = new JSONObject();
                scheduleEventBranch.put(schedule.getId(), eventBranch);


                final LocalTime startTime = schedule.getStartTime();
                final LocalTime endTime = schedule.getEndTime();

                eventBranch.put(Keys.EVENT_NAME, schedule.getScheduleEventName());
                eventBranch.put(Keys.DAYS, JSONArray.toJSONString(schedule.getWeekdayStrings()));
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


}
