package org.diffchecker.diffcheckerpoc.controllers.database.newdiff;

import com.dlsc.gemsfx.DialogPane;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.dbUtility.DBConnTester;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.DBConnectionStatus;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.DBDetails;

import java.net.URL;
import java.util.ResourceBundle;

public class DCDatabaseController implements Initializable {

    public VBox alpha_vbox;
    public VBox middle_vbox;
    public VBox beta_vbox;

    public HBox root_hbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setHVBoxGrowOptions();
        displayDBCredentialsPane();
    }

    private void setHVBoxGrowOptions(){
        HBox.setHgrow(alpha_vbox, Priority.ALWAYS);
        HBox.setHgrow(beta_vbox, Priority.ALWAYS);
        //HBox.setHgrow(middle_vbox, Priority.ALWAYS);
    }

    private void displayDBCredentialsPane() {
        alpha_vbox.getChildren().add(getDBCredentialsPane());
        beta_vbox.getChildren().add(getDBCredentialsPane());
    }

    private TitledPane getDBCredentialsPane(){
        TitledPane titledPane = new TitledPane();
        titledPane.setText("Configure Database Connection");
        //titledPane.setPadding(new Insets(10, 10, 10, 10));

        VBox credentials_vbox = new VBox();
        //credentials_vbox.setPadding(new Insets(10, 10, 10, 10));
        credentials_vbox.setSpacing(10);

        Label validationLabel = new Label("");

        MFXTextField database_url_field = new MFXTextField();
        database_url_field.setPrefWidth(1000.0);
        database_url_field.setFloatingText("Database Url");


        MFXTextField username_field = new MFXTextField();
        username_field.setPrefWidth(1000.0);
        username_field.setFloatingText("Username");

        MFXPasswordField password_field = new MFXPasswordField();
        password_field.setPrefWidth(1000.0);
        password_field.setFloatingText("Password");

        MFXButton test_connection_btn = new MFXButton("Test Connection");
        test_connection_btn.setButtonType(ButtonType.RAISED);

        DialogPane dialogPane = new DialogPane();

        test_connection_btn.setOnAction(actionEvent ->
                testDatabaseConnection(database_url_field.getText(),
                        username_field.getText(),
                        password_field.getText(), validationLabel, dialogPane));

        credentials_vbox.getChildren().addAll(database_url_field,
                username_field, password_field,
                validationLabel, test_connection_btn);

        StackPane stackPane = new StackPane(credentials_vbox, dialogPane);
        titledPane.setContent(stackPane);
        return titledPane;
    }

    private void testDatabaseConnection(String db_url, String db_username, String db_password,
                                        Label validationLabel, DialogPane dialogPane){
        Boolean areCredentialsValid = validateCredentials(db_url, db_username, db_password, validationLabel);
        if(!areCredentialsValid) return;
        else {
            //Test Database Connection
            DBDetails dbDetails = new DBDetails(db_url, db_username, db_password);
            DBConnectionStatus status = DBConnTester.testDatabaseConnection(dbDetails);
            if(status.getConnectionValid()){
                System.out.println("Database connection valid");
                dialogPane.showInformation("Connection Successful", "Connection successful");
            }else{
                System.out.println("Database connection not valid");
                dialogPane.showError("Connection Failed", status.getException());
            }
        }
    }

    private Boolean validateCredentials(String db_url, String db_username, String db_password, Label validationLabel){
        Boolean areCredentialsValid = true;
        String error_msg = "";
        error_msg += db_url.isEmpty() ? "Database URL ,":"";
        error_msg += db_username.isEmpty() ? "Username ,":"";
        error_msg += db_password.isEmpty() ? "Password ,":"";

        error_msg = error_msg.isBlank() ? "" : error_msg.substring(0, error_msg.length()-1) + "Cannot be empty";

        if(!error_msg.isEmpty()){
            validationLabel.setText(error_msg);
            areCredentialsValid = false;
        }else {
            validationLabel.setText("");
        }

        return areCredentialsValid;

    }



}
