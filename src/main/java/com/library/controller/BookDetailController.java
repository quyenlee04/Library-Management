package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.util.AlertUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BookDetailController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label categoryLabel;
    @FXML private Label publisherLabel;
    @FXML private Label yearLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button borrowButton;
    
    private Book book;
    private BookService bookService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookService = BookService.getInstance();
    }
    
    public void setBook(Book book) {
        this.book = book;
        displayBookDetails();
    }
    
    private void displayBookDetails() {
        if (book != null) {
            titleLabel.setText(book.getTenSach());
            authorLabel.setText(book.getTacGia());
            categoryLabel.setText(book.getTheLoai());
            // Change getNhaXuatBan() which doesn't exist to a property that does exist
            // Assuming publisherLabel should show some book information
            publisherLabel.setText("N/A"); // Replace with actual property when available
            yearLabel.setText(String.valueOf(book.getNamXuatBan()));
            statusLabel.setText(book.getTrangThai());
            descriptionTextArea.setText(book.getMoTa());
            
            // Use isAvailable() method from Book class
            if (book.isAvailable()) {
                borrowButton.setDisable(false);
            } else {
                borrowButton.setDisable(true);
            }
        }
    }

    @FXML
    private void handleBorrowBook(ActionEvent event) {
        if (book == null) {
            // Update to use the correct AlertUtil method signature (2 parameters instead of 3)
            AlertUtil.showError("Lỗi", "Không thể mượn sách do không tìm thấy thông tin sách.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/borrow-book-view.fxml"));
            Parent root = loader.load();
            
            BorrowBookController controller = loader.getController();
            controller.setBook(book);
            
            Stage stage = new Stage();
            stage.setTitle("Đặt mượn sách");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Sau khi đóng cửa sổ mượn sách, cập nhật lại thông tin sách
            book = bookService.getBookById(book.getMaSach()).orElse(book);
            displayBookDetails();
            
        } catch (IOException e) {
            // Update to use the correct AlertUtil method signature (2 parameters instead of 3)
            AlertUtil.showError("Lỗi", "Không thể mở cửa sổ mượn sách: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        // Đóng cửa sổ chi tiết sách
        if (borrowButton.getScene() != null) {
            borrowButton.getScene().getWindow().hide();
        }
    }
}