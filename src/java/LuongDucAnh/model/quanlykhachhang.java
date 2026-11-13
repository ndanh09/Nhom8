package model;

import java.sql.Timestamp;

public class quanlykhachhang {

    private int madonhang;
    private String makhachhang;
    private String hoten;
    private String sodienthoai;
    private String diachi;
    private String email;
    private String ghichu;
    private String phuongthucthanhtoan;
    private Timestamp ngaydat;
    private int tongtien;
    private String masanpham;
    private String trangthai;

    public quanlykhachhang() {
    }

    public quanlykhachhang(int madonhang, String makhachhang, String hoten,
            String sodienthoai, String diachi, String email,
            String ghichu, String phuongthucthanhtoan,
            Timestamp ngaydat, int tongtien, String masanpham) {

        this.madonhang = madonhang;
        this.makhachhang = makhachhang;
        this.hoten = hoten;
        this.sodienthoai = sodienthoai;
        this.diachi = diachi;
        this.email = email;
        this.ghichu = ghichu;
        this.phuongthucthanhtoan = phuongthucthanhtoan;
        this.ngaydat = ngaydat;
        this.tongtien = tongtien;
        this.masanpham = masanpham;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public int getMadonhang() {
        return madonhang;
    }

    public void setMadonhang(int madonhang) {
        this.madonhang = madonhang;
    }

    public String getMakhachhang() {
        return makhachhang;
    }

    public void setMakhachhang(String makhachhang) {
        this.makhachhang = makhachhang;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public String getPhuongthucthanhtoan() {
        return phuongthucthanhtoan;
    }

    public void setPhuongthucthanhtoan(String phuongthucthanhtoan) {
        this.phuongthucthanhtoan = phuongthucthanhtoan;
    }

    public Timestamp getNgaydat() {
        return ngaydat;
    }

    public void setNgaydat(Timestamp ngaydat) {
        this.ngaydat = ngaydat;
    }

    public int getTongtien() {
        return tongtien;
    }

    public void setTongtien(int tongtien) {
        this.tongtien = tongtien;
    }

    public String getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(String masanpham) {
        this.masanpham = masanpham;
    }

}
