package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application  {


    public ListView listView;
    public TextView textView;

    private Scene scene;

    public static void main(final String[] args) {
        launch();
    }



    @Override
    public void start(final Stage stage) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main_view.fxml"));
        final Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        this.scene = scene;

        stage.setTitle("Thoughts");
        stage.setScene(scene);
        stage.show();

        listView = new ListView(this);
        textView = new TextView(this);

        startup();
    }

    private void startup() {
        listView.unsortedThoughtList.doClick();
    }

    public Node getNodeByID(final String id) {
        if (id.charAt(0) == '#') throw new RuntimeException("ID's cannot start with #");

        return scene.lookup("#" + id);


    }



}