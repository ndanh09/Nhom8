package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DangXuatVanChuyenServlet", urlPatterns = {"/dangxuatvanchuyensvl"})
public class DangXuatVanChuyenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("vanchuyenEmail");
            session.removeAttribute("vanchuyen");
        }
        response.sendRedirect(request.getContextPath() + "/view/dangnhapvanchuyen.jsp");
    }
}
