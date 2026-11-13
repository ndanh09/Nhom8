package modeldao;

import connect.dbconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.TrangThai;
import model.DonHang;

public class TrangThaiDAO {

    private final PhieuGiamGiaDAO phieuGiamGiaDAO = new PhieuGiamGiaDAO();

    /**
     * Hoàn lại tồn kho (nguoiban + khohang) cho 1 dòng đơn bị hủy/hoàn trả, trên CÙNG connection
     * (để gọi trong transaction cùng với việc đổi trạng thái). Kho đã bị trừ 1 lần lúc thanh toán
     * (GioHangDAO.truTonKho) nên mọi đường hủy/hoàn trả đều phải cộng lại đúng 1 lần.
     * Sản phẩm đang "Hết hàng" ('2') được chuyển lại "Còn hàng" ('1'); "Ngừng bán" ('0') giữ nguyên.
     * Package-private để CapNhatTrangThaiDAO và VanChuyenDAO dùng chung.
     */
    static void hoanKho(Connection conn, String maSanPham, int soLuong) throws SQLException {
        String sqlNguoiBan = "UPDATE nguoiban SET soluongsanpham = soluongsanpham + ?, "
                + "trangthai = CASE WHEN trangthai = '2' THEN '1' ELSE trangthai END "
                + "WHERE masanpham=?";
        try (PreparedStatement ps = conn.prepareStatement(sqlNguoiBan)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maSanPham);
            ps.executeUpdate();
        }
        String sqlKho = "UPDATE khohang SET soluongton = soluongton + ? WHERE masanpham=?";
        try (PreparedStatement ps = conn.prepareStatement(sqlKho)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maSanPham);
            ps.executeUpdate();
        }
    }

    /**
     * Hoàn lại lượt sử dụng mã giảm giá (nếu có) của một hóa đơn (madonhang),
     * nhưng CHỈ khi TẤT CẢ sản phẩm trong hóa đơn đó đã ở trạng thái
     * Hủy/Hoàn trả (tinhtrang=3). Vì mã giảm giá được áp dụng cho CẢ hóa đơn
     * (lưu ở bảng donhang), trong khi một hóa đơn có thể có nhiều dòng
     * trangthai (nhiều sản phẩm) được hủy/hoàn trả riêng lẻ ở các thời điểm
     * khác nhau. Nếu hoàn lại ngay mỗi lần 1 sản phẩm bị hủy/hoàn trả thì mã
     * giảm giá sẽ được cộng lượt nhiều lần cho cùng 1 hóa đơn.
     */
    void hoanLaiMaGiamGiaNeuHetHieuLuc(int maDonHang) {
        String sqlDemConLai = "SELECT COUNT(*) FROM trangthai WHERE madonhang=? AND tinhtrang<>3";
        String sqlLayMaGiamGia = "SELECT magiamgia FROM donhang WHERE madonhang=?";

        try (Connection conn = dbconnect.getConnection()) {

            PreparedStatement psDem = conn.prepareStatement(sqlDemConLai);
            psDem.setInt(1, maDonHang);
            ResultSet rsDem = psDem.executeQuery();
            if (!(rsDem.next() && rsDem.getInt(1) == 0)) {
                // Hóa đơn vẫn còn sản phẩm khác chưa hủy/hoàn trả -> chưa hoàn mã
                return;
            }

            PreparedStatement psMa = conn.prepareStatement(sqlLayMaGiamGia);
            psMa.setInt(1, maDonHang);
            ResultSet rsMa = psMa.executeQuery();

            String maGiamGia = null;
            if (rsMa.next()) {
                maGiamGia = rsMa.getString("magiamgia");
            }

            if (maGiamGia != null && !maGiamGia.trim().isEmpty()) {
                phieuGiamGiaDAO.tangLuotSuDung(maGiamGia.trim());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== KHÓA ĐƠN HÀNG =====
    // Đơn (bảng donhang) có trangthai = N'Đã khóa' thì MỌI thao tác lên đơn đó (admin đổi trạng thái/hủy,
    // khách xác nhận nhận/hoàn trả/hủy, vận chuyển nhận/trả/giao/báo thất bại, sửa thông tin đơn)
    // đều bị chặn cho tới khi mở khóa. Điều kiện này được gắn thẳng vào các câu UPDATE trangthai
    // (xem DK_KHONG_KHOA) nên không thể lách qua bằng cách gửi request trực tiếp.
    public static final String DK_KHONG_KHOA
            = " AND NOT EXISTS (SELECT 1 FROM donhang dk WHERE dk.madonhang = trangthai.madonhang"
            + " AND dk.trangthai = N'Đã khóa')";

    /** Dòng trangthai (theo matrangthai) thuộc một đơn hàng đang bị khóa? */
    public static boolean donHangBiKhoa(int matrangthai) {
        String sql = "SELECT COUNT(*) FROM trangthai t JOIN donhang d ON d.madonhang = t.madonhang "
                + "WHERE t.matrangthai = ? AND d.trangthai = N'Đã khóa'";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matrangthai);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Đơn hàng (theo madonhang) đang bị khóa? */
    public static boolean donHangBiKhoaTheoMaDon(int madonhang) {
        String sql = "SELECT COUNT(*) FROM donhang WHERE madonhang = ? AND trangthai = N'Đã khóa'";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, madonhang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<TrangThai> getDanhSachDonHang(String maKH) {
        List<TrangThai> list = new ArrayList<>();
        // FIX: join thêm bảng nguoidung để lấy tensanpham -> phục vụ hiển thị
        // tên sản phẩm và tính năng tìm kiếm/nhấp nháy đồng bộ ở trang đơn hàng.
        String sql = "SELECT t.matrangthai, t.magiohang, t.makhachhang, t.masanpham, "
                + "t.soluong, t.kichco, t.giaban, t.ngaymua, t.tinhtrang, t.madonhang, "
                + "nd.tensanpham, "
                + "CASE WHEN dh.trangthai = N'Đã khóa' THEN 1 ELSE 0 END AS dakhoa "
                + "FROM trangthai t "
                + "LEFT JOIN nguoidung nd ON nd.masanpham = t.masanpham "
                + "LEFT JOIN donhang dh ON dh.madonhang = t.madonhang "
                + "WHERE t.makhachhang = ? "
                + "ORDER BY t.madonhang DESC, t.matrangthai ASC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TrangThai tt = new TrangThai();
                tt.setMatrangthai(rs.getInt("matrangthai"));
                tt.setMagiohang(rs.getInt("magiohang"));
                tt.setMakhachhang(rs.getString("makhachhang"));
                tt.setMasanpham(rs.getString("masanpham"));
                tt.setTensanpham(rs.getString("tensanpham"));
                tt.setSoluong(rs.getInt("soluong"));
                tt.setKichco(rs.getString("kichco"));
                tt.setGiaban(rs.getInt("giaban"));
                tt.setNgaymua(rs.getTimestamp("ngaymua"));
                tt.setTinhtrang(rs.getInt("tinhtrang"));
                tt.setMadonhang(rs.getInt("madonhang"));
                tt.setDonhangKhoa(rs.getInt("dakhoa") == 1);
                list.add(tt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách chi tiết sản phẩm của MỘT đơn hàng (theo madonhang) để
     * hiển thị hóa đơn, kèm tên sản phẩm và hình ảnh (join bảng nguoidung).
     */
    public List<TrangThai> getChiTietHoaDon(int maDonHang) {
        List<TrangThai> list = new ArrayList<>();
        String sql = "SELECT t.matrangthai, t.magiohang, t.makhachhang, t.masanpham, "
                + "t.soluong, t.kichco, t.giaban, t.ngaymua, t.tinhtrang, t.madonhang, "
                + "nd.tensanpham, nd.hinhanh "
                + "FROM trangthai t "
                + "LEFT JOIN nguoidung nd ON nd.masanpham = t.masanpham "
                + "WHERE t.madonhang = ? "
                + "ORDER BY t.matrangthai ASC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDonHang);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TrangThai tt = new TrangThai();
                tt.setMatrangthai(rs.getInt("matrangthai"));
                tt.setMagiohang(rs.getInt("magiohang"));
                tt.setMakhachhang(rs.getString("makhachhang"));
                tt.setMasanpham(rs.getString("masanpham"));
                tt.setSoluong(rs.getInt("soluong"));
                tt.setKichco(rs.getString("kichco"));
                tt.setGiaban(rs.getInt("giaban"));
                tt.setNgaymua(rs.getTimestamp("ngaymua"));
                tt.setTinhtrang(rs.getInt("tinhtrang"));
                tt.setMadonhang(rs.getInt("madonhang"));
                tt.setTensanpham(rs.getString("tensanpham"));
                tt.setHinhanh(rs.getString("hinhanh"));
                list.add(tt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Khách hàng tự xác nhận ĐÃ NHẬN hàng: chỉ cho phép khi đơn đang ở trạng
     * thái Đã giao (4) - tức người vận chuyển đã giao tới nơi và đang chờ
     * khách xác nhận. CHỈ đổi trạng thái đơn, KHÔNG trừ kho ở bước này vì
     * kho đã được trừ đúng 1 lần rồi tại thời điểm thanh toán
     * (GioHangDAO.truTonKho). Trừ thêm ở đây sẽ gây trừ kho 2 lần.
     */
    public boolean khachXacNhanDaNhan(int matrangthai, String maKH) {
        String sqlGet = "SELECT tinhtrang, makhachhang FROM trangthai WHERE matrangthai=?";

        try (Connection conn = dbconnect.getConnection()) {

            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, matrangthai);
            ResultSet rs = psGet.executeQuery();

            int tinhTrangCu;
            String makhachhangDon;

            if (rs.next()) {
                tinhTrangCu = rs.getInt("tinhtrang");
                makhachhangDon = rs.getString("makhachhang");
            } else {
                return false;
            }

            // Chỉ chủ đơn hàng mới được thao tác, và chỉ khi đơn đang Đã giao
            if (!makhachhangDon.equals(maKH) || tinhTrangCu != 4) {
                return false;
            }

            // FIX BUG: TRƯỚC ĐÂY hàm này trừ kho (nguoiban + khohang) một lần
            // nữa tại bước khách "Xác nhận đã nhận". Nhưng kho ĐÃ được trừ
            // đúng 1 lần rồi tại thời điểm thanh toán (xem
            // GioHangDAO.truTonKho, gọi trong DonHangServlet lúc tạo đơn).
            // Trừ thêm ở đây => bị trừ 2 LẦN cho cùng 1 đơn hàng (VD: mua 50/100,
            // sau thanh toán còn đúng 50, nhưng sau khi bấm "Đã nhận hàng" lại bị
            // trừ thêm 50 nữa => còn 0, hiển thị "hết hàng" dù thực tế vẫn còn 50).
            // Bước "Đã nhận hàng" chỉ nên đổi trạng thái đơn, KHÔNG được đụng vào
            // số lượng tồn kho nữa. (Việc cộng trả lại kho khi "Hoàn trả hàng"
            // ở khachHoanTraHang() bên dưới vẫn giữ nguyên vì đó là cộng trả cho
            // một lượt trả hàng thật, cân bằng đúng với lần trừ duy nhất lúc mua.)
            String sqlUpdate = "UPDATE trangthai SET tinhtrang=2 WHERE matrangthai=? AND tinhtrang=4" + DK_KHONG_KHOA;
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, matrangthai);

            return psUpdate.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Khách hàng HOÀN TRẢ HÀNG: chỉ cho phép khi đơn đang ở trạng thái Đã
     * nhận (2). Số lượng sản phẩm sẽ được cộng trả lại đúng bằng số lượng
     * trước khi mua (hoàn lại kho ở nguoiban và khohang; nguoidung tự đồng
     * bộ qua trigger DB).
     */
    public boolean khachHoanTraHang(int matrangthai, String maKH) {
        String sqlGet = "SELECT masanpham, soluong, tinhtrang, makhachhang, madonhang FROM trangthai WHERE matrangthai=?";

        try (Connection conn = dbconnect.getConnection()) {

            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, matrangthai);
            ResultSet rs = psGet.executeQuery();

            String maSanPham;
            int soLuong;
            int tinhTrangCu;
            String makhachhangDon;
            int maDonHang;

            if (rs.next()) {
                maSanPham = rs.getString("masanpham");
                soLuong = rs.getInt("soluong");
                tinhTrangCu = rs.getInt("tinhtrang");
                makhachhangDon = rs.getString("makhachhang");
                maDonHang = rs.getInt("madonhang");
            } else {
                return false;
            }

            if (!makhachhangDon.equals(maKH) || tinhTrangCu != 2) {
                return false;
            }

            // FIX: trước đây CỘNG KHO TRƯỚC rồi mới đổi trạng thái (và bỏ qua kết quả đổi trạng thái)
            // nên bấm đúp/gửi lặp yêu cầu là kho bị cộng nhiều lần. Nay đổi trạng thái trước bằng
            // câu UPDATE có điều kiện "AND tinhtrang=2" (chỉ 1 request thắng) và chỉ cộng kho khi
            // đổi thành công, cả hai trong 1 transaction.
            // Chỉ update nguoiban/khohang, KHÔNG update nguoidung vì trigger DB đã tự đồng bộ
            // (update cả 2 bảng sẽ cộng 2 lần).
            boolean ok;
            conn.setAutoCommit(false);
            try {
                String sqlUpdate = "UPDATE trangthai SET tinhtrang=3 WHERE matrangthai=? AND tinhtrang=2" + DK_KHONG_KHOA;
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setInt(1, matrangthai);
                    ok = psUpdate.executeUpdate() > 0;
                }
                if (ok) {
                    hoanKho(conn, maSanPham, soLuong);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }

            if (ok) {
                // Hoàn lại lượt dùng mã giảm giá của hóa đơn này (nếu hóa đơn
                // đã hết sản phẩm còn hiệu lực). Gọi SAU khi commit vì hàm này dùng connection khác.
                hoanLaiMaGiamGiaNeuHetHieuLuc(maDonHang);
            }

            return ok;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean huyDonHang(int matrangthai) {
        String sqlGet = "SELECT madonhang FROM trangthai WHERE matrangthai=?";
        String sqlUpdate = "UPDATE trangthai SET tinhtrang = 3 WHERE matrangthai = ? AND tinhtrang IN (0,1)" + DK_KHONG_KHOA;
        try (Connection conn = dbconnect.getConnection()) {

            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, matrangthai);
            ResultSet rs = psGet.executeQuery();
            if (!rs.next()) {
                return false;
            }
            int maDonHang = rs.getInt("madonhang");

            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setInt(1, matrangthai);
            boolean ok = ps.executeUpdate() > 0;

            if (ok) {
                // Hoàn lại lượt dùng mã giảm giá của hóa đơn này (nếu hóa đơn
                // đã hết sản phẩm còn hiệu lực)
                hoanLaiMaGiamGiaNeuHetHieuLuc(maDonHang);
            }

            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đã xóa capNhatTinhTrang(matrangthai, tinhtrang): hàm này update thẳng
    // trạng thái mà không kiểm tra chủ đơn hàng và không theo đúng thứ tự
    // Chờ xác nhận -> Đang giao -> Đã nhận -> Hủy, nên có thể bị lợi dụng để
    // nhảy thẳng một đơn bất kỳ sang "Hủy đơn". Quy trình cập nhật trạng thái
    // đúng đã có sẵn ở: khachXacNhanDaNhan, khachHoanTraHang, huyDonHang
    // (phía khách hàng) và CapNhatTrangThaiDAO.capNhatTrangThai (phía admin).

    public TrangThai getDonHang(int maTrangThai) {
        String sql = "SELECT * FROM trangthai WHERE matrangthai=?";
        try (
                Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, maTrangThai);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TrangThai tt = new TrangThai();
                tt.setMatrangthai(rs.getInt("matrangthai"));
                tt.setMagiohang(rs.getInt("magiohang"));
                tt.setMakhachhang(rs.getString("makhachhang"));
                tt.setMasanpham(rs.getString("masanpham"));
                tt.setSoluong(rs.getInt("soluong"));
                tt.setKichco(rs.getString("kichco"));
                tt.setGiaban(rs.getInt("giaban"));
                tt.setTinhtrang(rs.getInt("tinhtrang"));
                return tt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Chi tiết MỘT dòng sản phẩm (matrangthai) của hóa đơn: kèm tên, ảnh, danh mục sản phẩm,
     * người vận chuyển đang giữ đơn và cờ đơn hàng đang bị khóa.
     */
    public TrangThai getChiTietSanPham(int maTrangThai) {
        String sql = "SELECT t.matrangthai, t.magiohang, t.makhachhang, t.masanpham, t.soluong, t.kichco, "
                + "t.giaban, t.ngaymua, t.tinhtrang, t.madonhang, t.emailvanchuyen, "
                + "nd.tensanpham, nd.hinhanh, nb.danhmuc, "
                + "CASE WHEN dh.trangthai = N'Đã khóa' THEN 1 ELSE 0 END AS dakhoa "
                + "FROM trangthai t "
                + "LEFT JOIN nguoidung nd ON nd.masanpham = t.masanpham "
                + "LEFT JOIN nguoiban nb ON nb.masanpham = t.masanpham "
                + "LEFT JOIN donhang dh ON dh.madonhang = t.madonhang "
                + "WHERE t.matrangthai = ?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTrangThai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TrangThai tt = new TrangThai();
                    tt.setMatrangthai(rs.getInt("matrangthai"));
                    tt.setMagiohang(rs.getInt("magiohang"));
                    tt.setMakhachhang(rs.getString("makhachhang"));
                    tt.setMasanpham(rs.getString("masanpham"));
                    tt.setSoluong(rs.getInt("soluong"));
                    tt.setKichco(rs.getString("kichco"));
                    tt.setGiaban(rs.getInt("giaban"));
                    tt.setNgaymua(rs.getTimestamp("ngaymua"));
                    tt.setTinhtrang(rs.getInt("tinhtrang"));
                    tt.setMadonhang(rs.getInt("madonhang"));
                    tt.setEmailVanChuyen(rs.getString("emailvanchuyen"));
                    tt.setTensanpham(rs.getString("tensanpham"));
                    tt.setHinhanh(rs.getString("hinhanh"));
                    tt.setDanhmuc(rs.getString("danhmuc"));
                    tt.setDonhangKhoa(rs.getInt("dakhoa") == 1);
                    return tt;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DonHang getThongTinKhachHang(String maKH) {
        String sql = "SELECT TOP 1 * FROM donhang WHERE makhachhang=? ORDER BY madonhang DESC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DonHang dh = new DonHang();
                dh.setMadonhang(rs.getInt("madonhang"));
                dh.setMakhachhang(rs.getString("makhachhang"));
                dh.setHoten(rs.getString("hoten"));
                dh.setSodienthoai(rs.getString("sodienthoai"));
                dh.setEmail(rs.getString("email"));
                dh.setDiachi(rs.getString("diachi"));
                dh.setGhichu(rs.getString("ghichu"));
                return dh;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int tinhTongTien(String maKH) {
        String sql = "SELECT TOP 1 tongtien FROM donhang WHERE makhachhang=? ORDER BY madonhang DESC";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("tongtien");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getDanhSachMaSanPham(String maKH) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT masanpham FROM giohang WHERE makhachhang=?";
        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("masanpham"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // FIX: chỉ update nguoiban, bỏ update thủ công nguoidung vì trigger DB
    // (nguoiban -> nguoidung) đã tự đồng bộ, tránh trừ nguoidung 2 lần.
    public boolean truKhoKhiGiaoHang(String maSanPham, int soLuong) {
        try (Connection conn = dbconnect.getConnection()) {

            String sql2 = "UPDATE nguoiban SET soluongsanpham=soluongsanpham-? WHERE masanpham=? AND soluongsanpham>=?";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, soLuong);
            ps2.setString(2, maSanPham);
            ps2.setInt(3, soLuong);
            ps2.executeUpdate();

            String sql3 = "UPDATE khohang SET soluongton=soluongton-? WHERE masanpham=? AND soluongton>=?";
            PreparedStatement ps3 = conn.prepareStatement(sql3);
            ps3.setInt(1, soLuong);
            ps3.setString(2, maSanPham);
            ps3.setInt(3, soLuong);
            ps3.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
