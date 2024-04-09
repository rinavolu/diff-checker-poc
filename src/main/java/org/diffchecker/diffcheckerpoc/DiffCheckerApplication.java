package org.diffchecker.diffcheckerpoc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DiffCheckerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DiffCheckerApplication.class.getResource("dc-input-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Diff Checker Poc");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}