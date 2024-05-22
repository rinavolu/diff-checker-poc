package org.diffchecker.diffcheckerpoc.controllers.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DynamicTableView extends TableView<ObservableList<String>> {

    private ResultSet resultSet;

    private List<String> columnNames;

    public DynamicTableView(ResultSet resultSet) throws SQLException {
        super();
        this.resultSet = resultSet;
        this.columnNames = new ArrayList<>();
        buildTableData();
    }

    private void buildTableData() throws SQLException {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        //Get Columns & set headers
        System.out.println("Column count :"+resultSet.getMetaData().getColumnCount());
        for(int i = 0 ; i < resultSet.getMetaData().getColumnCount();i++){
            final int j = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(resultSet.getMetaData().getColumnName(i+1));
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));

            getColumns().addAll(col);
        }

        //Get Data
        while (resultSet.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(resultSet.getString(i));
            }
            //System.out.println("Row [1] added " + row);
            data.add(row);

        }

        //FINALLY ADDED TO TableView
        setItems(data);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }


}
