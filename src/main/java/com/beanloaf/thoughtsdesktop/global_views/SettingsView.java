package com.beanloaf.thoughtsdesktop.global_views;


import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.calendar.handlers.CanvasICalHandler;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.notes.views.ThoughtsView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//@SuppressWarnings("unchecked")
public class SettingsView extends ThoughtsView implements ThoughtsChangeListener {


    private TabPane settingsTabbedPane;
    /*      General Settings Layout         */
    private CheckBox pullOnStartupCheckBox, pushOnExitCheckBox, matchBraceCheckBox;
    private Spinner<Integer> refreshSpinner;
    private Button revalidateButton;

    /*      Calendar        */
    private TextField iCalURLTextField;
    private Button iCalSaveButton;
    private Label iCalConnectionMessageLabel;
    private Spinner<Integer> refreshCanvasICalSpinner;



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


    public SettingsView(final MainApplication main) {
        super(main);

        try {

            ThoughtsHelper.getInstance().addListener(this);

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

        pullOnStartupCheckBox = (CheckBox) findNodeById("pullOnStartupCheckBox");
        pullOnStartupCheckBox.selectedProperty().set((Boolean) main.settingsHandler.getSetting(SettingsHandler.Settings.PULL_ON_STARTUP));
        refreshSpinner = (Spinner<Integer>) findNodeById("refreshSpinner");
        refreshSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9));


        pushOnExitCheckBox = (CheckBox) findNodeById("pushOnExitCheckBox");
        pushOnExitCheckBox.selectedProperty().set((Boolean) main.settingsHandler.getSetting(SettingsHandler.Settings.PUSH_ON_EXIT));

        matchBraceCheckBox = (CheckBox) findNodeById("matchBraceCheckBox");
        matchBraceCheckBox.selectedProperty().set((Boolean) main.settingsHandler.getSetting(SettingsHandler.Settings.MATCH_BRACE));


        // Calendar
        iCalURLTextField = (TextField) findNodeById("iCalURLTextField");
        iCalSaveButton = (Button) findNodeById("iCalSaveButton");
        iCalConnectionMessageLabel = (Label) findNodeById("iCalConnectionMessageLabel");
        refreshCanvasICalSpinner = (Spinner<Integer>) findNodeById("refreshCanvasICalSpinner");
        refreshCanvasICalSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9));


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


        pullOnStartupCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.PULL_ON_STARTUP, isChecked));

        pushOnExitCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.PUSH_ON_EXIT, isChecked));

        matchBraceCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.MATCH_BRACE, isChecked));


        refreshSpinner.getValueFactory().setValue(((Double) main.settingsHandler.getSetting(SettingsHandler.Settings.DATABASE_REFRESH_RATE)).intValue());
        refreshSpinner.valueProperty().addListener((observableValue, integer, newValue) ->
                main.settingsHandler.changeSetting(SettingsHandler.Settings.DATABASE_REFRESH_RATE, newValue));

        // Calendar
        final String storedICalUrl = (String) main.settingsHandler.getSetting(SettingsHandler.Settings.CANVAS_ICAL_URL);

        iCalURLTextField.setText(storedICalUrl);
        new Thread(() -> {
            if (CanvasICalHandler.checkICalUrl(storedICalUrl)) {
                Platform.runLater(() -> iCalConnectionMessageLabel.setText("Connected!"));

            } else {
                Platform.runLater(() -> iCalConnectionMessageLabel.setText("Unable to connect to iCal. Please check the URL."));
            }
        }).start();


        iCalSaveButton.setOnAction(e -> new Thread(() -> {
            main.settingsHandler.changeSetting(SettingsHandler.Settings.CANVAS_ICAL_URL, iCalURLTextField.getText());
            if (CanvasICalHandler.checkICalUrl(iCalURLTextField.getText())) {
                Platform.runLater(() -> iCalConnectionMessageLabel.setText("Connected!"));

                main.calendarMain.getCanvasICalHandler().stopRefresh();
                main.calendarMain.getCanvasICalHandler().setAutoRefresh();

            } else {
                Platform.runLater(() -> iCalConnectionMessageLabel.setText("Unable to connect to iCal. Please check the URL."));
            }
        }).start());


        refreshCanvasICalSpinner.getValueFactory().setValue(((Double) main.settingsHandler.getSetting(SettingsHandler.Settings.CANVAS_ICAL_REFRESH_RATE)).intValue());
        refreshCanvasICalSpinner.valueProperty().addListener((observableValue, integer, newValue) -> {
            main.settingsHandler.changeSetting(SettingsHandler.Settings.CANVAS_ICAL_REFRESH_RATE, newValue);
            main.calendarMain.getCanvasICalHandler().setAutoRefresh();
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
        Platform.runLater(() -> {
            infoDisplayNameField.setText(user.displayName());
            infoEmailField.setText(user.email());
            infoUserIDField.setText(user.localId());
        });

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
