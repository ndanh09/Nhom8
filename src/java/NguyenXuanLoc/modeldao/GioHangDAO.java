package modeldao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import connect.dbconnect;
import model.GioHang;

public class GioHangDAO {

    // (Đã bỏ giới hạn cố định 10 sản phẩm/loại; giờ chỉ giới hạn theo đúng
    // tồn kho thực tế của từng size khi thêm/sửa số lượng trong giỏ hàng.)

    // 1. Kiểm tra sản phẩm cùng size đã có trong giỏ chưa
    public boolean checkTrungTrongGio(String maKhachHang, String maSanPham, String kichCo) {

        String sql = "SELECT * FROM giohang "
                   + "WHERE makhachhang = ? "
                   + "AND masanpham = ? "
                   + "AND kichco = ?";

        try (
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maKhachHang);
            ps.setString(2, maSanPham);
            ps.setString(3, kichCo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 1b. Lấy số lượng ĐANG CÓ trong giỏ của đúng 1 sản phẩm + size (0 nếu
    // chưa có trong giỏ). Dùng để kiểm tra giới hạn tối đa (mỗi sản phẩm
    // tối đa 10 cái) trước khi cộng thêm số lượng mới vào giỏ.
    public int getSoLuongTrongGio(String maKhachHang, String maSanPham, String kichCo) {

        String sql = "SELECT soluong FROM giohang "
                   + "WHERE makhachhang = ? "
                   + "AND masanpham = ? "
                   + "AND kichco = ?";

        try (
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maKhachHang);
            ps.setString(2, maSanPham);
            ps.setString(3, kichCo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("soluong");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    // 2. Thêm sản phẩm vào giỏ
    // Nếu trùng sản phẩm + size thì cộng số lượng
    public boolean activeThemGioHang(GioHang item) {

        try (
            Connection conn = dbconnect.getConnection()
        ) {


            if (checkTrungTrongGio(
                    item.getMakhachhang(),
                    item.getMasanpham(),
                    item.getKichco())) {


                String sql = "UPDATE giohang "
                           + "SET soluong = soluong + ? "
                           + "WHERE makhachhang = ? "
                           + "AND masanpham = ? "
                           + "AND kichco = ?";


                try(
                    PreparedStatement ps = conn.prepareStatement(sql)
                ){

                    ps.setInt(1,item.getSoluong());
                    ps.setString(2,item.getMakhachhang());
                    ps.setString(3,item.getMasanpham());
                    ps.setString(4,item.getKichco());

                    return ps.executeUpdate() > 0;
                }


            }else{


                String sql = "INSERT INTO giohang "
                           + "(makhachhang,masanpham,soluong,kichco,giaban) "
                           + "VALUES(?,?,?,?,?)";


                try(
                    PreparedStatement ps = conn.prepareStatement(sql)
                ){

                    ps.setString(1,item.getMakhachhang());
                    ps.setString(2,item.getMasanpham());
                    ps.setInt(3,item.getSoluong());
                    ps.setString(4,item.getKichco());
                    ps.setInt(5,item.getGiaban());


                    return ps.executeUpdate() > 0;

                }

            }


        } catch(Exception e){

            e.printStackTrace();

        }


        return false;
    }



    // 3. Lấy danh sách giỏ hàng theo khách hàng
    // FIX: JOIN thêm bảng nguoidung để lấy tensanpham -> dùng tra các size
    // thực tế đang có của sản phẩm đó (đồng bộ size với nguoidung).
    public ArrayList<GioHang> getDanhSachGioHang(String maKhachHang){

        ArrayList<GioHang> list = new ArrayList<>();


        String sql = "SELECT gh.magiohang,"
                   + "gh.makhachhang,"
                   + "gh.masanpham,"
                   + "nd.tensanpham,"
                   + "gh.soluong,"
                   + "gh.kichco,"
                   + "gh.giaban,"
                   + "nd.soluongsanpham AS tonkho "
                   + "FROM giohang gh "
                   + "LEFT JOIN nguoidung nd ON gh.masanpham = nd.masanpham "
                   + "WHERE gh.makhachhang = ?";


        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setString(1,maKhachHang);


            ResultSet rs = ps.executeQuery();


            while(rs.next()){


                GioHang gh = new GioHang();


                gh.setMagiohang(
                    rs.getInt("magiohang")
                );


                gh.setMakhachhang(
                    rs.getString("makhachhang")
                );


                gh.setMasanpham(
                    rs.getString("masanpham")
                );


                gh.setTensanpham(
                    rs.getString("tensanpham")
                );


                gh.setSoluong(
                    rs.getInt("soluong")
                );


                gh.setKichco(
                    rs.getString("kichco")
                );


                gh.setGiaban(
                    rs.getInt("giaban")
                );

                // Tồn kho hiện tại (để giỏ hàng hiển thị & giới hạn số lượng mua)
                gh.setTonKho(
                    rs.getInt("tonkho")
                );

                list.add(gh);

            }


        }catch(Exception e){

            e.printStackTrace();

        }


        return list;

    }

    // FIX MỚI: Lấy danh sách size THỰC TẾ đang có trong bảng nguoidung cho
    // 1 tên sản phẩm - dùng để đổ vào dropdown size ở giỏ hàng, thay vì
    // hardcode cứng S/M/L/XL như trước (sản phẩm nguoidung có size gì thì
    // giỏ hàng chỉ được hiện đúng size đó).
    public ArrayList<String> layDanhSachSizeTheoTen(String tenSanPham){

        ArrayList<String> list = new ArrayList<>();

        String sql = "SELECT DISTINCT kichco FROM nguoidung "
                   + "WHERE tensanpham = ? "
                   + "ORDER BY kichco";

        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setString(1, tenSanPham);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(rs.getString("kichco"));
            }

        }catch(Exception e){

            e.printStackTrace();

        }

        return list;

    }
    // FIX MỚI: Tra đúng "masanpham" (mã đầy đủ, VD mu1-XXL) tương ứng với
    // 1 tên sản phẩm + 1 size cụ thể. Dùng khi khách ĐỔI SIZE trong giỏ
    // hàng, để cập nhật lại đúng masanpham khớp với size mới chọn -
    // tránh tình trạng giỏ hiển thị size mới nhưng kho vẫn bị trừ theo
    // masanpham (size) cũ.
    public String layMaSanPhamTheoSize(String tenSanPham, String kichCo) {

        String sql = "SELECT masanpham FROM nguoidung "
                   + "WHERE tensanpham = ? "
                   + "AND kichco = ?";

        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setString(1, tenSanPham);
            ps.setString(2, kichCo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("masanpham");
            }

        }catch(Exception e){

            e.printStackTrace();

        }

        return null;
    }

    // 4. Cập nhật số lượng + kích cỡ trong giỏ hàng
    // FIX: trước đây chỉ update soluong + kichco, KHÔNG update masanpham
    // (mã sản phẩm gắn với size cụ thể, VD mu1-L / mu1-XXL). Hậu quả: khi
    // khách đổi size trong giỏ, cột kichco đổi đúng nhưng masanpham vẫn
    // là mã của size CŨ -> lúc kiểm tra tồn kho / trừ kho khi thanh toán
    // (đều dựa vào masanpham) bị trừ NHẦM sang size cũ thay vì size mới
    // khách vừa chọn. Nay update thêm cả masanpham cho khớp với size mới.
    public boolean updateGioHang(int maGioHang, int soLuongMoi, String kichCo, String maSanPhamMoi){

        String sql = "UPDATE giohang "
                   + "SET soluong = ?, kichco = ?, masanpham = ? "
                   + "WHERE magiohang = ?";


        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setInt(1, soLuongMoi);
            ps.setString(2, kichCo);
            ps.setString(3, maSanPhamMoi);
            ps.setInt(4, maGioHang);


            return ps.executeUpdate() > 0;


        }catch(Exception e){

            e.printStackTrace();

        }


        return false;
    }



    // 5. Cập nhật số lượng (giữ tương thích code cũ)
    public boolean updateSoLuong(int maGioHang, int soLuongMoi){

        String sql = "UPDATE giohang "
                   + "SET soluong = ? "
                   + "WHERE magiohang = ?";


        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setInt(1,soLuongMoi);
            ps.setInt(2,maGioHang);


            return ps.executeUpdate() > 0;


        }catch(Exception e){

            e.printStackTrace();

        }


        return false;
    }



    // 6. Xóa sản phẩm khỏi giỏ hàng
    public boolean deleteSanPhamKhoiGio(int maGioHang){

        String sql = "DELETE FROM giohang "
                   + "WHERE magiohang = ?";


        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setInt(1,maGioHang);


            return ps.executeUpdate() > 0;


        }catch(Exception e){

            e.printStackTrace();

        }


        return false;
    }




    // 6b. Xóa sản phẩm khỏi giỏ hàng CỦA ĐÚNG khách hàng đó.
    // FIX: bản deleteSanPhamKhoiGio(int) chỉ lọc theo magiohang nên khách này xóa được giỏ của khách khác.
    public boolean deleteSanPhamKhoiGio(int maGioHang, String maKhachHang){

        String sql = "DELETE FROM giohang "
                   + "WHERE magiohang = ? AND makhachhang = ?";

        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setInt(1,maGioHang);
            ps.setString(2,maKhachHang);

            return ps.executeUpdate() > 0;

        }catch(Exception e){

            e.printStackTrace();

        }

        return false;
    }

    // 7. Lấy tồn kho sản phẩm
    public int getTonKho(String maSanPham){

        String sql = "SELECT soluongsanpham "
                   + "FROM nguoidung "
                   + "WHERE masanpham = ?";


        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setString(1,maSanPham);


            ResultSet rs = ps.executeQuery();


            if(rs.next()){

                return rs.getInt("soluongsanpham");

            }


        }catch(Exception e){

            e.printStackTrace();

        }


        return 0;
    }




    // 8. Trừ tồn kho khi mua hàng
    // FIX: chỉ update bảng nguoiban. Bảng nguoidung được trigger DB
    // (nguoiban -> nguoidung) tự đồng bộ. Trước đây code update cả 2 bảng
    // thủ công => nguoidung bị trừ 2 lần (1 lần update trực tiếp + 1 lần do
    // trigger kích hoạt khi update nguoiban), khiến mua 1 sản phẩm bị trừ 2.
    // Nếu DB của bạn KHÔNG có trigger đồng bộ nguoiban -> nguoidung, hãy bật
    // lại đoạn update nguoidung bên dưới (đã comment) và xoá trigger ở DB.
    //
    // FIX MỚI: bảng khohang (Quản lý kho hàng) trước đây KHÔNG hề bị trừ khi
    // có đơn hàng, nên "SL Tồn" ở đó luôn giữ nguyên số nhập ban đầu dù đã
    // bán hết, nhìn như tồn kho ảo/gấp đôi so với thực tế (Quản lý size).
    // Nay trừ luôn soluongton bên khohang, đồng bộ đúng số thực đã bán.
    public boolean truTonKho(String maSanPham,int soLuongMua){

        String sqlNguoiBan = "UPDATE nguoiban "
                   + "SET soluongsanpham = soluongsanpham - ?, "
                   // FIX: giữ nguyên "Ngừng bán" ('0'); trước đây mọi đơn mua đều đặt lại thành '1'/'2'.
                   + "trangthai = CASE WHEN trangthai = '0' THEN '0' "
                   + "WHEN soluongsanpham - ? <= 0 "
                   + "THEN '2' ELSE '1' END "
                   + "WHERE masanpham = ? "
                   + "AND soluongsanpham >= ?";

        String sqlKhoHang = "UPDATE khohang "
                   + "SET soluongton = CASE WHEN soluongton - ? < 0 THEN 0 ELSE soluongton - ? END "
                   + "WHERE masanpham = ?";

        /* Không dùng nữa vì trigger DB đã tự đồng bộ nguoiban -> nguoidung.
           Chỉ bật lại nếu chắc chắn DB KHÔNG có trigger này.
        String sqlNguoiDung = "UPDATE nguoidung "
                   + "SET soluongsanpham = soluongsanpham - ?, "
                   + "trangthai = CASE "
                   + "WHEN soluongsanpham - ? <= 0 "
                   + "THEN '2' ELSE '1' END "
                   + "WHERE masanpham = ? "
                   + "AND soluongsanpham >= ?";
        */

        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement psNb = conn.prepareStatement(sqlNguoiBan)
        ){

            psNb.setInt(1,soLuongMua);
            psNb.setInt(2,soLuongMua);
            psNb.setString(3,maSanPham);
            psNb.setInt(4,soLuongMua);
            boolean okNb = psNb.executeUpdate() > 0;

            if (okNb) {
                try (PreparedStatement psKho = conn.prepareStatement(sqlKhoHang)) {
                    psKho.setInt(1, soLuongMua);
                    psKho.setInt(2, soLuongMua);
                    psKho.setString(3, maSanPham);
                    psKho.executeUpdate();
                }
            }

            return okNb;

        }catch(Exception e){

            e.printStackTrace();

        }


        return false;

    }




    // 9. Cộng lại tồn kho khi hủy đơn
    // FIX: chỉ update bảng nguoiban. Bảng nguoidung được trigger DB
    // (nguoiban -> nguoidung) tự đồng bộ. Trước đây code cộng thủ công cả 2
    // bảng => nguoidung bị cộng 2 lần (1 lần update trực tiếp + 1 lần do
    // trigger), khiến tồn kho khách nhìn thấy bị đội lên nhiều hơn cả số
    // lượng đã nhập mỗi khi có đơn bị hủy.
    //
    // FIX MỚI: cộng lại đúng bên khohang (SL Tồn) khi đơn bị hủy, đồng bộ
    // với việc trừ ở truTonKho() phía trên.
    public boolean congTonKho(String maSanPham,int soLuong){

        // FIX: chỉ chuyển "Hết hàng" ('2') -> "Còn hàng" ('1'). Trước đây điều kiện "soluongsanpham + ? > 0"
        // luôn đúng nên sản phẩm "Ngừng bán" ('0') bị bật lại thành "Còn hàng" mỗi khi có đơn bị hủy.
        String sqlNguoiBan = "UPDATE nguoiban "
                   + "SET soluongsanpham = soluongsanpham + ?, "
                   + "trangthai = CASE WHEN trangthai = '2' THEN '1' ELSE trangthai END "
                   + "WHERE masanpham = ?";

        String sqlKhoHang = "UPDATE khohang "
                   + "SET soluongton = soluongton + ? "
                   + "WHERE masanpham = ?";


        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement psNb = conn.prepareStatement(sqlNguoiBan)
        ){

            psNb.setInt(1,soLuong);
            psNb.setString(2,maSanPham);
            boolean okNb = psNb.executeUpdate() > 0;

            if (okNb) {
                try (PreparedStatement psKho = conn.prepareStatement(sqlKhoHang)) {
                    psKho.setInt(1, soLuong);
                    psKho.setString(2, maSanPham);
                    psKho.executeUpdate();
                }
            }

            return okNb;



        }catch(Exception e){

            e.printStackTrace();

        }


        return false;

    }    // 10. Xóa toàn bộ giỏ hàng sau khi đặt hàng
    public boolean clearCart(String maKhachHang){

        String sql = "DELETE FROM giohang "
                   + "WHERE makhachhang = ?";


        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){

            ps.setString(1,maKhachHang);


            return ps.executeUpdate() > 0;


        }catch(Exception e){

            e.printStackTrace();

        }


        return false;

    }




    // 11. Tạo dữ liệu trạng thái đơn hàng
    // FIX: trước đây trả về boolean nên khi đặt nhiều sản phẩm trong 1 lần
    // thanh toán, servlet không biết được TỪNG mã trạng thái (matrangthai)
    // vừa được tạo ra là mã nào -> thông báo "Đặt hàng thành công" chỉ hiện
    // được đúng 1 mã (madonhang) dù bảng danh sách bên dưới hiển thị nhiều
    // dòng #ORD-x khác nhau (mỗi dòng là 1 matrangthai). Nay trả về đúng
    // matrangthai vừa insert (dùng RETURN_GENERATED_KEYS) để servlet gom đủ
    // tất cả mã và hiển thị hết trong thông báo. Trả về -1 nếu insert lỗi.
    public int taoDonHang(GioHang item,int maDonHang,int giaSauGiam){


        String sql = "INSERT INTO trangthai "
                   + "(madonhang,"
                   + "magiohang,"
                   + "makhachhang,"
                   + "masanpham,"
                   + "soluong,"
                   + "kichco,"
                   + "giaban,"
                   + "ngaymua,"
                   + "tinhtrang)"
                   + " VALUES(?,?,?,?,?,?,?,GETDATE(),0)";



        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)
        ){


            ps.setInt(1,maDonHang);

            ps.setInt(2,item.getMagiohang());

            ps.setString(3,item.getMakhachhang());

            ps.setString(4,item.getMasanpham());

            ps.setInt(5,item.getSoluong());

            ps.setString(6,item.getKichco());

            ps.setInt(7,giaSauGiam);



            int soDongAnhHuong = ps.executeUpdate();

            if (soDongAnhHuong > 0) {

                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {

                    return rs.getInt(1);

                }

            }



        }catch(Exception e){

            e.printStackTrace();

        }



        return -1;

    }




    // 12. Tính tổng tiền giỏ hàng
    public int getTongTienGioHang(String maKhachHang){


        int tongTien = 0;


        String sql = "SELECT SUM(giaban * soluong) AS tong "
                   + "FROM giohang "
                   + "WHERE makhachhang = ?";



        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){


            ps.setString(1,maKhachHang);


            ResultSet rs = ps.executeQuery();



            if(rs.next()){

                tongTien = rs.getInt("tong");

            }



        }catch(Exception e){

            e.printStackTrace();

        }



        return tongTien;

    }





    // 13. Đếm số lượng sản phẩm trong giỏ
    public int countCart(String maKhachHang){


        int count = 0;


        String sql = "SELECT COUNT(*) AS soluong "
                   + "FROM giohang "
                   + "WHERE makhachhang = ?";



        try(
            Connection conn = dbconnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){


            ps.setString(1,maKhachHang);


            ResultSet rs = ps.executeQuery();



            if(rs.next()){

                count = rs.getInt("soluong");

            }



        }catch(Exception e){

            e.printStackTrace();

        }



        return count;

    }



}