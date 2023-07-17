package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.objects.ListItem;
import com.beanloaf.thoughtsdesktop.objects.TagListItem;
import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListView implements ThoughtsChangeListener {

    private final MainApplication main;

    public final VBox tagList, itemList;
    private final SplitPane listViewContainer; // houses both the tagList and itemList

    public final TagListItem unsortedThoughtList, sortedThoughtList;
    public final Map<String, TagListItem> thoughtListByTag = new HashMap<>();
    private TagListItem selectedTag;



    public ListView(final MainApplication main) {
        this.main = main;

        ThoughtsHelper.getInstance().addListener(this);

        this.tagList = (VBox) main.findNodeByID("tagList");
        this.itemList = (VBox) main.findNodeByID("itemList");
        this.listViewContainer = (SplitPane) main.findNodeByID("listViewContainer");




        this.listViewContainer.lookupAll(".split-pane-divider").forEach(div ->  {
            div.setMouseTransparent(true);
            div.setStyle("-fx-background-color: transparent;");
        });




        
        unsortedThoughtList = new TagListItem(main, this, "Unsorted");
        sortedThoughtList = new TagListItem(main, this, "Sorted");


        refreshThoughtList();
    }

    public void refreshThoughtList() {
        final long startTime = System.currentTimeMillis();

        TC.Paths.STORAGE_PATH.mkdir();
        TC.Paths.UNSORTED_DIRECTORY_PATH.mkdir();
        TC.Paths.SORTED_DIRECTORY_PATH.mkdir();


        final File[] unsortedFiles = TC.Paths.UNSORTED_DIRECTORY_PATH.listFiles();
        final File[] sortedFiles = TC.Paths.SORTED_DIRECTORY_PATH.listFiles();

        unsortedThoughtList.clear();
        sortedThoughtList.clear();
        thoughtListByTag.clear();
        tagList.getChildren().clear();



        for (final File file : unsortedFiles) {
            final ThoughtObject content = readFileContents(file, false);

            if (content != null) {
                unsortedThoughtList.add(content);
            }
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
            tagList.getChildren().add(thoughtListByTag.get(key));

        }


        unsortedThoughtList.getList().sort(ThoughtObject::compareTo);
        sortedThoughtList.getList().sort(ThoughtObject::compareTo);


        System.out.println("Total refresh time: " + (System.currentTimeMillis() - startTime) + "ms");

    }

    public ThoughtObject readFileContents(final File filePath, final boolean isSorted) {
        try {
            final String jsonString = new String(Files.readAllBytes(filePath.toPath()));
            final JSONObject data = (JSONObject) JSONValue.parse(jsonString);

            return new ThoughtObject(isSorted,
                    data.get("title").toString().trim(),
                    data.get("date").toString().trim(),
                    data.get("tag").toString().trim(),
                    data.get("body").toString().trim(),
                    filePath);

        } catch (Exception e) {
            System.err.println("Found invalid file " + filePath.toPath());
        }
        return null;
    }

    public void newFile() {
        final ThoughtObject newObject = new ThoughtObject("", "", "");
        newObject.save();
        unsortedThoughtList.add(newObject);

        unsortedThoughtList.doClick();
        ThoughtsHelper.getInstance().fireEvent(TC.Properties.SET_TEXT_FIELDS, newObject);

    }

    public void delete(final ThoughtObject obj) {

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

            ThoughtsHelper.getInstance().fireEvent(TC.Properties.SET_TEXT_FIELDS,
                    unsortedThoughtList.size() - 1 >= 0 ? unsortedThoughtList.get(index) : null);



        } else {    // object is sorted
            final TagListItem list = obj.getParent();

            if (list == null) return;

            int index = list.indexOf(obj);

            sortedThoughtList.remove(obj);
            removeTagFromTagList(obj);


            if (index > 0 && index < selectedTag.size()) {
                index--;
            } else if (index >= selectedTag.size()) {
                index = selectedTag.size() - 1;
            }


            ThoughtsHelper.getInstance().fireEvent(TC.Properties.SET_TEXT_FIELDS,
                    selectedTag.size() - 1 >= 0 ? selectedTag.get(index) : null);

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


    public void sort(final ThoughtObject obj) {
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
        }


        for (final Node node : itemList.getChildren()) {
            if (node.getClass() != ListItem.class) continue;

            final ListItem listItem = (ListItem) node;

            if (listItem.getThoughtObject().equals(obj)) {
                itemList.getChildren().remove(node);
                break;
            }

        }

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


    public void validateTag(final ThoughtObject obj) {
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

    public void validateItemListTitles() {
        for (final Node node : itemList.getChildren()) {
            if (node.getClass() != ListItem.class) continue;

            final ListItem listItem = (ListItem) node;


            if (!listItem.getText().equals(listItem.getThoughtObject().getTitle()))
                listItem.setText(listItem.getThoughtObject().getTitle());

        }




    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case TC.Properties.REFRESH -> refreshThoughtList();
            case TC.Properties.SORT -> sort((ThoughtObject) eventValue);
            case TC.Properties.SELECTED_TAG -> selectedTag = (TagListItem) eventValue;
            case TC.Properties.DELETE -> delete((ThoughtObject) eventValue);
            case TC.Properties.NEW_FILE -> newFile();
            case TC.Properties.VALIDATE_TAG -> validateTag((ThoughtObject) eventValue);
            case TC.Properties.VALIDATE_TITLE -> validateItemListTitles();
        }

    }
}
