package com.beanloaf.thoughtsdesktop.database;


import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.notes.objects.ThoughtObject;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.notes.views.TextView;
import com.google.common.io.BaseEncoding;

import javafx.application.Platform;
import org.apache.commons.codec.binary.Base32;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.beanloaf.thoughtsdesktop.notes.changeListener.Properties.Actions.*;
import static com.beanloaf.thoughtsdesktop.notes.changeListener.Properties.Data.*;


@SuppressWarnings("unchecked")
public class FirebaseHandler implements ThoughtsChangeListener {


    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com/users/";


    public ThoughtUser user;

    private String apiURL;


    private final MainApplication main;

    private boolean isOnline;


    /**
     * This holds a snapshot of what's in the database at the time of refresh.
     */
    public final DatabaseSnapshot databaseSnapshot = new DatabaseSnapshot();


    private boolean eventInProgress;


    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;


    public FirebaseHandler(final MainApplication main) {
        this.main = main;
        ThoughtsHelper.getInstance().addListener(this);

    }

    public void startup() {
        checkUserFile();
    }


    private void checkUserFile() {
        Logger.log("Checking user file...");

        try {
            TC.Directories.LOGIN_FILE.createNewFile();

            final BufferedReader bufferedReader = new BufferedReader(new FileReader(TC.Directories.LOGIN_FILE));
            final StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            JSONObject data = null;
            try {
                data = (JSONObject) new JSONParser().parse(new StringReader(stringBuilder.toString()));
            } catch (ParseException ignored) {

            }

            if (data == null) return;


            final String email = (String) data.get("email");
            final String password = (String) data.get("password");


            if (signInUser(email, AuthHandler.sp(password, true))) {
                start();
            }

        } catch (Exception e) {
            Logger.logException(e);
        }


    }


    private void start() {
        if (isConnectedToDatabase()) {
            registerURL();

            setAutoRefresh();

            Platform.runLater(() -> ThoughtsHelper.getInstance().fireEvent(LOG_IN_SUCCESS, user));


        }

    }

    private void setAutoRefresh() {
        if (scheduledTask != null) {
            scheduler.shutdownNow();
            scheduledTask.cancel(true);
        }

        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }


        scheduledTask = scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        refreshItems();
                    } catch (Exception e) {
                        Logger.log(e);
                    }

                },
                0,
                ((Double) main.settingsHandler.getSetting(SettingsHandler.Settings.DATABASE_REFRESH_RATE)).longValue(),
                TimeUnit.MINUTES);
    }

    public void stopRefresh() {
        if (scheduler != null) scheduler.shutdownNow();
    }

    private void signOut() {
        user = null;
        isOnline = false;

        databaseSnapshot.clear();

        eventInProgress = false;
    }

    private boolean isConnectedToDatabase() {
        return isConnectedToInternet() && user != null;

    }

    private boolean isConnectedToInternet() {
        try {
            // Checks to see if the pc is connected to the internet
            final URLConnection connection = new URL("https://www.google.com").openConnection();
            connection.connect();
            isOnline = true;

        } catch (Exception e) {
            Logger.log("Not connected to the internet.");
            isOnline = false;
        }

        return isOnline;
    }


    private void reconnectToDatabase() {
        if (refreshItems() == null) {
            checkUserFile();
        }
    }


    // This should be run inside a thread.
    private Boolean refreshItems() {
        if (!isConnectedToDatabase() || eventInProgress) {
            return false;
        }


        Logger.log("Refreshing database...");

        databaseSnapshot.clear();
        eventInProgress = true;

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            final int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                eventInProgress = false;
                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    return null;

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
            connection.disconnect();

            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(new StringReader(responseBuilder.toString()));


            if (json != null) {
                final Base32 b32 = new Base32();

                for (final Object path : json.keySet()) {

                    final String payload = json.get(path).toString();
                    final JSONObject data = (JSONObject) JSONValue.parse(payload);

                    final String filePath = new String(b32.decode((String) path)).replace("_", " ") + ".json";
                    final String title = new String(b32.decode((String) data.get("Title")));
                    final String tag = new String(b32.decode((String) data.get("Tag")));
                    final String date = new String(b32.decode((String) data.get("Date")));
                    final String body = new String(b32.decode(((String) data.get("Body"))
                            .replace("\\n", "\n").replace("\\t", "\t")));
                    databaseSnapshot.add(new ThoughtObject(true, false, title, date, tag, body, new File(filePath)));

                }
            }


            connection.disconnect();


        } catch (Exception e) {
            Logger.logException(e);
        }
        eventInProgress = false;

        Platform.runLater(() -> ThoughtsHelper.getInstance().fireEvent(SET_IN_DATABASE_DECORATORS));

        refreshPushPullLabels();
        return true;

    }

    private void registerURL() {
        try {
            if (user == null)
                throw new IllegalArgumentException("User cannot be null when registering URL.");

            apiURL = DATABASE_URL + user.localId() + ".json?auth=" + user.idToken();

        } catch (Exception e) {
            Logger.logException(e);
        }

    }

    private boolean pull() {
        if (eventInProgress || databaseSnapshot.size() == 0) {
            return false;
        }

        ThoughtsHelper.getInstance().targetEvent(TextView.class, PULL_IN_PROGRESS, true);
        eventInProgress = true;
        new Thread(() -> {
            reconnectToDatabase();
            for (final ThoughtObject obj : databaseSnapshot.getList()) {
                final ThoughtObject listObj = main.listView.sortedThoughtList.getByFile(obj.getFile());

                if (listObj != null) { // already exists
                    listObj.setTitle(obj.getTitle());
                    listObj.setTag(obj.getTag());
                    listObj.setBody(obj.getBody());
                    listObj.save();

                } else {
                    obj.save();

                }
            }

            Platform.runLater(() -> {
                ThoughtsHelper.getInstance().targetEvent(TextView.class, PULL_IN_PROGRESS, false);
                eventInProgress = false;
                ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REFRESH);
            });

        }).start();


        return true;
    }

    private boolean push() {
        return push(main.listView.sortedThoughtList.getList().toArray(new ThoughtObject[0]));
    }


    private boolean push(final ThoughtObject[] objList) {
        if (eventInProgress) return false;

        if (!isConnectedToDatabase()) {
            Logger.log("Not connected to the internet!");
            return false;
        }


        ThoughtsHelper.getInstance().targetEvent(TextView.class, Properties.Data.PUSH_IN_PROGRESS, true);
        eventInProgress = true;


        new Thread(() -> {
            try {
                reconnectToDatabase();

                final JSONObject batchPayload = new JSONObject();
                for (final ThoughtObject obj : objList) {
                    if (obj != null && !obj.isLocalOnly()) {
                        final String[] payload = convertThoughtObjectToJson(obj);
                        batchPayload.put(payload[0], payload[1]);

                    }
                }

                final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
                connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + user.idToken());
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(batchPayload.toString());
                writer.flush();
                writer.close();
                connection.disconnect();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Logger.log("Successfully inserted all files to the database.");
                } else {
                    Logger.log("Failed to insert all files to the database. Response code: " + responseCode);
                }

                refreshItems();

                Platform.runLater(() -> ThoughtsHelper.getInstance().targetEvent(TextView.class, Properties.Data.PUSH_IN_PROGRESS, false));
                Logger.log("Finished pushing files");

            } catch (Exception e) {
                Logger.logException(e);
            }
        }).start();
        eventInProgress = false;
        return true;
    }


    private String[] convertThoughtObjectToJson(final ThoughtObject obj) {
        final String path = obj.getFile().replace(".json", "").replace(" ", "_");

        final String json = String.format("{\"Body\": \"%s\", \"Date\": \"%s\", \"Tag\": \"%s\", \"Title\": \"%s\"}",
                BaseEncoding.base32().encode(obj.getBody().getBytes()),
                BaseEncoding.base32().encode(obj.getDate().getBytes()),
                BaseEncoding.base32().encode(obj.getTag().getBytes()),
                BaseEncoding.base32().encode(obj.getTitle().replace("\n", "\\\\n")
                        .replace("\t", "\\\\t").getBytes()));

        return new String[]{BaseEncoding.base32().encode(path.getBytes()).replace("=", ""), json};
    }

    private void removeEntryFromDatabase(final ThoughtObject obj) {
        if (!isConnectedToDatabase()) {
            Logger.log("Not connected to the internet!");
            return;
        }

        if (!obj.isSorted()) return;


        obj.setInDatabase(false);

        new Thread(() -> {
            try {
                reconnectToDatabase();

                final String path = obj.getFile().replace(".json", "").replace(" ", "_");
                final URL url = new URL(DATABASE_URL + user.localId() + "/" + BaseEncoding.base32().encode(path.getBytes()).replace("=", "") + ".json?auth=" + user.idToken());
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Logger.log("Removed \"" + obj.getTitle() + "\" from database.");
                } else {
                    Logger.log("Failed to delete database entry.");
                }

                connection.disconnect();
            } catch (Exception e) {
                Logger.logException(e);
            }
            refreshItems();
        }).start();


    }

    private boolean signInUser(final String email, final String password) {
        if (!isConnectedToInternet()) {
            Logger.log("Not connected to the internet.");
            return false;
        }

        final ThoughtUser returningUser = AuthHandler.signIn(email, password);

        if (returningUser != null) {
            user = returningUser;
            saveLoginInformation(email, password);
            return true;
        }
        Logger.log("Error logging in user.");
        return false;

    }


    private boolean registerNewUser(final String displayName, final String email, final String password) {
        if (!isConnectedToInternet()) {
            Logger.log("Not connected to the internet.");
            return false;
        }

        final ThoughtUser newUser = AuthHandler.signUp(displayName, email, password);
        if (newUser != null) {
            user = newUser;

            saveLoginInformation(email, password);

            return true;
        }
        Logger.log("Error registering new user.");
        return false;
    }


    private void saveLoginInformation(final String email, final String password) {
        Logger.log("Saving login info");
        try (FileOutputStream fWriter = new FileOutputStream(TC.Directories.LOGIN_FILE)) {

            final Map<String, String> textContent = new HashMap<>();

            textContent.put("email", email);
            textContent.put("password", password != null && password.isEmpty() ? password : AuthHandler.sp(password, false));

            fWriter.write(new JSONObject(textContent).toString().getBytes());


        } catch (Exception e) {
            Logger.logException(e);
        }

    }


    public void refreshPushPullLabels() {
        if (main.listView == null) return;

        final int numToPull = databaseSnapshot.findObjectsNotOnLocal(main.listView.sortedThoughtList.getList()).size();
        final int numToPush = databaseSnapshot.findObjectsNotInDatabase(main.listView.sortedThoughtList.getList()).size();

        final Map<String, Integer> map = new HashMap<>();
        map.put("pull", numToPull);
        map.put("push", numToPush);


        Platform.runLater(() -> ThoughtsHelper.getInstance().fireEvent(Properties.Data.PULL_PUSH_NUM, map));

    }


    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case PULL -> Platform.runLater(this::pull);
            case PUSH_ALL -> Platform.runLater(this::push);
            case LOG_IN_USER -> {
                final String[] info = (String[]) eventValue;
                if (signInUser(info[0], info[1])) start();

            }
            case REGISTER_NEW_USER -> {
                final String[] info = (String[]) eventValue;

                if (registerNewUser(info[0], info[1], info[2])) start();


            }

            case SIGN_OUT -> signOut();
            case REFRESH -> new Thread(() -> refreshItems()).start();
            case REMOVE_FROM_DATABASE -> removeEntryFromDatabase((ThoughtObject) eventValue);
            case PUSH_FILE -> push(new ThoughtObject[]{(ThoughtObject) eventValue});
            case TEST -> {

            }
            case REFRESH_PUSH_PULL_LABELS -> refreshPushPullLabels();
            case DATABASE_REFRESH_RATE -> setAutoRefresh();

        }
    }


}
