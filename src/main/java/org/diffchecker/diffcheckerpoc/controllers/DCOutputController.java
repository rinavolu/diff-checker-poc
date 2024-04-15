package org.diffchecker.diffcheckerpoc.controllers;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;

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

        close_stage.setOnAction(actionEvent -> {
            Stage stage =(Stage) close_stage.getScene().getWindow();
            stage.close();
        });
        initializeTextArea();

    }

    private void initializeTextArea(){
        text_area_output_alpha.setParagraphGraphicFactory(LineNumberFactory.get(text_area_output_alpha));
        text_area_output_beta.setParagraphGraphicFactory(LineNumberFactory.get(text_area_output_beta));
    }


    public void copyTextDetails(StyleClassedTextArea text_area_input_alpha,
                                StyleClassedTextArea text_area_input_beta){
        //Compute Diff

        //computeDiff(text_area_input_alpha,text_area_input_beta);

        newComputeDiff(text_area_input_alpha,text_area_input_beta);
    }


    public void newComputeDiff(StyleClassedTextArea text_area_input_alpha,
                               StyleClassedTextArea text_area_input_beta){
        String text_alpha= text_area_input_alpha.getText();
        String text_beta = text_area_input_beta.getText();

        Patch<String> patch = DiffUtils.diff(Arrays.asList(text_alpha.split("")), Arrays.asList(text_beta.split("")));

        List<AbstractDelta<String>> deltas = patch.getDeltas();
        int startPositionAlpha = 0;
        int startPositionBeta = 0;

        //Fill text area Alpha
        for(AbstractDelta<String> delta:deltas){

            //Alpha
            int startLineAlpha = delta.getSource().getPosition();
            int endLineAlpha = delta.getSource().getPosition()+delta.getSource().size();

            //Copy Text Alpha
            copyText("ALPHA",text_area_input_alpha,startPositionAlpha,startLineAlpha);

            //Add the delta to alpha
            copyDelta("ALPHA",delta,text_area_input_alpha);
            startPositionAlpha = endLineAlpha;


            //Beta
            int startLineBeta = delta.getTarget().getPosition();
            int endLineBeta = delta.getTarget().getPosition()+ delta.getTarget().size();

            //Copy Text Alpha
            copyText("BETA",text_area_input_beta,startPositionBeta,startLineBeta);

            //Add the delta to beta
            copyDelta("BETA",delta,text_area_input_beta);
            startPositionBeta = endLineBeta;


        }

        //Copy remaining text to alpha
        copyText("ALPHA",text_area_input_alpha,startPositionAlpha, text_alpha.length());

        //Copy remaining text to beta
        copyText("BETA",text_area_input_beta,startPositionBeta, text_beta.length());

    }

    public void copyText(String textAreaType, StyleClassedTextArea text_area_input,
                               int startPosition,int endPosition){
        String text= text_area_input.getText();
        System.out.println(textAreaType+" :: Appending Text copyText():: "+text.substring(startPosition,endPosition));
        if(textAreaType.equals("ALPHA")){
            this.text_area_output_alpha.append(text.substring(startPosition,endPosition),"no-diff-bg");
        }else{
            this.text_area_output_beta.append(text.substring(startPosition,endPosition),"no-diff-bg");
        }
    }

    private void copyDelta(String textAreaType, AbstractDelta<String> delta,StyleClassedTextArea text_area_input){
        String text = text_area_input.getText();
        switch (delta.getType()){
            case INSERT :
                copyDelta(textAreaType,delta,text, "new-line-highlight");
                break;
            case CHANGE,DELETE:
                copyDelta(textAreaType, delta,text,"old-line-highlight");
                break;
            case EQUAL:
                System.out.println("CHECK THIS: "+delta.getType());
        }
    }

    private void copyDelta(String type, AbstractDelta<String> delta,String text,String styleClass){
        if(type.equals("ALPHA")){
            int startPosition = delta.getSource().getPosition();
            int endPosition = delta.getSource().getPosition()+delta.getSource().size();
            System.out.println("Alpha :: Appending Delta Text ("+delta.getType()+") copyDelta():: "+
                    text.substring(startPosition,endPosition) +" size: "+text.substring(startPosition,endPosition).length());

            text_area_output_alpha.append(text.substring(startPosition, endPosition), styleClass);

        }else {
            int startPosition = delta.getTarget().getPosition();
            int endPosition = delta.getTarget().getPosition()+delta.getTarget().size();
            System.out.println("Beta :: Appending Delta Text ("+delta.getType()+") copyDelta():: "+text.substring(startPosition,endPosition));
            text_area_output_beta.append(text.substring(startPosition,endPosition),styleClass);
        }

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
