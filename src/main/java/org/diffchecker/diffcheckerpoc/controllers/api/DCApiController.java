package org.diffchecker.diffcheckerpoc.controllers.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        curl_alpha_text_area.setWrapText(true);
        curl_beta_text_area.setWrapText(true);
        initializeResponseTextArea();
        alpha_execute_btn.setOnAction(event -> executeAlphaCurl(curl_alpha_text_area.getText()));

        beta_execute_btn.setOnAction(event -> executeBetaCurl(curl_beta_text_area.getText()));
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
        String alpha_response = $(alpha_curl_str);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(alpha_response);
        alpha_response_text_area.appendText(gson.toJson(jsonElement));
        alpha_response_text_area.setStyleSpans(0,getStyleSpans(gson.toJson(jsonElement)));
    }

    private void executeBetaCurl(String beta_curl_str){
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

}
