package com.beanloaf.thoughtsdesktop.objects;

import com.beanloaf.thoughtsdesktop.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.res.TC;
import com.beanloaf.thoughtsdesktop.changeListener.ThoughtsHelper;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ListItem extends AnchorPane {

    private final ThoughtObject obj;

    private final Button button;



    // Decorators:
    final Text localOnlyDecorator;


    public ListItem(final ThoughtObject obj) {
        super();
        this.getStyleClass().add("listViewItem");
        this.obj = obj;

        button = new Button(obj.getTitle());
        button.setOnAction(e -> {
            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS, obj);
            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SELECTED_LIST_ITEM, this);
        });

        this.getChildren().add(TC.Tools.setAnchor(button, 0.0, 0.0, 0.0, 0.0));


        localOnlyDecorator = new Text("L");
        localOnlyDecorator.setStyle("-fx-fill: #B2B2B2; -fx-font-size: 20;");
        this.getChildren().add(TC.Tools.setAnchor(localOnlyDecorator, 0.0, null, 8.0, null));


        setLocal(obj.isLocalOnly());


    }

    public ThoughtObject getThoughtObject() {
        return this.obj;
    }


    public String getText() {
        return button.getText();
    }

    public void setText(final String text) {
        button.setText(text);
    }

    public void setLocal(final boolean isLocal) {
        localOnlyDecorator.setVisible(isLocal);


    }


}
