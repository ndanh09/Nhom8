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
import model.LuongVanChuyen;
import model.TrangThai;
import model.VanChuyen;
import modeldao.LuongVanChuyenDAO;
import modeldao.VanChuyenDAO;

@WebServlet(name = "QuanLyVanChuyenServlet", urlPatterns = {"/quanlyvanchuyenservlet"})
public class QuanLyVanChuyenServlet extends HttpServlet {

    private final VanChuyenDAO dao = new VanChuyenDAO();
    private final LuongVanChuyenDAO luongDAO = new LuongVanChuyenDAO();

    private String layEmailDangNhap(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute("vanchuyenEmail");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = layEmailDangNhap(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhapvanchuyen.jsp");
            return;
        }

        // Chỉ hiển thị "Đơn cần giao" đúng tỉnh/thành (khuvuc) mà tài khoản
        // này đang phụ trách. Lấy lại thông tin tài khoản mới nhất từ DB
        // (thay vì chỉ dùng object cũ trong session) để luôn đúng khuvuc
        // hiện tại kể cả khi admin vừa đổi khu vực của tài khoản này.
        HttpSession session = request.getSession();
        VanChuyen vc = dao.timTheoEmail(email);
        if (vc == null || "Khóa".equals(vc.getTrangthai())) {
            // Tài khoản không còn tồn tại hoặc vừa bị khóa -> buộc đăng xuất
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/view/dangnhapvanchuyen.jsp");
            return;
        }
        session.setAttribute("vanchuyen", vc);

        List<TrangThai> donCanGiao = dao.danhSachDonCanGiao(vc.getKhuvuc());
        List<TrangThai> donCuaToi = dao.donDangGiaoCuaToi(email);
        List<TrangThai> lichSu = dao.lichSuGiaoHang(email);

        request.setAttribute("donCanGiao", donCanGiao);
        request.setAttribute("donCuaToi", donCuaToi);
        request.setAttribute("lichSu", lichSu);

        // ==== Lương thưởng của tôi (lương cố định + hoa hồng 10%/đơn) ====
        Calendar cal = Calendar.getInstance();
        int thangHienTai = cal.get(Calendar.MONTH) + 1;
        int namHienTai = cal.get(Calendar.YEAR);

        LuongVanChuyen luongThangNay = luongDAO.tinhLuongTheoThang(email, thangHienTai, namHienTai);
        List<LuongVanChuyen> lichSuLuong = luongDAO.lichSuLuongCuaToi(email);

        request.setAttribute("luongThangNay", luongThangNay);
        request.setAttribute("lichSuLuong", lichSuLuong);
        request.setAttribute("thangHienTai", thangHienTai);
        request.setAttribute("namHienTai", namHienTai);
        request.setAttribute("luongCoBan", LuongVanChuyenDAO.LUONG_CO_BAN);
        request.setAttribute("tiLeHoaHong", (int) Math.round(LuongVanChuyenDAO.TI_LE_HOA_HONG * 100));

        request.getRequestDispatcher("/view/quanlyvanchuyen.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = layEmailDangNhap(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhapvanchuyen.jsp");
            return;
        }

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        try {
            int matrangthai = Integer.parseInt(request.getParameter("matrangthai"));
            boolean ok;

            // Đơn đang bị khóa -> vận chuyển cũng không được nhận/trả/giao/báo thất bại.
            if (modeldao.TrangThaiDAO.donHangBiKhoa(matrangthai)) {
                session.setAttribute("error", "Đơn hàng này đang bị khóa nên không thể thao tác. Vui lòng liên hệ quản lý.");
                response.sendRedirect(request.getContextPath() + "/quanlyvanchuyenservlet");
                return;
            }

            switch (action == null ? "" : action) {
                case "nhandon":
                    ok = dao.nhanDon(matrangthai, email);
                    session.setAttribute(ok ? "message" : "error",
                            ok ? "Đã nhận đơn, vui lòng tiến hành giao hàng."
                               : "Đơn này vừa được người khác nhận trước, vui lòng chọn đơn khác.");
                    break;
                case "tralaidon":
                    ok = dao.traLaiDon(matrangthai, email);
                    session.setAttribute(ok ? "message" : "error",
                            ok ? "Đã trả lại đơn về danh sách chung."
                               : "Không thể trả lại đơn này.");
                    break;
                case "danggiao":
                    ok = dao.capNhatDaGiao(matrangthai, email);
                    session.setAttribute(ok ? "message" : "error",
                            ok ? "Đã cập nhật đơn sang trạng thái Đã giao, chờ khách hàng xác nhận."
                               : "Không thể cập nhật, đơn này có thể đã được xử lý.");
                    break;
                case "giaothatbai":
                    ok = dao.baoGiaoThatBai(matrangthai, email);
                    session.setAttribute(ok ? "message" : "error",
                            ok ? "Đã báo giao thất bại/khách từ chối nhận cho đơn."
                               : "Không thể cập nhật, đơn này có thể đã được xử lý.");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu.");
        }

        response.sendRedirect(request.getContextPath() + "/quanlyvanchuyenservlet");
    }
}
