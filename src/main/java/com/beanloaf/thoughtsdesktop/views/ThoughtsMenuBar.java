package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.awt.Desktop;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThoughtsMenuBar {

    private final MainApplication main;
    private final MenuBar menuBar;

    public ThoughtsMenuBar(final MainApplication main) {
        this.main = main;
        menuBar = (MenuBar) main.getNodeByID("menuBar");


        setMenuOperations();


    }

    private void setMenuOperations() {
        final Map<String, Map<String, Runnable>> menuMap = new LinkedHashMap<>();

        /* File */
        final Map<String, Runnable> fileOptions = new LinkedHashMap<>();
        fileOptions.put("New File", () -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.NEW_FILE));

        fileOptions.put("Sort File", () ->
                ThoughtsHelper.getInstance().fireEvent(TC.Properties.SORT,
                ThoughtsHelper.getInstance().getSelectedFile()));

        fileOptions.put("Delete File", () ->
                ThoughtsHelper.getInstance().fireEvent(TC.Properties.DELETE,
                ThoughtsHelper.getInstance().getSelectedFile()));

        fileOptions.put("Refresh", () -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.REFRESH));
        fileOptions.put(null, null);
        fileOptions.put("Exit", () -> {
        });
        menuMap.put("File", fileOptions);


        /* Tools */
        final Map<String, Runnable> toolOptions = new LinkedHashMap<>();
        toolOptions.put("Export", () -> {
        });
        toolOptions.put("Settings", () -> {
        });
        menuMap.put("Tools", toolOptions);


        /* Cloud*/
        final Map<String, Runnable> cloudOptions = new LinkedHashMap<>();
        cloudOptions.put("Push Files", () -> {
        });
        cloudOptions.put("Pull Files", () -> {
        });
        cloudOptions.put(null, null);
        cloudOptions.put("Cloud Settings", () -> {
        });
        menuMap.put("Cloud", cloudOptions);


        /* Help */
        final Map<String, Runnable> helpOptions = new LinkedHashMap<>();
        helpOptions.put("Credits", () -> {
        });
        helpOptions.put("test", () -> {
        });
        helpOptions.put("GitHub", () -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/beanloaf/ThoughtsDesktop"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
            menuBar.getMenus().add(menu);
        }


    }

}
