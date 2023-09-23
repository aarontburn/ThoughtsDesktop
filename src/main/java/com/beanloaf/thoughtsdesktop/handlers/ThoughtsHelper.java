package com.beanloaf.thoughtsdesktop.handlers;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.notes.objects.ThoughtObject;
import com.google.common.base.CaseFormat;
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
    private final Map<Class<?>, Object> controllers = new HashMap<>();


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


    public static Node setAnchor(final Node node, final Number top, final Number bottom, final Number left, final Number right) {
        AnchorPane.setTopAnchor(node, top == null ? null : top.doubleValue());
        AnchorPane.setBottomAnchor(node, bottom == null ? null : bottom.doubleValue());
        AnchorPane.setLeftAnchor(node, left == null ? null : left.doubleValue());
        AnchorPane.setRightAnchor(node, right == null ? null : right.doubleValue());


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
            final String body = data.get("body") == null ? "" : data.get("body").toString();


            return new ThoughtObject(isSorted, localOnly, title, date, tag, body, filePath);

        } catch (Exception e) {
            Logger.logException(e);

        }
        return null;
    }




    public MainApplication getMain() {
        return (MainApplication) this.listeners.get(MainApplication.class);
    }

    public void addController(final Object controller) {
        this.controllers.put(controller.getClass(), controller);
    }

    public Object getController(final Class<?> controllerClass) {
        return this.controllers.get(controllerClass);
    }



    public static String toCamelCase(final String s) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s);
    }

    public static String getNumberSuffix(final int num) {
        if (!((num > 10) && (num < 19))) {
            return switch (num % 10) {
                case 1 -> "st";
                case 2 -> "nd";
                case 3 -> "rd";
                default -> "th";
            };
        }
        return "th";

    }


}
