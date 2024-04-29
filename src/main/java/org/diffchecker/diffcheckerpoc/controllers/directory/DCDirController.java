package org.diffchecker.diffcheckerpoc.controllers.directory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.apache.commons.io.FileUtils;
import org.diffchecker.diffcheckerpoc.DCFile;

import java.io.File;
import java.net.URL;
import java.util.*;

public class DCDirController implements Initializable {
    public TextField dir_input_alpha;
    public TextField dir_input_beta;
    public Button dir_input_read_alpha_btn;
    public Button dir_input_read_beta_btn;
    public TreeTableView<DCFile> dir_tree_alpha;
    public TreeTableView<DCFile> dir_tree_beta;
    public TreeTableView<DCFile> diff_dir_tree_alpha;
    public TreeTableView<DCFile> diff_dir_tree_beta;
    public Button diff_compute_btn;

    private Map<String,DCFile> alpha_map;

    private Map<String,DCFile> beta_map;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dir_input_alpha.setText("C:\\Users\\Ravindranadh Inavolu\\OneDrive\\Desktop\\test");
        dir_input_beta.setText("C:\\Users\\Ravindranadh Inavolu\\OneDrive\\Desktop\\testv2");

        //Configure & Add Columns
        addColumnsToTableView();
        dir_input_read_alpha_btn.setOnAction(actionEvent -> {
            TreeItem<DCFile> fileContents = getFileContents(dir_input_alpha.getText(), "ALPHA",false);
            fileContents.setExpanded(true);
            dir_tree_alpha.setRoot(fileContents);
        });

        dir_input_read_beta_btn.setOnAction(actionEvent -> {
            TreeItem<DCFile> fileContents = getFileContents(dir_input_beta.getText(), "BETA",false);
            fileContents.setExpanded(true);
            dir_tree_beta.setRoot(fileContents);
        });

        diff_compute_btn.setOnAction(actionEvent -> computeDiff());

    }

    public TreeItem<DCFile> getFileContents(String dirPath, String dir_type, boolean isDiffCheck){
        File file = new File(dirPath);
        TreeItem<DCFile> root;
        if(file.isDirectory()){
            Label rootLabel = getFileOrFolderLabel(file);
            DCFile dcFile = new DCFile(rootLabel,file,file.getPath(),FileUtils.sizeOfDirectory(file),getFilePermissions(file));
            root = new TreeItem<>(dcFile);
            String pathFromRoot = "";

            Arrays.stream(file.listFiles()).toList().forEach(internalFile-> root.getChildren().add(getFilesAndFolders(internalFile,dir_type,pathFromRoot,isDiffCheck)));
        } else {
            root = null;
        }
        return root;
    }

    public TreeItem<DCFile> getFilesAndFolders(File file, String dir_type,String pathFromRoot, boolean isDiffCheck){
        //TODO Refer to chatGPT code suggestion
        TreeItem<DCFile> root ;

        if(file.isFile()){
            Label fileLabel = getFileOrFolderLabel(file);
            DCFile dcFile = new DCFile(fileLabel,file,file.getPath(),FileUtils.sizeOf(file),getFilePermissions(file));
            pathFromRoot = addFileToMap(file,dir_type, dcFile, pathFromRoot, isDiffCheck);
            dcFile = modifyDCFileBasedOnDiff(dcFile, pathFromRoot, dir_type, isDiffCheck);
            root = new TreeItem<>(dcFile);
        }else {
            Label folderLabel = getFileOrFolderLabel(file);
            DCFile dcFile = new DCFile(folderLabel,file,file.getPath(),FileUtils.sizeOfDirectory(file),getFilePermissions(file));
            pathFromRoot = addFileToMap(file, dir_type, dcFile, pathFromRoot,isDiffCheck);
            dcFile = modifyDCFileBasedOnDiff(dcFile, pathFromRoot, dir_type, isDiffCheck);
            root = new TreeItem<>(dcFile);

            //List<File> files = Arrays.stream(file.listFiles()).toList();
            //files.sort(Comparator.comparing(File::isDirectory).reversed().thenComparing(File::getName));

            File[] files = file.listFiles();
            for(File internalFile: files){
                root.getChildren().add(getFilesAndFolders(internalFile,dir_type,pathFromRoot,isDiffCheck));
            }
        }
        root.setExpanded(true);
        return root;
    }

    private DCFile modifyDCFileBasedOnDiff(DCFile dcFile, String pathFromRoot, String dir_type, boolean isDiffCheck){
        if(!isDiffCheck) return dcFile;
        if(dir_type.equals("ALPHA")){
            if(alpha_map.containsKey(pathFromRoot)) {
                dcFile =alpha_map.get(pathFromRoot);
            }
        }else {
            if(beta_map.containsKey(pathFromRoot)){
                dcFile = beta_map.get(pathFromRoot);
            }
        }
        return dcFile;
    }

    private String addFileToMap(File file, String dir_type, DCFile dcFile, String pathFromRoot, boolean isDiffCheck){
        pathFromRoot += ("\\" + file.getName());
        if(isDiffCheck) return pathFromRoot;
        if(alpha_map==null) alpha_map= new HashMap<>();
        if(beta_map==null) beta_map= new HashMap<>();
        DCFile newDCFile = new DCFile(getFileOrFolderLabel(file),file, dcFile.getFilePath(), dcFile.getFileSize(),dcFile.getFilePermissions());
        if(dir_type.equals("ALPHA")){
            alpha_map.put(pathFromRoot,newDCFile);
        }else {
            beta_map.put(pathFromRoot,newDCFile);
        }
        return pathFromRoot;
    }

    private Label getFileOrFolderLabel(File file){
        Label label = new Label(file.getName());
        if(file.isFile()){
            FontAwesomeIconView fileIcon = new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT);
            fileIcon.setFill(Color.BLUE);
            label.setGraphic(fileIcon);
        }else {
            FontAwesomeIconView folderIcon = new FontAwesomeIconView(FontAwesomeIcon.FOLDER);
            folderIcon.setFill(Color.YELLOW);
            label.setGraphic(folderIcon);
        }
        return label;
    }

    private String getFilePermissions(File file){
        return (file.canRead() ? "r" : "-") +
                (file.canWrite() ? "w" : "-") +
                (file.canExecute() ? "x" : "-");
    }

    private void addColumnsToTableView(){
        //TODO Convert to arrays

        List<TreeTableColumn<DCFile,?>> columns = getColumns();
        columns.forEach(column ->  dir_tree_alpha.getColumns().add(column));

        columns = getColumns();
        columns.forEach(column ->  dir_tree_beta.getColumns().add(column));

        columns = getColumns();
        columns.forEach(column -> diff_dir_tree_alpha.getColumns().add(column));

        columns = getColumns();
        columns.forEach(column ->  diff_dir_tree_beta.getColumns().add(column));

    }

    private List<TreeTableColumn<DCFile,?>> getColumns(){
        List<TreeTableColumn<DCFile,?>> columns = new ArrayList<>();
        TreeTableColumn<DCFile, Label> column1 = new TreeTableColumn<>("File/Folder");
        column1.setPrefWidth(250);
        TreeTableColumn<DCFile,String> column2 = new TreeTableColumn<>("Path");
        TreeTableColumn<DCFile,String> column3 = new TreeTableColumn<>("Size");
        TreeTableColumn<DCFile,String> column4 = new TreeTableColumn<>("Permissions");

        column1.setCellValueFactory(new TreeItemPropertyValueFactory<>("fileName"));
        column2.setCellValueFactory(new TreeItemPropertyValueFactory<>("filePath"));
        column3.setCellValueFactory(new TreeItemPropertyValueFactory<>("fileSize"));
        column4.setCellValueFactory(new TreeItemPropertyValueFactory<>("filePermissions"));
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);
        columns.add(column4);
        return columns;
    }

    private void computeDiff(){
        computeDiff(alpha_map,beta_map);

        //ALPHA
        TreeItem<DCFile> fileContentsAlpha = getFileContents(dir_input_alpha.getText(), "ALPHA",true);
        fileContentsAlpha.setExpanded(true);
        diff_dir_tree_alpha.setRoot(fileContentsAlpha);

        //BETA
        TreeItem<DCFile> fileContentsBeta = getFileContents(dir_input_beta.getText(), "BETA",true);
        fileContentsBeta.setExpanded(true);
        diff_dir_tree_beta.setRoot(fileContentsBeta);

    }

    private void computeDiff(Map<String,DCFile> primary_map, Map<String, DCFile> secondary_map){

        //TODO Change primary to Alpha and secondary to Beta
        for (Map.Entry<String, DCFile> entry : primary_map.entrySet()) {
            //File present in both
            if(secondary_map.containsKey(entry.getKey())){
                DCFile primaryDCFile = entry.getValue();
                DCFile secondaryDCFile = secondary_map.get(entry.getKey());
                //If file is not equal
                if(!compareDCFile(primaryDCFile,secondaryDCFile)){
                    Label primaryLabel = primaryDCFile.getFileName();
                    primaryLabel.setBackground(getBackGroundColor(Color.RED));

                    Label secondaryLabel = secondaryDCFile.getFileName();
                    secondaryLabel.setBackground(getBackGroundColor(Color.GREEN));

                    //Change in map
                    primaryDCFile.setFileName(primaryLabel);
                    secondaryDCFile.setFileName(secondaryLabel);
                    primary_map.put(entry.getKey(),primaryDCFile);
                    secondary_map.put(entry.getKey(),secondaryDCFile);
                }
            } else{
                //File not present in secondary
                DCFile primaryDCFile = entry.getValue();
                Label primaryLabel = primaryDCFile.getFileName();
                primaryLabel.setBackground(getBackGroundColor(Color.RED));
                primaryDCFile.setFileName(primaryLabel);
                primary_map.put(entry.getKey(),primaryDCFile);
            }
        }

        //if(primary_map.size()==secondary_map.size()){
            for (Map.Entry<String, DCFile> entry : secondary_map.entrySet()) {
                DCFile secondaryDCFile = entry.getValue();
                //If file in secondary not available in primary
                if(!primary_map.containsKey(entry.getKey())){
                    Label secondaryFileLabel = secondaryDCFile.getFileName();
                    secondaryFileLabel.setBackground(getBackGroundColor(Color.GREEN));
                    secondaryDCFile.setFileName(secondaryFileLabel);
                    secondary_map.put(entry.getKey(),secondaryDCFile);
                }
            }
       // }
    }

    private boolean compareDCFile(DCFile dcFilePrimary, DCFile dcFileSecondary){
        //File Name
        if(!dcFilePrimary.getFileName().getText().equals(dcFileSecondary.getFileName().getText())) return false;
        //File Size
        if(!dcFilePrimary.getFileSize().equals(dcFileSecondary.getFileSize())) return false;
        //File Permissions
        if(!dcFilePrimary.getFilePermissions().equals(dcFileSecondary.getFilePermissions())) return false;

        return true;
    }

    private Background getBackGroundColor(Color color){
        return new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
    }


}
