package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.DanhGia;
import model.ThongKeDanhGia;

/**
 * Truy cập dữ liệu module ĐÁNH GIÁ & NHẬN XÉT SẢN PHẨM (bảng danhgia).
 *
 * Quy ước:
 *  - Khách hàng được định danh bằng EMAIL (khớp bảng dangky / trangthai.makhachhang).
 *  - Sản phẩm được đánh giá theo MÃ GỐC (nguoiban.magoc) - gộp mọi size.
 *  - Đơn "giao thành công" = trangthai.tinhtrang = 2 (Đã nhận).
 *  - Mọi kiểm tra quyền đánh giá đều làm TRỰC TIẾP trên DB, không tin dữ liệu form.
 */
public class DanhGiaDAO {

    /** Số đơn "Đã nhận" trong bảng trangthai. */
    public static final int TINH_TRANG_DA_NHAN = 2;
    public static final int KICH_THUOC_TRANG_MAC_DINH = 10;
    public static final int TOI_DA_KY_TU_NOI_DUNG = 1000;

    // ---- Kết quả kiemTraDieuKienDanhGia ----
    public static final int DK_DU_DIEU_KIEN = 0;
    public static final int DK_SAN_PHAM_KHONG_TON_TAI = 1;
    public static final int DK_CHUA_MUA = 2;          // chưa có đơn nào chứa sản phẩm này
    public static final int DK_CHUA_GIAO = 3;         // có mua nhưng chưa đơn nào giao thành công
    public static final int DK_DA_DANH_GIA = 4;
    public static final int DK_LOI = 5;

    // ---- Kết quả themDanhGia ----
    public static final int THEM_OK = 1;
    public static final int THEM_KHONG_DU_DIEU_KIEN = 0;
    public static final int THEM_TRUNG = -1;
    public static final int THEM_KHONG_HOP_LE = -2;
    public static final int THEM_LOI = -3;

    private static final int SQL_LOI_TRUNG_KHOA_1 = 2627;
    private static final int SQL_LOI_TRUNG_KHOA_2 = 2601;
    private static final int SQL_LOI_CHECK = 547;

    /** Bộ lọc cho trang quản trị. Trường null/rỗng = không lọc. */
    public static class BoLoc {

        private String khachHang;
        private String sanPham;
        private Integer soSao;
        private Boolean hienThi;       // true = đang hiển thị, false = đã ẩn, null = tất cả
        private boolean moiNhat = true;

        public String getKhachHang() { return khachHang; }
        public BoLoc setKhachHang(String v) { this.khachHang = v; return this; }

        public String getSanPham() { return sanPham; }
        public BoLoc setSanPham(String v) { this.sanPham = v; return this; }

        public Integer getSoSao() { return soSao; }
        public BoLoc setSoSao(Integer v) { this.soSao = (v != null && v >= 1 && v <= 5) ? v : null; return this; }

        public Boolean getHienThi() { return hienThi; }
        public BoLoc setHienThi(Boolean v) { this.hienThi = v; return this; }

        public boolean isMoiNhat() { return moiNhat; }
        public BoLoc setMoiNhat(boolean v) { this.moiNhat = v; return this; }
    }

    // =====================================================================
    // Hỗ trợ nội bộ
    // =====================================================================
    private static final String JOIN_HIEN_THI
            = "FROM danhgia d "
            + "LEFT JOIN dangky k ON k.email = d.makhachhang "
            + "OUTER APPLY (SELECT TOP 1 n.masanpham, n.tensanpham, n.hinhanh "
            + "             FROM nguoiban n WHERE n.magoc = d.magoc ORDER BY n.masanpham) p ";

    private static final String COT_HIEN_THI
            = "SELECT d.madanhgia, d.makhachhang, d.magoc, d.sosao, d.noidung, d.trangthai, d.ngaydanhgia, "
            + "k.hoten AS tenkhach, p.masanpham AS masanphamdaidien, p.tensanpham, p.hinhanh ";

    private static final String DA_NHAN_EXISTS
            = "EXISTS (SELECT 1 FROM trangthai t JOIN nguoiban n ON n.masanpham = t.masanpham "
            + "        WHERE t.makhachhang = ? AND n.magoc = ? AND t.tinhtrang = " + TINH_TRANG_DA_NHAN + ")";

    private static String lamSach(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean rong(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Chuẩn hóa chuỗi cho LIKE: thoát các ký tự đặc biệt của SQL Server. */
    private static String likeArg(String s) {
        String x = s.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_").replace("[", "\\[");
        return "%" + x + "%";
    }

    private static DanhGia doc(ResultSet rs) throws SQLException {
        DanhGia dg = new DanhGia();
        dg.setMaDanhGia(rs.getInt("madanhgia"));
        dg.setMaKhachHang(rs.getString("makhachhang"));
        dg.setMaGoc(rs.getString("magoc"));
        int sao = rs.getInt("sosao");
        dg.setSoSao(rs.wasNull() ? null : Integer.valueOf(sao));
        dg.setNoiDung(rs.getString("noidung"));
        dg.setHienThi(rs.getBoolean("trangthai"));
        dg.setNgayDanhGia(rs.getTimestamp("ngaydanhgia"));
        dg.setTenKhachHang(rs.getString("tenkhach"));
        dg.setMaSanPhamDaiDien(rs.getString("masanphamdaidien"));
        dg.setTenSanPham(rs.getString("tensanpham"));
        dg.setHinhAnh(rs.getString("hinhanh"));
        return dg;
    }

    private static void gan(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object o = params.get(i);
            if (o instanceof Integer) {
                ps.setInt(i + 1, (Integer) o);
            } else if (o instanceof Boolean) {
                ps.setBoolean(i + 1, (Boolean) o);
            } else {
                ps.setString(i + 1, (String) o);
            }
        }
    }

    // =====================================================================
    // Tra cứu sản phẩm
    // =====================================================================
    /** Mã gốc của 1 mã sản phẩm (size). null nếu sản phẩm không tồn tại. */
    public String magocTuMaSanPham(String masanpham) {
        if (rong(masanpham)) {
            return null;
        }
        String sql = "SELECT magoc FROM nguoiban WHERE masanpham = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, masanpham.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String g = rs.getString(1);
                    return rong(g) ? null : g.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tên / ảnh / mã size đại diện của sản phẩm theo mã gốc. null nếu không tồn tại. */
    public DanhGia laySanPhamTheoMaGoc(String magoc) {
        if (rong(magoc)) {
            return null;
        }
        String sql = "SELECT TOP 1 masanpham, tensanpham, hinhanh FROM nguoiban WHERE magoc = ? ORDER BY masanpham";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, magoc.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DanhGia dg = new DanhGia();
                    dg.setMaGoc(magoc.trim());
                    dg.setMaSanPhamDaiDien(rs.getString("masanpham"));
                    dg.setTenSanPham(rs.getString("tensanpham"));
                    dg.setHinhAnh(rs.getString("hinhanh"));
                    return dg;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =====================================================================
    // Điều kiện đánh giá (kiểm tra từ DB)
    // =====================================================================
    /** Khách này đã đánh giá sản phẩm này chưa (kể cả đánh giá đã bị ẩn). */
    public boolean kiemTraDaDanhGia(String email, String magoc) {
        if (rong(email) || rong(magoc)) {
            return false;
        }
        String sql = "SELECT 1 FROM danhgia WHERE makhachhang = ? AND magoc = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, magoc.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra khách có được đánh giá sản phẩm không: sản phẩm tồn tại, đã mua,
     * có đơn giao thành công, và chưa đánh giá. Trả về 1 trong các hằng DK_*.
     */
    public int kiemTraDieuKienDanhGia(String email, String magoc) {
        if (rong(email) || rong(magoc)) {
            return DK_CHUA_MUA;
        }
        email = email.trim();
        magoc = magoc.trim();
        String sql = "SELECT "
                + " (SELECT COUNT(*) FROM nguoiban WHERE magoc = ?) AS tonTai, "
                + " (SELECT COUNT(*) FROM trangthai t JOIN nguoiban n ON n.masanpham = t.masanpham "
                + "   WHERE t.makhachhang = ? AND n.magoc = ? AND t.tinhtrang <> 3) AS daMua, "
                + " (SELECT COUNT(*) FROM trangthai t JOIN nguoiban n ON n.masanpham = t.masanpham "
                + "   WHERE t.makhachhang = ? AND n.magoc = ? AND t.tinhtrang = " + TINH_TRANG_DA_NHAN + ") AS daGiao, "
                + " (SELECT COUNT(*) FROM danhgia WHERE makhachhang = ? AND magoc = ?) AS daDanhGia";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, magoc);
            ps.setString(2, email);
            ps.setString(3, magoc);
            ps.setString(4, email);
            ps.setString(5, magoc);
            ps.setString(6, email);
            ps.setString(7, magoc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt("tonTai") == 0) {
                        return DK_SAN_PHAM_KHONG_TON_TAI;
                    }
                    if (rs.getInt("daDanhGia") > 0) {
                        return DK_DA_DANH_GIA;
                    }
                    if (rs.getInt("daGiao") > 0) {
                        return DK_DU_DIEU_KIEN;
                    }
                    return rs.getInt("daMua") > 0 ? DK_CHUA_GIAO : DK_CHUA_MUA;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DK_LOI;
    }

    /** Câu thông báo tương ứng với mã DK_* (null nếu đủ điều kiện). */
    public static String thongBaoDieuKien(int dk) {
        switch (dk) {
            case DK_DU_DIEU_KIEN:
                return null;
            case DK_SAN_PHAM_KHONG_TON_TAI:
                return "Sản phẩm không tồn tại hoặc đã bị gỡ khỏi cửa hàng.";
            case DK_CHUA_MUA:
                return "Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua sản phẩm này.";
            case DK_CHUA_GIAO:
                return "Đơn hàng chứa sản phẩm này chưa được giao thành công. Bạn có thể đánh giá sau khi nhận hàng.";
            case DK_DA_DANH_GIA:
                return "Bạn đã đánh giá sản phẩm này rồi. Mỗi sản phẩm chỉ được đánh giá một lần.";
            default:
                return "Không kiểm tra được điều kiện đánh giá, vui lòng thử lại sau.";
        }
    }

    // =====================================================================
    // Thêm đánh giá
    // =====================================================================
    /**
     * Thêm đánh giá. Việc kiểm tra "đã nhận hàng" nằm NGAY TRONG câu INSERT (INSERT ... SELECT ...
     * WHERE EXISTS) nên không thể lách bằng cách gửi mã khách/mã sản phẩm giả. Gửi trùng (kể cả
     * đồng thời) bị ràng buộc UNIQUE(makhachhang, magoc) của DB chặn.
     *
     * @return THEM_OK, THEM_KHONG_DU_DIEU_KIEN, THEM_TRUNG, THEM_KHONG_HOP_LE hoặc THEM_LOI
     */
    public int themDanhGia(DanhGia dg) {
        if (dg == null || rong(dg.getMaKhachHang()) || rong(dg.getMaGoc())) {
            return THEM_KHONG_HOP_LE;
        }
        Integer sao = dg.getSoSao();
        String noiDung = lamSach(dg.getNoiDung());
        if (noiDung != null && noiDung.isEmpty()) {
            noiDung = null;
        }
        if (sao != null && (sao < 1 || sao > 5)) {
            return THEM_KHONG_HOP_LE;
        }
        if (sao == null && noiDung == null) {
            return THEM_KHONG_HOP_LE;
        }
        if (noiDung != null && noiDung.length() > TOI_DA_KY_TU_NOI_DUNG) {
            return THEM_KHONG_HOP_LE;
        }

        String email = dg.getMaKhachHang().trim();
        String magoc = dg.getMaGoc().trim();
        String sql = "INSERT INTO danhgia (makhachhang, magoc, sosao, noidung, trangthai, ngaydanhgia) "
                + "SELECT ?, ?, ?, ?, 1, GETDATE() WHERE " + DA_NHAN_EXISTS;
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, magoc);
            if (sao == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, sao);
            }
            if (noiDung == null) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setString(4, noiDung);
            }
            ps.setString(5, email);
            ps.setString(6, magoc);
            return ps.executeUpdate() > 0 ? THEM_OK : THEM_KHONG_DU_DIEU_KIEN;
        } catch (SQLException e) {
            int code = e.getErrorCode();
            if (code == SQL_LOI_TRUNG_KHOA_1 || code == SQL_LOI_TRUNG_KHOA_2) {
                return THEM_TRUNG;
            }
            if (code == SQL_LOI_CHECK) {
                // In ra Output để biết ràng buộc nào (CHECK hay khóa ngoại) đã từ chối dòng này
                System.err.println("[DanhGia] Vi phạm ràng buộc (547): " + e.getMessage());
                return THEM_KHONG_HOP_LE;
            }
            e.printStackTrace();
            return THEM_LOI;
        } catch (Exception e) {
            e.printStackTrace();
            return THEM_LOI;
        }
    }

    // =====================================================================
    // Khách truy cập / khách hàng
    // =====================================================================
    /** Đánh giá CÔNG KHAI của 1 sản phẩm (chỉ loại đang hiển thị). soSao null = tất cả. */
    public ArrayList<DanhGia> layDanhGiaSanPham(String magoc, Integer soSao) {
        ArrayList<DanhGia> ds = new ArrayList<>();
        if (rong(magoc)) {
            return ds;
        }
        StringBuilder sql = new StringBuilder(COT_HIEN_THI).append(JOIN_HIEN_THI)
                .append("WHERE d.magoc = ? AND d.trangthai = 1 ");
        List<Object> params = new ArrayList<>();
        params.add(magoc.trim());
        if (soSao != null && soSao >= 1 && soSao <= 5) {
            sql.append("AND d.sosao = ? ");
            params.add(soSao);
        }
        sql.append("ORDER BY d.ngaydanhgia DESC, d.madanhgia DESC");
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            gan(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(doc(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** TOÀN BỘ đánh giá của 1 khách (gồm cả đánh giá đã bị ẩn), mới nhất trước. */
    public ArrayList<DanhGia> layDanhGiaCuaToi(String email) {
        ArrayList<DanhGia> ds = new ArrayList<>();
        if (rong(email)) {
            return ds;
        }
        String sql = COT_HIEN_THI + JOIN_HIEN_THI
                + "WHERE d.makhachhang = ? ORDER BY d.ngaydanhgia DESC, d.madanhgia DESC";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(doc(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // =====================================================================
    // Quản trị viên
    // =====================================================================
    private static String where(BoLoc loc, List<Object> params) {
        StringBuilder w = new StringBuilder("WHERE 1 = 1 ");
        if (loc != null) {
            if (!rong(loc.getKhachHang())) {
                w.append("AND k.hoten LIKE ? ESCAPE '\\' ");
                params.add(likeArg(loc.getKhachHang()));
            }
            if (!rong(loc.getSanPham())) {
                w.append("AND p.tensanpham LIKE ? ESCAPE '\\' ");
                params.add(likeArg(loc.getSanPham()));
            }
            if (loc.getSoSao() != null) {
                w.append("AND d.sosao = ? ");
                params.add(loc.getSoSao());
            }
            if (loc.getHienThi() != null) {
                w.append("AND d.trangthai = ? ");
                params.add(loc.getHienThi());
            }
        }
        return w.toString();
    }

    /** Danh sách quản trị mặc định: không lọc, mới nhất trước. */
    public ArrayList<DanhGia> layDanhSachDanhGiaAdmin(int trang, int kichThuoc) {
        return timKiemDanhGia(new BoLoc(), trang, kichThuoc);
    }

    /** Tìm kiếm + lọc + sắp xếp + phân trang (trang đánh số từ 1) cho trang quản trị. */
    public ArrayList<DanhGia> timKiemDanhGia(BoLoc loc, int trang, int kichThuoc) {
        ArrayList<DanhGia> ds = new ArrayList<>();
        if (kichThuoc < 1) {
            kichThuoc = KICH_THUOC_TRANG_MAC_DINH;
        }
        if (trang < 1) {
            trang = 1;
        }
        boolean moi = loc == null || loc.isMoiNhat();
        List<Object> params = new ArrayList<>();
        String sql = COT_HIEN_THI + JOIN_HIEN_THI + where(loc, params)
                + "ORDER BY d.ngaydanhgia " + (moi ? "DESC" : "ASC") + ", d.madanhgia " + (moi ? "DESC" : "ASC")
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        params.add((trang - 1) * kichThuoc);
        params.add(kichThuoc);
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            gan(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(doc(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Tổng số đánh giá thỏa bộ lọc - phục vụ phân trang trên tập đã lọc. */
    public int demTongDanhGia(BoLoc loc) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT COUNT(*) " + JOIN_HIEN_THI + where(loc, params);
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            gan(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Chi tiết 1 đánh giá (cho admin). null nếu không tồn tại. */
    public DanhGia layDanhGiaTheoMa(int maDanhGia) {
        String sql = COT_HIEN_THI + JOIN_HIEN_THI + "WHERE d.madanhgia = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maDanhGia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return doc(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ẩn (hienThi = false) hoặc hiện lại (true) 1 đánh giá. CHỈ đổi trạng thái, không đụng tới
     * số sao / nội dung và không xóa dữ liệu. Servlet phải kiểm tra quyền Admin trước khi gọi.
     */
    public boolean capNhatTrangThai(int maDanhGia, boolean hienThi) {
        String sql = "UPDATE danhgia SET trangthai = ? WHERE madanhgia = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, hienThi);
            ps.setInt(2, maDanhGia);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =====================================================================
    // Thống kê
    // =====================================================================
    /** Thống kê toàn hệ thống (trang quản trị). */
    public ThongKeDanhGia thongKeDanhGia() {
        return thongKe(null);
    }

    /** Thống kê công khai của 1 sản phẩm (cùng nguyên tắc tính với trang quản trị). */
    public ThongKeDanhGia thongKeSanPham(String magoc) {
        return thongKe(rong(magoc) ? "__khong_co_san_pham__" : magoc.trim());
    }

    private ThongKeDanhGia thongKe(String magoc) {
        ThongKeDanhGia tk = new ThongKeDanhGia();
        String sql = "SELECT "
                + " SUM(CASE WHEN trangthai = 1 THEN 1 ELSE 0 END) AS tong, "
                + " SUM(CASE WHEN trangthai = 1 AND sosao IS NOT NULL THEN 1 ELSE 0 END) AS cosao, "
                + " SUM(CASE WHEN trangthai = 1 AND sosao IS NOT NULL THEN CAST(sosao AS BIGINT) ELSE 0 END) AS tongsao, "
                + " SUM(CASE WHEN trangthai = 0 THEN 1 ELSE 0 END) AS an, "
                + " SUM(CASE WHEN trangthai = 1 AND sosao = 1 THEN 1 ELSE 0 END) AS s1, "
                + " SUM(CASE WHEN trangthai = 1 AND sosao = 2 THEN 1 ELSE 0 END) AS s2, "
                + " SUM(CASE WHEN trangthai = 1 AND sosao = 3 THEN 1 ELSE 0 END) AS s3, "
                + " SUM(CASE WHEN trangthai = 1 AND sosao = 4 THEN 1 ELSE 0 END) AS s4, "
                + " SUM(CASE WHEN trangthai = 1 AND sosao = 5 THEN 1 ELSE 0 END) AS s5 "
                + "FROM danhgia" + (magoc == null ? "" : " WHERE magoc = ?");
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (magoc != null) {
                ps.setString(1, magoc);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tk.setTongLuotDanhGia(rs.getInt("tong"));
                    tk.setTongLuotChamSao(rs.getInt("cosao"));
                    tk.setTongSoSao(rs.getLong("tongsao"));
                    tk.setSoDaAn(rs.getInt("an"));
                    for (int s = 1; s <= 5; s++) {
                        tk.setSoLuongSao(s, rs.getInt("s" + s));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}
