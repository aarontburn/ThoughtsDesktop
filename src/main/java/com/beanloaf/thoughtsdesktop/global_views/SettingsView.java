package com.beanloaf.thoughtsdesktop.global_views;


import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.notes.views.ThoughtsView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SettingsView extends ThoughtsView implements ThoughtsChangeListener {

    private static SettingsView instance;


    public static SettingsView getInstance(final MainApplication main) {
        if (instance == null) {
            instance = new SettingsView(main);
        } else {

            // TODO: Fix this part. Will not open if settings window is minimized. This does work but its ugly.
//            instance.settingsWindow.setMaximized(true);
//            instance.settingsWindow.setMaximized(false);

            instance.settingsWindow.setAlwaysOnTop(true);
            instance.settingsWindow.setAlwaysOnTop(false);
        }
        return instance;
    }

    public static boolean isInstanceActive() {
        return instance != null;
    }

    public static void closeWindow() {
        Platform.exit();
    }


    private Stage settingsWindow;
    private Scene scene;

    private TabPane settingsTabbedPane;
    /*      General Settings Layout         */
    private CheckBox lightThemeCheckBox, pullOnStartupCheckBox, pushOnExitCheckBox, matchBraceCheckBox;
    private Spinner<Integer> refreshSpinner;
    private Button revalidateButton;



    /*      ----------------                */

    /*      Cloud Layout Components         */
    // Login/Register Layout
    private AnchorPane loginRegisterLayout;
    private Button loginLayoutButton, registerLayoutButton;


    // Login Layout
    private AnchorPane loginLayout;
    private Button loginBackButton, loginButton;
    private TextField loginEmailInput, loginPasswordInput;
    private CheckBox loginShowPasswordCheckBox;


    // Register Layout
    private AnchorPane registerLayout;
    private Button registerBackButton, registerButton;
    private TextField registerNameInput, registerEmailInput, registerPasswordInput, registerReenterPasswordInput;


    // Info Layout
    private AnchorPane infoLayout;
    private Label infoDisplayNameField, infoEmailField, infoUserIDField;
    private Button infoSignOutButton;

    /*      ----------------     */
    private AnchorPane[] layoutList;

    private void setLayoutList() {
        layoutList = new AnchorPane[]{loginRegisterLayout, loginLayout, registerLayout, infoLayout};
    }


    private SettingsView(final MainApplication main) {
        super(main);

        try {

            ThoughtsHelper.getInstance().addListener(this);

            final FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/settings_view.fxml"));
            final Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            this.scene = scene;

            setScene(scene);

            this.settingsWindow = new Stage();
            this.settingsWindow.setResizable(false);
            settingsWindow.setTitle("Thoughts - Settings");
            settingsWindow.setScene(scene);
            settingsWindow.show();
            settingsWindow.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
                instance = null;
                ThoughtsHelper.getInstance().removeListener(this);
            });


            findNodes();
            setLayoutList();

            attachEvents();


            if (main.firebaseHandler.user == null) {
                swapLayouts(loginRegisterLayout);
            } else {
                setUserInfo(main.firebaseHandler.user);
                swapLayouts(infoLayout);
            }


        } catch (Exception e) {
            Logger.logException(e);
        }


    }



    public void setSelectedTab(final int index) {
        settingsTabbedPane.getSelectionModel().select(index);


    }


    private void findNodes() {
        settingsTabbedPane = (TabPane) findNodeById("settingsTabbedPane");

        // General Settings
        revalidateButton = (Button) findNodeById("revalidateButton");

        lightThemeCheckBox = (CheckBox) findNodeById("lightThemeCheckBox");
        lightThemeCheckBox.selectedProperty().set((Boolean) main.settingsHandler.getSetting(SettingsHandler.Settings.LIGHT_THEME));

        pullOnStartupCheckBox = (CheckBox) findNodeById("pullOnStartupCheckBox");
        pullOnStartupCheckBox.selectedProperty().set((Boolean) main.settingsHandler.getSetting(SettingsHandler.Settings.PULL_ON_STARTUP));

        refreshSpinner = (Spinner<Integer>) findNodeById("refreshSpinner");
        refreshSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9));


        pushOnExitCheckBox = (CheckBox) findNodeById("pushOnExitCheckBox");
        pushOnExitCheckBox.selectedProperty().set((Boolean) main.settingsHandler.getSetting(SettingsHandler.Settings.PUSH_ON_EXIT));

        matchBraceCheckBox = (CheckBox) findNodeById("matchBraceCheckBox");
        matchBraceCheckBox.selectedProperty().set((Boolean) main.settingsHandler.getSetting(SettingsHandler.Settings.MATCH_BRACE));

        // Login/Register Layout
        loginRegisterLayout = (AnchorPane) findNodeById("loginRegisterLayout");
        loginLayoutButton = (Button) findNodeById("loginLayoutButton");
        registerLayoutButton = (Button) findNodeById("registerLayoutButton");


        // Login Layout
        loginLayout = (AnchorPane) findNodeById("loginLayout");
        loginBackButton = (Button) findNodeById("loginBackButton");
        loginButton = (Button) findNodeById("loginButton");
        loginEmailInput = (TextField) findNodeById("loginEmailInput");
        loginPasswordInput = (TextField) findNodeById("loginPasswordInput");
        loginShowPasswordCheckBox = (CheckBox) findNodeById("loginShowPasswordCheckBox");


        // Register Layout
        registerLayout = (AnchorPane) findNodeById("registerLayout");
        registerBackButton = (Button) findNodeById("registerBackButton");
        registerButton = (Button) findNodeById("registerButton");
        registerNameInput = (TextField) findNodeById("registerNameInput");
        registerEmailInput = (TextField) findNodeById("registerEmailInput");
        registerPasswordInput = (TextField) findNodeById("registerPasswordInput");
        registerReenterPasswordInput = (TextField) findNodeById("registerReenterPasswordInput");

        // Info Layout
        infoLayout = (AnchorPane) findNodeById("infoLayout");
        infoDisplayNameField = (Label) findNodeById("infoDisplayNameField");
        infoEmailField = (Label) findNodeById("infoEmailField");
        infoUserIDField = (Label) findNodeById("infoUserIDField");
        infoSignOutButton = (Button) findNodeById("infoSignOutButton");

    }

    private void attachEvents() {
        // General Settings
        revalidateButton.setOnAction(e -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REVALIDATE_THOUGHT_LIST));

        lightThemeCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.LIGHT_THEME, isChecked));

        pullOnStartupCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.PULL_ON_STARTUP, isChecked));

        pushOnExitCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.PUSH_ON_EXIT, isChecked));

        matchBraceCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.MATCH_BRACE, isChecked));


        refreshSpinner.getValueFactory().setValue(((Double) main.settingsHandler.getSetting(SettingsHandler.Settings.DATABASE_REFRESH_RATE)).intValue());
        refreshSpinner.valueProperty().addListener((observableValue, integer, newValue) -> {
            main.settingsHandler.changeSetting(SettingsHandler.Settings.DATABASE_REFRESH_RATE, newValue);
        });


        // Login/Register Layout
        loginLayoutButton.setOnAction(e -> swapLayouts(loginLayout));
        registerLayoutButton.setOnAction(e -> swapLayouts(registerLayout));

        // Login Layout
        loginBackButton.setOnAction(e -> swapLayouts(loginRegisterLayout));
        loginButton.setOnAction(e -> {
            final String email = loginEmailInput.getText();
            final String password = loginPasswordInput.getText();

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.LOG_IN_USER, new String[]{email, password});

        });

        // Register Layout
        registerBackButton.setOnAction(e -> swapLayouts(loginRegisterLayout));
        registerButton.setOnAction(e -> {
            final String name = registerNameInput.getText();
            final String email = registerEmailInput.getText();
            final String password = registerPasswordInput.getText();
            final String reenterPassword = registerReenterPasswordInput.getText();


            if (!password.equals(reenterPassword)) {
                System.err.println("Passwords do not match");

                return;
            }


            // TODO: Email and password validation
            ThoughtsHelper.getInstance().fireEvent(Properties.Data.REGISTER_NEW_USER, new String[]{name, email, password});

        });

        // Info Layout
        infoSignOutButton.setOnAction(e -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.SIGN_OUT));


    }


    private void swapLayouts(final AnchorPane layout) {
        for (final AnchorPane l : layoutList) {
            l.setVisible(false);
        }

        layout.setVisible(true);
    }

    private void setUserInfo(final ThoughtUser user) {
        infoDisplayNameField.setText(user.displayName());
        infoEmailField.setText(user.email());
        infoUserIDField.setText(user.localId());
    }


    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case Properties.Data.LOG_IN_SUCCESS -> {
                if (eventValue == null)
                    throw new IllegalArgumentException("User must be passed in with LOG_IN_SUCCESS property");

                setUserInfo((ThoughtUser) eventValue);

                swapLayouts(infoLayout);


            }
            case Properties.Actions.SIGN_OUT -> swapLayouts(loginRegisterLayout);
        }


    }
}
