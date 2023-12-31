package com.beanloaf.thoughtsdesktop.notes.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import javafx.scene.Node;
import javafx.scene.Scene;

public class ThoughtsView {

    public final MainApplication main;
    public Scene scene;

    public ThoughtsView(final MainApplication main) {
        this(main, null);

    }
    
    public ThoughtsView(final MainApplication main, final Scene scene) {
        this.main = main;
        this.scene = scene;
    }

    public void setScene(final Scene scene) {
        this.scene = scene;
    }
    


    public Node findNodeById(final String nodeId) {
        if (nodeId.charAt(0) == '#') throw new IllegalArgumentException("ID's cannot start with #");

        return scene == null ? main.findNodeById(nodeId) : scene.lookup("#" + nodeId);
    }
    
    





}
