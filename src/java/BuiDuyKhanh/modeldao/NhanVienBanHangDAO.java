package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.NhanVienBanHang;

/**
 * Quản lý tài khoản nhân viên bán hàng (chỉ ADMIN được tạo / sửa / khóa).
 * Không xóa hẳn tài khoản, chỉ khóa / mở khóa.
 */
public class NhanVienBanHangDAO {

    private NhanVienBanHang anhXa(ResultSet rs) throws SQLException {
        NhanVienBanHang nv = new NhanVienBanHang();
        nv.setEmail(rs.getString("email"));
        nv.setHoten(rs.getString("hoten"));
        nv.setMatkhau(rs.getString("matkhau"));
        nv.setSodienthoai(rs.getString("sodienthoai"));
        nv.setNgaydangky(rs.getString("ngaydangky"));
        nv.setTrangthai(rs.getString("trangthai"));
        nv.setAnhcccd(rs.getString("anhcccd"));
        return nv;
    }

    public boolean checkEmailTonTai(String email) {
        String sql = "SELECT 1 FROM nhanvienbanhang WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean them(NhanVienBanHang nv) {
        String sql = "INSERT INTO nhanvienbanhang (email, hoten, matkhau, sodienthoai, anhcccd, ngaydangky, trangthai) "
                + "VALUES (?, ?, ?, ?, ?, CONVERT(VARCHAR(10), GETDATE(), 23), N'Đang hoạt động')";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getEmail());
            ps.setString(2, nv.getHoten());
            ps.setString(3, nv.getMatkhau());
            ps.setString(4, nv.getSodienthoai());
            ps.setString(5, nv.getAnhcccd());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sửa thông tin. matkhauMoi / anhcccdMoi để null hoặc rỗng = giữ nguyên giá trị cũ.
     */
    public boolean sua(String email, String hoten, String sodienthoai, String matkhauMoi, String anhcccdMoi) {
        boolean doiMk = matkhauMoi != null && !matkhauMoi.trim().isEmpty();
        boolean doiAnh = anhcccdMoi != null && !anhcccdMoi.trim().isEmpty();
        String sql = "UPDATE nhanvienbanhang SET hoten = ?, sodienthoai = ?"
                + (doiMk ? ", matkhau = ?" : "")
                + (doiAnh ? ", anhcccd = ?" : "")
                + " WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, hoten);
            ps.setString(i++, sodienthoai);
            if (doiMk) {
                ps.setString(i++, matkhauMoi);
            }
            if (doiAnh) {
                ps.setString(i++, anhcccdMoi);
            }
            ps.setString(i, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatTrangThai(String email, String trangthai) {
        String sql = "UPDATE nhanvienbanhang SET trangthai = ? WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangthai);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public NhanVienBanHang timTheoEmail(String email) {
        String sql = "SELECT * FROM nhanvienbanhang WHERE email = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return anhXa(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Dùng khi sau này cho nhân viên đăng nhập: tài khoản bị khóa thì không đăng nhập được. */
    public NhanVienBanHang dangNhap(String email, String matkhau) {
        String sql = "SELECT * FROM nhanvienbanhang WHERE email = ? AND matkhau = ? AND trangthai <> N'Khóa'";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, matkhau);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return anhXa(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NhanVienBanHang> danhSach(String keyword) {
        List<NhanVienBanHang> list = new ArrayList<>();
        boolean coTim = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT * FROM nhanvienbanhang "
                + (coTim ? "WHERE hoten LIKE ? OR email LIKE ? OR sodienthoai LIKE ? " : "")
                + "ORDER BY ngaydangky DESC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (coTim) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
            }
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
