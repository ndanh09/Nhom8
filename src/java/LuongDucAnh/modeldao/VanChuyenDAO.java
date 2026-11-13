package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.TrangThai;
import model.VanChuyen;

public class VanChuyenDAO {

    // ================== TÀI KHOẢN VẬN CHUYỂN (chỉ ADMIN được tạo) ==================
    //
    // GHI CHÚ: Người vận chuyển KHÔNG được tự đăng ký tài khoản. Tài khoản
    // trong bảng "vanchuyen" chỉ do admin tạo trực tiếp (xem dangKy bên
    // dưới), sau đó admin tự gửi thông tin đăng nhập (email/mật khẩu) cho
    // người vận chuyển.

    public boolean checkEmailTonTai(String email) {
        String sql = "SELECT 1 FROM vanchuyen WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Admin tạo trực tiếp 1 tài khoản vận chuyển (không qua hồ sơ ứng tuyển),
     * kèm ảnh CCCD + ảnh xác minh để lưu hồ sơ nhân viên.
     *
     * @return true nếu tạo thành công, false nếu có lỗi (vd: DB thiếu cột
     *         anhcccd/anhxacminh do chưa chạy migration_vanchuyen.sql mới).
     */
    public boolean dangKy(VanChuyen vc) {
        String sql = "INSERT INTO vanchuyen (email, hoten, matkhau, sodienthoai, khuvuc, anhcccd, anhxacminh, ngaydangky, trangthai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, CONVERT(VARCHAR(10), GETDATE(), 23), N'Đang hoạt động')";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vc.getEmail());
            ps.setString(2, vc.getHoten());
            ps.setString(3, vc.getMatkhau());
            ps.setString(4, vc.getSodienthoai());
            ps.setString(5, vc.getKhuvuc());
            ps.setString(6, vc.getAnhcccd());
            ps.setString(7, vc.getAnhxacminh());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public VanChuyen dangNhap(String email, String matkhau) {
        // Tài khoản đang bị admin khóa thì không cho đăng nhập.
        String sql = "SELECT * FROM vanchuyen WHERE email = ? AND matkhau = ? AND trangthai <> N'Khóa'";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, matkhau);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return anhXaTaiKhoan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tra cứu lại thông tin tài khoản vận chuyển theo email (không kiểm tra
     * mật khẩu). Dùng để lấy khuvuc mới nhất mỗi khi vào trang quản lý giao
     * hàng, tránh trường hợp dùng khuvuc cũ đã lưu trong session từ lúc
     * đăng nhập nếu admin vừa đổi khu vực của tài khoản này.
     */
    public VanChuyen timTheoEmail(String email) {
        String sql = "SELECT * FROM vanchuyen WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return anhXaTaiKhoan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================== NGHIỆP VỤ GIAO HÀNG ==================

    private TrangThai anhXa(ResultSet rs) throws SQLException {
        TrangThai tt = new TrangThai();
        tt.setMatrangthai(rs.getInt("matrangthai"));
        tt.setMagiohang(rs.getInt("magiohang"));
        tt.setMakhachhang(rs.getString("makhachhang"));
        tt.setMasanpham(rs.getString("masanpham"));
        tt.setSoluong(rs.getInt("soluong"));
        tt.setKichco(rs.getString("kichco"));
        tt.setGiaban(rs.getInt("giaban"));
        tt.setNgaymua(rs.getTimestamp("ngaymua"));
        tt.setTinhtrang(rs.getInt("tinhtrang"));
        tt.setMadonhang(rs.getInt("madonhang"));
        tt.setTensanpham(rs.getString("tensanpham"));
        tt.setEmailVanChuyen(rs.getString("emailvanchuyen"));
        tt.setHotenNguoiNhan(rs.getString("hoten"));
        tt.setSdtNguoiNhan(rs.getString("sodienthoai"));
        tt.setDiaChiNguoiNhan(rs.getString("diachi"));
        tt.setTinhThanhNguoiNhan(rs.getString("tinhthanh"));
        tt.setGhiChuDonHang(rs.getString("ghichu"));
        return tt;
    }

    private static final String SELECT_BASE
            = "SELECT t.matrangthai, t.magiohang, t.makhachhang, t.masanpham, t.soluong, t.kichco, "
            + "t.giaban, t.ngaymua, t.tinhtrang, t.madonhang, t.emailvanchuyen, "
            + "nd.tensanpham, dh.hoten, dh.sodienthoai, dh.diachi, dh.tinhthanh, dh.ghichu "
            + "FROM trangthai t "
            + "LEFT JOIN nguoidung nd ON nd.masanpham = t.masanpham "
            + "LEFT JOIN donhang dh ON dh.madonhang = t.madonhang ";

    /**
     * Danh sách đơn đang chờ giao (tinhtrang = 1: Đang giao), CHƯA có ai
     * nhận, và thuộc đúng tỉnh/thành (khuvuc) mà người vận chuyển đang phụ
     * trách — mỗi tài khoản vận chuyển chỉ thấy/được "bắt" đơn ở tỉnh/thành
     * của mình, không thấy đơn ở tỉnh/thành khác.
     *
     * @param khuvuc tỉnh/thành người vận chuyển phụ trách (vanchuyen.khuvuc).
     *               Nếu null/rỗng (tài khoản chưa được gán khu vực) thì trả
     *               về danh sách rỗng, tránh lộ đơn của tỉnh/thành khác.
     */
    public List<TrangThai> danhSachDonCanGiao(String khuvuc) {
        List<TrangThai> list = new ArrayList<>();
        if (khuvuc == null || khuvuc.trim().isEmpty()) {
            return list;
        }
        String sql = SELECT_BASE
                + "WHERE t.tinhtrang = 1 AND t.emailvanchuyen IS NULL AND dh.tinhthanh = ? "
                + "ORDER BY t.ngaymua ASC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, khuvuc.trim());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(anhXa(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Danh sách đơn người vận chuyển này đang phụ trách: gồm cả đơn đang giao
     * (1) và đơn đã giao xong nhưng còn chờ khách hàng xác nhận (4).
     */
    public List<TrangThai> donDangGiaoCuaToi(String email) {
        List<TrangThai> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE t.tinhtrang IN (1,4) AND t.emailvanchuyen = ? ORDER BY t.ngaymua ASC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(anhXa(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lịch sử các đơn người vận chuyển này đã giao xong (2 - Đã nhận) hoặc
     * giao thất bại/hoàn trả (3 - Hủy) trong khi đơn đó thuộc trách nhiệm của
     * họ.
     */
    public List<TrangThai> lichSuGiaoHang(String email) {
        List<TrangThai> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE t.emailvanchuyen = ? AND t.tinhtrang IN (2,3) ORDER BY t.ngaymua DESC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(anhXa(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Người vận chuyển "bắt đơn": chỉ thành công nếu đơn đang ở trạng thái
     * Đang giao (1) và CHƯA có người vận chuyển nào khác nhận trước.
     */
    public boolean nhanDon(int matrangthai, String email) {
        String sql = "UPDATE trangthai SET emailvanchuyen = ? "
                + "WHERE matrangthai = ? AND tinhtrang = 1 AND emailvanchuyen IS NULL" + TrangThaiDAO.DK_KHONG_KHOA;
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, matrangthai);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Người vận chuyển hủy nhận đơn (trả đơn lại cho danh sách chung), chỉ
     * cho phép khi đơn đó đúng là do họ đang giữ.
     */
    public boolean traLaiDon(int matrangthai, String email) {
        String sql = "UPDATE trangthai SET emailvanchuyen = NULL "
                + "WHERE matrangthai = ? AND tinhtrang = 1 AND emailvanchuyen = ?" + TrangThaiDAO.DK_KHONG_KHOA;
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matrangthai);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đã bỏ hàm xacNhanDaGiao(matrangthai, email) kiểu cũ (chuyển thẳng
    // tinhtrang 1 -> 2 "Đã nhận" và trừ kho luôn): làm vậy khiến đơn bị đánh
    // dấu "Đã nhận" mà KHÁCH HÀNG chưa hề tự xác nhận. Nay tách thành 2 bước
    // riêng biệt:
    //   - Người vận chuyển: xác nhận GIAO XONG (1 -> 4 "Đã giao", KHÔNG trừ
    //     kho ở bước này) — xem hàm capNhatDaGiao() bên dưới.
    //   - Khách hàng: tự xác nhận ĐÃ NHẬN (4 -> 2 "Đã nhận", lúc này mới trừ
    //     kho) — xem TrangThaiDAO.khachXacNhanDaNhan().

    /**
     * Người vận chuyển xác nhận ĐÃ GIAO tới nơi: chuyển tinhtrang 1 -> 4 (Đã
     * giao, chờ khách hàng xác nhận). KHÔNG trừ kho ở bước này vì kho chỉ nên
     * trừ khi khách hàng thật sự xác nhận đã nhận hàng (tránh trừ kho nhầm
     * nếu khách từ chối nhận). Chỉ cho phép nếu đơn đang thuộc quyền xử lý
     * của chính người vận chuyển này.
     */
    public boolean capNhatDaGiao(int matrangthai, String email) {
        String sql = "UPDATE trangthai SET tinhtrang = 4 "
                + "WHERE matrangthai = ? AND tinhtrang = 1 AND emailvanchuyen = ?" + TrangThaiDAO.DK_KHONG_KHOA;
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matrangthai);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Người vận chuyển báo GIAO THẤT BẠI / khách từ chối nhận hàng: chuyển
     * tinhtrang sang 3 (Hủy đơn).
     *
     * FIX: bản cũ ghi chú "đơn chưa từng bị trừ kho nên không cần hoàn kho" - SAI: kho đã bị trừ ngay
     * lúc thanh toán (GioHangDAO.truTonKho, gọi từ DonHangServlet). Nên giao thất bại phải hoàn lại
     * kho (và lượt mã giảm giá nếu cả hóa đơn đã hủy) giống đường khách tự hủy đơn.
     */
    public boolean baoGiaoThatBai(int matrangthai, String email) {
        String sqlGet = "SELECT masanpham, soluong, madonhang FROM trangthai "
                + "WHERE matrangthai = ? AND tinhtrang = 1 AND emailvanchuyen = ?";
        String sql = "UPDATE trangthai SET tinhtrang = 3 "
                + "WHERE matrangthai = ? AND tinhtrang = 1 AND emailvanchuyen = ?" + TrangThaiDAO.DK_KHONG_KHOA;
        try (Connection conn = dbconnect.getConnection()) {
            String maSanPham;
            int soLuong;
            int maDonHang;
            try (PreparedStatement psGet = conn.prepareStatement(sqlGet)) {
                psGet.setInt(1, matrangthai);
                psGet.setString(2, email);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    maSanPham = rs.getString("masanpham");
                    soLuong = rs.getInt("soluong");
                    maDonHang = rs.getInt("madonhang");
                }
            }

            boolean ok;
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, matrangthai);
                    ps.setString(2, email);
                    ok = ps.executeUpdate() > 0;
                }
                if (ok) {
                    TrangThaiDAO.hoanKho(conn, maSanPham, soLuong);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }

            if (ok) {
                new TrangThaiDAO().hoanLaiMaGiamGiaNeuHetHieuLuc(maDonHang);
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================== QUẢN LÝ TÀI KHOẢN VẬN CHUYỂN (ADMIN) ==================

    private VanChuyen anhXaTaiKhoan(ResultSet rs) throws SQLException {
        VanChuyen vc = new VanChuyen();
        vc.setEmail(rs.getString("email"));
        vc.setHoten(rs.getString("hoten"));
        vc.setMatkhau(rs.getString("matkhau"));
        vc.setSodienthoai(rs.getString("sodienthoai"));
        vc.setKhuvuc(rs.getString("khuvuc"));
        vc.setNgaydangky(rs.getString("ngaydangky"));
        vc.setTrangthai(rs.getString("trangthai"));
        vc.setAnhcccd(rs.getString("anhcccd"));
        vc.setAnhxacminh(rs.getString("anhxacminh"));
        return vc;
    }

    public List<VanChuyen> danhSachTaiKhoan() {
        return danhSachTaiKhoan(null);
    }

    /**
     * Danh sách tài khoản vận chuyển, có thể lọc theo từ khóa (tên, email,
     * số điện thoại hoặc khu vực) — dùng cho ô tìm kiếm ở trang admin.
     */
    public List<VanChuyen> danhSachTaiKhoan(String keyword) {
        List<VanChuyen> list = new ArrayList<>();
        String sql = "SELECT * FROM vanchuyen "
                + (keyword != null && !keyword.trim().isEmpty()
                        ? "WHERE hoten LIKE ? OR email LIKE ? OR sodienthoai LIKE ? OR khuvuc LIKE ? " : "")
                + "ORDER BY ngaydangky DESC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(anhXaTaiKhoan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Admin đổi tỉnh/thành (khu vực giao hàng) phụ trách của 1 tài khoản
     * vận chuyển, ví dụ khi tài khoản đó đang có khuvuc dạng tự do cũ (từ
     * trước khi có dropdown chọn tỉnh/thành) hoặc cần điều chuyển địa bàn.
     */
    public boolean capNhatKhuVuc(String email, String khuvucMoi) {
        String sql = "UPDATE vanchuyen SET khuvuc = ? WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, khuvucMoi);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Khóa / Mở khóa tài khoản vận chuyển (không xóa hẳn, tránh vỡ khóa ngoại
     * emailvanchuyen ở bảng trangthai đối với các đơn đã từng gán cho họ).
     */
    public boolean capNhatTrangThaiTaiKhoan(String email, String trangthai) {
        String sql = "UPDATE vanchuyen SET trangthai = ? WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangthai);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
