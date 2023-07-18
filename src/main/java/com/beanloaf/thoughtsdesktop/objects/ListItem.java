package com.beanloaf.thoughtsdesktop.objects;

import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import javafx.scene.control.Button;

public class ListItem extends Button {

    private final ThoughtObject obj;


    public ListItem(final ThoughtObject obj) {
        super(obj.getTitle());

        this.obj = obj;
        this.setStyle(TC.CSS.LIST_ITEM);
        this.setOnMouseEntered(e -> this.setStyle(TC.CSS.LIST_ITEM_HOVER));
        this.setOnMouseExited(e -> this.setStyle(TC.CSS.LIST_ITEM));
        this.setWrapText(true);

        this.setOnMouseClicked(e -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.SET_TEXT_FIELDS, obj));


    }

    public ThoughtObject getThoughtObject() {
        return this.obj;
    }



}
