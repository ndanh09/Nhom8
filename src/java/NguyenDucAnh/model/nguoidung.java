package model;

public class nguoidung {

    private String masanpham;
    private String ngayban; 
    private String tensanpham;
    private String hinhanh; // <--- THÊM THUỘC TÍNH LƯU ẢNH Ở ĐÂY
    private int soluongsanpham;
    private String kichco;
    private int giasanpham;
    private String trangthai;
    private String danhmuc;

    // Constructor có đầy đủ tham số (đã cập nhật hinhanh, danhmuc)
    public nguoidung(String masanpham, String ngayban, String tensanpham, String hinhanh, int soluongsanpham, String kichco, int giasanpham, String trangthai, String danhmuc) {
        this.masanpham = masanpham;
        this.ngayban = ngayban;
        this.tensanpham = tensanpham;
        this.hinhanh = hinhanh; // <--- Cập nhật vào hàm tạo
        this.soluongsanpham = soluongsanpham;
        this.kichco = kichco;
        this.giasanpham = giasanpham;
        this.trangthai = trangthai;
        this.danhmuc = danhmuc;
    }

    // Constructor không tham số
    public nguoidung() {
    }

    public String getDanhmuc() {
        return danhmuc;
    }

    public void setDanhmuc(String danhmuc) {
        this.danhmuc = danhmuc;
    }

    // GETTER & SETTER CHO HÌNH ẢNH (BỔ SUNG MỚI)
    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    // CÁC HÀM GETTER & SETTER CÒN LẠI GIỮ NGUYÊN
    public String getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(String masanpham) {
        this.masanpham = masanpham;
    }

    public String getNgayban() {
        return ngayban;
    }

    public void setNgayban(String ngayban) {
        this.ngayban = ngayban;
    }

    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
    }

    public int getSoluongsanpham() {
        return soluongsanpham;
    }

    public void setSoluongsanpham(int soluongsanpham) {
        this.soluongsanpham = soluongsanpham;
    }

    public String getKichco() {
        return kichco;
    }

    public void setKichco(String kichco) {
        this.kichco = kichco;
    }

    public int getGiasanpham() {
        return giasanpham;
    }

    public void setGiasanpham(int giasanpham) {
        this.giasanpham = giasanpham;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}