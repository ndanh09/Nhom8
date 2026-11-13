package model;

import java.util.Date;

public class DoanhThu {
    private Date ngayBan;
    private int tongSoDonHang;
    private int tongSoLuongBan;
    private long tongDoanhThuNgay;

    public DoanhThu() {
    }

    // Getter và Setter
    public Date getNgayBan() {
        return ngayBan;
    }

    public void setNgayBan(Date ngayBan) {
        this.ngayBan = ngayBan;
    }

    public int getTongSoDonHang() {
        return tongSoDonHang;
    }

    public void setTongSoDonHang(int tongSoDonHang) {
        this.tongSoDonHang = tongSoDonHang;
    }

    public int getTongSoLuongBan() {
        return tongSoLuongBan;
    }

    public void setTongSoLuongBan(int tongSoLuongBan) {
        this.tongSoLuongBan = tongSoLuongBan;
    }

    public long getTongDoanhThuNgay() {
        return tongDoanhThuNgay;
    }

    public void setTongDoanhThuNgay(long tongDoanhThuNgay) {
        this.tongDoanhThuNgay = tongDoanhThuNgay;
    }
}