package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import model.DonHangGanNhanVien;
import model.LuongNhanVienBanHang;

/**
 * DAO xử lý LƯƠNG cho nhân viên bán hàng (làm giống module lương vận chuyển).
 *
 * Quy tắc:
 *   - Lương cố định: 7.000.000đ / người / tháng.
 *   - Hoa hồng: 10% giá trị MỖI ĐƠN mà nhân viên phụ trách (donhang.emailnhanvienbanhang)
 *     và đã ở trạng thái "Đã nhận" (trangthai.tinhtrang = 2) - cùng cách tính với module
 *     Doanh thu và lương vận chuyển: giaban * soluong từng dòng sản phẩm.
 *   - Tổng lương = Lương cố định + Hoa hồng.
 *
 * Mốc tháng/năm lấy theo trangthai.ngaymua (ngày khách đặt), giống LuongVanChuyenDAO.
 * Đơn chưa gán nhân viên thì không ai được hoa hồng.
 */
public class LuongNhanVienBanHangDAO {

    public static final long LUONG_CO_BAN = 7_000_000L;
    public static final int TI_LE_HOA_HONG_PHAN_TRAM = 10;

    /** 10% làm tròn đến đồng, tính bằng số nguyên để không bị sai số số thực. */
    private static long tinhHoaHong(long tongHoaDon) {
        return (tongHoaDon * TI_LE_HOA_HONG_PHAN_TRAM + 50) / 100;
    }

    /** Đã chạy migration_luong_nhanvienbanhang.sql chưa (có cột donhang.emailnhanvienbanhang + bảng lương). */
    public boolean daChayMigration() {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'donhang' AND COLUMN_NAME = 'emailnhanvienbanhang') + "
                + "(SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'luongnhanvienbanhang')";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) >= 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------------------------------------ TÍNH LƯƠNG

    /** Tính (real-time, CHƯA lưu DB) lương của 1 nhân viên trong 1 tháng/năm. */
    public LuongNhanVienBanHang tinhLuongTheoThang(String email, int thang, int nam) {
        LuongNhanVienBanHang l = new LuongNhanVienBanHang();
        l.setEmail(email);
        l.setThang(thang);
        l.setNam(nam);
        l.setLuongcoban(LUONG_CO_BAN);
        l.setTrangthai("Chưa chốt");
        l.setDaChot(false);

        String sql = "SELECT COUNT(DISTINCT t.madonhang) AS sodon, "
                + "ISNULL(SUM(CAST(t.giaban AS BIGINT) * t.soluong), 0) AS tonghd "
                + "FROM trangthai t JOIN donhang d ON d.madonhang = t.madonhang "
                + "WHERE d.emailnhanvienbanhang = ? AND t.tinhtrang = 2 "
                + "AND MONTH(t.ngaymua) = ? AND YEAR(t.ngaymua) = ?";

        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    l.setSodonhoanthanh(rs.getInt("sodon"));
                    l.setTonghoadon(rs.getLong("tonghd"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long hoaHong = tinhHoaHong(l.getTonghoadon());
        l.setHoahong(hoaHong);
        l.setTongluong(LUONG_CO_BAN + hoaHong);
        return l;
    }

    /** Nhân viên đăng ký SAU tháng đang xem thì tháng đó chưa đi làm -> không hiện dòng lương. */
    private static boolean chuaVaoLamTrongThang(String ngaydangky, int thang, int nam) {
        if (ngaydangky == null || ngaydangky.length() < 7) {
            return false;
        }
        try {
            int y = Integer.parseInt(ngaydangky.substring(0, 4));   // định dạng yyyy-MM-dd
            int m = Integer.parseInt(ngaydangky.substring(5, 7));
            return y * 12 + m > nam * 12 + thang;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Bảng lương của TẤT CẢ nhân viên bán hàng trong 1 tháng/năm (trang admin).
     * Tháng đã CHỐT -> lấy đúng số liệu đã chốt; chưa chốt -> tạm tính real-time.
     * Tài khoản đã khóa chỉ hiện nếu tháng đó đã có bản ghi chốt (khỏi tính lương cho người đã nghỉ).
     */
    public List<LuongNhanVienBanHang> tinhLuongTatCaTheoThang(int thang, int nam, String keyword) {
        boolean coTim = keyword != null && !keyword.trim().isEmpty();
        String sqlNv = "SELECT email, hoten, ngaydangky FROM nhanvienbanhang "
                + "WHERE (trangthai <> N'Khóa' OR email IN "
                + "(SELECT email FROM luongnhanvienbanhang WHERE thang = ? AND nam = ?)) "
                + (coTim ? "AND (hoten LIKE ? OR email LIKE ?) " : "")
                + "ORDER BY hoten";

        // Đọc danh sách nhân viên xong, đóng kết nối rồi mới tính từng người (tránh giữ nhiều kết nối cùng lúc).
        List<String[]> dsNv = new ArrayList<>();
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sqlNv)) {
            int i = 1;
            ps.setInt(i++, thang);
            ps.setInt(i++, nam);
            if (coTim) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(i++, kw);
                ps.setString(i++, kw);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsNv.add(new String[]{rs.getString("email"), rs.getString("hoten"), rs.getString("ngaydangky")});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<LuongNhanVienBanHang> list = new ArrayList<>();
        for (String[] nv : dsNv) {
            LuongNhanVienBanHang daChot = timBanGhiDaChot(nv[0], thang, nam);
            if (daChot != null) {
                daChot.setHoten(nv[1]);
                list.add(daChot);
            } else if (!chuaVaoLamTrongThang(nv[2], thang, nam)) {
                LuongNhanVienBanHang tam = tinhLuongTheoThang(nv[0], thang, nam);
                tam.setHoten(nv[1]);
                list.add(tam);
            }
        }
        return list;
    }

    // ------------------------------------------------------------------ CHỐT / THANH TOÁN

    private LuongNhanVienBanHang anhXa(ResultSet rs) throws SQLException {
        LuongNhanVienBanHang l = new LuongNhanVienBanHang();
        l.setMaluong(rs.getInt("maluong"));
        l.setEmail(rs.getString("email"));
        l.setThang(rs.getInt("thang"));
        l.setNam(rs.getInt("nam"));
        l.setLuongcoban(rs.getLong("luongcoban"));
        l.setSodonhoanthanh(rs.getInt("sodonhoanthanh"));
        l.setTonghoadon(rs.getLong("tonghoadon"));
        l.setHoahong(rs.getLong("hoahong"));
        l.setTongluong(rs.getLong("tongluong"));
        l.setTrangthai(rs.getString("trangthai"));
        l.setNgaychot(rs.getTimestamp("ngaychot"));
        l.setNgaythanhtoan(rs.getTimestamp("ngaythanhtoan"));
        l.setDaChot(true);
        return l;
    }

    public LuongNhanVienBanHang timBanGhiDaChot(String email, int thang, int nam) {
        String sql = "SELECT * FROM luongnhanvienbanhang WHERE email = ? AND thang = ? AND nam = ?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return anhXa(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Admin "chốt lương": tính số liệu tại thời điểm hiện tại rồi lưu cố định vào bảng
     * luongnhanvienbanhang. Tháng đã chốt rồi thì không ghi đè (tránh mất số liệu nếu đã trả lương).
     */
    public boolean chotLuong(String email, int thang, int nam) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (timBanGhiDaChot(email, thang, nam) != null) {
            return true;
        }
        LuongNhanVienBanHang tinh = tinhLuongTheoThang(email, thang, nam);
        String sql = "INSERT INTO luongnhanvienbanhang "
                + "(email, thang, nam, luongcoban, sodonhoanthanh, tonghoadon, hoahong, tongluong, trangthai, ngaychot) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, N'Chưa thanh toán', GETDATE())";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ps.setLong(4, tinh.getLuongcoban());
            ps.setInt(5, tinh.getSodonhoanthanh());
            ps.setLong(6, tinh.getTonghoadon());
            ps.setLong(7, tinh.getHoahong());
            ps.setLong(8, tinh.getTongluong());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Đánh dấu 1 bản ghi lương đã chốt là ĐÃ THANH TOÁN. */
    public boolean danhDauDaThanhToan(int maluong) {
        String sql = "UPDATE luongnhanvienbanhang SET trangthai = N'Đã thanh toán', ngaythanhtoan = GETDATE() "
                + "WHERE maluong = ? AND trangthai <> N'Đã thanh toán'";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maluong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------------------------------------ GÁN ĐƠN CHO NHÂN VIÊN

    /**
     * Các đơn hàng (gộp theo mã đơn) đặt trong tháng/năm, kèm nhân viên đang phụ trách.
     * Ẩn đơn mà mọi sản phẩm đều đã bị hủy (không còn gì để tính hoa hồng).
     */
    public List<DonHangGanNhanVien> danhSachDonTrongThang(int thang, int nam) {
        List<DonHangGanNhanVien> list = new ArrayList<>();
        String sql = "SELECT d.madonhang, d.hoten, d.emailnhanvienbanhang, MIN(t.ngaymua) AS ngaymua, "
                + "ISNULL(SUM(CASE WHEN t.tinhtrang <> 3 THEN CAST(t.giaban AS BIGINT) * t.soluong ELSE 0 END), 0) AS tongdon, "
                + "ISNULL(SUM(CASE WHEN t.tinhtrang = 2 THEN CAST(t.giaban AS BIGINT) * t.soluong ELSE 0 END), 0) AS danhan "
                + "FROM donhang d JOIN trangthai t ON t.madonhang = d.madonhang "
                + "WHERE MONTH(t.ngaymua) = ? AND YEAR(t.ngaymua) = ? "
                + "GROUP BY d.madonhang, d.hoten, d.emailnhanvienbanhang "
                + "HAVING SUM(CASE WHEN t.tinhtrang <> 3 THEN 1 ELSE 0 END) > 0 "
                + "ORDER BY d.madonhang DESC";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonHangGanNhanVien dh = new DonHangGanNhanVien();
                    dh.setMadonhang(rs.getInt("madonhang"));
                    dh.setHoten(rs.getString("hoten"));
                    dh.setEmailNhanVien(rs.getString("emailnhanvienbanhang"));
                    dh.setNgaymua(rs.getTimestamp("ngaymua"));
                    dh.setTongdon(rs.getLong("tongdon"));
                    dh.setDanhan(rs.getLong("danhan"));
                    list.add(dh);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private boolean daChot(Connection con, String email, int thang, int nam) throws SQLException {
        if (email == null) {
            return false;
        }
        String sql = "SELECT 1 FROM luongnhanvienbanhang WHERE email = ? AND thang = ? AND nam = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Gán đơn hàng cho nhân viên bán hàng (emailMoi rỗng = bỏ gán).
     *
     * @return null nếu thành công, ngược lại là thông báo lỗi để hiện cho admin.
     * Không cho đổi nếu nhân viên cũ hoặc mới đã được CHỐT lương tháng của đơn đó
     * (số liệu đã chốt không đổi theo, đổi gán sẽ làm lệch lương).
     */
    public String ganNhanVien(int maDonHang, String emailMoi) {
        String moi = (emailMoi == null || emailMoi.trim().isEmpty()) ? null : emailMoi.trim();

        try (Connection con = dbconnect.getConnection()) {
            // Đơn có tồn tại không, đang gán cho ai, thuộc tháng nào.
            String cu = null;
            Timestamp ngay = null;
            String sqlDon = "SELECT d.emailnhanvienbanhang AS cu, MIN(t.ngaymua) AS ngaymua "
                    + "FROM donhang d JOIN trangthai t ON t.madonhang = d.madonhang "
                    + "WHERE d.madonhang = ? GROUP BY d.emailnhanvienbanhang";
            try (PreparedStatement ps = con.prepareStatement(sqlDon)) {
                ps.setInt(1, maDonHang);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return "Không tìm thấy đơn hàng #ORD-" + maDonHang + ".";
                    }
                    cu = rs.getString("cu");
                    ngay = rs.getTimestamp("ngaymua");
                }
            }

            if (moi == null ? cu == null : moi.equals(cu)) {
                return null;   // không có gì thay đổi
            }

            if (moi != null) {
                String sqlNv = "SELECT trangthai FROM nhanvienbanhang WHERE email = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlNv)) {
                    ps.setString(1, moi);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            return "Không tìm thấy nhân viên bán hàng " + moi + ".";
                        }
                        if ("Khóa".equals(rs.getString("trangthai"))) {
                            return "Tài khoản " + moi + " đang bị khóa nên không thể gán đơn.";
                        }
                    }
                }
            }

            if (ngay != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(ngay.getTime());
                int thang = cal.get(Calendar.MONTH) + 1;
                int nam = cal.get(Calendar.YEAR);
                if (daChot(con, cu, thang, nam) || daChot(con, moi, thang, nam)) {
                    return "Lương tháng " + thang + "/" + nam + " của nhân viên liên quan đã được chốt nên không đổi gán đơn #ORD-" + maDonHang + " được nữa.";
                }
            }

            String sqlUpd = "UPDATE donhang SET emailnhanvienbanhang = ? WHERE madonhang = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpd)) {
                if (moi == null) {
                    ps.setNull(1, java.sql.Types.NVARCHAR);
                } else {
                    ps.setString(1, moi);
                }
                ps.setInt(2, maDonHang);
                return ps.executeUpdate() > 0 ? null : "Không cập nhật được đơn #ORD-" + maDonHang + ".";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Có lỗi khi gán đơn hàng. Kiểm tra đã chạy migration_luong_nhanvienbanhang.sql chưa.";
        }
    }
}
