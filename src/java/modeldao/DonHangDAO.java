package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.DonHang;

public class DonHangDAO {

    // Độ dài tối đa (ký tự) của cột donhang.masanpham: -2 = chưa tra, -1 = không giới hạn (MAX) / không biết.
    private static volatile int doDaiMasanpham = -2;

    /**
     * Cột donhang.masanpham chỉ là chuỗi thông tin (các mã sản phẩm nối bằng dấu phẩy); chi tiết từng
     * sản phẩm nằm ở bảng trangthai. Khi mua nhiều sản phẩm cùng lúc chuỗi này có thể dài hơn độ dài
     * cột -> INSERT lỗi -> "Đặt hàng thất bại". Nay cắt gọn (ở ranh giới dấu phẩy) cho vừa cột.
     */
    private String catChoVuaCot(Connection con, String chuoi) {
        if (chuoi == null) {
            return null;
        }
        int max = doDaiMasanpham;
        if (max == -2) {
            max = -1;
            String sql = "SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS "
                    + "WHERE TABLE_NAME='donhang' AND COLUMN_NAME='masanpham'";
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int n = rs.getInt(1);
                    if (!rs.wasNull() && n > 0) {
                        max = n;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            doDaiMasanpham = max;
        }
        if (max <= 0 || chuoi.length() <= max) {
            return chuoi;
        }
        String cat = chuoi.substring(0, max);
        int phay = cat.lastIndexOf(',');
        if (phay > 0) {
            cat = cat.substring(0, phay);
        }
        return cat;
    }

    /**
     * Lưu thông tin mã giảm giá vào đơn (để hóa đơn hiển thị và để hoàn lại lượt dùng mã khi đơn bị hủy).
     * Làm riêng sau khi đơn đã tạo: nếu DB chưa có các cột này thì chỉ mất thông tin mã giảm, KHÔNG làm
     * hỏng việc đặt hàng.
     */
    public boolean luuThongTinGiamGia(int maDonHang, String maGiamGia, int soTienGiam, int tongSauGiam) {
        String sql = "UPDATE donhang SET magiamgia=?, sotiengiam=?, tongtiensaugiam=? WHERE madonhang=?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maGiamGia);
            ps.setInt(2, soTienGiam);
            ps.setInt(3, tongSauGiam);
            ps.setInt(4, maDonHang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean themDonHang(DonHang dh) {

        // FIX: mã đơn hàng (madonhang) là thông tin BẮT BUỘC, dùng ngay sau
        // đó để tạo các dòng chi tiết sản phẩm trong bảng "trangthai" (mỗi
        // sản phẩm trong đơn = 1 dòng, có cột madonhang tham chiếu đơn này).
        // Trước đây tách INSERT và lấy ID (SCOPE_IDENTITY) thành 2 bước
        // riêng: nếu bảng donhang có trigger, SCOPE_IDENTITY() có thể KHÔNG
        // lấy đúng ID (trả về NULL -> 0) dù đơn đã lưu thành công. Hậu quả:
        // các dòng chi tiết sản phẩm bị tạo với madonhang SAI (=0), không
        // khớp với đơn hàng thật -> đặt hàng báo "thành công" nhưng sản
        // phẩm không hiện lên ở trang "Trạng thái đơn hàng" (đơn hàng bị
        // "mồ côi", không có sản phẩm nào đi kèm).
        //
        // Nay: gộp INSERT + lấy id vừa tạo vào MỘT lần thực thi duy nhất
        // bằng OUTPUT INTO bảng biến tạm (@InsertedId) - cách lấy id AN
        // TOÀN VỚI TRIGGER trên SQL Server (khác SCOPE_IDENTITY, không bị
        // trigger làm sai lệch). Nếu vì lý do nào đó không lấy được id thì
        // coi luôn là THẤT BẠI (thay vì âm thầm dùng id sai =0), để không
        // tạo ra đơn hàng "mồ côi" không có sản phẩm nào đi kèm.
        String sql = "DECLARE @InsertedId TABLE (madonhang INT); "
                + "INSERT INTO donhang(makhachhang,hoten,sodienthoai,email,diachi,tinhthanh,ghichu,masanpham,tongtien,ngaydat) "
                + "OUTPUT INSERTED.madonhang INTO @InsertedId "
                + "VALUES (?,?,?,?,?,?,?,?,?,GETDATE()); "
                + "SELECT madonhang FROM @InsertedId;";

        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dh.getMakhachhang());
            ps.setString(2, dh.getHoten());
            ps.setString(3, dh.getSodienthoai());
            ps.setString(4, dh.getEmail());
            ps.setString(5, dh.getDiachi());
            ps.setString(6, dh.getTinhthanh());
            ps.setString(7, dh.getGhichu());
            ps.setString(8, catChoVuaCot(con, dh.getMasanpham()));
            ps.setInt(9, dh.getTongtien());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    dh.setMadonhang(rs.getInt(1));
                    return true;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;
    }

    public DonHang getDonHangMoiNhat(String maKhachHang) {
        String sql = "SELECT TOP 1 * FROM donhang WHERE makhachhang=? ORDER BY madonhang DESC";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DonHang dh = new DonHang();
                dh.setMadonhang(rs.getInt("madonhang"));
                dh.setMakhachhang(rs.getString("makhachhang"));
                dh.setHoten(rs.getString("hoten"));
                dh.setSodienthoai(rs.getString("sodienthoai"));
                dh.setEmail(rs.getString("email"));
                dh.setDiachi(rs.getString("diachi"));
                dh.setTinhthanh(rs.getString("tinhthanh"));
                dh.setGhichu(rs.getString("ghichu"));
                dh.setMasanpham(rs.getString("masanpham"));
                dh.setTongtien(rs.getInt("tongtien"));
                dh.setNgaydat(rs.getTimestamp("ngaydat"));
                dh.setMagiamgia(rs.getString("magiamgia"));
                dh.setSotiengiam(rs.getInt("sotiengiam"));
                dh.setTongtiensaugiam(rs.getInt("tongtiensaugiam"));
                return dh;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DonHang getDonHangTheoMa(int maDonHang) {
        String sql = "SELECT * FROM donhang WHERE madonhang=?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maDonHang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DonHang dh = new DonHang();
                dh.setMadonhang(rs.getInt("madonhang"));
                dh.setMakhachhang(rs.getString("makhachhang"));
                dh.setHoten(rs.getString("hoten"));
                dh.setSodienthoai(rs.getString("sodienthoai"));
                dh.setEmail(rs.getString("email"));
                dh.setDiachi(rs.getString("diachi"));
                dh.setTinhthanh(rs.getString("tinhthanh"));
                dh.setGhichu(rs.getString("ghichu"));
                dh.setMasanpham(rs.getString("masanpham"));
                dh.setTongtien(rs.getInt("tongtien"));
                dh.setNgaydat(rs.getTimestamp("ngaydat"));
                dh.setMagiamgia(rs.getString("magiamgia"));
                dh.setSotiengiam(rs.getInt("sotiengiam"));
                dh.setTongtiensaugiam(rs.getInt("tongtiensaugiam"));
                return dh;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean capNhatThongTin(DonHang dh) {
        String sql = "UPDATE donhang SET hoten=?,sodienthoai=?,email=?,diachi=?,tinhthanh=?,ghichu=?,masanpham=?,tongtien=? WHERE madonhang=?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dh.getHoten());
            ps.setString(2, dh.getSodienthoai());
            ps.setString(3, dh.getEmail());
            ps.setString(4, dh.getDiachi());
            ps.setString(5, dh.getTinhthanh());
            ps.setString(6, dh.getGhichu());
            ps.setString(7, catChoVuaCot(con, dh.getMasanpham()));
            ps.setInt(8, dh.getTongtien());
            ps.setInt(9, dh.getMadonhang());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaDonHang(int maDonHang) {
        String sql = "DELETE FROM donhang WHERE madonhang=?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maDonHang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean kiemTraMaSanPham(String maSanPham) {
        String sql = "SELECT masanpham FROM nguoidung WHERE masanpham=?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int tinhTongTien(String maKhachHang) {
        String sql = "SELECT ISNULL(SUM(giaban*soluong),0) tongtien FROM giohang WHERE makhachhang=?";
        try (Connection con = dbconnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("tongtien");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
