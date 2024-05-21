package org.diffchecker.diffcheckerpoc.controllers.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.diffchecker.diffcheckerpoc.DiffMatch;
import org.diffchecker.diffcheckerpoc.JsonMatch;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.util.*;

import static org.toilelibre.libe.curl.Curl.$;

public class DCApiController implements Initializable {


    public TextArea curl_alpha_text_area;
    public Button alpha_execute_btn;
    public StyleClassedTextArea alpha_response_text_area;
    public TextArea curl_beta_text_area;
    public Button beta_execute_btn;
    public StyleClassedTextArea beta_response_text_area;
    public Button diff_check_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        curl_alpha_text_area.setWrapText(true);
        curl_beta_text_area.setWrapText(true);
        initializeResponseTextArea();
        alpha_execute_btn.setOnAction(event -> executeAlphaCurl(curl_alpha_text_area.getText()));

        beta_execute_btn.setOnAction(event -> executeBetaCurl(curl_beta_text_area.getText()));
        diff_check_btn.setOnAction(actionEvent -> computeDiff());
    }

    private void computeDiff() {
        //Refer this for multiple style spans
        //https://github.com/FXMisc/RichTextFX/issues/715#issuecomment-377659072
        List<StyleSpans<Collection<String>>> computedStyleSpans = getComputedStyleSpans();
        alpha_response_text_area.setStyleSpans(0, computedStyleSpans.get(0));
        beta_response_text_area.setStyleSpans(0, computedStyleSpans.get(1));
    }

    private void initializeResponseTextArea(){
        alpha_response_text_area.setWrapText(true);
        alpha_response_text_area.setParagraphGraphicFactory(LineNumberFactory.get(alpha_response_text_area));

        beta_response_text_area.setWrapText(true);
        beta_response_text_area.setParagraphGraphicFactory(LineNumberFactory.get(beta_response_text_area));
    }

    private void executeAlphaCurl(String alpha_curl_str){

        /*TODO Refactoring Required
        *  1. Use only one either gson or fasterxml
        *  2. Refactor getStyleSpans() */
        alpha_response_text_area.clear();
        String alpha_response = $(alpha_curl_str);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(alpha_response);
        alpha_response_text_area.appendText(gson.toJson(jsonElement));
        alpha_response_text_area.setStyleSpans(0,getStyleSpans(gson.toJson(jsonElement)));
    }

    private void executeBetaCurl(String beta_curl_str){
        beta_response_text_area.clear();
        String beta_response = $(beta_curl_str);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(beta_response);
        beta_response_text_area.appendText(gson.toJson(jsonElement));
        beta_response_text_area.setStyleSpans(0,getStyleSpans(gson.toJson(jsonElement)));
    }

    private StyleSpans<Collection<String>> getStyleSpans(String jsonResponse){

        //Reference : https://leward.eu/json-editor-with-richtextfx-part-2/
        List<JsonMatch> matches = new ArrayList<>();
        JsonFactory jsonFactory = new JsonFactory();
        try {
            com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(jsonResponse);
            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();
                int start = (int) parser.currentTokenLocation().getCharOffset();
                int end = start + parser.getTextLength();

                // Because getTextLength() does contain the surrounding ""
                if(jsonToken == JsonToken.VALUE_STRING || jsonToken == JsonToken.FIELD_NAME) {
                    end += 2;
                }

                String className = jsonTokenToClassName(jsonToken);
                if (!className.isEmpty()) {
                    JsonMatch m = new JsonMatch(className, start, end);
                    matches.add(m);
                }
            }
        } catch (Exception e) {
             System.err.printf("JSON Error: %s%n", e.getMessage());
        }

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastPos = 0;
        for (JsonMatch match : matches) {
            // Fill the gaps, since Style Spans need to be contiguous.
            if (match.start > lastPos) {
                int length = match.start - lastPos;
                spansBuilder.add(Collections.emptyList(), length);
            }

            int length = match.end - match.start;
            spansBuilder.add(Collections.singleton(match.className), length);
            lastPos = match.end;
        }

        if(lastPos == 0) {
            spansBuilder.add(Collections.emptyList(), jsonResponse.length());
        }

        return spansBuilder.create();
    }

    private String jsonTokenToClassName(JsonToken jsonToken) {
        if (jsonToken == null) {
            return "";
        }
        return switch (jsonToken) {
            case FIELD_NAME -> "json-property";
            case VALUE_STRING -> "json-string";
            case VALUE_NUMBER_FLOAT, VALUE_NUMBER_INT -> "json-number";
            default -> "";
        };
    }


    //New Diff Compute Logic
    private List<StyleSpans<Collection<String>>> getComputedStyleSpans(){

        List<DiffMatch> diffMatchesAlpha = new ArrayList<>();
        List<DiffMatch> diffMatchesBeta = new ArrayList<>();

        //StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        String response_alpha = this.alpha_response_text_area.getText();
        String response_beta = this.beta_response_text_area.getText();
        Patch<String> patch = DiffUtils.diff(Arrays.asList(response_alpha.split("")), Arrays.asList(response_beta.split("")));

        List<AbstractDelta<String>> deltas = patch.getDeltas();
        int startPositionAlpha = 0;
        int startPositionBeta = 0;

        for(AbstractDelta<String> delta:deltas){
            //Alpha
            int alpha_start_pos = delta.getSource().getPosition();
            int alpha_end_pos = delta.getSource().getPosition() + delta.getSource().size();
            //Copy Non Diff Text
            diffMatchesAlpha.add(new DiffMatch("",
                    startPositionAlpha
                    ,alpha_start_pos));
            //Copy Delta
            diffMatchesAlpha.add(new DiffMatch(getStyleClass(delta.getType()),
                    alpha_start_pos,
                    alpha_end_pos));
            startPositionAlpha = alpha_end_pos;


            //Beta
            int beta_start_pos = delta.getTarget().getPosition();
            int beta_end_pos = delta.getTarget().getPosition() + delta.getTarget().size();
            //Copy Non Diff Text
            diffMatchesBeta.add(new DiffMatch("",
                    startPositionBeta,
                    beta_start_pos));
            //Copy Delta
            diffMatchesBeta.add(new DiffMatch(getStyleClass(delta.getType()),
                    beta_start_pos,
                    beta_end_pos));
            startPositionBeta = beta_end_pos;
        }

        //Build Spans
        //Alpha
        StyleSpansBuilder<Collection<String>> spansBuilderAlpha = new StyleSpansBuilder<>();
        for(DiffMatch diffMatch:diffMatchesAlpha){
            int length = diffMatch.endPos - diffMatch.startPos;
            if(diffMatch.styleClass.isEmpty()){
                spansBuilderAlpha.add(Collections.emptyList(), length);
            }else {
                spansBuilderAlpha.add(Collections.singleton(diffMatch.styleClass),length);
            }
        }
        StyleSpansBuilder<Collection<String>> spansBuilderBeta = new StyleSpansBuilder<>();
        for(DiffMatch diffMatch:diffMatchesBeta){
            int length = diffMatch.endPos - diffMatch.startPos;
            if(diffMatch.styleClass.isEmpty()){
                spansBuilderBeta.add(Collections.emptyList(), length);
            }else {
                spansBuilderBeta.add(Collections.singleton(diffMatch.styleClass),length);
            }
        }

        List<StyleSpans<Collection<String>>> spansBuilder = new ArrayList<>();
        spansBuilder.add(spansBuilderAlpha.create());
        spansBuilder.add(spansBuilderBeta.create());
        return spansBuilder;
    }

    private String getText(String text , int startPos, int endPos){
        return text.substring(startPos, endPos+1);
    }

    private String getStyleClass(DeltaType type){
        String className = switch (type) {
            case INSERT -> "new-line-highlight";
            case CHANGE, DELETE -> "old-line-highlight";
            case EQUAL -> "";
        };
        return className;
    }

}
