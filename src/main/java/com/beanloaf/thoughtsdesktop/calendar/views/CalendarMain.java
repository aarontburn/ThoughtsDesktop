package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CalendarHandler;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CalendarJsonHandler;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CanvasICalHandler;
import com.beanloaf.thoughtsdesktop.calendar.objects.BasicEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleBoxItem;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.views.children.left_panel.LeftPanel;
import com.beanloaf.thoughtsdesktop.calendar.views.children.overlays.EventOverlay;
import com.beanloaf.thoughtsdesktop.calendar.views.children.overlays.ScheduleOverlay;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.RightPanel;
import com.beanloaf.thoughtsdesktop.notes.views.ThoughtsView;
import javafx.application.Platform;
import javafx.scene.Node;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalendarMain extends ThoughtsView {

    public final List<Runnable> queuedTasks = new ArrayList<>();
    private final CalendarHandler calendarHandler;
    private final CalendarJsonHandler calendarJson;
    private final CanvasICalHandler canvasICalHandler;


    private final RightPanel rightPanel;
    private final LeftPanel leftPanel;


    /*  Full Screen Overlays    */
    public enum Overlays {
        CALENDAR, SCHEDULE, LOADING, EVENT
    }

    private final Map<Overlays, Node> overlayMap = new HashMap<>();

    public CalendarMain(final MainApplication main) {
        super(main);
        this.overlayMap.put(Overlays.CALENDAR, findNodeById("calendarOverlay"));
        this.overlayMap.put(Overlays.SCHEDULE, findNodeById("scheduleOverlay"));
        this.overlayMap.put(Overlays.LOADING, findNodeById("loadingOverlay"));
        this.overlayMap.put(Overlays.EVENT, findNodeById("eventOverlay"));

        this.leftPanel = new LeftPanel(this);
        this.rightPanel = new RightPanel(this);


        this.calendarHandler = new CalendarHandler(this);
        this.canvasICalHandler = new CanvasICalHandler(this);
        this.calendarJson = new CalendarJsonHandler(this);



        Platform.runLater(() -> {
            // Reading schedules
            for (final ScheduleData data : getJsonHandler().getScheduleDataList()) {
                getRightPanel().getMonthView().addScheduleToCalendarDay(data);
                getLeftPanel().addScheduleBoxItem(new ScheduleBoxItem(this, data));
            }

            // Reading calendar.json
            final Map<LocalDate, List<DayEvent>> eventMap = getJsonHandler().getEventMap();

            for (final LocalDate date : eventMap.keySet()) {
                for (final DayEvent event : eventMap.get(date)) {
                    getRightPanel().getMonthView().addEventToCalendarDay(date, event);
                }
            }

            startup();
        });

        for (final Runnable runnable : queuedTasks) {
            runnable.run();
        }
        queuedTasks.clear();

    }

    private void startup() {
        getRightPanel().getMonthView().startupMonthView();
        getRightPanel().getWeekView().changeWeek(getCalendarHandler().getSelectedDay().getDate());

        rightPanel.swapRightPanel(RightPanel.RightLayouts.MONTH);
        swapOverlay(Overlays.CALENDAR);
    }

    public void onOpen() {
        this.rightPanel.getMonthView().onOpen();
    }


    public void swapOverlay(final Overlays swapToOverlay) {
        swapOverlay(swapToOverlay, null);
    }

    public void swapOverlay(final Overlays swapToOverlay, final Object arguments) {
        if (swapToOverlay == null) {
            throw new IllegalArgumentException("swapToOverlay cannot be null.");
        }

        for (final Overlays overlay : overlayMap.keySet()) {
            overlayMap.get(overlay).setVisible(false);
        }

        if (swapToOverlay == Overlays.SCHEDULE) {
            final ScheduleOverlay scheduleOverlay = new ScheduleOverlay(this, (ScheduleData) arguments);
        } else if (swapToOverlay == Overlays.EVENT) {
            final EventOverlay eventOverlay = new EventOverlay(this, (BasicEvent) arguments);
        }

        overlayMap.get(swapToOverlay).setVisible(true);
    }

    public RightPanel getRightPanel() {
        return this.rightPanel;
    }

    public LeftPanel getLeftPanel() {
        return this.leftPanel;
    }


    public CalendarJsonHandler getJsonHandler() {
        return this.calendarJson;
    }

    public CalendarHandler getCalendarHandler() {
        return this.calendarHandler;
    }

    public CanvasICalHandler getCanvasICalHandler() {
        return this.canvasICalHandler;
    }


}
