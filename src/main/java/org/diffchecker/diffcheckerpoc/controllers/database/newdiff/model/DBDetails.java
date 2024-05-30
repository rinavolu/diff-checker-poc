package org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model;

public class DBDetails {

    private String db_url;

    private String db_username;

    private String db_password;

    public DBDetails(String db_url, String db_username, String db_password) {
        this.db_url = db_url;
        this.db_username = db_username;
        this.db_password = db_password;
    }

    public String getDb_url() {
        return db_url;
    }

    public String getDb_username() {
        return db_username;
    }

    public String getDb_password() {
        return db_password;
    }
}
