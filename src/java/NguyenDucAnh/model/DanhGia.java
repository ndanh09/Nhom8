package model;

import java.sql.Timestamp;

/**
 * 1 đánh giá / nhận xét của khách hàng về 1 sản phẩm (bảng danhgia).
 * Sản phẩm được tính theo MÃ GỐC (magoc) - gộp mọi size của cùng 1 áo.
 *
 * soSao  = null  -> khách chỉ viết nhận xét.
 * noiDung = null -> khách chỉ chấm sao.
 */
public class DanhGia {

    private int maDanhGia;
    private String maKhachHang;      // email khách hàng
    private String maGoc;            // mã gốc sản phẩm
    private Integer soSao;           // 1..5 hoặc null
    private String noiDung;          // nhận xét hoặc null
    private boolean hienThi = true;  // true = đang hiển thị, false = đã bị admin ẩn
    private Timestamp ngayDanhGia;

    // ---- Dữ liệu ghép từ bảng khác, chỉ để hiển thị ----
    private String tenKhachHang;
    private String tenSanPham;
    private String hinhAnh;
    private String maSanPhamDaiDien; // masanpham 1 size đại diện, dùng làm link sang trang chi tiết

    public DanhGia() {
    }

    public DanhGia(String maKhachHang, String maGoc, Integer soSao, String noiDung) {
        this.maKhachHang = maKhachHang;
        this.maGoc = maGoc;
        this.soSao = soSao;
        this.noiDung = noiDung;
    }

    /** Có chấm sao hay không. */
    public boolean isCoSao() {
        return soSao != null;
    }

    /** Có nội dung nhận xét (sau khi bỏ khoảng trắng) hay không. */
    public boolean isCoNoiDung() {
        return noiDung != null && !noiDung.trim().isEmpty();
    }

    public int getMaDanhGia() { return maDanhGia; }
    public void setMaDanhGia(int maDanhGia) { this.maDanhGia = maDanhGia; }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getMaGoc() { return maGoc; }
    public void setMaGoc(String maGoc) { this.maGoc = maGoc; }

    public Integer getSoSao() { return soSao; }
    public void setSoSao(Integer soSao) { this.soSao = soSao; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public boolean isHienThi() { return hienThi; }
    public void setHienThi(boolean hienThi) { this.hienThi = hienThi; }

    public Timestamp getNgayDanhGia() { return ngayDanhGia; }
    public void setNgayDanhGia(Timestamp ngayDanhGia) { this.ngayDanhGia = ngayDanhGia; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getMaSanPhamDaiDien() { return maSanPhamDaiDien; }
    public void setMaSanPhamDaiDien(String maSanPhamDaiDien) { this.maSanPhamDaiDien = maSanPhamDaiDien; }
}
