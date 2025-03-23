package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.library.model.Book;
import com.library.util.DBUtil;

public class BookDAO {
    private static BookDAO instance;
    
    private BookDAO() {
        // Private constructor for singleton pattern
    }
    
    public static BookDAO getInstance() {
        if (instance == null) {
            instance = new BookDAO();
        }
        return instance;
    }
    
    public List<Book> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM sach";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all books: " + e.getMessage());
            e.printStackTrace();
            
            // For testing, return mock data if database is not available
            // if (books.isEmpty()) {
            //     books = getMockBooks();
            // }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return books;
    }
    
    public Optional<Book> findById(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM sach WHERE maSach = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Book book = mapResultSetToBook(rs);
                return Optional.of(book);
            }
        } catch (SQLException e) {
            System.err.println("Error finding book by id: " + e.getMessage());
            e.printStackTrace();
            
            // For testing, return mock data if database is not available
            // for (Book book : getMockBooks()) {
            //     if (book.getId().equals(id)) {
            //         return Optional.of(book);
            //     }
            // }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return Optional.empty();
    }
    
    public List<Book> findByTitle(String title) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM sach WHERE tenSach LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + title + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error finding books by title: " + e.getMessage());
            e.printStackTrace();
            
            // For testing, return mock data if database is not available
            // if (books.isEmpty()) {
            //     for (Book book : getMockBooks()) {
            //         if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
            //             books.add(book);
            //         }
            //     }
            // }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return books;
    }
    
    public List<Book> findByAuthor(String author) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM sach WHERE tacGia LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + author + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error finding books by author: " + e.getMessage());
            e.printStackTrace();
            
            // For testing, return mock data if database is not available
            // if (books.isEmpty()) {
            //     for (Book book : getMockBooks()) {
            //         if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
            //             books.add(book);
            //         }
            //     }
            // }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return books;
    }
    
    public List<Book> findByCategory(String category) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT * FROM sach WHERE theLoai LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + category + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error finding books by category: " + e.getMessage());
            e.printStackTrace();
            
            // For testing, return mock data if database is not available
            // if (books.isEmpty()) {
            //     for (Book book : getMockBooks()) {
            //         if (book.getCategory().toLowerCase().contains(category.toLowerCase())) {
            //             books.add(book);
            //         }
            //     }
            // }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return books;
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setMaSach(rs.getString("maSach"));
        book.setTenSach(rs.getString("tenSach"));
        book.setTacGia(rs.getString("tacGia"));
        book.setNamXuatBan(rs.getInt("namXuatBan"));
        book.setTheLoai(rs.getString("theLoai"));
        book.setTrangThai(rs.getString("trangThai"));
        book.setMoTa(rs.getString("moTa"));
        book.setSoLuong(rs.getInt("soLuong"));
        book.setSoLuongKhaDung(rs.getInt("soLuongKhaDung"));
        return book;
    }
    
    public boolean save(Book book) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql;
            
            // If maSach is null, exclude it from the INSERT statement to let database auto-increment
            if (book.getMaSach() == null) {
                sql = "INSERT INTO sach (tenSach, tacGia, namXuatBan, theLoai, trangThai, moTa, soLuong, soLuongKhaDung) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, book.getTenSach());
                stmt.setString(2, book.getTacGia());
                stmt.setInt(3, book.getNamXuatBan());
                stmt.setString(4, book.getTheLoai());
                stmt.setString(5, book.getTrangThai());
                stmt.setString(6, book.getMoTa());
                stmt.setInt(7, book.getSoLuong());
                stmt.setInt(8, book.getSoLuongKhaDung());
            } else {
                sql = "INSERT INTO sach (maSach, tenSach, tacGia, namXuatBan, theLoai, trangThai, moTa, soLuong, soLuongKhaDung) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(book.getMaSach()));
                stmt.setString(2, book.getTenSach());
                stmt.setString(3, book.getTacGia());
                stmt.setInt(4, book.getNamXuatBan());
                stmt.setString(5, book.getTheLoai());
                stmt.setString(6, book.getTrangThai());
                stmt.setString(7, book.getMoTa());
                stmt.setInt(8, book.getSoLuong());
                stmt.setInt(9, book.getSoLuongKhaDung());
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            // If auto-increment was used, get the generated ID and set it in the book object
            if (book.getMaSach() == null && rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    book.setMaSach(String.valueOf(generatedKeys.getInt(1)));
                }
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving book: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public boolean update(Book book) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "UPDATE sach SET tenSach = ?, tacGia = ?, namXuatBan = ?, " +
                         "theLoai = ?, trangThai = ?, moTa = ?, soLuong = ?, soLuongKhaDung = ? " +
                         "WHERE maSach = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, book.getTenSach());
            stmt.setString(2, book.getTacGia());
            stmt.setInt(3, book.getNamXuatBan());
            stmt.setString(4, book.getTheLoai());
            stmt.setString(5, book.getTrangThai());
            stmt.setString(6, book.getMoTa());
            stmt.setInt(7, book.getSoLuong());
            stmt.setInt(8, book.getSoLuongKhaDung());
            stmt.setString(9, book.getMaSach());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
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
            String sql = "DELETE FROM sach WHERE maSach = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public int countBooks() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM sach";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting books: " + e.getMessage());
            e.printStackTrace();
            
            // For testing, return mock count if database is not available
            // return getMockBooks().size();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return 0;
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
    
    // private List<Book> getMockBooks() {
    //     List<Book> books = new ArrayList<>();
        
    //     Book book1 = new Book("1", "Clean Code", "Robert C. Martin", "9780132350884");
    //     book1.setPublisher("Prentice Hall");
    //     book1.setCategory("Programming");
    //     book1.setPublicationYear(2008);
    //     book1.setDescription("A handbook of agile software craftsmanship");
    //     book1.setLocation("Section A, Shelf 1");
    //     books.add(book1);
        
    //     Book book2 = new Book("B002", "Design Patterns", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "9780201633610");
    //     book2.setPublisher("Addison-Wesley Professional");
    //     book2.setCategory("Programming");
    //     book2.setPublicationYear(1994);
    //     book2.setDescription("Elements of Reusable Object-Oriented Software");
    //     book2.setLocation("Section A, Shelf 2");
    //     books.add(book2);
        
    //     Book book3 = new Book("B003", "The Pragmatic Programmer", "Andrew Hunt, David Thomas", "9780201616224");
    //     book3.setPublisher("Addison-Wesley Professional");
    //     book3.setCategory("Programming");
    //     book3.setPublicationYear(1999);
    //     book3.setDescription("From Journeyman to Master");
    //     book3.setLocation("Section A, Shelf 1");
    //     books.add(book3);
        
    //     Book book4 = new Book("B004", "Introduction to Algorithms", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein", "9780262033848");
    //     book4.setPublisher("MIT Press");
    //     book4.setCategory("Computer Science");
    //     book4.setPublicationYear(2009);
    //     book4.setDescription("A comprehensive introduction to algorithms");
    //     book4.setLocation("Section B, Shelf 3");
    //     books.add(book4);
        
    //     Book book5 = new Book("B005", "Artificial Intelligence: A Modern Approach", "Stuart Russell, Peter Norvig", "9780136042594");
    //     book5.setPublisher("Pearson");
    //     book5.setCategory("Computer Science");
    //     book5.setPublicationYear(2009);
    //     book5.setDescription("The leading textbook in Artificial Intelligence");
    //     book5.setLocation("Section B, Shelf 4");
    //     books.add(book5);
        
    //     return books;
    // }
}
