module com.example.thoughtsdesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires CustomStage;
    requires json.simple;
    requires java.desktop;
    requires com.google.common;
    requires org.apache.commons.codec;

    opens com.beanloaf.thoughtsdesktop to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.changeListener;
    opens com.beanloaf.thoughtsdesktop.changeListener to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.objects;
    opens com.beanloaf.thoughtsdesktop.objects to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.views;
    opens com.beanloaf.thoughtsdesktop.views to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.res;
    opens com.beanloaf.thoughtsdesktop.res to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop;
    exports com.beanloaf.thoughtsdesktop.controllers;
    opens com.beanloaf.thoughtsdesktop.controllers to javafx.fxml;
}