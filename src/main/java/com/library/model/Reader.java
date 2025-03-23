package com.library.model;

public class Reader {
    private String maDocGia;
    private String tenDocGia;
    private String email;
    private String soDienThoai;
    private String taiKhoanID;
    
    public Reader() {
    }
    
    public Reader(String maDocGia, String tenDocGia, String email, String soDienThoai, String taiKhoanID) {
        this.maDocGia = maDocGia;
        this.tenDocGia = tenDocGia;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.taiKhoanID = taiKhoanID;
    }
    
    // Getters and Setters
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
    
    public String getTaiKhoanID() {
        return taiKhoanID;
    }
    
    public void setTaiKhoanID(String taiKhoanID) {
        this.taiKhoanID = taiKhoanID;
    }
    
    @Override
    public String toString() {
        return tenDocGia;
    }
}
