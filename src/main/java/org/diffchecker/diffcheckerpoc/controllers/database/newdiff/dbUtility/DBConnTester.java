package org.diffchecker.diffcheckerpoc.controllers.database.newdiff.dbUtility;

import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.DBConnectionStatus;
import org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model.DBDetails;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnTester {

    public static DBConnectionStatus testDatabaseConnection(DBDetails details) {
        Connection conn = null;
        DBConnectionStatus connectionStatus = new DBConnectionStatus();
        try {
            conn = DriverManager.getConnection(details.getDb_url(), details.getDb_username(), details.getDb_password());
            connectionStatus.setConnectionValid(true);
            connectionStatus.setException("");
        } catch (SQLException e) {
            connectionStatus.setConnectionValid(false);
            connectionStatus.setException(e.getMessage());
        } finally {
            try{ if (conn != null) conn.close();
                //System.out.println("Database Connection Closed");
            } catch(SQLException e) {
                System.out.println("Unable to close connection");
            }
        }
        return connectionStatus;
    }
}
