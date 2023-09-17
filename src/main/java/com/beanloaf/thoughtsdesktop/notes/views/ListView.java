package com.beanloaf.thoughtsdesktop.notes.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.database.DatabaseSnapshot;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.notes.objects.ListItem;
import com.beanloaf.thoughtsdesktop.notes.objects.TagListItem;
import com.beanloaf.thoughtsdesktop.notes.objects.ThoughtObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.*;

import static com.beanloaf.thoughtsdesktop.notes.changeListener.Properties.Actions.*;
import static com.beanloaf.thoughtsdesktop.notes.changeListener.Properties.Data.*;
import static com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper.readFileContents;

public class ListView extends ThoughtsView implements ThoughtsChangeListener {


    public final VBox tagList, itemList;

    public final TagListItem unsortedThoughtList, sortedThoughtList;
    public final Map<String, TagListItem> thoughtListByTag = new HashMap<>();
    private TagListItem selectedTagItem;
    private ListItem selectedListItem;
    private final TextField searchBar;


    public ListView(final MainApplication main) {
        super(main);

        ThoughtsHelper.getInstance().addListener(this);

        this.tagList = (VBox) main.findNodeById("tagList");
        this.itemList = (VBox) main.findNodeById("itemList");


        final SplitPane listViewContainer = (SplitPane) findNodeById("listViewContainer");

        listViewContainer.lookupAll(".split-pane-divider").forEach(div -> {
            div.setMouseTransparent(true);
            div.setStyle("-fx-background-color: transparent;");
        });

        this.searchBar = (TextField) findNodeById("searchBar");

        unsortedThoughtList = new TagListItem(this, "Unsorted");
        sortedThoughtList = new TagListItem(this, "Sorted");


        searchBar.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) searchFor(searchBar.getText());
        });


        refreshThoughtList();
    }

    private void refreshThoughtList() {

        final long startTime = System.currentTimeMillis();

        TC.Directories.STORAGE_PATH.mkdir();
        TC.Directories.UNSORTED_DIRECTORY_PATH.mkdir();
        TC.Directories.SORTED_DIRECTORY_PATH.mkdir();


        final File[] unsortedFiles = TC.Directories.UNSORTED_DIRECTORY_PATH.listFiles();
        final File[] sortedFiles = TC.Directories.SORTED_DIRECTORY_PATH.listFiles();

        unsortedThoughtList.clear();
        sortedThoughtList.clear();
        thoughtListByTag.clear();


        if (unsortedFiles == null) throw new RuntimeException("unsortedFiles is null");
        if (sortedFiles == null) throw new RuntimeException("sortedFiles is null");


        for (final File file : unsortedFiles) {
            final ThoughtObject content = readFileContents(file, false);
            if (content != null) unsortedThoughtList.add(content);

        }

        for (final File file : sortedFiles) {
            final ThoughtObject content = readFileContents(file, true);

            if (content != null) {
                sortedThoughtList.add(content);
                final String tag = content.getTag();

                TagListItem list = thoughtListByTag.get(tag);
                if (list == null) { // tag doesn't exist in list yet
                    list = new TagListItem(this, tag);
                    thoughtListByTag.put(tag, list);
                }
                list.add(content);
                content.setParent(list);

            }

        }

        Platform.runLater(() -> {
            tagList.getChildren().clear();
            tagList.getChildren().add(unsortedThoughtList);
            tagList.getChildren().add(sortedThoughtList);

            final List<String> set = new ArrayList<>(thoughtListByTag.keySet());
            set.sort(String.CASE_INSENSITIVE_ORDER);

            for (final String key : set) {
                final TagListItem tagListItem = thoughtListByTag.get(key);
                tagList.getChildren().add(tagListItem);
            }
        });





        unsortedThoughtList.getList().sort(ThoughtObject::compareTo);
        sortedThoughtList.getList().sort(ThoughtObject::compareTo);


        Logger.log("Total refresh time: " + (System.currentTimeMillis() - startTime) + "ms");

    }


    private void searchFor(final String searchText) {


        if (searchText.isEmpty()) {
            tagList.getChildren().clear();

            refreshThoughtList();
            return;

        }

        final long startTime = System.currentTimeMillis();


        final List<ThoughtObject> allThoughtsList = new ArrayList<>();
        allThoughtsList.addAll(unsortedThoughtList.getList());
        allThoughtsList.addAll(sortedThoughtList.getList());


        final TagListItem unsortedSearch = new TagListItem(this, "Unsorted");
        final TagListItem sortedSearch = new TagListItem(this, "Sorted");

        final List<ThoughtObject> thoughtsWithSearchText = new ArrayList<>();

        for (final ThoughtObject obj : allThoughtsList) {
            if (obj.getTitle().toLowerCase().contains(searchText.toLowerCase())
                    || obj.getTag().toLowerCase().contains(searchText.toLowerCase())
                    || obj.getDate().toLowerCase().contains(searchText.toLowerCase())
                    || obj.getBody().toLowerCase().contains(searchText.toLowerCase())) {

                thoughtsWithSearchText.add(obj);

                if (obj.isSorted()) {
                    sortedSearch.add(obj);
                } else {
                    unsortedSearch.add(obj);
                }


            }

        }

        tagList.getChildren().clear();

        tagList.getChildren().add(unsortedSearch);
        tagList.getChildren().add(sortedSearch);


        final Map<String, TagListItem> searchTagListItemList = new HashMap<>();

        for (final ThoughtObject obj : thoughtsWithSearchText) {
            final String tag = obj.getTag();


            TagListItem list = searchTagListItemList.get(tag);
            if (list == null) { // tag doesn't exist in list yet
                list = new TagListItem(this, tag);
                searchTagListItemList.put(tag, list);
            }
            list.add(obj);

        }

        final List<String> set = new ArrayList<>(searchTagListItemList.keySet());
        set.sort(String.CASE_INSENSITIVE_ORDER);

        for (final String key : set) {
            final TagListItem tagListItem = searchTagListItemList.get(key);

            tagList.getChildren().add(tagListItem);

        }


        Logger.log("Search time: " + (System.currentTimeMillis() - startTime) + "ms");


    }

    private void newFile(final ThoughtObject obj, final boolean titleLocked, final boolean tagLocked, final boolean bodyLocked) {
        final ThoughtObject newObject = new ThoughtObject(
                titleLocked ? obj.getTitle() : "",
                tagLocked ? obj.getTag() : "",
                bodyLocked ? obj.getBody() : "");
        newObject.save();
        unsortedThoughtList.add(newObject);

        unsortedThoughtList.doClick();
        ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS, newObject);

    }


    private void delete(final ThoughtObject obj) {

        if (obj == null) {
            return;
        }
        Logger.log("Deleting " + obj.getTitle());


        Node nodeToRemove = null;
        final ObservableList<Node> children = itemList.getChildrenUnmodifiable();
        int index = -1;

        for (int i = 0; i < children.size(); i++) {
            final Node node = children.get(i);
            if (node.getClass() != ListItem.class) continue;

            final ListItem listItem = (ListItem) node;

            if (listItem.getThoughtObject().equals(obj)) {
                index = i;
                nodeToRemove = node;
                break;
            }
        }


        if (!obj.isSorted()) { // object is unsorted

            unsortedThoughtList.remove(obj);

            if (index > 0 && index < unsortedThoughtList.size()) {
                index--;
            } else if (index >= unsortedThoughtList.size()) {
                index = unsortedThoughtList.size() - 1;
            }

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS,
                    index >= 0 && unsortedThoughtList.size() > 0 ? unsortedThoughtList.get(index) : new Object());

        } else {    // object is sorted
            sortedThoughtList.remove(obj);
            removeTagFromTagList(obj);

            if (index > 0 && index < selectedTagItem.size()) {
                index--;
            } else if (index >= selectedTagItem.size()) {
                index = selectedTagItem.size() - 1;
            }

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS,
                    index >= 0 && selectedTagItem.size() > 0 ? selectedTagItem.get(index) : new Object());


        }

        ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REFRESH_PUSH_PULL_LABELS);


        obj.delete();

        if (nodeToRemove != null) itemList.getChildren().remove(nodeToRemove);


    }


    private void sort(final ThoughtObject obj) {
        if (obj == null) {
            return;
        }


        Logger.log((obj.isSorted() ? "Unsorting " : "Sorting ") + obj.getTitle());

        obj.sort();

        if (obj.isSorted()) { // sort
            unsortedThoughtList.remove(obj);
            sortedThoughtList.add(obj);

            obj.setParent(addTagToTagList(obj));


        } else { // unsort

            sortedThoughtList.remove(obj);
            unsortedThoughtList.add(obj);

            removeTagFromTagList(obj);


            ThoughtsHelper.getInstance().targetEvent(FirebaseHandler.class, Properties.Data.REMOVE_FROM_DATABASE, obj);
        }


        for (final Node node : itemList.getChildren()) {
            if (node.getClass() != ListItem.class) continue;

            final ListItem listItem = (ListItem) node;

            if (listItem.getThoughtObject().equals(obj)) {
                itemList.getChildren().remove(node);
                break;
            }

        }

        ThoughtsHelper.getInstance().targetEvent(FirebaseHandler.class, Properties.Actions.REFRESH_PUSH_PULL_LABELS);


    }

    private void removeTagFromTagList(final ThoughtObject obj) {
        final TagListItem list = obj.getParent();

        if (list == null) return;

        list.remove(obj);

        if (list.isEmpty()) {
            thoughtListByTag.remove(list.getTag());
            // remove tag from view

            tagList.getChildren().remove(list);

        }
    }

    private TagListItem addTagToTagList(final ThoughtObject obj) {
        final String tag = obj.getTag();

        TagListItem list = thoughtListByTag.get(tag);

        if (list == null) { // tag doesn't exist in list yet
            list = new TagListItem(this, tag);
            thoughtListByTag.put(tag, list);

            tagList.getChildren().add(getAlphaIndex(list), list);

        }
        list.add(obj);
        return list;
    }

    private int getAlphaIndex(final TagListItem tagListItem) {
        return binaryGetIndex(tagListItem.getTag(), 2, tagList.getChildren().size() - 1);

    }

    private int binaryGetIndex(final String tag, final int left, final int right) {
        int mid = left + (right - left) / 2;

        if (left > right) {
            return right + 1;
        }

        Node m;
        Node n;

        try {
            m = tagList.getChildren().get(mid);
        } catch (Exception e) {
            m = null;
        }

        try {
            n = tagList.getChildren().get(mid + 1);
        } catch (Exception e) {
            n = null;
        }


        TagListItem midItem = null;
        TagListItem nextItem = null;

        if (m != null && m.getClass() == TagListItem.class) {
            midItem = (TagListItem) m;
        }
        if (n != null && n.getClass() == TagListItem.class) {
            nextItem = (TagListItem) n;
        }

        if (midItem == null || nextItem == null) {

            return mid + 1;
        }

        final int midToTag = tag.compareToIgnoreCase(midItem.getTag());
        final int nextToTag = tag.compareToIgnoreCase(nextItem.getTag());


        if (midToTag < 0) {
            return binaryGetIndex(tag, left, mid - 1);

        } else {
            if (nextToTag < 0) {
                return mid + 1;
            } else {
                return binaryGetIndex(tag, mid + 1, right);
            }
        }


    }


    private void validateTag(final ThoughtObject obj) {
        if (obj == null) {
            return;
        }


        final String tag = obj.getTag();

        final TagListItem list = obj.getParent();

        if (list != null) {
            if (!list.getTag().equals(tag)) {
                // add to new list
                final TagListItem newList = addTagToTagList(obj);
                // remove from old list
                removeTagFromTagList(obj);

                obj.setParent(newList);

            }
        }

    }

    private void validateItemList() {
        for (final Node node : itemList.getChildren()) {
            if (node.getClass() != ListItem.class) continue;

            final ListItem listItem = (ListItem) node;

            if (!listItem.getText().equals(listItem.getThoughtObject().getTitle())) {
                listItem.setText(listItem.getThoughtObject().getTitle());
            }


            if (listItem.inDatabaseDecorator.isVisible() != listItem.getThoughtObject().isInDatabase()) {
                listItem.setDecorator(ListItem.Decorators.IN_DATABASE, listItem.getThoughtObject().isInDatabase());
            }

            if (listItem.localOnlyDecorator.isVisible() != listItem.getThoughtObject().isLocalOnly()) {
                listItem.setDecorator(ListItem.Decorators.LOCAL_ONLY, listItem.getThoughtObject().isLocalOnly());
            }


        }


    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case REFRESH -> {
                refreshThoughtList();


                for (final Node node : tagList.getChildren()) {
                    if (node.getClass() != TagListItem.class) continue;

                    final TagListItem tagListItem = (TagListItem) node;

                    if (selectedTagItem.equals(tagListItem)) {
                        selectedTagItem = tagListItem;
                        selectedTagItem.doClick();
                        break;
                    }

                }


            }
            case SORT -> sort((ThoughtObject) eventValue);
            case SELECTED_TAG_ITEM -> {
                selectedTagItem = (TagListItem) eventValue;

                for (Node node : tagList.getChildren()) {
                    if (node.getClass() != TagListItem.class) continue;
                    node.getStyleClass().remove("tagListSelected");
                }



            }
            case DELETE -> delete((ThoughtObject) eventValue);
            case NEW_FILE -> {
                final Object[] data = (Object[]) eventValue;

                newFile((ThoughtObject) data[0], (boolean) data[1], (boolean) data[2], (boolean) data[3]);
            }
            case VALIDATE_TAG -> validateTag((ThoughtObject) eventValue);
            case VALIDATE_ITEM_LIST -> validateItemList();
            case REVALIDATE_THOUGHT_LIST -> {
                for (final ThoughtObject obj : unsortedThoughtList.getList()) {
                    obj.save();
                }

                for (final ThoughtObject obj : sortedThoughtList.getList()) {
                    obj.save();
                }
            }
            case SELECTED_LIST_ITEM -> {

                this.selectedListItem = (ListItem) eventValue;


                for (final Node node : itemList.getChildren()) {
                    if (node.getClass() != ListItem.class) continue;

                    node.getStyleClass().remove("itemListSelected");
                }


                selectedListItem.getStyleClass().add("itemListSelected");

            }
            case CHECKBOX_PRESSED -> {
                if (selectedListItem == null) return;
                this.selectedListItem.setDecorator(ListItem.Decorators.LOCAL_ONLY, (boolean) eventValue);
            }
            case SET_IN_DATABASE_DECORATORS -> Platform.runLater(() -> {
                final DatabaseSnapshot snapshot = main.firebaseHandler.databaseSnapshot;

                final List<ThoughtObject> objectsInDatabase = snapshot.findObjectsInDatabase(sortedThoughtList.getList());

                for (final ThoughtObject obj : sortedThoughtList.getList()) {
                    obj.setInDatabase(objectsInDatabase.contains(obj));

                }

                validateItemList();
            });
        }

    }
}
