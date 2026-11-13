package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.DoanhThu;

public class DoanhThuDAO {

    // Hiển thị toàn bộ doanh thu
    public List<DoanhThu> getDoanhThuTheoNgay() {

        List<DoanhThu> list = new ArrayList<>();

        String sql = "SELECT "
                + "CAST(ngaymua AS DATE) AS NgayBan, "
                + "COUNT(matrangthai) AS TongSoDonHang, "
                + "SUM(soluong) AS TongSoLuongBan, "
                + "SUM(soluong * giaban) AS TongDoanhThuNgay "
                + "FROM trangthai "
                + "WHERE tinhtrang = 2 "
                + "GROUP BY CAST(ngaymua AS DATE) "
                + "ORDER BY NgayBan DESC";

        try (
                Connection con = dbconnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                DoanhThu dt = new DoanhThu();

                dt.setNgayBan(rs.getDate("NgayBan"));
                dt.setTongSoDonHang(rs.getInt("TongSoDonHang"));
                dt.setTongSoLuongBan(rs.getInt("TongSoLuongBan"));
                dt.setTongDoanhThuNgay(rs.getLong("TongDoanhThuNgay"));

                list.add(dt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lọc theo khoảng ngày
    public List<DoanhThu> getDoanhThuTheoNgay(String tuNgay, String denNgay) {

        List<DoanhThu> list = new ArrayList<>();

        String sql = "SELECT "
                + "CAST(ngaymua AS DATE) AS NgayBan, "
                + "COUNT(matrangthai) AS TongSoDonHang, "
                + "SUM(soluong) AS TongSoLuongBan, "
                + "SUM(soluong * giaban) AS TongDoanhThuNgay "
                + "FROM trangthai "
                + "WHERE tinhtrang = 2 "
                + "AND CAST(ngaymua AS DATE) BETWEEN ? AND ? "
                + "GROUP BY CAST(ngaymua AS DATE) "
                + "ORDER BY NgayBan DESC";

        try (
                Connection con = dbconnect.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tuNgay);
            ps.setString(2, denNgay);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                DoanhThu dt = new DoanhThu();

                dt.setNgayBan(rs.getDate("NgayBan"));
                dt.setTongSoDonHang(rs.getInt("TongSoDonHang"));
                dt.setTongSoLuongBan(rs.getInt("TongSoLuongBan"));
                dt.setTongDoanhThuNgay(rs.getLong("TongDoanhThuNgay"));

                list.add(dt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}