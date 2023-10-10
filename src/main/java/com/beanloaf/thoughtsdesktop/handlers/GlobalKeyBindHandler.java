package com.beanloaf.thoughtsdesktop.handlers;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class GlobalKeyBindHandler {

    private final MainApplication main;

    private final Map<Object, Runnable> keybindMap = new HashMap<>();

    public GlobalKeyBindHandler(final MainApplication main) {
        this.main = main;

        registerKeyBinds();
    }

    private void registerKeyBinds() {
        keybindMap.put(new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN), main::swapToNextLayout);
        keybindMap.put(new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), main::swapToPreviousLayout);

        keybindMap.put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), () -> {
            if (main.getCurrentLayout() == MainApplication.Layouts.NOTES) {
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.SORT, ThoughtsHelper.getInstance().getSelectedFile());
            }
        });

        keybindMap.put(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), () -> {
            if (main.getCurrentLayout() == MainApplication.Layouts.NOTES) {
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.DELETE, ThoughtsHelper.getInstance().getSelectedFile());
            }
        });

        keybindMap.put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> {
            if (main.getCurrentLayout() == MainApplication.Layouts.NOTES) {
                ThoughtsHelper.getInstance().fireEvent(Properties.Actions.NEW_FILE_BUTTON_PRESS);
            }
        });

        keybindMap.put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> {
            if (main.getCurrentLayout() == MainApplication.Layouts.NOTES) {
                ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PULL);
            }
        });

        keybindMap.put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), () -> {
            if (main.getCurrentLayout() == MainApplication.Layouts.NOTES) {
                ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PUSH_ALL);
            }
        });

        keybindMap.put(KeyCode.F5, () -> {
            if (main.getCurrentLayout() == MainApplication.Layouts.NOTES) {
                ThoughtsHelper.getInstance().fireEvent(Properties.Actions.REFRESH);
            } else if (main.getCurrentLayout() == MainApplication.Layouts.CALENDAR) {
                main.calendarMain.getCanvasICalHandler().refresh();
            }
        });


    }

    public void fireKeyBind(final KeyEvent event) {

        if (event.isShiftDown() || event.isControlDown()) {
            for (final Object key : keybindMap.keySet()) {
                if (key.getClass() != KeyCodeCombination.class) {
                    continue;
                }
                final KeyCodeCombination comb = (KeyCodeCombination) key;
                if (comb.match(event)) {
                    keybindMap.get(key).run();
                    break;
                }
            }
        } else {
            for (final Object key : keybindMap.keySet()) {
                if (key.getClass() != KeyCode.class) {
                    continue;
                }
                final KeyCode keyCode = (KeyCode) key;
                if (keyCode.getCode() == event.getCode().getCode()) {
                    keybindMap.get(key).run();
                    break;
                }
            }
        }


    }


}
