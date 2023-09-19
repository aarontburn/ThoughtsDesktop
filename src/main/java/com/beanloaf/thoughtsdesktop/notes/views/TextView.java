package com.beanloaf.thoughtsdesktop.notes.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.notes.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.database.FirebaseHandler;
import com.beanloaf.thoughtsdesktop.database.ThoughtUser;
import com.beanloaf.thoughtsdesktop.notes.objects.ThoughtObject;
import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.res.TC;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.robot.Robot;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class TextView extends ThoughtsView implements ThoughtsChangeListener {



    /*  Cloud Header    */
    private final Label cloudHeaderDisplayName;
    private final GridPane cloudHeader;
    private final ProgressIndicator pushProgressIndicator, pullProgressIndicator;
    private final Button pullButton, pushAllButton;

    /* ---------------  */


    /*  Text Fields    */
    private final TextField titleTextField, tagTextField;
    private final TextArea bodyTextField;
    private final Text dateText;
    private final AnchorPane bodyAnchorPane;
    private final Label enlargeBodyButton;
    private final GridPane enlargedBodyHeader;
    private final Label enlargeBodyTitle, enlargeBodyTag, enlargeBodyDate;
    /* ---------------  */


    /*  Button Toolbar  */
    private final CheckBox localOnlyCheckBox, lockTitleCheckBox, lockTagCheckBox, lockBodyCheckBox;
    private final Button pushFileButton, deleteLocalButton, forceSaveButton;
    private final Button sortButton, newFileButton, deleteButton;

    /* ---------------  */


    public TextView(final MainApplication main) {
        super(main);

        ThoughtsHelper.getInstance().addListener(this);


        /*  Text Fields    */
        titleTextField = (TextField) findNodeById("titleTextField");
        tagTextField = (TextField) findNodeById("tagTextField");
        dateText = (Text) findNodeById("dateText");
        bodyTextField = (TextArea) findNodeById("bodyTextField");
        bodyAnchorPane = (AnchorPane) findNodeById("bodyAnchorPane");
        enlargeBodyButton = (Label) findNodeById("enlargeBodyButton");
        enlargedBodyHeader = (GridPane) findNodeById("enlargedBodyHeader");

        enlargeBodyTitle = (Label) findNodeById("enlargeBodyTitle");
        enlargeBodyTag = (Label) findNodeById("enlargeBodyTag");
        enlargeBodyDate = (Label) findNodeById("enlargeBodyDate");

        /* ---------------  */


        /*  Button Toolbar  */
        sortButton = (Button) findNodeById("sortButton");
        newFileButton = (Button) findNodeById("newFileButton");
        deleteButton = (Button) findNodeById("deleteButton");

        deleteLocalButton = (Button) findNodeById("deleteLocalButton");
        forceSaveButton = (Button) findNodeById("forceSaveButton");
        pushFileButton = (Button) findNodeById("pushFileButton");

        localOnlyCheckBox = (CheckBox) findNodeById("localOnlyCheckBox");
        lockTitleCheckBox = (CheckBox) findNodeById("lockTitleCheckBox");
        lockTagCheckBox = (CheckBox) findNodeById("lockTagCheckBox");
        lockBodyCheckBox = (CheckBox) findNodeById("lockBodyCheckBox");
        /* ---------------  */


        /*  Cloud Header    */
        pullButton = (Button) findNodeById("pullButton");
        pushAllButton = (Button) findNodeById("pushAllButton");

        cloudHeader = (GridPane) findNodeById("cloudHeader");
        cloudHeaderDisplayName = (Label) findNodeById("cloudHeaderDisplayName");
        pushProgressIndicator = (ProgressIndicator) findNodeById("pushProgressIndicator");
        pullProgressIndicator = (ProgressIndicator) findNodeById("pullProgressIndicator");
        /* ---------------  */


        attachTextFieldEvents();
        attachButtonToolbarEvents();
        attachCloudHeaderEvents();

    }


    private void attachTextFieldEvents() {
        titleTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setTitle(titleTextField.getText());
            obj.save();
            ThoughtsHelper.getInstance().fireEvent(Properties.Actions.VALIDATE_ITEM_LIST);

            enlargeBodyTitle.setText("Title: " + obj.getTitle());
        });

        titleTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) new Robot().keyPress(KeyCode.TAB);
        });


        tagTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setTag(tagTextField.getText());
            obj.save();

            enlargeBodyTag.setText("Tag: " + obj.getTag());

            if (obj.isSorted()) ThoughtsHelper.getInstance().fireEvent(Properties.Data.VALIDATE_TAG, obj);

        });

        tagTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) new Robot().keyPress(KeyCode.TAB);
        });

        bodyTextField.focusedProperty().addListener((arg0, oldPropertyValue, isFocused) -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.setBody(bodyTextField.getText());
            obj.save();
        });

        enlargedBodyHeader.setVisible(false);
        enlargeBodyButton.setOnMouseClicked(e -> {
            enlargeBodyButton.requestFocus();
            final boolean notEnlarged = AnchorPane.getTopAnchor(bodyAnchorPane) == 240.0;
            AnchorPane.setTopAnchor(bodyAnchorPane, notEnlarged ? 16.0 : 240.0);
            enlargeBodyButton.setText(notEnlarged ? "↙" : "↗");
            enlargedBodyHeader.setVisible(notEnlarged);
            AnchorPane.setTopAnchor(bodyTextField, notEnlarged ? 32.0 : 0.0);
        });


    }

    private void attachButtonToolbarEvents() {
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


        deleteButton.setOnAction(e -> {
            final ThoughtObject objToDelete = ThoughtsHelper.getInstance().getSelectedFile();

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.DELETE, objToDelete);
            ThoughtsHelper.getInstance().fireEvent(Properties.Data.REMOVE_FROM_DATABASE, objToDelete);

        });

        deleteLocalButton.setVisible(false);
        deleteLocalButton.setOnAction(e ->
                ThoughtsHelper.getInstance().fireEvent(Properties.Data.DELETE,
                        ThoughtsHelper.getInstance().getSelectedFile()));

        pushFileButton.setVisible(false);
        pushFileButton.setOnAction(e -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null || !obj.isSorted() || obj.isLocalOnly()) return;

            ThoughtsHelper.getInstance().fireEvent(Properties.Data.PUSH_FILE, obj);
        });

        forceSaveButton.setOnAction(e -> {
            final ThoughtObject obj = ThoughtsHelper.getInstance().getSelectedFile();
            if (obj == null) return;
            obj.save();
        });

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

    private void attachCloudHeaderEvents() {
        if (main.firebaseHandler.user != null) {
            this.cloudHeaderDisplayName.setText("Logged in as: " + main.firebaseHandler.user.displayName());
            this.cloudHeader.setDisable(false);
        }

        pushAllButton.setOnAction(e -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PUSH_ALL));
        pullButton.setOnAction(e -> ThoughtsHelper.getInstance().fireEvent(Properties.Actions.PULL));

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


        final String title = obj.getTitle().equals(TC.DEFAULT_TITLE) ? "" : obj.getTitle();
        final String tag = obj.getTag().equals(TC.DEFAULT_TAG) ? "" : obj.getTag();
        final String date = !disabledFields ? "Created on: " + obj.getDate() : " ";
        final String body = obj.getBody().equals(TC.DEFAULT_BODY) ? "" : obj.getBody();

        titleTextField.setText(title);
        tagTextField.setText(tag);
        dateText.setText(date);
        bodyTextField.setText(body);

        localOnlyCheckBox.selectedProperty().set(obj.isLocalOnly());

        enlargeBodyTitle.setText("Title: " + obj.getTitle());
        enlargeBodyTag.setText("Tag: " + obj.getTag());
        enlargeBodyDate.setText(!disabledFields ? "" + obj.getDate() : " ");


        ThoughtsHelper.getInstance().isChangingTextFields = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case Properties.Data.SET_TEXT_FIELDS -> {
                ThoughtObject obj = null;

                if (eventValue.getClass() == ThoughtObject.class) {
                    obj = (ThoughtObject) eventValue;
                    pushFileButton.setVisible(obj.isSorted());
                    deleteLocalButton.setVisible(obj.isSorted());
                }

                ThoughtsHelper.getInstance().setSelectedFile(obj);
                setTextFields(obj);


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
