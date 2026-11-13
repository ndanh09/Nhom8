package modeldao;

import connect.dbconnect;
import java.sql.*;
import java.util.ArrayList;
import model.quanlykhohang;

/**
 * FIX (toàn file):
 *  - Trước đây Connection được mở 1 lần duy nhất trong constructor và giữ làm field cho cả vòng
 *    đời ứng dụng (DAO nằm trong field của servlet). Hậu quả: 1 kết nối dùng chung cho mọi request
 *    (JDBC Connection không an toàn khi nhiều luồng dùng cùng lúc); nếu SQL Server restart/ngắt
 *    kết nối hoặc DB chưa bật lúc khởi động (conn = null) thì mọi chức năng kho hàng chết cho tới
 *    khi deploy lại; PreparedStatement/ResultSet không bao giờ đóng. Nay mỗi hàm tự mở/đóng kết nối.
 *  - themHoacCongDon dùng "CURRENT_DATE" - không phải cú pháp T-SQL của SQL Server nên nhánh cộng
 *    dồn kho luôn lỗi (bị nuốt trong catch). Đổi sang CAST(GETDATE() AS DATE).
 *  - getByID trước đây nuốt lỗi hoàn toàn (catch rỗng) -> nay in lỗi ra log.
 */
public class quanlykhohangdao {

    private quanlykhohang docDong(ResultSet rs) throws SQLException {
        return new quanlykhohang(
                rs.getInt("makho"),
                rs.getString("masanpham"),
                rs.getInt("soluongnhap"),
                rs.getInt("soluongton"),
                rs.getString("vitri"),
                rs.getDate("ngaynhapkho"),
                rs.getString("nhacungcap")
        );
    }

    //Hiển thị
    public ArrayList<quanlykhohang> getAll() {

        ArrayList<quanlykhohang> list = new ArrayList<>();

        String sql = "select * from khohang";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(docDong(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    //Tìm kiếm theo Mã SP, Vị trí hoặc Nhà cung cấp
    public ArrayList<quanlykhohang> search(String keyword) {

        ArrayList<quanlykhohang> list = new ArrayList<>();

        String sql = "select * from khohang where masanpham like ? or vitri like ? or nhacungcap like ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(docDong(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    //Lấy theo Mã sản phẩm
    public quanlykhohang getByMaSanPham(String masanpham) {

        String sql = "select * from khohang where masanpham=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

    //Đồng bộ từ trang Người bán: nếu sản phẩm đã có trong kho thì cộng dồn thêm
    //số lượng vừa nhập vào SL Nhập & SL Tồn, nếu chưa có thì tạo dòng kho mới.
    //Dùng khi người bán thêm/sửa sản phẩm để SL Nhập tự cập nhật mà không cần
    //admin phải vào tay nhập lại ở trang Kho hàng.
    public void themHoacCongDon(String masanpham, int soLuongThem, String nhacungcap) {

        if (soLuongThem <= 0) {
            return;
        }

        quanlykhohang hienTai = getByMaSanPham(masanpham);

        try (Connection conn = dbconnect.getConnection()) {

            if (hienTai == null) {

                String sql = "insert into khohang(masanpham,soluongnhap,soluongton,vitri,nhacungcap) values(?,?,?,?,?)";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, masanpham);
                    ps.setInt(2, soLuongThem);
                    ps.setInt(3, soLuongThem);
                    ps.setString(4, "");
                    ps.setString(5, nhacungcap == null ? "" : nhacungcap);

                    ps.executeUpdate();
                }

            } else {

                // Cộng dồn ngay trong SQL (soluongnhap = soluongnhap + ?) thay vì đọc-rồi-ghi lại
                // bằng Java để 2 lần nhập cùng lúc không ghi đè mất nhau.
                String sql = "update khohang set soluongnhap=soluongnhap+?,soluongton=soluongton+?,"
                        + "ngaynhapkho=CAST(GETDATE() AS DATE) where makho=?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, soLuongThem);
                    ps.setInt(2, soLuongThem);
                    ps.setInt(3, hienTai.getMakho());

                    ps.executeUpdate();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Cập nhật hàng loạt: chỉ đổi Vị trí và/hoặc Nhà cung cấp, không đụng SL Nhập/SL Tồn.
    //Ô nào để trống thì giữ nguyên giá trị cũ của ô đó.
    public boolean updateViTriNhaCungCap(int id, String vitri, String nhacungcap) {

        StringBuilder sql = new StringBuilder("update khohang set ");
        ArrayList<Object> params = new ArrayList<>();
        boolean coCot = false;

        if (vitri != null && !vitri.trim().isEmpty()) {
            sql.append("vitri=?");
            params.add(vitri.trim());
            coCot = true;
        }

        if (nhacungcap != null && !nhacungcap.trim().isEmpty()) {
            if (coCot) {
                sql.append(",");
            }
            sql.append("nhacungcap=?");
            params.add(nhacungcap.trim());
            coCot = true;
        }

        if (!coCot) {
            return false;
        }

        sql.append(" where makho=?");
        params.add(id);

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, (String) p);
                }
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    //Lấy theo ID
    public quanlykhohang getByID(int id) {

        String sql = "select * from khohang where makho=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

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

    //Sửa
    public boolean update(quanlykhohang k) {

        String sql = "update khohang set masanpham=?,soluongnhap=?,soluongton=?,vitri=?,nhacungcap=? where makho=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, k.getMasanpham());
            ps.setInt(2, k.getSoluongnhap());
            ps.setInt(3, k.getSoluongton());
            ps.setString(4, k.getVitri());
            ps.setString(5, k.getNhacungcap());
            ps.setInt(6, k.getMakho());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    //Xóa
    public boolean delete(int id) {

        String sql = "delete from khohang where makho=?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

}
