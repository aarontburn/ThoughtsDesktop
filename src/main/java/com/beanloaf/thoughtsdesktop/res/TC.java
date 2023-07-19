package com.beanloaf.thoughtsdesktop.res;

import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.nio.file.Files;

public class TC {

    public static final String DEFAULT_TITLE = "<untitled>";
    public static final String DEFAULT_BODY = "<description>";
    public static final String DEFAULT_TAG = "<untagged>";
    public static final String DEFAULT_DATE = "<date>";


    public static class CSS {
        public static final String LIST_ITEM =
                "-fx-font-size: 22; " +
                        "-fx-border-color: rgb(95, 95, 95); " +
                        "-fx-max-width: 1000000;";

        public static final String LIST_ITEM_HOVER =
                "-fx-font-size: 22; " +
                        "-fx-border-color: rgb(41, 163, 211); " +
                        "-fx-max-width: 1000000;";


    }


    public static class Directories {

        public static final File STORAGE_PATH = new File(System.getProperty("user.dir") + "/storage/");
        public static final File UNSORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/unsorted/");
        public static final File SORTED_DIRECTORY_PATH = new File(STORAGE_PATH, "/sorted/");
        public static final File LOGIN_PATH = new File(STORAGE_PATH, "user.json");
    }



    public static class Tools {

        public static Node setAnchor(final Node node, final Double top, final Double bottom, final Double left, final Double right) {
            AnchorPane.setTopAnchor(node, top);
            AnchorPane.setBottomAnchor(node, bottom);
            AnchorPane.setLeftAnchor(node, left);
            AnchorPane.setRightAnchor(node, right);


            return node;


        }

        public static ThoughtObject readFileContents(final File filePath, final boolean isSorted) {
            try {
                final String jsonString = new String(Files.readAllBytes(filePath.toPath()));
                final JSONObject data = (JSONObject) JSONValue.parse(jsonString);

                if (data == null) {
                    return null;
                }

                final Boolean localOnly = data.get("localOnly") == null ? null : (boolean) data.get("localOnly");
                final String title = data.get("title") == null ? "" : data.get("title").toString().trim();
                final String date = data.get("date") == null ? null : data.get("date").toString().trim();
                final String tag = data.get("tag") == null ? "" : data.get("tag").toString().trim();
                final String body = data.get("body") == null ? "" : data.get("body").toString().trim();


                return new ThoughtObject(isSorted, localOnly, title, date, tag, body, filePath);

            } catch (Exception e) {
                System.out.println(filePath);

                e.printStackTrace();
            }
            return null;
        }

    }




}
