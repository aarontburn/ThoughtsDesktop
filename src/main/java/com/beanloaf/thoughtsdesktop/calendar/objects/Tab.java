package com.beanloaf.thoughtsdesktop.calendar.objects;

import com.beanloaf.thoughtsdesktop.calendar.handlers.Calendar;
import com.beanloaf.thoughtsdesktop.calendar.views.CalendarView;
import com.beanloaf.thoughtsdesktop.calendar.views.TabController;
import javafx.scene.Node;

public abstract class Tab {

    private final CalendarView view;
    private final TabController tabController;

    public Tab(final CalendarView view, final TabController tabController) {
        this.view = view;
        this.tabController = tabController;
    }

    protected abstract void locateNodes();

    protected abstract void attachEvents();

    protected abstract void createGUI();

    public CalendarView getView() {
        return this.view;
    }

    public TabController getTabController() {
        return this.tabController;
    }

    public Node findNodeById(final String nodeID) {
        return view.findNodeById(nodeID);
    }






}
