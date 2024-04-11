package org.diffchecker.diffcheckerpoc.controllers;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.Paragraph;
import org.reactfx.collection.LiveList;

import java.net.URL;
import java.util.*;

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

        text_area_output_alpha.setWrapText(true);
        text_area_output_beta.setWrapText(true);
        initializeTextArea();

    }

    private void initializeTextArea(){
        text_area_output_alpha.setParagraphGraphicFactory(LineNumberFactory.get(text_area_output_alpha));
        text_area_output_beta.setParagraphGraphicFactory(LineNumberFactory.get(text_area_output_beta));
    }


    public void copyTextDetails(StyleClassedTextArea text_area_input_alpha,
                                StyleClassedTextArea text_area_input_beta){
        //Compute Diff

        computeDiff(text_area_input_alpha,text_area_input_beta);
        //text_area_output_alpha.replaceText(text_area_input_alpha.getText());
        //text_area_output_beta.replaceText(text_area_input_beta.getText());
    }

    private void computeDiff(StyleClassedTextArea text_area_input_alpha,
                             StyleClassedTextArea text_area_input_beta){
        int totalParagraphs = text_area_input_alpha.getParagraphs().size();
        for(int i=0;i<totalParagraphs;i++){
            String text_alpha = text_area_input_alpha.getText(i);
            String text_beta = text_area_input_beta.getText(i);
            List<AbstractDelta<String>> deltas = getDeltas(text_alpha,text_beta);
            List<Integer> diffIndexesAlpha = new ArrayList<>();
            List<Integer> diffIndexesBeta = new ArrayList<>();
            for (AbstractDelta<String> delta : deltas) {
                int startIndexAlpha = delta.getSource().getPosition();
                int endIndexAlpha = delta.getSource().last();
                for(int j=startIndexAlpha;j<=endIndexAlpha;j++){
                    diffIndexesAlpha.add(j);
                }

                int startIndexBeta = delta.getTarget().getPosition();
                int endIndexBeta = delta.getTarget().last();
                for(int j=startIndexBeta;j<=endIndexBeta;j++){
                    diffIndexesBeta.add(j);
                }
            }

            //AppendText
            for(int index=0;index<text_alpha.length();index++){
                char ch = text_alpha.charAt(index);
                if(diffIndexesAlpha.contains(index)){
                    text_area_output_alpha.append(String.valueOf(ch),"old-line-highlight");
                }else{
                    text_area_output_alpha.append(String.valueOf(ch),"old-line");
                }
            }

            for(int index=0;index<text_beta.length();index++){
                char ch = text_beta.charAt(index);
                if(diffIndexesBeta.contains(index)){
                    text_area_output_beta.append(String.valueOf(ch),"new-line-highlight");
                }else{
                    text_area_output_beta.append(String.valueOf(ch),"new-line");
                }
            }



        }

    }

    private List<AbstractDelta<String>> getDeltas(String text_alpha, String text_beta){
        Patch<String> diff = DiffUtils.diff(Arrays.asList(text_alpha.split("")), Arrays.asList(text_beta.split("")));
        return diff.getDeltas();
    }


}
