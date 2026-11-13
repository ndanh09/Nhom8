package model;

// Đại diện cho 1 sản phẩm sau khi đã GỘP tất cả các size lại làm 1 dòng.
// Ví dụ: mu1-l, mu1-xl, mu1-xxl (3 dòng trong bảng nguoiban) sẽ được gộp
// thành 1 dòng "nhomsanpham" duy nhất với magoc = "mu1".
// Dùng cho trang Quản lý sản phẩm (danh sách chính, không hiện từng size nữa).
public class nhomsanpham {

    private String magoc;
    private String tensanpham;
    private String hinhanh;
    private String danhmuc;
    private int giasanpham;
    private String ngayban;
    private String trangthai;
    private int tongsoluong;   // tổng số lượng tồn cộng dồn của TẤT CẢ size
    private int sosize;        // số lượng size đang có cho sản phẩm này
    private String dssize;     // danh sách size, ví dụ "S, M, L" - để hiển thị nhanh

    public nhomsanpham() {
    }

    public nhomsanpham(String magoc, String tensanpham, String hinhanh, String danhmuc,
            int giasanpham, String ngayban, String trangthai,
            int tongsoluong, int sosize, String dssize) {
        this.magoc = magoc;
        this.tensanpham = tensanpham;
        this.hinhanh = hinhanh;
        this.danhmuc = danhmuc;
        this.giasanpham = giasanpham;
        this.ngayban = ngayban;
        this.trangthai = trangthai;
        this.tongsoluong = tongsoluong;
        this.sosize = sosize;
        this.dssize = dssize;
    }

    public String getMagoc() {
        return magoc;
    }

    public void setMagoc(String magoc) {
        this.magoc = magoc;
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

    public String getDanhmuc() {
        return danhmuc;
    }

    public void setDanhmuc(String danhmuc) {
        this.danhmuc = danhmuc;
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

    public int getTongsoluong() {
        return tongsoluong;
    }

    public void setTongsoluong(int tongsoluong) {
        this.tongsoluong = tongsoluong;
    }

    public int getSosize() {
        return sosize;
    }

    public void setSosize(int sosize) {
        this.sosize = sosize;
    }

    public String getDssize() {
        return dssize;
    }

    public void setDssize(String dssize) {
        this.dssize = dssize;
    }
}
