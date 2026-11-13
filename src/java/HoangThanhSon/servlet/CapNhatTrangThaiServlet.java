package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.TrangThai;
import modeldao.CapNhatTrangThaiDAO;
import util.AdminGuard;

@WebServlet("/capnhattrangthaiservlet")
public class CapNhatTrangThaiServlet extends HttpServlet {

    private CapNhatTrangThaiDAO trangThaiDAO = new CapNhatTrangThaiDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        List<TrangThai> listOrder = trangThaiDAO.getAllDanhSachDonHang();

        // FIX: ô "Tìm theo mã đơn, khách hàng, mã SP..." trước đây gửi tham số
        // keyword nhưng servlet bỏ qua nên tìm kiếm không có tác dụng.
        String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            String kwMa = kw.startsWith("#") ? kw.substring(1) : kw;
            if (kwMa.startsWith("ord-")) {
                kwMa = kwMa.substring(4);
            }
            List<TrangThai> locDs = new java.util.ArrayList<>();
            for (TrangThai tt : listOrder) {
                String maDon = String.valueOf(tt.getMadonhang() > 0 ? tt.getMadonhang() : tt.getMatrangthai());
                String kh = tt.getMakhachhang() == null ? "" : tt.getMakhachhang().toLowerCase();
                String sp = tt.getMasanpham() == null ? "" : tt.getMasanpham().toLowerCase();
                if (maDon.equals(kwMa) || kh.contains(kw) || sp.contains(kw)) {
                    locDs.add(tt);
                }
            }
            listOrder = locDs;
            request.setAttribute("keyword", keyword.trim());
        }

        request.setAttribute("listOrder", listOrder);

        request.getRequestDispatcher("/view/capnhatTrangthai.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("capnhatTrangthai".equals(action)) {

            try {

                int matrangthai = Integer.parseInt(
                        request.getParameter("matrangthai")
                );

                int tinhtrangMoi = Integer.parseInt(
                        request.getParameter("tinhtrangMoi")
                );

                // Đơn đang bị khóa (Quản lý đơn hàng) -> không được thao tác gì cho tới khi mở khóa.
                if (modeldao.TrangThaiDAO.donHangBiKhoa(matrangthai)) {
                    request.getSession().setAttribute("error", "Đơn hàng này đang bị khóa nên không thể thao tác. Vui lòng mở khóa ở trang Quản lý đơn hàng trước.");
                    response.sendRedirect(request.getContextPath() + "/capnhattrangthaiservlet");
                    return;
                }

                boolean update = trangThaiDAO.capNhatTrangThai(
                        matrangthai,
                        tinhtrangMoi
                );

                if (update) {

                    request.getSession().setAttribute(
                            "message",
                            "Cập nhật trạng thái đơn hàng thành công!"
                    );

                } else {

                    request.getSession().setAttribute(
                            "error",
                            "Không thể cập nhật: trạng thái phải đi theo đúng thứ tự Chờ xác nhận → Đang giao → Đã nhận (hoặc Hủy đơn ở 2 bước đầu)."
                    );

                }

            } catch (Exception e) {

                e.printStackTrace();

                request.getSession().setAttribute(
                        "error",
                        "Có lỗi xảy ra khi cập nhật."
                );

            }

        }
        response.sendRedirect(request.getContextPath() + "/capnhattrangthaiservlet");

    }

}
