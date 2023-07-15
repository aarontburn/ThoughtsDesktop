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

    opens com.beanloaf.thoughtsdesktop to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop;
    exports com.beanloaf.thoughtsdesktop.changeListener;
    opens com.beanloaf.thoughtsdesktop.changeListener to javafx.fxml;
}