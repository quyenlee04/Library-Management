package com.library.model;

import java.time.LocalDate;

public class Book {
    // Fields to match database columns
    private String maSach; // Instead of id
    private String tenSach; // Instead of title
    private String tacGia;  // Instead of author
    private int namXuatBan; // Instead of publicationYear
    private String theLoai; // Instead of category
    private String trangThai; // New field for status (CHO_MUON, DA_MUON, etc.)
    private String moTa;    // Instead of description
    private int soLuong;    // New field for total quantity
    private int soLuongKhaDung; // New field for available quantity
    
    // Fields not in database but useful for application
    private LocalDate addedDate;
    private String location; // Shelf/section in the library
    
    // Constructors
    public Book() {
        this.trangThai = "CHO_MUON"; // Default status is available
        this.addedDate = LocalDate.now();
        this.soLuong = 1;
        this.soLuongKhaDung = 1;
    }
    
    public Book(String maSach, String tenSach, String tacGia) {
        this();
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
    }
    
    // Getters and Setters
    public String getMaSach() {
        return maSach;
    }
    
    public void setMaSach(String maSach) {
        this.maSach = maSach;
    }
    
    public String getTenSach() {
        return tenSach;
    }
    
    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }
    
    public String getTacGia() {
        return tacGia;
    }
    
    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }
    
    public int getNamXuatBan() {
        return namXuatBan;
    }
    
    public void setNamXuatBan(int namXuatBan) {
        this.namXuatBan = namXuatBan;
    }
    
    public String getTheLoai() {
        return theLoai;
    }
    
    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public int getSoLuong() {
        return soLuong;
    }
    
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
    
    public int getSoLuongKhaDung() {
        return soLuongKhaDung;
    }
    
    public void setSoLuongKhaDung(int soLuongKhaDung) {
        this.soLuongKhaDung = soLuongKhaDung;
    }
    
    public LocalDate getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    // Compatibility methods for existing code
    public String getId() {
        return maSach;
    }
    
    public void setId(String id) {
        this.maSach = id;
    }
    
    public String getTitle() {
        return tenSach;
    }
    
    public void setTitle(String title) {
        this.tenSach = title;
    }
    
    public String getAuthor() {
        return tacGia;
    }
    
    public void setAuthor(String author) {
        this.tacGia = author;
    }
    
    public String getCategory() {
        return theLoai;
    }
    
    public void setCategory(String category) {
        this.theLoai = category;
    }
    
    public int getPublicationYear() {
        return namXuatBan;
    }
    
    public void setPublicationYear(int publicationYear) {
        this.namXuatBan = publicationYear;
    }
    
    public String getDescription() {
        return moTa;
    }
    
    public void setDescription(String description) {
        this.moTa = description;
    }
    
    public boolean isAvailable() {
        return "CHO_MUON".equals(trangThai);
    }
    
    public void setAvailable(boolean available) {
        this.trangThai = available ? "CHO_MUON" : "DA_MUON";
    }
    
    @Override
    public String toString() {
        return tenSach + " by " + tacGia;
    }
}
