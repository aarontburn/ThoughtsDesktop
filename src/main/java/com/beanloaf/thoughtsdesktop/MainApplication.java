package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.views.ListView;
import com.beanloaf.thoughtsdesktop.views.SettingsView;
import com.beanloaf.thoughtsdesktop.views.ThoughtsMenuBar;
import com.beanloaf.thoughtsdesktop.views.TextView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application implements ThoughtsChangeListener {


    private Scene scene;


    public ListView listView;
    public TextView textView;
    private ThoughtsMenuBar menuBar;
    public FirebaseHandler firebaseHandler;


    public static void main(final String[] args) {
        launch();
    }



    @Override
    public void start(final Stage stage) throws IOException {

        final FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/main_view.fxml"));
        final Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        this.scene = scene;

        stage.setTitle("Thoughts");
        stage.setScene(scene);
        stage.show();


        ThoughtsHelper.getInstance().addListener(this);
        menuBar = new ThoughtsMenuBar(this);
        listView = new ListView(this);
        textView = new TextView(this);


        startup();

        firebaseHandler = new FirebaseHandler(this);

        new Thread(() -> firebaseHandler.startup()).start();

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