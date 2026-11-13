package model;

public class nguoiban {

    private String masanpham;
    private String magoc;
    private String tensanpham;
    private String hinhanh; 
    private int soluongsanpham;
    private String kichco;
    private int giasanpham;
    private String ngayban;
    private String trangthai;
    private String danhmuc;

    // Hàm khởi tạo đầy đủ tham số (Có hình ảnh, có danh mục, có mã gốc)
    public nguoiban(String masanpham, String magoc, String tensanpham, String hinhanh, int soluongsanpham, String kichco, int giasanpham, String ngayban, String trangthai, String danhmuc) {
        this.masanpham = masanpham;
        this.magoc = magoc;
        this.tensanpham = tensanpham;
        this.hinhanh = hinhanh;
        this.soluongsanpham = soluongsanpham;
        this.kichco = kichco;
        this.giasanpham = giasanpham;
        this.ngayban = ngayban;
        this.trangthai = trangthai;
        this.danhmuc = danhmuc;
    }

    // Hàm khởi tạo mặc định trống
    public nguoiban() {
    }

    public String getMagoc() {
        return magoc;
    }

    public void setMagoc(String magoc) {
        this.magoc = magoc;
    }

    public String getDanhmuc() {
        return danhmuc;
    }

    public void setDanhmuc(String danhmuc) {
        this.danhmuc = danhmuc;
    }

    // Các hàm Getter và Setter
    public String getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(String masanpham) {
        this.masanpham = masanpham;
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

    public String getNgayban() {
        return ngayban;
    }

    public void setNgayban(String ngayban) {
        this.ngayban = ngayban;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}