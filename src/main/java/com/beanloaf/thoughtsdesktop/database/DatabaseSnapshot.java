package com.beanloaf.thoughtsdesktop.database;

import com.beanloaf.thoughtsdesktop.notes.objects.ThoughtObject;

import java.util.*;

public class DatabaseSnapshot {

    private final Map<String, ThoughtObject> databaseList = new HashMap<>();

    public DatabaseSnapshot() {

    }


    /**
     * This checks if the Thought is in the database, meaning it needs to have IDENTICAL fields. If any of the fields
     * is not equal, it will not add it to the list.
     * <p>
     * NOTE: This will ignore any objects that are local only (obj.isLocalOnly()).
     * This means that the provided lists will only deal with files that are eligible to be put into the database.
     *
     * @param otherList The list to compare to, most likely main.listview.sortedThoughtList();
     * @return A list of items that ARE PRESENT in the database
     */
    public List<ThoughtObject> findObjectsInDatabase(final List<ThoughtObject> otherList) {
        final List<ThoughtObject> inDatabaseList = new ArrayList<>();

        for (final ThoughtObject obj : otherList) {
            if (obj.isLocalOnly()) continue;

            final ThoughtObject databaseObj = this.databaseList.get(obj.getFile());

            if (databaseObj != null) {
                inDatabaseList.add(databaseObj);
            }

        }

        return inDatabaseList;

    }

    /**
     * This checks if the Thought is NOT in the database.
     * <p>
     * NOTE: This will ignore any objects that are local only (obj.isLocalOnly()).
     * This means that the provided lists will only deal with files that are eligible to be put into the database.
     *
     * @param otherList The list to compare to, most likely main.listview.sortedThoughtList();
     * @return A list of items that are NOT present in the database
     */

    public List<ThoughtObject> findObjectsNotInDatabase(final List<ThoughtObject> otherList) {
        final List<ThoughtObject> notInDatabaseList = new ArrayList<>();

        for (final ThoughtObject obj : otherList) {

            if (obj.isLocalOnly()) continue;

            final ThoughtObject databaseObj = this.databaseList.get(obj.getFile());

            if (databaseObj == null || !databaseObj.equals(obj) ) {
                notInDatabaseList.add(obj);

            }
        }

        return notInDatabaseList;
    }


    /**
     * This checks if the Thought is in the database but not on the local system.
     * <p>
     * NOTE: This will ignore any objects that are local only (obj.isLocalOnly()).
     * This means that the provided lists will only deal with files that are eligible to be put into the database.
     *
     * @param localList The list to compare to, most likely main.listview.sortedThoughtList();
     * @return A list of items that are present in the database but not on the local system
     */
    public List<ThoughtObject> findObjectsNotOnLocal(final List<ThoughtObject> localList) {
        final List<ThoughtObject> notOnLocalList = new ArrayList<>();


        final Map<String, ThoughtObject> localMap = new HashMap<>();

        for (final ThoughtObject obj : localList) {
            localMap.put(obj.getFile(), obj);

        }



        for (final String fileName : databaseList.keySet()) {
            if (!localMap.containsKey(fileName)) {
                notOnLocalList.add(databaseList.get(fileName));

            }
        }

        return notOnLocalList;
    }



    public void add(final ThoughtObject obj) {
        this.databaseList.put(obj.getFile(), obj);
    }

    public void clear() {
        databaseList.clear();
    }

    public Integer size() {
        return databaseList.size();
    }

    public List<ThoughtObject> getList() {
        return new ArrayList<>(databaseList.values());
    }


}
