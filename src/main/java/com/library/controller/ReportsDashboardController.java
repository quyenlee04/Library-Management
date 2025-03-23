package com.library.controller;

import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Reader;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.service.FineService;
import com.library.service.ReaderService;
import com.library.util.PDFGenerator;
import com.library.view.ViewManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReportsDashboardController implements Initializable {

    @FXML private TabPane tabPane;
    
    // Book inventory tab
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, String> colBookId;
    @FXML private TableColumn<Book, String> colBookTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colYear;
    @FXML private TableColumn<Book, String> colCategory;
    @FXML private TableColumn<Book, String> colStatus;
    @FXML private TableColumn<Book, Integer> colQuantity;
    @FXML private TableColumn<Book, Integer> colAvailable;
    @FXML private PieChart chartBookCategories;
    @FXML private PieChart chartBookStatus;
    
    // Borrowing history tab
    @FXML private TableView<Borrowing> tblBorrowings;
    @FXML private TableColumn<Borrowing, String> colBorrowId;
    @FXML private TableColumn<Borrowing, String> colBorrowBookTitle;
    @FXML private TableColumn<Borrowing, String> colBorrowReader;
    @FXML private TableColumn<Borrowing, LocalDate> colBorrowDate;
    @FXML private TableColumn<Borrowing, LocalDate> colDueDate;
    @FXML private TableColumn<Borrowing, LocalDate> colReturnDate;
    @FXML private TableColumn<Borrowing, String> colBorrowStatus;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private Button btnFilterBorrowings;
    @FXML private BarChart<String, Number> chartBorrowingsByMonth;
    
    // Fines tab
    @FXML private TableView<Fine> tblFines;
    @FXML private TableColumn<Fine, String> colFineId;
    @FXML private TableColumn<Fine, String> colFineBorrowId;
    @FXML private TableColumn<Fine, String> colFineReader;
    @FXML private TableColumn<Fine, Double> colFineAmount;
    @FXML private TableColumn<Fine, String> colFineReason;
    @FXML private TableColumn<Fine, LocalDate> colFineDate;
    @FXML private TableColumn<Fine, Boolean> colFinePaid;
    @FXML private DatePicker dpFineStartDate;
    @FXML private DatePicker dpFineEndDate;
    @FXML private Button btnFilterFines;
    @FXML private Label lblTotalFines;
    @FXML private Label lblPaidFines;
    @FXML private Label lblUnpaidFines;
    @FXML private PieChart chartFineStatus;
    
    // Reader statistics tab
    @FXML private TableView<Reader> tblReaders;
    @FXML private TableColumn<Reader, String> colReaderId;
    @FXML private TableColumn<Reader, String> colReaderName;
    @FXML private TableColumn<Reader, String> colReaderEmail;
    @FXML private TableColumn<Reader, String> colReaderPhone;
    @FXML private TableColumn<Reader, LocalDate> colReaderJoinDate;
    @FXML private BarChart<String, Number> chartReaderActivity;
    
    // Services
    private BookService bookService;
    private BorrowingService borrowingService;
    private ReaderService readerService;
    private FineService fineService;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        bookService = BookService.getInstance();
        borrowingService = BorrowingService.getInstance();
        readerService = ReaderService.getInstance();
        fineService = FineService.getInstance();
        
        // Initialize date pickers with default values (last 30 days)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        dpStartDate.setValue(startDate);
        dpEndDate.setValue(endDate);
        dpFineStartDate.setValue(startDate);
        dpFineEndDate.setValue(endDate);
        
        // Initialize table columns
        initializeBookInventoryTab();
        initializeBorrowingHistoryTab();
        initializeFinesTab();
        initializeReaderStatisticsTab();
        
        // Load initial data
        loadBookInventoryData();
        loadBorrowingHistoryData();
        loadFinesData();
        loadReaderStatisticsData();
    }
    
    private void initializeBookInventoryTab() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("maSach"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("tacGia"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("namXuatBan"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("soLuongKhaDung"));
    }
    
    private void initializeBorrowingHistoryTab() {
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("maMuonTra"));
        colBorrowBookTitle.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colBorrowReader.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("ngayMuon"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("ngayHenTra"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("ngayTraThucTe"));
        colBorrowStatus.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        
        // Format date columns
        formatDateColumn(colBorrowDate);
        formatDateColumn(colDueDate);
        formatDateColumn(colReturnDate);
    }
    
    private void initializeFinesTab() {
        colFineId.setCellValueFactory(new PropertyValueFactory<>("maPhiPhat"));
        colFineBorrowId.setCellValueFactory(new PropertyValueFactory<>("maMuonTra"));
        colFineReader.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colFineAmount.setCellValueFactory(new PropertyValueFactory<>("soTienPhat"));
        colFineReason.setCellValueFactory(new PropertyValueFactory<>("lyDo"));
        colFineDate.setCellValueFactory(new PropertyValueFactory<>("ngayPhat"));
        colFinePaid.setCellValueFactory(new PropertyValueFactory<>("daTra"));
        
        // Format date column
        formatDateColumn(colFineDate);
    }
    
    private void initializeReaderStatisticsTab() {
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("maDocGia"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colReaderEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colReaderPhone.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colReaderJoinDate.setCellValueFactory(new PropertyValueFactory<>("ngayDangKy"));
        
        // Format date column
        formatDateColumn(colReaderJoinDate);
    }
    
    private <T> void formatDateColumn(TableColumn<T, LocalDate> column) {
        column.setCellFactory(col -> new TableCell<T, LocalDate>() {
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
    }
    
    private void loadBookInventoryData() {
        try {
            // Load books table
            List<Book> books = bookService.getAllBooks();
            tblBooks.setItems(FXCollections.observableArrayList(books));
            
            // Load book categories chart
            Map<String, Integer> categoryCounts = new HashMap<>();
            for (Book book : books) {
                String category = book.getTheLoai();
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            }
            
            ObservableList<PieChart.Data> categoryData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                categoryData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            chartBookCategories.setData(categoryData);
            
            // Load book status chart
            Map<String, Integer> statusCounts = new HashMap<>();
            for (Book book : books) {
                String status = book.getTrangThai();
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
            
            ObservableList<PieChart.Data> statusData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                statusData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            chartBookStatus.setData(statusData);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading book inventory data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadBorrowingHistoryData() {
        try {
            LocalDate startDate = dpStartDate.getValue();
            LocalDate endDate = dpEndDate.getValue();
            
            if (startDate == null || endDate == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select valid start and end dates.");
                return;
            }
            
            // Load borrowings table
            List<Borrowing> borrowings = borrowingService.getBorrowingsByDateRange(startDate, endDate);
            tblBorrowings.setItems(FXCollections.observableArrayList(borrowings));
            
            // Load borrowings by month chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Borrowings by Month");
            
            Map<String, Integer> borrowingsByMonth = new HashMap<>();
            for (Borrowing borrowing : borrowings) {
                String month = borrowing.getNgayMuon().getMonth().toString() + " " + borrowing.getNgayMuon().getYear();
                borrowingsByMonth.put(month, borrowingsByMonth.getOrDefault(month, 0) + 1);
            }
            
            for (Map.Entry<String, Integer> entry : borrowingsByMonth.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            chartBorrowingsByMonth.getData().clear();
            chartBorrowingsByMonth.getData().add(series);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading borrowing history data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadFinesData() {
        try {
            LocalDate startDate = dpFineStartDate.getValue();
            LocalDate endDate = dpFineEndDate.getValue();
            
            if (startDate == null || endDate == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select valid start and end dates for fines.");
                return;
            }
            
            // Load fines table
            List<Fine> fines = fineService.getFinesByDateRange(startDate, endDate);
            tblFines.setItems(FXCollections.observableArrayList(fines));
            
            // Calculate totals
            double totalFineAmount = fines.stream().mapToDouble(Fine::getSoTienPhat).sum();
            double paidFineAmount = fines.stream().filter(Fine::isDaTra).mapToDouble(Fine::getSoTienPhat).sum();
            double unpaidFineAmount = totalFineAmount - paidFineAmount;
            
            lblTotalFines.setText(String.format("%.2f", totalFineAmount));
            lblPaidFines.setText(String.format("%.2f", paidFineAmount));
            lblUnpaidFines.setText(String.format("%.2f", unpaidFineAmount));
            
            // Load fine status chart
            ObservableList<PieChart.Data> fineData = FXCollections.observableArrayList(
                new PieChart.Data("Paid", paidFineAmount),
                new PieChart.Data("Unpaid", unpaidFineAmount)
            );
            chartFineStatus.setData(fineData);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading fines data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadReaderStatisticsData() {
        try {
            // Load readers table
            List<Reader> readers = readerService.getAllReaders();
            tblReaders.setItems(FXCollections.observableArrayList(readers));
            
            // Load reader activity chart (borrowings per reader)
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Borrowings per Reader");
            
            Map<String, Integer> readerActivity = borrowingService.getBorrowingsCountByReader();
            
            // Sort by borrowing count (descending) and limit to top 10
            List<Map.Entry<String, Integer>> topReaders = readerActivity.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toList());
            
            for (Map.Entry<String, Integer> entry : topReaders) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            chartReaderActivity.getData().clear();
            chartReaderActivity.getData().add(series);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading reader statistics data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleFilterBorrowings(ActionEvent event) {
        loadBorrowingHistoryData();
    }
    
    @FXML
    private void handleFilterFines(ActionEvent event) {
        loadFinesData();
    }
    
    @FXML
    private void handleExportBookInventory(ActionEvent event) {
        exportReport("book_inventory", tblBooks.getItems());
    }
    
    @FXML
    private void handleExportBorrowingHistory(ActionEvent event) {
        exportReport("borrowing_history", tblBorrowings.getItems());
    }
    
    @FXML
    private void handleExportFines(ActionEvent event) {
        exportReport("fines", tblFines.getItems());
    }
    
    @FXML
    private void handleExportReaderStatistics(ActionEvent event) {
        exportReport("reader_statistics", tblReaders.getItems());
    }
    
    private <T> void exportReport(String reportName, List<T> data) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Export Directory");
            File selectedDirectory = directoryChooser.showDialog(new Stage());
            
            if (selectedDirectory != null) {
                String filePath = selectedDirectory.getAbsolutePath() + File.separator + reportName + "_" + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
                
                PDFGenerator pdfGenerator = new PDFGenerator();
                boolean success = pdfGenerator.generateReport(reportName, data, filePath);
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Report exported successfully to: " + filePath);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to export report.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error exporting report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        ViewManager.getInstance().switchToLibrarianDashboard();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}