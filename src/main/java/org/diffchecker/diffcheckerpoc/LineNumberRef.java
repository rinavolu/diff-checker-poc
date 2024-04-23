package org.diffchecker.diffcheckerpoc;

public class LineNumberRef {

    private String type;

    private Integer alphaLineNumber;

    private Integer betaLineNumber;

    public LineNumberRef(String type, Integer alphaLineNumber, Integer betaLineNumber) {
        this.type = type;
        this.alphaLineNumber = alphaLineNumber;
        this.betaLineNumber = betaLineNumber;
    }

    public String getType() {
        return type;
    }

    public Integer getAlphaLineNumber() {
        return alphaLineNumber;
    }

    public Integer getBetaLineNumber() {
        return betaLineNumber;
    }
}
