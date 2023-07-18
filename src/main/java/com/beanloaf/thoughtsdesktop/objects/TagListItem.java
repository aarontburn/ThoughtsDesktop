package com.beanloaf.thoughtsdesktop.objects;

import com.beanloaf.thoughtsdesktop.views.ListView;
import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class TagListItem extends Button implements Comparable<TagListItem> {

    private final List<ThoughtObject> taggedObjects = new ArrayList<>();

    private final MainApplication main;
    public final ListView listView;
    private final String tag;

    public TagListItem(final MainApplication main, final ListView listView, final String tag) {
        super(tag);

        this.setStyle(TC.CSS.LIST_ITEM);
        this.setOnMouseEntered(e -> this.setStyle(TC.CSS.LIST_ITEM_HOVER));
        this.setOnMouseExited(e -> this.setStyle(TC.CSS.LIST_ITEM));
        this.setWrapText(true);

        this.main = main;
        this.listView = listView;
        this.tag = tag;

        this.setOnMouseClicked(e -> doClick());



    }

    public void doClick() {
        ThoughtsHelper.getInstance().targetEvent(ListView.class, TC.Properties.SELECTED_TAG, this);

        final ObservableList<Node> children = listView.itemList.getChildren();

        children.clear();

        for (final ThoughtObject obj : taggedObjects) {
            final ListItem listItem = new ListItem(obj);
            children.add(listItem);

        }

        ThoughtsHelper.getInstance().fireEvent(TC.Properties.SET_TEXT_FIELDS, get(0));
    }

    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || !this.getClass().equals(other.getClass())) {
            return false;
        }

        return this.tag.equals(((TagListItem) other).getTag());
    }


    public List<ThoughtObject> getList() {
        return this.taggedObjects;
    }


    public TagListItem add(final ThoughtObject obj) {
        if (!this.taggedObjects.contains(obj)) {
            this.taggedObjects.add(obj);
        }
        return this;
    }

    public TagListItem remove(final ThoughtObject obj) {
        this.taggedObjects.remove(obj);

        return this;
    }

    public ThoughtObject get(final int index) {
        try {
            return this.taggedObjects.get(index);
        } catch (Exception e) {
            return this.taggedObjects.size() > 0 ? this.taggedObjects.get(0) : null;
        }

    }

    public ThoughtObject getByFile(final String file) {
        for (final ThoughtObject obj : taggedObjects) {
            if (obj.getFile().equals(file)) return obj;
        }
        return null;

    }

    public int size() {
        return this.taggedObjects.size();
    }

    public boolean isEmpty() {
        return this.taggedObjects.size() == 0;
    }

    public boolean contains(final ThoughtObject obj) {
        return this.taggedObjects.contains(obj);
    }


    public TagListItem clear() {
        this.taggedObjects.clear();
        return this;
    }

    public int indexOf(final ThoughtObject obj) {
        return this.taggedObjects.indexOf(obj);
    }


    @Override
    public int compareTo(final TagListItem tagListItem) {
        return this.getTag().compareToIgnoreCase(tagListItem.getTag());
    }

}
