package org.diffchecker.diffcheckerpoc.controllers;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class DCOutputController implements Initializable {


    public StyleClassedTextArea text_area_output_alpha;
    public StyleClassedTextArea text_area_output_beta;
    public Button close_stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        text_area_output_alpha.clear();
        text_area_output_beta.clear();
        text_area_output_alpha.setEditable(false);
        text_area_output_beta.setEditable(false);
        initializeTextArea();

    }

    private void initializeTextArea(){
        text_area_output_alpha.setParagraphGraphicFactory(LineNumberFactory.get(text_area_output_alpha));
        text_area_output_beta.setParagraphGraphicFactory(LineNumberFactory.get(text_area_output_beta));
    }


    public void copyTextDetails(StyleClassedTextArea text_area_input_alpha,
                                StyleClassedTextArea text_area_input_beta){

        text_area_output_alpha.replaceText(text_area_input_alpha.getText());
        text_area_output_beta.replaceText(text_area_input_beta.getText());
    }


}
