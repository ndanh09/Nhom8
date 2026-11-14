package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import model.TaiKhoanYeuThich;
import modeldao.YeuThichDAO;
import util.AdminGuard;

/**
 * LUỒNG QUẢN TRỊ VIÊN (vai trò ADMIN, cùng nhóm với trang Quản trị viên / tài khoản):
 * xem và dọn danh sách yêu thích THEO TỪNG TÀI KHOẢN khách hàng.
 *
 *  GET  /quanlyyeuthichtaikhoan              : danh sách tài khoản + số sản phẩm yêu thích
 *  GET  /quanlyyeuthichtaikhoan?email=...    : danh sách yêu thích của 1 tài khoản
 *  POST action=xoamuc | xoatatca             : gỡ 1 sản phẩm / xóa hết yêu thích của tài khoản
 * Phần xếp hạng Top sản phẩm nằm ở luồng sản phẩm (QuanLyTopYeuThichServlet).
 */
@WebServlet(name = "QuanLyYeuThichTaiKhoanServlet", urlPatterns = {"/quanlyyeuthichtaikhoan"})
public class QuanLyYeuThichTaiKhoanServlet extends HttpServlet {

    private final YeuThichDAO dao = new YeuThichDAO();

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }
        String keyword = request.getParameter("keyword");
        boolean chiCo = "1".equals(request.getParameter("chico"));
        String email = request.getParameter("email");

        request.setAttribute("dsTaiKhoan", dao.layTaiKhoan(keyword, chiCo));
        request.setAttribute("tongLuot", dao.tongLuot());
        request.setAttribute("soTaiKhoan", dao.soTaiKhoanCoYeuThich());
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("chiCo", chiCo);

        if (email != null && !email.trim().isEmpty()) {
            TaiKhoanYeuThich tk = dao.layMotTaiKhoan(email);
            if (tk == null) {
                request.getSession().setAttribute("error", "Không tìm thấy tài khoản khách hàng này!");
            } else {
                request.setAttribute("tkChon", tk);
                request.setAttribute("dsCuaTk", dao.layCuaKhach(tk.getEmail()));
            }
        }
        request.getRequestDispatcher("/view/quanlyyeuthichtaikhoan.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String email = request.getParameter("email");

        try {
            if (action == null || email == null || email.trim().isEmpty() || dao.layMotTaiKhoan(email) == null) {
                session.setAttribute("error", "Tài khoản không tồn tại hoặc yêu cầu không hợp lệ!");
            } else if ("xoamuc".equals(action)) {
                if (dao.xoaMotMuc(email.trim(), request.getParameter("magoc"))) {
                    session.setAttribute("success", "Đã gỡ sản phẩm khỏi danh sách yêu thích của tài khoản.");
                } else {
                    session.setAttribute("error", "Không gỡ được sản phẩm này (có thể đã được gỡ trước đó)!");
                }
            } else if ("xoatatca".equals(action)) {
                int n = dao.xoaTatCa(email.trim());
                if (n < 0) {
                    session.setAttribute("error", "Không xóa được danh sách yêu thích của tài khoản này!");
                } else {
                    session.setAttribute("success", "Đã xóa " + n + " sản phẩm khỏi danh sách yêu thích của tài khoản.");
                }
            } else {
                session.setAttribute("error", "Thao tác không hợp lệ!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại.");
        }

        String keyword = request.getParameter("keyword");
        String chico = request.getParameter("chico");
        response.sendRedirect(request.getContextPath() + "/quanlyyeuthichtaikhoan?email=" + enc(email)
                + "&keyword=" + enc(keyword) + ("1".equals(chico) ? "&chico=1" : ""));
    }

    @Override
    public String getServletInfo() {
        return "Yêu thích theo tài khoản - luồng quản trị viên (ADMIN)";
    }
}
