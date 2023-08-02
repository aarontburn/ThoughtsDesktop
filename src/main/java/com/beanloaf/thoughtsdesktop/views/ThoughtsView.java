package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import javafx.scene.Node;

public class ThoughtsView {

    public final MainApplication main;

    public ThoughtsView(final MainApplication main) {
        this.main = main;

    }


    public Node findNodeByID(final String nodeID) {
        return main.findNodeByID(nodeID);
    }





}
