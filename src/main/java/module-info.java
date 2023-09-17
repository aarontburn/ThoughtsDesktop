module com.beanloaf.thoughtsdesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires json.simple;
    requires java.desktop;
    requires com.google.common;
    requires org.apache.commons.codec;
    requires annotations;
    requires kotlin.stdlib;
    requires net.sf.biweekly;

    opens com.beanloaf.thoughtsdesktop to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.notes.changeListener;
    opens com.beanloaf.thoughtsdesktop.notes.changeListener to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.notes.objects;
    opens com.beanloaf.thoughtsdesktop.notes.objects to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.global_views;
    opens com.beanloaf.thoughtsdesktop.global_views to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.res;
    opens com.beanloaf.thoughtsdesktop.res to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop;
    exports com.beanloaf.thoughtsdesktop.database;
    opens com.beanloaf.thoughtsdesktop.database to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.calendar.objects;
    opens com.beanloaf.thoughtsdesktop.calendar.objects to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.calendar.views;
    opens com.beanloaf.thoughtsdesktop.calendar.views to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.notes.views;
    opens com.beanloaf.thoughtsdesktop.notes.views to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.calendar.objects.schedule;
    opens com.beanloaf.thoughtsdesktop.calendar.objects.schedule to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.calendar.enums;
    opens com.beanloaf.thoughtsdesktop.calendar.enums to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel;
    opens com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children;
    opens com.beanloaf.thoughtsdesktop.calendar.views.children.right_panel.children to javafx.fxml;
    exports com.beanloaf.thoughtsdesktop.calendar.views.children.overlays;
    opens com.beanloaf.thoughtsdesktop.calendar.views.children.overlays to javafx.fxml;
}