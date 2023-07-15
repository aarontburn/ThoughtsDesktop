package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import javafx.scene.control.Button;

public class ListItem extends Button {

    private final ThoughtObject obj;


    public ListItem(final ThoughtObject obj) {
        super(obj.getTitle());

        this.obj = obj;
        this.setStyle(TC.LIST_ITEM_CSS);

        this.setOnMouseClicked(e -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.SET_TEXT_FIELDS, obj));


    }

    public ThoughtObject getThoughtObject() {
        return this.obj;
    }



}
