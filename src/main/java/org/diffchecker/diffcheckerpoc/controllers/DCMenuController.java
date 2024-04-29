package org.diffchecker.diffcheckerpoc.controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DCMenuController implements Initializable {
    public Button text_dc_btn;
    public Button dir_dc_btn;
    public Button api_dc_btn;
    public Button audio_dc_btn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        text_dc_btn.setOnAction(actionEvent -> textDiffCheckerButtonOnAction());
        dir_dc_btn.setOnAction(actionEvent -> directoryDiffButtonOnAction());
        api_dc_btn.setDisable(true);
        audio_dc_btn.setDisable(true);
    }

    private void textDiffCheckerButtonOnAction() {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/diffchecker/diffcheckerpoc/dc-input-scene.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Text Checker Poc");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void directoryDiffButtonOnAction(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/diffchecker/diffcheckerpoc/dc-directory-scene.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Dir Checker Poc");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
