package com.library.model;

import java.time.LocalDate;

public class BorrowingDetail {
    // Borrowing information
    private String maMuonTra;
    private LocalDate ngayMuon;
    private LocalDate ngayHenTra;
    private LocalDate ngayTraThucTe;
    private String trangThai;
    
    // Reader information
    private String maDocGia;
    private String tenDocGia;
    private String email;
    private String soDienThoai;
    
    // Book information
    private String maSach;
    private String tenSach;
    private String tacGia;
    private String theLoai;
    
    // Default constructor
    public BorrowingDetail() {
    }
    
    // Getters and Setters
    public String getMaMuonTra() {
        return maMuonTra;
    }
    
    public void setMaMuonTra(String maMuonTra) {
        this.maMuonTra = maMuonTra;
    }
    
    public LocalDate getNgayMuon() {
        return ngayMuon;
    }
    
    public void setNgayMuon(LocalDate ngayMuon) {
        this.ngayMuon = ngayMuon;
    }
    
    public LocalDate getNgayHenTra() {
        return ngayHenTra;
    }
    
    public void setNgayHenTra(LocalDate ngayHenTra) {
        this.ngayHenTra = ngayHenTra;
    }
    
    public LocalDate getNgayTraThucTe() {
        return ngayTraThucTe;
    }
    
    public void setNgayTraThucTe(LocalDate ngayTraThucTe) {
        this.ngayTraThucTe = ngayTraThucTe;
    }
    
    public String getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public String getMaDocGia() {
        return maDocGia;
    }
    
    public void setMaDocGia(String maDocGia) {
        this.maDocGia = maDocGia;
    }
    
    public String getTenDocGia() {
        return tenDocGia;
    }
    
    public void setTenDocGia(String tenDocGia) {
        this.tenDocGia = tenDocGia;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSoDienThoai() {
        return soDienThoai;
    }
    
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
    
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
    
    public String getTheLoai() {
        return theLoai;
    }
    
    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }
}