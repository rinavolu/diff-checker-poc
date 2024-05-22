package org.diffchecker.diffcheckerpoc.controllers.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DCTableController implements Initializable {

    public BorderPane left_border_pane;

    public BorderPane right_border_pane;

    /*public TableView tableView;
    private ObservableList<ObservableList> data;*/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fetchData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchData() throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        String sql = "SELECT * FROM lk_state";
        ResultSet resultSet = databaseConnection.executeQuery(sql);
        DynamicTableView dynamicTableView = new DynamicTableView(resultSet);

        left_border_pane.setCenter(dynamicTableView);

        sql = "SELECT * FROM lk_state_2";
        ResultSet resultSet2 = databaseConnection.executeQuery(sql);
        DynamicTableView dynamicTableView2 =  new DynamicTableView(resultSet2);

        right_border_pane.setCenter(dynamicTableView2);

        //buildTableView(resultSet);
        //left_pane.getChildren().add(tableView);

    }

    /*private void buildTableView(ResultSet rs) {
        //https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
        tableView = new TableView();
        data = FXCollections.observableArrayList();

        try{
            *//**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             *********************************
             *//*
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tableView.getColumns().addAll(col);
                //System.out.println("Column [" + i + "] ");
            }

            *//**
             * ******************************
             * Data added to ObservableList *
             *******************************
             *//*

            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                //System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tableView.setItems(data);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }*/
}
