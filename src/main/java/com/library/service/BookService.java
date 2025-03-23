package com.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.library.dao.BookDAO;
import com.library.model.Book;

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
    
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }
        
        String searchTerm = query.toLowerCase();
        return getAllBooks().stream()
                .filter(book -> 
                    book.getTenSach().toLowerCase().contains(searchTerm) ||
                    book.getTacGia().toLowerCase().contains(searchTerm) ||
                    book.getTheLoai().toLowerCase().contains(searchTerm) ||
                    book.getMoTa() != null && book.getMoTa().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    public boolean saveBook(Book book) {
        // If maSach is null or empty, let the database auto-increment it
        // by setting it to null or 0 depending on your database configuration
        if (book.getMaSach() == null || book.getMaSach().isEmpty()) {
            book.setMaSach(null); // Let database auto-increment
        } else {
            // If a value is provided, make sure it's a valid integer
            try {
                Integer.parseInt(book.getMaSach());
            } catch (NumberFormatException e) {
                // If not a valid integer, set to null to let database auto-increment
                book.setMaSach(null);
            }
        }
        return bookDAO.save(book);
    }
    
    public boolean updateBook(Book book) {
        return bookDAO.update(book);
    }
    
    public boolean deleteBook(String id) {
        return bookDAO.delete(id);
    }
    
    /**
     * Gets the total number of books in the system
     * @return The total number of books
     */
    public int getTotalBooks() {
        return bookDAO.findAll().size();
    }
    
    public int getAvailableBooks() {
        return (int) getAllBooks().stream()
                .filter(book -> "CHO_MUON".equals(book.getTrangThai()))
                .count();
    }
    
    public List<Book> getBooksByCategory(String category) {
        return getAllBooks().stream()
                .filter(book -> book.getTheLoai().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    
    public List<String> getAllCategories() {
        return getAllBooks().stream()
                .map(Book::getTheLoai)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    public boolean isBookAvailable(String bookId) {
        Optional<Book> bookOpt = getBookById(bookId);
        return bookOpt.isPresent() && "CHO_MUON".equals(bookOpt.get().getTrangThai());
    }
    
    public boolean updateBookAvailability(String bookId, boolean available) {
        Optional<Book> bookOpt = getBookById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setTrangThai(available ? "CHO_MUON" : "DA_MUON");
            return updateBook(book);
        }
        return false;
    }
    
    public List<Book> getRecentlyAddedBooks(int limit) {
        return getAllBooks().stream()
                .sorted((b1, b2) -> b2.getAddedDate().compareTo(b1.getAddedDate()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public List<Book> getPopularBooks(int limit) {
        // This would normally be based on borrowing statistics
        // For now, we'll just return some books
        return getAllBooks().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    // Add these methods to your BookService class
    
    public List<Book> findByTitle(String title) {
        return bookDAO.findByTitle(title);
    }
    
    public List<Book> findByAuthor(String author) {
        return bookDAO.findByAuthor(author);
    }
    
    // This method needs to be updated or removed since ISBN isn't in your database
    // You can either remove it or implement a different search
    public List<Book> findByIsbn(String isbn) {
        // Since ISBN isn't in your database, this method should be removed
        // or changed to search by a different field
        return new ArrayList<>(); // Return empty list for now
    }
    
    public List<Book> findByCategory(String category) {
        return bookDAO.findByCategory(category);
    }
}
