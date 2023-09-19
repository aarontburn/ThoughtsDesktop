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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DayEvent extends EventBoxLabel implements EventLabel, TypedEvent, Comparable<DayEvent> {

    public final static String DAY_EVENT_ID = "dayEvent";

    private final CalendarMain main;


    private final BasicEvent event;

    private final List<EventLabel> references = new ArrayList<>();


    public boolean isReference;


    private final Types eventType;






    // Cloning constructor, used to tie the eventbox object to the one in the grid
    public DayEvent(final DayEvent reference, final CalendarMain main) {
        this(reference.getDate(), reference.getEventTitle(), main, reference.getEventType());

        this.isReference = true;

        reference.addReference(this);
        this.references.add(reference);

        event.setStartTime(reference.event.getStartTime());
        event.setEndTime(reference.event.getEndTime());
        event.setDescription(reference.event.getDescription());
        event.setId(reference.getEventID());
        event.setCompleted(reference.event.isComplete());

        this.setText(getDisplayTime(event.getStartTime()) + event.getTitle());
    }


    // Constructor for creating a NEW event
    public DayEvent(final LocalDate day, final String eventName, final CalendarMain main, final Types eventType) {
        this(day, eventName, UUID.randomUUID().toString(), main, eventType);
    }


    // Constructor for reading an EXISTING event from file
    public DayEvent(final LocalDate day, final String eventTitle, final String eventID,
                    final CalendarMain main, final Types eventType) {
        super(eventTitle);
        this.main = main;

        this.eventType = eventType;




        String graphic = "icons/calendar-small-page.png";
        if (eventType == Types.SCHEDULE) {
            graphic = "icons/schedule-icon.png";
        } else if (eventType == Types.CANVAS) {
            graphic = "icons/canvas-icon.png";
        }


        setGraphic(new ImageView(new Image(String.valueOf(MainApplication.class.getResource(graphic)), 17.5, 17.5, true, true)));

        event = new BasicEvent(eventTitle);
        event.setStartDate(day);




        getToolTip().textProperty().bindBidirectional(this.textProperty());
        this.setId(DAY_EVENT_ID);
        this.event.setId(eventID);


        this.getChildren().addListener((ListChangeListener<Node>) change -> {
            for (final Node node : getChildren()) {
                node.setId(DAY_EVENT_ID);
                if (node.getClass().getSimpleName().equals("LabeledText")) {
                    final Text text = (Text) node;
                    text.setStrikethrough(this.event.isComplete());
                }
            }
        });

        this.setOnMouseClicked(e -> onClick());

    }


    @Override
    public Types getEventType() {
        return this.eventType;
    }


    @Override
    public void onClick() {
        this.main.getRightPanel().getMonthView().selectEvent(this, false);
        Logger.log("Event \"" + this.event.getTitle() + "\" was pressed. Type: " + eventType);
    }


    public void setEventTitle(final String eventTitle) {
        this.updateEventTitle(eventTitle);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateEventTitle(eventTitle);
        }
    }

    public void setDescription(final String description) {
        this.updateDescription(description);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateDescription(description);
        }
    }

    public void setStartDate(final LocalDate date) {
        this.updateStartDate(date);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateStartDate(date);
        }
    }

    public void setEndDate(final LocalDate date) {
        // This should not be used.

        this.updateEndDate(date);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateEndDate(date);
        }
    }


    public void setStartTime(final LocalTime startTime) {
        this.updateStartTime(startTime);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateStartTime(startTime);
        }
    }

    public void setEndTime(final LocalTime endTime) {
        this.updateEndTime(endTime);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateEndTime(endTime);
        }

    }


    public void setCompleted(final boolean isCompleted, final boolean save) {
        updateCompletion(isCompleted);

        for (final EventLabel eventLabel : references) {
            eventLabel.updateCompletion(isCompleted);
        }

        if (save) main.getRightPanel().getMonthView().saveEvent(this);
    }

    public void addReference(final EventLabel reference) {
        this.references.add(reference);
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
        String formattedTime = "";
        if (time != null) {
            formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a")).replace(" AM", "a").replace(" PM", "p") + " | ";
        }
        return formattedTime;
    }



    @Override
    public void updateEventTitle(String eventTitle) {
        event.setTitle(eventTitle);
        this.setText(getDisplayTime(getStartTime()) + eventTitle);
    }

    @Override
    public void updateDescription(String description) {
        this.event.setDescription(description);
    }

    @Override
    public void updateStartDate(LocalDate date) {
        this.event.setStartDate(date);
    }

    @Override
    public void updateEndDate(LocalDate date) {
        // This should not be used.
        this.event.setEndDate(date);

    }

    @Override
    public void updateStartTime(LocalTime time) {
        this.event.setStartTime(time);
        this.setText(getDisplayTime(time) + event.getTitle());
    }

    @Override
    public void updateEndTime(LocalTime time) {
        this.event.setEndTime(time);
    }

    @Override
    public void updateCompletion(boolean isComplete) {
        this.event.setCompleted(isComplete);

        for (final Node node : this.getChildren()) {
            if (node.getClass().getSimpleName().equals("LabeledText")) {
                final Text text = (Text) node;
                text.setStrikethrough(isComplete);
            }
        }
    }

    @Override
    public int compareTo(@NotNull DayEvent other) {
        // Compare by startDate if both events have non-null startDate
        if (this.getStartTime() != null && other.getStartTime() != null) {
            return this.getStartTime().compareTo(other.getStartTime());
        }

        // Compare by name if at least one of the events has a null startDate
        if (this.getStartTime() == null && other.getStartTime() == null) {
            // If both have null startDate, sort by name
            return this.getEventTitle().compareTo(other.getEventTitle());
        } else if (this.getStartTime() == null) {
            // If only this event has a null startDate, it comes after the other
            return 1;
        } else {
            // If only the other event has a null startDate, it comes before this one
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
