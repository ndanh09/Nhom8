package NguyenXuanLoc.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import model.PhieuGiamGia;
import modeldao.PhieuGiamGiaDAO;
import util.AdminGuard;

@WebServlet(name = "phieugiamgiaservlet", urlPatterns = {"/phieugiamgiaservlet"})
public class phieugiamgiaservlet extends HttpServlet {

    private PhieuGiamGiaDAO pggDAO = new PhieuGiamGiaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        try {

            switch (action) {

                case "edit":

                    String id = request.getParameter("id");

                    PhieuGiamGia pgg = pggDAO.getPhieuGiamGia(id);

                    request.setAttribute("pgg", pgg);

                    break;

                case "khoa":

                    id = request.getParameter("id");

                    pggDAO.capNhatTrangThai(id, false);

                    response.sendRedirect(request.getContextPath() + "/phieugiamgiaservlet?keyword="
                            + java.net.URLEncoder.encode(request.getParameter("keyword") == null ? "" : request.getParameter("keyword"), "UTF-8"));
                    return;

                case "mokhoa":

                    id = request.getParameter("id");

                    pggDAO.capNhatTrangThai(id, true);

                    response.sendRedirect(request.getContextPath() + "/phieugiamgiaservlet?keyword="
                            + java.net.URLEncoder.encode(request.getParameter("keyword") == null ? "" : request.getParameter("keyword"), "UTF-8"));
                    return;

                default:
                    break;
            }

            String keyword = request.getParameter("keyword");
            ArrayList<PhieuGiamGia> list = pggDAO.getDanhSachPhieuGiamGia(keyword);

            request.setAttribute("listPhieu", list);
            request.setAttribute("keyword", keyword);

            request.getRequestDispatcher("view/phieugiamgia.jsp").forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(request.getContextPath() + "/phieugiamgiaservlet");
        }

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

        try {

            PhieuGiamGia pgg = new PhieuGiamGia();

            pgg.setMagiamgia(request.getParameter("magiamgia"));
            pgg.setTenphieu(request.getParameter("tenphieu"));

            pgg.setLoaigiam(Integer.parseInt(request.getParameter("loaigiam")));

            pgg.setGiatrigiam(Integer.parseInt(request.getParameter("giatrigiam")));

            String giam = request.getParameter("giamtoida");

            if (giam == null || giam.trim().isEmpty()) {
                pgg.setGiamtoida(0);
            } else {
                pgg.setGiamtoida(Integer.parseInt(giam));
            }

            pgg.setDieukien(Integer.parseInt(request.getParameter("dieukien")));

            pgg.setSoluong(Integer.parseInt(request.getParameter("soluong")));

            pgg.setNgaybatdau(Date.valueOf(request.getParameter("ngaybatdau")));

            pgg.setNgayketthuc(Date.valueOf(request.getParameter("ngayketthuc")));

            pgg.setTrangthai(request.getParameter("trangthai") != null);

            if ("add".equals(action)) {

                pggDAO.themPhieuGiamGia(pgg);

            } else if ("update".equals(action)) {

                pggDAO.suaPhieuGiamGia(pgg);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        response.sendRedirect(request.getContextPath() + "/phieugiamgiaservlet");
    }
}