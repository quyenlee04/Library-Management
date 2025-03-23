package com.library.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.library.model.Borrowing;
import com.library.model.BorrowingDetail;
import com.library.util.DBUtil;

public class BorrowingDAO {
    private static BorrowingDAO instance;
    
    private BorrowingDAO() {
        // Private constructor for singleton pattern
    }
    
    public static BorrowingDAO getInstance() {
        if (instance == null) {
            instance = new BorrowingDAO();
        }
        return instance;
    }
    
    public List<Borrowing> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT mt.*, dg.tenDocGia, s.tenSach FROM muontra mt " +
                         "JOIN docgia dg ON mt.maDocGia = dg.maDocGia " +
                         "JOIN sach s ON mt.maSach = s.maSach";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrowing borrowing = mapResultSetToBorrowing(rs);
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all borrowings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return borrowings;
    }
    
    public List<Borrowing> findByReaderId(String readerId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT mt.*, dg.tenDocGia, s.tenSach FROM muontra mt " +
                         "JOIN docgia dg ON mt.maDocGia = dg.maDocGia " +
                         "JOIN sach s ON mt.maSach = s.maSach " +
                         "WHERE mt.maDocGia = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(readerId));
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrowing borrowing = mapResultSetToBorrowing(rs);
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println("Error finding borrowings by reader id: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return borrowings;
    }
    
    public List<Borrowing> findByBookId(String bookId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT mt.*, dg.tenDocGia, s.tenSach FROM muontra mt " +
                         "JOIN docgia dg ON mt.maDocGia = dg.maDocGia " +
                         "JOIN sach s ON mt.maSach = s.maSach " +
                         "WHERE mt.maSach = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(bookId));
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrowing borrowing = mapResultSetToBorrowing(rs);
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println("Error finding borrowings by book id: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return borrowings;
    }
    
    public Optional<Borrowing> findById(String id) {
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
            stmt.setInt(1, Integer.parseInt(id));
            
            if (rs.next()) {
                Borrowing borrowing = mapResultSetToBorrowing(rs);
                return Optional.of(borrowing);
            }
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error deleting borrowing: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public boolean saveAll(List<Borrowing> borrowings) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean success = true;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            String sql = "INSERT INTO muontra (maDocGia, maSach, ngayMuon, ngayHenTra, trangThai, ghiChu) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            for (Borrowing borrowing : borrowings) {
                stmt.setInt(1, Integer.parseInt(borrowing.getMaDocGia()));
                stmt.setInt(2, Integer.parseInt(borrowing.getMaSach()));
                stmt.setDate(3, Date.valueOf(borrowing.getNgayMuon()));
                stmt.setDate(4, Date.valueOf(borrowing.getNgayHenTra()));
                stmt.setString(5, borrowing.getTrangThai());
                stmt.setString(6, borrowing.getGhiChu());
                
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            
            // Check if all inserts were successful
            for (int result : results) {
                if (result <= 0) {
                    success = false;
                    break;
                }
            }
            
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
            
            return success;
        } catch (SQLException e) {
            System.err.println("Error saving multiple borrowings: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeResources(conn, stmt, rs);
        }
    }
    
    public List<Borrowing> findOverdue() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT mt.*, dg.tenDocGia, s.tenSach FROM muontra mt " +
                         "JOIN docgia dg ON mt.maDocGia = dg.maDocGia " +
                         "JOIN sach s ON mt.maSach = s.maSach " +
                         "WHERE mt.ngayHenTra < CURRENT_DATE AND mt.ngayTraThucTe IS NULL";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrowing borrowing = mapResultSetToBorrowing(rs);
                borrowings.add(borrowing);
            }
        } catch (SQLException e) {
            System.err.println("Error finding overdue borrowings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return borrowings;
    }
    
    public boolean returnBook(String borrowingId, LocalDate returnDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE muontra SET ngayTraThucTe = ?, trangThai = 'Returned' WHERE maMuonTra = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setInt(2, Integer.parseInt(borrowingId));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extends the due date for a borrowing and updates the extension count
     * 
     * @param borrowingId The ID of the borrowing to extend
     * @param newDueDate The new due date
     * @param newExtensionCount The new extension count
     * @return true if the extension was successful, false otherwise
     */
    public boolean extendBorrowing(String borrowingId, LocalDate newDueDate, int newExtensionCount) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE muontra SET ngayHenTra = ?, soLanGiaHan = ? WHERE maMuonTra = ? AND ngayTraThucTe IS NULL";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(newDueDate));
            stmt.setInt(2, newExtensionCount);
            stmt.setInt(3, Integer.parseInt(borrowingId));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error extending borrowing: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public int countActiveBorrowings() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM muontra WHERE ngayTraThucTe IS NULL";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active borrowings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return 0;
    }
    
    private Borrowing mapResultSetToBorrowing(ResultSet rs) throws SQLException {
        Borrowing borrowing = new Borrowing();
        borrowing.setMaMuonTra(String.valueOf(rs.getInt("maMuonTra")));
        borrowing.setMaDocGia(String.valueOf(rs.getInt("maDocGia")));
        borrowing.setMaSach(String.valueOf(rs.getInt("maSach")));
        borrowing.setNgayMuon(rs.getDate("ngayMuon").toLocalDate());
        borrowing.setNgayHenTra(rs.getDate("ngayHenTra").toLocalDate());
        
        Date ngayTraThucTe = rs.getDate("ngayTraThucTe");
        if (ngayTraThucTe != null) {
            borrowing.setNgayTraThucTe(ngayTraThucTe.toLocalDate());
        }
        
        borrowing.setTrangThai(rs.getString("trangThai"));
        borrowing.setGhiChu(rs.getString("ghiChu"));
        
        // Get joined data if available
        try {
            borrowing.setTenDocGia(rs.getString("tenDocGia"));
            borrowing.setTenSach(rs.getString("tenSach"));
        } catch (SQLException e) {
            // These columns might not be in the result set, ignore
        }
        
        return borrowing;
    }
    
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets detailed information about a borrowing including reader and book details
     * 
     * @param borrowingId The ID of the borrowing
     * @return A list of borrowing details
     */
    public List<BorrowingDetail> getBorrowingDetails(String borrowingId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BorrowingDetail> details = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT mt.*, dg.tenDocGia, dg.email, dg.soDienThoai, " +
                         "s.tenSach, s.tacGia, s.theLoai " +
                         "FROM muontra mt " +
                         "JOIN docgia dg ON mt.maDocGia = dg.maDocGia " +
                         "JOIN sach s ON mt.maSach = s.maSach " +
                         "WHERE mt.maMuonTra = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(borrowingId));
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BorrowingDetail detail = new BorrowingDetail();
                
                // Set borrowing information
                detail.setMaMuonTra(String.valueOf(rs.getInt("maMuonTra")));
                detail.setNgayMuon(rs.getDate("ngayMuon").toLocalDate());
                detail.setNgayHenTra(rs.getDate("ngayHenTra").toLocalDate());
                
                Date ngayTraThucTe = rs.getDate("ngayTraThucTe");
                if (ngayTraThucTe != null) {
                    detail.setNgayTraThucTe(ngayTraThucTe.toLocalDate());
                }
                
                detail.setTrangThai(rs.getString("trangThai"));
                
                // Set reader information
                detail.setMaDocGia(String.valueOf(rs.getInt("maDocGia")));
                detail.setTenDocGia(rs.getString("tenDocGia"));
                detail.setEmail(rs.getString("email"));
                detail.setSoDienThoai(rs.getString("soDienThoai"));
                
                // Set book information
                detail.setMaSach(String.valueOf(rs.getInt("maSach")));
                detail.setTenSach(rs.getString("tenSach"));
                detail.setTacGia(rs.getString("tacGia"));
                detail.setTheLoai(rs.getString("theLoai"));
                
                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrowing details: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return details;
    }
    
    /**
     * Updates a borrowing record in the database
     * 
     * @param borrowing The borrowing to update
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateBorrowing(Borrowing borrowing) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE muontra SET ngayTraThucTe = ? WHERE maMuonTra = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setDate(1, borrowing.getNgayTraThucTe() != null ? 
                         Date.valueOf(borrowing.getNgayTraThucTe()) : null);
            stmt.setInt(2, Integer.parseInt(borrowing.getMaMuonTra()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating borrowing: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public boolean update(Borrowing borrowing) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE muontra SET maDocGia = ?, maSach = ?, ngayMuon = ?, " +
                         "ngayHenTra = ?, ngayTraThucTe = ?, trangThai = ?, ghiChu = ? " +
                         "WHERE maMuonTra = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, Integer.parseInt(borrowing.getMaDocGia()));
            stmt.setInt(2, Integer.parseInt(borrowing.getMaSach()));
            stmt.setDate(3, Date.valueOf(borrowing.getNgayMuon()));
            stmt.setDate(4, Date.valueOf(borrowing.getNgayHenTra()));
            
            if (borrowing.getNgayTraThucTe() != null) {
                stmt.setDate(5, Date.valueOf(borrowing.getNgayTraThucTe()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, borrowing.getTrangThai());
            stmt.setString(7, borrowing.getGhiChu());
            stmt.setInt(8, Integer.parseInt(borrowing.getMaMuonTra()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating borrowing: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Finds borrowings within a specific date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of borrowings in the date range
     */
    public List<Borrowing> findByDateRange(LocalDate startDate, LocalDate endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Borrowing> borrowings = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT m.*, s.tenSach, d.tenDocGia FROM muontra m " +
                         "JOIN sach s ON m.maSach = s.maSach " +
                         "JOIN docgia d ON m.maDocGia = d.maDocGia " +
                         "WHERE m.ngayMuon BETWEEN ? AND ? " +
                         "ORDER BY m.ngayMuon DESC";
        
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrowing borrowing = new Borrowing();
                borrowing.setMaMuonTra(rs.getString("maMuonTra"));
                borrowing.setMaSach(rs.getString("maSach"));
                borrowing.setMaDocGia(rs.getString("maDocGia"));
                borrowing.setNgayMuon(rs.getDate("ngayMuon").toLocalDate());
                borrowing.setNgayHenTra(rs.getDate("ngayHenTra").toLocalDate());
                
                Date ngayTraThucTe = rs.getDate("ngayTraThucTe");
                if (ngayTraThucTe != null) {
                    borrowing.setNgayTraThucTe(ngayTraThucTe.toLocalDate());
                }
                
                borrowing.setTrangThai(rs.getString("trangThai"));
                borrowing.setTenSach(rs.getString("tenSach"));
                borrowing.setTenDocGia(rs.getString("tenDocGia"));
                
                borrowings.add(borrowing);
            }
            
            return borrowings;
        } catch (SQLException e) {
            System.err.println("Error finding borrowings by date range: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
}