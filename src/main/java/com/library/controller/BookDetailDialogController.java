package com.library.controller;

import com.library.model.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class BookDetailDialogController {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label categoryLabel;
    @FXML private Label publisherLabel;
    @FXML private Label yearLabel;
    @FXML private Label isbnLabel;
    @FXML private Label availableLabel;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView bookCoverImage;
    @FXML private Button closeButton;
    
    private Book book;
    
    public void setBook(Book book) {
        this.book = book;
        displayBookDetails();
    }
    
    private void displayBookDetails() {
        titleLabel.setText(book.getTenSach());
        authorLabel.setText(book.getTacGia());
        categoryLabel.setText(book.getTheLoai());
        
        // Thay thế các phương thức không tồn tại bằng các phương thức có sẵn hoặc giá trị mặc định
        publisherLabel.setText("Không có thông tin"); // Book không có getNhaXuatBan()
        yearLabel.setText(String.valueOf(book.getNamXuatBan()));
        isbnLabel.setText("Không có thông tin"); // Book không có getIsbn()
        
        // Sử dụng phương thức getSoLuongKhaDung() thay vì getSoLuong() để kiểm tra tính khả dụng
        availableLabel.setText(book.getSoLuongKhaDung() > 0 ? "Có sẵn" : "Đã hết");
        
        // Sử dụng getMoTa() để hiển thị mô tả
        descriptionArea.setText(book.getMoTa() != null ? book.getMoTa() : "Không có mô tả");
        
        // Hiển thị ảnh bìa sách - sử dụng ảnh mặc định vì Book không có getAnhBia()
        try {
            // Luôn sử dụng ảnh mặc định vì Book không có phương thức getAnhBia()
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_book.png"));
            bookCoverImage.setImage(defaultImage);
        } catch (Exception e) {
            // Xử lý nếu không thể tải ảnh mặc định
            bookCoverImage.setImage(null);
            e.printStackTrace();
        }
        
      
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    public static void showBookDetailDialog(Book book) throws IOException {
        FXMLLoader loader = new FXMLLoader(BookDetailDialogController.class.getResource("/fxml/member/book_detail_dialog.fxml"));
        Scene scene = new Scene(loader.load());
        
        BookDetailDialogController controller = loader.getController();
        controller.setBook(book);
        
        Stage stage = new Stage();
        stage.setTitle("Chi tiết sách");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
    }
}