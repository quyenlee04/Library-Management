package com.library.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.library.model.ActivityLog;
import com.library.model.Borrowing;
import com.library.model.BorrowingDetail;
import com.library.service.ActivityLogService;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.service.ReaderService;
import com.library.view.ViewManager;

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

public class LibrarianDashboardController implements Initializable {
    
    @FXML private Label lblTotalBooks;
    @FXML private Label lblTotalReaders;
    @FXML private Label lblActiveBorrowings;
    @FXML private Label lblOverdueBooks;
    
    @FXML private TableView<ActivityLog> tblRecentActivities;
    @FXML private TableColumn<ActivityLog, String> colDate;
    @FXML private TableColumn<ActivityLog, String> colActivity;
    @FXML private TableColumn<ActivityLog, String> colUser;
    
    private AuthService authService;
    private BookService bookService;
    private ReaderService readerService;
    @FXML private TableView<BorrowingDetail> tblActiveBorrowings;
    @FXML private TableColumn<BorrowingDetail, String> colBorrowId;
    @FXML private TableColumn<BorrowingDetail, String> colBookTitle;
    @FXML private TableColumn<BorrowingDetail, String> colReaderName;
    @FXML private TableColumn<BorrowingDetail, LocalDate> colBorrowDate;
    @FXML private TableColumn<BorrowingDetail, LocalDate> colDueDate;
    @FXML private TableColumn<BorrowingDetail, String> colStatus;
    
    private BorrowingService borrowingService;
    private ActivityLogService activityLogService;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        authService = AuthService.getInstance();
        bookService = BookService.getInstance();
        readerService = ReaderService.getInstance();
        borrowingService = BorrowingService.getInstance();
        activityLogService = ActivityLogService.getInstance();
        
        // Initialize recent activities table columns
        colDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getTimestamp().toLocalDate();
            return new SimpleStringProperty(date.format(dateFormatter));
        });
        
        colActivity.setCellValueFactory(new PropertyValueFactory<>("action"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        // Initialize active borrowings table columns
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("maMuonTra"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        
        // Format date columns
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("ngayMuon"));
        colBorrowDate.setCellFactory(column -> new TableCell<BorrowingDetail, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });
        
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("ngayHenTra"));
        colDueDate.setCellFactory(column -> new TableCell<BorrowingDetail, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                    // Highlight overdue dates
                    if (date.isBefore(LocalDate.now())) {
                        setTextFill(javafx.scene.paint.Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(javafx.scene.paint.Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });
        
        colStatus.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        
        // Load dashboard data
        loadDashboardData();
        
        // Load active borrowings
        loadActiveBorrowings();
    }
    
    private void loadDashboardData() {
        // Load statistics
        try {
            int totalBooks = bookService.getTotalBooks();
            lblTotalBooks.setText(String.valueOf(totalBooks));
            
            int totalReaders = readerService.getTotalReaders();
            lblTotalReaders.setText(String.valueOf(totalReaders));
            
            int activeBorrowings = borrowingService.getTotalActiveBorrowings();
            lblActiveBorrowings.setText(String.valueOf(activeBorrowings));
            
            int overdueBooks = borrowingService.getTotalOverdueBorrowings();
            lblOverdueBooks.setText(String.valueOf(overdueBooks));
            
            // Load recent activities
            ObservableList<ActivityLog> recentActivities = 
                FXCollections.observableArrayList(activityLogService.getRecentActivities(10));
            tblRecentActivities.setItems(recentActivities);
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        ViewManager.getInstance().switchToLoginView();
    }
    
    @FXML
    private void handleBooksManagement(ActionEvent event) {
        ViewManager.getInstance().switchToBookManagementView();
    }
    
    @FXML
    private void handleReadersManagement(ActionEvent event) {
        ViewManager.getInstance().switchToReaderManagementView();
    }
    
    @FXML
    private void handleBorrowingsManagement(ActionEvent event) {
        ViewManager.getInstance().switchToProcessBorrowingView();
    }
    
    @FXML
    private void handleReturnsManagement(ActionEvent event) {
        ViewManager.getInstance().switchToProcessReturnView();
    }
    
    @FXML
    private void handleFinesManagement(ActionEvent event) {
        ViewManager.getInstance().switchToFineManagementView();
    }
    
    @FXML
    private void handleReports(ActionEvent event) {
        ViewManager.getInstance().switchToReportsView();
    }
    
    // Add these field declarations to your controller
    @FXML private Button btnBooks;
    @FXML private Button btnReaders;
    @FXML private Button btnBorrowings;
    @FXML private Button btnReturns;
    @FXML private Button btnFines;
    @FXML private Button btnReports;
    @FXML private Button btnLogout;

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadDashboardData();
        loadActiveBorrowings();
    }
    
    private void updateDashboardStats() {
        // Update dashboard statistics
        BookService bookService = BookService.getInstance();
        ReaderService readerService = ReaderService.getInstance();
        BorrowingService borrowingService = BorrowingService.getInstance();
        
        // Set the values for the dashboard cards
        lblTotalBooks.setText(String.valueOf(bookService.getAllBooks().size()));
        lblTotalReaders.setText(String.valueOf(readerService.getAllReaders().size()));
        lblActiveBorrowings.setText(String.valueOf(borrowingService.getActiveBorrowingsCount()));
        lblOverdueBooks.setText(String.valueOf(borrowingService.getOverdueBorrowingsCount()));
        
        // Load recent activities
        List<String> activities = borrowingService.getRecentActivities(5);
        tblRecentActivities.getItems().clear();
        
        for (String activity : activities) {
            // Parse the activity string to extract date and details
            int dashIndex = activity.lastIndexOf(" - ");
            if (dashIndex > 0) {
                String activityText = activity.substring(0, dashIndex);
                String dateTimeText = activity.substring(dashIndex + 3);
                
                // Create a new activity log
                ActivityLog log = new ActivityLog();
                
                // Convert the string to LocalDateTime instead of Timestamp
                try {
                    // Parse the dateTimeText to LocalDateTime
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeText);
                    log.setTimestamp(dateTime);
                } catch (Exception e) {
                    // If parsing fails, use current time
                    log.setTimestamp(LocalDateTime.now());
                    System.err.println("Error parsing date: " + dateTimeText + " - " + e.getMessage());
                }
                
                log.setAction(activityText);
                log.setUsername("System");
                
                tblRecentActivities.getItems().add(log);
            }
        }
    }
    
    private void loadActiveBorrowings() {
        BorrowingService borrowingService = BorrowingService.getInstance();
        List<Borrowing> activeBorrowings = borrowingService.getActiveBorrowings();
        
        // Clear existing items
        tblActiveBorrowings.getItems().clear();
        
        // Process each active borrowing
        for (Borrowing borrowing : activeBorrowings) {
            try {
                // Get detailed information about this borrowing
                List<BorrowingDetail> details = borrowingService.getBorrowingDetails(borrowing.getMaMuonTra());
                
                // Add all details to the table
                tblActiveBorrowings.getItems().addAll(details);
            } catch (Exception e) {
                System.err.println("Error loading borrowing details: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // If no active borrowings, display a message
        if (tblActiveBorrowings.getItems().isEmpty()) {
            // You could add a placeholder or message here
            System.out.println("No active borrowings found");
        }
    }
}