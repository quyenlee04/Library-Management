package com.library.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class BookManagementController implements Initializable {

    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> searchTypeComboBox;
    
    @FXML
    private TableView<Book> bookTable;
    
    @FXML
    private TableColumn<Book, String> idColumn;
    
    @FXML
    private TableColumn<Book, String> titleColumn;
    
    @FXML
    private TableColumn<Book, String> authorColumn;
    
    // These columns were removed from the FXML
    // @FXML
    // private TableColumn<Book, String> isbnColumn;
    // @FXML
    // private TableColumn<Book, String> publisherColumn;
    
    @FXML
    private TableColumn<Book, String> categoryColumn;
    
    @FXML
    private TableColumn<Book, Integer> yearColumn;
    
    // This was renamed from availabilityColumn to statusColumn
    @FXML
    private TableColumn<Book, String> statusColumn;
    
    // New columns added to the FXML
    @FXML
    private TableColumn<Book, Integer> quantityColumn;
    
    @FXML
    private TableColumn<Book, Integer> availableQuantityColumn;
    
    @FXML
    private TableColumn<Book, Void> actionsColumn;
    
    @FXML
    private Label statusLabel;
    
    private BookService bookService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookService = BookService.getInstance();
        
        // Initialize search type combo box
        searchTypeComboBox.setItems(FXCollections.observableArrayList(
                "All", "Title", "Author" , "Category"));
        searchTypeComboBox.setValue("All");
        
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        // Remove these lines that are causing the NullPointerException
        // isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        // publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        
        // This was renamed from availabilityColumn to statusColumn
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        
        // New columns added to the FXML
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        availableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("soLuongKhaDung"));
        
        // Configure actions column with edit and delete buttons
        actionsColumn.setCellFactory(col -> new TableCell<Book, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    
                    Button editButton = new Button("Edit");
                    editButton.getStyleClass().add("button-secondary");
                    editButton.setOnAction(event -> handleEditBook(book));
                    
                    Button deleteButton = new Button("Delete");
                    deleteButton.getStyleClass().add("button-danger");
                    deleteButton.setOnAction(event -> handleDeleteBook(book));
                    
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
        
        // Load books
        loadBooks();
    }
    
    /**
     * Handles the action when the "Back to Dashboard" button is clicked.
     * Navigates back to the appropriate dashboard based on user role.
     */
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        ViewManager.getInstance().switchToLibrarianDashboard();
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        String searchType = searchTypeComboBox.getValue();
        
        List<Book> books;
        
        if (searchTerm.isEmpty()) {
            books = bookService.getAllBooks();
        } else {
            switch (searchType) {
                case "Title":
                    books = bookService.findByTitle(searchTerm);
                    break;
                case "Author":
                    books = bookService.findByAuthor(searchTerm);
                    break;
                case "Category":
                    books = bookService.findByCategory(searchTerm);
                    break;
                default:
                    books = bookService.searchBooks(searchTerm);
                    break;
            }
        }
        
        bookTable.setItems(FXCollections.observableArrayList(books));
        statusLabel.setText("Found " + books.size() + " books");
    }
    
    @FXML
    private void handleAddBook(ActionEvent event) {
        BookDialogController.showAddDialog().ifPresent(book -> {
            if (bookService.saveBook(book)) {
                AlertUtil.showInformation("Success", "Book added successfully");
                loadBooks();
            } else {
                AlertUtil.showError("Error", "Failed to add book");
            }
        });
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadBooks();
        statusLabel.setText("Book list refreshed");
    }
    
    private void handleEditBook(Book book) {
        BookDialogController.showEditDialog(book).ifPresent(updatedBook -> {
            if (bookService.updateBook(updatedBook)) {
                AlertUtil.showInformation("Success", "Book updated successfully");
                loadBooks();
            } else {
                AlertUtil.showError("Error", "Failed to update book");
            }
        });
    }
    
    private void handleDeleteBook(Book book) {
        if (AlertUtil.showConfirmation("Confirm Delete", 
                "Are you sure you want to delete this book: " + book.getTitle() + "?")) {
            if (bookService.deleteBook(book.getId())) {
                AlertUtil.showInformation("Success", "Book deleted successfully");
                loadBooks();
            } else {
                AlertUtil.showError("Error", "Failed to delete book");
            }
        }
    }
    
    private void loadBooks() {
        List<Book> books = bookService.getAllBooks();
        bookTable.setItems(FXCollections.observableArrayList(books));
        statusLabel.setText("Total books: " + books.size());
    }
}