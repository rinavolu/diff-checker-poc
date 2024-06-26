package org.diffchecker.diffcheckerpoc.controllers;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.diffchecker.diffcheckerpoc.DiffLineNumber;
import org.diffchecker.diffcheckerpoc.Factory.DiffHighlightFactoryAlpha;
import org.diffchecker.diffcheckerpoc.Factory.DiffHighlightFactoryBeta;
import org.diffchecker.diffcheckerpoc.LineNumberRef;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.reactfx.value.Var;

import java.net.URL;
import java.util.*;
import java.util.function.IntFunction;

public class DCOutputController implements Initializable {


    public StyleClassedTextArea text_area_output_alpha;
    public StyleClassedTextArea text_area_output_beta;
    public Button traverse_to_next_delta_btn;
    public VirtualizedScrollPane scroll_pane_beta;
    public VirtualizedScrollPane scroll_pane_alpha;
    public Label total_characters_alpha;
    public Label total_lines_alpha;
    public Label total_characters_beta;
    public Label total_lines_beta;
    public ProgressBar diff_compute_progress_bar;

    private ObservableList<Integer> diffLineNumbersAlpha;
    private ObservableList<Integer> diffLineNumbersBeta;

    private ListProperty<Integer> diffLineNumbersPropertyAlpha;

    private ListProperty<Integer> diffLineNumbersPropertyBeta;

    public Button close_stage;
    private List<LineNumberRef> lineNumberRefs;
    private Iterator<LineNumberRef> lineNumberRefIterator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        text_area_output_alpha.clear();
        text_area_output_beta.clear();
        text_area_output_alpha.setEditable(false);
        text_area_output_beta.setEditable(false);

        text_area_output_alpha.setWrapText(true);
        text_area_output_beta.setWrapText(true);

        this.diffLineNumbersAlpha = FXCollections.observableArrayList();
        this.diffLineNumbersBeta = FXCollections.observableArrayList();

        this.diffLineNumbersPropertyAlpha = new SimpleListProperty<>(diffLineNumbersAlpha);
        this.diffLineNumbersPropertyBeta = new SimpleListProperty<>(diffLineNumbersBeta);

        close_stage.setOnAction(actionEvent -> {
            Stage stage =(Stage) close_stage.getScene().getWindow();
            stage.close();
        });
        traverse_to_next_delta_btn.setOnAction(actionEvent -> travelThroughDeltas());
        lineNumberRefs= new ArrayList<>();
        initializeTextArea();
        //addDualScrolls();
    }

    private void initializeTextArea(){
        //Alpha
        IntFunction<Node> numberFactoryAlpha = LineNumberFactory.get(text_area_output_alpha);
        IntFunction<Node> arrowFactoryAlpha = new DiffHighlightFactoryAlpha(diffLineNumbersPropertyAlpha);
        IntFunction<Node> graphicFactoryAlpha = line -> {
            HBox hbox = new HBox(
                    numberFactoryAlpha.apply(line),
                    arrowFactoryAlpha.apply(line));
            hbox.setStyle("-fx-background-color: #dedbd5;");
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };

        text_area_output_alpha.setParagraphGraphicFactory(graphicFactoryAlpha);

        //Beta
        IntFunction<Node> numberFactoryBeta = LineNumberFactory.get(text_area_output_beta);
        IntFunction<Node> arrowFactoryBeta = new DiffHighlightFactoryBeta(diffLineNumbersPropertyBeta);
        IntFunction<Node> graphicFactoryBeta = line -> {
            HBox hbox = new HBox(
                    numberFactoryBeta.apply(line),
                    arrowFactoryBeta.apply(line));
            hbox.setStyle("-fx-background-color: #dedbd5;");
            hbox.setAlignment(Pos.CENTER_LEFT);
            return hbox;
        };
        text_area_output_beta.setParagraphGraphicFactory(graphicFactoryBeta);
    }


    public void copyTextDetails(String text_area_input_alpha,
                                String text_area_input_beta,
                                String total_characters_alpha,
                                String total_lines_alpha,
                                String total_characters_beta,
                                String total_lines_beta){
        //Compute Diff

        //computeDiff(text_area_input_alpha,text_area_input_beta);
        this.total_characters_alpha.setText(total_characters_alpha);
        this.total_lines_alpha.setText(total_lines_alpha);
        this.total_lines_beta.setText(total_lines_beta);
        this.total_characters_beta.setText(total_characters_beta);

        newComputeDiff(text_area_input_alpha,text_area_input_beta);
    }


    public void newComputeDiff(String text_alpha,
                               String text_beta){
        int text_alpha_length = text_alpha.length();
        int text_beta_length = text_beta.length();
        Patch<String> patch = DiffUtils.diff(Arrays.asList(text_alpha.split("")), Arrays.asList(text_beta.split("")));

        List<AbstractDelta<String>> deltas = patch.getDeltas();

        double progressIncrement = (double) 1 /deltas.size();
        //System.out.println("Initial Progress: "+diff_compute_progress_bar.getProgress());
        Platform.runLater(() -> {
            int startPositionAlpha = 0;
            int startPositionBeta = 0;

            //Fill text area Alpha
            for(AbstractDelta<String> delta:deltas) {
                //Platform.runLater(()->diff_compute_progress_bar.setProgress(diff_compute_progress_bar.getProgress()+progressIncrement));
                //System.out.println("Setting Progress to :"+(diff_compute_progress_bar.getProgress()+progressIncrement));
                diff_compute_progress_bar.setProgress(diff_compute_progress_bar.getProgress()+progressIncrement);

                //Add paragraph numbers to list
                //alpha
                int alpha_paragraph_line_number =-1;
                int beta_paragraph_line_number =-1;
                if (delta.getSource().getPosition() < text_alpha.length()) {
                    alpha_paragraph_line_number = getLineNumberForIndex(text_alpha, delta.getSource().getPosition());
                    int alpha_new_lines_count = getLinesCount(delta.getSource().getLines());
                    DiffLineNumber alphaDiffLineNumber = new DiffLineNumber(alpha_paragraph_line_number, alpha_new_lines_count);

                    addDiffLineNumbers(alphaDiffLineNumber, true);
                }

                //beta
                if(delta.getTarget().getPosition() < text_beta.length()) {
                    beta_paragraph_line_number = getLineNumberForIndex(text_beta, delta.getTarget().getPosition());
                    int beta_new_lines_count = getLinesCount(delta.getTarget().getLines());
                    DiffLineNumber betaDiffLineNumber = new DiffLineNumber(beta_paragraph_line_number, beta_new_lines_count);

                    addDiffLineNumbers(betaDiffLineNumber, false);
                }

                if(alpha_paragraph_line_number!=-1 || beta_paragraph_line_number!=-1){
                    String str="";
                    if(alpha_paragraph_line_number==-1&&beta_paragraph_line_number!=-1){
                        str="BETA";
                    }
                    if (alpha_paragraph_line_number!=-1&&beta_paragraph_line_number==-1) {
                        str="ALPHA";
                    }
                    if (alpha_paragraph_line_number!=-1&&beta_paragraph_line_number!=-1){
                        str= "ALPHA+BETA";
                    }
                    lineNumberRefs.add(new LineNumberRef(str,alpha_paragraph_line_number,beta_paragraph_line_number));
                }

                //Alpha
                int startLineAlpha = delta.getSource().getPosition();
                int endLineAlpha = delta.getSource().getPosition()+delta.getSource().size();

                //Copy Text Alpha
                copyText("ALPHA",text_alpha,startPositionAlpha,startLineAlpha);

                //Add the delta to alpha
                copyDelta("ALPHA",delta,text_alpha);
                startPositionAlpha = endLineAlpha;


                //Beta
                int startLineBeta = delta.getTarget().getPosition();
                int endLineBeta = delta.getTarget().getPosition()+ delta.getTarget().size();

                //Copy Text Alpha
                copyText("BETA",text_beta,startPositionBeta,startLineBeta);

                //Add the delta to beta
                copyDelta("BETA",delta,text_beta);
                startPositionBeta = endLineBeta;
            }

            //Copy remaining text to alpha
            copyText("ALPHA",text_alpha,startPositionAlpha, text_alpha_length);

            //Copy remaining text to beta
            copyText("BETA",text_beta,startPositionBeta, text_beta_length);
            lineNumberRefIterator = lineNumberRefs.iterator();
        });
    }

    private void addDiffLineNumbers(DiffLineNumber diffLineNumber,Boolean isAlpha){
        int paraNumber = diffLineNumber.getParagraphNumber();
        int linesCount = diffLineNumber.getLinesCount();
        if(linesCount==0){
            if(isAlpha){
                diffLineNumbersAlpha.add(paraNumber);
            }else{
                diffLineNumbersBeta.add(paraNumber);
            }
            return;
        }
        for(int i=0; i < diffLineNumber.getLinesCount();i++){
            if(isAlpha)
                diffLineNumbersAlpha.add(paraNumber+i);
            else
                diffLineNumbersBeta.add(paraNumber+i);
        }
    }

    public int getLineNumberForIndex(String text, int index) {
        int lineNumber = 1; // Assuming lines are numbered starting from 1

        // Extract the substring up to the given index
        String substring = text.substring(0, index+1);

        // Count the number of newline characters in the substring
        for (int i = 0; i < substring.length(); i++) {
            if (substring.charAt(i) == '\n') {
                lineNumber++;
            }
        }

         return lineNumber;
    }

    public Integer getLinesCount(List<String> lines){
        Integer count = 0;
        for (String i : lines) {
            if (i.contains("\n")) count++;
        }
        return count;
    }

    public void copyText(String textAreaType, String text_input,
                               int startPosition,int endPosition){
       // System.out.println(textAreaType+" :: Appending Text copyText():: "+text.substring(startPosition,endPosition));
        if(textAreaType.equals("ALPHA")){
            this.text_area_output_alpha.append(text_input.substring(startPosition,endPosition),"no-diff-bg");
        }else{
            this.text_area_output_beta.append(text_input.substring(startPosition,endPosition),"no-diff-bg");
        }
    }

    private void copyDelta(String textAreaType, AbstractDelta<String> delta,String text_input){
        switch (delta.getType()){
            case INSERT :
                copyDelta(textAreaType,delta,text_input, "new-line-highlight");
                break;
            case CHANGE,DELETE:
                copyDelta(textAreaType, delta,text_input,"old-line-highlight");
                break;
            case EQUAL:
                System.out.println("CHECK THIS: "+delta.getType());
        }
    }

    private void copyDelta(String type, AbstractDelta<String> delta,String text,String styleClass){
        if(type.equals("ALPHA")){
            int startPosition = delta.getSource().getPosition();
            int endPosition = delta.getSource().getPosition()+delta.getSource().size();
           /* System.out.println("Alpha :: Appending Delta Text ("+delta.getType()+") copyDelta():: "+
                    text.substring(startPosition,endPosition) +" size: "+text.substring(startPosition,endPosition).length());*/

            text_area_output_alpha.append(text.substring(startPosition, endPosition), styleClass);

        }else {
            int startPosition = delta.getTarget().getPosition();
            int endPosition = delta.getTarget().getPosition()+delta.getTarget().size();
            //System.out.println("Beta :: Appending Delta Text ("+delta.getType()+") copyDelta():: "+text.substring(startPosition,endPosition));
            text_area_output_beta.append(text.substring(startPosition,endPosition),styleClass);
        }

    }

    private void travelThroughDeltas(){
        if(lineNumberRefIterator.hasNext()){
            LineNumberRef ref = lineNumberRefIterator.next();
            travelToLineNumbers(ref.getAlphaLineNumber(), ref.getBetaLineNumber());
        }
    }

    private void travelToLineNumbers(Integer alpha_line_number, Integer beta_line_number){
        System.out.println("Travel to Line Map Alpha:"+alpha_line_number+" Beta:"+beta_line_number);
        if(alpha_line_number!=-1){
            text_area_output_alpha.showParagraphAtCenter(alpha_line_number);
        }
        if(beta_line_number!=-1){
            text_area_output_beta.showParagraphAtCenter(beta_line_number);
        }
        if(!lineNumberRefIterator.hasNext()){
            lineNumberRefIterator = lineNumberRefs.iterator();
        }
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
