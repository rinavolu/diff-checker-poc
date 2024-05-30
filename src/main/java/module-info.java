module org.diffchecker2.diffcheckerpoc {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires io.github.javadiffutils;
    requires org.fxmisc.flowless;
    requires reactfx;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.apache.commons.io;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.core5.httpcore5.h2;
    requires curl;
    requires com.google.gson;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.apache.commons.dbutils;
    requires MaterialFX;
    requires com.dlsc.gemsfx;


    opens org.diffchecker.diffcheckerpoc to javafx.fxml;
    exports org.diffchecker.diffcheckerpoc;
    exports org.diffchecker.diffcheckerpoc.controllers.database.newdiff;
    exports org.diffchecker.diffcheckerpoc.controllers;
    exports org.diffchecker.diffcheckerpoc.controllers.directory;
    exports org.diffchecker.diffcheckerpoc.controllers.api;
    exports org.diffchecker.diffcheckerpoc.controllers.database;
    opens org.diffchecker.diffcheckerpoc.controllers to javafx.fxml;
    exports org.diffchecker.diffcheckerpoc.controllers.database.newdiff.model;
}