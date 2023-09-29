package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DayEvent extends EventBoxLabel implements EventLabel, TypedEvent, Comparable<DayEvent> {

    public final static String DAY_EVENT_ID = "dayEvent";
    private final CalendarMain main;
    private final BasicEvent event;

    // Cloning constructor, used to tie the event-box object to the one in the grid
    public DayEvent(@NotNull final DayEvent reference, final CalendarMain main) {
        this(reference.event, main);
        reference.addReference(this);
        addReference(reference);
        this.setText(getDisplayTime(event.getStartTime()) + event.getTitle());
    }

    // Constructor for creating a NEW event
    public DayEvent(final LocalDate day, final String eventTitle, final CalendarMain main, final Types eventType) {
        this(day, eventTitle, UUID.randomUUID().toString(), main, eventType);
    }

    // Constructor for reading an EXISTING event from file
    public DayEvent(final LocalDate day, final String eventTitle, final String eventID, final CalendarMain main, final Types eventType) {
        this(new BasicEvent(eventTitle)
                        .setStartDate(day)
                        .setId(eventID)
                        .setDisplayColor(CH.getRandomColor())
                        .setEventType(eventType),
                main);
    }

    public DayEvent(final LocalDate day, final String eventTitle, final String eventID, final CalendarMain main, final Types eventType, final String altText) {
        this(day, eventTitle, eventID, main, eventType);
        this.event.setAltText(altText);
    }

    public DayEvent(final BasicEvent event, final CalendarMain main) {
        super(getDisplayTime(event.getStartTime()) + event.getTitle());
        this.main = main;
        this.event = event.addReference(this);
        this.setId(DAY_EVENT_ID);

        setGraphic(getEventIcon(getEventType()));

        this.updateDisplayColor(event.getDisplayColor());
        super.getToolTip().textProperty().bindBidirectional(this.textProperty());

        this.getChildren().addListener((ListChangeListener<Node>) change -> {
            for (final Node node : getChildren()) {
                node.setId(DAY_EVENT_ID);
                if (node.getClass().getSimpleName().equals("LabeledText")) {
                    ((Text) node).setStrikethrough(this.event.isComplete());
                }
            }
        });


        this.setOnMouseClicked(e -> onClick());


    }


    @Override
    public Types getEventType() {
        return this.event.getEventType();
    }


    @Override
    public void onClick() {
        this.main.getRightPanel().getMonthView().selectEvent(this, false);
        Logger.log("Event \"" + this.event.getTitle() + "\" was pressed. Color: " + event.getDisplayColor());
    }


    public void setEventTitle(final String eventTitle) {
        this.event.setTitle(eventTitle);
        this.updateEventTitle(eventTitle);
    }

    public void setDescription(final String description) {
        this.event.setDescription(description);
        this.updateDescription(description);
    }

    public void setStartDate(final LocalDate date) {
        this.event.setStartDate(date);
        this.updateStartDate(date);


    }

    public void setStartTime(final LocalTime startTime) {
        this.event.setStartTime(startTime);
        this.updateStartTime(startTime);
    }

    public void setStartTime(final String stringTime) {
        try {
            setStartTime(LocalTime.parse(stringTime));
        } catch (DateTimeParseException e) {
            setStartTime((LocalTime) null);
        }
    }

    public void setEndTime(final LocalTime endTime) {
        this.event.setEndTime(endTime);
        this.updateEndTime(endTime);
    }

    public void setEndTime(final String stringTime) {
        try {
            setEndTime(LocalTime.parse(stringTime));
        } catch (DateTimeParseException e) {
            setEndTime((LocalTime) null);
        }
    }


    public void setCompleted(final boolean isCompleted, final boolean save) {
        this.event.setCompleted(isCompleted);
        this.updateCompletion(isCompleted);


        if (save) {
            main.getRightPanel().getMonthView().saveEvent(this.event, main.getLeftPanel().getEventInputFields());
        }
    }

    public void addReference(final EventLabel reference) {
        this.event.addReference(reference);
    }


    @Override
    public String getEventTitle() {
        return this.event.getTitle();
    }

    public LocalDate getDate() {
        return this.event.getStartDate();
    }

    public LocalTime getStartTime() {
        return this.event.getStartTime();
    }

    public LocalTime getEndTime() {
        return this.event.getEndTime();
    }

    public String getDescription() {
        return this.event.getDescription();
    }

    public String getEventID() {
        return this.event.getId();
    }

    public boolean isCompleted() {
        return this.event.isComplete();
    }

    public static String getDisplayTime(final LocalTime time) {
        return time == null ? "" : time.format(DateTimeFormatter.ofPattern("h:mm a")).replace(" AM", "a").replace(" PM", "p") + " | ";
    }

    public static ImageView getEventIcon(final TypedEvent.Types type) {
        String graphic = "icons/calendar-small-page.png";
        if (type == Types.SCHEDULE) {
            graphic = "icons/schedule-icon.png";
        } else if (type == Types.CANVAS) {
            graphic = "icons/canvas-icon.png";
        }
        return new ImageView(new Image(String.valueOf(MainApplication.class.getResource(graphic)), 17.5, 17.5, true, true));
    }

    public String getDisplayColor() {
        return this.event.getDisplayColor();
    }

    public void setDisplayColor(final String color) {
        this.event.setDisplayColor(color);
    }

    @Override
    public void updateEventTitle(String eventTitle) {
        this.setText(getDisplayTime(getStartTime()) + eventTitle);
    }

    @Override
    public void updateDescription(String description) {
    }

    @Override
    public void updateStartDate(LocalDate date) {
    }

    @Override
    public void updateEndDate(LocalDate date) {
        // This should not be used.

    }

    @Override
    public void updateStartTime(LocalTime time) {
        this.setText(getDisplayTime(time) + event.getTitle());
    }

    @Override
    public void updateEndTime(LocalTime time) {

    }


    @Override
    public void updateCompletion(boolean isComplete) {
        for (final Node node : this.getChildren()) {
            if (node.getClass().getSimpleName().equals("LabeledText")) {
                final Text text = (Text) node;
                text.setStrikethrough(isComplete);
            }
        }
    }

    @Override
    public void updateDisplayColor(String color) {
        this.setStyle("-fx-border-color: derive(" + event.getDisplayColor() + ", -25%); -fx-background-color: " + event.getDisplayColor() + ";");
    }


    public BasicEvent getEvent() {
        return this.event;
    }

    public String getAltText() {
        return this.event.getAltText();
    }

    @Override
    public int compareTo(@NotNull DayEvent other) {
        if (this.getStartTime() != null && other.getStartTime() != null) {
            return this.getStartTime().compareTo(other.getStartTime());
        }

        if (this.getStartTime() == null && other.getStartTime() == null) {
            return this.getEventTitle().compareTo(other.getEventTitle());
        } else if (this.getStartTime() == null) {
            return 1;
        } else {
            return -1;
        }



    }

    public static Comparator<Node> getDayEventComparator() {
        return (o1, o2) -> {
            if (o1.getClass() != DayEvent.class && o2.getClass() != DayEvent.class) {
                return 0;
            }

            final DayEvent event1 = (DayEvent) o1;
            final DayEvent event2 = (DayEvent) o2;

            return event1.compareTo(event2);
        };

    }

}
