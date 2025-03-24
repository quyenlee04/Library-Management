package com.library.controller;

import com.library.model.Borrowing;
import com.library.model.Reader;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.BorrowingService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.util.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MyBorrowingsController implements Initializable {

    @FXML private Label readerInfoLabel;
    @FXML private TableView<Borrowing> borrowingTableView;
    @FXML private TableColumn<Borrowing, String> bookTitleColumn;
    @FXML private TableColumn<Borrowing, LocalDate> borrowDateColumn;
    @FXML private TableColumn<Borrowing, LocalDate> dueDateColumn;
    @FXML private TableColumn<Borrowing, String> statusColumn;
    @FXML private TableColumn<Borrowing, String> actionColumn;
    @FXML private Button refreshButton;
    @FXML private Button closeButton;
    
    private BorrowingService borrowingService;
    private ReaderService readerService;
    private Reader currentReader;
    private ObservableList<Borrowing> borrowingList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        borrowingService = BorrowingService.getInstance();
        readerService = ReaderService.getInstance();
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
        
        // Cột thao tác (gia hạn)
        actionColumn.setCellFactory(createActionCellFactory());
    }
    
    private Callback<TableColumn<Borrowing, String>, TableCell<Borrowing, String>> createActionCellFactory() {
        return param -> new TableCell<Borrowing, String>() {
            private final Button extendButton = new Button("Gia hạn");
            
            {
                extendButton.setOnAction(event -> {
                    Borrowing borrowing = getTableView().getItems().get(getIndex());
                    handleExtendBorrowing(borrowing);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Borrowing borrowing = getTableView().getItems().get(getIndex());
                    // Chỉ cho phép gia hạn nếu sách chưa quá hạn
                    if ("DANG_MUON".equals(borrowing.getTrangThai())) {
                        setGraphic(extendButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };
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
                User currentUser = AuthService.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // Tìm Reader dựa trên thông tin User
                    Optional<Reader> readerByUser = readerService.getReaderById(currentUser.getId());
                    if (readerByUser.isPresent()) {
                        currentReader = readerByUser.get();
                        readerInfoLabel.setText("Độc giả: " + currentReader.getTenDocGia() + " (Mã: " + currentReader.getMaDocGia() + ")");
                        // Cập nhật SessionManager
                        SessionManager.setCurrentUserId(currentReader.getMaDocGia());
                    } else {
                        AlertUtil.showError("Lỗi", "Không tìm thấy thông tin độc giả cho tài khoản này");
                        closeWindow();
                    }
                } else {
                    AlertUtil.showError("Lỗi", "Không tìm thấy thông tin độc giả");
                    closeWindow();
                }
            }
        } else {
            AlertUtil.showError("Lỗi", "Vui lòng đăng nhập để xem sách đang mượn");
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
    
    private void handleExtendBorrowing(Borrowing borrowing) {
        LocalDate today = LocalDate.now();
        LocalDate currentDueDate = borrowing.getNgayHenTra();
        
        // Kiểm tra nếu sách đã quá hạn
        if (currentDueDate.isBefore(today)) {
            AlertUtil.showWarning("Không thể gia hạn", "Sách đã quá hạn trả, không thể gia hạn.");
            return;
        }
        
        // Gia hạn thêm 7 ngày từ ngày hẹn trả hiện tại
        LocalDate newDueDate = currentDueDate.plusDays(7);
        
        // Cập nhật ngày hẹn trả mới
        borrowing.setNgayHenTra(newDueDate);
        
        // Lưu thay đổi vào cơ sở dữ liệu
        boolean success = borrowingService.updateBorrowing(borrowing);
        
        if (success) {
            AlertUtil.showInformation("Gia hạn thành công", 
                    "Sách đã được gia hạn thành công đến ngày " + 
                    newDueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            // Làm mới dữ liệu
            loadBorrowingData();
        } else {
            AlertUtil.showError("Lỗi", "Không thể gia hạn sách. Vui lòng thử lại sau.");
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadBorrowingData();
    }
    
    @FXML
    private void handleClose() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}