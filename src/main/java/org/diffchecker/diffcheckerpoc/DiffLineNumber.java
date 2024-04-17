package org.diffchecker.diffcheckerpoc;

public class DiffLineNumber {

    private Integer paragraphNumber;

    private Integer linesCount;


    public DiffLineNumber(Integer paragraphNumber, Integer linesCount) {
        this.paragraphNumber = paragraphNumber;
        this.linesCount = linesCount;
    }

    public Integer getParagraphNumber() {
        return paragraphNumber;
    }

    public Integer getLinesCount() {
        return linesCount;
    }
}
