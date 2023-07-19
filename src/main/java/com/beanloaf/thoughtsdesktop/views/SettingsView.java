package com.beanloaf.thoughtsdesktop.views;


import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SettingsView implements ThoughtsChangeListener {

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


    private Stage settingsWindow;
    private Scene scene;
    private MainApplication main;



    private TabPane settingsTabbedPane;
    /*      General Settings Layout         */
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

        try {
            this.main = main;

            ThoughtsHelper.getInstance().addListener(this);

            final FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/settings_view.fxml"));
            final Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            this.scene = scene;

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
            e.printStackTrace();
        }


    }

    public void setSelectedTab(final int index) {
        settingsTabbedPane.getSelectionModel().select(index);



    }



    private Node findNodeByID(final String id) {
        if (id.charAt(0) == '#') throw new IllegalArgumentException("ID's cannot start with #");

        return scene.lookup("#" + id);


    }

    private void findNodes() {
        settingsTabbedPane = (TabPane) findNodeByID("settingsTabbedPane");

        // General Settings
        revalidateButton = (Button) findNodeByID("revalidateButton");


        // Login/Register Layout
        loginRegisterLayout = (AnchorPane) findNodeByID("loginRegisterLayout");
        loginLayoutButton = (Button) findNodeByID("loginLayoutButton");
        registerLayoutButton = (Button) findNodeByID("registerLayoutButton");


        // Login Layout
        loginLayout = (AnchorPane) findNodeByID("loginLayout");
        loginBackButton = (Button) findNodeByID("loginBackButton");
        loginButton = (Button) findNodeByID("loginButton");
        loginEmailInput = (TextField) findNodeByID("loginEmailInput");
        loginPasswordInput = (TextField) findNodeByID("loginPasswordInput");
        loginShowPasswordCheckBox = (CheckBox) findNodeByID("loginShowPasswordCheckBox");


        // Register Layout
        registerLayout = (AnchorPane) findNodeByID("registerLayout");
        registerBackButton = (Button) findNodeByID("registerBackButton");
        registerButton = (Button) findNodeByID("registerButton");
        registerNameInput = (TextField) findNodeByID("registerNameInput");
        registerEmailInput = (TextField) findNodeByID("registerEmailInput");
        registerPasswordInput = (TextField) findNodeByID("registerPasswordInput");
        registerReenterPasswordInput = (TextField) findNodeByID("registerReenterPasswordInput");

        // Info Layout
        infoLayout = (AnchorPane) findNodeByID("infoLayout");
        infoDisplayNameField = (Label) findNodeByID("infoDisplayNameField");
        infoEmailField = (Label) findNodeByID("infoEmailField");
        infoUserIDField = (Label) findNodeByID("infoUserIDField");
        infoSignOutButton = (Button) findNodeByID("infoSignOutButton");

    }

    private void attachEvents() {
        // General Settings
        revalidateButton.setOnAction(e -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REVALIDATE_THOUGHT_LIST));



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
                if (eventValue == null) throw new IllegalArgumentException("User must be passed in with LOG_IN_SUCCESS property");

                setUserInfo((ThoughtUser) eventValue);

                swapLayouts(infoLayout);


            }
            case Properties.Actions.SIGN_OUT -> swapLayouts(loginRegisterLayout);
        }


    }
}
