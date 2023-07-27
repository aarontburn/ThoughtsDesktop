package com.beanloaf.thoughtsdesktop.changeListener;

public class Properties {
    /**
     *  This class holds properties that should NOT come with a payload.
     */
    public static class Actions {
        public static final String REFRESH = "refresh";
        public static final String VALIDATE_ITEM_LIST = "validate title";
        public static final String OPEN_SETTINGS = "open settings";
        public static final String OPEN_CLOUD_SETTINGS = "open cloud settings";
        public static final String PUSH_ALL = "push";
        public static final String PULL = "pull";
        public static final String SIGN_OUT = "sign out";
        public static final String REVALIDATE_THOUGHT_LIST = "validate thought list";
        public static final String NEW_FILE_BUTTON_PRESS = "new file button press";
        public static final String TEST = "test";
        public static final String REFRESH_PUSH_PULL_LABELS = "set push pull labels";
        public static final String DATABASE_REFRESH_RATE = "database refresh rate";





    }

    /**
     *  This class holds properties that SHOULD come with a payload.
     */
    public static class Data {
        public static final String SET_TEXT_FIELDS = "set text fields";
        public static final String SORT = "sort";
        public static final String SELECTED_TAG_ITEM = "selected tag";
        public static final String SELECTED_LIST_ITEM = "selected list item";
        public static final String DELETE = "delete";
        public static final String NEW_FILE = "new file";
        public static final String VALIDATE_TAG = "validate tag";
        public static final String PUSH_FILE = "push file";
        public static final String REGISTER_NEW_USER = "register new user";
        public static final String LOG_IN_USER = "log in user";
        public static final String LOG_IN_SUCCESS = "log in success";
        public static final String PULL_PUSH_NUM = "push pull num";
        public static final String REMOVE_FROM_DATABASE = "remove from database";
        public static final String CHECKBOX_PRESSED = "checkbox pressed";
        public static final String PUSH_IN_PROGRESS = "push in progress";
        public static final String PULL_IN_PROGRESS = "pull in progress";
        public static final String SET_IN_DATABASE_DECORATORS = "set in database decorators";




    }
}
