package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.LuongVanChuyen;

/**
 * DAO xử lý nghiệp vụ LƯƠNG THƯỞNG cho người vận chuyển.
 *
 * Quy tắc (theo yêu cầu):
 *   - Lương cố định: 5.000.000đ / người / tháng.
 *   - Hoa hồng: 10% trên giá trị hóa đơn của MỖI ĐƠN người đó giao thành
 *     công (tính theo giaban*soluong từng dòng trong bảng trangthai đã ở
 *     trạng thái tinhtrang = 2 "Đã nhận" — cùng cách tính đang dùng ở module
 *     Doanh thu, xem DoanhThuDAO).
 *   - Tổng lương = Lương cố định + Hoa hồng.
 *
 * Ghi chú: bảng trangthai không có cột riêng lưu "ngày giao xong", nên mốc
 * thời gian dùng để gộp theo tháng/năm là ngaymua (ngày khách đặt đơn).
 */
public class LuongVanChuyenDAO {

    public static final long LUONG_CO_BAN = 5_000_000L;
    public static final double TI_LE_HOA_HONG = 0.10;

    /**
     * Tính (real-time, CHƯA lưu DB) lương của 1 người vận chuyển trong 1
     * tháng/năm, dựa trên dữ liệu hiện có ở bảng trangthai.
     */
    public LuongVanChuyen tinhLuongTheoThang(String email, int thang, int nam) {
        LuongVanChuyen lvc = new LuongVanChuyen();
        lvc.setEmail(email);
        lvc.setThang(thang);
        lvc.setNam(nam);
        lvc.setLuongcoban(LUONG_CO_BAN);
        lvc.setTrangthai("Chưa chốt");
        lvc.setDaChot(false);

        String sql = "SELECT COUNT(DISTINCT madonhang) AS sodon, "
                + "ISNULL(SUM(giaban * soluong), 0) AS tonghd "
                + "FROM trangthai "
                + "WHERE emailvanchuyen = ? AND tinhtrang = 2 "
                + "AND MONTH(ngaymua) = ? AND YEAR(ngaymua) = ?";

        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lvc.setSodonhoanthanh(rs.getInt("sodon"));
                lvc.setTonghoadon(rs.getLong("tonghd"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long hoaHong = Math.round(lvc.getTonghoadon() * TI_LE_HOA_HONG);
        lvc.setHoahong(hoaHong);
        lvc.setTongluong(LUONG_CO_BAN + hoaHong);
        return lvc;
    }

    /**
     * Danh sách lương của TẤT CẢ người vận chuyển trong 1 tháng/năm — dùng
     * cho trang admin. Với mỗi người: nếu tháng đó ĐÃ CHỐT thì lấy đúng số
     * liệu đã chốt (đóng băng) trong bảng luongvanchuyen; nếu CHƯA CHỐT thì
     * trả về số liệu tạm tính real-time để admin xem trước.
     */
    public List<LuongVanChuyen> tinhLuongTatCaTheoThang(int thang, int nam) {
        return tinhLuongTatCaTheoThang(thang, nam, null);
    }

    /**
     * Giống tinhLuongTatCaTheoThang(thang, nam) nhưng có thêm tìm kiếm theo
     * tên hoặc email nhân viên vận chuyển (dùng cho ô tìm kiếm ở trang
     * admin quản lý lương). keyword rỗng/null thì trả về toàn bộ.
     */
    public List<LuongVanChuyen> tinhLuongTatCaTheoThang(int thang, int nam, String keyword) {
        List<LuongVanChuyen> list = new ArrayList<>();

        String sqlNv = "SELECT email, hoten FROM vanchuyen "
                + (keyword != null && !keyword.trim().isEmpty()
                        ? "WHERE hoten LIKE ? OR email LIKE ? " : "")
                + "ORDER BY hoten";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sqlNv)) {

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String email = rs.getString("email");
                String hoten = rs.getString("hoten");

                LuongVanChuyen daChot = timBanGhiDaChot(email, thang, nam);

                if (daChot != null) {
                    daChot.setHoten(hoten);
                    list.add(daChot);
                } else {
                    LuongVanChuyen tam = tinhLuongTheoThang(email, thang, nam);
                    tam.setHoten(hoten);
                    list.add(tam);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private LuongVanChuyen anhXa(ResultSet rs) throws SQLException {
        LuongVanChuyen lvc = new LuongVanChuyen();
        lvc.setMaluong(rs.getInt("maluong"));
        lvc.setEmail(rs.getString("email"));
        lvc.setThang(rs.getInt("thang"));
        lvc.setNam(rs.getInt("nam"));
        lvc.setLuongcoban(rs.getLong("luongcoban"));
        lvc.setSodonhoanthanh(rs.getInt("sodonhoanthanh"));
        lvc.setTonghoadon(rs.getLong("tonghoadon"));
        lvc.setHoahong(rs.getLong("hoahong"));
        lvc.setTongluong(rs.getLong("tongluong"));
        lvc.setTrangthai(rs.getString("trangthai"));
        lvc.setNgaychot(rs.getTimestamp("ngaychot"));
        lvc.setNgaythanhtoan(rs.getTimestamp("ngaythanhtoan"));
        lvc.setDaChot(true);
        return lvc;
    }

    public LuongVanChuyen timBanGhiDaChot(String email, int thang, int nam) {
        String sql = "SELECT * FROM luongvanchuyen WHERE email = ? AND thang = ? AND nam = ?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return anhXa(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Admin "chốt lương": tính toán số liệu tại thời điểm hiện tại rồi lưu
     * cố định vào bảng luongvanchuyen (đóng băng, không đổi theo dữ liệu
     * phát sinh về sau). Nếu tháng/năm đó đã chốt trước rồi thì bỏ qua
     * (không ghi đè, tránh mất số liệu nếu đã thanh toán).
     */
    public boolean chotLuong(String email, int thang, int nam) {
        if (timBanGhiDaChot(email, thang, nam) != null) {
            return true;
        }

        LuongVanChuyen tinh = tinhLuongTheoThang(email, thang, nam);

        String sql = "INSERT INTO luongvanchuyen "
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Đánh dấu 1 bản ghi lương đã chốt là ĐÃ THANH TOÁN cho người vận
     * chuyển.
     */
    public boolean danhDauDaThanhToan(int maluong) {
        String sql = "UPDATE luongvanchuyen SET trangthai = N'Đã thanh toán', ngaythanhtoan = GETDATE() "
                + "WHERE maluong = ? AND trangthai <> N'Đã thanh toán'";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maluong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lịch sử các tháng lương ĐÃ CHỐT của 1 người vận chuyển (dùng cho
     * chính người vận chuyển tự xem lương của mình).
     */
    public List<LuongVanChuyen> lichSuLuongCuaToi(String email) {
        List<LuongVanChuyen> list = new ArrayList<>();
        String sql = "SELECT * FROM luongvanchuyen WHERE email = ? ORDER BY nam DESC, thang DESC";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
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
}
