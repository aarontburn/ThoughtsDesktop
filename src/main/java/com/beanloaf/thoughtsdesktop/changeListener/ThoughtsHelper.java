package com.beanloaf.thoughtsdesktop.changeListener;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ThoughtsHelper {

    private ThoughtsHelper() {
    }

    private static ThoughtsHelper instance;

    public static ThoughtsHelper getInstance() {
        if (instance == null) {
            instance = new ThoughtsHelper();
        }
        return instance;
    }

    private final Map<Class<?>, ThoughtsChangeListener> listeners = new HashMap<>();


    private ThoughtObject selectedFile;

    public boolean isChangingTextFields;


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

        properArguments(eventName, eventValue);


        for (final Class<?> key : listeners.keySet()) {
            listeners.get(key).eventFired(eventName, eventValue);
        }

    }

    public void targetEvent(final Class<?> className, final String eventName, final Object eventValue) {
        if (eventName == null) {
            throw new IllegalArgumentException("eventName cannot be null.");
        }

        properArguments(eventName, eventValue);


        final ThoughtsChangeListener listener = listeners.get(className);

        listener.eventFired(eventName, eventValue);

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


    private void properArguments(final String eventName, final Object eventValue) {
        if (belongsToClass(eventName, Properties.Data.class) && eventValue == null) {
            throw new IllegalArgumentException("Properties from the Properties.Data class cannot have a null payload");
        }

        if (belongsToClass(eventName, Properties.Actions.class) && eventValue != null) {
            throw new IllegalArgumentException("Properties from the Properties.Action class cannot have a payload.");
        }
    }

    private static boolean belongsToClass(final String input, final Class<?> inputClass) {
        try {
            final Field[] fields = inputClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == String.class && field.get(null).equals(input)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.logException(e);
        }
        return false;
    }


    public static Node setAnchor(final Node node, final Double top, final Double bottom, final Double left, final Double right) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, right);


        return node;

    }

    public static ThoughtObject readFileContents(final File filePath, final boolean isSorted) {
        try {
            final JSONObject data = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(filePath.toPath())));

            if (data == null) {
                return null;
            }

            final Boolean localOnly = data.get("localOnly") == null ? null : (boolean) data.get("localOnly");
            final String title = data.get("title") == null ? "" : data.get("title").toString().trim();
            final String date = data.get("date") == null ? null : data.get("date").toString().trim();
            final String tag = data.get("tag") == null ? "" : data.get("tag").toString().trim();
            final String body = data.get("body") == null ? "" : data.get("body").toString().trim();


            return new ThoughtObject(isSorted, localOnly, title, date, tag, body, filePath);

        } catch (Exception e) {
            Logger.logException(e);

        }
        return null;
    }


}
