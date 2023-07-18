package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class TextView implements ThoughtsChangeListener {


    private final MainApplication main;


    private final TextField titleTextField, tagTextField;
    private final TextArea bodyTextField;
    private final Text dateText;


    private final Button sortButton, newFileButton, deleteButton;
    private final Button pullButton, pushAllButton, pushFileButton;

    private final Label cloudHeaderDisplayName, cloudHeaderNumPull, cloudHeaderNumPush;
    private final GridPane cloudHeader;


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
        cloudHeaderNumPull = (Label) main.findNodeByID("cloudHeaderNumPull");
        cloudHeaderNumPush = (Label) main.findNodeByID("cloudHeaderNumPush");


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
            ThoughtsHelper.getInstance().fireEvent(TC.Properties.VALIDATE_TITLE);
        });


        tagTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setTag(tagTextField.getText());
            obj.save();


            if (obj.isSorted()) ThoughtsHelper.getInstance().fireEvent(TC.Properties.VALIDATE_TAG, obj);

        });

        bodyTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setBody(bodyTextField.getText());
            obj.save();
        });


        sortButton.setOnMouseClicked(e ->
                ThoughtsHelper.getInstance().fireEvent(TC.Properties.SORT,
                        ThoughtsHelper.getInstance().getSelectedFile()));

        newFileButton.setOnMouseClicked(e -> {
            final boolean isTitleLocked = lockTitleCheckBox.selectedProperty().get();
            final boolean isTagLocked = lockTagCheckBox.selectedProperty().get();
            final boolean isBodyLocked = lockBodyCheckBox.selectedProperty().get();


            ThoughtsHelper.getInstance().fireEvent(TC.Properties.NEW_FILE,
                    new Object[]{
                            ThoughtsHelper.getInstance().getSelectedFile(),
                            isTitleLocked,
                            isTagLocked,
                            isBodyLocked});
        });

        deleteButton.setOnMouseClicked(e ->
                ThoughtsHelper.getInstance().fireEvent(TC.Properties.DELETE,
                        ThoughtsHelper.getInstance().getSelectedFile()));

        pushAllButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.PUSH_ALL));

        pushFileButton.setVisible(false);
        pushFileButton.setOnMouseClicked(e -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null || !obj.isSorted() || obj.isLocalOnly()) return;

            ThoughtsHelper.getInstance().fireEvent(TC.Properties.PUSH_FILE, obj);
        });


        pullButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.PULL));

        localOnlyCheckBox.selectedProperty().addListener((observableValue, oldValue, isChecked) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;

            obj.setLocalOnly(isChecked);
            obj.save();

            if (obj.isSorted() && obj.isLocalOnly())
                ThoughtsHelper.getInstance().fireEvent(TC.Properties.REMOVE_FROM_DATABASE, obj);
            ThoughtsHelper.getInstance().targetEvent(FirebaseHandler.class, TC.Properties.REFRESH);


        });
    }

    private void setTextFields(ThoughtObject obj) {
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


    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case TC.Properties.SET_TEXT_FIELDS -> {
                final ThoughtObject obj = (ThoughtObject) eventValue;


                ThoughtsHelper.getInstance().setSelectedFile(obj);
                setTextFields(obj);
                pushFileButton.setVisible(obj.isSorted());



            }
            case TC.Properties.LOG_IN_SUCCESS -> {
                final ThoughtUser user = (ThoughtUser) eventValue;
                this.cloudHeaderDisplayName.setText("Logged in as: " + user.displayName());
                this.cloudHeader.setDisable(false);


            }
            case TC.Properties.SIGN_OUT -> {
                this.cloudHeaderDisplayName.setText("Not logged in.");
                this.cloudHeader.setDisable(true);
            }

            case TC.Properties.PULL_PUSH_NUM -> {
                final Integer[] pushPull = (Integer[]) eventValue;

                cloudHeaderNumPull.setText(pushPull[0] + " files can be pulled.");
                cloudHeaderNumPush.setText(pushPull[1] + " files not pushed.");


            }
        }


    }
}
