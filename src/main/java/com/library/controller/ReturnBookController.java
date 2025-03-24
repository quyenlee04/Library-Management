package com.library.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.library.model.Borrowing;
import com.library.model.Book;
import com.library.model.Reader;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.service.FineService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.util.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleBooleanProperty;

public class ReturnBookController implements Initializable {

    @FXML private Label readerInfoLabel;
    @FXML private TableView<Borrowing> borrowingTableView;
    @FXML private TableColumn<Borrowing, String> bookTitleColumn;
    @FXML private TableColumn<Borrowing, LocalDate> borrowDateColumn;
    @FXML private TableColumn<Borrowing, LocalDate> dueDateColumn;
    @FXML private TableColumn<Borrowing, String> statusColumn;
    @FXML private TableColumn<Borrowing, Boolean> selectColumn;
    @FXML private Button returnButton;
    @FXML private Button cancelButton;
    
    private BorrowingService borrowingService;
    private BookService bookService;
    private ReaderService readerService;
    private FineService fineService;
    private Reader currentReader;
    private ObservableList<Borrowing> borrowingList;
    private final ObservableList<Borrowing> selectedBooks = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        borrowingService = BorrowingService.getInstance();
        bookService = BookService.getInstance();
        readerService = ReaderService.getInstance();
        fineService = FineService.getInstance();
        borrowingList = FXCollections.observableArrayList();
        
        // Thiết lập các cột cho TableView
        setupTableColumns();
        
        // Lấy thông tin độc giả hiện tại
        loadCurrentReader();
        
        // Tải dữ liệu mượn/trả
        loadBorrowingData();
    }
    
    private void setupTableColumns() {
        // Cấu hình hiển thị cho các cột
        bookTitleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTenSach()));
            
        borrowDateColumn.setCellValueFactory(
            new PropertyValueFactory<>("ngayMuon"));
        borrowDateColumn.setCellFactory(column -> new TableCell<Borrowing, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });
        
        dueDateColumn.setCellValueFactory(
            new PropertyValueFactory<>("ngayHenTra"));
        dueDateColumn.setCellFactory(column -> new TableCell<Borrowing, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });
        
        statusColumn.setCellValueFactory(
            new PropertyValueFactory<>("trangThai"));
        statusColumn.setCellFactory(column -> new TableCell<Borrowing, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                } else {
                    switch (status) {
                        case "DANG_MUON":
                            setText("Đang mượn");
                            break;
                        case "QUA_HAN":
                            setText("Quá hạn");
                            setStyle("-fx-text-fill: red;");
                            break;
                        default:
                            setText(status);
                    }
                }
            }
        });
        
        // Cột checkbox để chọn sách cần trả
        selectColumn.setCellValueFactory(param -> {
            Borrowing borrowing = param.getValue();
            SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(selectedBooks.contains(borrowing));
            
            // Listener để cập nhật danh sách sách đã chọn
            booleanProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedBooks.add(borrowing);
                } else {
                    selectedBooks.remove(borrowing);
                }
                // Cập nhật trạng thái nút trả sách
                returnButton.setDisable(selectedBooks.isEmpty());
            });
            
            return booleanProperty;
        });
        
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);
        borrowingTableView.setEditable(true);
        
        // Mặc định nút trả sách bị vô hiệu hóa
        returnButton.setDisable(true);
    }
    
    private void loadCurrentReader() {
        String currentUserId = SessionManager.getCurrentUserId();
        System.out.println("Loading reader with ID: " + currentUserId);
        
        if (currentUserId != null && !currentUserId.isEmpty()) {
            Optional<Reader> readerOpt = readerService.getReaderById(currentUserId);
            if (readerOpt.isPresent()) {
                currentReader = readerOpt.get();
                readerInfoLabel.setText("Độc giả: " + currentReader.getTenDocGia() + " (Mã: " + currentReader.getMaDocGia() + ")");
            } else {
                // Thử lấy thông tin từ User hiện tại
                AlertUtil.showError("Lỗi", "Không tìm thấy thông tin độc giả");
                closeWindow();
            }
        } else {
            AlertUtil.showError("Lỗi", "Vui lòng đăng nhập để xem lịch sử mượn/trả");
            closeWindow();
        }
    }
    
    private void loadBorrowingData() {
        if (currentReader != null) {
            // Chỉ lấy những sách đang mượn (chưa trả)
            List<Borrowing> borrowings = borrowingService.getBorrowingsByReaderId(currentReader.getMaDocGia())
                .stream()
                .filter(b -> b.getNgayTraThucTe() == null)
                .collect(Collectors.toList());
            
            // Kiểm tra và cập nhật trạng thái quá hạn
            LocalDate today = LocalDate.now();
            borrowings.forEach(b -> {
                if (b.getNgayHenTra().isBefore(today)) {
                    b.setTrangThai("QUA_HAN");
                }
            });
            
            borrowingList.clear();
            borrowingList.addAll(borrowings);
            borrowingTableView.setItems(borrowingList);
        }
    }
    
    @FXML
    private void handleReturnBooks(ActionEvent event) {
        if (selectedBooks.isEmpty()) {
            AlertUtil.showWarning("Cảnh báo", "Vui lòng chọn ít nhất một sách để trả");
            return;
        }
        
        boolean confirm = AlertUtil.showConfirmation("Xác nhận trả sách", 
                "Bạn có chắc chắn muốn trả " + selectedBooks.size() + " sách đã chọn?");
        
        if (confirm) {
            try {
                LocalDate returnDate = LocalDate.now();
                
                // Process each book individually instead of as a batch
                boolean allSuccess = true;
                
                for (Borrowing borrowing : selectedBooks) {
                    // Update each borrowing record with return date
                    boolean success = borrowingService.returnBook(borrowing.getMaMuonTra(), returnDate);
                    
                    if (success) {
                        // Update book availability - using the correct method
                        try {
                            // First get the book by ID
                            Optional<Book> bookOpt = bookService.getBookById(borrowing.getMaSach());
                            // In the handleReturnBooks method
                            if (bookOpt.isPresent()) {
                                Book book = bookOpt.get();
                                // Change this line from "AVAILABLE" to "CHO_MUON" to match your status convention
                                book.setTrangThai("CHO_MUON");
                                bookService.updateBook(book);
                            }
                        } catch (Exception e) {
                            System.err.println("Error updating book status: " + e.getMessage());
                        }
                        
                        // Check if book is overdue and create fine if needed
                        if (borrowing.getNgayHenTra().isBefore(returnDate)) {
                            try {
                                // If FineService is not implemented yet, we can skip this
                                // or implement a basic version
                                System.out.println("Book is overdue, creating fine for: " + borrowing.getMaMuonTra());
                                // Uncomment when FineService is implemented
                                // fineService.createOverdueFine(borrowing.getMaMuonTra());
                            } catch (Exception e) {
                                System.err.println("Error creating fine: " + e.getMessage());
                                // Continue processing even if fine creation fails
                            }
                        }
                    } else {
                        allSuccess = false;
                    }
                }
                
                if (allSuccess) {
                    AlertUtil.showInformation("Thành công", "Bạn đã trả sách thành công.");
                } else {
                    AlertUtil.showWarning("Cảnh báo", "Một số sách đã được trả, nhưng có lỗi xảy ra với một số sách khác.");
                }
                
                // Reload data regardless of success/failure
                selectedBooks.clear();
                loadBorrowingData();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showError("Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}