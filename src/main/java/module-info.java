module org.diffchecker2.diffcheckerpoc {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires io.github.javadiffutils;
    requires org.fxmisc.flowless;
    requires reactfx;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.apache.commons.io;



    opens org.diffchecker.diffcheckerpoc to javafx.fxml;
    exports org.diffchecker.diffcheckerpoc;
    exports org.diffchecker.diffcheckerpoc.controllers;
    exports org.diffchecker.diffcheckerpoc.controllers.directory;
    opens org.diffchecker.diffcheckerpoc.controllers to javafx.fxml;
}