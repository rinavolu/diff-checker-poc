package org.diffchecker.diffcheckerpoc.controllers.database;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DCTableController implements Initializable {

    public BorderPane left_border_pane;

    public BorderPane right_border_pane;

    public Button diff_compute_btn;

    private Map<String,String> alpha_data_map,beta_data_map;

    //TODO Change to Array
    private List<KeyDiff> keyDiffs;

    /*public TableView tableView;
    private ObservableList<ObservableList> data;*/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO Check Sneaky Throws
        try {
            fetchData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        diff_compute_btn.setOnAction(event-> onDiffComputeBtnClicked());
    }

    private void fetchData() throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        String sql = "SELECT * FROM lk_state";
        String tableName = "lk_state";
        ResultSet resultSet = databaseConnection.executeQuery(sql);
        DynamicTableView dynamicTableView = new DynamicTableView(resultSet, fetchPrimaryKeysColumnName(databaseConnection, tableName));
        alpha_data_map = dynamicTableView.getDataMap();
        left_border_pane.setCenter(dynamicTableView);

        sql = "SELECT * FROM lk_state_2";
        tableName = "lk_state_2";
        ResultSet resultSet2 = databaseConnection.executeQuery(sql);
        DynamicTableView dynamicTableView2 =  new DynamicTableView(resultSet2, fetchPrimaryKeysColumnName(databaseConnection, tableName));
        beta_data_map = dynamicTableView2.getDataMap();
        right_border_pane.setCenter(dynamicTableView2);
    }

    private void onDiffComputeBtnClicked() {
        this.keyDiffs =  new ArrayList<>();
        Map<String,String> primaryMap, secondaryMap;
        String primaryMapType, secondaryMapType;

        if(alpha_data_map.size() >= beta_data_map.size()){
            primaryMap = alpha_data_map;
            secondaryMap = beta_data_map;
            primaryMapType = "ALPHA";
            secondaryMapType = "BETA";
        }else{
            primaryMap = beta_data_map;
            secondaryMap = alpha_data_map;
            primaryMapType = "BETA";
            secondaryMapType = "ALPHA";
        }

        for(Map.Entry<String, String> entry: primaryMap.entrySet()){
            //Filter key values that are not in secondary map
            if(!secondaryMap.containsKey(entry.getKey())){
                if(primaryMapType.equals("ALPHA")) {
                    keyDiffs.add(new KeyDiff(entry.getKey(), "ARM"));
                }else{
                    keyDiffs.add(new KeyDiff(entry.getKey(), "BRM"));
                }
            }else{
                //Compare key values that are in secondary map
                if(!secondaryMap.get(entry.getKey()).equals(entry.getValue())){
                    keyDiffs.add(new KeyDiff(entry.getKey(),"ABRMM"));
                }
            }
        }

        for(Map.Entry<String, String> entry: secondaryMap.entrySet()){
            //Filter key values that are not in primary map
            if(!primaryMap.containsKey(entry.getKey())){
                if(secondaryMapType.equals("ALPHA")) {
                    keyDiffs.add(new KeyDiff(entry.getKey(), "ARM"));
                }else{
                    keyDiffs.add(new KeyDiff(entry.getKey(), "BRM"));
                }
            }
        }
        System.out.println(keyDiffs.size());
    }

    private String[] fetchPrimaryKeysColumnName(DatabaseConnection databaseConnection,String table) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        String[] primaryKeys;
        ResultSet resultSet = databaseConnection.getDatabaseMetaData().getPrimaryKeys(null, null, table);
        while(resultSet.next()){
            columnNames.add(resultSet.getString("COLUMN_NAME"));
        }
        primaryKeys = new String[columnNames.size()];
        System.out.println("Fetched Primary keys for "+table+ " : "+columnNames);
        return columnNames.toArray(primaryKeys);
    }
}

class KeyDiff {

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
