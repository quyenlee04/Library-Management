module com.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires jbcrypt;
    requires itextpdf; // Changed from com.itextpdf.kernel
    // Remove these lines as they're for iText 7
    // requires com.itextpdf.layout;
    // requires com.itextpdf.io;

    opens com.library to javafx.fxml;
    opens com.library.controller to javafx.fxml;
    opens com.library.model to javafx.base;

    exports com.library;
    exports com.library.controller;
    exports com.library.model;
    exports com.library.util;
    exports com.library.service;
}
