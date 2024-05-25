package org.diffchecker.diffcheckerpoc.controllers.database;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DCTableController implements Initializable {

    public BorderPane left_border_pane;

    public BorderPane right_border_pane;

    public Button diff_compute_btn;

    private Map<String,String> alpha_data_map,beta_data_map;

    private DynamicTableView alpha_dynamic_table_view, beta_dynamic_table_view;

    //TODO Change to Array
    private List<KeyDiff> keyDiffs;
    private Map<String, KeyDiff> key_diff_map;

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
        alpha_dynamic_table_view = new DynamicTableView(resultSet, fetchPrimaryKeysColumnName(databaseConnection, tableName));
        alpha_data_map = alpha_dynamic_table_view.getDataMap();
        left_border_pane.setCenter(alpha_dynamic_table_view);

        sql = "SELECT * FROM lk_state_2";
        tableName = "lk_state_2";
        ResultSet resultSet2 = databaseConnection.executeQuery(sql);
        beta_dynamic_table_view =  new DynamicTableView(resultSet2, fetchPrimaryKeysColumnName(databaseConnection, tableName));
        beta_data_map = beta_dynamic_table_view.getDataMap();
        right_border_pane.setCenter(beta_dynamic_table_view);
    }

    private void onDiffComputeBtnClicked() {
        this.keyDiffs =  new ArrayList<>();
        this.key_diff_map = new HashMap<>();
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
                    key_diff_map.put(entry.getKey(), new KeyDiff(entry.getKey(), "ARM"));
                }else{
                    keyDiffs.add(new KeyDiff(entry.getKey(), "BRM"));
                    key_diff_map.put(entry.getKey(), new KeyDiff(entry.getKey(), "BRM"));
                }
            }else{
                //Compare key values that are in secondary map
                if(!secondaryMap.get(entry.getKey()).equals(entry.getValue())){
                    keyDiffs.add(new KeyDiff(entry.getKey(),"ABRMM"));
                    key_diff_map.put(entry.getKey(), new KeyDiff(entry.getKey(), "ABRMM"));
                }
            }
        }

        for(Map.Entry<String, String> entry: secondaryMap.entrySet()){
            //Filter key values that are not in primary map
            if(!primaryMap.containsKey(entry.getKey())){
                if(secondaryMapType.equals("ALPHA")) {
                    keyDiffs.add(new KeyDiff(entry.getKey(), "ARM"));
                    key_diff_map.put(entry.getKey(), new KeyDiff(entry.getKey(), "ARM"));
                }else{
                    keyDiffs.add(new KeyDiff(entry.getKey(), "BRM"));
                    key_diff_map.put(entry.getKey(), new KeyDiff(entry.getKey(), "BRM"));
                }
            }
        }
        DynamicTableView tableViewAlpha = new DynamicTableView(alpha_dynamic_table_view.getColumns(),
                alpha_dynamic_table_view.getItems(),
                key_diff_map);
        left_border_pane.getChildren().clear();
        left_border_pane.setCenter(tableViewAlpha);


        DynamicTableView tableViewBeta = new DynamicTableView(beta_dynamic_table_view.getColumns(),
                beta_dynamic_table_view.getItems(), key_diff_map);
        right_border_pane.getChildren().clear();
        right_border_pane.setCenter(tableViewBeta);
    }

    private void applyStylingBasedOnLookup(){

        //Note: not a suitable approach
        //TODO need to know primary key Indexes
        //TODO remove 3rd parameter @applyStyleForTableRow
        //https://gist.github.com/jewelsea/2886805
        int i =0;
        final ObservableList<ObservableList<String>> alpha_items = alpha_dynamic_table_view.getItems();
        final ObservableList<ObservableList<String>> beta_items = beta_dynamic_table_view.getItems();
        Set<Node> nodes = alpha_dynamic_table_view.lookupAll("TableRow");
        for(Node n : alpha_dynamic_table_view.lookupAll("TableRow")){
            if(n instanceof TableRow<?>){
                String primaryKeyValue = alpha_items.get(i).get(0);
                System.out.println("Checking alpha primaryKeyValue "+primaryKeyValue+" i value"+ i );
                if(key_diff_map.containsKey(primaryKeyValue)){
                    TableRow tableRow = (TableRow) n;
                    applyStyleForTableRow(tableRow, primaryKeyValue, "ALPHA");
                }
            }else System.out.println("Not a instance");
            //if(i==alpha_items.size()+1) break;
            i++;
        }
        i=0;

        for(Node n : beta_dynamic_table_view.lookupAll("TableRow")){
            if(n instanceof TableRow<?>){
                String primaryKeyValue = beta_items.get(i).get(0);
                System.out.println("Checking beta primaryKeyValue "+primaryKeyValue+" i value"+ i );
                if(key_diff_map.containsKey(primaryKeyValue)){
                    TableRow tableRow = (TableRow) n;
                    applyStyleForTableRow(tableRow , primaryKeyValue, "BETA");
                }
            }
            if(i==beta_items.size()) break;
            i++;
        }

    }

    private void applyStyleForTableRow(TableRow tableRow, String primaryKey, String lookupType){
        String diffType= key_diff_map.get(primaryKey).getDiffType();

        if(diffType.equals("ARM")||diffType.equals("BRM")){
            tableRow.getStyleClass().add("arm_brm_container");
        } else if (diffType.equals("ABRMM")) {
            tableRow.getStyleClass().add("abrmm_container");
        }
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

