module org.diffchecker2.diffcheckerpoc {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.diffchecker2.diffcheckerpoc to javafx.fxml;
    exports org.diffchecker2.diffcheckerpoc;
}