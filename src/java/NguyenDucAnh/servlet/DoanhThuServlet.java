package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.DoanhThu;
import modeldao.DoanhThuDAO;
import util.AdminGuard;

@WebServlet("/doanhthuservlet")
public class DoanhThuServlet extends HttpServlet {

    private DoanhThuDAO doanhThuDAO = new DoanhThuDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraDoanhThu(request, response)) {
            return;
        }

        String tuNgay = request.getParameter("tuNgay");
        String denNgay = request.getParameter("denNgay");

        List<DoanhThu> listDoanhThu;

        if (tuNgay != null && !tuNgay.isEmpty()
                && denNgay != null && !denNgay.isEmpty()) {

            listDoanhThu = doanhThuDAO.getDoanhThuTheoNgay(tuNgay, denNgay);

        } else {

            listDoanhThu = doanhThuDAO.getDoanhThuTheoNgay();

        }

        request.setAttribute("listDoanhThu", listDoanhThu);

        request.getRequestDispatcher("/view/doanhthu.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
}