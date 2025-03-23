package com.library.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.library.model.Reader;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReaderManagementController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private TableView<Reader> tblReaders;
    @FXML private TableColumn<Reader, String> colId;
    @FXML private TableColumn<Reader, String> colName;
    @FXML private TableColumn<Reader, String> colPhone;
    @FXML private TableColumn<Reader, String> colEmail;
    @FXML private TableColumn<Reader, String> colStatus;
    @FXML private TableColumn<Reader, Void> colActions;
    
    private ReaderService readerService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readerService = ReaderService.getInstance();
        
        // Initialize table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("maDocGia"));
        colName.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Since we don't have a trangThai column in the database, we'll use a computed value
        // or just display the account status if available
        colStatus.setCellValueFactory(cellData -> {
            String taiKhoanID = cellData.getValue().getTaiKhoanID();
            return new SimpleStringProperty(taiKhoanID != null && !taiKhoanID.isEmpty() ? "Active" : "No Account");
        });
        
        // Configure action column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            
            {
                editBtn.setOnAction(event -> {
                    Reader reader = getTableView().getItems().get(getIndex());
                    handleEditReader(reader);
                });
                
                deleteBtn.setOnAction(event -> {
                    Reader reader = getTableView().getItems().get(getIndex());
                    handleDeleteReader(reader);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });
        
        // Load readers
        loadReaders();
    }
    
    private void loadReaders() {
        try {
            List<Reader> readers = readerService.getAllReaders();
            tblReaders.setItems(FXCollections.observableArrayList(readers));
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to load readers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = txtSearch.getText().trim();
        try {
            List<Reader> readers;
            if (searchText.isEmpty()) {
                readers = readerService.getAllReaders();
            } else {
                readers = readerService.searchReaders(searchText);
            }
            tblReaders.setItems(FXCollections.observableArrayList(readers));
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to search readers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAddReader(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reader_dialog.fxml"));
            Parent root = loader.load();
            
            ReaderDialogController controller = loader.getController();
            controller.setMode("add");
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Reader");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(txtSearch.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isConfirmed()) {
                loadReaders();
                AlertUtil.showInformation("Success", "Reader added successfully");
            }
        } catch (IOException e) {
            AlertUtil.showError("Error", "Failed to open add reader dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleEditReader(Reader reader) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reader_dialog.fxml"));
            Parent root = loader.load();
            
            ReaderDialogController controller = loader.getController();
            controller.setMode("edit");
            controller.setReader(reader);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Reader");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(txtSearch.getScene().getWindow());
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isConfirmed()) {
                loadReaders();
                AlertUtil.showInformation("Success", "Reader updated successfully");
            }
        } catch (IOException e) {
            AlertUtil.showError("Error", "Failed to open edit reader dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteReader(Reader reader) {
        boolean confirmed = AlertUtil.showConfirmation("Confirm Delete", 
                "Are you sure you want to delete reader: " + reader.getTenDocGia() + "?");
        
        if (confirmed) {
            try {
                boolean success = readerService.deleteReader(reader.getMaDocGia());
                if (success) {
                    AlertUtil.showInformation("Success", "Reader deleted successfully");
                    loadReaders();
                } else {
                    AlertUtil.showError("Error", "Failed to delete reader");
                }
            } catch (Exception e) {
                AlertUtil.showError("Error", "Failed to delete reader: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadReaders();
        AlertUtil.showInformation("Success", "Reader list refreshed");
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        ViewManager.getInstance().switchToLibrarianDashboard();
    }
}