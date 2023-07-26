package com.beanloaf.thoughtsdesktop.objects;

import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.views.ListView;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper.setAnchor;

public class TagListItem extends AnchorPane implements Comparable<TagListItem> {

    private final List<ThoughtObject> taggedObjects = new ArrayList<>();

    public final ListView listView;
    private final String tag;

    private final Button button;
    private final List<DecoratorText> decorators = new ArrayList<>();


    public final DecoratorText pinnedDecorator;


    public TagListItem(final ListView listView, final String tag) {
        super();
        this.getStyleClass().add("tagList");

        this.listView = listView;
        this.tag = tag;


        button = new Button(tag);
        button.setOnAction(e -> {

            taggedObjects.sort(ThoughtObject::compareTo);

            ThoughtsHelper.getInstance().targetEvent(ListView.class, Properties.Data.SELECTED_TAG_ITEM, this);
            this.getStyleClass().add("tagListSelected");


            final ObservableList<Node> children = listView.itemList.getChildren();

            children.clear();

            ListItem firstObj = null;

            for (int i = 0; i < taggedObjects.size(); i++) {

                final ListItem listItem = new ListItem(taggedObjects.get(i));
                if (i == 0) firstObj = listItem;

                children.add(listItem);
            }


            if (firstObj != null) {
                firstObj.doClick();
            } else {
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS, new Object());
            }

        });

        this.getChildren().add(setAnchor(button, 0.0, 0.0, 0.0, 0.0));


        pinnedDecorator = new DecoratorText("P");
        this.getChildren().add(pinnedDecorator);

        pinnedDecorator.setVisible(false);

        this.setOnMouseEntered(e -> showDecorators());
        this.setOnMouseExited(e -> hideInactiveDecorators());

    }

    private void showDecorators() {
        for (final DecoratorText decorator : decorators) {
            decorator.setVisible(true);
        }
    }

    private void hideInactiveDecorators() {

        for (final DecoratorText decorator : decorators) {
            if (!decorator.isActive()) decorator.setVisible(false);
        }

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


    public class DecoratorText extends Text {

        private boolean isActive;

        public DecoratorText(final String text) {
            super(text);
            this.setStyle("-fx-fill: #B2B2B2; -fx-font-size: 16;");
            setAnchor(this, 0.0, null, 4.0, null);
            decorators.add(this);


            this.setOnMouseClicked(e -> setActive(!isActive()));

        }



        public boolean isActive() {
            return this.isActive;
        }

        public void setActive(final boolean isActive) {
            this.isActive = isActive;
        }

    }

}
