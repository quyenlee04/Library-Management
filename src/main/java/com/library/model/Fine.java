package com.library.model;

import java.time.LocalDate;

/**
 * Represents a fine in the library system
 */
public class Fine {
    private String maPhieuPhat;
    private String maMuonTra;
    private String lyDo;
    private double soTienPhat;
    private LocalDate ngayTra;
    private String tenSach;
    
    // For display purposes
    private String tenDocGia;
    private LocalDate ngayPhat;
    
    public Fine() {
        // Default constructor
        this.ngayPhat = LocalDate.now();
    }
    
    public Fine(String maPhieuPhat, String maMuonTra, String lyDo, double soTienPhat) {
        this.maPhieuPhat = maPhieuPhat;
        this.maMuonTra = maMuonTra;
        this.lyDo = lyDo;
        this.soTienPhat = soTienPhat;
        this.ngayPhat = LocalDate.now();
    }
    
    // Getters and Setters
    public String getMaPhieuPhat() {
        return maPhieuPhat;
    }
    
    public void setMaPhieuPhat(String maPhieuPhat) {
        this.maPhieuPhat = maPhieuPhat;
    }
    
  
    
    public String getLyDo() {
        return lyDo;
    }
    
    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }
    
    public double getSoTienPhat() {
        return soTienPhat;
    }
    
    public void setSoTienPhat(double soTienPhat) {
        this.soTienPhat = soTienPhat;
    }
    
    public String getTenDocGia() {
        return tenDocGia;
    }
    
    public void setTenDocGia(String tenDocGia) {
        this.tenDocGia = tenDocGia;
    }
    
    public LocalDate getNgayPhat() {
        return ngayPhat;
    }
    
    public void setNgayPhat(LocalDate ngayPhat) {
        this.ngayPhat = ngayPhat;
    }
    
    // For compatibility with existing code
    public void setSoTien(double soTien) {
        this.soTienPhat = soTien;
    }
    
    public double getSoTien() {
        return this.soTienPhat;
    }

    public String getMaMuonTra() {
        return maMuonTra;
    }

    public void setMaMuonTra(String maMuonTra) {
        this.maMuonTra = maMuonTra;
    }

    // For compatibility with FineManagementController
    public boolean isDaTra() {
        return false; // Default implementation
    }
    
    public String getTrangThai() {
        return "Unpaid"; // Default implementation
    }
    
    public void setTrangThai(String trangThai) {
        // No-op implementation for compatibility
    }

    public LocalDate getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(LocalDate ngayTra) {
        this.ngayTra = ngayTra;
    }

    @Override
    public String toString() {
        return "Fine [maPhieuPhat=" + maPhieuPhat + ", maPhieuMuon=" + maMuonTra + 
               ", lyDo=" + lyDo + ", soTienPhat=" + soTienPhat + "]";
    }

    /**
     * Gets the book title associated with this fine.
     * @return the book title
     */
    public String getTenSach() {
        return tenSach;
    }

    /**
     * Sets the book title associated with this fine.
     * @param tenSach the book title to set
     */
    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }
}
