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
    
    public boolean save(Fine fine) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql;
            
            // If maPhiPhat is null, exclude it from the INSERT statement to let database auto-increment
            if (fine.getMaPhiPhat() == null || fine.getMaPhiPhat().isEmpty()) {
                sql = "INSERT INTO phiphat (maPhieuMuon, soTien, lyDo, ngayPhat, daTra) " +
                      "VALUES (?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, fine.getMaPhieuMuon());
                stmt.setDouble(2, fine.getSoTien());
                stmt.setString(3, fine.getLyDo());
                stmt.setDate(4, Date.valueOf(fine.getNgayPhat()));
                stmt.setBoolean(5, fine.isDaTra());
            } else {
                sql = "INSERT INTO phiphat (maPhiPhat, maPhieuMuon, soTien, lyDo, ngayPhat, daTra) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, fine.getMaPhiPhat());
                stmt.setString(2, fine.getMaPhieuMuon());
                stmt.setDouble(3, fine.getSoTien());
                stmt.setString(4, fine.getLyDo());
                stmt.setDate(5, Date.valueOf(fine.getNgayPhat()));
                stmt.setBoolean(6, fine.isDaTra());
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            // If auto-increment was used, get the generated ID and set it in the fine object
            if ((fine.getMaPhiPhat() == null || fine.getMaPhiPhat().isEmpty()) && rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    fine.setMaPhiPhat(rs.getString(1));
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
            String sql = "UPDATE phiphat SET maPhieuMuon = ?, soTien = ?, lyDo = ?, " +
                         "ngayPhat = ?, daTra = ?, ngayTra = ? WHERE maPhiPhat = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, fine.getMaPhieuMuon());
            stmt.setDouble(2, fine.getSoTien());
            stmt.setString(3, fine.getLyDo());
            stmt.setDate(4, Date.valueOf(fine.getNgayPhat()));
            stmt.setBoolean(5, fine.isDaTra());
            
            if (fine.getNgayTra() != null) {
                stmt.setDate(6, Date.valueOf(fine.getNgayTra()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            
            stmt.setString(7, fine.getMaPhiPhat());
            
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
    
    private Fine mapResultSetToFine(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setMaPhiPhat(rs.getString("maPhiPhat"));
        fine.setMaPhieuMuon(rs.getString("maPhieuMuon"));
        fine.setSoTien(rs.getDouble("soTien"));
        fine.setLyDo(rs.getString("lyDo"));
        
        Date ngayPhat = rs.getDate("ngayPhat");
        if (ngayPhat != null) {
            fine.setNgayPhat(ngayPhat.toLocalDate());
        }
        
        fine.setDaTra(rs.getBoolean("daTra"));
        
        Date ngayTra = rs.getDate("ngayTra");
        if (ngayTra != null) {
            fine.setNgayTra(ngayTra.toLocalDate());
        }
        
        fine.setTenDocGia(rs.getString("tenDocGia"));
        
        return fine;
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
