package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.PhieuGiamGia;

public class PhieuGiamGiaDAO {

    // ===========================
    // LẤY DANH SÁCH PHIẾU GIẢM GIÁ
    // ===========================
    public ArrayList<PhieuGiamGia> getDanhSachPhieuGiamGia() {
        return getDanhSachPhieuGiamGia(null);
    }

    /**
     * Danh sách phiếu giảm giá, có thể lọc theo từ khóa (mã hoặc tên phiếu)
     * — dùng cho ô tìm kiếm ở trang admin quản lý mã giảm giá.
     */
    public ArrayList<PhieuGiamGia> getDanhSachPhieuGiamGia(String keyword) {

        ArrayList<PhieuGiamGia> list = new ArrayList<>();

        String sql = "SELECT * FROM phieugiamgia "
                + (keyword != null && !keyword.trim().isEmpty()
                        ? "WHERE magiamgia LIKE ? OR tenphieu LIKE ? " : "")
                + "ORDER BY magiamgia";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                PhieuGiamGia p = new PhieuGiamGia();

                p.setMagiamgia(rs.getString("magiamgia"));
                p.setTenphieu(rs.getString("tenphieu"));
                p.setLoaigiam(rs.getInt("loaigiam"));
                p.setGiatrigiam(rs.getInt("giatrigiam"));
                p.setGiamtoida(rs.getInt("giamtoida"));
                p.setDieukien(rs.getInt("dieukien"));
                p.setSoluong(rs.getInt("soluong"));
                p.setNgaybatdau(rs.getDate("ngaybatdau"));
                p.setNgayketthuc(rs.getDate("ngayketthuc"));
                p.setTrangthai(rs.getBoolean("trangthai"));

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===========================
    // LẤY 1 PHIẾU GIẢM GIÁ
    // ===========================
    public PhieuGiamGia getPhieuGiamGia(String maGiamGia) {

        String sql = "SELECT * FROM phieugiamgia WHERE magiamgia=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGiamGia);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                PhieuGiamGia p = new PhieuGiamGia();

                p.setMagiamgia(rs.getString("magiamgia"));
                p.setTenphieu(rs.getString("tenphieu"));
                p.setLoaigiam(rs.getInt("loaigiam"));
                p.setGiatrigiam(rs.getInt("giatrigiam"));
                p.setGiamtoida(rs.getInt("giamtoida"));
                p.setDieukien(rs.getInt("dieukien"));
                p.setSoluong(rs.getInt("soluong"));
                p.setNgaybatdau(rs.getDate("ngaybatdau"));
                p.setNgayketthuc(rs.getDate("ngayketthuc"));
                p.setTrangthai(rs.getBoolean("trangthai"));

                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }    // ===========================
    // THÊM PHIẾU GIẢM GIÁ
    // ===========================
    public boolean themPhieuGiamGia(PhieuGiamGia p) {

        String sql = "INSERT INTO phieugiamgia "
                + "(magiamgia,tenphieu,loaigiam,giatrigiam,giamtoida,dieukien,soluong,ngaybatdau,ngayketthuc,trangthai) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getMagiamgia());
            ps.setString(2, p.getTenphieu());
            ps.setInt(3, p.getLoaigiam());
            ps.setInt(4, p.getGiatrigiam());
            ps.setInt(5, p.getGiamtoida());
            ps.setInt(6, p.getDieukien());
            ps.setInt(7, p.getSoluong());
            ps.setDate(8, p.getNgaybatdau());
            ps.setDate(9, p.getNgayketthuc());
            ps.setBoolean(10, p.isTrangthai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===========================
    // SỬA PHIẾU GIẢM GIÁ
    // ===========================
    public boolean suaPhieuGiamGia(PhieuGiamGia p) {

        String sql = "UPDATE phieugiamgia SET "
                + "tenphieu=?,"
                + "loaigiam=?,"
                + "giatrigiam=?,"
                + "giamtoida=?,"
                + "dieukien=?,"
                + "soluong=?,"
                + "ngaybatdau=?,"
                + "ngayketthuc=?,"
                + "trangthai=? "
                + "WHERE magiamgia=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getTenphieu());
            ps.setInt(2, p.getLoaigiam());
            ps.setInt(3, p.getGiatrigiam());
            ps.setInt(4, p.getGiamtoida());
            ps.setInt(5, p.getDieukien());
            ps.setInt(6, p.getSoluong());
            ps.setDate(7, p.getNgaybatdau());
            ps.setDate(8, p.getNgayketthuc());
            ps.setBoolean(9, p.isTrangthai());
            ps.setString(10, p.getMagiamgia());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }    // ===========================
    // XÓA PHIẾU GIẢM GIÁ
    // ===========================
    public boolean xoaPhieuGiamGia(String maGiamGia) {

        String sql = "DELETE FROM phieugiamgia WHERE magiamgia=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGiamGia);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===========================
    // KHÓA / MỞ KHÓA (thay cho xóa hẳn) PHIẾU GIẢM GIÁ
    // trangthai = false (0) -> Ngừng (khóa), true (1) -> Hoạt động (mở khóa)
    // ===========================
    public boolean capNhatTrangThai(String maGiamGia, boolean trangthai) {

        String sql = "UPDATE phieugiamgia SET trangthai=? WHERE magiamgia=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, trangthai);
            ps.setString(2, maGiamGia);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===========================
    // KIỂM TRA MÃ GIẢM GIÁ
    // ===========================
    public PhieuGiamGia kiemTraMa(String maGiamGia) {

        String sql = "SELECT * FROM phieugiamgia "
                + "WHERE magiamgia=? "
                + "AND trangthai=1 "
                + "AND soluong>0 "
                + "AND GETDATE() BETWEEN ngaybatdau AND ngayketthuc";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGiamGia);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                PhieuGiamGia p = new PhieuGiamGia();

                p.setMagiamgia(rs.getString("magiamgia"));
                p.setTenphieu(rs.getString("tenphieu"));
                p.setLoaigiam(rs.getInt("loaigiam"));
                p.setGiatrigiam(rs.getInt("giatrigiam"));
                p.setGiamtoida(rs.getInt("giamtoida"));
                p.setDieukien(rs.getInt("dieukien"));
                p.setSoluong(rs.getInt("soluong"));
                p.setNgaybatdau(rs.getDate("ngaybatdau"));
                p.setNgayketthuc(rs.getDate("ngayketthuc"));
                p.setTrangthai(rs.getBoolean("trangthai"));

                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ===========================
    // TÍNH TIỀN ĐƯỢC GIẢM
    // ===========================
    public int tinhTienGiam(PhieuGiamGia pgg, int tongTien) {

        if (pgg == null) {
            return 0;
        }

        if (tongTien < pgg.getDieukien()) {
            return 0;
        }

        if (pgg.getLoaigiam() == 0) {
            return pgg.getGiatrigiam();
        }

        int tienGiam = tongTien * pgg.getGiatrigiam() / 100;

        if (pgg.getGiamtoida() > 0 && tienGiam > pgg.getGiamtoida()) {
            tienGiam = pgg.getGiamtoida();
        }

        return tienGiam;
    }

    // ===========================
    // DANH SÁCH MÃ GIẢM GIÁ CÒN KHẢ DỤNG (dành cho người dùng xem)
    // Điều kiện: đang bật, còn số lượng, đang trong thời gian áp dụng
    // ===========================
    public ArrayList<PhieuGiamGia> getDanhSachMaGiamGiaKhaDung() {

        ArrayList<PhieuGiamGia> list = new ArrayList<>();

        String sql = "SELECT * FROM phieugiamgia "
                + "WHERE trangthai=1 "
                + "AND soluong>0 "
                + "AND GETDATE() BETWEEN ngaybatdau AND ngayketthuc "
                + "ORDER BY ngayketthuc ASC";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                PhieuGiamGia p = new PhieuGiamGia();

                p.setMagiamgia(rs.getString("magiamgia"));
                p.setTenphieu(rs.getString("tenphieu"));
                p.setLoaigiam(rs.getInt("loaigiam"));
                p.setGiatrigiam(rs.getInt("giatrigiam"));
                p.setGiamtoida(rs.getInt("giamtoida"));
                p.setDieukien(rs.getInt("dieukien"));
                p.setSoluong(rs.getInt("soluong"));
                p.setNgaybatdau(rs.getDate("ngaybatdau"));
                p.setNgayketthuc(rs.getDate("ngayketthuc"));
                p.setTrangthai(rs.getBoolean("trangthai"));

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===========================
    // GIẢM SỐ LƯỢNG VOUCHER
    // ===========================
    public boolean giamLuotSuDung(String maGiamGia) {

        String sql = "UPDATE phieugiamgia "
                + "SET soluong = soluong - 1 "
                + "WHERE magiamgia=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGiamGia);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===========================
    // HOÀN LẠI LƯỢT SỬ DỤNG VOUCHER
    // (dùng khi khách hủy đơn / hoàn trả hàng, để mã giảm giá được dùng lại)
    // ===========================
    public boolean tangLuotSuDung(String maGiamGia) {

        String sql = "UPDATE phieugiamgia "
                + "SET soluong = soluong + 1 "
                + "WHERE magiamgia=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGiamGia);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}