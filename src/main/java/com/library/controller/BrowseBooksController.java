package com.library.controller;

import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.util.AlertUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BrowseBooksController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeComboBox;
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private TableColumn<Book, String> publishYearColumn;
    @FXML private TableColumn<Book, String> statusColumn;
    @FXML private TableColumn<Book, String> actionsColumn;
    @FXML private Label resultCountLabel;
    
    private BookService bookService;
    private BorrowingService borrowingService;
    private ObservableList<Book> booksList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookService = BookService.getInstance();
        borrowingService = BorrowingService.getInstance();
        
        // Thiết lập các loại tìm kiếm
        searchTypeComboBox.setItems(FXCollections.observableArrayList(
            "Tất cả", "Tên sách", "Tác giả", "Thể loại"
        ));
        searchTypeComboBox.getSelectionModel().selectFirst();
        
        // Thiết lập các cột cho bảng sách
        setupTableColumns();
        
        // Load tất cả sách
        loadAllBooks();
        
        // Thêm listener cho việc nhấn Enter trong ô tìm kiếm
        searchField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleSearch(new ActionEvent());
            }
        });
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaSach()));
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenSach()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTacGia()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTheLoai()));
        publishYearColumn.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().getNamXuatBan())));
        statusColumn.setCellValueFactory(data -> {
            boolean available = data.getValue().getSoLuong() > 0;
            return new SimpleStringProperty(available ? "Có sẵn" : "Đã hết");
        });
        
        // Cột thao tác với nút Xem chi tiết và Đặt mượn
        actionsColumn.setCellFactory(col -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    
                    Button viewButton = new Button("Xem");
                    viewButton.setOnAction(event -> handleViewBookDetails(book));
                    
                    Button borrowButton = new Button("Mượn");
                    borrowButton.setOnAction(event -> handleBorrowBook(book));
                    borrowButton.setDisable(book.getSoLuong() <= 0);
                    
                    HBox buttons = new HBox(5, viewButton, borrowButton);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadAllBooks() {
        List<Book> books = bookService.getAllBooks();
        booksList.setAll(books);
        booksTable.setItems(booksList);
        updateResultCount();
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        String searchType = searchTypeComboBox.getValue();
        
        List<Book> searchResults;
        
        if (keyword.isEmpty()) {
            searchResults = bookService.getAllBooks();
        } else {
            switch (searchType) {
                case "Tên sách":
                    searchResults = bookService.findBooksByTitle(keyword);
                    break;
                case "Tác giả":
                    searchResults = bookService.findBooksByAuthor(keyword);
                    break;
                case "Thể loại":
                    searchResults = bookService.findBooksByCategory(keyword);
                    break;
                default: // Tất cả
                    searchResults = bookService.searchBooks(keyword);
                    break;
            }
        }
        
        booksList.setAll(searchResults);
        booksTable.setItems(booksList);
        updateResultCount();
    }
    
    private void updateResultCount() {
        resultCountLabel.setText("Tổng số: " + booksList.size() + " sách");
    }
    
    private void handleViewBookDetails(Book book) {
        try {
            // Hiển thị dialog chi tiết sách
            BookDetailDialogController.showBookDetailDialog(book);
        } catch (Exception e) {
            AlertUtil.showError("Lỗi", "Không thể hiển thị chi tiết sách: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleBorrowBook(Book book) {
        try {
            // Sửa đường dẫn đến file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrow-book-view.fxml"));
            Parent root = loader.load();
            
            BorrowBookController controller = loader.getController();
            controller.setBook(book);
            
            Stage stage = new Stage();
            stage.setTitle("Đặt mượn sách");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            // Sửa lỗi: searchButton chưa được khai báo, thay bằng searchField
            stage.initOwner(searchField.getScene().getWindow());
            
            stage.showAndWait();
            
            // Refresh book list after borrowing
            handleSearch(null);
        } catch (IOException e) {
            AlertUtil.showError("Lỗi", "Không thể mở màn hình đặt mượn sách: " + e.getMessage());
            System.err.println("Đường dẫn file FXML không tìm thấy: " + getClass().getResource("/fxml/borrow-book-view.fxml"));
            e.printStackTrace();
        }
    }
    
    // Phương thức này được gọi từ controller cha để truyền dịch vụ nếu cần
    public void setServices(BookService bookService, BorrowingService borrowingService) {
        this.bookService = bookService;
        this.borrowingService = borrowingService;
        
        // Tải lại dữ liệu nếu cần
        if (booksList.isEmpty()) {
            loadAllBooks();
        }
    }
}