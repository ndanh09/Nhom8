package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.quantrivien;

public class quantriviendao {

    // Lấy danh sách quản trị viên
    public List<quantrivien> getAll() {
        return getAll(null);
    }

    // Lấy danh sách quản trị viên, có thể lọc theo từ khóa (tên hoặc email)
    public List<quantrivien> getAll(String keyword) {
        // FIX: list cục bộ (trước đây là field dùng chung giữa các request -> dữ liệu lẫn nhau khi truy cập đồng thời)
        List<quantrivien> listqtv = new ArrayList<>();
        String sql = "SELECT * FROM quantrivien "
                + (keyword != null && !keyword.trim().isEmpty()
                        ? "WHERE hoten LIKE ? OR email LIKE ? " : "");
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listqtv.add(new quantrivien(
                            rs.getString("hoten"),
                            rs.getString("email"),
                            rs.getString("sodienthoai"),
                            rs.getString("ngaydangky"),
                            rs.getString("matkhau"),
                            rs.getString("trangthai")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listqtv;
    }

    // Thêm
    public void them(quantrivien qtv) {
        String sql = "INSERT INTO quantrivien(hoten,email,sodienthoai,ngaydangky,matkhau) VALUES(?,?,?,?,?)";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, qtv.getHoten());
            ps.setString(2, qtv.getEmail());
            ps.setString(3, qtv.getSodienthoai());
            ps.setString(4, qtv.getNgaydangky());
            ps.setString(5, qtv.getMatkhau());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra email tồn tại (chỉ boolean)
    public boolean check(String email) {
        String sql = "SELECT email FROM quantrivien WHERE email=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy đầy đủ thông tin quản trị viên theo email (dùng để phân biệt lỗi đăng nhập)
    public quantrivien findByEmail(String email) {
        String sql = "SELECT * FROM quantrivien WHERE email=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new quantrivien(
                            rs.getString("hoten"),
                            rs.getString("email"),
                            rs.getString("sodienthoai"),
                            rs.getString("ngaydangky"),
                            rs.getString("matkhau"),
                            rs.getString("trangthai")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Sửa
    public void sua(quantrivien qtv) {
        String sql = "UPDATE quantrivien SET hoten=?, sodienthoai=?, ngaydangky=?, matkhau=? WHERE email=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, qtv.getHoten());
            ps.setString(2, qtv.getSodienthoai());
            ps.setString(3, qtv.getNgaydangky());
            ps.setString(4, qtv.getMatkhau());
            ps.setString(5, qtv.getEmail());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa (không dùng nữa - giữ lại để tương thích)
    public void xoa(String email) {
        String sql = "DELETE FROM quantrivien WHERE email=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khóa / Mở khóa tài khoản quản trị viên (không xóa hẳn dữ liệu)
    public boolean capNhatTrangThai(String email, String trangthai) {
        String sql = "UPDATE quantrivien SET trangthai=? WHERE email=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trangthai);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dangNhap(String email, String matkhau) {
        // Tài khoản đang bị khóa thì không cho đăng nhập.
        String sql = "SELECT * FROM quantrivien WHERE email=? AND matkhau=? AND (trangthai IS NULL OR trangthai <> N'Đã khóa')";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, matkhau);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}