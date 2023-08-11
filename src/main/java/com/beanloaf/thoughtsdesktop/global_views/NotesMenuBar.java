package com.beanloaf.thoughtsdesktop.global_views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class NotesMenuBar {

    private final MainApplication main;
    private final MenuBar notesMenuBar;

    public NotesMenuBar(final MainApplication main) {
        this.main = main;
        notesMenuBar = (MenuBar) main.findNodeByID("notesMenuBar");


        setMenuOperations();


    }

    private void setMenuOperations() {
        final Map<String, Map<String, Runnable>> menuMap = new LinkedHashMap<>();

        /* File */
        final Map<String, Runnable> fileOptions = new LinkedHashMap<>();
        fileOptions.put("New File", () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.NEW_FILE_BUTTON_PRESS));

        fileOptions.put("Sort File", () ->
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.SORT,
                ThoughtsHelper.getInstance().getSelectedFile()));

        fileOptions.put("Delete File", () ->
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.DELETE,
                ThoughtsHelper.getInstance().getSelectedFile()));

        fileOptions.put("Refresh", () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REFRESH));
        fileOptions.put(null, null);
        fileOptions.put("Exit", () -> {
        });
        menuMap.put("File", fileOptions);


        /* Tools */
        final Map<String, Runnable> toolOptions = new LinkedHashMap<>();
        toolOptions.put("Export", () -> {
        });
        toolOptions.put("Settings", () -> ThoughtsHelper.getInstance().targetEvent(MainApplication.class, Properties.Actions.OPEN_NOTES_SETTINGS));
        menuMap.put("Tools", toolOptions);


        /* Cloud*/
        final Map<String, Runnable> cloudOptions = new LinkedHashMap<>();
        cloudOptions.put("Push Files", () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PUSH_ALL));
        cloudOptions.put("Pull Files", () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PULL));
        cloudOptions.put(null, null);
        cloudOptions.put("Cloud Settings", () -> ThoughtsHelper.getInstance().targetEvent(MainApplication.class, Properties.Actions.OPEN_CLOUD_SETTINGS));
        menuMap.put("Cloud", cloudOptions);


        /* Help */
        final Map<String, Runnable> helpOptions = new LinkedHashMap<>();
        helpOptions.put("Credits", () -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/beanloaf/ThoughtsDesktop"));
                } catch (Exception e) {
                    Logger.logException(e);
                }
            }
        });
        helpOptions.put("test", () -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.TEST));

        menuMap.put("Help", helpOptions);

        createMenuUI(menuMap);

    }

    private void createMenuUI(final Map<String, Map<String, Runnable>> menuMap) {
        for (final String menuName : menuMap.keySet()) {
            final Menu menu = new Menu(menuName);
            for (final String menuOption : menuMap.get(menuName).keySet()) {
                if (menuOption == null) {
                    menu.getItems().add(new SeparatorMenuItem());
                    continue;
                }
                final MenuItem menuItem = new MenuItem(menuOption);

                menuItem.setOnAction(event -> menuMap.get(menuName).get(menuOption).run());
                menu.getItems().add(menuItem);
            }
            notesMenuBar.getMenus().add(menu);
        }


    }

}
