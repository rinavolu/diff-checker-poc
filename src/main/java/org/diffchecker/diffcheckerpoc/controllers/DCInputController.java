package org.diffchecker.diffcheckerpoc.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class DCInputController implements Initializable {


    public StyleClassedTextArea text_area_alpha;
    public StyleClassedTextArea text_area_beta;
    public Button diff_check_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTextArea();
    }

    private void initializeTextArea(){
        text_area_alpha.clear();
        text_area_beta.clear();

        text_area_alpha.setParagraphGraphicFactory(LineNumberFactory.get(text_area_alpha));
        text_area_beta.setParagraphGraphicFactory(LineNumberFactory.get(text_area_beta));

    }
}