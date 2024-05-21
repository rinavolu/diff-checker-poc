package org.diffchecker.diffcheckerpoc;

public class DiffMatch {

    public String styleClass;

    public int startPos;

    public int endPos;

    public DiffMatch(String styleClass, int startPos, int endPos) {
        this.styleClass = styleClass;
        this.startPos = startPos;
        this.endPos = endPos;
    }
}
