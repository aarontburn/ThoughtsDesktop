package com.beanloaf.thoughtsdesktop.calendar.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CalendarHandler;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CalendarJsonHandler;
import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleBoxItem;
import com.beanloaf.thoughtsdesktop.calendar.objects.schedule.ScheduleData;
import com.beanloaf.thoughtsdesktop.calendar.views.children.overlays.ScheduleOverlay;
import com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.RightPanel;
import com.beanloaf.thoughtsdesktop.notes.views.ThoughtsView;
import javafx.application.Platform;
import javafx.scene.Node;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalendarMain extends ThoughtsView {


    private final CalendarHandler calendarHandler;
    private final CalendarJsonHandler calendarJson;


    private final RightPanel rightPanel;


    /*  Full Screen Overlays    */
    public enum Overlays {
        CALENDAR, SCHEDULE
    }

    private ScheduleOverlay scheduleOverlay;


    private final Map<Overlays, Node> overlayMap = new HashMap<>();

    public CalendarMain(final MainApplication main) {
        super(main);
        this.overlayMap.put(Overlays.CALENDAR, findNodeById("calendarTab"));
        this.overlayMap.put(Overlays.SCHEDULE, findNodeById("newScheduleTab"));

        this.calendarHandler = new CalendarHandler(this);
        this.calendarJson = new CalendarJsonHandler(this);
        this.rightPanel = new RightPanel(this);


        Platform.runLater(() -> {
            for (final ScheduleData data : getJsonHandler().getScheduleDataList()) {
                getRightPanel().getMonthView().addScheduleToCalendarDay(data);

                if (getRightPanel().getMonthView().calendarScheduleBox == null) {
                    getRightPanel().getMonthView().calendarScheduleBox.getChildren().add(new ScheduleBoxItem(this, data));
                    continue;
                }

                getRightPanel().getMonthView().calendarScheduleBox.getChildren().add(new ScheduleBoxItem(this, data));
            }

            final Map<LocalDate, List<DayEvent>> eventMap = getJsonHandler().getEventMap();

            for (final LocalDate date : eventMap.keySet()) {
                for (final DayEvent event : eventMap.get(date)) {
                    getRightPanel().getMonthView().addEventToCalendarDay(date, event);
                }
            }

            startup();
        });


    }

    private void startup() {
        getRightPanel().getMonthView().startupMonthView();
        getRightPanel().getWeekView().changeWeek(getCalendarHandler().getSelectedDay().getDate());
    }

    public void onOpen() {
        this.rightPanel.getMonthView().onOpen();
    }


    public void swapOverlay(final Overlays swapToOverlay) {
        swapOverlay(swapToOverlay, null);
    }

    public void swapOverlay(final Overlays swapToOverlay, final Object arguments) {
        if (swapToOverlay == null) throw new IllegalArgumentException("swapToOverlay cannot be null.");

        for (final Overlays overlay : overlayMap.keySet()) {
            overlayMap.get(overlay).setVisible(false);
        }

        if (swapToOverlay == Overlays.SCHEDULE) {
            scheduleOverlay = new ScheduleOverlay(this, (ScheduleData) arguments);
        }

        overlayMap.get(swapToOverlay).setVisible(true);
    }

    public RightPanel getRightPanel() {
        return this.rightPanel;
    }


    public CalendarJsonHandler getJsonHandler() {
        return this.calendarJson;
    }

    public CalendarHandler getCalendarHandler() {
        return this.calendarHandler;
    }


}
