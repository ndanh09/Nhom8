package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import model.DonHang;
import model.GioHang;
import model.dangkytaikhoan;
import modeldao.DonHangDAO;
import modeldao.GioHangDAO;
import modeldao.dangkydao;

@WebServlet("/donhangservlet")
public class DonHangServlet extends HttpServlet {

    private DonHangDAO donHangDAO = new DonHangDAO();
    private GioHangDAO ghDAO = new GioHangDAO();
    private dangkydao dkDAO = new dangkydao();

    // FIX (mua nhiều sản phẩm bị tách thành nhiều đơn / đơn bị tạo trùng): mỗi khách chỉ được xử lý
    // MỘT yêu cầu thanh toán tại một thời điểm. Trước đây bấm "Xác nhận đặt hàng" 2 lần liên tiếp
    // (hoặc 2 tab) làm 2 request chạy song song cùng đọc 1 giỏ hàng -> tạo 2 đơn cho cùng sản phẩm.
    private static final ConcurrentHashMap<String, Object> KHOA_THANH_TOAN = new ConcurrentHashMap<>();

    // Danh sách sản phẩm KHÁCH ĐÃ TÍCH CHỌN ở giỏ hàng (giohangservlet lưu vào session "listCart" khi
    // bấm "TIẾN HÀNH THANH TOÁN"). Không còn rơi về "lấy toàn bộ giỏ hàng" như trước: khi session đã
    // hết/đã đặt xong, một request gửi lặp sẽ mua nhầm cả những sản phẩm khách KHÔNG chọn.
    @SuppressWarnings("unchecked")
    private ArrayList<GioHang> layDanhSachDaChon(HttpSession session) {

        List<GioHang> selected = (List<GioHang>) session.getAttribute("listCart");

        if (selected == null || selected.isEmpty()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(selected);
    }

    // Trang giỏ hàng hiển thị thông báo qua session "toastMessage"/"toastType" (không phải "error").
    private void baoLoiGioHang(HttpSession session, String message) {
        session.setAttribute("toastType", "error");
        session.setAttribute("toastMessage", message);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        String maKH = (String) session.getAttribute("email");

        if (maKH == null || maKH.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        ArrayList<GioHang> listCart = layDanhSachDaChon(session);

        if (listCart.isEmpty()) {
            baoLoiGioHang(session, "Vui lòng chọn sản phẩm ở giỏ hàng rồi bấm thanh toán.");
            response.sendRedirect(request.getContextPath() + "/giohangservlet");
            return;
        }

        Integer tongTien = (Integer) session.getAttribute("thanhTien");

        if (tongTien == null) {

            int total = 0;

            for (GioHang item : listCart) {
                total += item.getGiaban() * item.getSoluong();
            }

            tongTien = total;
        }

        request.setAttribute("tongTien", tongTien);

        // Danh sách "Mã sản phẩm" hiển thị ở form thanh toán = các sản phẩm ĐÃ CHỌN.
        List<String> listMaSP = new ArrayList<>();
        for (GioHang item : listCart) {
            listMaSP.add(item.getMasanpham());
        }

        request.setAttribute("listMaSP", listMaSP);

        // Lấy thông tin khách hàng đã lưu từ lần thanh toán trước (nếu có) để tự điền form
        dangkytaikhoan thongTinKH = dkDAO.layThongTin(maKH);
        request.setAttribute("thongTinKH", thongTinKH);

        request.getRequestDispatcher("/view/thongtinkhachhang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        String maKhachHang = (String) session.getAttribute("email");

        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        Object khoa = KHOA_THANH_TOAN.computeIfAbsent(maKhachHang.trim().toLowerCase(), k -> new Object());

        synchronized (khoa) {
            xuLyDatHang(request, response, session, maKhachHang);
        }
    }

    private void xuLyDatHang(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, String maKhachHang) throws IOException {

        Integer thanhTien = (Integer) session.getAttribute("thanhTien");

        ArrayList<GioHang> listCart = layDanhSachDaChon(session);

        if (listCart.isEmpty()) {
            // Đơn đã được đặt xong ở request trước (bấm đúp / gửi lại form) hoặc phiên thanh toán đã hết.
            baoLoiGioHang(session, "Không có sản phẩm nào để đặt hàng. Nếu bạn vừa đặt hàng, hãy xem ở trang Đơn hàng.");
            response.sendRedirect(request.getContextPath() + "/giohangservlet");
            return;
        }

        int tongGoc = 0;

        for (GioHang item : listCart) {
            tongGoc += item.getGiaban() * item.getSoluong();
        }

        if (thanhTien == null) {
            thanhTien = tongGoc;
        }

        // Thành tiền lưu trong session có thể cũ -> không được vượt tổng gốc hoặc âm.
        if (thanhTien > tongGoc) {
            thanhTien = tongGoc;
        }
        if (thanhTien < 0) {
            thanhTien = 0;
        }

        // Mã sản phẩm của đơn lấy từ giỏ hàng ĐÃ CHỌN trên server, không tin input hidden từ trình duyệt.
        List<String> dsMaSP = new ArrayList<>();
        for (GioHang item : listCart) {
            if (!dsMaSP.contains(item.getMasanpham())) {
                dsMaSP.add(item.getMasanpham());
            }
        }

        for (String ma : dsMaSP) {
            if (!donHangDAO.kiemTraMaSanPham(ma)) {
                response.sendRedirect(request.getContextPath() + "/donhangservlet?error=masanpham");
                return;
            }
        }

        // Kiểm tra lại số lượng và tồn kho NGAY TRƯỚC KHI lưu đơn (session "listCart" có thể đã cũ).
        // Gộp theo mã sản phẩm: cùng 1 sản phẩm khác size nằm ở 2 dòng giỏ thì tổng số lượng mới phải <= tồn kho.
        java.util.Map<String, Integer> tongTheoSP = new java.util.LinkedHashMap<>();
        for (GioHang item : listCart) {
            if (item.getSoluong() < 1) {
                baoLoiGioHang(session, "Số lượng của sản phẩm " + item.getMasanpham() + " không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/giohangservlet");
                return;
            }
            tongTheoSP.merge(item.getMasanpham(), item.getSoluong(), Integer::sum);
        }
        for (java.util.Map.Entry<String, Integer> e : tongTheoSP.entrySet()) {
            if (e.getValue() > ghDAO.getTonKho(e.getKey())) {
                baoLoiGioHang(session, "Sản phẩm " + e.getKey()
                        + " không đủ số lượng trong kho, vui lòng kiểm tra lại giỏ hàng.");
                response.sendRedirect(request.getContextPath() + "/giohangservlet");
                return;
            }
        }

        String hoten = request.getParameter("hoten");
        String sodienthoai = request.getParameter("sodienthoai");
        String email = request.getParameter("email");
        String diachi = request.getParameter("diachi");
        String tinhthanh = request.getParameter("tinhthanh");
        String ghichu = request.getParameter("ghichu");

        DonHang dh = new DonHang();

        dh.setMakhachhang(maKhachHang);
        dh.setMasanpham(String.join(",", dsMaSP));
        dh.setHoten(hoten);
        dh.setSodienthoai(sodienthoai);
        dh.setEmail(email);
        dh.setDiachi(diachi);
        dh.setTinhthanh(tinhthanh);
        dh.setGhichu(ghichu);
        dh.setTongtien(thanhTien);

        // MỘT lần thanh toán = MỘT đơn hàng (madonhang). Các sản phẩm trong đơn là các dòng chi tiết
        // ở bảng trangthai (cùng madonhang) và khách chỉ thấy 1 mã đơn #ORD-<madonhang>.
        boolean save = donHangDAO.themDonHang(dh);

        if (!save) {
            session.setAttribute("error", "Đặt hàng thất bại, vui lòng thử lại. Giỏ hàng của bạn vẫn được giữ nguyên.");
            response.sendRedirect(request.getContextPath() + "/donhangservlet");
            return;
        }

        int tienGiam = tongGoc - thanhTien;

        int soDong = listCart.size();

        int giamMoiDong = 0;

        if (soDong > 0) {
            giamMoiDong = tienGiam / soDong;
        }

        // Các dòng KHÔNG đặt được (hết hàng đúng lúc có người khác mua) -> giữ nguyên trong giỏ.
        List<GioHang> dongKhongDatDuoc = new ArrayList<>();
        List<GioHang> dongDatDuoc = new ArrayList<>();
        int tongTienThucTe = 0;

        for (GioHang item : listCart) {

            int tongTienSP = item.getGiaban() * item.getSoluong();

            int tongSauGiam = tongTienSP - giamMoiDong;
            if (tongSauGiam < 0) {
                tongSauGiam = 0;
            }

            int giaSauGiam = tongSauGiam / item.getSoluong();

            // Trừ tồn kho TRƯỚC và kiểm tra kết quả (truTonKho chỉ trừ khi còn đủ hàng).
            if (!ghDAO.truTonKho(item.getMasanpham(), item.getSoluong())) {
                dongKhongDatDuoc.add(item);
                continue;
            }

            int maTrangThaiVuaTao = ghDAO.taoDonHang(item, dh.getMadonhang(), giaSauGiam);

            if (maTrangThaiVuaTao > 0) {
                dongDatDuoc.add(item);
                tongTienThucTe += tongSauGiam;
            } else {
                // Tạo dòng đơn lỗi -> hoàn lại kho vừa trừ để không mất hàng.
                ghDAO.congTonKho(item.getMasanpham(), item.getSoluong());
                dongKhongDatDuoc.add(item);
            }
        }

        if (dongDatDuoc.isEmpty()) {
            // Không dòng nào đặt được (VD vừa có người mua hết hàng): xóa đơn rỗng vừa tạo để không để lại
            // đơn "mồ côi" không có sản phẩm, báo lỗi, giỏ hàng còn nguyên.
            donHangDAO.xoaDonHang(dh.getMadonhang());
            baoLoiGioHang(session, "Đặt hàng thất bại do sản phẩm vừa hết hàng, vui lòng kiểm tra lại giỏ hàng.");
            response.sendRedirect(request.getContextPath() + "/giohangservlet");
            return;
        }

        // Lưu lại thông tin để lần thanh toán sau tự điền sẵn (khách vẫn sửa được nếu muốn)
        dkDAO.capNhatThongTinKhachHang(maKhachHang, hoten, sodienthoai, diachi);

        // Chỉ một phần sản phẩm đặt được -> sửa lại đơn cho khớp (danh sách sản phẩm + tổng tiền).
        if (!dongKhongDatDuoc.isEmpty()) {
            List<String> maDat = new ArrayList<>();
            for (GioHang item : dongDatDuoc) {
                if (!maDat.contains(item.getMasanpham())) {
                    maDat.add(item.getMasanpham());
                }
            }
            dh.setMasanpham(String.join(",", maDat));
            dh.setTongtien(tongTienThucTe);
            donHangDAO.capNhatThongTin(dh);
        }

        // Ghi mã giảm giá đã dùng vào đơn (hóa đơn hiển thị + hoàn lượt dùng mã khi đơn bị hủy).
        String maGiamGia = (String) session.getAttribute("maGiamGia");
        if (tienGiam > 0 && maGiamGia != null && !maGiamGia.trim().isEmpty()) {
            donHangDAO.luuThongTinGiamGia(dh.getMadonhang(), maGiamGia.trim(), tienGiam, dh.getTongtien());
        }

        // Chỉ xóa khỏi giỏ đúng những sản phẩm vừa đặt; sản phẩm chưa chọn / không đặt được vẫn còn nguyên.
        for (GioHang item : dongDatDuoc) {
            ghDAO.deleteSanPhamKhoiGio(item.getMagiohang());
        }

        // Số lượt còn lại của mã giảm giá đã được trừ ngay khi khách bấm "Áp dụng" ở giỏ hàng
        // (giohangservlet) nên ở bước này KHÔNG trừ lại.
        session.removeAttribute("thanhTien");
        session.removeAttribute("maGiamGia");
        session.removeAttribute("listCart");
        session.removeAttribute("selectedCartIds");
        session.removeAttribute("totalCartPrice");
        session.removeAttribute("tienGiam");

        // Chỉ 1 mã đơn duy nhất cho cả lần mua, bất kể có bao nhiêu sản phẩm.
        String thongBao = "Đặt hàng thành công! Mã đơn hàng của bạn là #ORD-" + dh.getMadonhang()
                + (dongDatDuoc.size() > 1 ? " (gồm " + dongDatDuoc.size() + " sản phẩm)" : "")
                + ". Cảm ơn bạn đã mua sắm tại LODI AUTHENTIC STORE.";

        if (!dongKhongDatDuoc.isEmpty()) {
            StringBuilder ds = new StringBuilder();
            for (GioHang item : dongKhongDatDuoc) {
                if (ds.length() > 0) {
                    ds.append(", ");
                }
                ds.append(item.getMasanpham());
            }
            thongBao += " Lưu ý: " + ds + " đã hết hàng nên chưa được đặt và vẫn còn trong giỏ.";
        }

        session.setAttribute("message", thongBao);

        response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
    }
}
