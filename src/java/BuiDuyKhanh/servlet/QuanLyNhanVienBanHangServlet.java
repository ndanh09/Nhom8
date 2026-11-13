package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.NhanVienBanHang;
import modeldao.NhanVienBanHangDAO;
import util.AdminGuard;

/**
 * Trang admin: quản lý nhân viên bán hàng. Admin tạo tài khoản (kèm ảnh CCCD),
 * sửa thông tin / đổi ảnh CCCD, khóa / mở khóa tài khoản.
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
@WebServlet(name = "QuanLyNhanVienBanHangServlet", urlPatterns = {"/quanlynhanvienbanhang"})
public class QuanLyNhanVienBanHangServlet extends HttpServlet {

    private final NhanVienBanHangDAO dao = new NhanVienBanHangDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String keyword = request.getParameter("keyword");
        request.setAttribute("dsNhanVien", dao.danhSach(keyword));
        request.setAttribute("keyword", keyword);

        // Bấm "Sửa" -> ?sua=email : hiện form sửa với dữ liệu có sẵn
        String sua = request.getParameter("sua");
        if (sua != null && !sua.trim().isEmpty()) {
            request.setAttribute("nvSua", dao.timTheoEmail(sua.trim()));
        }

        request.getRequestDispatcher("/view/quanlynhanvienbanhang.jsp").forward(request, response);
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
        String redirect = request.getContextPath() + "/quanlynhanvienbanhang";

        try {
            switch (action == null ? "" : action) {

                case "them": {
                    String email = request.getParameter("email");
                    String hoten = request.getParameter("hoten");
                    String matkhau = request.getParameter("matkhau");
                    String sdt = request.getParameter("sodienthoai");
                    Part partAnh = request.getPart("anhcccd");

                    if (email == null || email.trim().isEmpty()
                            || hoten == null || hoten.trim().isEmpty()
                            || matkhau == null || matkhau.trim().isEmpty()
                            || sdt == null || sdt.trim().isEmpty()) {
                        session.setAttribute("error", "Vui lòng nhập đầy đủ thông tin bắt buộc.");
                    } else if (partAnh == null || partAnh.getSize() == 0) {
                        session.setAttribute("error", "Vui lòng tải lên ảnh CCCD.");
                    } else if (dao.checkEmailTonTai(email.trim())) {
                        session.setAttribute("error", "Email này đã có tài khoản nhân viên bán hàng.");
                    } else {
                        String tenAnh = util.ImageStorage.luuAnh(partAnh, "nhanvienbanhang_cccd_");
                        NhanVienBanHang nv = new NhanVienBanHang();
                        nv.setEmail(email.trim());
                        nv.setHoten(hoten.trim());
                        nv.setMatkhau(matkhau);
                        nv.setSodienthoai(sdt.trim());
                        nv.setAnhcccd(tenAnh);
                        boolean ok = dao.them(nv);
                        session.setAttribute(ok ? "success" : "error",
                                ok ? "Đã tạo tài khoản nhân viên bán hàng " + email.trim() + "."
                                   : "Tạo tài khoản thất bại. Kiểm tra đã chạy migration_nhanvienbanhang.sql chưa.");
                    }
                    break;
                }

                case "sua": {
                    String email = request.getParameter("email");
                    String hoten = request.getParameter("hoten");
                    String sdt = request.getParameter("sodienthoai");
                    String matkhau = request.getParameter("matkhau");
                    Part partAnh = request.getPart("anhcccd");

                    if (email == null || hoten == null || hoten.trim().isEmpty()
                            || sdt == null || sdt.trim().isEmpty()) {
                        session.setAttribute("error", "Họ tên và số điện thoại không được để trống.");
                        redirect += "?sua=" + java.net.URLEncoder.encode(email == null ? "" : email, "UTF-8");
                    } else {
                        String tenAnhMoi = null;
                        if (partAnh != null && partAnh.getSize() > 0) {
                            tenAnhMoi = util.ImageStorage.luuAnh(partAnh, "nhanvienbanhang_cccd_");
                        }
                        boolean ok = dao.sua(email, hoten.trim(), sdt.trim(), matkhau, tenAnhMoi);
                        session.setAttribute(ok ? "success" : "error",
                                ok ? "Đã cập nhật thông tin nhân viên " + email + "."
                                   : "Không tìm thấy tài khoản.");
                    }
                    break;
                }

                case "khoa": {
                    String email = request.getParameter("email");
                    boolean ok = dao.capNhatTrangThai(email, "Khóa");
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã khóa tài khoản " + email + "." : "Không tìm thấy tài khoản.");
                    break;
                }

                case "mokhoa": {
                    String email = request.getParameter("email");
                    boolean ok = dao.capNhatTrangThai(email, "Đang hoạt động");
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã mở khóa tài khoản " + email + "." : "Không tìm thấy tài khoản.");
                    break;
                }

                default:
                    break;
            }
        } catch (IllegalArgumentException e) {
            // VD file tải lên không phải ảnh
            session.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu.");
        }

        response.sendRedirect(redirect);
    }
}
