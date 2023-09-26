package com.beanloaf.thoughtsdesktop.calendar.handlers;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.beanloaf.thoughtsdesktop.calendar.enums.Keys;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.CH;
import com.beanloaf.thoughtsdesktop.calendar.objects.TypedEvent;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.util.Pair;
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

    private final static LocalTime CANVAS_DEFAULT_START_TIME = LocalTime.of(0, 0);
    private final static LocalTime CANVAS_DEFAULT_END_TIME = LocalTime.of(23, 59);


    private final CalendarMain main;

    private final List<BasicEvent> iCalCanvasEventsList = new ArrayList<>();
    private final Map<String, Map<String, Object>> classMap = new HashMap<>();


    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;


    public CanvasICalHandler(final CalendarMain main) {
        this.main = main;

        TC.Directories.CALENDAR_PATH.mkdir();

        if (checkICalUrl((String) SettingsHandler.getInstance().getSetting(SettingsHandler.Settings.CANVAS_ICAL_URL))) {
            setAutoRefresh();
        }

    }

    private Map<String, Map<String, Object>> readCanvasEventsFromJson() {

        final Map<String, Map<String, Object>> cachedCanvasEvents = new HashMap<>();

        try {
            final JSONObject data = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(TC.Directories.CANVAS_ICAL_DATA_FILE.toPath())));
            final JSONHelper dataHelper = new JSONHelper(data);

            for (final Object c : dataHelper.getKeys()) { // Class Name
                final String className = (String) c;
                final JSONHelper classBranch = dataHelper.getBranch(c);

                final String classColor = classBranch.getString(Keys.DISPLAY_COLOR);
                cachedCanvasEvents.computeIfAbsent(className, k -> new HashMap<>()).put(Keys.DISPLAY_COLOR.toString(), classColor);

                for (final Object e : classBranch.getKeys()) { // Event UID and Display Color
                    if (e.equals(Keys.DISPLAY_COLOR.toString())) {
                        continue;
                    }

                    final JSONHelper eventBranch = classBranch.getBranch(e);

                    final BasicEvent event = new BasicEvent();
                    final String uid = (String) e;
                    final String startTime = eventBranch.getString(Keys.START_TIME);
                    final String startDate = eventBranch.getString(Keys.START_DATE);
                    final String endTime = eventBranch.getString(Keys.END_TIME);
                    final String description = eventBranch.getString(Keys.DESCRIPTION);
                    final Boolean isCompleted = eventBranch.getBoolean(Keys.COMPLETED);

                    event.setTitle(eventBranch.getString(Keys.TITLE));
                    event.setId(uid);
                    event.setDescription(description == null ? "" : description);
                    event.setCompleted(isCompleted != null && isCompleted);
                    event.setDisplayColor(classColor == null ? CH.getRandomColor() : classColor);

                    try {
                        event.setStartTime(startTime == null ? null : LocalTime.parse(startTime));
                    } catch (DateTimeParseException parseException) {
                        event.setStartTime(null);
                    }

                    try {
                        event.setEndTime(endTime == null ? null : LocalTime.parse(endTime));
                    } catch (DateTimeParseException parseException) {
                        event.setEndTime(null);
                    }

                    try {
                        event.setStartDate(startDate == null ? null : LocalDate.parse(startDate));
                    } catch (DateTimeParseException parseException) {
                        event.setStartDate(null);
                    }


                    cachedCanvasEvents.computeIfAbsent(className, k -> new HashMap<>()).put(uid, event);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return cachedCanvasEvents;


    }

    public void refresh() {
        Logger.log("Refreshing Canvas iCal...");

        iCalCanvasEventsList.clear();
        classMap.clear();
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
                    if (!startDateTime.toLocalTime().equals(CANVAS_DEFAULT_START_TIME)) {
                        startTime = startDateTime.toLocalTime();
                    }
                    startDate = startDateTime.toLocalDate();
                }

                if (event.getDateEnd() != null) {
                    final LocalDateTime endDateTime = LocalDateTime.ofInstant(event.getDateEnd().getValue().toInstant(), ZoneId.systemDefault());
                    if (!endDateTime.toLocalTime().equals(CANVAS_DEFAULT_END_TIME)) {
                        endTime = endDateTime.toLocalTime();
                    }
                }


                String desc = "";
                if (event.getLocation() != null && !event.getLocation().getValue().equals("")) {
                    desc += "Location: " + event.getLocation().getValue() + "\n\n";
                }
                if (event.getDescription() != null) {
                    desc += event.getDescription().getValue();
                }

                final String eventTitle = event.getSummary().getValue();
                final BasicEvent e = new BasicEvent(eventTitle)
                        .setId(event.getUid().getValue())
                        .setStartDate(startDate)
                        .setStartTime(startTime)
                        .setEndTime(endTime)
                        .setDescription(desc)
                        .setEventType(TypedEvent.Types.CANVAS)
                        .setDisplayColor(CH.getRandomColor());

                try {
                    final String className = eventTitle.substring(eventTitle.lastIndexOf('[')).replace("[", "").replace("]", "");
                    e.setAltText(className);



                    classMap.computeIfAbsent(className, k -> new HashMap<>()).put(event.getUid().getValue(), e);
                } catch (Exception ignored) {
                    classMap.computeIfAbsent(Keys.EVENTS.name(), k -> new HashMap<>()).put(event.getUid().getValue(), e);
                }

                iCalCanvasEventsList.add(e);
            }
        } catch (Exception e) {
            Logger.log(e);
        }

        /*
         * <class name>: {
         *      "Display Color": rgb(r, g, b),
         *      <uid>: {
         *          <event details>
         *      }
         * }
         * */
        final Map<String, Map<String, Object>> cachedCanvasEvents = readCanvasEventsFromJson();


        for (final String className : classMap.keySet()) {
            final Map<String, Object> newEventMap = classMap.get(className);
            final Map<String, Object> cachedEventMap = cachedCanvasEvents.get(className);

            final String cachedColor = (String) cachedEventMap.get(Keys.DISPLAY_COLOR.toString());
            if (cachedColor != null) {
                newEventMap.put(Keys.DISPLAY_COLOR.toString(), cachedColor);
            }

            for (final String uid : newEventMap.keySet()) {
                if (uid.equals(Keys.DISPLAY_COLOR.toString())) {
                    continue;
                }
                final BasicEvent event = (BasicEvent) newEventMap.get(uid);
                final BasicEvent cachedEvent = (BasicEvent) cachedEventMap.get(uid);
                if (cachedEvent != null) {
                    event.setCompleted(cachedEvent.isComplete());
                    event.setDisplayColor(cachedColor);
                }
            }
        }

        cacheCanvasEventsToJson();


        if (main.getRightPanel() == null) {
            main.queuedTasks.add(() -> main.getRightPanel().getMonthView().addCanvasEventsToCalendar(iCalCanvasEventsList));
        } else {
            main.getRightPanel().getMonthView().addCanvasEventsToCalendar(iCalCanvasEventsList);
        }

    }

    private void cacheCanvasEventsToJson() {
        new Thread(() -> {
            final JSONObject root = new JSONObject();

            for (final String className : classMap.keySet()) {  // CLASS NAME
                final JSONObject classJson = new JSONObject();
                root.put(className, classJson);

                final Map<String, Object> contentMap = classMap.get(className);
                classJson.put(Keys.DISPLAY_COLOR.toString(), contentMap.get(Keys.DISPLAY_COLOR.toString()));

                for (final Object o : contentMap.values()) {
                    if (o.getClass() != BasicEvent.class) {
                        continue;
                    }

                    final BasicEvent e = (BasicEvent) o;

                    final JSONObject classEvent = new JSONObject();
                    classJson.put(e.getId(), classEvent);

                    final LocalTime startTime = e.getStartTime();
                    final LocalTime endTime = e.getEndTime();

                    classEvent.put(Keys.TITLE, e.getTitle());
                    classEvent.put(Keys.START_DATE, e.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    classEvent.put(Keys.START_TIME, startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                    classEvent.put(Keys.END_TIME, endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                    classEvent.put(Keys.DESCRIPTION, e.getDescription());
                    classEvent.put(Keys.COMPLETED, e.isComplete());
                }

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
        if (!found) {
            Logger.log("ERROR: Could not find cached canvas event by UID: " + uid);
        }

        cacheCanvasEventsToJson();
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
