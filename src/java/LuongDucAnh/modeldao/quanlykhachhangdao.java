package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.quanlykhachhang;

/**
 * FIX (toàn file): trước đây Connection/PreparedStatement/ResultSet là FIELD của DAO (dùng chung
 * giữa mọi request vì DAO nằm trong field của servlet) và không bao giờ được đóng -> 2 request
 * đồng thời ghi đè conn/ps/rs của nhau (dữ liệu sai/lỗi), đồng thời rò rỉ kết nối. Nay mỗi hàm tự
 * mở và đóng tài nguyên cục bộ bằng try-with-resources.
 */
public class quanlykhachhangdao {

    //====================== HIỂN THỊ ======================

    public List<quanlykhachhang> getAllKhachHang() {
        return getAllKhachHang(null);
    }

    /**
     * Danh sách khách hàng (donhang), có thể lọc theo từ khóa (mã khách
     * hàng, họ tên, số điện thoại hoặc email) — dùng cho ô tìm kiếm ở trang
     * admin quản lý khách hàng.
     */
    public List<quanlykhachhang> getAllKhachHang(String keyword) {

        List<quanlykhachhang> list = new ArrayList<>();

        boolean coTuKhoa = keyword != null && !keyword.trim().isEmpty();

        String sql = "SELECT * FROM donhang "
                + (coTuKhoa
                        ? "WHERE makhachhang LIKE ? OR hoten LIKE ? OR sodienthoai LIKE ? OR email LIKE ? " : "")
                + "ORDER BY madonhang DESC";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (coTuKhoa) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
                ps.setString(4, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    quanlykhachhang kh = new quanlykhachhang();

                    kh.setMadonhang(rs.getInt("madonhang"));
                    kh.setMakhachhang(rs.getString("makhachhang"));
                    kh.setHoten(rs.getString("hoten"));
                    kh.setSodienthoai(rs.getString("sodienthoai"));
                    kh.setDiachi(rs.getString("diachi"));
                    kh.setEmail(rs.getString("email"));
                    kh.setGhichu(rs.getString("ghichu"));
                    kh.setPhuongthucthanhtoan(rs.getString("phuongthucthanhtoan"));
                    kh.setNgaydat(rs.getTimestamp("ngaydat"));
                    kh.setTongtien(rs.getInt("tongtien"));
                    kh.setMasanpham(rs.getString("masanpham"));
                    kh.setTrangthai(rs.getString("trangthai"));

                    list.add(kh);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //====================== TÌM THEO ID ======================

    public quanlykhachhang timTheoID(int id) {

        String sql = "SELECT * FROM donhang WHERE madonhang=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    quanlykhachhang kh = new quanlykhachhang();

                    kh.setMadonhang(rs.getInt("madonhang"));
                    kh.setMakhachhang(rs.getString("makhachhang"));
                    kh.setHoten(rs.getString("hoten"));
                    kh.setSodienthoai(rs.getString("sodienthoai"));
                    kh.setDiachi(rs.getString("diachi"));
                    kh.setEmail(rs.getString("email"));
                    kh.setGhichu(rs.getString("ghichu"));
                    kh.setPhuongthucthanhtoan(rs.getString("phuongthucthanhtoan"));
                    kh.setNgaydat(rs.getTimestamp("ngaydat"));
                    kh.setTongtien(rs.getInt("tongtien"));
                    kh.setMasanpham(rs.getString("masanpham"));

                    return kh;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //====================== CẬP NHẬT ======================

    public boolean capNhatKhachHang(quanlykhachhang kh) {

        String sql = "UPDATE donhang SET "
                + "makhachhang=?,"
                + "hoten=?,"
                + "sodienthoai=?,"
                + "diachi=?,"
                + "email=?,"
                + "ghichu=?,"
                + "phuongthucthanhtoan=?,"
                + "tongtien=?,"
                + "masanpham=? "
                + "WHERE madonhang=? AND ISNULL(trangthai, N'') <> N'Đã khóa'";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getMakhachhang());
            ps.setString(2, kh.getHoten());
            ps.setString(3, kh.getSodienthoai());
            ps.setString(4, kh.getDiachi());
            ps.setString(5, kh.getEmail());
            ps.setString(6, kh.getGhichu());
            ps.setString(7, kh.getPhuongthucthanhtoan());
            ps.setInt(8, kh.getTongtien());
            ps.setString(9, kh.getMasanpham());
            ps.setInt(10, kh.getMadonhang());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //====================== XÓA (không dùng nữa - giữ lại để tương thích) ======================

    public boolean xoaKhachHang(int id) {

        String sql = "DELETE FROM donhang WHERE madonhang=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //====================== KHÓA / MỞ KHÓA (thay cho xóa hẳn) ======================
    // trangthai: N'Đã khóa' hoặc N'Đang hoạt động'. Cần chạy migration thêm
    // cột trangthai NVARCHAR(20) DEFAULT N'Đang hoạt động' vào bảng donhang.

    public boolean capNhatTrangThai(int id, String trangthai) {

        String sql = "UPDATE donhang SET trangthai=? WHERE madonhang=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangthai);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
