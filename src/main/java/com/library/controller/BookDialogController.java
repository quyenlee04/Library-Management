package com.library.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.library.model.Book;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BookDialogController {
    
    private static final List<String> CATEGORIES = Arrays.asList(
            "Fiction", "Non-Fiction", "Science", "Technology", "Computer Science", 
            "Programming", "Mathematics", "History", "Biography", "Self-Help",
            "Business", "Economics","Novel", "Philosophy", "Psychology", "Religion",
            "Art", "Music", "Travel", "Cooking", "Health", "Other"
    );
    
    public static Optional<Book> showAddDialog() {
        // Create the dialog
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");
        
        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField titleField = new TextField();
        titleField.setPromptText("Book title");
        
        TextField authorField = new TextField();
        authorField.setPromptText("Author name");
        
        TextField yearField = new TextField();
        yearField.setPromptText("Publication Year (e.g., 2023)");
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(FXCollections.observableArrayList(CATEGORIES));
        categoryCombo.setValue("Fiction");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Book description");
        descriptionArea.setPrefRowCount(3);
        
        TextField quantityField = new TextField("1");
        quantityField.setPromptText("Quantity");
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(
                "CHO_MUON", "DA_MUON", "HU_HONG", "MAT_SACH"));
        statusCombo.setValue("CHO_MUON");
        
        // Add fields to grid
        int row = 0;
        grid.add(new Label("Title:"), 0, row);
        grid.add(titleField, 1, row++);
        
        grid.add(new Label("Author:"), 0, row);
        grid.add(authorField, 1, row++);
        
        grid.add(new Label("Publication Year:"), 0, row);
        grid.add(yearField, 1, row++);
        
        grid.add(new Label("Category:"), 0, row);
        grid.add(categoryCombo, 1, row++);
        
        grid.add(new Label("Status:"), 0, row);
        grid.add(statusCombo, 1, row++);
        
        grid.add(new Label("Description:"), 0, row);
        grid.add(descriptionArea, 1, row++);
        
        grid.add(new Label("Quantity:"), 0, row);
        grid.add(quantityField, 1, row++);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        titleField.requestFocus();
        
        // Convert the result to a book when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Validate input
                if (titleField.getText().trim().isEmpty() || 
                    authorField.getText().trim().isEmpty()) {
                    return null;
                }
                
                // Validate year if provided
                int publicationYear = 0;
                if (!yearField.getText().trim().isEmpty()) {
                    try {
                        publicationYear = Integer.parseInt(yearField.getText().trim());
                    } catch (NumberFormatException e) {
                        return null; // Invalid year format
                    }
                }
                
                // Validate quantity
                int quantity = 1;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity < 1) quantity = 1;
                } catch (NumberFormatException e) {
                    quantity = 1;
                }
                
                Book book = new Book();
                book.setMaSach(UUID.randomUUID().toString().substring(0, 8));
                book.setTenSach(titleField.getText().trim());
                book.setTacGia(authorField.getText().trim());
                book.setNamXuatBan(publicationYear);
                book.setTheLoai(categoryCombo.getValue());
                book.setTrangThai(statusCombo.getValue());
                book.setMoTa(descriptionArea.getText().trim());
                book.setSoLuong(quantity);
                book.setSoLuongKhaDung(quantity);
                
                return book;
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
    
    public static Optional<Book> showEditDialog(Book book) {
        // Create the dialog
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Edit book details");
        
        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields with existing values
        TextField titleField = new TextField(book.getTenSach());
        TextField authorField = new TextField(book.getTacGia());
        
        TextField yearField = new TextField(book.getNamXuatBan() > 0 ? 
                String.valueOf(book.getNamXuatBan()) : "");
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(FXCollections.observableArrayList(CATEGORIES));
        categoryCombo.setValue(book.getTheLoai());
        
        TextArea descriptionArea = new TextArea(book.getMoTa());
        descriptionArea.setPrefRowCount(3);
        
        TextField quantityField = new TextField(String.valueOf(book.getSoLuong()));
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(
                "CHO_MUON", "DA_MUON", "HU_HONG", "MAT_SACH"));
        statusCombo.setValue(book.getTrangThai());
        
        // Add fields to grid
        int row = 0;
        grid.add(new Label("Title:"), 0, row);
        grid.add(titleField, 1, row++);
        
        grid.add(new Label("Author:"), 0, row);
        grid.add(authorField, 1, row++);
        
        grid.add(new Label("Publication Year:"), 0, row);
        grid.add(yearField, 1, row++);
        
        grid.add(new Label("Category:"), 0, row);
        grid.add(categoryCombo, 1, row++);
        
        grid.add(new Label("Status:"), 0, row);
        grid.add(statusCombo, 1, row++);
        
        grid.add(new Label("Description:"), 0, row);
        grid.add(descriptionArea, 1, row++);
        
        grid.add(new Label("Quantity:"), 0, row);
        grid.add(quantityField, 1, row++);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        titleField.requestFocus();
        
        // Convert the result to a book when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Validate input
                if (titleField.getText().trim().isEmpty() || 
                    authorField.getText().trim().isEmpty()) {
                    return null;
                }
                
                // Validate year if provided
                int publicationYear = 0;
                if (!yearField.getText().trim().isEmpty()) {
                    try {
                        publicationYear = Integer.parseInt(yearField.getText().trim());
                    } catch (NumberFormatException e) {
                        return null; // Invalid year format
                    }
                }
                
                // Validate quantity
                int quantity = 1;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity < 1) quantity = 1;
                } catch (NumberFormatException e) {
                    quantity = 1;
                }
                
                Book updatedBook = new Book();
                updatedBook.setMaSach(book.getMaSach());
                updatedBook.setTenSach(titleField.getText().trim());
                updatedBook.setTacGia(authorField.getText().trim());
                updatedBook.setNamXuatBan(publicationYear);
                updatedBook.setTheLoai(categoryCombo.getValue());
                updatedBook.setTrangThai(statusCombo.getValue());
                updatedBook.setMoTa(descriptionArea.getText().trim());
                updatedBook.setSoLuong(quantity);
                updatedBook.setSoLuongKhaDung(book.getSoLuongKhaDung());
                
                return updatedBook;
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
}