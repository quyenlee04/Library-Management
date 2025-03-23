package com.library.controller;

import com.library.model.Role;
import com.library.service.RoleService;
import com.library.util.AlertUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RoleManagementController implements Initializable {

    @FXML private TableView<Role> roleTable;
    @FXML private TableColumn<Role, String> roleNameColumn;
    @FXML private TableColumn<Role, String> descriptionColumn;
    @FXML private TableColumn<Role, String> permissionsColumn;
    @FXML private TableColumn<Role, String> actionsColumn;
    
    private RoleService roleService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleService = RoleService.getInstance();
        
        // Initialize table columns
        roleNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        permissionsColumn.setCellValueFactory(data -> {
            List<String> permissions = data.getValue().getPermissions();
            return new SimpleStringProperty(String.join(", ", permissions));
        });
        
        // Configure actions column with edit and delete buttons
        actionsColumn.setCellFactory(col -> new TableCell<Role, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Role role = getTableView().getItems().get(getIndex());
                    
                    Button editButton = new Button("Edit");
                    editButton.setOnAction(event -> handleEditRole(role));
                    
                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(event -> handleDeleteRole(role));
                    
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
        
        // Load roles
        loadRoles();
    }
    
    private void loadRoles() {
        List<Role> roles = roleService.getAllRoles();
        roleTable.setItems(FXCollections.observableArrayList(roles));
    }
    
    @FXML
    private void handleAddRole(ActionEvent event) {
        RoleDialogController.showAddDialog().ifPresent(role -> {
            if (roleService.saveRole(role)) {
                AlertUtil.showInformation("Success", "Role added successfully");
                loadRoles(); // Refresh the table
            } else {
                AlertUtil.showError("Error", "Failed to add role");
            }
        });
    }
    
    private void handleEditRole(Role role) {
        RoleDialogController.showEditDialog(role).ifPresent(updatedRole -> {
            if (roleService.updateRole(updatedRole)) {
                AlertUtil.showInformation("Success", "Role updated successfully");
                loadRoles(); // Refresh the table
            } else {
                AlertUtil.showError("Error", "Failed to update role");
            }
        });
    }
    
    private void handleDeleteRole(Role role) {
        if (AlertUtil.showConfirmation("Confirm Delete", 
                "Are you sure you want to delete role: " + role.getName() + "?")) {
            if (roleService.deleteRole(role.getId())) {
                AlertUtil.showInformation("Success", "Role deleted successfully");
                loadRoles(); // Refresh the table
            } else {
                AlertUtil.showError("Error", "Failed to delete role");
            }
        }
    }
}