package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.library.model.Reader;
import com.library.util.DBUtil;

public class ReaderDAO {
    private static ReaderDAO instance;
    
    private ReaderDAO() {
        // Private constructor for singleton pattern
    }
    
    public static ReaderDAO getInstance() {
        if (instance == null) {
            instance = new ReaderDAO();
        }
        return instance;
    }
    
    public List<Reader> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Reader> readers = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM docgia";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Reader reader = mapResultSetToReader(rs);
                readers.add(reader);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all readers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return readers;
    }
    
    public Optional<Reader> findById(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM docgia WHERE maDocGia = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Reader reader = mapResultSetToReader(rs);
                return Optional.of(reader);
            }
        } catch (SQLException e) {
            System.err.println("Error finding reader by id: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return Optional.empty();
    }
    
    public List<Reader> search(String keyword) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Reader> readers = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM docgia WHERE tenDocGia LIKE ? OR email LIKE ? OR soDienThoai LIKE ?";
            stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Reader reader = mapResultSetToReader(rs);
                readers.add(reader);
            }
        } catch (SQLException e) {
            System.err.println("Error searching readers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return readers;
    }
    
    public boolean save(Reader reader) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql;
            
            if (reader.getMaDocGia() == null || reader.getMaDocGia().isEmpty()) {
                // Insert new reader without maDocGia (auto-increment)
                sql = "INSERT INTO docgia (tenDocGia, email, soDienThoai, taiKhoanID) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, reader.getTenDocGia());
                stmt.setString(2, reader.getEmail());
                stmt.setString(3, reader.getSoDienThoai());
                stmt.setString(4, reader.getTaiKhoanID());
            } else {
                // Insert with specified maDocGia
                sql = "INSERT INTO docgia (maDocGia, tenDocGia, email, soDienThoai, taiKhoanID) VALUES (?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(reader.getMaDocGia()));
                stmt.setString(2, reader.getTenDocGia());
                stmt.setString(3, reader.getEmail());
                stmt.setString(4, reader.getSoDienThoai());
                stmt.setString(5, reader.getTaiKhoanID());
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            // If auto-increment was used, get the generated ID
            if ((reader.getMaDocGia() == null || reader.getMaDocGia().isEmpty()) && rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    reader.setMaDocGia(String.valueOf(rs.getInt(1)));
                }
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving reader: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    public boolean update(Reader reader) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE docgia SET tenDocGia = ?, email = ?, soDienThoai = ?, taiKhoanID = ? WHERE maDocGia = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, reader.getTenDocGia());
            stmt.setString(2, reader.getEmail());
            stmt.setString(3, reader.getSoDienThoai());
            stmt.setString(4, reader.getTaiKhoanID());
            stmt.setInt(5, Integer.parseInt(reader.getMaDocGia()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reader: " + e.getMessage());
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
            String sql = "DELETE FROM docgia WHERE maDocGia = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(id));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting reader: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public int countAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM docgia";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting readers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return 0;
    }
    
    private Reader mapResultSetToReader(ResultSet rs) throws SQLException {
        Reader reader = new Reader();
        reader.setMaDocGia(rs.getString("maDocGia"));
        reader.setTenDocGia(rs.getString("tenDocGia"));
        reader.setEmail(rs.getString("email"));
        reader.setSoDienThoai(rs.getString("soDienThoai"));
        reader.setTaiKhoanID(rs.getString("taiKhoanID"));
        return reader;
    }
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBUtil.getInstance().closeConnection(conn);
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
