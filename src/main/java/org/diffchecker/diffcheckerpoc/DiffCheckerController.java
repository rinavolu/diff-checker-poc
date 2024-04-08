package org.diffchecker.diffcheckerpoc;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DiffCheckerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}