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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BorrowingHistoryController implements Initializable {

    @FXML private Label readerInfoLabel;
    @FXML private TableView<Borrowing> borrowingTableView;
    @FXML private TableColumn<Borrowing, String> bookTitleColumn;
    @FXML private TableColumn<Borrowing, LocalDate> borrowDateColumn;
    @FXML private TableColumn<Borrowing, LocalDate> dueDateColumn;
    @FXML private TableColumn<Borrowing, LocalDate> returnDateColumn;
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
        
        returnDateColumn.setCellValueFactory(
            new PropertyValueFactory<>("ngayTraThucTe"));
        returnDateColumn.setCellFactory(column -> new TableCell<Borrowing, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText("Chưa trả");
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
                        case "DA_TRA":
                            setText("Đã trả");
                            break;
                        case "QUA_HAN":
                            setText("Quá hạn");
                            break;
                        default:
                            setText(status);
                    }
                }
            }
        });
        
        // Cột thao tác (nút gia hạn)
        actionColumn.setCellFactory(column -> new TableCell<Borrowing, String>() {
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
                    // Chỉ hiển thị nút gia hạn nếu sách đang mượn và chưa trả
                    if ("DANG_MUON".equals(borrowing.getTrangThai()) && borrowing.getNgayTraThucTe() == null) {
                        setGraphic(extendButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
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
            // Thử lấy thông tin từ User hiện tại nếu userId trống
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
                    AlertUtil.showError("Lỗi", "Vui lòng đăng nhập để xem lịch sử mượn/trả");
                    closeWindow();
                }
            } else {
                AlertUtil.showError("Lỗi", "Vui lòng đăng nhập để xem lịch sử mượn/trả");
                closeWindow();
            }
        }
    }
    
    private void loadBorrowingData() {
        if (currentReader != null) {
            List<Borrowing> borrowings = borrowingService.getBorrowingsByReaderId(currentReader.getMaDocGia());
            borrowingList.clear();
            borrowingList.addAll(borrowings);
            borrowingTableView.setItems(borrowingList);
        }
    }
    
    private void handleExtendBorrowing(Borrowing borrowing) {
        // Kiểm tra điều kiện gia hạn
        LocalDate today = LocalDate.now();
        
        // Không cho phép gia hạn nếu đã quá hạn
        if (borrowing.getNgayHenTra().isBefore(today)) {
            AlertUtil.showWarning("Không thể gia hạn", "Sách đã quá hạn trả, không thể gia hạn");
            return;
        }
        
        // Xác nhận gia hạn
        boolean confirm = AlertUtil.showConfirmation("Xác nhận gia hạn", 
                "Bạn có chắc chắn muốn gia hạn sách \"" + borrowing.getTenSach() + "\" thêm 7 ngày?");
        
        if (confirm) {
            boolean success = borrowingService.extendBorrowing(borrowing.getMaMuonTra());
            if (success) {
                AlertUtil.showInformation("Thành công", "Gia hạn sách thành công");
                loadBorrowingData(); // Tải lại dữ liệu
            } else {
                AlertUtil.showError("Lỗi", "Không thể gia hạn sách. Vui lòng thử lại sau");
            }
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadBorrowingData();
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
    
    /**
     * Hiển thị chỉ những sách quá hạn
     */
    public void showOnlyOverdueBooks() {
        if (currentReader != null) {
            LocalDate today = LocalDate.now();
            List<Borrowing> overdueBooks = borrowingService.getBorrowingsByReaderId(currentReader.getMaDocGia())
                .stream()
                .filter(b -> b.getNgayTraThucTe() == null && b.getNgayHenTra().isBefore(today))
                .collect(Collectors.toList());
            
            borrowingList.clear();
            borrowingList.addAll(overdueBooks);
            borrowingTableView.setItems(borrowingList);
            
            // Cập nhật tiêu đề để hiển thị rõ đang xem sách quá hạn
            readerInfoLabel.setText("Độc giả: " + currentReader.getTenDocGia() + 
                                   " (Mã: " + currentReader.getMaDocGia() + ") - Sách quá hạn");
        }
    }
}