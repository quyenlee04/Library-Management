package com.library.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.BorrowingDetail;
import com.library.model.Fine;
import com.library.service.ActivityLogService;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.service.FineService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProcessReturnController implements Initializable {

    @FXML private TextField txtBorrowId;
    @FXML private Button btnSearch;
    @FXML private TableView<BorrowingDetail> tblBorrowingDetails;
    @FXML private TableColumn<BorrowingDetail, String> colBorrowId;
    @FXML private TableColumn<BorrowingDetail, String> colBookId;
    @FXML private TableColumn<BorrowingDetail, String> colBookTitle;
    @FXML private TableColumn<BorrowingDetail, String> colReaderId;
    @FXML private TableColumn<BorrowingDetail, String> colReaderName;
    @FXML private TableColumn<BorrowingDetail, LocalDate> colBorrowDate;
    @FXML private TableColumn<BorrowingDetail, LocalDate> colDueDate;
    @FXML private DatePicker dpReturnDate;
    @FXML private Label lblStatus;
    @FXML private Label lblFine;
    @FXML private Button btnReturn;
    @FXML private Button btnCancel;
    @FXML private Button btnBack;
    
    private BorrowingService borrowingService;
    private BookService bookService;
    private ReaderService readerService;
    private FineService fineService;
    private ActivityLogService activityLogService;
    
    private Borrowing currentBorrowing;
    private double fineAmount = 0.0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        borrowingService = BorrowingService.getInstance();
        bookService = BookService.getInstance();
        readerService = ReaderService.getInstance();
        fineService = FineService.getInstance();
        activityLogService = ActivityLogService.getInstance();
        
        // Set up table columns
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("maMuonTra"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("maSach"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("maDocGia"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("ngayMuon"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("ngayHenTra"));
        
        // Format date columns
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
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
        
        // Set default return date to today
        dpReturnDate.setValue(LocalDate.now());
        
        // Add listener to return date to recalculate fine
        dpReturnDate.valueProperty().addListener((obs, oldVal, newVal) -> calculateFine());
        
        // Add selection listener to the table
        tblBorrowingDetails.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    handleBorrowingSelection(newSelection);
                }
            });
        
        // Load all active borrowings
        loadActiveBorrowings();
    }
    
    /**
     * Loads all active borrowings into the table
     */
    private void loadActiveBorrowings() {
        try {
            // Clear previous data
            tblBorrowingDetails.getItems().clear();
            lblStatus.setText("");
            lblFine.setText("");
            btnReturn.setDisable(true);
            
            // Get all active borrowings
            List<Borrowing> activeBorrowings = borrowingService.getActiveBorrowings();
            
            // Create a list to hold all borrowing details
            List<BorrowingDetail> allDetails = new ArrayList<>();
            
            // Process each active borrowing
            for (Borrowing borrowing : activeBorrowings) {
                try {
                    // Get detailed information about this borrowing
                    List<BorrowingDetail> details = borrowingService.getBorrowingDetails(borrowing.getMaMuonTra());
                    
                    // Add all details to our list
                    allDetails.addAll(details);
                } catch (Exception e) {
                    System.err.println("Error loading borrowing details: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Add all details to the table
            tblBorrowingDetails.setItems(FXCollections.observableArrayList(allDetails));
            
            // If no active borrowings, display a message
            if (tblBorrowingDetails.getItems().isEmpty()) {
                lblStatus.setText("No active borrowings found");
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Error loading active borrowings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles when a borrowing is selected from the table
     */
    private void handleBorrowingSelection(BorrowingDetail detail) {
        try {
            // Get the full borrowing information
            Optional<Borrowing> borrowingOpt = borrowingService.getBorrowingById(detail.getMaMuonTra());
            
            if (borrowingOpt.isPresent()) {
                currentBorrowing = borrowingOpt.get();
                
                // Enable return button
                btnReturn.setDisable(false);
                
                // Calculate fine if any
                calculateFine();
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Error selecting borrowing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String borrowId = txtBorrowId.getText().trim();
        
        if (borrowId.isEmpty()) {
            AlertUtil.showError("Error", "Please enter a borrowing ID");
            return;
        }
        
        try {
            // Clear previous results
            tblBorrowingDetails.getItems().clear();
            
            // Get borrowing by ID
            Optional<Borrowing> borrowingOpt = borrowingService.getBorrowingById(borrowId);
            
            if (!borrowingOpt.isPresent()) {
                AlertUtil.showInformation("Not Found", "No borrowing found with ID: " + borrowId);
                return;
            }
            
            Borrowing borrowing = borrowingOpt.get();
            
            // Check if already returned
            if (borrowing.getNgayTraThucTe() != null) {
                AlertUtil.showInformation("Already Returned", 
                    "This borrowing has already been returned on " + 
                    borrowing.getNgayTraThucTe().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return;
            }
            
            // Create a BorrowingDetail object to display in the table
            BorrowingDetail detail = new BorrowingDetail();
            detail.setMaMuonTra(borrowing.getMaMuonTra());
            detail.setMaSach(borrowing.getMaSach());
            detail.setTenSach(borrowing.getTenSach());
            detail.setMaDocGia(borrowing.getMaDocGia());
            detail.setTenDocGia(borrowing.getTenDocGia());
            detail.setNgayMuon(borrowing.getNgayMuon());
            detail.setNgayHenTra(borrowing.getNgayHenTra());
            detail.setTrangThai(borrowing.getTrangThai());
            
            // Set the current borrowing
            currentBorrowing = borrowing;
            
            // Populate the table with details
            tblBorrowingDetails.setItems(FXCollections.observableArrayList(Collections.singletonList(detail)));
            
            // Set return date to today by default
            dpReturnDate.setValue(LocalDate.now());
            
            // Calculate fine if any
            calculateFine();
            
            // Update status
            lblStatus.setText(borrowing.getTrangThai());
            
        } catch (NumberFormatException e) {
            AlertUtil.showError("Error", "Invalid borrowing ID format. Please enter a numeric ID.");
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to retrieve borrowing details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void calculateFine() {
        if (currentBorrowing == null || dpReturnDate.getValue() == null) {
            return;
        }
        
        LocalDate dueDate = currentBorrowing.getNgayHenTra();
        LocalDate returnDate = dpReturnDate.getValue();
        
        // Reset fine amount
        fineAmount = 0.0;
        
        // Calculate days overdue
        if (returnDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            
            // Calculate fine (e.g., $1 per day late)
            fineAmount = daysLate * 1.0; // $1 per day
            
            lblStatus.setText("Overdue by " + daysLate + " days");
            lblFine.setText(String.format("Fine: $%.2f", fineAmount));
        } else {
            lblStatus.setText("On time");
            lblFine.setText("No fine");
        }
    }
    
    @FXML
    private void handleReturn(ActionEvent event) {
        if (currentBorrowing == null) {
            return;
        }
        
        // Confirm return
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Return");
        confirmAlert.setHeaderText("Confirm Book Return");
        
        if (fineAmount > 0) {
            confirmAlert.setContentText("This return is overdue and has a fine of $" + 
                    String.format("%.2f", fineAmount) + ". Proceed with return?");
        } else {
            confirmAlert.setContentText("Confirm return of this book?");
        }
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Process the return
                LocalDate returnDate = dpReturnDate.getValue();
                
                // Update borrowing with actual return date
                currentBorrowing.setNgayTraThucTe(returnDate);
                currentBorrowing.setTrangThai("DA_TRA"); // Set status to returned
                borrowingService.updateBorrowing(currentBorrowing);
                
                // Update book status to available
                Book book = bookService.getBookById(currentBorrowing.getMaSach()).orElse(null);
                if (book != null) {
                    // Use "AVAILABLE" instead of "Available" to match database enum values
                    book.setTrangThai("AVAILABLE");
                    bookService.updateBook(book);
                }
                
                // Create fine if necessary
                if (fineAmount > 0) {
                    Fine fine = new Fine();
                    fine.setMaMuonTra(currentBorrowing.getMaMuonTra());
                    fine.setNgayPhat(LocalDate.now());
                    fine.setSoTienPhat(fineAmount);
                    fine.setLyDo("Overdue return");
                    
                    fineService.createFine(fine);
                }
                
                // Log the activity
                activityLogService.logActivity(
                        "Return", 
                        "Book '" + currentBorrowing.getTenSach() + "' returned by " + 
                        currentBorrowing.getTenDocGia());
                
                // Show success message
                showAlert(AlertType.INFORMATION, "Success", "Book returned successfully" + 
                        (fineAmount > 0 ? ". Fine of $" + String.format("%.2f", fineAmount) + " has been recorded." : "."));
                
                // Reload the active borrowings list
                loadActiveBorrowings();
                
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Error processing return: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        clearForm();
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        ViewManager.getInstance().switchToLibrarianView();
    }
    
    private void clearForm() {
        txtBorrowId.clear();
        tblBorrowingDetails.getItems().clear();
        dpReturnDate.setValue(LocalDate.now());
        lblStatus.setText("");
        lblFine.setText("");
        btnReturn.setDisable(true);
        currentBorrowing = null;
        fineAmount = 0.0;
    }
    
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}