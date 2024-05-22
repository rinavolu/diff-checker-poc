package org.diffchecker.diffcheckerpoc.controllers.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.RowProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicTableView extends TableView<ObservableList<String>> {

    private ResultSet resultSet;

    private List<String> columnNames;

    private String[] primaryKeys;

    private Map<String, String> dataMap;

    public DynamicTableView(ResultSet resultSet,String[] primaryKeys) throws SQLException {
        super();
        this.resultSet = resultSet;
        this.columnNames = new ArrayList<>();
        this.primaryKeys = primaryKeys;
        this.dataMap = new HashMap<>();
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

        RowProcessor rowProcessor = new BasicRowProcessor();

        //Get Data
        while (resultSet.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();

            //Process row to a map
            //Extract Primary Key values for map
            StringBuilder primaryKey = new StringBuilder();
            for(String pkColumn : primaryKeys){
                primaryKey.append(resultSet.getString(pkColumn));
            }

            String rowStr = rowProcessor.toMap(resultSet).toString();

            this.dataMap.put(primaryKey.toString(), rowStr);

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

    public Map<String, String> getDataMap() {
        return dataMap;
    }
}
