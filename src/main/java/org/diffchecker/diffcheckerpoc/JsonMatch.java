package org.diffchecker.diffcheckerpoc;

public class JsonMatch {

    public String className;

    public int start;

    public int end;

    public JsonMatch(String className, int start, int end) {
        this.className = className;
        this.start = start;
        this.end = end;
    }
}
