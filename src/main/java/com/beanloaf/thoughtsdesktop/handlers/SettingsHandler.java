package com.beanloaf.thoughtsdesktop.handlers;

import com.beanloaf.thoughtsdesktop.res.TC;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsHandler {


    private final Map<Settings, Object> settingPropertyList = new ConcurrentHashMap<>();

    public enum Settings {
        LIGHT_THEME("lightTheme", false),
        PULL_ON_STARTUP("pullOnStartup", false),
        PUSH_ON_EXIT("pushOnExit", false),
        MATCH_BRACE("matchBrace", true),
        WINDOW_WIDTH("windowWidth", 1920),
        WINDOW_HEIGHT("windowHeight", 1080),
        WINDOW_X("windowX", 0.0),
        WINDOW_Y("windowY", 0.0),
        WINDOW_MAXIMIZED("windowMaximized", false);


        private final String name;
        private final Object defaultState;

        Settings(final String name, final Object defaultState) {
            this.name = name;
            this.defaultState = defaultState;
        }

        public String getName() {
            return this.name;
        }

        public static Settings getEnumByString(final String s) {
            for (final Settings settings : Settings.values()) {
                if (settings.getName().equals(s)) return settings;
            }

            throw new IllegalArgumentException("Invalid String passed: " + s);

        }
    }


    public SettingsHandler() {
        checkSettingsFile();

    }

    private void setDefaults() {
        for (final Settings key : Settings.values()) {
            settingPropertyList.put(key, key.defaultState);
        }

    }


    private void checkSettingsFile() {

        try {
            if (TC.Directories.SETTINGS_PATH.createNewFile()) {
                Logger.log("Created new settings file. Writing defaults...");
                setDefaults();
                saveSettingsFile();

            } else { // already exists
                // read file
                Logger.log("Reading settings from file...");
                final JSONObject data = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(TC.Directories.SETTINGS_PATH.toPath())));

                if (data == null) {
                    setDefaults();
                    saveSettingsFile();
                    return;
                }


                for (final Object key : data.keySet()) {
                    final Settings enumKey = Settings.getEnumByString(key.toString());

                    settingPropertyList.put(enumKey, data.get(enumKey.getName()));
                }


                for (final Settings s : Settings.values()) {
                    if (!settingPropertyList.containsKey(s)) {
                        settingPropertyList.put(s, s.defaultState);
                        Logger.log("getting from default: " + s.getName() + " | " + s.defaultState);
                        saveSettingsFile();
                    }
                }


                Logger.log(settingPropertyList);

            }

        } catch (Exception e) {
            Logger.logException(e);
        }

    }


    private void saveSettingsFile() {

        new Thread(() -> {
            try (FileOutputStream fWriter = new FileOutputStream(TC.Directories.SETTINGS_PATH)) {
                final Map<String, Object> data = new HashMap<>();


                for (final Settings key : settingPropertyList.keySet()) {
                    data.put(key.getName(), settingPropertyList.get(key) == null ? key.defaultState : settingPropertyList.get(key));
                }


                fWriter.write(new JSONObject(data).toString().getBytes());

            } catch (IOException e) {
                Logger.logException(e);
            }
        }).start();


    }


    public void changeSetting(final Settings setting, final Object newValue) {
        settingPropertyList.put(setting, newValue);
        saveSettingsFile();

    }

    public Object getSetting(final Settings setting) {

        Object returnValue = settingPropertyList.get(setting);

        if (returnValue == null) returnValue = setting.defaultState;


        if (returnValue instanceof Double || returnValue instanceof Long || returnValue instanceof Integer) {
            return (Double) returnValue;
        }


        return returnValue;
    }


}
