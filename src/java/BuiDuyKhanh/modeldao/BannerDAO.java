package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Banner;

/**
 * Truy vấn bảng "banner" (xem migration_banner.sql).
 */
public class BannerDAO {

    private Banner anhXa(ResultSet rs) throws SQLException {
        Banner b = new Banner();
        b.setId(rs.getInt("id"));
        b.setVitri(rs.getString("vitri"));
        b.setNhan(rs.getString("nhan"));
        b.setTieude(rs.getString("tieude"));
        b.setMota(rs.getString("mota"));
        b.setHinhanh(rs.getString("hinhanh"));
        b.setLienket(rs.getString("lienket"));
        b.setNuttext(rs.getString("nuttext"));
        b.setThutu(rs.getInt("thutu"));
        b.setHienthi(rs.getBoolean("hienthi"));
        b.setNgaytao(rs.getTimestamp("ngaytao"));
        return b;
    }

    /**
     * Danh sách banner cho trang người dùng: chỉ banner đang hiện của 1 vị trí,
     * sắp theo thutu rồi id.
     *
     * @return danh sách (có thể rỗng); null nếu lỗi SQL (vd: chưa chạy migration_banner.sql)
     */
    public ArrayList<Banner> getBannerHienThi(String vitri) {
        String sql = "SELECT * FROM banner WHERE vitri = ? AND hienthi = 1 ORDER BY thutu, id";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vitri);
            ResultSet rs = ps.executeQuery();
            ArrayList<Banner> ds = new ArrayList<>();
            while (rs.next()) {
                ds.add(anhXa(rs));
            }
            return ds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Danh sách cho trang admin: gồm cả banner đang ẩn. vitri rỗng/null = tất cả. */
    public ArrayList<Banner> getDanhSach(String vitri) {
        boolean loc = vitri != null && !vitri.trim().isEmpty();
        String sql = "SELECT * FROM banner " + (loc ? "WHERE vitri = ? " : "") + "ORDER BY vitri DESC, thutu, id";
        ArrayList<Banner> ds = new ArrayList<>();
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (loc) {
                ps.setString(1, vitri.trim());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ds.add(anhXa(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public Banner getBanner(int id) {
        String sql = "SELECT * FROM banner WHERE id = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return anhXa(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(Banner b) {
        String sql = "INSERT INTO banner (vitri, nhan, tieude, mota, hinhanh, lienket, nuttext, thutu, hienthi) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getVitri());
            ps.setString(2, b.getNhan());
            ps.setString(3, b.getTieude());
            ps.setString(4, b.getMota());
            ps.setString(5, b.getHinhanh());
            ps.setString(6, b.getLienket());
            ps.setString(7, b.getNuttext());
            ps.setInt(8, b.getThutu());
            ps.setBoolean(9, b.isHienthi());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Sửa banner. hinhanh null/rỗng = giữ nguyên ảnh cũ. */
    public boolean sua(Banner b) {
        boolean doiAnh = b.getHinhanh() != null && !b.getHinhanh().trim().isEmpty();
        String sql = "UPDATE banner SET vitri = ?, nhan = ?, tieude = ?, mota = ?, lienket = ?, nuttext = ?, "
                + "thutu = ?, hienthi = ?" + (doiAnh ? ", hinhanh = ?" : "") + " WHERE id = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, b.getVitri());
            ps.setString(i++, b.getNhan());
            ps.setString(i++, b.getTieude());
            ps.setString(i++, b.getMota());
            ps.setString(i++, b.getLienket());
            ps.setString(i++, b.getNuttext());
            ps.setInt(i++, b.getThutu());
            ps.setBoolean(i++, b.isHienthi());
            if (doiAnh) {
                ps.setString(i++, b.getHinhanh());
            }
            ps.setInt(i, b.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatHienThi(int id, boolean hienthi) {
        String sql = "UPDATE banner SET hienthi = ? WHERE id = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, hienthi);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(int id) {
        String sql = "DELETE FROM banner WHERE id = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
