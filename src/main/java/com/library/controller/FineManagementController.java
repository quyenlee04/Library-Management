package com.library.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.library.model.Fine;
import com.library.service.FineService;
import com.library.util.AlertUtil;

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
    private TableView<Fine> fineTable;
    
    @FXML
    private TableColumn<Fine, String> colMaPhiPhat;
    
    @FXML
    private TableColumn<Fine, String> colTenDocGia;
    
    @FXML
    private TableColumn<Fine, String> colMaPhieuMuon;
    
    @FXML
    private TableColumn<Fine, Double> colSoTien;
    
    @FXML
    private TableColumn<Fine, String> colLyDo;
    
    @FXML
    private TableColumn<Fine, String> colNgayPhat;
    
    @FXML
    private TableColumn<Fine, String> colTrangThai;
    
    @FXML
    private TableColumn<Fine, String> colNgayTra;
    
    @FXML
    private TextField txtSearch;
    
    @FXML
    private Button btnSearch;
    
    @FXML
    private Button btnPayFine;
    
    @FXML
    private Button btnRefresh;
    
    @FXML
    private Label lblTotalUnpaid;
    
    private FineService fineService;
    private ObservableList<Fine> fineList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fineService = FineService.getInstance();
        
        // Initialize table columns
        colMaPhiPhat.setCellValueFactory(new PropertyValueFactory<>("maPhiPhat"));
        colTenDocGia.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colMaPhieuMuon.setCellValueFactory(new PropertyValueFactory<>("maPhieuMuon"));
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("soTien"));
        colLyDo.setCellValueFactory(new PropertyValueFactory<>("lyDo"));
        
        colNgayPhat.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getNgayPhat();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        
        colTrangThai.setCellValueFactory(cellData -> {
            boolean daTra = cellData.getValue().isDaTra();
            return new SimpleStringProperty(daTra ? "Đã trả" : "Chưa trả");
        });
        
        colNgayTra.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getNgayPhat();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        
        // Load data
        loadFines();
        
        // Set button actions
        btnRefresh.setOnAction(event -> loadFines());
        btnPayFine.setOnAction(this::handlePayFine);
        btnSearch.setOnAction(this::handleSearch);
    }
    
    private void loadFines() {
        List<Fine> fines = fineService.getAllFines();
        fineList = FXCollections.observableArrayList(fines);
        fineTable.setItems(fineList);
        
        // Update total unpaid amount
        updateTotalUnpaid();
    }
    
    private void updateTotalUnpaid() {
        double totalUnpaid = fineService.getTotalUnpaidFines();
        lblTotalUnpaid.setText(String.format("Tổng tiền phạt chưa thu: %.0f VND", totalUnpaid));
    }
    
    @FXML
    private void handlePayFine(ActionEvent event) {
        Fine selectedFine = fineTable.getSelectionModel().getSelectedItem();
        
        if (selectedFine == null) {
            AlertUtil.showError("Lỗi", "Vui lòng chọn một phiếu phạt để thanh toán");
            return;
        }
        
        if (selectedFine.isDaTra()) {
            AlertUtil.showError("Lỗi", "Phiếu phạt này đã được thanh toán");
            return;
        }
        
        boolean success = fineService.payFine(selectedFine.getMaPhieuPhat());
        
        if (success) {
            AlertUtil.showInformation("Thành công", "Đã thanh toán phiếu phạt thành công");
            loadFines();
        } else {
            AlertUtil.showError("Lỗi", "Không thể thanh toán phiếu phạt");
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = txtSearch.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            fineTable.setItems(fineList);
            return;
        }
        
        ObservableList<Fine> filteredList = FXCollections.observableArrayList();
        
        for (Fine fine : fineList) {
            if (fine.getMaPhieuPhat().toLowerCase().contains(searchText) ||
                fine.getMaMuonTra().toLowerCase().contains(searchText) ||
                fine.getTenDocGia().toLowerCase().contains(searchText) ||
                fine.getLyDo().toLowerCase().contains(searchText)) {
                filteredList.add(fine);
            }
        }
        
        fineTable.setItems(filteredList);
    }
}