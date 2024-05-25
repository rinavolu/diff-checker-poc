package org.diffchecker.diffcheckerpoc.controllers.database;

public class KeyDiff {

    /*
    * ALPHA_ROW_MISS - ARM
    * BETA_ROW_MISS - BRM
    * ALPHA_ROW_MISS_MATCH in alpha table - ARMM
    * BETA_ROW_MISS_MATCH in beta table - BRMM
    * ROW_MISS_MATCH - ABRMM
    * */

    private String primaryKey;

    private String diffType;


    public KeyDiff(String primaryKey, String diffType) {
        this.primaryKey = primaryKey;
        this.diffType = diffType;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getDiffType() {
        return diffType;
    }
}
