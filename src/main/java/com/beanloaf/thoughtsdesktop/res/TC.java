package com.beanloaf.thoughtsdesktop.res;

import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.nio.file.Files;

public class TC {

    public static final String DEFAULT_TITLE = "<untitled>";
    public static final String DEFAULT_BODY = "<description>";
    public static final String DEFAULT_TAG = "<untagged>";
    public static final String DEFAULT_DATE = "<date>";


    public static class CSS {
        public static final String LIST_ITEM =
                "-fx-font-size: 22; " +
                        "-fx-border-color: rgb(95, 95, 95); " +
                        "-fx-max-width: 1000000;";

        public static final String LIST_ITEM_HOVER =
                "-fx-font-size: 22; " +
                        "-fx-border-color: rgb(41, 163, 211); " +
                        "-fx-max-width: 1000000;";


    }


    public static class Directories {

        public static final File STORAGE_PATH = new File(System.getProperty("user.dir") + "/storage/");
        public static final File UNSORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/unsorted/");
        public static final File SORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/sorted/");
        public static final File LOGIN_PATH = new File(STORAGE_PATH, "user.json");
    }








}
