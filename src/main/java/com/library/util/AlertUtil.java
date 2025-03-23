package com.library.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Utility class for displaying various types of alerts in the application.
 */
public class AlertUtil {
    
    /**
     * Shows an information alert with the given title and message.
     * 
     * @param title The title of the alert
     * @param message The message to display
     */
    public static void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows an error alert with the given title and message.
     * 
     * @param title The title of the alert
     * @param message The error message to display
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows an error alert with the given title, header, and message.
     * 
     * @param title The title of the alert
     * @param header The header text
     * @param message The error message to display
     */
    public static void showError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows a warning alert with the given title and message.
     * 
     * @param title The title
     * @param message The message to display
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}