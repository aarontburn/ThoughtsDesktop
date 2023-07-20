package com.beanloaf.thoughtsdesktop.changeListener;

import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;

import java.util.*;

public class DatabaseSnapshot {

    private final Map<String, ThoughtObject> databaseList = new HashMap<>();

    public DatabaseSnapshot() {

    }




    /**
     *
     * This checks if the Thought is in the database, meaning it needs to have IDENTICAL fields. If any of the fields
     *      is not equal, it will not add it to the database.
     *
     *      NOTE: This will ignore any objects that are local only (obj.isLocalOnly()).
     *          This means that the provided lists will only deal with files that are eligible to be put into the database.
     *
     *
     * @param otherList The list to compare to, most likely main.listview.sortedThoughtList();
     * @param inDatabase Boolean; if true, will return a list of objects that are present in both the database and in the other list.
     *                   If false, will return a list of objects that are NOT in the database but in the other list.
     *
     * @return A list of items that ARE PRESENT in the database
     */
    public List<ThoughtObject> findObjectsInDatabase(final List<ThoughtObject> otherList, final boolean inDatabase) {
        final List<ThoughtObject> databaseList = new ArrayList<>();
        final List<ThoughtObject> notInDatabaseList = new ArrayList<>();

        for (final ThoughtObject obj : otherList) {

            if (obj.isLocalOnly()) continue;


            if (this.databaseList.containsValue(obj)) {
                databaseList.add(obj);
            } else {
                System.out.println("Local: "+ obj);
                notInDatabaseList.add(obj);
            }

        }


        for (final ThoughtObject obj : getList()) {
            if (obj.getTitle().equals("11156")) {
                System.out.println("In database: " + obj);
            }
        }

        return inDatabase ? databaseList : notInDatabaseList;

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
