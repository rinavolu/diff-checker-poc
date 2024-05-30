package org.diffchecker.diffcheckerpoc.controllers.database.newdiff;

import com.dlsc.gemsfx.DialogPane;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.dbUtility.DBConnTester;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.AreaType;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.DBConnectionStatus;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.DBDetails;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.ProceedType;

import java.net.URL;
import java.util.ResourceBundle;

public class DCDatabaseControllerV2 implements Initializable {

    public VBox root_vbox;

    private BooleanProperty alpha_connection_status, beta_connection_status , connection_status;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        alpha_connection_status = new SimpleBooleanProperty(false);
        beta_connection_status = new SimpleBooleanProperty(false);
        connection_status = new SimpleBooleanProperty(false);
        root_vbox.getChildren().addAll(getConfigureCredentialsPane(), getCenteredMFXButton(ProceedType.PROCEED_TO_META_DATA));
    }

    private TitledPane getConfigureCredentialsPane(){
        HBox hBox = new HBox();
        hBox.setSpacing(20);
        StackPane alpha_stack_pane =  getCredentialsPane(AreaType.ALPHA);
        StackPane beta_stack_pane =  getCredentialsPane(AreaType.BETA);
        hBox.getChildren().addAll(alpha_stack_pane, beta_stack_pane);

        return new TitledPane("Configure Database Credentials", hBox);
    }

    private StackPane getCredentialsPane(AreaType areaType){

        //Fields in VBOX
        Text area_txt = new Text();
        area_txt.setText(areaType.equals(AreaType.ALPHA) ? "ALPHA" : "BETA");

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

        MFXButton edit_credentials_btn = new MFXButton("Edit Credentials");
        edit_credentials_btn.setButtonType(ButtonType.RAISED);
        edit_credentials_btn.setDisable(true);

        edit_credentials_btn.setOnAction(actionEvent -> {
            database_url_field.setEditable(true);
            username_field.setEditable(true);
            password_field.setEditable(true);
            test_connection_btn.setDisable(false);
            edit_credentials_btn.setDisable(true);
        });

        if(areaType.equals(AreaType.ALPHA)) {
            alpha_connection_status.addListener(ChangeListener -> {
                if (alpha_connection_status.get()) {
                    System.out.println("Alpha Connection Status is true");
                    database_url_field.setEditable(false);
                    username_field.setEditable(false);
                    password_field.setEditable(false);
                    test_connection_btn.setDisable(true);
                    edit_credentials_btn.setDisable(false);
                }
            });
        } else{
            beta_connection_status.addListener(ChangeListener -> {
                if (beta_connection_status.get()) {
                    System.out.println("Beta Connection Status is true");
                    database_url_field.setEditable(false);
                    username_field.setEditable(false);
                    password_field.setEditable(false);
                    test_connection_btn.setDisable(true);
                    edit_credentials_btn.setDisable(false);
                }
            });
        }

        Label validationLabel = new Label("");

        //VBOX Configuration
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().addAll(area_txt, database_url_field, username_field,
                password_field, validationLabel, edit_credentials_btn, test_connection_btn);

        //Style
        if(areaType.equals(AreaType.ALPHA)){
            vbox.setStyle("-fx-background-color: #b0bfd9;");
        }else{
            vbox.setStyle("-fx-background-color: #d5d9b0;");
        }

        DialogPane dialogPane = new DialogPane();

        test_connection_btn.setOnAction(actionEvent -> {
                 testDatabaseConnection(database_url_field.getText(),
                        username_field.getText(),
                        password_field.getText(), validationLabel, dialogPane , areaType);
        } );


        return new StackPane(vbox, dialogPane);
    }

    private void testDatabaseConnection(String db_url, String db_username, String db_password,
                                        Label validationLabel, DialogPane dialogPane, AreaType areaType){
        Boolean areCredentialsValid = validateCredentials(db_url, db_username, db_password, validationLabel);
        if(!areCredentialsValid) return;
        else {
            //Test Database Connection
            DBDetails dbDetails = new DBDetails(db_url, db_username, db_password);
            DBConnectionStatus status = DBConnTester.testDatabaseConnection(dbDetails);
            if(status.getConnectionValid()){
                System.out.println(areaType.toString() + " Database connection is valid");
                dialogPane.showInformation("Connection Successful", "Connection successful");
                if(areaType.equals(AreaType.ALPHA)){
                    alpha_connection_status.set(true);
                }else{
                    beta_connection_status.set(true);
                }
            }else{
                System.out.println(areaType.toString() + "Database connection is not valid");
                dialogPane.showError("Connection Failed", status.getException());
                if(areaType.equals(AreaType.ALPHA)){
                    alpha_connection_status.set(false);
                }else{
                    beta_connection_status.set(false);
                }
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

    private HBox getCenteredMFXButton(ProceedType proceedType){
        String str = "";
        if(proceedType.equals(ProceedType.PROCEED_TO_META_DATA)){
            str ="Proceed";
        }
        MFXButton button = new MFXButton(str);
        button.setButtonType(ButtonType.RAISED);
        HBox hBox = new HBox(button);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }
}
