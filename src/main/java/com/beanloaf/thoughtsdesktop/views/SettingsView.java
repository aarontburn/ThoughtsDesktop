package com.beanloaf.thoughtsdesktop.views;


public class SettingsView {

    private static SettingsView instance;

    public static SettingsView getInstance() {
        if (instance == null) {
            instance = new SettingsView();
        }
        return instance;
    }

    private SettingsView() {


    }




}
