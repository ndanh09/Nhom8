package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/dangxuatservlet")
public class DangXuatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute("user");
            session.removeAttribute("email");
            // FIX: trước đây KHÔNG xóa isAdmin nên "đăng xuất" xong vẫn còn quyền admin.
            session.removeAttribute("isAdmin");
            session.removeAttribute("vaiTro");
            session.removeAttribute("maGiamGia");
            session.removeAttribute("listCart");
            session.removeAttribute("thanhTien");
            session.removeAttribute("selectedCartIds");
            session.removeAttribute("totalCartPrice");
            session.removeAttribute("tienGiam");
        }

        // Các trang admin gọi /dangxuatservlet?admin=1 -> về thẳng trang đăng nhập.
        if (request.getParameter("admin") != null) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
    }
}
