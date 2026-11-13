package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.DonHang;
import model.TrangThai;
import model.VanChuyen;
import modeldao.DonHangDAO;
import modeldao.TrangThaiDAO;
import modeldao.VanChuyenDAO;
import util.AdminGuard;

/**
 * Trang CHI TIẾT TỪNG SẢN PHẨM của một hóa đơn (theo matrangthai = 1 dòng sản phẩm).
 * Đi từ trang Hóa đơn (/hoadonservlet) bấm "Xem chi tiết" ở dòng sản phẩm.
 * - Khách hàng: chỉ xem được sản phẩm thuộc đơn của chính mình.
 * - QUANLY: xem được tất cả. Chỉ xem, không sửa dữ liệu (đơn bị khóa vẫn xem được).
 */
@WebServlet("/chitiethoadonservlet")
public class ChiTietHoaDonServlet extends HttpServlet {

    private final DonHangDAO donHangDAO = new DonHangDAO();
    private final TrangThaiDAO trangThaiDAO = new TrangThaiDAO();
    private final VanChuyenDAO vanChuyenDAO = new VanChuyenDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        int maTrangThai;
        try {
            maTrangThai = Integer.parseInt(request.getParameter("matrangthai"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
            return;
        }

        TrangThai sp = trangThaiDAO.getChiTietSanPham(maTrangThai);
        if (sp == null) {
            session.setAttribute("error", "Không tìm thấy sản phẩm trong hóa đơn.");
            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
            return;
        }

        DonHang donHang = donHangDAO.getDonHangTheoMa(sp.getMadonhang());
        if (donHang == null) {
            session.setAttribute("error", "Không tìm thấy đơn hàng #ORD-" + sp.getMadonhang());
            response.sendRedirect(request.getContextPath() + "/trangthaiservlet");
            return;
        }

        boolean isAdmin = AdminGuard.laQuanLy(request);
        boolean isChuDonHang = email.equalsIgnoreCase(donHang.getMakhachhang());
        if (!isAdmin && !isChuDonHang) {
            response.sendRedirect(request.getContextPath() + "/trangthaiservlet?error=khongcoquyen");
            return;
        }

        VanChuyen vanChuyen = null;
        if (sp.getEmailVanChuyen() != null && !sp.getEmailVanChuyen().trim().isEmpty()) {
            vanChuyen = vanChuyenDAO.timTheoEmail(sp.getEmailVanChuyen());
        }

        request.setAttribute("sp", sp);
        request.setAttribute("donHang", donHang);
        request.setAttribute("vanChuyen", vanChuyen);
        request.setAttribute("isAdmin", isAdmin);

        request.getRequestDispatcher("/view/chitiethoadon.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
