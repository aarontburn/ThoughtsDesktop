package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.views.ListView;
import com.beanloaf.thoughtsdesktop.views.SettingsView;
import com.beanloaf.thoughtsdesktop.views.ThoughtsMenuBar;
import com.beanloaf.thoughtsdesktop.views.TextView;
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

public class MainApplication extends Application implements ThoughtsChangeListener {


    private Scene scene;


    public ListView listView;
    public TextView textView;
    private ThoughtsMenuBar menuBar;
    public FirebaseHandler firebaseHandler;
    public SettingsHandler settingsHandler;


    public static void main(final String[] args) {
        launch();
    }


    @Override
    public void start(final Stage stage) throws IOException {
        settingsHandler = new SettingsHandler();


        final FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/main_view.fxml"));
        final Scene scene = new Scene(fxmlLoader.load(),
                (Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_WIDTH),
                (Double)  settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_HEIGHT));
        this.scene = scene;

        stage.setTitle("Thoughts");
        stage.setScene(scene);

        stage.setX((Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_X));
        stage.setY((Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_Y));

        stage.setMaximized((Boolean) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_MAXIMIZED));

        stage.show();

        scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_X, stage.getX());
            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_Y, stage.getY());

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_WIDTH, stage.getWidth());
            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_HEIGHT, stage.getHeight());

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_MAXIMIZED, stage.isMaximized());


            if (SettingsView.isInstanceActive()) SettingsView.closeWindow();



        });


        ThoughtsHelper.getInstance().addListener(this);
        menuBar = new ThoughtsMenuBar(this);
        listView = new ListView(this);
        textView = new TextView(this);


        startup();

        firebaseHandler = new FirebaseHandler(this);

        new Thread(() -> firebaseHandler.startup()).start();

        setKeybindings();


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

        keybindings.put(new KeyCharacterCombination(KeyCode.F5.getChar()),
                () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REFRESH));


    }

    private void startup() {
        listView.unsortedThoughtList.doClick();
    }

    public Node findNodeByID(final String id) {
        if (id.charAt(0) == '#') throw new IllegalArgumentException("ID's cannot start with #");

        return scene.lookup("#" + id);


    }


    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case Properties.Actions.OPEN_SETTINGS -> SettingsView.getInstance(this);
            case Properties.Actions.OPEN_CLOUD_SETTINGS -> SettingsView.getInstance(this).setSelectedTab(1);
        }
    }
}