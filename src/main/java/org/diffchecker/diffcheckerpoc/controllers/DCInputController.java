package org.diffchecker.diffcheckerpoc.controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.reactfx.value.Var;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;

public class DCInputController implements Initializable {


    public StyleClassedTextArea text_area_alpha;
    public StyleClassedTextArea text_area_beta;
    public Button diff_check_button;
    public VirtualizedScrollPane scroll_pane_alpha;
    public VirtualizedScrollPane scroll_pane_beta;
    public Label total_characters_alpha;
    public Label total_lines_alpha;
    public Label total_characters_beta;
    public Label total_lines_beta;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTextArea();
    }

    private void initializeTextArea(){
        text_area_alpha.clear();
        text_area_beta.clear();
        text_area_alpha.setWrapText(true);
        text_area_beta.setWrapText(true);

        text_area_alpha.setParagraphGraphicFactory(LineNumberFactory.get(text_area_alpha));
        text_area_beta.setParagraphGraphicFactory(LineNumberFactory.get(text_area_beta));

        text_area_alpha.beingUpdatedProperty().addListener(observable -> {
            total_lines_alpha.setText(String.valueOf(text_area_alpha.getParagraphs().size()));
            total_characters_alpha.setText(String.valueOf(text_area_alpha.getText().length()));
        });

        text_area_beta.beingUpdatedProperty().addListener(observable -> {
            total_lines_beta.setText(String.valueOf(text_area_beta.getParagraphs().size()));
            total_characters_beta.setText(String.valueOf(text_area_beta.getText().length()));
        });



        diff_check_button.setOnAction(actionEvent -> {
            try {
                onDiffCheckButton();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //addDualScrolls();
    }


    private void onDiffCheckButton() throws IOException {
        //System.out.println("Before FXML Load: "+ Instant.now());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/diffchecker/diffcheckerpoc/dc-output-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        //System.out.println("After Stage Creation: "+ Instant.now());
        DCOutputController controller= fxmlLoader.getController();
        controller.copyTextDetails(this.text_area_alpha.getText(),
                this.text_area_beta.getText(),
                this.total_characters_alpha.getText(),
                this.total_lines_alpha.getText(),
                this.total_characters_beta.getText(),
                this.total_lines_beta.getText());
        //System.out.println("After Copy Text Details: "+ Instant.now());
    }

    private void addDualScrolls(){
        Var<Double> alphaScrollY = scroll_pane_alpha.estimatedScrollYProperty();
        Var<Double> betaScrollY = scroll_pane_beta.estimatedScrollYProperty();
        boolean[] isBusy = new boolean[2];
        final int LEFT = 0, RIGHT = 1;

        alphaScrollY.addListener( (ob,ov,nv) ->
        {
            isBusy[LEFT] = true;
            if ( ! isBusy[RIGHT] ) betaScrollY.setValue( nv );
            isBusy[LEFT] = false;
        });

        betaScrollY.addListener( (ob,ov,nv) ->
        {
            isBusy[RIGHT] = true;
            if ( ! isBusy[LEFT] ) alphaScrollY.setValue( nv );
            isBusy[RIGHT] = false;
        });
    }
}