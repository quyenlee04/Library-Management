/**
 * The main module for the library application. It declares the required dependencies and
 * exposes the necessary packages for FXML loading.
 */
module com.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    
    opens com.library to javafx.fxml;
    opens com.library.controller to javafx.fxml;
    
    exports com.library;
    exports com.library.controller;
    exports com.library.model;
}