package com.beanloaf.thoughtsdesktop.changeListener;

import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;

import java.util.HashMap;
import java.util.Map;

public class ThoughtsHelper {

    private static ThoughtsHelper instance;


    private final Map<Class<?>, ThoughtsChangeListener> listeners = new HashMap<>();


    private ThoughtObject selectedFile;


    public static ThoughtsHelper getInstance() {
        if (instance == null) {
            instance = new ThoughtsHelper();
        }
        return instance;
    }


    private ThoughtsHelper() {
    }

    public void addListener(final ThoughtsChangeListener listener) {
        if (listener == null) throw new IllegalArgumentException("Listener cannot be null");

        listeners.put(listener.getClass(), listener);
    }

    public void removeListener(final ThoughtsChangeListener listener) {
        if (listener == null) throw new IllegalArgumentException("Listener cannot be null");

        listeners.remove(listener.getClass());

    }

    public void fireEvent(final String eventName) {
        fireEvent(eventName, null);

    }

    public void fireEvent(final String eventName, final Object eventValue) {
        if (eventName == null) throw new IllegalArgumentException("eventName cannot be null");

        for (final Class<?> key : listeners.keySet()) {
            listeners.get(key).eventFired(eventName, eventValue);
        }

    }

    public void targetEvent(final Class<?> className, final String eventName, final Object eventValue) {
        if (eventName == null) {
            throw new IllegalArgumentException("eventName cannot be null.");
        }

        final ThoughtsChangeListener listener = listeners.get(className);

        if (listener != null) {
            listener.eventFired(eventName, eventValue);
        } else {
            throw new IllegalArgumentException("Listener not found");
        }

    }

    public void targetEvent(final Class<?> className, final String eventName) {
        targetEvent(className, eventName, null);
    }

    public ThoughtObject getSelectedFile() {
        return this.selectedFile;
    }

    public void setSelectedFile(final ThoughtObject selectedFile) {
        this.selectedFile = selectedFile;
    }
}
