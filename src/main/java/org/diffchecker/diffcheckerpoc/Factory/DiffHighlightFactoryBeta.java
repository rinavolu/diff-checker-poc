package org.diffchecker.diffcheckerpoc.Factory;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.reactfx.value.Val;

import java.util.function.IntFunction;

public class DiffHighlightFactoryBeta implements IntFunction<Node> {
    private final ListProperty<Integer> diffLine;

    public DiffHighlightFactoryBeta(ListProperty<Integer> diffLine) {
        this.diffLine = diffLine;
    }


    @Override
    public Node apply(int lineNumber) {
        StackPane root = new StackPane();
        Rectangle rectangle = new Rectangle(8,18);
        Color betaDiffColor = Color.rgb(128, 235, 96);
        rectangle.setFill(betaDiffColor);
        root.getChildren().addAll(rectangle);
        ObservableValue<Boolean> visible = Val.map(diffLine, sl -> sl.contains(lineNumber+1));

        root.visibleProperty().bind(
                Val.flatMap(root.sceneProperty(), scene -> {
                    return scene != null ? visible : Val.constant(false);
                }));

        return root;
    }
}
