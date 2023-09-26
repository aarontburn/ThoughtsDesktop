package com.beanloaf.thoughtsdesktop.calendar.handlers;

import com.beanloaf.thoughtsdesktop.calendar.enums.Keys;
import org.json.simple.JSONObject;

import java.util.Set;

public class JSONHelper {


    public final JSONObject json;



    public JSONHelper(final JSONObject obj) {
        this.json = obj;
    }

    public String getString(final Keys key) {
        return getString(key.toString());
    }

    public String getString(final String key) {
        return (String) json.get(key);
    }


    public Boolean getBoolean(final Keys key) {
        return getBoolean(key.toString());
    }

    public Boolean getBoolean(final String key) {
        return (Boolean) json.get(key);
    }


    public JSONHelper getBranch(final Object branchKey) {
        final Object branch = json.get(branchKey);

        if (branch == null) return null;
        if (branch.getClass() != JSONObject.class) throw new IllegalArgumentException("Object with key: '" + branchKey + "' is NOT a branch.");

        return new JSONHelper((JSONObject) branch);
    }

    public Set getKeys() {
        return json.keySet();
    }

    public JSONObject getJson() {
        return this.json;
    }



}
