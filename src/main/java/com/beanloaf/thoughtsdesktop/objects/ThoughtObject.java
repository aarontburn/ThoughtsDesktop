package com.beanloaf.thoughtsdesktop.objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


import com.beanloaf.thoughtsdesktop.res.TC;
import org.json.simple.JSONObject;

public class ThoughtObject implements Comparable<ThoughtObject> {


    public String dir;
    private final String file;


    private String title;
    private String tag;
    private String body;
    private final String date;


    private boolean isSorted;
    private boolean isLocalOnly;
    private boolean isInDatabase;


    private TagListItem parent; // parent is only set for sorted objects


    public ThoughtObject(final boolean isSorted, final Boolean isLocalOnly,
                         final String title,
                         final String date,
                         final String tag,
                         final String body,
                         final File file) {

        this.isLocalOnly = isLocalOnly == null ? false : isLocalOnly;
        this.isSorted = isSorted;
        this.title = title;
        this.tag = tag;
        this.date = date == null ? getDisplayDateTime() : date;
        this.body = body;
        this.file = file == null ? null : file.getName();
        this.dir = isSorted ? TC.Directories.SORTED_DIRECTORY_PATH.toString() : TC.Directories.UNSORTED_DIRECTORY_PATH.toString();
    }


    /**
     * This constructor is used when generating BRAND NEW ThoughtObjects, not registering pre-existing ones.
     *
     * @param title The initial title
     * @param tag   The initial tag
     * @param body  The initial body
     */
    public ThoughtObject(final String title,
                         final String tag,
                         final String body) {

        this.isSorted = false;
        this.title = title;
        this.tag = tag;
        this.date = getDisplayDateTime();
        this.body = body;
        this.file = createFileName();
        this.dir = isSorted ? TC.Directories.SORTED_DIRECTORY_PATH.toString() : TC.Directories.UNSORTED_DIRECTORY_PATH.toString();
    }

    public void setParent(final TagListItem parent) {
        this.parent = parent;
    }

    public TagListItem getParent() {
        return this.parent;
    }

    public Boolean save() {
        if (file == null) {
            return null;
        }

        final File file = new File(this.dir, this.file);

        try {
            new File(this.dir).mkdir();

            file.createNewFile();

            try (FileOutputStream fWriter = new FileOutputStream(file)) {
                final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();
                data.put("title", this.title);
                data.put("date", this.date);
                data.put("tag", this.tag);
                data.put("body", this.body);
                data.put("localOnly", this.isLocalOnly);
                fWriter.write(new JSONObject(data).toString().getBytes());

                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public void sort() {
        if (file == null) {
            return;
        }

        final String[] path = this.dir.split(Pattern.quote(File.separator));

        final File f = new File(this.dir, this.file);


        switch (path[path.length - 1]) {
            case "unsorted" -> { // unsorted -> sorted
                this.dir = this.dir.replace("unsorted", "sorted");
                f.renameTo(new File(this.dir, this.file));
                this.isSorted = true;
            }
            case "sorted" -> { // sorted -> unsorted
                this.dir = this.dir.replace("sorted", "unsorted");
                f.renameTo(new File(this.dir, this.file));
                this.isSorted = false;
            }
            default -> throw new RuntimeException("Attempting to sort an invalid file path..." + this.dir);
        }


    }

    public void delete() {
        if (file == null) {
            return;
        }
        new File(this.dir, this.file).delete();

    }

    public void setTitle(final String title) {
        this.title = title.isEmpty() ? TC.DEFAULT_TITLE : title;
    }

    public void setTag(final String tag) {
        this.tag = tag.isEmpty() ? TC.DEFAULT_TAG : tag;
    }

    public void setBody(final String body) {
        this.body = body.isEmpty() ? TC.DEFAULT_BODY : body;
    }


    public String getTitle() {
        if (this.file == null || !this.title.isEmpty()) {
            return this.title;
        }

        return TC.DEFAULT_TITLE;
    }

    public String getTag() {
        if (this.file == null || !this.tag.isEmpty()) {
            return this.tag;
        }

        return TC.DEFAULT_TAG;
    }

    public String getDate() {
        if (this.file == null || !this.date.isEmpty()) {
            return this.date;
        }
        return TC.DEFAULT_DATE;
    }

    public String getBody() {
        if (this.file == null || !this.body.isEmpty()) {
            return this.body;
        }


        return TC.DEFAULT_BODY;
    }

    public String getFile() {
        return this.file;
    }

    public boolean isSorted() {
        return this.isSorted;
    }

    public boolean isLocalOnly() {
        return isLocalOnly;
    }
    public void setLocalOnly(final boolean isLocal) {
        this.isLocalOnly = isLocal;
    }

    public boolean isInDatabase() {
        return this.isInDatabase;
    }

    public void setInDatabase(final boolean isInDatabase) {
        this.isInDatabase = isInDatabase;
    }




    private String getDisplayDateTime() {
        final Date d = new Date();
        final SimpleDateFormat contentDate = new SimpleDateFormat("MM/dd/yyyy");
        final SimpleDateFormat contentTime = new SimpleDateFormat("HH:mm:ss");

        return String.format("%s at %s", contentDate.format(d), contentTime.format(d));
    }

    private String createFileName() {
        final Date d = new Date();
        final SimpleDateFormat fileFormat = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss");

        return fileFormat.format(d) + ".json";
    }


    @Override
    public String toString() {
        return "[Title: " + title + " Tag: " + tag + " Body: " + body + " Date/Time: " + date + " File: " + file + "]";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ThoughtObject that = (ThoughtObject) obj;
        return Objects.equals(dir, that.dir)
                && Objects.equals(file, that.file)
                && Objects.equals(title, that.title)
                && Objects.equals(tag, that.tag)
                && Objects.equals(body, that.body)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dir, file, title, tag, body, date);
    }

    @Override
    public int compareTo(final ThoughtObject thoughtObject) {
        return this.title.compareToIgnoreCase(thoughtObject.getTitle());
    }
}