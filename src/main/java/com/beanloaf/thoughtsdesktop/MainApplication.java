package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.global_views.GlobalHeaderController;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.global_views.HomeView;
import com.beanloaf.thoughtsdesktop.notes.views.ListView;
import com.beanloaf.thoughtsdesktop.global_views.NotesMenuBar;
import com.beanloaf.thoughtsdesktop.global_views.SettingsView;
import com.beanloaf.thoughtsdesktop.notes.views.TextView;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

import static com.beanloaf.thoughtsdesktop.notes.changeListener.Properties.Actions.*;

public class MainApplication extends Application implements ThoughtsChangeListener {


    private Scene scene;

    private NotesMenuBar notesMenuBar;
    public FirebaseHandler firebaseHandler;
    public SettingsHandler settingsHandler;


    public GlobalHeaderController headerController;


    public HomeView homeView;
    public CalendarMain calendarMain;
    public ListView listView;
    public TextView textView;

    public SettingsView settingsView;


    /*  Layouts  */
    private Node[] layoutList;
    private Node homeRoot, notepadFXML, calendarFXML, settingsFXML;
    public Layouts currentLayout;

    public enum Layouts {
        HOME(0),
        NOTES(1),
        CALENDAR(2),
        SETTINGS(3);

        private final int layoutNum;

        Layouts(final int layoutNum) {
            this.layoutNum = layoutNum;
        }

        public static Layouts getNextLayout(final Layouts layout) {
            final Layouts[] layouts = values();
            return layout.layoutNum == layouts.length - 1 ? layouts[0] : layouts[layout.layoutNum + 1];
        }

        public static Layouts getPreviousLayout(final Layouts layout) {
            final Layouts[] layouts = values();
            return layout.layoutNum <= 0 ? layouts[layouts.length - 1] : layouts[layout.layoutNum - 1];
        }
    }


    public static void main(final String[] args) {
        launch();
    }


    @Override
    public void start(final Stage stage) throws IOException {
        TC.Directories.STORAGE_PATH.mkdirs();


        settingsHandler = SettingsHandler.getInstance();
        ThoughtsHelper.getInstance().addListener(this);


        final Scene scene = new Scene(
                new FXMLLoader(MainApplication.class.getResource("fxml/home_screen.fxml")).load(),
                (Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_WIDTH),
                (Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_HEIGHT));


        this.scene = scene;

        stage.setTitle("Thoughts");
        stage.setScene(scene);

        stage.setX((Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_X));
        stage.setY((Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_Y));
        stage.setMaximized((Boolean) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_MAXIMIZED));

        stage.show();


        scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            homeView.clock.stop();
            firebaseHandler.stopRefresh();
            calendarMain.getCanvasICalHandler().stopRefresh();

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_X, stage.getX());
            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_Y, stage.getY());

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_WIDTH, stage.getWidth());
            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_HEIGHT, stage.getHeight());

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_MAXIMIZED, stage.isMaximized());

        });




        /*  Layouts */
        homeRoot = findNodeById("homeRoot");
        notepadFXML = findNodeById("notepadFXML");
        calendarFXML = findNodeById("calendarFXML");
        settingsFXML = findNodeById("settingsFXML");


        layoutList = new Node[]{homeRoot, notepadFXML, calendarFXML, settingsFXML};
        /*  ------  */


        headerController = (GlobalHeaderController) ThoughtsHelper.getInstance().getController(GlobalHeaderController.class);
        firebaseHandler = new FirebaseHandler(this);


        new Thread(() -> firebaseHandler.startup()).start();
        setKeybindings();
        swapLayouts(Layouts.HOME);

        homeView = new HomeView(this);
        settingsView = new SettingsView(this);


        final MainApplication main = this;
        new Thread(() -> {
            // TODO: these all need to go into a Notepad class
            notesMenuBar = new NotesMenuBar(main);
            textView = new TextView(main);
            listView = new ListView(main);

            calendarMain = new CalendarMain(main);
        }).start();


    }

    public void swapToNextLayout() {
        swapLayouts(Layouts.getNextLayout(currentLayout));
    }

    public void swapToPreviousLayout() {
        swapLayouts(Layouts.getPreviousLayout(currentLayout));
    }

    public void swapLayouts(final Layouts layout) {
        this.currentLayout = layout;
        switch (layout) {
            case NOTES -> {
                headerController.setSelectedTab(headerController.headerNotesButton);
                toggleLayoutVisibility(notepadFXML);
            }
            case HOME -> {
                headerController.setSelectedTab(headerController.headerHomeButton);
                toggleLayoutVisibility(homeRoot);

            }

            case CALENDAR -> {
                headerController.setSelectedTab(headerController.headerCalendarButton);
                toggleLayoutVisibility(calendarFXML);
                if (calendarMain != null) calendarMain.onOpen();

            }
            case SETTINGS -> {
                headerController.setSelectedTab(headerController.headerSettingsButton);
                toggleLayoutVisibility(settingsFXML);


            }

            default -> throw new IllegalArgumentException("Not sure how you got here. Illegal enum passed: " + layout);
        }

    }

    private void toggleLayoutVisibility(final Node visibleLayout) {
        for (final Node node : layoutList) {
            node.setVisible(false);
        }
        visibleLayout.setVisible(true);
    }

    private void setKeybindings() {
        final ObservableMap<KeyCombination, Runnable> keybindings = scene.getAccelerators();

        keybindings.put(new KeyCharacterCombination(KeyCode.Q.getChar(), KeyCombination.CONTROL_DOWN),
                () -> ThoughtsHelper.getInstance().fireEvent(Properties.Data.SORT,
                        ThoughtsHelper.getInstance().getSelectedFile()));

        keybindings.put(new KeyCharacterCombination(KeyCode.D.getChar(), KeyCombination.CONTROL_DOWN),
                () -> ThoughtsHelper.getInstance().fireEvent(Properties.Data.DELETE,
                        ThoughtsHelper.getInstance().getSelectedFile()));

        keybindings.put(new KeyCharacterCombination(KeyCode.N.getChar(), KeyCombination.CONTROL_DOWN),
                () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.NEW_FILE_BUTTON_PRESS));

        keybindings.put(new KeyCharacterCombination(KeyCode.P.getChar(), KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
                () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PULL));

        keybindings.put(new KeyCharacterCombination(KeyCode.P.getChar(), KeyCombination.CONTROL_DOWN),
                () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PUSH_ALL));

        keybindings.put(new KeyCharacterCombination(KeyCode.TAB.getChar(), KeyCombination.CONTROL_DOWN),
                this::swapToNextLayout);

        keybindings.put(new KeyCharacterCombination(KeyCode.TAB.getChar(), KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
                this::swapToPreviousLayout);

        // TODO: this doesn't trigger
        keybindings.put(new KeyCharacterCombination(KeyCode.F5.getChar()),
                () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REFRESH));
    }


    public Node findNodeById(final String id) {
        if (id.charAt(0) == '#') throw new IllegalArgumentException("ID's cannot start with #");

        return this.scene.lookup("#" + id);
    }


    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case OPEN_HOME_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(0);
            }
            case OPEN_NOTES_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(1);

            }
            case OPEN_CALENDAR_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(2);
            }
            case OPEN_CLOUD_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(3);

            }
        }
    }
}