module org.diffchecker2.diffcheckerpoc {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires io.github.javadiffutils;


    opens org.diffchecker.diffcheckerpoc to javafx.fxml;
    exports org.diffchecker.diffcheckerpoc;
}