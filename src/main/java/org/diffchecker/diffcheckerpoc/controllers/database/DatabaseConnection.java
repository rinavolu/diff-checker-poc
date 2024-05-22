package org.diffchecker.diffcheckerpoc.controllers.database;

import java.sql.*;

public class DatabaseConnection {

    private Connection connection;

    private DatabaseMetaData databaseMetaData;

    private static final String url = "jdbc:postgresql://localhost:5432/diff_checker";
    private static final String user = "postgres";
    private static final String password = "RAvijay@7482";

    public DatabaseConnection() {
        try{
            this.connection = DriverManager.getConnection(url,user,password);
            this.databaseMetaData = connection.getMetaData();
            System.out.println("Connection established");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        ResultSet resultSet =null;
        Statement statement;
        try{
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery(query);
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public DatabaseMetaData getDatabaseMetaData() {
        return databaseMetaData;
    }
}
