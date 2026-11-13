package model;

public class DonHang {

    private int madonhang;
    private String makhachhang;
    private String hoten;
    private String masanpham;
    private String sodienthoai;
    private String diachi;
    private String tinhthanh;
    private String email;
    private String ghichu;
    private String phuongthucthanhtoan = "Thanh toán khi giao hàng";
    private int tongtien;
    private java.util.Date ngaydat;
    private String magiamgia;
    private int sotiengiam;
    private int tongtiensaugiam;

    public String getMagiamgia() {
        return magiamgia;
    }

    public void setMagiamgia(String magiamgia) {
        this.magiamgia = magiamgia;
    }

    public int getSotiengiam() {
        return sotiengiam;
    }

    public void setSotiengiam(int sotiengiam) {
        this.sotiengiam = sotiengiam;
    }

    public int getTongtiensaugiam() {
        return tongtiensaugiam;
    }

    public void setTongtiensaugiam(int tongtiensaugiam) {
        this.tongtiensaugiam = tongtiensaugiam;
    }

    public DonHang(int madonhang, String makhachhang, String hoten, String masanpham, String sodienthoai, String diachi, String email, String ghichu, String phuongthucthanhtoan, int tongtien, java.util.Date ngaydat) {
        this.madonhang = madonhang;
        this.makhachhang = makhachhang;
        this.hoten = hoten;
        this.masanpham = masanpham;
        this.sodienthoai = sodienthoai;
        this.diachi = diachi;
        this.email = email;
        this.ghichu = ghichu;
        this.phuongthucthanhtoan = phuongthucthanhtoan;
        this.tongtien = tongtien;
        this.ngaydat = ngaydat;
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

    public String getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(String masanpham) {
        this.masanpham = masanpham;
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

    public String getTinhthanh() {
        return tinhthanh;
    }

    public void setTinhthanh(String tinhthanh) {
        this.tinhthanh = tinhthanh;
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

    public int getTongtien() {
        return tongtien;
    }

    public void setTongtien(int tongtien) {
        this.tongtien = tongtien;
    }

    public java.util.Date getNgaydat() {
        return ngaydat;
    }

    public void setNgaydat(java.util.Date ngaydat) {
        this.ngaydat = ngaydat;
    }

    public DonHang() {
    }

}
