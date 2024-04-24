package org.diffchecker.diffcheckerpoc;

import javafx.scene.control.Label;

import java.io.File;

public class DCFile {

    private Label fileName;

    private File file;

    private String filePath;

    private Boolean isDirectory;

    private long fileSize;

    private String filePermissions;

    public DCFile(Label fileName, File file, String filePath, long fileSize,String filePermissions) {
        this.fileName = fileName;
        this.file = file;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.isDirectory = file.isDirectory();
        this.filePermissions = filePermissions;
    }

    public Label getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

    public String getFilePath() {
        return filePath;
    }

    public Boolean getDirectory() {
        return isDirectory;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFilePermissions() {
        return filePermissions;
    }

    public void setFileName(Label fileName) {
        this.fileName = fileName;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDirectory(Boolean directory) {
        isDirectory = directory;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilePermissions(String filePermissions) {
        this.filePermissions = filePermissions;
    }
}
