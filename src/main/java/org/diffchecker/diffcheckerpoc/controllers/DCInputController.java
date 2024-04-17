package org.diffchecker.diffcheckerpoc.controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.reactfx.value.Var;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DCInputController implements Initializable {


    public StyleClassedTextArea text_area_alpha;
    public StyleClassedTextArea text_area_beta;
    public Button diff_check_button;
    public VirtualizedScrollPane scroll_pane_alpha;
    public VirtualizedScrollPane scroll_pane_beta;

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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/diffchecker/diffcheckerpoc/dc-output-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        DCOutputController controller= fxmlLoader.getController();
        controller.copyTextDetails(this.text_area_alpha,this.text_area_beta);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
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