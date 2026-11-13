package modeldao;

import connect.dbconnect;
import model.*;
import java.sql.*;

public class dangnhapdao {

    /**
     * Đăng nhập: kiểm tra email + mật khẩu + tài khoản chưa bị khóa, trong 1 lần query.
     * Giữ lại cho các nơi khác trong dự án còn đang gọi hàm này.
     */
    public dangkytaikhoan login(dangkytaikhoan tk) {
        String sql = "SELECT * FROM dangky WHERE email=? AND matkhau=? "
                + "AND (trangthai IS NULL OR trangthai <> N'Đã khóa')";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tk.getEmail());
            ps.setString(2, tk.getMatkhau());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Chỉ tìm theo email (không kèm mật khẩu).
     * Dùng để phân biệt lỗi "tài khoản không tồn tại" và "sai mật khẩu" ở servlet.
     */
    public dangkytaikhoan findByEmail(String email) {
        String sql = "SELECT * FROM dangky WHERE email=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private dangkytaikhoan mapResultSetToUser(ResultSet rs) throws SQLException {
        dangkytaikhoan user = new dangkytaikhoan();
        user.setEmail(rs.getString("email"));
        user.setHoten(rs.getString("hoten"));
        user.setMatkhau(rs.getString("matkhau"));
        user.setSodienthoai(rs.getString("sodienthoai"));
        user.setTrangthai(rs.getString("trangthai"));
        return user;
    }   
}