package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class TextView implements ThoughtsChangeListener {


    private final MainApplication main;


    private final TextField titleTextField, tagTextField;
    private final TextArea bodyTextField;
    private final Text dateText;


    private final Button sortButton, newFileButton, deleteButton;
    private final Button pullButton, pushAllButton, pushFileButton;



    private final Label cloudHeaderDisplayName;
    private final GridPane cloudHeader;
    private final ProgressIndicator pushProgressIndicator, pullProgressIndicator;


    private final CheckBox localOnlyCheckBox, lockTitleCheckBox, lockTagCheckBox, lockBodyCheckBox;

    public TextView(final MainApplication main) {
        this.main = main;

        ThoughtsHelper.getInstance().addListener(this);

        titleTextField = (TextField) main.findNodeByID("titleTextField");
        tagTextField = (TextField) main.findNodeByID("tagTextField");
        dateText = (Text) main.findNodeByID("dateText");
        bodyTextField = (TextArea) main.findNodeByID("bodyTextField");


        sortButton = (Button) main.findNodeByID("sortButton");
        newFileButton = (Button) main.findNodeByID("newFileButton");
        deleteButton = (Button) main.findNodeByID("deleteButton");


        pullButton = (Button) main.findNodeByID("pullButton");
        pushAllButton = (Button) main.findNodeByID("pushAllButton");
        pushFileButton = (Button) main.findNodeByID("pushFileButton");

        cloudHeader = (GridPane) main.findNodeByID("cloudHeader");
        cloudHeaderDisplayName = (Label) main.findNodeByID("cloudHeaderDisplayName");
        pushProgressIndicator = (ProgressIndicator) main.findNodeByID("pushProgressIndicator");
        pullProgressIndicator = (ProgressIndicator) main.findNodeByID("pullProgressIndicator");

        localOnlyCheckBox = (CheckBox) main.findNodeByID("localOnlyCheckBox");
        lockTitleCheckBox = (CheckBox) main.findNodeByID("lockTitleCheckBox");
        lockTagCheckBox = (CheckBox) main.findNodeByID("lockTagCheckBox");
        lockBodyCheckBox = (CheckBox) main.findNodeByID("lockBodyCheckBox");


        attachEvents();

    }

    private void attachEvents() {
        titleTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setTitle(titleTextField.getText());
            obj.save();
            ThoughtsHelper.getInstance().fireEvent(Properties.Actions.VALIDATE_TITLE);
        });


        tagTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setTag(tagTextField.getText());
            obj.save();


            if (obj.isSorted()) ThoughtsHelper.getInstance().fireEvent(Properties.Data.VALIDATE_TAG, obj);

        });

        bodyTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setBody(bodyTextField.getText());
            obj.save();
        });


        sortButton.setOnAction(e ->
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.SORT,
                        ThoughtsHelper.getInstance().getSelectedFile()));

        newFileButton.setOnAction(e -> {
            final boolean isTitleLocked = lockTitleCheckBox.selectedProperty().get();
            final boolean isTagLocked = lockTagCheckBox.selectedProperty().get();
            final boolean isBodyLocked = lockBodyCheckBox.selectedProperty().get();


            ThoughtsHelper.getInstance().fireEvent(Properties.Data.NEW_FILE,
                    new Object[]{
                            ThoughtsHelper.getInstance().getSelectedFile(),
                            isTitleLocked,
                            isTagLocked,
                            isBodyLocked});
        });



        deleteButton.setOnAction(e ->
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.DELETE,
                        ThoughtsHelper.getInstance().getSelectedFile()));

        pushAllButton.setOnAction(e -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PUSH_ALL));

        pushFileButton.setVisible(false);
        pushFileButton.setOnAction(e -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null || !obj.isSorted() || obj.isLocalOnly()) return;

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.PUSH_FILE, obj);
        });


        pullButton.setOnAction(e -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PULL));

        localOnlyCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) -> {
            if (ThoughtsHelper.getInstance().isChangingTextFields) {
                return;
            }


            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;

            obj.setLocalOnly(isChecked);
            obj.save();

            if (obj.isSorted() && obj.isLocalOnly())
                ThoughtsHelper.getInstance().targetEvent(FirebaseHandler.class, Properties.Data.REMOVE_FROM_DATABASE, obj);
            else {
                ThoughtsHelper.getInstance().targetEvent(FirebaseHandler.class, Properties.Actions.REFRESH);

            }

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.CHECKBOX_PRESSED, isChecked);

        });
    }

    private void setTextFields(ThoughtObject obj) {
        ThoughtsHelper.getInstance().isChangingTextFields = true;
        if (obj == null) {
            obj = new ThoughtObject(false, false,
                    "Thoughts",
                    "",
                    "by @beanloaf",
                    "Get started by creating or selecting a file.",
                    null);

        }

        final boolean disabledFields = obj.getFile() == null;

        titleTextField.setDisable(disabledFields);
        tagTextField.setDisable(disabledFields);
        bodyTextField.setDisable(disabledFields);

        titleTextField.setText(obj.getTitle().equals(TC.DEFAULT_TITLE) ? "" : obj.getTitle());
        tagTextField.setText(obj.getTag().equals(TC.DEFAULT_TAG) ? "" : obj.getTag());
        dateText.setText(!disabledFields ? "Created on: " + obj.getDate() : " ");
        bodyTextField.setText(obj.getBody().equals(TC.DEFAULT_BODY) ? "" : obj.getBody());

        localOnlyCheckBox.selectedProperty().set(obj.isLocalOnly());

        ThoughtsHelper.getInstance().isChangingTextFields = false;
    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case Properties.Data.SET_TEXT_FIELDS -> {
                final ThoughtObject obj = (ThoughtObject) eventValue;
                if (obj == null) return;

                ThoughtsHelper.getInstance().setSelectedFile(obj);
                setTextFields(obj);
                pushFileButton.setVisible(obj.isSorted());



            }
            case Properties.Data.LOG_IN_SUCCESS -> {
                final ThoughtUser user = (ThoughtUser) eventValue;
                if (user == null) return;

                this.cloudHeaderDisplayName.setText("Logged in as: " + user.displayName());
                this.cloudHeader.setDisable(false);


            }
            case Properties.Actions.SIGN_OUT -> {
                this.cloudHeaderDisplayName.setText("Not logged in.");
                this.cloudHeader.setDisable(true);
            }

            case Properties.Data.PULL_PUSH_NUM -> {
                final Map<String, Integer> map = (HashMap<String, Integer>) eventValue;

                pushAllButton.setText("Push All (" + map.get("push") + ")");
                pullButton.setText("Pull (" + map.get("pull") + ")");


            }
            case Properties.Actions.NEW_FILE_BUTTON_PRESS -> newFileButton.fire();
            case Properties.Data.PUSH_IN_PROGRESS -> pushProgressIndicator.setVisible((boolean) eventValue);
            case Properties.Data.PULL_IN_PROGRESS -> pullProgressIndicator.setVisible((boolean) eventValue);
        }


    }
}
