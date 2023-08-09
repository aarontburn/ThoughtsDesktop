package com.beanloaf.thoughtsdesktop.res;


import java.io.File;

public class TC {

    public static final String DEFAULT_TITLE = "<untitled>";
    public static final String DEFAULT_BODY = "<description>";
    public static final String DEFAULT_TAG = "<untagged>";
    public static final String DEFAULT_DATE = "<date>";


    public static class Directories {

        public static final File STORAGE_PATH = new File(System.getProperty("user.dir") + "/storage/");
        public static final File UNSORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/unsorted/");
        public static final File SORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/sorted/");
        public static final File LOGIN_PATH = new File(STORAGE_PATH, "user.json");
        public static final File SETTINGS_PATH = new File(STORAGE_PATH, "settings.json");
        public static final File CALENDAR_PATH = new File(STORAGE_PATH, "calendar.json");
        public static final File CALENDAR_TEST_PATH = new File(STORAGE_PATH, "calendar-test.json");
    }


}
