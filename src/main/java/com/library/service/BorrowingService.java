package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.model.Borrowing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BorrowingService {
    private static BorrowingService instance;
    private final BorrowingDAO borrowingDAO;
    
    private BorrowingService() {
        borrowingDAO = BorrowingDAO.getInstance();
    }
    
    public static BorrowingService getInstance() {
        if (instance == null) {
            instance = new BorrowingService();
        }
        return instance;
    }
    
    public List<Borrowing> getAllBorrowings() {
        return borrowingDAO.findAll();
    }
    
    public Optional<Borrowing> getBorrowingById(String id) {
        return borrowingDAO.findById(id);
    }
    
    public List<Borrowing> getBorrowingsByReaderId(String readerId) {
        return borrowingDAO.findByReaderId(readerId);
    }
    
    public List<Borrowing> getActiveBorrowings() {
        return getAllBorrowings().stream()
                .filter(b -> b.getReturnDate() == null)
                .collect(Collectors.toList());
    }
    
    public List<Borrowing> getOverdueBorrowings() {
        LocalDate today = LocalDate.now();
        return getAllBorrowings().stream()
                .filter(b -> b.getReturnDate() == null && b.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }
    
    public int getActiveBorrowingsCount() {
        return getActiveBorrowings().size();
    }
    
    public int getOverdueBorrowingsCount() {
        return getOverdueBorrowings().size();
    }
    
    public boolean saveBorrowing(Borrowing borrowing) {
        return borrowingDAO.save(borrowing);
    }
    
    public boolean updateBorrowing(Borrowing borrowing) {
        return borrowingDAO.update(borrowing);
    }
    
    public boolean deleteBorrowing(String id) {
        return borrowingDAO.delete(id);
    }
    
    public boolean returnBook(String borrowingId, LocalDate returnDate) {
        Optional<Borrowing> borrowingOpt = getBorrowingById(borrowingId);
        if (borrowingOpt.isPresent()) {
            Borrowing borrowing = borrowingOpt.get();
            borrowing.setReturnDate(returnDate);
            return updateBorrowing(borrowing);
        }
        return false;
    }
    
    public List<String> getRecentActivities(int limit) {
        // This would normally fetch from a log or activity table
        // For now, we'll return mock data
        List<String> activities = new ArrayList<>();
        activities.add("Book 'Java Programming' borrowed by John Doe - " + LocalDateTime.now().minusHours(2));
        activities.add("Book 'Database Design' returned by Jane Smith - " + LocalDateTime.now().minusHours(5));
        activities.add("New book 'Clean Code' added to inventory - " + LocalDateTime.now().minusDays(1));
        activities.add("User account created for 'newuser' - " + LocalDateTime.now().minusDays(2));
        activities.add("System configuration updated - " + LocalDateTime.now().minusDays(3));
        
        return activities.stream().limit(limit).collect(Collectors.toList());
    }
}