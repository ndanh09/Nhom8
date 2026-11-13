package modeldao;
import java.sql.*;
import java.util.*;
import connect.dbconnect;
import model.*;

/**
 * FIX (toàn file): trước đây MỌI hàm đều mở Connection mà không bao giờ đóng (rò rỉ kết nối), và
 * sp() trả về list là field của DAO dùng chung giữa các request. Nay dùng try-with-resources và
 * list cục bộ. them() trả boolean để servlet biết có lưu được hay không.
 */
public class nguoibandao {

    // Đọc 1 dòng kết quả (nguoiban) từ ResultSet - dùng chung cho các hàm bên dưới.
    private nguoiban docDong(ResultSet rs) throws SQLException {
        String masanpham = rs.getString("masanpham");
        String tensanpham = rs.getString("tensanpham");
        String hinhanh = rs.getString("hinhanh");
        int soluongsanpham = rs.getInt("soluongsanpham");
        String kichco = rs.getString("kichco");
        int giasanpham = rs.getInt("giasanpham");
        String ngayban = rs.getString("ngayban");
        String trangthai = rs.getString("trangthai");
        String danhmuc = rs.getString("danhmuc");
        String magoc = rs.getString("magoc");
        // Phòng trường hợp dữ liệu cũ chưa kịp backfill cột magoc (NULL) thì tự
        // suy ra tạm từ masanpham/kichco để không bị lỗi khi gộp nhóm.
        if (magoc == null || magoc.trim().isEmpty()) {
            magoc = layMaGoc(masanpham, kichco);
        }
        return new nguoiban(masanpham, magoc, tensanpham, hinhanh, soluongsanpham, kichco, giasanpham, ngayban, trangthai, danhmuc);
    }

    public ArrayList<nguoiban> sp() {
        ArrayList<nguoiban> listsp = new ArrayList<>();
        String sql = "SELECT * FROM nguoiban";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listsp.add(docDong(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listsp;
    }

    public nguoiban layTheoMa(String masanpham) {
        String sql = "SELECT * FROM nguoiban WHERE masanpham = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, masanpham);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docDong(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(nguoiban sp) {
        String sql = "INSERT INTO nguoiban (masanpham, magoc, tensanpham, hinhanh, soluongsanpham, kichco, giasanpham, ngayban, trangthai, danhmuc) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getMasanpham());
            ps.setString(2, sp.getMagoc());
            ps.setString(3, sp.getTensanpham());
            ps.setString(4, sp.getHinhanh());
            ps.setInt(5, sp.getSoluongsanpham());
            ps.setString(6, sp.getKichco());
            ps.setInt(7, sp.getGiasanpham());
            ps.setString(8, sp.getNgayban());
            ps.setString(9, sp.getTrangthai());
            ps.setString(10, sp.getDanhmuc());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean check(String masanpham) {
        String sql = "SELECT 1 FROM nguoiban WHERE masanpham = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, masanpham);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Sửa 1 dòng (1 size) cụ thể. KHÔNG đụng tới magoc vì magoc xác định
    // sản phẩm này thuộc nhóm nào, sửa số lượng/giá của 1 size không được
    // phép nhảy nhóm.
    public void sua(nguoiban sp) {
        String sql = "UPDATE nguoiban SET tensanpham=?, hinhanh=?, soluongsanpham=?, kichco=?, giasanpham=?, ngayban=?, trangthai=?, danhmuc=? WHERE masanpham=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTensanpham());
            ps.setString(2, sp.getHinhanh());
            ps.setInt(3, sp.getSoluongsanpham());
            ps.setString(4, sp.getKichco());
            ps.setInt(5, sp.getGiasanpham());
            ps.setString(6, sp.getNgayban());
            ps.setString(7, sp.getTrangthai());
            ps.setString(8, sp.getDanhmuc());
            ps.setString(9, sp.getMasanpham());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cộng dồn thêm số lượng cho ĐÚNG 1 size cụ thể (masanpham đã gồm size,
    // vd "mu1-M"). Dùng khi bên trang Quản lý kho hàng "nhập thêm" hàng, để
    // số lượng riêng theo size (nguoiban) đồng bộ theo.
    //
    // FIX: trạng thái chỉ tự chuyển "Hết hàng" ('2') -> "Còn hàng" ('1'). Trước đây điều kiện
    // "soluongsanpham + ? > 0" luôn đúng (vì số nhập thêm > 0) nên sản phẩm "Ngừng bán" ('0') cũng
    // bị bật lại thành "Còn hàng" mỗi lần nhập thêm kho.
    public void congSoLuong(String masanpham, int soLuongThem) {
        if (soLuongThem <= 0) {
            return;
        }
        String sql = "UPDATE nguoiban SET soluongsanpham = soluongsanpham + ?, "
                + "trangthai = CASE WHEN trangthai = '2' THEN '1' ELSE trangthai END "
                + "WHERE masanpham=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuongThem);
            ps.setString(2, masanpham);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void xoa(String masanpham) {
        String sql = "DELETE FROM nguoiban WHERE masanpham=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, masanpham);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== NHÓM SẢN PHẨM THEO SIZE (dùng cột magoc thật) ==================

    // Chỉ dùng làm phương án dự phòng khi cột magoc trong DB bị NULL (dữ liệu cũ
    // chưa backfill). Bình thường mã gốc lấy thẳng từ cột magoc, không tính lại.
    public String layMaGoc(String masanpham, String kichco) {
        if (masanpham != null && kichco != null
                && masanpham.toLowerCase().endsWith("-" + kichco.toLowerCase())) {
            return masanpham.substring(0, masanpham.length() - (kichco.length() + 1));
        }
        return masanpham;
    }

    // Gộp toàn bộ sản phẩm theo cột magoc, mỗi mã gốc trả về đúng 1 dòng đại diện
    // (thông tin chung lấy theo dòng đầu tiên, số lượng cộng dồn tất cả size).
    public ArrayList<nhomsanpham> layDanhSachNhom() {

        LinkedHashMap<String, ArrayList<nguoiban>> nhom = new LinkedHashMap<>();

        for (nguoiban sp : sp()) {
            nhom.computeIfAbsent(sp.getMagoc(), k -> new ArrayList<>()).add(sp);
        }

        ArrayList<nhomsanpham> ketQua = new ArrayList<>();

        for (Map.Entry<String, ArrayList<nguoiban>> e : nhom.entrySet()) {

            ArrayList<nguoiban> ds = e.getValue();

            // Ưu tiên chọn 1 size CÒN HÀNG làm đại diện để hiển thị Trạng thái cho cả nhóm;
            // chỉ khi TẤT CẢ size đều hết/ngừng bán mới giữ đại diện là dòng đầu tiên.
            nguoiban daiDien = ds.get(0);
            for (nguoiban sp : ds) {
                if ("1".equals(sp.getTrangthai()) && sp.getSoluongsanpham() > 0) {
                    daiDien = sp;
                    break;
                }
            }

            int tongSoLuong = 0;
            StringBuilder dssize = new StringBuilder();
            for (nguoiban sp : ds) {
                tongSoLuong += sp.getSoluongsanpham();
                if (dssize.length() > 0) {
                    dssize.append(", ");
                }
                dssize.append(sp.getKichco());
            }

            ketQua.add(new nhomsanpham(
                    e.getKey(),
                    daiDien.getTensanpham(),
                    daiDien.getHinhanh(),
                    daiDien.getDanhmuc(),
                    daiDien.getGiasanpham(),
                    daiDien.getNgayban(),
                    daiDien.getTrangthai(),
                    tongSoLuong,
                    ds.size(),
                    dssize.toString()
            ));
        }

        return ketQua;
    }

    // Lấy toàn bộ các dòng (size) thuộc về 1 mã gốc - truy vấn thẳng theo cột
    // magoc, dùng cho trang Quản lý size.
    public ArrayList<nguoiban> layTheoMaGoc(String magoc) {
        ArrayList<nguoiban> ketQua = new ArrayList<>();
        String sql = "SELECT * FROM nguoiban WHERE magoc = ?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, magoc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(docDong(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    // Thông tin đại diện (dùng để hiện phần đầu trang Quản lý size).
    public nhomsanpham layNhomTheoMaGoc(String magoc) {
        ArrayList<nguoiban> ds = layTheoMaGoc(magoc);
        if (ds.isEmpty()) {
            return null;
        }
        // Cùng logic với layDanhSachNhom() - ưu tiên chọn 1 size còn hàng làm đại diện.
        nguoiban daiDien = ds.get(0);
        for (nguoiban sp : ds) {
            if ("1".equals(sp.getTrangthai()) && sp.getSoluongsanpham() > 0) {
                daiDien = sp;
                break;
            }
        }
        int tongSoLuong = 0;
        StringBuilder dssize = new StringBuilder();
        for (nguoiban sp : ds) {
            tongSoLuong += sp.getSoluongsanpham();
            if (dssize.length() > 0) {
                dssize.append(", ");
            }
            dssize.append(sp.getKichco());
        }
        return new nhomsanpham(magoc, daiDien.getTensanpham(), daiDien.getHinhanh(), daiDien.getDanhmuc(),
                daiDien.getGiasanpham(), daiDien.getNgayban(), daiDien.getTrangthai(), tongSoLuong, ds.size(), dssize.toString());
    }

    // Sửa thông tin CHUNG (tên, ảnh, danh mục, giá, ngày bán, trạng thái) cho
    // TẤT CẢ các size thuộc 1 mã gốc cùng lúc - dùng ở trang Quản lý sản phẩm
    // vì các size của cùng 1 sản phẩm nên đồng bộ các thông tin này với nhau.
    public void suaNhom(String magoc, String tensanpham, String hinhanh, String danhmuc,
            int giasanpham, String ngayban, String trangthai) {

        String sql = "UPDATE nguoiban SET tensanpham=?, hinhanh=?, danhmuc=?, giasanpham=?, ngayban=?, trangthai=? WHERE magoc=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tensanpham);
            ps.setString(2, hinhanh);
            ps.setString(3, danhmuc);
            ps.setInt(4, giasanpham);
            ps.setString(5, ngayban);
            ps.setString(6, trangthai);
            ps.setString(7, magoc);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xoá toàn bộ các size thuộc 1 mã gốc (xoá cả sản phẩm).
    public void xoaNhom(String magoc) {
        String sql = "DELETE FROM nguoiban WHERE magoc=?";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, magoc);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
