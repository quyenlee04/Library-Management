package com.library.service;

import com.library.dao.BookDAO;
import com.library.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookService {
    private static BookService instance;
    private BookDAO bookDAO;
    
    private BookService() {
        bookDAO = BookDAO.getInstance();
    }
    
    public static BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }
    
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }
    
    public Optional<Book> getBookById(String id) {
        return bookDAO.findById(id);
    }
    
    public boolean addBook(Book book) {
        return bookDAO.save(book);
    }
    
    public boolean updateBook(Book book) {
        return bookDAO.update(book);
    }
    
    public boolean deleteBook(String id) {
        return bookDAO.delete(id);
    }
    
    public int getTotalBooks() {
        return bookDAO.findAll().size();
    }
    
    // Các phương thức tìm kiếm sách
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        return getAllBooks().stream()
                .filter(book -> 
                    book.getTenSach().toLowerCase().contains(searchTerm) ||
                    book.getTacGia().toLowerCase().contains(searchTerm) ||
                    book.getTheLoai().toLowerCase().contains(searchTerm) ||
                    (book.getMoTa() != null && book.getMoTa().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }
    
    public List<Book> findBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = title.toLowerCase().trim();
        return getAllBooks().stream()
                .filter(book -> book.getTenSach().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    public List<Book> findBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = author.toLowerCase().trim();
        return getAllBooks().stream()
                .filter(book -> book.getTacGia().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    public List<Book> findBooksByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = category.toLowerCase().trim();
        return getAllBooks().stream()
                .filter(book -> book.getTheLoai().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
}
