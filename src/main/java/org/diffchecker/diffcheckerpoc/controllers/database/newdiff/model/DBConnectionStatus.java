package org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model;

public class DBConnectionStatus {

    private Boolean isConnectionValid;

    private String exception;

    public DBConnectionStatus(Boolean isConnectionValid, String exception) {
        this.isConnectionValid = isConnectionValid;
        this.exception = exception;
    }

    public DBConnectionStatus() {
    }

    public Boolean getConnectionValid() {
        return isConnectionValid;
    }

    public String getException() {
        return exception;
    }

    public void setConnectionValid(Boolean connectionValid) {
        isConnectionValid = connectionValid;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
