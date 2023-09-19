package com.beanloaf.thoughtsdesktop.calendar.handlers;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.beanloaf.thoughtsdesktop.calendar.enums.Keys;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.res.TC;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("unchecked")
public class CanvasICalHandler {

    private final CalendarMain main;

    private final List<BasicEvent> iCalCanvasEventsList = new ArrayList<>();


    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;


    public CanvasICalHandler(final CalendarMain main) {
        this.main = main;


        TC.Directories.CALENDAR_PATH.mkdir();

        if (checkICalUrl((String) SettingsHandler.getInstance().getSetting(SettingsHandler.Settings.CANVAS_ICAL_URL))) {
            setAutoRefresh();
        }

    }

    private Map<String, BasicEvent> readCanvasEventsFromJson() {
        final Map<String, BasicEvent> cachedCanvasEvents = new HashMap<>();
        try {
            final JSONObject data = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(TC.Directories.CANVAS_ICAL_DATA_FILE.toPath())));
            final JSONHelper dataHelper = new JSONHelper(data);


            for (final Object o : dataHelper.getKeys()) {
                final BasicEvent event = new BasicEvent();

                final String uid = (String) o;
                final JSONHelper eventBranch = dataHelper.getBranch(uid);

                final String startTime = eventBranch.getString(Keys.START_TIME);
                final String startDate = eventBranch.getString(Keys.START_DATE);
                final String endTime = eventBranch.getString(Keys.END_TIME);
                final String description = eventBranch.getString(Keys.DESCRIPTION);
                final Boolean isCompleted = eventBranch.getBoolean(Keys.COMPLETED);

                event.setTitle(eventBranch.getString(Keys.TITLE));
                event.setId(uid);
                event.setDescription(description == null ? "" : description);
                event.setCompleted(isCompleted != null && isCompleted);


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

                if (startDate == null) {
                    event.setStartDate(null);
                } else {
                    try {
                        final LocalDate parsedStartDate = LocalDate.parse(startDate);
                        event.setStartDate(parsedStartDate);
                    } catch (DateTimeParseException parseException) {
                        event.setStartDate(null);
                    }
                }

                cachedCanvasEvents.put(uid, event);
            }

        } catch (Exception ignored) {

        }

        return cachedCanvasEvents;


    }

    public void refresh() {
        Logger.log("Refreshing Canvas iCal...");

        iCalCanvasEventsList.clear();
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL((String) SettingsHandler.getInstance().getSetting(SettingsHandler.Settings.CANVAS_ICAL_URL)).openConnection();

            final int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Logger.log("Could not access Canvas ICal, with response code: " + responseCode);
                return;
            }

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            responseReader.close();
            connection.disconnect();

            final ICalendar iCal = Biweekly.parse(responseBuilder.toString()).first();

            for (final VEvent event : iCal.getEvents()) {
                LocalDate startDate = null;
                LocalTime startTime = null;

                LocalTime endTime = null;

                if (event.getDateStart() != null) {
                    final LocalDateTime startDateTime = LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(), ZoneId.systemDefault());

                    if (!startDateTime.toLocalTime().equals(LocalTime.of(0, 0))) {
                        startTime = startDateTime.toLocalTime();
                    }
                    startDate = startDateTime.toLocalDate();


                }

                if (event.getDateEnd() != null) {
                    final LocalDateTime endDateTime = LocalDateTime.ofInstant(event.getDateEnd().getValue().toInstant(), ZoneId.systemDefault());
                    endTime = endDateTime.toLocalTime();
                }


                String desc = "";
                if (!event.getLocation().getValue().equals("")) {
                    desc += "Location/Address: " + event.getLocation().getValue() + "\n\n";
                }
                if (event.getDescription() != null) {
                    desc += event.getDescription().getValue();
                }

                final BasicEvent e = new BasicEvent(event.getSummary().getValue())
                        .setId(event.getUid().getValue())
                        .setStartDate(startDate)
                        .setStartTime(startTime)
                        .setEndTime(endTime)
                        .setDescription(desc)
                        .setEventType(TypedEvent.Types.CANVAS);

                iCalCanvasEventsList.add(e);

            }

        } catch (Exception e) {
            Logger.log(e);
        }

        final Map<String, BasicEvent> cachedCanvasEvents = readCanvasEventsFromJson();
        for (final BasicEvent event : iCalCanvasEventsList) {
            final BasicEvent cachedEvent = cachedCanvasEvents.get(event.getId());
            if (cachedEvent != null) {
                event.setCompleted(cachedEvent.isComplete());
            }
        }
        cacheCanvasEventsToJson(iCalCanvasEventsList);


        if (main.getRightPanel() == null) {
            main.queuedTasks.add(() -> main.getRightPanel().getMonthView().addCanvasEventsToCalendar(iCalCanvasEventsList));
        } else {
            main.getRightPanel().getMonthView().addCanvasEventsToCalendar(iCalCanvasEventsList);
        }

    }

    private void cacheCanvasEventsToJson(final List<BasicEvent> eventsToCache) {
        new Thread(() -> {
            final JSONObject root = new JSONObject();
            for (final BasicEvent event : eventsToCache) {
                final JSONObject eventBranch = new JSONObject();

                final LocalTime startTime = event.getStartTime();
                final LocalTime endTime = event.getEndTime();

                eventBranch.put(Keys.TITLE, event.getTitle());
                eventBranch.put(Keys.START_DATE, event.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                eventBranch.put(Keys.START_TIME, startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                eventBranch.put(Keys.END_TIME, endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                eventBranch.put(Keys.DESCRIPTION, event.getDescription());
                eventBranch.put(Keys.COMPLETED, event.isComplete());

                root.put(event.getId(), eventBranch);
            }


            Logger.log("Caching Canvas events to canvas_ical.json");
            try {
                TC.Directories.CALENDAR_PATH.mkdir();
                TC.Directories.CANVAS_ICAL_DATA_FILE.createNewFile();
                try (FileOutputStream fWriter = new FileOutputStream(TC.Directories.CANVAS_ICAL_DATA_FILE)) {
                    fWriter.write(root.toString().getBytes());
                }
            } catch (Exception e) {
                Logger.log(e);
            }
        }).start();
    }

    public void editCanvasEventCompletion(final String uid, final boolean isComplete) {
        boolean found = false;
        for (final BasicEvent event : iCalCanvasEventsList) {
            if (event.getId().equals(uid)) {
                event.setCompleted(isComplete);
                found = true;
                break;
            }
        }
        if (!found) Logger.log("ERROR: Could not find cached canvas event by UID: " + uid);

        cacheCanvasEventsToJson(iCalCanvasEventsList);
    }


    public void setAutoRefresh() {
        if (scheduledTask != null) {
            scheduler.shutdownNow();
            scheduledTask.cancel(true);
        }

        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }


        scheduledTask = scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        refresh();
                    } catch (Exception e) {
                        Logger.log(e);
                    }
                },
                0,
                ((Double) SettingsHandler.getInstance().getSetting(SettingsHandler.Settings.CANVAS_ICAL_REFRESH_RATE)).longValue(),
                TimeUnit.MINUTES);
    }

    public void stopRefresh() {
        if (scheduler != null) scheduler.shutdownNow();
    }

    public static boolean checkICalUrl(final String url) {
        if (url == null || url.equals("")) return false;
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            final int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Logger.log("Could not access Canvas ICal, with response code: " + responseCode);
                return false;
            }
            connection.disconnect();
            return true;


        } catch (Exception e) {
            return false;
        }

    }


}
