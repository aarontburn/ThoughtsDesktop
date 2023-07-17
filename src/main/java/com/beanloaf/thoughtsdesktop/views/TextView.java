package com.beanloaf.thoughtsdesktop.views;

import com.beanloaf.thoughtsdesktop.MainApplication;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsChangeListener;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import com.beanloaf.thoughtsdesktop.objects.ThoughtObject;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class TextView implements ThoughtsChangeListener {


    private final MainApplication main;


    private final TextField titleTextField, tagTextField;
    private final TextArea bodyTextField;
    private final Text dateText;


    private final Button sortButton, newFileButton, deleteButton;
    private final Button pullButton, pushButton;

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
        pushButton = (Button) main.findNodeByID("pushButton");
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

        newFileButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.NEW_FILE));

        deleteButton.setOnMouseClicked(e ->
                ThoughtsHelper.getInstance().fireEvent(TC.Properties.DELETE,
                ThoughtsHelper.getInstance().getSelectedFile()));

        pushButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.PUSH));

        pullButton.setOnMouseClicked(e -> ThoughtsHelper.getInstance().fireEvent(TC.Properties.PULL));
    }

    private void setTextFields(ThoughtObject obj) {
        if (obj == null) {
            obj = new ThoughtObject(false,
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


    }

    @Override
    public void eventFired(final String eventName, final Object eventValue) {
        switch (eventName) {
            case TC.Properties.SET_TEXT_FIELDS -> {
                ThoughtsHelper.getInstance().setSelectedFile((ThoughtObject) eventValue);
                setTextFields((ThoughtObject) eventValue);
            }
            default -> {

            }
        }


    }
}
