package com.beanloaf.thoughtsdesktop.objects;

import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.views.ListView;
import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class TagListItem extends AnchorPane implements Comparable<TagListItem> {

    private final List<ThoughtObject> taggedObjects = new ArrayList<>();

    private final MainApplication main;
    public final ListView listView;
    private final String tag;

    private final Button button;

    public TagListItem(final MainApplication main, final ListView listView, final String tag) {
        super();
        this.getStyleClass().add("listViewItem");

        this.main = main;
        this.listView = listView;
        this.tag = tag;


        button = new Button(tag);
        button.setOnAction(e -> {
            ThoughtsHelper.getInstance().targetEvent(ListView.class, Properties.Data.SELECTED_TAG_ITEM, this);

            final ObservableList<Node> children = listView.itemList.getChildren();

            children.clear();

            for (final ThoughtObject obj : taggedObjects) {
                final ListItem listItem = new ListItem(obj);
                children.add(listItem);

            }

            if (get(0) != null)
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS, get(0));
        });

        this.getChildren().add(TC.Tools.setAnchor(button, 0.0, 0.0, 0.0, 0.0));



    }

    public void doClick() {
        button.fire();

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
