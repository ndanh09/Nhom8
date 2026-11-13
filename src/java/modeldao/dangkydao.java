package modeldao;
import java.util.*;
import model.*;
import connect.dbconnect;
import java.sql.*;

/**
 * FIX (toàn file): trước đây mọi hàm mở Connection bằng dbconnect.getConnection() nhưng KHÔNG
 * bao giờ đóng -> rò rỉ 1 kết nối SQL Server mỗi lần đăng nhập/đăng ký/khóa tài khoản...,
 * lâu dần hết kết nối. Danh sách kết quả cũng là field của DAO (listdktk) trong khi DAO được
 * giữ làm field của servlet và dùng chung cho mọi request -> các request chạy đồng thời
 * clear()/add() lẫn vào nhau. Nay dùng try-with-resources và list cục bộ cho từng lần gọi.
 */
public class dangkydao {

    public ArrayList<dangkytaikhoan> dktk(){
        return dktk(null);
    }

    // Danh sách tài khoản khách hàng, có thể lọc theo từ khóa (tên hoặc email)
    public ArrayList<dangkytaikhoan> dktk(String keyword){
        ArrayList<dangkytaikhoan> list = new ArrayList<>();
        boolean coTuKhoa = keyword != null && !keyword.trim().isEmpty();
        // Lấy rõ tên cột kể cả diachi để tránh lệch cột khi bảng có thêm cột mới
        String sql = "SELECT email, hoten, matkhau, sodienthoai, diachi, trangthai FROM dangky "
                + (coTuKhoa ? "WHERE hoten LIKE ? OR email LIKE ? " : "");
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (coTuKhoa) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dangkytaikhoan kh = new dangkytaikhoan(
                            rs.getString("email"),
                            rs.getString("hoten"),
                            rs.getString("matkhau"),
                            rs.getString("sodienthoai"),
                            rs.getString("diachi"));
                    kh.setTrangthai(rs.getString("trangthai"));
                    list.add(kh);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // FIX: trả về boolean (trước đây void và nuốt lỗi nên servlet luôn báo "đăng ký thành công").
    public boolean dk(dangkytaikhoan dktk){
        // Khai báo rõ tên cột (bao gồm diachi) thay vì INSERT INTO dangky VALUES(?,?,?,?)
        String sql = "INSERT INTO dangky (email, hoten, matkhau, sodienthoai, diachi) VALUES(?,?,?,?,?)";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dktk.getEmail());
            ps.setString(2, dktk.getHoten());
            ps.setString(3, dktk.getMatkhau());
            ps.setString(4, dktk.getSodienthoai());
            ps.setString(5, dktk.getDiachi()); // Có thể null nếu form không nhập
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // FIX: kiểm tra trùng email bằng 1 câu truy vấn (trước đây nạp CẢ BẢNG tài khoản vào list dùng
    // chung rồi duyệt, và NPE nếu email null).
    public boolean check(String email){
        if (email == null) {
            return false;
        }
        String sql = "SELECT 1 FROM dangky WHERE LOWER(email) = LOWER(?)";
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

    // Xóa tài khoản khách hàng theo email (không dùng nữa - giữ lại để tương thích)
    public void xoa(String email){
        String sql = "DELETE FROM dangky WHERE email = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khóa / Mở khóa tài khoản khách hàng (không xóa hẳn dữ liệu, có thể mở lại)
    public boolean capNhatTrangThai(String email, String trangthai){
        String sql = "UPDATE dangky SET trangthai = ? WHERE email = ?";
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

    // Lấy thông tin đã lưu của khách hàng (họ tên, sđt, địa chỉ) để tự điền lúc thanh toán
    public dangkytaikhoan layThongTin(String email){
        String sql = "SELECT email, hoten, matkhau, sodienthoai, diachi FROM dangky WHERE email = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new dangkytaikhoan(
                            rs.getString("email"),
                            rs.getString("hoten"),
                            rs.getString("matkhau"),
                            rs.getString("sodienthoai"),
                            rs.getString("diachi")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lưu lại thông tin khách hàng (họ tên, sđt, địa chỉ) sau khi đặt hàng
    // để lần thanh toán sau tự điền sẵn, khách vẫn có thể sửa nếu muốn.
    public void capNhatThongTinKhachHang(String email, String hoten, String sodienthoai, String diachi){
        String sql = "UPDATE dangky SET hoten = ?, sodienthoai = ?, diachi = ? WHERE email = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hoten);
            ps.setString(2, sodienthoai);
            ps.setString(3, diachi);
            ps.setString(4, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
