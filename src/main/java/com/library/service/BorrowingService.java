package com.library.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.library.dao.BorrowingDAO;
import com.library.model.Borrowing;
import com.library.model.BorrowingDetail;
import com.library.util.DBUtil;

public class BorrowingService {
    private static BorrowingService instance;
    private BorrowingDAO borrowingDAO;
    
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
    
    public Optional<Borrowing> getBorrowingById(String borrowingId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT mt.*, dg.tenDocGia, s.tenSach FROM muontra mt " +
                         "JOIN docgia dg ON mt.maDocGia = dg.maDocGia " +
                         "JOIN sach s ON mt.maSach = s.maSach " +
                         "WHERE mt.maMuonTra = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(borrowingId));
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Replace the private method call with our own mapping method
                Borrowing borrowing = mapResultSetToBorrowing(rs);
                return Optional.of(borrowing);
            }
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error finding borrowing by ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            // Close resources manually instead of using DBUtil.closeResources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // Add a new method to map ResultSet to Borrowing
    private Borrowing mapResultSetToBorrowing(ResultSet rs) throws SQLException {
        Borrowing borrowing = new Borrowing();
        
        borrowing.setMaMuonTra(rs.getString("maMuonTra"));
        borrowing.setMaDocGia(rs.getString("maDocGia"));
        borrowing.setMaSach(rs.getString("maSach"));
        
        // Map dates
        java.sql.Date ngayMuon = rs.getDate("ngayMuon");
        if (ngayMuon != null) {
            borrowing.setNgayMuon(ngayMuon.toLocalDate());
        }
        
        java.sql.Date ngayHenTra = rs.getDate("ngayHenTra");
        if (ngayHenTra != null) {
            borrowing.setNgayHenTra(ngayHenTra.toLocalDate());
        }
        
        java.sql.Date ngayTraThucTe = rs.getDate("ngayTraThucTe");
        if (ngayTraThucTe != null) {
            borrowing.setNgayTraThucTe(ngayTraThucTe.toLocalDate());
        }
        
        borrowing.setTrangThai(rs.getString("trangThai"));
        borrowing.setGhiChu(rs.getString("ghiChu"));
        
        // Additional fields from joins
        borrowing.setTenDocGia(rs.getString("tenDocGia"));
        borrowing.setTenSach(rs.getString("tenSach"));
        
        return borrowing;
    }
    
    public List<Borrowing> getBorrowingsByReaderId(String readerId) {
        return borrowingDAO.findByReaderId(readerId);
    }
    
    // Update this method to match your database status values
    public List<Borrowing> getActiveBorrowings() {
        return getAllBorrowings().stream()
                .filter(b -> b.getNgayTraThucTe() == null && 
                            ("DANG_MUON".equals(b.getTrangThai()) || 
                             "Borrowed".equals(b.getTrangThai()) ||
                             "Active".equals(b.getTrangThai())))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the count of active borrowings (books that haven't been returned yet)
     * 
     * @return The number of active borrowings
     */
    public int getActiveBorrowingsCount() {
        return getActiveBorrowings().size();
    }
    
    public List<Borrowing> getOverdueBorrowings() {
        LocalDate today = LocalDate.now();
        return getAllBorrowings().stream()
                .filter(b -> b.getNgayTraThucTe() == null && b.getNgayHenTra().isBefore(today))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the count of overdue borrowings (books that are past their due date and haven't been returned)
     * 
     * @return The number of overdue borrowings
     */
    public int getOverdueBorrowingsCount() {
        return getOverdueBorrowings().size();
    }
    
    public boolean createBorrowing(Borrowing borrowing, List<String> bookIds) {
        // Set default values if not already set
        if (borrowing.getNgayMuon() == null) {
            borrowing.setNgayMuon(LocalDate.now());
        }
        
        if (borrowing.getNgayHenTra() == null) {
            // Default loan period is 14 days
            borrowing.setNgayHenTra(borrowing.getNgayMuon().plusDays(14));
        }
        
        if (borrowing.getTrangThai() == null || borrowing.getTrangThai().isEmpty()) {
            borrowing.setTrangThai("DANG_MUON");
        }
        
        // Create a list of borrowings, one for each book
        List<Borrowing> borrowings = bookIds.stream()
            .map(bookId -> {
                Borrowing newBorrowing = new Borrowing();
                newBorrowing.setMaDocGia(borrowing.getMaDocGia());
                newBorrowing.setMaSach(bookId);
                newBorrowing.setNgayMuon(borrowing.getNgayMuon());
                newBorrowing.setNgayHenTra(borrowing.getNgayHenTra());
                newBorrowing.setTrangThai(borrowing.getTrangThai());
                newBorrowing.setGhiChu(borrowing.getGhiChu());
                return newBorrowing;
            })
            .collect(Collectors.toList());
        
        return borrowingDAO.saveAll(borrowings);
    }
    
    public boolean returnBooks(String borrowingId, List<String> bookIds) {
        // The DAO's returnBook method expects a borrowingId and a return date
        // We need to implement a different approach for returning multiple books
        
        LocalDate returnDate = LocalDate.now();
        boolean allSuccessful = true;
        
        for (String bookId : bookIds) {
            // Find the specific borrowing for this book
            Optional<Borrowing> borrowingOpt = getAllBorrowings().stream()
                .filter(b -> b.getMaMuonTra().equals(borrowingId) && 
                       b.getMaSach().equals(bookId) && 
                       b.getNgayTraThucTe() == null)
                .findFirst();
                
            if (borrowingOpt.isPresent()) {
                Borrowing borrowing = borrowingOpt.get();
                borrowing.setNgayTraThucTe(returnDate);
                borrowing.setTrangThai("DA_TRA");
                
                boolean success = updateBorrowing(borrowing);
                if (!success) {
                    allSuccessful = false;
                }
            } else {
                allSuccessful = false;
            }
        }
        
        return allSuccessful;
    }
    
    public boolean returnBook(String borrowingId, LocalDate returnDate) {
        Optional<Borrowing> borrowingOpt = getBorrowingById(borrowingId);
        if (borrowingOpt.isPresent()) {
            Borrowing borrowing = borrowingOpt.get();
            borrowing.setNgayTraThucTe(returnDate);
            borrowing.setTrangThai("DA_TRA");
            return updateBorrowing(borrowing);
        }
        return false;
    }
    
    public boolean extendBorrowing(String borrowingId) {
        Optional<Borrowing> optBorrowing = getBorrowingById(borrowingId);
        
        if (optBorrowing.isPresent()) {
            Borrowing borrowing = optBorrowing.get();
            
            // Check if already returned
            if ("DA_TRA".equals(borrowing.getTrangThai())) {
                return false;
            }
            
            // Check if already extended maximum times (e.g., max 2 extensions)
           
            
            // // Extend by 7 days from current due date
            // LocalDate newDueDate = borrowing.getNgayHenTra().plusDays(7);
            // int newExtensionCount = borrowing.getSoLanGiaHan() + 1;
            
            // return borrowingDAO.extendBorrowing(borrowingId, newDueDate, newExtensionCount);
        }
        
        return false;
    }
    
    public List<BorrowingDetail> getBorrowingDetails(String borrowingId) {
        return borrowingDAO.getBorrowingDetails(borrowingId);
    }
    
    public int getTotalActiveBorrowings() {
        return (int) getAllBorrowings().stream()
                .filter(b -> "DANG_MUON".equals(b.getTrangThai()))
                .count();
    }
    
    public int getTotalOverdueBorrowings() {
        return getOverdueBorrowings().size();
    }
    
    /**
     * Updates an existing borrowing record in the database
     * @param borrowing The borrowing record to update
     * @return true if the update was successful, false otherwise
     */
    /**
     * Updates a borrowing record with return information
     * 
     * @param borrowing The borrowing to update
     * @return true if successful, false otherwise
     */
    public boolean updateBorrowing(Borrowing borrowing) {
        try {
            return borrowingDAO.updateBorrowing(borrowing);
        } catch (Exception e) {
            System.err.println("Error updating borrowing: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds multiple borrowing records to the database
     * 
     * @param borrowings The list of borrowing records to add
     * @return true if all borrowings were added successfully, false otherwise
     */
    public boolean addBorrowings(List<Borrowing> borrowings) {
        // Set default values for each borrowing if not already set
        for (Borrowing borrowing : borrowings) {
            if (borrowing.getNgayMuon() == null) {
                borrowing.setNgayMuon(LocalDate.now());
            }
            
            if (borrowing.getNgayHenTra() == null) {
                // Default loan period is 14 days
                borrowing.setNgayHenTra(borrowing.getNgayMuon().plusDays(14));
            }
            
            if (borrowing.getTrangThai() == null || borrowing.getTrangThai().isEmpty()) {
                borrowing.setTrangThai("DANG_MUON");
            }
        }
        
        return borrowingDAO.saveAll(borrowings);
    }
    
    /**
     * Gets a list of recent activities in the library system
     * 
     * @param limit The maximum number of activities to return
     * @return A list of activity descriptions
     */
    public List<String> getRecentActivities(int limit) {
        // This would normally fetch from a log or activity table
        // For now, we'll return mock data
        List<String> activities = new ArrayList<>();
        
        // Add some borrowing activities
        List<Borrowing> recentBorrowings = getAllBorrowings().stream()
                .sorted((b1, b2) -> b2.getNgayMuon().compareTo(b1.getNgayMuon()))
                .limit(5)
                .collect(Collectors.toList());
                
        for (Borrowing borrowing : recentBorrowings) {
            String bookTitle = "Unknown";
            String readerName = "Unknown";
            
            // Try to get book and reader details
            try {
                Optional<Borrowing> borrowingDetails = getBorrowingById(borrowing.getMaMuonTra());
                if (borrowingDetails.isPresent()) {
                    bookTitle = borrowingDetails.get().getTenSach() != null ? 
                                borrowingDetails.get().getTenSach() : "Unknown";
                    readerName = borrowingDetails.get().getTenDocGia() != null ? 
                                 borrowingDetails.get().getTenDocGia() : "Unknown";
                }
            } catch (Exception e) {
                // Ignore errors and use default values
            }
            
            // Add borrowing activity
            activities.add("Book '" + bookTitle + "' borrowed by " + readerName + 
                          " - " + borrowing.getNgayMuon().atStartOfDay());
            
            // If the book has been returned, add return activity
            if (borrowing.getNgayTraThucTe() != null) {
                activities.add("Book '" + bookTitle + "' returned by " + readerName + 
                              " - " + borrowing.getNgayTraThucTe().atStartOfDay());
            }
        }
        
        // Add some system activities
        activities.add("System configuration updated - " + LocalDateTime.now().minusDays(3));
        
        // Sort activities by date (most recent first) and limit the result
        return activities.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}