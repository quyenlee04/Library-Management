package com.library.model;

import java.time.LocalDate;

public class Fine {
    private String maPhiPhat;
    private String maPhieuMuon;
    private double soTien;
    private String lyDo;
    private LocalDate ngayPhat;
    private boolean daTra;
    private LocalDate ngayTra;
    
    // For display purposes
    private String tenDocGia;
    
    public Fine() {
        this.ngayPhat = LocalDate.now();
        this.daTra = false;
    }
    
    // Getters and Setters
    public String getMaPhiPhat() {
        return maPhiPhat;
    }
    
    public void setMaPhiPhat(String maPhiPhat) {
        this.maPhiPhat = maPhiPhat;
    }
    
    public String getMaPhieuMuon() {
        return maPhieuMuon;
    }
    
    public void setMaPhieuMuon(String maPhieuMuon) {
        this.maPhieuMuon = maPhieuMuon;
    }
    
    public double getSoTien() {
        return soTien;
    }
    
    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }
    
    public String getLyDo() {
        return lyDo;
    }
    
    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }
    
    public LocalDate getNgayPhat() {
        return ngayPhat;
    }
    
    public void setNgayPhat(LocalDate ngayPhat) {
        this.ngayPhat = ngayPhat;
    }
    
    public boolean isDaTra() {
        return daTra;
    }
    
    public void setDaTra(boolean daTra) {
        this.daTra = daTra;
    }
    
    public LocalDate getNgayTra() {
        return ngayTra;
    }
    
    public void setNgayTra(LocalDate ngayTra) {
        this.ngayTra = ngayTra;
    }
    
    public String getTenDocGia() {
        return tenDocGia;
    }
    
    public void setTenDocGia(String tenDocGia) {
        this.tenDocGia = tenDocGia;
    }
}
