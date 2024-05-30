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
    public Button database_btn;
    public Button new_db_diff_btn;
    public Button db_diff_v2_btn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        text_dc_btn.setOnAction(actionEvent -> textDiffCheckerButtonOnAction());
        dir_dc_btn.setOnAction(actionEvent -> directoryDiffButtonOnAction());
        api_dc_btn.setOnAction(actionEvent -> apiDiffButtonOnAction());
        database_btn.setOnAction(actionEvent -> databaseDiffButtonOnAction());
        audio_dc_btn.setDisable(true);
        new_db_diff_btn.setOnAction(actionEvent -> newDatabaseDiffButtonOnAction());
        db_diff_v2_btn.setOnAction(actionEvent -> newDatabaseDiffV2ButtonOnAction());
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

    private void apiDiffButtonOnAction(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/diffchecker/diffcheckerpoc/dc-api-scene.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("API Diff Checker Poc");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void databaseDiffButtonOnAction(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/diffchecker/diffcheckerpoc/dc-table-scene.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Database Diff Checker Poc");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void newDatabaseDiffButtonOnAction(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                getResource("/org/diffchecker/diffcheckerpoc/database/styles/dc-database-scene.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Database Diff Checker Poc");
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.show();
    }

    private void newDatabaseDiffV2ButtonOnAction(){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                getResource("/org/diffchecker/diffcheckerpoc/database/styles/dc-database-scene-v2.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Database Diff Checker Poc V2");
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.show();
    }

}
