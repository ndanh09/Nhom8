package servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DonHangGanNhanVien;
import model.LuongNhanVienBanHang;
import model.NhanVienBanHang;
import modeldao.LuongNhanVienBanHangDAO;
import modeldao.NhanVienBanHangDAO;
import util.AdminGuard;

/**
 * Trang admin: Quản lý lương nhân viên bán hàng theo tháng.
 *
 * Quy tắc: 7.000.000đ cố định / người / tháng + 10% giá trị mỗi đơn được gán cho nhân viên
 * và khách đã nhận hàng. Admin gán đơn cho nhân viên ngay trên trang này, rồi "Chốt lương"
 * và "Đã thanh toán" (giống trang lương vận chuyển).
 */
@WebServlet(name = "LuongNhanVienBanHangAdminServlet", urlPatterns = {"/luongnhanvienbanhang"})
public class LuongNhanVienBanHangAdminServlet extends HttpServlet {

    private final LuongNhanVienBanHangDAO dao = new LuongNhanVienBanHangDAO();
    private final NhanVienBanHangDAO nvDao = new NhanVienBanHangDAO();

    private static int docSo(String s, int macDinh, int min, int max) {
        try {
            int v = Integer.parseInt(s == null ? "" : s.trim());
            return (v >= min && v <= max) ? v : macDinh;
        } catch (NumberFormatException e) {
            return macDinh;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        Calendar cal = Calendar.getInstance();
        int thang = docSo(request.getParameter("thang"), cal.get(Calendar.MONTH) + 1, 1, 12);
        int nam = docSo(request.getParameter("nam"), cal.get(Calendar.YEAR), 2000, 2100);
        String keyword = request.getParameter("keyword");

        boolean daMigration = dao.daChayMigration();
        request.setAttribute("thieuMigration", !daMigration);

        List<LuongNhanVienBanHang> dsLuong = new java.util.ArrayList<>();
        List<DonHangGanNhanVien> dsDon = new java.util.ArrayList<>();
        if (daMigration) {
            dsLuong = dao.tinhLuongTatCaTheoThang(thang, nam, keyword);
            dsDon = dao.danhSachDonTrongThang(thang, nam);
        }

        // Danh sách nhân viên cho ô chọn "phụ trách" (tài khoản khóa vẫn hiện để đơn cũ còn hiển thị đúng tên).
        List<NhanVienBanHang> dsNhanVien = nvDao.danhSach(null);
        dsNhanVien.sort(Comparator.comparing(NhanVienBanHang::getHoten, String.CASE_INSENSITIVE_ORDER));

        request.setAttribute("dsLuong", dsLuong);
        request.setAttribute("dsDon", dsDon);
        request.setAttribute("dsNhanVien", dsNhanVien);
        request.setAttribute("thang", thang);
        request.setAttribute("nam", nam);
        request.setAttribute("keyword", keyword);
        request.setAttribute("luongCoBan", LuongNhanVienBanHangDAO.LUONG_CO_BAN);
        request.setAttribute("tiLeHoaHong", LuongNhanVienBanHangDAO.TI_LE_HOA_HONG_PHAN_TRAM);

        request.getRequestDispatcher("/view/luongnhanvienbanhang.jsp").forward(request, response);
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

        Calendar cal = Calendar.getInstance();
        int thang = docSo(request.getParameter("thang"), cal.get(Calendar.MONTH) + 1, 1, 12);
        int nam = docSo(request.getParameter("nam"), cal.get(Calendar.YEAR), 2000, 2100);

        try {
            switch (action == null ? "" : action) {

                case "chotluong": {
                    String email = request.getParameter("email");
                    boolean ok = dao.chotLuong(email, thang, nam);
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã chốt lương tháng " + thang + "/" + nam + " cho " + email + "."
                               : "Chốt lương thất bại. Kiểm tra đã chạy migration_luong_nhanvienbanhang.sql chưa.");
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

                case "gandon": {
                    int madonhang = Integer.parseInt(request.getParameter("madonhang"));
                    String email = request.getParameter("email");
                    String loi = dao.ganNhanVien(madonhang, email);
                    if (loi == null) {
                        boolean bo = email == null || email.trim().isEmpty();
                        session.setAttribute("success", bo
                                ? "Đã bỏ gán nhân viên khỏi đơn #ORD-" + madonhang + "."
                                : "Đã gán đơn #ORD-" + madonhang + " cho " + email.trim() + ".");
                    } else {
                        session.setAttribute("error", loi);
                    }
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
        String redirect = request.getContextPath() + "/luongnhanvienbanhang?thang=" + thang + "&nam=" + nam;
        if (keyword != null && !keyword.trim().isEmpty()) {
            redirect += "&keyword=" + java.net.URLEncoder.encode(keyword.trim(), "UTF-8");
        }
        response.sendRedirect(redirect);
    }
}
