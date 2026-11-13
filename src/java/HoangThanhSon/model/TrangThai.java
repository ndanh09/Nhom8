package model;

import java.util.Date;

public class TrangThai {

    private int matrangthai;
    private int magiohang;
    private String makhachhang;
    private String masanpham;
    private int soluong;
    private String kichco;
    private int giaban;
    private Date ngaymua;
    private int tinhtrang;
    private int madonhang;
    private String tensanpham;
    private String hinhanh;

    // ==== Bổ sung cho module QUẢN LÝ VẬN CHUYỂN ====
    private String emailVanChuyen;   // email người vận chuyển đang nhận đơn này (null = chưa ai nhận)
    private String hotenNguoiNhan;   // họ tên người nhận hàng (lấy từ bảng donhang)
    private String sdtNguoiNhan;     // số điện thoại người nhận hàng (lấy từ bảng donhang)
    private String diaChiNguoiNhan;  // địa chỉ giao hàng (lấy từ bảng donhang)
    private String tinhThanhNguoiNhan; // tỉnh/thành giao hàng (lấy từ bảng donhang), dùng để phân vùng cho vận chuyển
    private String ghiChuDonHang;    // ghi chú đơn hàng (lấy từ bảng donhang)

    // Đơn hàng (donhang) chứa dòng này đang bị admin KHÓA: không ai thao tác được cho tới khi mở khóa.
    private boolean donhangKhoa;

    public boolean isDonhangKhoa() {
        return donhangKhoa;
    }

    public void setDonhangKhoa(boolean donhangKhoa) {
        this.donhangKhoa = donhangKhoa;
    }

    // Danh mục sản phẩm (chỉ dùng ở trang chi tiết sản phẩm trong hóa đơn)
    private String danhmuc;

    public String getDanhmuc() {
        return danhmuc;
    }

    public void setDanhmuc(String danhmuc) {
        this.danhmuc = danhmuc;
    }

    public TrangThai() {
    }

    public String getEmailVanChuyen() {
        return emailVanChuyen;
    }

    public void setEmailVanChuyen(String emailVanChuyen) {
        this.emailVanChuyen = emailVanChuyen;
    }

    public String getHotenNguoiNhan() {
        return hotenNguoiNhan;
    }

    public void setHotenNguoiNhan(String hotenNguoiNhan) {
        this.hotenNguoiNhan = hotenNguoiNhan;
    }

    public String getSdtNguoiNhan() {
        return sdtNguoiNhan;
    }

    public void setSdtNguoiNhan(String sdtNguoiNhan) {
        this.sdtNguoiNhan = sdtNguoiNhan;
    }

    public String getDiaChiNguoiNhan() {
        return diaChiNguoiNhan;
    }

    public void setDiaChiNguoiNhan(String diaChiNguoiNhan) {
        this.diaChiNguoiNhan = diaChiNguoiNhan;
    }

    public String getTinhThanhNguoiNhan() {
        return tinhThanhNguoiNhan;
    }

    public void setTinhThanhNguoiNhan(String tinhThanhNguoiNhan) {
        this.tinhThanhNguoiNhan = tinhThanhNguoiNhan;
    }

    public String getGhiChuDonHang() {
        return ghiChuDonHang;
    }

    public void setGhiChuDonHang(String ghiChuDonHang) {
        this.ghiChuDonHang = ghiChuDonHang;
    }

    public int getMadonhang() {
        return madonhang;
    }

    public void setMadonhang(int madonhang) {
        this.madonhang = madonhang;
    }

    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    // Tạo các Constructor, Getter và Setter tại đây...
    public int getMatrangthai() {
        return matrangthai;
    }

    public void setMatrangthai(int matrangthai) {
        this.matrangthai = matrangthai;
    }

    public int getMagiohang() {
        return magiohang;
    }

    public void setMagiohang(int magiohang) {
        this.magiohang = magiohang;
    }

    public String getMakhachhang() {
        return makhachhang;
    }

    public void setMakhachhang(String makhachhang) {
        this.makhachhang = makhachhang;
    }

    public String getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(String masanpham) {
        this.masanpham = masanpham;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public String getKichco() {
        return kichco;
    }

    public void setKichco(String kichco) {
        this.kichco = kichco;
    }

    public int getGiaban() {
        return giaban;
    }

    public void setGiaban(int giaban) {
        this.giaban = giaban;
    }

    public Date getNgaymua() {
        return ngaymua;
    }

    public void setNgaymua(Date ngaymua) {
        this.ngaymua = ngaymua;
    }

    public int getTinhtrang() {
        return tinhtrang;
    }

    public void setTinhtrang(int tinhtrang) {
        this.tinhtrang = tinhtrang;
    }
}
