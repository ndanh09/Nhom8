package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.DonHang;
import model.TrangThai;
import modeldao.DonHangDAO;
import modeldao.TrangThaiDAO;
import util.AdminGuard;

/**
 * Servlet hiển thị hóa đơn của một đơn hàng (theo madonhang).
 * - Khách hàng: chỉ xem được hóa đơn của chính đơn hàng mình đã đặt.
 * - Quản trị viên: xem được hóa đơn của bất kỳ đơn hàng nào.
 */
@WebServlet("/hoadonservlet")
public class HoaDonServlet extends HttpServlet {

    private final DonHangDAO donHangDAO = new DonHangDAO();
    private final TrangThaiDAO trangThaiDAO = new TrangThaiDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");

        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        int maDonHang;

        try {
            maDonHang = Integer.parseInt(request.getParameter("madonhang"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
            return;
        }

        DonHang donHang = donHangDAO.getDonHangTheoMa(maDonHang);

        if (donHang == null) {
            session.setAttribute("error", "Không tìm thấy đơn hàng #ORD-" + maDonHang);
            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
            return;
        }

        // Nhân viên xem được mọi hóa đơn: vai trò QUANLY (trang cập nhật trạng thái đơn hàng).
        boolean isAdmin = AdminGuard.laQuanLy(request);
        boolean isChuDonHang = email.equalsIgnoreCase(donHang.getMakhachhang());

        if (!isAdmin && !isChuDonHang) {
            response.sendRedirect(request.getContextPath() + "/trangthaiservlet?error=khongcoquyen");
            return;
        }

        List<TrangThai> chiTiet = trangThaiDAO.getChiTietHoaDon(maDonHang);

        int tongTienHang = 0;

        for (TrangThai ct : chiTiet) {
            tongTienHang += ct.getGiaban() * ct.getSoluong();
        }

        // Giá từng dòng trong trangthai đã được trừ phần giảm giá, nên khi đơn có áp
        // mã giảm giá thì "Tổng tiền hàng" phải là tổng TRƯỚC giảm = tổng thanh toán + tiền giảm.
        if (donHang.getSotiengiam() > 0) {
            tongTienHang = donHang.getTongtien() + donHang.getSotiengiam();
        }

        request.setAttribute("donHang", donHang);
        request.setAttribute("chiTiet", chiTiet);
        request.setAttribute("tongTienHang", tongTienHang);
        request.setAttribute("isAdmin", isAdmin);

        request.getRequestDispatcher("/view/hoadon.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
