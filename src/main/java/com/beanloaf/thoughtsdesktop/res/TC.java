package com.beanloaf.thoughtsdesktop.res;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.File;

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

    public static class Properties {
        public static final String SET_TEXT_FIELDS = "set text fields";
        public static final String REFRESH = "refresh";
        public static final String SORT = "sort";
        public static final String SELECTED_TAG = "selected tag";
        public static final String DELETE = "delete";
        public static final String NEW_FILE = "new file";
        public static final String VALIDATE_TAG = "validate tag";
        public static final String VALIDATE_TITLE = "validate title";
        public static final String OPEN_SETTINGS = "open settings";
        public static final String PUSH = "push";
        public static final String PULL = "pull";
        public static final String LOG_IN_USER = "log in user";
        public static final String LOG_IN_SUCCESS = "log in success";
        public static final String SIGN_OUT = "sign out";


    }


    public static class Paths {
        public static final File STORAGE_PATH = new File("C:/Users/ACA/IdeaProjects/ThoughtsDesktop/storage/"); // TODO: This is build only.
        public static final File UNSORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/unsorted/");
        public static final File SORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/sorted/");
        public static final File LOGIN_PATH = new File("/");
    };



}
