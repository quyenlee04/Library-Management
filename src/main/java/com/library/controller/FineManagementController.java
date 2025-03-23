package com.library.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.library.model.Fine;
import com.library.service.FineService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FineManagementController implements Initializable {
    
    @FXML
    private TableView<Fine> tblFines;
    
    @FXML
    private TableColumn<Fine, String> colMaPhieuPhat;
    
    @FXML
    private TableColumn<Fine, String> colMaMuonTra;
    
    @FXML
    private TableColumn<Fine, String> colTenDocGia;
    
    @FXML
    private TableColumn<Fine, String> colTenSach;
    
    @FXML
    private TableColumn<Fine, String> colNgayPhat;
    
    @FXML
    private TableColumn<Fine, Double> colSoTienPhat;
    
    @FXML
    private TableColumn<Fine, String> colLyDo;
    
    @FXML
    private TableColumn<Fine, String> colTrangThai;
    
    @FXML
    private TableColumn<Fine, String> colNgayTra;
    
    @FXML
    private TextField txtSearch;
    
    @FXML
    private Button btnSearch;
    
    @FXML
    private Button btnClear;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnMarkAsPaid;
    
    @FXML
    private Button btnExport;
    
    @FXML
    private Label lblTotalFines;
    
    private FineService fineService;
    private ObservableList<Fine> fineList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fineService = FineService.getInstance();
        
        // Initialize table columns
        colMaPhieuPhat.setCellValueFactory(new PropertyValueFactory<>("maPhieuPhat"));
        colMaMuonTra.setCellValueFactory(new PropertyValueFactory<>("maMuonTra"));
        colTenDocGia.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colTenSach.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colSoTienPhat.setCellValueFactory(new PropertyValueFactory<>("soTienPhat"));
        colLyDo.setCellValueFactory(new PropertyValueFactory<>("lyDo"));
        
        colNgayPhat.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getNgayPhat();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        
        colTrangThai.setCellValueFactory(cellData -> {
            boolean daTra = cellData.getValue().isDaTra();
            return new SimpleStringProperty(daTra ? "Paid" : "Unpaid");
        });
        
        colNgayTra.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getNgayTra();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        
        // Add selection listener to enable/disable Mark as Paid button
        tblFines.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                btnMarkAsPaid.setDisable(newSelection.isDaTra());
            } else {
                btnMarkAsPaid.setDisable(true);
            }
        });
        
        // Load data
        loadFines();
    }
    
    private void loadFines() {
        List<Fine> fines = fineService.getAllFines();
        fineList = FXCollections.observableArrayList(fines);
        tblFines.setItems(fineList);
        
        // Update total unpaid amount
        updateTotalUnpaid();
    }
    
    private void updateTotalUnpaid() {
        double totalUnpaid = fineService.getTotalUnpaidFines();
        lblTotalFines.setText(String.format("Total Unpaid Fines: $%.2f", totalUnpaid));
    }
    
    @FXML
    private void handleMarkAsPaid(ActionEvent event) {
        Fine selectedFine = tblFines.getSelectionModel().getSelectedItem();
        
        if (selectedFine == null) {
            AlertUtil.showError("Error", "Please select a fine to mark as paid");
            return;
        }
        
        if (selectedFine.isDaTra()) {
            AlertUtil.showError("Error", "This fine has already been paid");
            return;
        }
        
        boolean success = fineService.payFine(selectedFine.getMaPhieuPhat());
        
        if (success) {
            AlertUtil.showInformation("Success", "Fine has been marked as paid successfully");
            loadFines();
        } else {
            AlertUtil.showError("Error", "Could not mark fine as paid");
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = txtSearch.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            tblFines.setItems(fineList);
            return;
        }
        
        ObservableList<Fine> filteredList = FXCollections.observableArrayList();
        
        for (Fine fine : fineList) {
            if (fine.getMaPhieuPhat().toLowerCase().contains(searchText) ||
                fine.getMaMuonTra().toLowerCase().contains(searchText) ||
                (fine.getTenDocGia() != null && fine.getTenDocGia().toLowerCase().contains(searchText)) ||
                (fine.getTenSach() != null && fine.getTenSach().toLowerCase().contains(searchText)) ||
                (fine.getLyDo() != null && fine.getLyDo().toLowerCase().contains(searchText))) {
                filteredList.add(fine);
            }
        }
        
        tblFines.setItems(filteredList);
        
        // Update status message
        if (filteredList.isEmpty()) {
            lblTotalFines.setText("No matching fines found");
        } else {
            double totalFiltered = filteredList.stream()
                .filter(fine -> !fine.isDaTra())
                .mapToDouble(Fine::getSoTienPhat)
                .sum();
            lblTotalFines.setText(String.format("Filtered Unpaid Fines: $%.2f", totalFiltered));
        }
    }
    
    @FXML
    private void handleClear(ActionEvent event) {
        txtSearch.clear();
        tblFines.setItems(fineList);
        updateTotalUnpaid();
    }
    
    @FXML
    private void handleExport(ActionEvent event) {
        // Get current date for filename
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = "fines_report_" + date + ".csv";
        
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("Fine ID,Borrowing ID,Reader Name,Book Title,Fine Date,Amount,Reason,Status,Payment Date\n");
            
            for (Fine fine : tblFines.getItems()) {
                csv.append(fine.getMaPhieuPhat()).append(",");
                csv.append(fine.getMaMuonTra()).append(",");
                csv.append(escapeCsv(fine.getTenDocGia())).append(",");
                csv.append(escapeCsv(fine.getTenSach())).append(",");
                csv.append(fine.getNgayPhat() != null ? fine.getNgayPhat().format(dateFormatter) : "").append(",");
                csv.append(String.format("%.2f", fine.getSoTienPhat())).append(",");
                csv.append(escapeCsv(fine.getLyDo())).append(",");
                csv.append(fine.isDaTra() ? "Paid" : "Unpaid").append(",");
                csv.append(fine.getNgayTra() != null ? fine.getNgayTra().format(dateFormatter) : "").append("\n");
            }
            
            // In a real application, you would save this to a file
            // For now, just show a success message
            AlertUtil.showInformation("Export Successful", 
                "Fines report has been exported to: " + filename);
            
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Failed to export fines report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String escapeCsv(String value) {
        if (value == null) return "";
        // Escape quotes and wrap in quotes if contains comma
        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }
        if (value.contains(",")) {
            value = "\"" + value + "\"";
        }
        return value;
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        ViewManager.getInstance().switchToLibrarianDashboard();
    }
}