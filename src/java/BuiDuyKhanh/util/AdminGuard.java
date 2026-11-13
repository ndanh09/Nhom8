package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Phân quyền khu quản trị theo 3 VAI TRÒ (lưu ở session "vaiTro"):
 *
 *  - ADMIN    : trang admin (/qtv), quản lý vận chuyển, quản lý nhân sự và lương.
 *  - QUANLY   : quản lý sản phẩm (+ size), quản lý đơn hàng, quản lý kho hàng,
 *               quản lý mã giảm giá, cập nhật trạng thái đơn hàng.
 *  - DOANHTHU : quản lý doanh thu + quản lý trò chuyện (chung 1 luồng).
 *
 * Muốn chuyển một trang sang vai trò khác: đổi lời gọi kiemTraAdmin / kiemTraQuanLy /
 * kiemTraDoanhThu ở servlet đó và đổi nhãn menu tương ứng trong JSP (c:if vaiTro).
 *
 * Session "isAdmin" vẫn được đặt = true cho CẢ 2 vai trò, nghĩa là "đang đăng nhập khu
 * quản trị (không phải khách hàng)" - header/footer/chatbot dùng cờ này. Quyền vào từng
 * trang quản trị phải kiểm tra bằng vai trò (kiemTraAdmin / kiemTraQuanLy), không dùng isAdmin.
 */
public final class AdminGuard {

    public static final String VAI_TRO = "vaiTro";
    public static final String ADMIN = "ADMIN";
    public static final String QUANLY = "QUANLY";
    public static final String DOANHTHU = "DOANHTHU";

    // ---- Tài khoản cố định (mật khẩu mặc định giống 2 tài khoản admin cũ) ----
    private static final String MAT_KHAU_MAC_DINH = "admin123";
    private static final String[] EMAIL_ADMIN = {
        "anhldth08719@gmail.com",
        "khanhbdth08636@gmail.com"
    };
    private static final String[] EMAIL_QUANLY = {
        "locnxth08880@gmail.com"
    };
    // Vai trò quản lý doanh thu + trò chuyện. ĐỔI email này thành email thật của người phụ trách.
    private static final String[] EMAIL_DOANHTHU = {
        "doanhthu@lodi.local"
    };

    private AdminGuard() {
    }

    private static boolean co(String[] ds, String email) {
        if (email == null) {
            return false;
        }
        for (String e : ds) {
            if (e.equals(email)) {
                return true;
            }
        }
        return false;
    }

    /** @return ADMIN / QUANLY / DOANHTHU nếu đúng email + mật khẩu của tài khoản cố định, ngược lại null. */
    public static String vaiTroTaiKhoanCoDinh(String email, String matkhau) {
        if (matkhau == null || !MAT_KHAU_MAC_DINH.equals(matkhau)) {
            return null;
        }
        if (co(EMAIL_ADMIN, email)) {
            return ADMIN;
        }
        if (co(EMAIL_QUANLY, email)) {
            return QUANLY;
        }
        if (co(EMAIL_DOANHTHU, email)) {
            return DOANHTHU;
        }
        return null;
    }

    /** Email này thuộc 1 tài khoản quản trị cố định (dùng để báo "Sai mật khẩu" thay vì "không tồn tại"). */
    public static boolean laEmailQuanTriCoDinh(String email) {
        return co(EMAIL_ADMIN, email) || co(EMAIL_QUANLY, email) || co(EMAIL_DOANHTHU, email);
    }

    /**
     * Mở phiên đăng nhập khu quản trị với 1 vai trò. Đổi session id (chống session
     * fixation) và gỡ phiên khách hàng còn sót lại.
     */
    public static void moPhien(HttpServletRequest request, String email, String vaiTro) {
        HttpSession session = request.getSession();
        request.changeSessionId();
        session.removeAttribute("user");
        session.setAttribute("email", email);
        session.setAttribute("isAdmin", true);
        session.setAttribute(VAI_TRO, vaiTro);
    }

    /** Trang đầu tiên của từng vai trò sau khi đăng nhập. */
    public static String trangChu(String vaiTro) {
        if (QUANLY.equals(vaiTro)) {
            return "/nguoibanservlet";
        }
        if (DOANHTHU.equals(vaiTro)) {
            return "/doanhthuservlet";
        }
        return "/qtv";
    }

    private static String vaiTroHienTai(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return null;
        }
        Object vt = session.getAttribute(VAI_TRO);
        if (ADMIN.equals(vt) || QUANLY.equals(vt) || DOANHTHU.equals(vt)) {
            return (String) vt;
        }
        return null;
    }

    /** Đang đăng nhập khu quản trị (bất kỳ vai trò nào). */
    public static boolean isAdmin(HttpServletRequest request) {
        return vaiTroHienTai(request) != null;
    }

    public static boolean laAdmin(HttpServletRequest request) {
        return ADMIN.equals(vaiTroHienTai(request));
    }

    public static boolean laQuanLy(HttpServletRequest request) {
        return QUANLY.equals(vaiTroHienTai(request));
    }

    public static boolean laDoanhThu(HttpServletRequest request) {
        return DOANHTHU.equals(vaiTroHienTai(request));
    }

    /**
     * @return true nếu đúng vai trò. Nếu không: chưa đăng nhập -> về trang đăng nhập;
     * đã đăng nhập vai trò khác -> về trang chủ của vai trò đó. Trả về false (servlet
     * gọi phải return ngay).
     */
    private static boolean kiemTra(HttpServletRequest request, HttpServletResponse response, String vaiTroCan)
            throws IOException {
        String vt = vaiTroHienTai(request);
        if (vaiTroCan.equals(vt)) {
            return true;
        }
        String dich = vt == null ? "/view/dangnhap.jsp" : trangChu(vt);
        response.sendRedirect(request.getContextPath() + dich);
        return false;
    }

    public static boolean kiemTraAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        return kiemTra(request, response, ADMIN);
    }

    public static boolean kiemTraQuanLy(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        return kiemTra(request, response, QUANLY);
    }

    public static boolean kiemTraDoanhThu(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        return kiemTra(request, response, DOANHTHU);
    }
}
