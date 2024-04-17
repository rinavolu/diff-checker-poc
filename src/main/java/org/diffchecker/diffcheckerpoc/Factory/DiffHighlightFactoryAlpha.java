package org.diffchecker.diffcheckerpoc.Factory;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.reactfx.value.Val;

import java.util.function.IntFunction;

public class DiffHighlightFactoryAlpha implements IntFunction<Node>  {

    private final ListProperty<Integer> diffLine;

    public DiffHighlightFactoryAlpha(ListProperty<Integer> diffLine) {
        this.diffLine = diffLine;
    }


    @Override
    public Node apply(int lineNumber) {
        //Beta
        Color alphaDiffColor = Color.rgb(235, 119, 96);
        VBox root = new VBox();
        root.setPrefWidth(8);
        root.setBackground(new Background(new BackgroundFill(alphaDiffColor,null,null)));
        ObservableValue<Boolean> visible = Val.map(diffLine, sl -> sl.contains(lineNumber+1));

        root.visibleProperty().bind(
                Val.flatMap(root.sceneProperty(), scene -> {
                    return scene != null ? visible : Val.constant(false);
                }));

        return root;
    }
}
