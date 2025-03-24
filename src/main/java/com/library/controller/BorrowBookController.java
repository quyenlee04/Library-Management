package com.library.controller;

import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.Reader;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BorrowBookController implements Initializable {

    @FXML private Label bookTitleLabel;
    @FXML private Label bookAuthorLabel;
    @FXML private Label bookCategoryLabel;
    @FXML private Label bookStatusLabel;
    @FXML private DatePicker borrowDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    
    private Book selectedBook;
    private Reader currentReader;
    private BookService bookService;
    private ReaderService readerService;
    private BorrowingService borrowingService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookService = BookService.getInstance();
        readerService = ReaderService.getInstance();
        borrowingService = BorrowingService.getInstance();
        
        // Thiết lập ngày mượn là ngày hiện tại
        borrowDatePicker.setValue(LocalDate.now());
        
        // Thiết lập ngày trả mặc định là 14 ngày sau
        returnDatePicker.setValue(LocalDate.now().plusDays(14));
        
        // Lấy thông tin độc giả hiện tại từ session
        String currentUserId = SessionManager.getCurrentUserId();
        if (currentUserId != null) {
            try {
                // Sử dụng phương thức đúng để lấy thông tin độc giả
                Optional<Reader> readerOpt = readerService.getReaderById(currentUserId);
                if (readerOpt.isPresent()) {
                    currentReader = readerOpt.get();
                } else {
                    AlertUtil.showError("Error", "Không tìm thấy thông tin độc giả");
                    cancelBorrow(null);
                }
            } catch (Exception e) {
                AlertUtil.showError("Error", "Không thể lấy thông tin độc giả: " + e.getMessage());
                e.printStackTrace();
                cancelBorrow(null);
            }
        } else {
            AlertUtil.showError("Error", "Vui lòng đăng nhập để mượn sách");
            cancelBorrow(null);
        }
    }
    
    public void setBook(Book book) {
        this.selectedBook = book;
        displayBookInfo();
    }
    
    private void displayBookInfo() {
        if (selectedBook != null) {
            bookTitleLabel.setText(selectedBook.getTenSach());
            bookAuthorLabel.setText(selectedBook.getTacGia());
            bookCategoryLabel.setText(selectedBook.getTheLoai());
            bookStatusLabel.setText(selectedBook.getTrangThai());
            
            // Kiểm tra xem sách có sẵn để mượn không
            if (!"CHO_MUON".equals(selectedBook.getTrangThai())) {
                confirmButton.setDisable(true);
                AlertUtil.showWarning("Warning", "Sách này hiện không có sẵn để mượn");
            }else{
                confirmButton.setDisable(false);
            }
        }
    }
    
    @FXML
    private void confirmBorrow(ActionEvent event) {
        if (selectedBook == null || currentReader == null) {
            AlertUtil.showError("Error", "Không thể hoàn tất việc mượn sách do thiếu thông tin");
            return;
        }
        
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();
        
        if (borrowDate == null || returnDate == null) {
            AlertUtil.showError("Error", "Vui lòng chọn ngày mượn và ngày trả");
            return;
        }
        
        if (returnDate.isBefore(borrowDate)) {
            AlertUtil.showError("Error", "Ngày trả phải sau ngày mượn");
            return;
        }
        
        // Tạo đối tượng Borrowing mới
        Borrowing borrowing = new Borrowing();
        borrowing.setMaDocGia(currentReader.getMaDocGia());
        borrowing.setMaSach(selectedBook.getMaSach());
        borrowing.setNgayMuon(borrowDate);
        borrowing.setNgayHenTra(returnDate);
        borrowing.setTrangThai("DANG_MUON");
        
        // Tạo danh sách chứa ID của sách được mượn
        List<String> bookIds = new ArrayList<>();
        bookIds.add(selectedBook.getMaSach());
        
        // Lưu thông tin mượn sách - sử dụng phương thức có sẵn trong BorrowingService
        try {
            boolean success = borrowingService.createBorrowing(borrowing, bookIds);
            
            if (success) {
                // Cập nhật trạng thái sách
                selectedBook.setTrangThai("DA_MUON");
                bookService.updateBook(selectedBook);
                
                AlertUtil.showInformation("Information", "Bạn đã đặt mượn sách thành công");
                
                // Đóng cửa sổ đặt mượn
                cancelBorrow(event);
            } else {
                AlertUtil.showError("Error", "Đã xảy ra lỗi khi lưu thông tin mượn sách");
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void cancelBorrow(ActionEvent event) {
        // Đóng cửa sổ hiện tại
        if (cancelButton.getScene() != null) {
            cancelButton.getScene().getWindow().hide();
        }
    }
}