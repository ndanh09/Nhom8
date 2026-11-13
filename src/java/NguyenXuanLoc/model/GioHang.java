package model;

public class GioHang {
    private int magiohang;      // Mã dòng tự tăng trong SQL Server
    private String makhachhang; // Mã tài khoản người mua (mặc định là GUEST)
    private String masanpham;   // Mã sản phẩm
    private String tensanpham;  // FIX: Tên sản phẩm - dùng để tra các size khác nhau của cùng 1 sản phẩm bên bảng nguoidung
    private int soluong;        // Số lượng mua
    private String kichco;      // Kích cỡ (Size) của sản phẩm
    private int giaban;         // Giá bán tại thời điểm mua
    private int tonKho;         // Số lượng còn lại trong kho (để hiển thị & giới hạn ở giỏ hàng)

    // Constructor không tham số (Bắt buộc phải có trong Java Bean)
    public GioHang() {
    }

    // Constructor đầy đủ tham số để khởi tạo nhanh đối tượng
    public GioHang(String makhachhang, String masanpham, int soluong, String kichco, int giaban) {
        this.makhachhang = makhachhang;
        this.masanpham = masanpham;
        this.soluong = soluong;
        this.kichco = kichco;
        this.giaban = giaban;
    }

    // Toàn bộ các hàm Getter và Setter để các file Servlet/DAO có thể gọi lấy dữ liệu
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

    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
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

    // FIX: Mỗi size của 1 sản phẩm là 1 dòng riêng trong bảng nguoidung
    // (masanpham là khoá chính), nên masanpham thực tế lưu dạng
    // "<mã gốc>-<size>", vd "ARG1-XL". Cột "Kích cỡ" ở giỏ hàng đã hiển thị
    // size riêng rồi, nên hàm này trả về ĐÚNG mã gốc (bỏ đúng hậu tố
    // "-<size>" ở cuối, dựa theo giá trị kichco thật của dòng này) để hiển
    // thị ở cột "Mã sản phẩm", tránh lặp lại size 2 lần.
    // Chỉ dùng để HIỂN THỊ - các chỗ khác (tạo đơn, trừ tồn kho...) vẫn
    // phải dùng getMasanpham() (mã đầy đủ, đúng khoá chính trong DB).
    public String getMaSanPhamGoc() {

        if (masanpham == null) {
            return masanpham;
        }

        if (kichco != null && !kichco.isEmpty()) {

            String hauTo = "-" + kichco;

            if (masanpham.endsWith(hauTo)) {
                return masanpham.substring(0, masanpham.length() - hauTo.length());
            }
        }

        return masanpham;
    }

    public int getGiaban() {
        return giaban;
    }

    public void setGiaban(int giaban) {
        this.giaban = giaban;
    }

    public int getTonKho() {
        return tonKho;
    }

    public void setTonKho(int tonKho) {
        this.tonKho = tonKho;
    }
}