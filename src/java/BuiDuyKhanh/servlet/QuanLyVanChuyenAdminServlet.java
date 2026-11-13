package servlet;

import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.AdminGuard;
import jakarta.servlet.http.Part;
import model.VanChuyen;
import modeldao.VanChuyenDAO;

/**
 * Trang admin: quản lý tài khoản vận chuyển. Chỉ admin mới được tạo tài
 * khoản vận chuyển (không có hình thức tự đăng ký/ứng tuyển). Khi tạo tài
 * khoản, admin nhập kèm ảnh CCCD + ảnh xác minh của nhân viên để lưu hồ sơ.
 * Admin cũng có thể xem đầy đủ thông tin nhân viên vận chuyển hiện có, và
 * khóa/mở khóa tài khoản.
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB / 1 ảnh
        maxRequestSize = 1024 * 1024 * 30     // 30MB / request
)
@WebServlet(name = "QuanLyVanChuyenAdminServlet", urlPatterns = {"/quanlyvanchuyenadmin"})
public class QuanLyVanChuyenAdminServlet extends HttpServlet {

    private final VanChuyenDAO dao = new VanChuyenDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }

        String keyword = request.getParameter("keyword");
        request.setAttribute("dsTaiKhoan", dao.danhSachTaiKhoan(keyword));
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher("/view/quanlyvanchuyenadmin.jsp").forward(request, response);
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

        try {
            switch (action == null ? "" : action) {

                case "them_taikhoan": {
                    String email = request.getParameter("email");
                    String hoten = request.getParameter("hoten");
                    String matkhau = request.getParameter("matkhau");
                    String sdt = request.getParameter("sodienthoai");
                    String khuvuc = request.getParameter("khuvuc");

                    Part partAnhCccd = request.getPart("anhcccd");
                    Part partAnhXacMinh = request.getPart("anhxacminh");
                    boolean thieuAnh = partAnhCccd == null || partAnhCccd.getSize() == 0
                            || partAnhXacMinh == null || partAnhXacMinh.getSize() == 0;

                    if (email == null || email.trim().isEmpty()
                            || hoten == null || hoten.trim().isEmpty()
                            || matkhau == null || matkhau.trim().isEmpty()
                            || sdt == null || sdt.trim().isEmpty()
                            || khuvuc == null || khuvuc.trim().isEmpty()) {
                        session.setAttribute("error", "Vui lòng nhập đầy đủ thông tin bắt buộc.");
                    } else if (thieuAnh) {
                        session.setAttribute("error", "Vui lòng tải lên đầy đủ ảnh CCCD và ảnh xác minh.");
                    } else if (dao.checkEmailTonTai(email.trim())) {
                        session.setAttribute("error", "Email này đã có tài khoản vận chuyển.");
                    } else {
                        // FIX: lưu qua ImageStorage.luuAnh (chống path traversal qua tên file, chỉ nhận file ảnh).
                        String tenAnhCccd = util.ImageStorage.luuAnh(partAnhCccd, "vanchuyen_cccd_");
                        String tenAnhXacMinh = util.ImageStorage.luuAnh(partAnhXacMinh, "vanchuyen_xacminh_");

                        VanChuyen vc = new VanChuyen(email.trim(), hoten.trim(), matkhau, sdt.trim(), khuvuc.trim());
                        vc.setAnhcccd(tenAnhCccd);
                        vc.setAnhxacminh(tenAnhXacMinh);
                        boolean ok = dao.dangKy(vc);
                        if (ok) {
                            session.setAttribute("success",
                                    "Đã tạo tài khoản vận chuyển cho " + email.trim()
                                    + ". Vui lòng gửi thông tin đăng nhập (email/mật khẩu) cho người vận chuyển.");
                        } else {
                            session.setAttribute("error",
                                    "Tạo tài khoản thất bại (lỗi cơ sở dữ liệu). Kiểm tra lại đã chạy "
                                    + "migration_vanchuyen.sql bản mới nhất (bổ sung cột anhcccd, anhxacminh) chưa.");
                        }
                    }
                    break;
                }

                case "suakhuvuc": {
                    String email = request.getParameter("email");
                    String khuvuc = request.getParameter("khuvuc");
                    if (khuvuc == null || khuvuc.trim().isEmpty()) {
                        session.setAttribute("error", "Vui lòng chọn tỉnh/thành hợp lệ.");
                    } else {
                        boolean ok = dao.capNhatKhuVuc(email, khuvuc.trim());
                        session.setAttribute(ok ? "success" : "error",
                                ok ? "Đã cập nhật khu vực giao hàng cho " + email + " sang " + khuvuc.trim() + "."
                                   : "Không tìm thấy tài khoản.");
                    }
                    break;
                }

                case "khoa": {
                    String email = request.getParameter("email");
                    boolean ok = dao.capNhatTrangThaiTaiKhoan(email, "Khóa");
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã khóa tài khoản " + email + "." : "Không tìm thấy tài khoản.");
                    break;
                }

                case "mokhoa": {
                    String email = request.getParameter("email");
                    boolean ok = dao.capNhatTrangThaiTaiKhoan(email, "Đang hoạt động");
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã mở khóa tài khoản " + email + "." : "Không tìm thấy tài khoản.");
                    break;
                }

                default:
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("error", "Dữ liệu nhập vào không hợp lệ.");
        } catch (IllegalArgumentException e) {
            // Lỗi do người dùng (VD file tải lên không phải ảnh) -> hiện đúng thông báo đã viết sẵn.
            session.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu.");
        }

        response.sendRedirect(request.getContextPath() + "/quanlyvanchuyenadmin");
    }
}
