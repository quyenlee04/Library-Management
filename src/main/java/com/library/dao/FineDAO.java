package com.library.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.library.model.Fine;
import com.library.util.DBUtil;

public class FineDAO {
    private static FineDAO instance;
    
    private FineDAO() {
        // Private constructor for singleton pattern
    }
    
    public static FineDAO getInstance() {
        if (instance == null) {
            instance = new FineDAO();
        }
        return instance;
    }
    
    public List<Fine> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Fine> fines = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT p.*, d.tenDocGia FROM phiphat p " +
                         "JOIN phieumuon pm ON p.maPhieuMuon = pm.maPhieuMuon " +
                         "JOIN docgia d ON pm.maDocGia = d.maDocGia";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Fine fine = mapResultSetToFine(rs);
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all fines: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return fines;
    }
    
    public Optional<Fine> findById(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT p.*, d.tenDocGia FROM phiphat p " +
                         "JOIN phieumuon pm ON p.maPhieuMuon = pm.maPhieuMuon " +
                         "JOIN docgia d ON pm.maDocGia = d.maDocGia " +
                         "WHERE p.maPhiPhat = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Fine fine = mapResultSetToFine(rs);
                return Optional.of(fine);
            }
        } catch (SQLException e) {
            System.err.println("Error finding fine by id: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return Optional.empty();
    }
    
    public List<Fine> findByBorrowingId(String borrowingId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Fine> fines = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT p.*, d.tenDocGia FROM phiphat p " +
                         "JOIN phieumuon pm ON p.maPhieuMuon = pm.maPhieuMuon " +
                         "JOIN docgia d ON pm.maDocGia = d.maDocGia " +
                         "WHERE p.maPhieuMuon = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, borrowingId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Fine fine = mapResultSetToFine(rs);
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.err.println("Error finding fines by borrowing id: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return fines;
    }
    
    public List<Fine> findUnpaidFines() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Fine> fines = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT p.*, d.tenDocGia FROM phiphat p " +
                         "JOIN phieumuon pm ON p.maPhieuMuon = pm.maPhieuMuon " +
                         "JOIN docgia d ON pm.maDocGia = d.maDocGia " +
                         "WHERE p.daTra = 0";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Fine fine = mapResultSetToFine(rs);
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.err.println("Error finding unpaid fines: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return fines;
    }
    
    private Fine mapResultSetToFine(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setMaPhieuPhat(rs.getString("maPhieuPhat"));
        fine.setMaMuonTra(rs.getString("maPhieuMuon"));
        fine.setSoTienPhat(rs.getDouble("soTienPhat"));
        fine.setLyDo(rs.getString("lyDo"));
        
        // Try to get tenDocGia if it exists in the result set
        try {
            fine.setTenDocGia(rs.getString("tenDocGia"));
        } catch (SQLException e) {
            // Column might not be in the result set, ignore
        }
        
        return fine;
    }
    
    public boolean save(Fine fine) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql;
            
            // If maPhieuPhat is null, exclude it from the INSERT statement to let database auto-increment
            if (fine.getMaPhieuPhat() == null || fine.getMaPhieuPhat().isEmpty()) {
                sql = "INSERT INTO phieuphat (maPhieuMuon, lyDo, soTienPhat) " +
                      "VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, Integer.parseInt(fine.getMaMuonTra()));
                stmt.setString(2, fine.getLyDo());
                stmt.setDouble(3, fine.getSoTienPhat());
            } else {
                sql = "INSERT INTO phieuphat (maPhieuPhat, maPhieuMuon, lyDo, soTienPhat) " +
                      "VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(fine.getMaPhieuPhat()));
                stmt.setInt(2, Integer.parseInt(fine.getMaMuonTra()));
                stmt.setString(3, fine.getLyDo());
                stmt.setDouble(4, fine.getSoTienPhat());
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            // If auto-increment was used, get the generated ID and set it in the fine object
            if ((fine.getMaPhieuPhat() == null || fine.getMaPhieuPhat().isEmpty()) && rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    fine.setMaPhieuPhat(String.valueOf(rs.getInt(1)));
                }
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving fine: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    public boolean update(Fine fine) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE phieuphat SET maPhieuMuon = ?, lyDo = ?, soTienPhat = ? WHERE maPhieuPhat = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(fine.getMaMuonTra()));
            stmt.setString(2, fine.getLyDo());
            stmt.setDouble(3, fine.getSoTienPhat());
            stmt.setInt(4, Integer.parseInt(fine.getMaPhieuPhat()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating fine: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public boolean payFine(String fineId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE phiphat SET daTra = ?, ngayTra = ? WHERE maPhiPhat = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, true);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setString(3, fineId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error paying fine: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public boolean delete(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "DELETE FROM phiphat WHERE maPhiPhat = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting fine: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBUtil.closeConnection(conn);
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
