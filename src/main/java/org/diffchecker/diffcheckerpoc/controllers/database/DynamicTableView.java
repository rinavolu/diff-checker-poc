package org.diffchecker.diffcheckerpoc.controllers.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

    public DynamicTableView(ObservableList<TableColumn<ObservableList<String>, ?>> columns,
                            ObservableList<ObservableList<String>> rows, Map<String, KeyDiff> keyDiffs){
        super();
        this.columnNames = new ArrayList<>();
        buildTableData(columns, rows, keyDiffs);
    }

    private void buildTableData() throws SQLException {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        //Get Columns & set headers
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

    private void buildTableData(ObservableList<TableColumn<ObservableList<String>, ?>>columns,
                                ObservableList<ObservableList<String>> rows, Map<String, KeyDiff> keyDiffs){
        getColumns().setAll(columns);
        setRowFactory(tv -> new TableRow<ObservableList<String>>(){
            @Override
            public void updateItem(ObservableList<String> item, boolean empty) {
                super.updateItem(item, empty);
                if(item==null || empty) {
                    setGraphic(null);
                    setStyle("");
                }
                else if (keyDiffs.containsKey(item.get(0))) {
                    String primaryKey = item.get(0);
                    KeyDiff keyDiff = keyDiffs.get(primaryKey);
                    if(keyDiff.getDiffType().equals("ARM")||keyDiff.getDiffType().equals("BRM")){
                        //System.out.println("Applying style for "+primaryKey);
                        getStyleClass().clear();
                        getStyleClass().add("arm_brm_container");
                    }else if(keyDiff.getDiffType().equals("ABRMM")){
                        getStyleClass().clear();
                        getStyleClass().add("abrmm_container");
                    }
                }else{
                    getStyleClass().add("normal_container");
                }
            }
        });
        setItems(rows);
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }
}
