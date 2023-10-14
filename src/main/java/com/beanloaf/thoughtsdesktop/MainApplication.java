package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.calendar.views.CalendarMain;
import com.beanloaf.thoughtsdesktop.handlers.GlobalKeyBindHandler;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.global_views.GlobalHeaderController;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.handlers.SettingsHandler;
import com.beanloaf.thoughtsdesktop.global_views.HomeView;
import com.beanloaf.thoughtsdesktop.notes.views.ListView;
import com.beanloaf.thoughtsdesktop.global_views.NotesMenuBar;
import com.beanloaf.thoughtsdesktop.global_views.SettingsView;
import com.beanloaf.thoughtsdesktop.notes.views.TextView;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefRequestContextHandler;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.network.CefCookieManager;


import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.util.TimerTask;

import static com.beanloaf.thoughtsdesktop.notes.changeListener.Properties.Actions.*;

public class MainApplication extends Application implements ThoughtsChangeListener {


    private final GlobalKeyBindHandler keyBindHandler = new GlobalKeyBindHandler(this);
    public FirebaseHandler firebaseHandler;
    public SettingsHandler settingsHandler;
    public GlobalHeaderController headerController;
    public HomeView homeView;
    public CalendarMain calendarMain;
    public ListView listView;
    public TextView textView;
    public SettingsView settingsView;
    public Layouts currentLayout;
    private Scene scene;
    private NotesMenuBar notesMenuBar;
    /*  Layouts  */
    private Node[] layoutList;
    private Node homeRoot, notepadFXML, calendarFXML, settingsFXML;

    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        TC.Directories.STORAGE_PATH.mkdirs();


        settingsHandler = SettingsHandler.getInstance();
        ThoughtsHelper.getInstance().addListener(this);


        final Scene scene = new Scene(
                new FXMLLoader(MainApplication.class.getResource("fxml/home_screen.fxml")).load(),
                (Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_WIDTH),
                (Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_HEIGHT));


        this.scene = scene;
        this.scene.addEventFilter(KeyEvent.KEY_PRESSED, keyBindHandler::fireKeyBind);

        stage.setTitle("Thoughts");
        stage.setScene(scene);

        stage.setX((Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_X));
        stage.setY((Double) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_Y));
        stage.setMaximized((Boolean) settingsHandler.getSetting(SettingsHandler.Settings.WINDOW_MAXIMIZED));

        stage.show();


        scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            homeView.clock.stop();
            firebaseHandler.stopRefresh();
            calendarMain.getCanvasICalHandler().stopRefresh();

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_X, stage.getX());
            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_Y, stage.getY());

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_WIDTH, stage.getWidth());
            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_HEIGHT, stage.getHeight());

            settingsHandler.changeSetting(SettingsHandler.Settings.WINDOW_MAXIMIZED, stage.isMaximized());

        });




        /*  Layouts */
        homeRoot = findNodeById("homeRoot");
        notepadFXML = findNodeById("notepadFXML");
        calendarFXML = findNodeById("calendarFXML");
        settingsFXML = findNodeById("settingsFXML");


        layoutList = new Node[]{homeRoot, notepadFXML, calendarFXML, settingsFXML};
        /*  ------  */


        headerController = (GlobalHeaderController) ThoughtsHelper.getInstance().getController(GlobalHeaderController.class);
        firebaseHandler = new FirebaseHandler(this);


        new Thread(() -> firebaseHandler.startup()).start();
        swapLayouts(Layouts.HOME);

        homeView = new HomeView(this);
        settingsView = new SettingsView(this);


        final MainApplication main = this;
        new Thread(() -> {
            // TODO: these all need to go into a Notepad class
            notesMenuBar = new NotesMenuBar(main);
            textView = new TextView(main);
            listView = new ListView(main);

            calendarMain = new CalendarMain(main);
        }).start();


        final String URL = "https://google.com/";
        final boolean OFFSCREEN = false;
        final boolean TRANSPARENT = false;

        try {
            SwingUtilities.invokeLater(() -> {
                if (!CefApp.startup(new String[]{})) {
                    System.out.println("Startup initialization failed!");
                    return;
                }

                final SwingNode swingNode = new SwingNode();

                CefSettings settings = new CefSettings();
                settings.windowless_rendering_enabled = OFFSCREEN;
                CefApp cefApp = CefApp.getInstance(settings);
                CefClient client = cefApp.createClient();
                client.addMessageRouter(CefMessageRouter.create());
                CefBrowser browser = client.createBrowser(URL, OFFSCREEN, TRANSPARENT);

                JTextField address = new JTextField(URL);
                address.addActionListener(e -> {
                    browser.loadURL(address.getText());
                    swingNode.getContent().repaint();
                });

                final JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(browser.getUIComponent(), BorderLayout.CENTER);
                panel.add(address, BorderLayout.NORTH);
                panel.setVisible(true);

                swingNode.setContent(panel);





                Platform.runLater(() -> {
                    ((AnchorPane) homeRoot).getChildren().add(ThoughtsHelper.setAnchor(swingNode, 50, 50, 50, 50));
                });
                browser.loadURL("https://google.com/");

            });
        } catch (Exception e) {
            Logger.log(e);
        }


    }


    public void swapToNextLayout() {
        swapLayouts(Layouts.getNextLayout(currentLayout));
    }

    public void swapToPreviousLayout() {
        swapLayouts(Layouts.getPreviousLayout(currentLayout));
    }

    public void swapLayouts(final Layouts layout) {
        this.currentLayout = layout;
        switch (layout) {
            case NOTES -> {
                headerController.setSelectedTab(headerController.headerNotesButton);
                toggleLayoutVisibility(notepadFXML);
            }
            case HOME -> {
                headerController.setSelectedTab(headerController.headerHomeButton);
                toggleLayoutVisibility(homeRoot);
            }

            case CALENDAR -> {
                headerController.setSelectedTab(headerController.headerCalendarButton);
                toggleLayoutVisibility(calendarFXML);
                if (calendarMain != null) {
                    calendarMain.onOpen();
                }

            }
            case SETTINGS -> {
                headerController.setSelectedTab(headerController.headerSettingsButton);
                toggleLayoutVisibility(settingsFXML);

            }

            default -> throw new IllegalArgumentException("Not sure how you got here. Illegal enum passed: " + layout);
        }

    }

    private void toggleLayoutVisibility(final Node visibleLayout) {
        for (final Node node : layoutList) {
            node.setVisible(false);
        }
        visibleLayout.setVisible(true);
    }

    public Layouts getCurrentLayout() {
        return this.currentLayout;
    }


    public Node findNodeById(final String id) {
        if (id.charAt(0) == '#') throw new IllegalArgumentException("ID's cannot start with #");

        return this.scene.lookup("#" + id);
    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {

        switch (eventName) {
            case OPEN_HOME_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(0);
            }
            case OPEN_NOTES_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(1);

            }
            case OPEN_CALENDAR_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(2);
            }
            case OPEN_CLOUD_SETTINGS -> {
                swapLayouts(Layouts.SETTINGS);
                settingsView.setSelectedTab(3);

            }
        }
    }


    public enum Layouts {
        HOME(0),
        NOTES(1),
        CALENDAR(2),
        SETTINGS(3);

        private final int layoutNum;

        Layouts(final int layoutNum) {
            this.layoutNum = layoutNum;
        }

        public static Layouts getNextLayout(final Layouts layout) {
            final Layouts[] layouts = values();
            return layout.layoutNum == layouts.length - 1 ? layouts[0] : layouts[layout.layoutNum + 1];
        }

        public static Layouts getPreviousLayout(final Layouts layout) {
            final Layouts[] layouts = values();
            return layout.layoutNum <= 0 ? layouts[layouts.length - 1] : layouts[layout.layoutNum - 1];
        }
    }
}