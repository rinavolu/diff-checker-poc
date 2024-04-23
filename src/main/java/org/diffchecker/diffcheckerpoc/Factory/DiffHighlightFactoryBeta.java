package org.diffchecker.diffcheckerpoc.Factory;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.reactfx.value.Val;

import java.util.function.IntFunction;

public class DiffHighlightFactoryBeta implements IntFunction<Node> {
    private final ListProperty<Integer> diffLine;

    public DiffHighlightFactoryBeta(ListProperty<Integer> diffLine) {
        this.diffLine = diffLine;
    }


    @Override
    public Node apply(int lineNumber) {
        //Alpha
        Color betaDiffColor = Color.rgb(128, 235, 96);
        VBox root = new VBox();
        root.setPrefWidth(8);
        root.setBackground(new Background(new BackgroundFill(betaDiffColor,null,null)));
        ObservableValue<Boolean> visible = Val.map(diffLine, sl -> sl.contains(lineNumber+1));

        root.visibleProperty().bind(
                Val.flatMap(root.sceneProperty(), scene -> {
                    return scene != null ? visible : Val.constant(false);
                }));

        return root;
    }
}
