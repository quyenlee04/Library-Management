package com.library.model;

import java.time.LocalDate;

public class Borrowing {
    private String maMuonTra;
    private String maDocGia;
    private String maSach;
    private LocalDate ngayMuon;
    private LocalDate ngayHenTra;
    private LocalDate ngayTraThucTe;
    private String trangThai;
    private String ghiChu;
    
    // For joining with other tables
    private String tenDocGia;
    private String tenSach;
    
    public Borrowing() {
    }
    
    public Borrowing(String maMuonTra, String maDocGia, String maSach, LocalDate ngayMuon, 
                    LocalDate ngayHenTra, LocalDate ngayTraThucTe, String trangThai, String ghiChu) {
        this.maMuonTra = maMuonTra;
        this.maDocGia = maDocGia;
        this.maSach = maSach;
        this.ngayMuon = ngayMuon;
        this.ngayHenTra = ngayHenTra;
        this.ngayTraThucTe = ngayTraThucTe;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }
    
    // Getters and Setters
    public String getMaMuonTra() {
        return maMuonTra;
    }
    
    public void setMaMuonTra(String maMuonTra) {
        this.maMuonTra = maMuonTra;
    }
    
    public String getMaDocGia() {
        return maDocGia;
    }
    
    public void setMaDocGia(String maDocGia) {
        this.maDocGia = maDocGia;
    }
    
    public String getMaSach() {
        return maSach;
    }
    
    public void setMaSach(String maSach) {
        this.maSach = maSach;
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
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public String getTenDocGia() {
        return tenDocGia;
    }
    
    public void setTenDocGia(String tenDocGia) {
        this.tenDocGia = tenDocGia;
    }
    
    public String getTenSach() {
        return tenSach;
    }
    
    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }
}