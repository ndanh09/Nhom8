package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;
import model.TrangThai; // Sử dụng chung model TrangThai sẵn có của bạn

public class CapNhatTrangThaiDAO {
    /**
     * 1. Hàm lấy TOÀN BỘ đơn hàng của TẤT CẢ khách hàng trong hệ thống Dùng để
     * đổ dữ liệu lên bảng quản trị của Admin
     */
    public List<TrangThai> getAllDanhSachDonHang() {
        List<TrangThai> list = new ArrayList<>();
        String sql = "SELECT t.matrangthai, t.magiohang, t.makhachhang, t.masanpham, t.soluong, t.kichco, t.giaban, "
                + "t.ngaymua, t.tinhtrang, t.madonhang, "
                + "CASE WHEN d.trangthai = N'Đã khóa' THEN 1 ELSE 0 END AS dakhoa "
                + "FROM trangthai t LEFT JOIN donhang d ON d.madonhang = t.madonhang "
                + "ORDER BY t.ngaymua DESC";

        try (Connection conn = dbconnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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
                tt.setDonhangKhoa(rs.getInt("dakhoa") == 1);

                list.add(tt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Kiểm tra thứ tự chuyển trạng thái có hợp lệ không (dành cho ADMIN):
     * 0 Chờ xác nhận -> 1 Đang giao (duyệt đơn, chuyển cho vận chuyển xử lý)
     *                    hoặc 3 Hủy đơn
     * 1 Đang giao    -> CHỈ được 3 Hủy đơn. Việc chuyển sang "Đã giao" (4) là
     *                    của người vận chuyển (VanChuyenDAO.capNhatDaGiao),
     *                    admin không còn quyền tự đánh dấu đã giao nữa.
     * 4 Đã giao      -> CHỈ được 3 Hủy đơn (admin chỉ xem hoặc hủy, không tự
     *                    xác nhận thay khách). Việc chuyển sang "Đã nhận" (2)
     *                    là của khách hàng (TrangThaiDAO.khachXacNhanDaNhan).
     * 2 Đã nhận      -> 3 Hủy đơn (cho phép hủy/trả hàng sau khi đã nhận)
     * 3 Hủy đơn là trạng thái cuối, không đổi được nữa.
     */
    private boolean chuyenTrangThaiHopLe(int tinhTrangCu, int tinhTrangMoi) {
        switch (tinhTrangCu) {
            case 0:
                return tinhTrangMoi == 1 || tinhTrangMoi == 3;
            case 1:
                return tinhTrangMoi == 3;
            case 4:
                return tinhTrangMoi == 3;
            case 2:
                return tinhTrangMoi == 3;
            default:
                return false;
        }
    }

    /**
     * 2. Hàm cập nhật trạng thái đơn hàng trực tiếp vào bảng trangthai Gọi
     * Store Procedure sp_CapNhatTrangThaiDonHang đã viết ở SQL
     */
    public boolean capNhatTrangThai(int maTrangThai, int tinhTrangMoi) {
        try (Connection conn = dbconnect.getConnection()) {
            String sqlGet = "SELECT masanpham, soluong, tinhtrang, madonhang FROM trangthai WHERE matrangthai=?";
            String maSanPham;
            int soLuong;
            int tinhTrangCu;
            int maDonHang;
            try (PreparedStatement psGet = conn.prepareStatement(sqlGet)) {
                psGet.setInt(1, maTrangThai);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    maSanPham = rs.getString("masanpham");
                    soLuong = rs.getInt("soluong");
                    tinhTrangCu = rs.getInt("tinhtrang");
                    maDonHang = rs.getInt("madonhang");
                }
            }

            // Chặn chuyển trạng thái không đúng thứ tự (vd: Chờ xác nhận -> Đã nhận
            // trực tiếp, hoặc cập nhật lùi lại trạng thái trước đó).
            if (!chuyenTrangThaiHopLe(tinhTrangCu, tinhTrangMoi)) {
                return false;
            }

            // Lưu ý (giữ nguyên từ bản trước): KHÔNG trừ kho khi chuyển sang "Đã nhận" vì kho đã được
            // trừ đúng 1 lần ngay lúc thanh toán (GioHangDAO.truTonKho).
            //
            // FIX: HOÀN KHO KHI HỦY ĐƠN. Trước đây chỉ hoàn kho khi hủy đơn đang ở trạng thái "Đã nhận"
            // (tinhTrangCu == 2), trong khi kho đã bị trừ từ lúc thanh toán với MỌI đơn -> admin hủy đơn
            // đang Chờ xác nhận / Đang giao / Đã giao thì số hàng đó mất khỏi kho vĩnh viễn. Nay hủy
            // (chuyển sang 3) từ bất kỳ trạng thái nào cũng hoàn kho đúng 1 lần.
            // Đồng thời đổi trạng thái TRƯỚC bằng UPDATE có điều kiện "AND tinhtrang=<cũ>" (chống 2 admin/
            // bấm đúp cùng hủy 1 đơn -> hoàn kho 2 lần) và chỉ hoàn kho khi đổi thành công, trong 1 transaction.
            boolean ok;
            conn.setAutoCommit(false);
            try {
                String sqlUpdate = "UPDATE trangthai SET tinhtrang=? WHERE matrangthai=? AND tinhtrang=?" + TrangThaiDAO.DK_KHONG_KHOA;
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setInt(1, tinhTrangMoi);
                    psUpdate.setInt(2, maTrangThai);
                    psUpdate.setInt(3, tinhTrangCu);
                    ok = psUpdate.executeUpdate() > 0;
                }
                if (ok && tinhTrangMoi == 3) {
                    TrangThaiDAO.hoanKho(conn, maSanPham, soLuong);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }

            if (ok && tinhTrangMoi == 3) {
                // Hoàn lượt dùng mã giảm giá của hóa đơn (nếu cả hóa đơn đã bị hủy), giống đường khách tự hủy.
                new TrangThaiDAO().hoanLaiMaGiamGiaNeuHetHieuLuc(maDonHang);
            }

            return ok;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
