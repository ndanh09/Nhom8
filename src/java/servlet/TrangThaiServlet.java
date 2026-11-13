package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import model.NhomDonHang;
import model.TrangThai;
import model.nguoidung;
import modeldao.GioHangDAO;
import modeldao.TrangThaiDAO;
import modeldao.nguoidungdao;

@WebServlet("/trangthaiservlet")
public class TrangThaiServlet extends HttpServlet {

    private TrangThaiDAO trangThaiDAO = new TrangThaiDAO();
    private GioHangDAO gioHangDAO = new GioHangDAO();
    private nguoidungdao spDAO = new nguoidungdao();

    // Gom danh sách sản phẩm về 1 dòng đại diện / 1 tên sản phẩm,
    // dùng cho ô tìm kiếm gợi ý sang trang Sản phẩm (giống nguoidungservlet).
    private ArrayList<nguoidung> gomTheoTenSanPham(ArrayList<nguoidung> dsGoc) {
        LinkedHashMap<String, nguoidung> unique = new LinkedHashMap<>();
        for (nguoidung sp : dsGoc) {
            if (!unique.containsKey(sp.getTensanpham())) {
                unique.put(sp.getTensanpham(), sp);
            }
        }
        return new ArrayList<>(unique.values());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String maKH = (String) session.getAttribute("email");

        if (maKH == null || maKH.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        List<TrangThai> listOrder = trangThaiDAO.getDanhSachDonHang(maKH);

        request.setAttribute("listOrder", listOrder);

        // Gom các dòng sản phẩm cùng một lần thanh toán (cùng madonhang) thành
        // MỘT đơn hàng để khách chỉ thấy 1 mã đơn thay vì mỗi sản phẩm 1 mã.
        LinkedHashMap<Integer, NhomDonHang> nhomDon = new LinkedHashMap<>();
        for (TrangThai tt : listOrder) {
            int khoa = tt.getMadonhang() > 0 ? tt.getMadonhang() : -tt.getMatrangthai();
            NhomDonHang nhom = nhomDon.get(khoa);
            if (nhom == null) {
                nhom = new NhomDonHang(tt.getMadonhang());
                nhomDon.put(khoa, nhom);
            }
            nhom.them(tt);
        }
        request.setAttribute("listNhomDon", new ArrayList<>(nhomDon.values()));

        // Số lượng sản phẩm trong giỏ hàng, để badge giỏ hàng ở header luôn
        // hiển thị đúng số thật, đồng bộ với các trang khác.
        request.setAttribute("cartCount", gioHangDAO.countCart(maKH));

        // Toàn bộ danh sách sản phẩm (bên trang nguoidung) để ô tìm kiếm
        // gợi ý theo sản phẩm toàn hệ thống, không chỉ sản phẩm trong đơn hàng.
        ArrayList<nguoidung> listsp = gomTheoTenSanPham(spDAO.sp());

        request.setAttribute("listsp", listsp);

        request.getRequestDispatcher("/view/donhang.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        // Đã bỏ action "updateStatus": nó gọi thẳng capNhatTinhTrang() mà
        // không kiểm tra đơn có phải của khách đang đăng nhập không, và
        // không kiểm tra thứ tự chuyển trạng thái hợp lệ (Chờ xác nhận ->
        // Đang giao -> Đã nhận -> Hủy). Vì vậy trước đây chỉ cần gọi action
        // này là đơn có thể nhảy thẳng từ "Chờ xác nhận" sang "Hủy đơn",
        // bỏ qua bước "Đang giao". Các action bên dưới (cancel,
        // confirmReceived, hoantra) đã kiểm tra đúng chủ đơn + đúng trạng
        // thái hiện tại nên vẫn giữ nguyên.

        if ("confirmReceived".equals(action)) {

            try {

                int matrangthai = Integer.parseInt(request.getParameter("matrangthai"));
                String maKH = (String) session.getAttribute("email");

                if (modeldao.TrangThaiDAO.donHangBiKhoa(matrangthai)) {
                    session.setAttribute("error", "Đơn hàng này đang bị khóa nên tạm thời không thể thao tác. Vui lòng liên hệ cửa hàng.");
                    response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
                    return;
                }

                boolean ok = trangThaiDAO.khachXacNhanDaNhan(matrangthai, maKH);

                session.setAttribute(ok ? "message" : "error",
                        ok ? "Đã xác nhận nhận hàng thành công!" : "Không thể xác nhận, vui lòng thử lại.");

            } catch (Exception e) {

                e.printStackTrace();

            }

            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");

            return;

        }

        if ("hoantra".equals(action)) {

            try {

                int matrangthai = Integer.parseInt(request.getParameter("matrangthai"));
                String maKH = (String) session.getAttribute("email");

                if (modeldao.TrangThaiDAO.donHangBiKhoa(matrangthai)) {
                    session.setAttribute("error", "Đơn hàng này đang bị khóa nên tạm thời không thể thao tác. Vui lòng liên hệ cửa hàng.");
                    response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
                    return;
                }

                boolean ok = trangThaiDAO.khachHoanTraHang(matrangthai, maKH);

                session.setAttribute(ok ? "message" : "error",
                        ok ? "Hoàn trả hàng thành công, số lượng sản phẩm đã được cập nhật lại!" : "Không thể hoàn trả, vui lòng thử lại.");

            } catch (Exception e) {

                e.printStackTrace();

            }

            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");

            return;

        }

        if ("cancel".equals(action)) {

            try {

                int matrangthai = Integer.parseInt(request.getParameter("matrangthai"));

                if (modeldao.TrangThaiDAO.donHangBiKhoa(matrangthai)) {
                    session.setAttribute("error", "Đơn hàng này đang bị khóa nên tạm thời không thể thao tác. Vui lòng liên hệ cửa hàng.");
                    response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
                    return;
                }

                TrangThai tt = trangThaiDAO.getDonHang(matrangthai);

                String maKHDangNhap = (String) session.getAttribute("email");

                // FIX 1: chỉ chủ đơn mới được hủy (trước đây chỉ cần biết matrangthai là hủy được đơn người khác).
                if (tt != null && maKHDangNhap != null && maKHDangNhap.equals(tt.getMakhachhang())) {

                    if (tt.getTinhtrang() == 0 || tt.getTinhtrang() == 1) {

                        // FIX 2: đổi trạng thái TRƯỚC (câu UPDATE có điều kiện tinhtrang IN (0,1) nên chỉ
                        // 1 request thắng), chỉ hoàn kho khi hủy thành công. Trước đây hoàn kho trước và
                        // bỏ qua kết quả -> bấm đúp/gửi lặp là kho bị cộng nhiều lần.
                        if (trangThaiDAO.huyDonHang(matrangthai)) {

                            gioHangDAO.congTonKho(
                                    tt.getMasanpham(),
                                    tt.getSoluong()
                            );

                        }

                    }

                }

            } catch (Exception e) {

                e.printStackTrace();

            }

            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");

            return;

        }

        response.sendRedirect(request.getContextPath() + "/trangthaiservlet");

    }

}