package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.DatabaseSnapshot;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.objects.ListItem;
import com.beanloaf.thoughtsdesktop.objects.TagListItem;
import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.*;

import static com.beanloaf.thoughtsdesktop.changeListener.Properties.Actions.*;
import static com.beanloaf.thoughtsdesktop.changeListener.Properties.Data.*;
import static com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper.readFileContents;

public class ListView implements ThoughtsChangeListener {

    private final MainApplication main;

    public final VBox tagList, itemList;
    private final SplitPane listViewContainer; // houses both the tagList and itemList

    public final TagListItem unsortedThoughtList, sortedThoughtList;
    public final Map<String, TagListItem> thoughtListByTag = new HashMap<>();
    private TagListItem selectedTagItem;
    private ListItem selectedListItem;

    private final TextField searchBar;


    public ListView(final MainApplication main) {
        this.main = main;

        ThoughtsHelper.getInstance().addListener(this);

        this.tagList = (VBox) main.findNodeByID("tagList");
        this.itemList = (VBox) main.findNodeByID("itemList");
        this.listViewContainer = (SplitPane) main.findNodeByID("listViewContainer");


        this.listViewContainer.lookupAll(".split-pane-divider").forEach(div -> {
            div.setMouseTransparent(true);
            div.setStyle("-fx-background-color: transparent;");
        });


        this.searchBar = (TextField) main.findNodeByID("searchBar");

        unsortedThoughtList = new TagListItem(main, this, "Unsorted");
        sortedThoughtList = new TagListItem(main, this, "Sorted");




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
        tagList.getChildren().clear();

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
                    list = new TagListItem(main, this, tag);
                    thoughtListByTag.put(tag, list);
                }
                list.add(content);
                content.setParent(list);

            }

        }

        tagList.getChildren().add(unsortedThoughtList);
        tagList.getChildren().add(sortedThoughtList);

        final List<String> set = new ArrayList<>(thoughtListByTag.keySet());
        set.sort(String.CASE_INSENSITIVE_ORDER);

        for (final String key : set) {
            final TagListItem tagListItem = thoughtListByTag.get(key);

            tagList.getChildren().add(tagListItem);

        }


        unsortedThoughtList.getList().sort(ThoughtObject::compareTo);
        sortedThoughtList.getList().sort(ThoughtObject::compareTo);


        System.out.println("Total refresh time: " + (System.currentTimeMillis() - startTime) + "ms");

    }


    private void searchFor(final String searchText) {
        final long startTime = System.currentTimeMillis();


        final List<ThoughtObject> allThoughtsList = new ArrayList<>();
        allThoughtsList.addAll(unsortedThoughtList.getList());
        allThoughtsList.addAll(sortedThoughtList.getList());

        final List<ThoughtObject> thoughtsWithSearchText = new ArrayList<>();

        for (final ThoughtObject obj : allThoughtsList) {
            if (obj.getTitle().contains(searchText)
                    || obj.getTag().contains(searchText)
                    || obj.getDate().contains(searchText)
                    || obj.getBody().contains(searchText)) {

                thoughtsWithSearchText.add(obj);
            }

        }

        System.out.println(thoughtsWithSearchText);


        for (final Node node : tagList.getChildren()) {
            if (node.getClass() != TagListItem.class) continue;

            final TagListItem tagListItem = (TagListItem) node;

            for (final ThoughtObject obj : thoughtsWithSearchText) {
                if (tagListItem.getTag().equals(obj.getTag())) {


                }


            }

        }

        System.out.println("Search time: " + (System.currentTimeMillis() - startTime) + "ms");


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
        System.out.println("Deleting " + obj.getTitle());

        if (!obj.isSorted()) { // object is unsorted
            int index = unsortedThoughtList.indexOf(obj);

            unsortedThoughtList.remove(obj);

            if (index > 0 && index < unsortedThoughtList.size()) {
                index--;
            } else if (index >= unsortedThoughtList.size()) {
                index = unsortedThoughtList.size() - 1;
            }

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS,
                    unsortedThoughtList.size() - 1 >= 0 ? unsortedThoughtList.get(index) : null);


        } else {    // object is sorted
            final TagListItem list = obj.getParent();

            if (list == null) return;

            int index = list.indexOf(obj);

            sortedThoughtList.remove(obj);
            removeTagFromTagList(obj);


            if (index > 0 && index < selectedTagItem.size()) {
                index--;
            } else if (index >= selectedTagItem.size()) {
                index = selectedTagItem.size() - 1;
            }


            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS,
                    selectedTagItem.size() - 1 >= 0 ? selectedTagItem.get(index) : null);

        }

        obj.delete();

        for (final Node node : itemList.getChildren()) {
            if (node.getClass() != ListItem.class) continue;

            final ListItem listItem = (ListItem) node;

            if (listItem.getThoughtObject().equals(obj)) {
                itemList.getChildren().remove(node);
                break;
            }

        }

    }


    private void sort(final ThoughtObject obj) {
        if (obj == null) {
            return;
        }


        System.out.println((obj.isSorted() ? "Unsorting " : "Sorting ") + obj.getTitle());

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
            list = new TagListItem(main, this, tag);
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


            if (listItem.inDatabaseDecorator.isVisible() != listItem.getThoughtObject().isInDatabase()){
                listItem.setDecorator(ListItem.Decorators.IN_DATABASE, listItem.getThoughtObject().isInDatabase());
            }

            if (listItem.localOnlyDecorator.isVisible() != listItem.getThoughtObject().isLocalOnly()){
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

                for (final Node node : tagList.getChildren()) {
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
            case SET_IN_DATABASE_DECORATORS -> new Thread(() -> {
                final DatabaseSnapshot snapshot = (DatabaseSnapshot) eventValue;

                final List<ThoughtObject> objectsInDatabase = snapshot.findObjectsInDatabase(sortedThoughtList.getList());

                for (final ThoughtObject obj : sortedThoughtList.getList()) {
                    obj.setInDatabase(objectsInDatabase.contains(obj));
                }

                validateItemList();
            }).start();
        }

    }
}
