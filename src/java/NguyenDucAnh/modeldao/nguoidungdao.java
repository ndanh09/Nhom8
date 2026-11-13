package modeldao;

import java.util.*;
import java.sql.*;
import connect.dbconnect;
import model.*;

public class nguoidungdao {

    public ArrayList<nguoidung> sp() {
        // FIX: list cục bộ cho từng lần gọi. Trước đây là field của DAO (dùng chung giữa mọi request
        // vì DAO nằm trong field của servlet) nên request này clear() list đang được request khác duyệt.
        ArrayList<nguoidung> listsp = new ArrayList<>();
        String sql = "SELECT * FROM nguoidung";
        Connection con = dbconnect.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Gọi dữ liệu bằng tên cột để tránh lỗi lệch thứ tự index
                String masanpham = rs.getString("masanpham");
                String ngayban = rs.getString("ngayban");
                String tensanpham = rs.getString("tensanpham");
                String hinhanh = rs.getString("hinhanh"); // <--- ĐỌC THÊM DỮ LIỆU CỘT ẢNH MỚI
                int soluongsanpham = rs.getInt("soluongsanpham");
                String kichco = rs.getString("kichco");
                int giasanpham = rs.getInt("giasanpham");
                String trangthai = rs.getString("trangthai");
                String danhmuc = rs.getString("danhmuc");

                // Truyền đầy đủ tham số (bao gồm cả hinhanh, danhmuc) vào Constructor của đối tượng nguoidung
                listsp.add(new nguoidung(masanpham, ngayban, tensanpham, hinhanh, soluongsanpham, kichco, giasanpham, trangthai, danhmuc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Khuyên dùng: Đóng kết nối để tránh tràn bộ nhớ (Connection leak)
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listsp;
    }

    // Lấy chi tiết 1 sản phẩm theo mã sản phẩm
    public nguoidung layTheoMa(String masanpham) {
        nguoidung sp = null;
        String sql = "SELECT * FROM nguoidung WHERE masanpham = ?";
        Connection con = dbconnect.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, masanpham);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sp = new nguoidung(
                        rs.getString("masanpham"),
                        rs.getString("ngayban"),
                        rs.getString("tensanpham"),
                        rs.getString("hinhanh"),
                        rs.getInt("soluongsanpham"),
                        rs.getString("kichco"),
                        rs.getInt("giasanpham"),
                        rs.getString("trangthai"),
                        rs.getString("danhmuc")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sp;
    }

    // Lấy tất cả các biến thể (size khác nhau) của cùng 1 sản phẩm theo tên sản phẩm
    public ArrayList<nguoidung> layTheoTen(String tensanpham) {
        ArrayList<nguoidung> list = new ArrayList<>();
        String sql = "SELECT * FROM nguoidung WHERE tensanpham = ? ORDER BY kichco";
        Connection con = dbconnect.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tensanpham);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new nguoidung(
                        rs.getString("masanpham"),
                        rs.getString("ngayban"),
                        rs.getString("tensanpham"),
                        rs.getString("hinhanh"),
                        rs.getInt("soluongsanpham"),
                        rs.getString("kichco"),
                        rs.getInt("giasanpham"),
                        rs.getString("trangthai"),
                        rs.getString("danhmuc")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}