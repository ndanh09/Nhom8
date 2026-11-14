package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import model.SanPhamYeuThich;
import model.TaiKhoanYeuThich;
import model.nguoiban;

/**
 * Truy vấn 2 bảng "yeuthich" và "top_yeuthich" (xem migration_yeuthich.sql).
 *
 * Sản phẩm luôn được tính theo MÃ GỐC (nguoiban.magoc) - gộp mọi size, không đổi khi quản lý sửa tên.
 * Các hàm đọc không ném lỗi: lỗi SQL (vd. chưa chạy migration) -> trả về danh sách rỗng / false để
 * trang chủ và trang sản phẩm vẫn hiển thị bình thường.
 *
 * Phân luồng dùng DAO này:
 *  - Khách hàng : doiTrangThai, layMagocDaThich, layCuaKhach, layTopHienThi
 *  - Quản lý (luồng sản phẩm) : layBangQuanLy, layUngVienGhim, ghim, boGhim, datAn, datThuTu, datLai, xoaTheoMagoc
 *  - Quản trị viên (tài khoản): layTaiKhoan, layMotTaiKhoan, layCuaKhach, xoaMotMuc, xoaTatCa
 */
public class YeuThichDAO {

    /** Số sản phẩm hiển thị ở mục "Top áo yêu thích" trên trang chủ khi quản lý chưa chỉnh (bảng cauhinh_yeuthich). */
    public static final int TOP_MAC_DINH = 8;
    public static final int TOP_TOI_DA = 50;
    private static final String KHOA_TOP = "TOP_HIEN_THI";

    private final nguoibandao sanPhamDao = new nguoibandao();

    // ------------------------------------------------------------------
    // Dữ liệu nền
    // ------------------------------------------------------------------

    /** Gộp bảng nguoiban theo mã gốc: mỗi sản phẩm 1 dòng (đại diện ưu tiên size còn hàng, cộng dồn tồn kho). */
    private LinkedHashMap<String, SanPhamYeuThich> layMapSanPham() {
        LinkedHashMap<String, SanPhamYeuThich> map = new LinkedHashMap<>();
        Map<String, Boolean> daiDienConHang = new HashMap<>();   // đại diện hiện tại của từng nhóm còn hàng chưa
        for (nguoiban sp : sanPhamDao.sp()) {
            String magoc = sp.getMagoc();
            if (magoc == null || magoc.trim().isEmpty()) {
                continue;
            }
            boolean conHang = "1".equals(sp.getTrangthai()) && sp.getSoluongsanpham() > 0;
            SanPhamYeuThich g = map.get(magoc);
            if (g == null) {
                g = new SanPhamYeuThich();
                g.setMagoc(magoc);
                g.setMasanpham(sp.getMasanpham());
                g.setTensanpham(sp.getTensanpham());
                g.setHinhanh(sp.getHinhanh());
                g.setDanhmuc(sp.getDanhmuc());
                g.setGiasanpham(sp.getGiasanpham());
                g.setTrangthai(sp.getTrangthai());
                g.setTongsoluong(sp.getSoluongsanpham());
                map.put(magoc, g);
                daiDienConHang.put(magoc, conHang);
            } else {
                g.setTongsoluong(g.getTongsoluong() + sp.getSoluongsanpham());
                // chỉ đổi đại diện khi đại diện cũ hết hàng mà size này còn hàng
                if (conHang && !daiDienConHang.get(magoc)) {
                    g.setMasanpham(sp.getMasanpham());
                    g.setTrangthai(sp.getTrangthai());
                    g.setGiasanpham(sp.getGiasanpham());
                    daiDienConHang.put(magoc, true);
                }
            }
        }
        return map;
    }

    private Map<String, Integer> demTheoMagoc() {
        Map<String, Integer> kq = new HashMap<>();
        String sql = "SELECT magoc, COUNT(*) AS soluot FROM yeuthich GROUP BY magoc";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                kq.put(rs.getString("magoc"), rs.getInt("soluot"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq;
    }

    private static String dinhDangNgay(Timestamp t) {
        return t == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(t);
    }

    /**
     * Toàn bộ sản phẩm đã có lượt yêu thích hoặc đã được quản lý ghim/ẩn, sắp theo thứ tự Top:
     * ghim trước (theo thutu), sau đó theo số lượt yêu thích giảm dần, rồi theo tên.
     * Đồng thời gán hạng (hang) và cờ trongTop cho các sản phẩm đủ điều kiện hiển thị.
     */
    private ArrayList<SanPhamYeuThich> xepHang() {
        LinkedHashMap<String, SanPhamYeuThich> sanPham = layMapSanPham();
        Map<String, Integer> dem = demTheoMagoc();

        // cấu hình ghim / ẩn của quản lý
        String sql = "SELECT magoc, ghim, thutu, an FROM top_yeuthich";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SanPhamYeuThich g = sanPham.get(rs.getString("magoc"));
                if (g != null) {
                    g.setGhim(rs.getBoolean("ghim"));
                    g.setThutu(rs.getInt("thutu"));
                    g.setAn(rs.getBoolean("an"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<SanPhamYeuThich> ds = new ArrayList<>();
        for (SanPhamYeuThich g : sanPham.values()) {
            Integer n = dem.get(g.getMagoc());
            g.setSoluotyeuthich(n == null ? 0 : n);
            if (g.getSoluotyeuthich() > 0 || g.isGhim() || g.isAn()) {
                ds.add(g);
            }
        }
        ds.sort(Comparator
                .comparing((SanPhamYeuThich g) -> !g.isGhim())
                .thenComparingInt(g -> g.isGhim() ? g.getThutu() : 0)
                .thenComparing((a, b) -> Integer.compare(b.getSoluotyeuthich(), a.getSoluotyeuthich()))
                .thenComparing(g -> g.getTensanpham() == null ? "" : g.getTensanpham().toLowerCase()));

        int soTop = layTopHienThiSo();
        int hang = 0;
        for (SanPhamYeuThich g : ds) {
            boolean duDieuKien = !g.isAn() && !"0".equals(g.getTrangthai());
            if (duDieuKien) {
                hang++;
                g.setHang(hang);
                g.setTrongTop(hang <= soTop);
            }
        }
        return ds;
    }

    // ------------------------------------------------------------------
    // KHÁCH HÀNG
    // ------------------------------------------------------------------

    /** Mã gốc của 1 mã sản phẩm (1 size). null nếu sản phẩm không tồn tại. */
    public String magocTuMaSanPham(String masanpham) {
        if (masanpham == null || masanpham.trim().isEmpty()) {
            return null;
        }
        nguoiban sp = sanPhamDao.layTheoMa(masanpham.trim());
        return sp == null ? null : sp.getMagoc();
    }

    /**
     * Bật/tắt yêu thích.
     *
     * @return 1 = vừa thêm vào yêu thích, 0 = vừa bỏ yêu thích, -1 = lỗi
     */
    public int doiTrangThai(String email, String magoc) {
        try (Connection con = dbconnect.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM yeuthich WHERE makhachhang = ? AND magoc = ?")) {
                ps.setString(1, email);
                ps.setString(2, magoc);
                if (ps.executeUpdate() > 0) {
                    return 0;
                }
            }
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO yeuthich (makhachhang, magoc) VALUES (?, ?)")) {
                ps.setString(1, email);
                ps.setString(2, magoc);
                ps.executeUpdate();
                return 1;
            } catch (SQLException trung) {
                // bấm đúp: dòng đã được request kia thêm rồi -> coi như đã yêu thích
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Mã gốc này còn tồn tại trong cửa hàng không. */
    public boolean tonTaiMagoc(String magoc) {
        if (magoc == null || magoc.trim().isEmpty()) {
            return false;
        }
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM nguoiban WHERE magoc = ?")) {
            ps.setString(1, magoc.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Trong danh sách mã sản phẩm (từng size) trên 1 trang, những mã nào thuộc sản phẩm khách đã yêu thích.
     * Dùng để tô đỏ các nút tim khi trang vừa tải xong.
     */
    public ArrayList<String> locMaSanPhamDaThich(String email, String[] dsMa) {
        ArrayList<String> kq = new ArrayList<>();
        if (dsMa == null || dsMa.length == 0) {
            return kq;
        }
        LinkedHashSet<String> daThich = layMagocDaThich(email);
        if (daThich.isEmpty()) {
            return kq;
        }
        Map<String, String> maToMagoc = new HashMap<>();
        for (nguoiban sp : sanPhamDao.sp()) {
            maToMagoc.put(sp.getMasanpham(), sp.getMagoc());
        }
        for (String ma : dsMa) {
            String magoc = maToMagoc.get(ma);
            if (magoc != null && daThich.contains(magoc)) {
                kq.add(ma);
            }
        }
        return kq;
    }

    public int demLuotYeuThich(String magoc) {
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM yeuthich WHERE magoc = ?")) {
            ps.setString(1, magoc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public LinkedHashSet<String> layMagocDaThich(String email) {
        LinkedHashSet<String> kq = new LinkedHashSet<>();
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT magoc FROM yeuthich WHERE makhachhang = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq;
    }

    /** Danh sách yêu thích của 1 khách (mới thêm đứng trước). Sản phẩm đã bị xóa khỏi cửa hàng được bỏ qua. */
    public ArrayList<SanPhamYeuThich> layCuaKhach(String email) {
        ArrayList<SanPhamYeuThich> kq = new ArrayList<>();
        LinkedHashMap<String, SanPhamYeuThich> sanPham = layMapSanPham();
        Map<String, Integer> dem = demTheoMagoc();
        String sql = "SELECT magoc, ngaythem FROM yeuthich WHERE makhachhang = ? ORDER BY ngaythem DESC";
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPhamYeuThich g = sanPham.get(rs.getString("magoc"));
                    if (g == null) {
                        continue;
                    }
                    Integer n = dem.get(g.getMagoc());
                    g.setSoluotyeuthich(n == null ? 0 : n);
                    g.setNgaythem(dinhDangNgay(rs.getTimestamp("ngaythem")));
                    kq.add(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq;
    }

    /** Các sản phẩm đang hiển thị ở mục "Top áo yêu thích" trang chủ (tối đa TOP_HIEN_THI). */
    public ArrayList<SanPhamYeuThich> layTopHienThi() {
        ArrayList<SanPhamYeuThich> kq = new ArrayList<>();
        for (SanPhamYeuThich g : xepHang()) {
            if (g.isTrongTop()) {
                kq.add(g);
            }
        }
        return kq;
    }

    // ------------------------------------------------------------------
    // QUẢN LÝ - LUỒNG SẢN PHẨM
    // ------------------------------------------------------------------

    /** Bảng quản lý Top: có thể lọc theo từ khóa (tên / mã gốc) và danh mục. Hạng vẫn là hạng thật trong toàn Top. */
    public ArrayList<SanPhamYeuThich> layBangQuanLy(String keyword, String danhmuc) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        String dm = danhmuc == null ? "" : danhmuc.trim();
        ArrayList<SanPhamYeuThich> kq = new ArrayList<>();
        for (SanPhamYeuThich g : xepHang()) {
            if (!kw.isEmpty()) {
                String ten = g.getTensanpham() == null ? "" : g.getTensanpham().toLowerCase();
                String ma = g.getMagoc() == null ? "" : g.getMagoc().toLowerCase();
                if (!ten.contains(kw) && !ma.contains(kw)) {
                    continue;
                }
            }
            if (!dm.isEmpty() && (g.getDanhmuc() == null || !dm.equalsIgnoreCase(g.getDanhmuc().trim()))) {
                continue;
            }
            kq.add(g);
        }
        return kq;
    }

    /** Sản phẩm chưa được ghim (để chọn ghim thêm, kể cả sản phẩm chưa có lượt yêu thích nào). */
    public ArrayList<SanPhamYeuThich> layUngVienGhim() {
        ArrayList<SanPhamYeuThich> kq = new ArrayList<>();
        Map<String, Boolean> daGhim = new HashMap<>();
        for (SanPhamYeuThich g : xepHang()) {
            if (g.isGhim()) {
                daGhim.put(g.getMagoc(), Boolean.TRUE);
            }
        }
        for (SanPhamYeuThich g : layMapSanPham().values()) {
            if (!daGhim.containsKey(g.getMagoc()) && !"0".equals(g.getTrangthai())) {
                kq.add(g);
            }
        }
        return kq;
    }

    /** Số sản phẩm hiển thị ở mục Top trang chủ (quản lý chỉnh được); lỗi/chưa cấu hình -> TOP_MAC_DINH. */
    public int layTopHienThiSo() {
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT giatri FROM cauhinh_yeuthich WHERE khoa = ?")) {
            ps.setString(1, KHOA_TOP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int v = rs.getInt(1);
                    return Math.max(1, Math.min(TOP_TOI_DA, v));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TOP_MAC_DINH;
    }

    public boolean datTopHienThiSo(int so) {
        int v = Math.max(1, Math.min(TOP_TOI_DA, so));
        String sql = "MERGE cauhinh_yeuthich AS t USING (SELECT ? AS khoa) AS s ON t.khoa = s.khoa "
                + "WHEN MATCHED THEN UPDATE SET giatri = ? "
                + "WHEN NOT MATCHED THEN INSERT (khoa, giatri) VALUES (s.khoa, ?);";
        return chay(sql, KHOA_TOP, v, v);
    }

    /** Tổng số lượt yêu thích của toàn cửa hàng. */
    public int tongLuot() {
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM yeuthich");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int soTaiKhoanCoYeuThich() {
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(DISTINCT makhachhang) FROM yeuthich");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean chay(String sql, Object... tham) {
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < tham.length; i++) {
                ps.setObject(i + 1, tham[i]);
            }
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Ghim sản phẩm lên đầu Top với thứ tự thutu (nhỏ đứng trước). Ghim thì tự bỏ ẩn. */
    public boolean ghim(String magoc, int thutu) {
        String sql = "MERGE top_yeuthich AS t USING (SELECT ? AS magoc) AS s ON t.magoc = s.magoc "
                + "WHEN MATCHED THEN UPDATE SET ghim = 1, thutu = ?, an = 0, ngaycapnhat = GETDATE() "
                + "WHEN NOT MATCHED THEN INSERT (magoc, ghim, thutu, an) VALUES (s.magoc, 1, ?, 0);";
        return chay(sql, magoc, thutu, thutu);
    }

    public boolean boGhim(String magoc) {
        boolean ok = chay("UPDATE top_yeuthich SET ghim = 0, thutu = 0, ngaycapnhat = GETDATE() WHERE magoc = ?", magoc);
        chay("DELETE FROM top_yeuthich WHERE magoc = ? AND ghim = 0 AND an = 0", magoc);
        return ok;
    }

    /** Ẩn/hiện sản phẩm khỏi Top trang chủ. Ẩn thì tự bỏ ghim. */
    public boolean datAn(String magoc, boolean an) {
        String sql = "MERGE top_yeuthich AS t USING (SELECT ? AS magoc) AS s ON t.magoc = s.magoc "
                + "WHEN MATCHED THEN UPDATE SET an = ?, ghim = CASE WHEN ? = 1 THEN 0 ELSE ghim END, "
                + "thutu = CASE WHEN ? = 1 THEN 0 ELSE thutu END, ngaycapnhat = GETDATE() "
                + "WHEN NOT MATCHED THEN INSERT (magoc, ghim, thutu, an) VALUES (s.magoc, 0, 0, ?);";
        int v = an ? 1 : 0;
        boolean ok = chay(sql, magoc, v, v, v, v);
        chay("DELETE FROM top_yeuthich WHERE magoc = ? AND ghim = 0 AND an = 0", magoc);
        return ok;
    }

    public boolean datThuTu(String magoc, int thutu) {
        return chay("UPDATE top_yeuthich SET thutu = ?, ngaycapnhat = GETDATE() WHERE magoc = ? AND ghim = 1", thutu, magoc);
    }

    /** Đưa sản phẩm về xếp hạng tự động (xóa cấu hình ghim/ẩn). */
    public boolean datLai(String magoc) {
        return chay("DELETE FROM top_yeuthich WHERE magoc = ?", magoc);
    }

    /** Gọi khi quản lý XÓA sản phẩm: dọn luôn lượt yêu thích và cấu hình Top của sản phẩm đó. */
    public void xoaTheoMagoc(String magoc) {
        if (magoc == null) {
            return;
        }
        chay("DELETE FROM yeuthich WHERE magoc = ?", magoc);
        chay("DELETE FROM top_yeuthich WHERE magoc = ?", magoc);
    }

    // ------------------------------------------------------------------
    // QUẢN TRỊ VIÊN - LUỒNG TÀI KHOẢN
    // ------------------------------------------------------------------

    private TaiKhoanYeuThich docTaiKhoan(ResultSet rs) throws SQLException {
        TaiKhoanYeuThich t = new TaiKhoanYeuThich();
        t.setEmail(rs.getString("email"));
        t.setHoten(rs.getString("hoten"));
        t.setSodienthoai(rs.getString("sodienthoai"));
        t.setTrangthai(rs.getString("trangthai"));
        t.setSoyeuthich(rs.getInt("soyeuthich"));
        t.setNgaygannhat(dinhDangNgay(rs.getTimestamp("gannhat")));
        return t;
    }

    private static final String SQL_TAIKHOAN
            = "SELECT d.email, d.hoten, d.sodienthoai, ISNULL(d.trangthai, N'Đang hoạt động') AS trangthai, "
            + "COUNT(y.magoc) AS soyeuthich, MAX(y.ngaythem) AS gannhat "
            + "FROM dangky d LEFT JOIN yeuthich y ON y.makhachhang = d.email ";

    private static final String SQL_TAIKHOAN_NHOM = "GROUP BY d.email, d.hoten, d.sodienthoai, d.trangthai ";

    /** Danh sách tài khoản khách hàng kèm số sản phẩm yêu thích. */
    public List<TaiKhoanYeuThich> layTaiKhoan(String keyword, boolean chiCoYeuThich) {
        String kw = keyword == null ? "" : keyword.trim();
        StringBuilder sql = new StringBuilder(SQL_TAIKHOAN);
        if (!kw.isEmpty()) {
            sql.append("WHERE (d.email LIKE ? OR d.hoten LIKE ? OR d.sodienthoai LIKE ?) ");
        }
        sql.append(SQL_TAIKHOAN_NHOM);
        if (chiCoYeuThich) {
            sql.append("HAVING COUNT(y.magoc) > 0 ");
        }
        sql.append("ORDER BY COUNT(y.magoc) DESC, d.hoten");
        List<TaiKhoanYeuThich> kq = new ArrayList<>();
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (!kw.isEmpty()) {
                String like = "%" + kw + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(docTaiKhoan(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq;
    }

    public TaiKhoanYeuThich layMotTaiKhoan(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String sql = SQL_TAIKHOAN + "WHERE d.email = ? " + SQL_TAIKHOAN_NHOM;
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docTaiKhoan(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Admin gỡ 1 sản phẩm khỏi danh sách yêu thích của 1 tài khoản. */
    public boolean xoaMotMuc(String email, String magoc) {
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM yeuthich WHERE makhachhang = ? AND magoc = ?")) {
            ps.setString(1, email);
            ps.setString(2, magoc);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Admin xóa toàn bộ danh sách yêu thích của 1 tài khoản. @return số dòng đã xóa (-1 nếu lỗi) */
    public int xoaTatCa(String email) {
        try (Connection con = dbconnect.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM yeuthich WHERE makhachhang = ?")) {
            ps.setString(1, email);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
