package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.VanChuyen;
import modeldao.VanChuyenDAO;

@WebServlet(name = "DangNhapVanChuyenServlet", urlPatterns = {"/dangnhapvanchuyensvl"})
public class DangNhapVanChuyenServlet extends HttpServlet {

    private final VanChuyenDAO dao = new VanChuyenDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/dangnhapvanchuyen.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String matkhau = request.getParameter("matkhau");

        VanChuyen vc = dao.dangNhap(email, matkhau);

        if (vc != null) {
            HttpSession session = request.getSession();
            session.setAttribute("vanchuyenEmail", vc.getEmail());
            session.setAttribute("vanchuyen", vc);
            response.sendRedirect(request.getContextPath() + "/quanlyvanchuyenservlet");
            return;
        }

        request.setAttribute("error", "Sai email hoặc mật khẩu, tài khoản chưa được admin cấp, hoặc đang bị khóa.");
        request.getRequestDispatcher("/view/dangnhapvanchuyen.jsp").forward(request, response);
    }
}
