package com.library.controller;

import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.Reader;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProcessBorrowingController implements Initializable {

    @FXML private TextField txtReaderSearch;
    @FXML private TableView<Reader> tblReaders;
    @FXML private TableColumn<Reader, String> colReaderId;
    @FXML private TableColumn<Reader, String> colReaderName;
    @FXML private TableColumn<Reader, String> colReaderPhone;
    @FXML private TableColumn<Reader, String> colReaderEmail;
    
    @FXML private TextField txtBookSearch;
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, String> colBookId;
    @FXML private TableColumn<Book, String> colBookTitle;
    @FXML private TableColumn<Book, String> colBookAuthor;
    @FXML private TableColumn<Book, String> colBookStatus;
    @FXML private TableColumn<Book, Void> colBookAction;
    
    @FXML private TableView<Book> tblSelectedBooks;
    @FXML private TableColumn<Book, String> colSelectedBookId;
    @FXML private TableColumn<Book, String> colSelectedBookTitle;
    @FXML private TableColumn<Book, String> colSelectedBookAuthor;
    @FXML private TableColumn<Book, Void> colSelectedBookAction;
    
    @FXML private DatePicker dpBorrowDate;
    @FXML private DatePicker dpDueDate;
    
    private ReaderService readerService;
    private BookService bookService;
    private BorrowingService borrowingService;
    
    private ObservableList<Book> selectedBooks = FXCollections.observableArrayList();
    private Reader selectedReader;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readerService = ReaderService.getInstance();
        bookService = BookService.getInstance();
        borrowingService = BorrowingService.getInstance();
        
        // Initialize reader table columns
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("maDocGia"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("tenDocGia"));
        colReaderPhone.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colReaderEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Initialize book table columns
        colBookId.setCellValueFactory(new PropertyValueFactory<>("maSach"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colBookAuthor.setCellValueFactory(new PropertyValueFactory<>("tacGia"));
        colBookStatus.setCellValueFactory(cellData -> {
            boolean available = cellData.getValue().isAvailable();
            return new SimpleStringProperty(available ? "Available" : "Borrowed");
        });
        
        // Add button to add book to selected books
        colBookAction.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Add");
            
            {
                addButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    if (book.isAvailable()) {
                        addToSelectedBooks(book);
                    } else {
                        AlertUtil.showError("Error", "This book is already borrowed.");
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    addButton.setDisable(!book.isAvailable() || selectedBooks.contains(book));
                    setGraphic(addButton);
                }
            }
        });
        
        // Initialize selected books table
        colSelectedBookId.setCellValueFactory(new PropertyValueFactory<>("maSach"));
        colSelectedBookTitle.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colSelectedBookAuthor.setCellValueFactory(new PropertyValueFactory<>("tacGia"));
        
        // Add button to remove book from selected books
        colSelectedBookAction.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            
            {
                removeButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    removeFromSelectedBooks(book);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });
        
        tblSelectedBooks.setItems(selectedBooks);
        
        // Set up date pickers
        dpBorrowDate.setValue(LocalDate.now());
        dpDueDate.setValue(LocalDate.now().plusDays(14)); // Default loan period is 14 days
        
        // Add listener to reader table selection
        tblReaders.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReader = newSelection;
            }
        });
        
        // Load initial data
        loadBooks();
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
    
    private void loadBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            tblBooks.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to load books: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearchReader(ActionEvent event) {
        String searchText = txtReaderSearch.getText().trim();
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
    private void handleSearchBook(ActionEvent event) {
        String searchText = txtBookSearch.getText().trim();
        try {
            List<Book> books;
            if (searchText.isEmpty()) {
                books = bookService.getAllBooks();
            } else {
                books = bookService.searchBooks(searchText);
            }
            tblBooks.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to search books: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addToSelectedBooks(Book book) {
        if (!selectedBooks.contains(book)) {
            selectedBooks.add(book);
        }
    }
    
    private void removeFromSelectedBooks(Book book) {
        selectedBooks.remove(book);
    }
    
    @FXML
    private void handleClear(ActionEvent event) {
        selectedBooks.clear();
        selectedReader = null;
        tblReaders.getSelectionModel().clearSelection();
        dpBorrowDate.setValue(LocalDate.now());
        dpDueDate.setValue(LocalDate.now().plusDays(14));
        txtReaderSearch.clear();
        txtBookSearch.clear();
    }
    
    @FXML
    private void handleProcessBorrowing(ActionEvent event) {
        // Validate input
        if (selectedReader == null) {
            AlertUtil.showError("Error", "Please select a reader");
            return;
        }
        
        if (selectedBooks.isEmpty()) {
            AlertUtil.showError("Error", "Please select at least one book");
            return;
        }
        
        LocalDate borrowDate = dpBorrowDate.getValue();
        LocalDate dueDate = dpDueDate.getValue();
        
        if (borrowDate == null) {
            AlertUtil.showError("Error", "Please select a borrow date");
            return;
        }
        
        if (dueDate == null) {
            AlertUtil.showError("Error", "Please select a due date");
            return;
        }
        
        if (dueDate.isBefore(borrowDate)) {
            AlertUtil.showError("Error", "Due date cannot be before borrow date");
            return;
        }
        
        try {
            // Create borrowing records for each book
            List<Borrowing> borrowings = new ArrayList<>();
            
            for (Book book : selectedBooks) {
                Borrowing borrowing = new Borrowing();
                borrowing.setMaDocGia(selectedReader.getMaDocGia());
                borrowing.setMaSach(book.getMaSach());
                borrowing.setNgayMuon(borrowDate);
                borrowing.setNgayHenTra(dueDate);
                borrowing.setTrangThai("Borrowed");
                
                borrowings.add(borrowing);
            }
            
            // Save all borrowings
            boolean success = borrowingService.addBorrowings(borrowings);
            
            if (success) {
                // Update book status
                for (Book book : selectedBooks) {
                    book.setAvailable(false);
                    bookService.updateBook(book);
                }
                
                AlertUtil.showInformation("Success", "Borrowing processed successfully");
                handleClear(null);
                loadBooks(); // Refresh book list to show updated availability
            } else {
                AlertUtil.showError("Error", "Failed to process borrowing");
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to process borrowing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        ViewManager.getInstance().switchToLibrarianDashboard();
    }
}