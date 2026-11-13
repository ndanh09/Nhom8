package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modeldao.quantriviendao;
import modeldao.dangkydao;
import model.dangkytaikhoan;
import util.AdminGuard;

@WebServlet(name = "quantrivien", urlPatterns = {"/qtv"})
public class quantrivien extends HttpServlet {

    private final quantriviendao dao = new quantriviendao();
    // Dao của bảng dangky, dùng để quản lý tài khoản khách hàng (đăng nhập được trang nguoidung)
    private final dangkydao khdao = new dangkydao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chỉ vai trò ADMIN mới vào được trang này (chưa đăng nhập -> trang đăng nhập;
        // vai trò QUANLY -> về trang quản lý sản phẩm).
        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        String keyword = request.getParameter("keyword");

        if ("edit".equals(action)) {
            request.setAttribute("hoten", request.getParameter("hoten"));
            request.setAttribute("email", request.getParameter("email"));
            request.setAttribute("sodienthoai", request.getParameter("sodienthoai"));
            request.setAttribute("ngaydangky", request.getParameter("ngaydangky"));
            request.setAttribute("matkhau", request.getParameter("matkhau"));
        }
        request.setAttribute("listqtv", dao.getAll(keyword));
        request.setAttribute("listkh", khdao.dktk(keyword));
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/view/quantrivien.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        // FIX: "admin_login" là bước đăng nhập nên phải cho phép khi CHƯA có session.
        // Mọi action khác (thêm/sửa/xóa tài khoản admin, khách hàng...) bắt buộc
        // phải đã đăng nhập admin từ trước mới được thực hiện.
        if (!"admin_login".equals(action) && !AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }

        try {
            // GỘP: 1 action duy nhất để thêm tài khoản, phân biệt bằng tham số loaitk ("qtv" hoặc "kh")
            if (action.equals("add_taikhoan")) {

                // SỬA: chức năng "Thêm tài khoản" chỉ còn tạo KHÁCH HÀNG (nguoidung).
                // Không đọc/tin tham số "loaitk" từ client nữa để chặn việc ai đó tự
                // sửa form/gửi request thủ công với loaitk=qtv nhằm tạo quản trị viên.
                // Việc thêm quản trị viên mới (nếu cần) phải làm trực tiếp trong DB
                // hoặc bổ sung một chức năng riêng, có kiểm soát chặt hơn.
                String hoten = request.getParameter("hoten");
                String email = request.getParameter("email");
                String sodienthoai = request.getParameter("sodienthoai");
                String matkhau = request.getParameter("matkhau");
                String diachi = request.getParameter("diachi");

                if (khdao.check(email)) {
                    request.setAttribute("error", "Email khách hàng đã tồn tại!");
                } else {
                    dangkytaikhoan kh = new dangkytaikhoan(email, hoten, matkhau, sodienthoai, diachi);
                    khdao.dk(kh);
                    request.setAttribute("success", "Tạo tài khoản khách hàng thành công! Tài khoản có thể đăng nhập vào trang người dùng.");
                }

            } else if (action.equals("update")) {
                // Chỉ áp dụng cho quản trị viên (giữ nguyên như cũ)
                String hoten = request.getParameter("hoten");
                String email = request.getParameter("email");
                String sodienthoai = request.getParameter("sodienthoai");
                String ngaydangky = request.getParameter("ngaydangky");
                String matkhau = request.getParameter("matkhau");

                if (!dao.check(email)) {
                    request.setAttribute("error", "Không tìm thấy quản trị viên!");
                } else {
                    model.quantrivien qtv = new model.quantrivien(hoten, email, sodienthoai, ngaydangky, matkhau);
                    dao.sua(qtv);
                    request.setAttribute("success", "Cập nhật thành công!");
                }

            // GỘP: 1 action duy nhất để khóa tài khoản, phân biệt bằng tham số loaitk
            } else if (action.equals("khoa_taikhoan")) {

                String loaitk = request.getParameter("loaitk");
                String email = request.getParameter("email");

                // FIX: trước đây luôn báo "đã khóa" dù không có dòng nào bị ảnh hưởng (VD tài khoản
                // admin cố định không nằm trong DB), và admin có thể tự khóa chính mình.
                String emailDangNhap = (String) request.getSession().getAttribute("email");

                if ("qtv".equals(loaitk)) {
                    if (email != null && email.equalsIgnoreCase(emailDangNhap)) {
                        request.setAttribute("error", "Bạn không thể tự khóa tài khoản đang đăng nhập!");
                    } else if (dao.capNhatTrangThai(email, "Đã khóa")) {
                        request.setAttribute("success", "Đã khóa tài khoản quản trị viên!");
                    } else {
                        request.setAttribute("error", "Không khóa được tài khoản quản trị viên này!");
                    }
                } else {
                    if (khdao.capNhatTrangThai(email, "Đã khóa")) {
                        request.setAttribute("success", "Đã khóa tài khoản khách hàng!");
                    } else {
                        request.setAttribute("error", "Không khóa được tài khoản khách hàng này!");
                    }
                }

            // GỘP: 1 action duy nhất để mở khóa tài khoản, phân biệt bằng tham số loaitk
            } else if (action.equals("mokhoa_taikhoan")) {

                String loaitk = request.getParameter("loaitk");
                String email = request.getParameter("email");

                if ("qtv".equals(loaitk)) {
                    if (dao.capNhatTrangThai(email, "Đang hoạt động")) {
                        request.setAttribute("success", "Đã mở khóa tài khoản quản trị viên!");
                    } else {
                        request.setAttribute("error", "Không mở khóa được tài khoản quản trị viên này!");
                    }
                } else {
                    if (khdao.capNhatTrangThai(email, "Đang hoạt động")) {
                        request.setAttribute("success", "Đã mở khóa tài khoản khách hàng!");
                    } else {
                        request.setAttribute("error", "Không mở khóa được tài khoản khách hàng này!");
                    }
                }

            } else if (action.equals("admin_login")) {

                String email = request.getParameter("email");
                String matkhau = request.getParameter("matkhau");

                // Tài khoản cố định (ADMIN hoặc QUANLY) hoặc quản trị viên trong DB (ADMIN)
                String vaiTro = AdminGuard.vaiTroTaiKhoanCoDinh(email, matkhau);
                if (vaiTro == null && dao.dangNhap(email, matkhau)) {
                    vaiTro = AdminGuard.ADMIN;
                }
                if (vaiTro != null) {
                    AdminGuard.moPhien(request, email, vaiTro);
                    response.sendRedirect(request.getContextPath() + AdminGuard.trangChu(vaiTro));
                    return;
                } else {
                    request.setAttribute("thongbao", "Sai email hoặc mật khẩu");
                    request.getRequestDispatcher("/view/dangnhap.jsp").forward(request, response);
                    return;
                }

            } else if (action.equals("user_login")) {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra!");
        }

        String keyword = request.getParameter("keyword");
        request.setAttribute("listqtv", dao.getAll(keyword));
        request.setAttribute("listkh", khdao.dktk(keyword));
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/view/quantrivien.jsp").forward(request, response);
    }
}
