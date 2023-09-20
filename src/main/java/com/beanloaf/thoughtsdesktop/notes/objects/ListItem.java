package com.beanloaf.thoughtsdesktop.notes.objects;

import com.beanloaf.thoughtsdesktop.notes.changeListener.Properties;
import com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.beanloaf.thoughtsdesktop.handlers.ThoughtsHelper.setAnchor;

public class ListItem extends AnchorPane {

    public enum Decorators {
        LOCAL_ONLY,
        IN_DATABASE

    }


    private final ThoughtObject obj;

    private final Button button;



    // Decorators:
    public final DecoratorText localOnlyDecorator, inDatabaseDecorator;
    private final List<DecoratorText> decorators = new ArrayList<>();



    public ListItem(final ThoughtObject obj) {
        super();
        this.getStyleClass().add("itemList");
        this.obj = obj;

        button = new Button(obj.getTitle());
        button.setOnAction(e -> {
            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SET_TEXT_FIELDS, obj);
            ThoughtsHelper.getInstance().fireEvent(Properties.Data.SELECTED_LIST_ITEM, this);
        });

        this.getChildren().add(setAnchor(button, 0.0, 0.0, 0.0, 0.0));


        localOnlyDecorator = new DecoratorText("L");
        this.getChildren().add(localOnlyDecorator);

        inDatabaseDecorator = new DecoratorText("U"); // TODO: find a better symbol for this
        this.getChildren().add(inDatabaseDecorator);



        localOnlyDecorator.setVisible(obj.isLocalOnly());
        inDatabaseDecorator.setVisible(obj.isInDatabase());

    }

    public void doClick() {
        button.fire();
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


    public void setDecorator(final Decorators decorator, final boolean visible) {
        for (final DecoratorText d : decorators) {
            d.setVisible(false);
        }


        switch (decorator) {
            case LOCAL_ONLY -> localOnlyDecorator.setVisible(visible);
            case IN_DATABASE -> inDatabaseDecorator.setVisible(visible);
            default -> throw new IllegalArgumentException("Illegal enum inputted.");
        }

    }







    public class DecoratorText extends Text {

        public DecoratorText(final String text) {
            super(text);
            this.setStyle("-fx-fill: #B2B2B2; -fx-font-size: 16;");
            setAnchor(this, 0.0, null, 4.0, null);

            decorators.add(this);
        }

    }


}
