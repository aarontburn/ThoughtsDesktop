package com.beanloaf.thoughtsdesktop.database;


import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.google.common.io.BaseEncoding;

import org.apache.commons.codec.binary.Base32;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseHandler implements ThoughtsChangeListener {


    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com/users/";


    public ThoughtUser user;

    private String apiURL;


    private final MainApplication main;

    private boolean isOnline;

    private List<ThoughtObject> cloudThoughtsList;


    public FirebaseHandler(final MainApplication main) {
        this.main = main;
        ThoughtsHelper.getInstance().addListener(this);
        checkUserFile();

    }


    private void checkUserFile() {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader("user.json"));
            final StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            final JSONObject data = (JSONObject) new JSONParser()
                    .parse(new StringReader(stringBuilder.toString()));

            final String email = (String) data.get("email");
            final String password = (String) data.get("password");


            if (signInUser(email, AuthHandler.sp(password, true))) {
                start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    public void start() {
        if (isConnectedToDatabase()) {
            registerURL();
            refreshItems();

            ThoughtsHelper.getInstance().fireEvent(TC.Properties.LOG_IN_SUCCESS, user);
        }

    }

    public void signOut() {
        user = null;
        isOnline = false;
        cloudThoughtsList = null;
    }

    public boolean isConnectedToDatabase() {
        return isConnectedToInternet() && user != null;

    }

    public boolean isConnectedToInternet() {
        try {
            // Checks to see if the pc is connected to the internet
            final URL url = new URL("https://www.google.com");
            final URLConnection connection = url.openConnection();
            connection.connect();
            isOnline = true;

        } catch (Exception e) {
            System.out.println("Not connected to the internet.");
            isOnline = false;
        }

        return isOnline;

    }


    private void refreshItems() {
        if (!isOnline) {
            return;
        }
        cloudThoughtsList = new ArrayList<>();

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            final int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                if (responseCode == 401) {
                    System.err.println("Invalid credentials at refreshItems()");
                    return;
                } else {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + connection.getResponseCode());
                }
            }

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }
            responseReader.close();


            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(new StringReader(responseBuilder.toString()));


            final Base32 b32 = new Base32();

            for (final Object path : json.keySet()) {
                final String filePath = new String(b32.decode((String) path))
                        .replace("_", " ") + ".json";
                final String title = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Title")));
                final String tag = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Tag")));
                final String date = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Date")));
                final String body = new String(b32.decode(((String) ((JSONObject) json.get(path)).get("Body"))
                        .replace("\\n", "\n").replace("\\t", "\t")));
                cloudThoughtsList.add(new ThoughtObject(true, title, date, tag, body, new File(filePath)));

            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void registerURL() {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null when registering URL.");
            }

            apiURL = DATABASE_URL + user.localId() + ".json?auth=" + user.idToken();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean pull() {
        if (cloudThoughtsList == null || cloudThoughtsList.size() == 0) {
            return false;
        }

        refreshItems();
        for (final ThoughtObject obj : cloudThoughtsList) {
            obj.save();
        }

        ThoughtsHelper.getInstance().fireEvent(TC.Properties.REFRESH);

        return true;
    }

    public boolean push() {
        if (!isConnectedToDatabase()) {
            System.out.println("Not connected to the internet!");
            return false;
        }

        new Thread(() -> {
            try {
                final File[] sortedFileDirectory = TC.Paths.SORTED_DIRECTORY_PATH.listFiles();
                for (final File file : Objects.requireNonNull(sortedFileDirectory)) {
                    final ThoughtObject tObj = this.main.listView.readFileContents(file, true);
                    if (tObj != null) {
                        addEntryIntoDatabase(tObj);
                    }
                }
                refreshItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return true;
    }

    private void addEntryIntoDatabase(final ThoughtObject obj) {
        if (!isConnectedToDatabase()) {
            System.out.println("Not connected to the internet!");
            return;
        }

        try {
            final String path = obj.getFile().replace(".json", "").replace(" ", "_");

            final String json = String.format("{\"%s\": { \"Body\": \"%s\", \"Date\": \"%s\", \"Tag\": \"%s\", \"Title\": \"%s\"}}",
                    BaseEncoding.base32().encode(path.getBytes()).replace("=", ""),
                    BaseEncoding.base32().encode(obj.getBody().getBytes()),
                    BaseEncoding.base32().encode(obj.getDate().getBytes()),
                    BaseEncoding.base32().encode(obj.getTag().getBytes()),
                    BaseEncoding.base32().encode(obj.getTitle().replace("\n", "\\\\n")
                            .replace("\t", "\\\\t").getBytes()));

            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + user.idToken());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Data successfully inserted to the database.");
            } else {
                System.out.println("Failed to insert data to the database. Response code: " + responseCode);
            }

            refreshItems();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public boolean signInUser(final String email, final String password) {
        if (!isConnectedToInternet()) {
            System.err.println("Not connected to the internet.");
            return false;
        }

        final ThoughtUser returningUser = AuthHandler.signIn(email, password);

        if (returningUser != null) {
            user = returningUser;
            saveLoginInformation(email, password);
            return true;
        }
        System.err.println("Error logging in user.");
        return false;

    }


    public boolean registerNewUser(final String displayName, final String email, final String password) {
        if (!isConnectedToInternet()) {
            System.err.println("Not connected to the internet.");
            return false;
        }

        final ThoughtUser newUser = AuthHandler.signUp(displayName, email, password);
        if (newUser != null) {
            user = newUser;

            saveLoginInformation(email, password);

            return true;
        }
        System.err.println("Error registering new user.");
        return false;
    }


    private void saveLoginInformation(final String email, final String password) {
        System.out.println("Saving login info");
        try (FileOutputStream fWriter = new FileOutputStream("user.json")) {

            final Map<String, String> textContent = new HashMap<>();

            textContent.put("email", email);
            textContent.put("password", password != null && password.isEmpty() ? password : AuthHandler.sp(password, false));

            final JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toString().getBytes());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case TC.Properties.PULL -> pull();
            case TC.Properties.PUSH -> push();
            case TC.Properties.LOG_IN_USER -> {
                final String[] info = (String[]) eventValue;
                signInUser(info[0], info[1]);

            }
            case TC.Properties.SIGN_OUT -> signOut();
        }
    }




}
