package servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.AdminGuard;
import model.LuongVanChuyen;
import modeldao.LuongVanChuyenDAO;

/**
 * Trang admin: Quản lý lương thưởng người vận chuyển theo tháng.
 *
 * Quy tắc lương: 5.000.000đ cố định / người / tháng + 10% hoa hồng trên giá
 * trị các đơn giao thành công trong tháng đó.
 */
@WebServlet(name = "LuongVanChuyenAdminServlet", urlPatterns = {"/luongvanchuyenadmin"})
public class LuongVanChuyenAdminServlet extends HttpServlet {

    private final LuongVanChuyenDAO dao = new LuongVanChuyenDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        int thang;
        int nam;

        try {
            thang = Integer.parseInt(request.getParameter("thang"));
        } catch (Exception e) {
            thang = cal.get(Calendar.MONTH) + 1;
        }

        try {
            nam = Integer.parseInt(request.getParameter("nam"));
        } catch (Exception e) {
            nam = cal.get(Calendar.YEAR);
        }

        String keyword = request.getParameter("keyword");

        List<LuongVanChuyen> dsLuong = dao.tinhLuongTatCaTheoThang(thang, nam, keyword);

        request.setAttribute("dsLuong", dsLuong);
        request.setAttribute("thang", thang);
        request.setAttribute("nam", nam);
        request.setAttribute("keyword", keyword);
        request.setAttribute("luongCoBan", LuongVanChuyenDAO.LUONG_CO_BAN);
        request.setAttribute("tiLeHoaHong", (int) Math.round(LuongVanChuyenDAO.TI_LE_HOA_HONG * 100));

        request.getRequestDispatcher("/view/luongvanchuyenadmin.jsp").forward(request, response);
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
        String thang = request.getParameter("thang");
        String nam = request.getParameter("nam");

        try {
            switch (action == null ? "" : action) {

                case "chotluong": {
                    String email = request.getParameter("email");
                    boolean ok = dao.chotLuong(email, Integer.parseInt(thang), Integer.parseInt(nam));
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã chốt lương tháng " + thang + "/" + nam + " cho " + email + "."
                               : "Chốt lương thất bại.");
                    break;
                }

                case "thanhtoan": {
                    int maluong = Integer.parseInt(request.getParameter("maluong"));
                    boolean ok = dao.danhDauDaThanhToan(maluong);
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã đánh dấu thanh toán thành công."
                               : "Không thể cập nhật (có thể đã thanh toán trước đó).");
                    break;
                }

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu.");
        }

        String keyword = request.getParameter("keyword");
        String redirect = request.getContextPath() + "/luongvanchuyenadmin";
        if (thang != null && nam != null) {
            redirect += "?thang=" + thang + "&nam=" + nam;
            if (keyword != null && !keyword.trim().isEmpty()) {
                redirect += "&keyword=" + java.net.URLEncoder.encode(keyword.trim(), "UTF-8");
            }
        }
        response.sendRedirect(redirect);
    }
}
