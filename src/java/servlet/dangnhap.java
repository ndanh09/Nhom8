package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modeldao.*;
import model.dangkytaikhoan;
import model.quantrivien;
import util.AdminGuard;

@WebServlet(name = "dangnhap", urlPatterns = {"/dangnhapsvl"})
public class dangnhap extends HttpServlet {

    private static final String DA_KHOA = "Đã khóa";

    dangnhapdao dangnhapdao = new dangnhapdao();
    quantriviendao qtvdao = new quantriviendao();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Servlet dangnhap</title></head>");
            out.println("<body><h1>Servlet dangnhap at " + request.getContextPath() + "</h1></body>");
            out.println("</html>");
        }
    }

    // FIX: doGet trước đây rỗng -> vào /dangnhapsvl bằng GET ra trang trắng.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
    }

    // FIX: mở phiên đăng nhập KHÁCH HÀNG. Phải gỡ "isAdmin" còn sót lại từ lần admin
    // "đăng xuất" trước đó, nếu không khách đăng nhập sau sẽ vào được trang admin.
    private void batDauPhienKhach(HttpServletRequest request, dangkytaikhoan user) {
        HttpSession session = request.getSession();
        request.changeSessionId();
        session.removeAttribute("isAdmin");
        session.removeAttribute(AdminGuard.VAI_TRO);
        session.setAttribute("email", user.getEmail());
        session.setAttribute("user", user);
    }

    private void baoLoi(HttpServletRequest request, HttpServletResponse response, String loi)
            throws ServletException, IOException {
        request.setAttribute("error", loi);
        request.getRequestDispatcher("/view/dangnhap.jsp").forward(request, response);
    }

    // So sánh mật khẩu an toàn với null (mật khẩu trong DB có thể NULL -> trước đây NPE/lỗi 500).
    private static boolean khopMatKhau(String trongDb, String nhap) {
        return trongDb != null && nhap != null && trongDb.equals(nhap);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String matkhau = request.getParameter("matkhau");

        if (email == null || email.trim().isEmpty() || matkhau == null) {
            baoLoi(request, response, "Vui lòng nhập email và mật khẩu");
            return;
        }
        email = email.trim();

        // ----- Tài khoản quản trị cố định (hardcode): ADMIN hoặc QUANLY -----
        String vaiTroCoDinh = AdminGuard.vaiTroTaiKhoanCoDinh(email, matkhau);
        if (vaiTroCoDinh != null) {
            AdminGuard.moPhien(request, email, vaiTroCoDinh);
            response.sendRedirect(request.getContextPath() + AdminGuard.trangChu(vaiTroCoDinh));
            return;
        }

        // ----- Kiểm tra admin trong DB (vai trò ADMIN) -----
        quantrivien qtv = qtvdao.findByEmail(email);
        if (qtv != null) {
            if (DA_KHOA.equals(qtv.getTrangthai())) {
                baoLoi(request, response, "Tài khoản đã bị khóa");
            } else if (khopMatKhau(qtv.getMatkhau(), matkhau)) {
                AdminGuard.moPhien(request, email, AdminGuard.ADMIN);
                response.sendRedirect(request.getContextPath() + AdminGuard.trangChu(AdminGuard.ADMIN));
            } else {
                baoLoi(request, response, "Sai mật khẩu");
            }
            return;
        }

        // Email của tài khoản quản trị cố định nhưng nhập sai mật khẩu
        if (AdminGuard.laEmailQuanTriCoDinh(email)) {
            baoLoi(request, response, "Sai mật khẩu");
            return;
        }

        // ----- 2 tài khoản người dùng cố định (hardcode) -----
        boolean fixedEmailExists = "khanh123@gmail.com".equals(email) || "danh123@gmail.com".equals(email);
        if (fixedEmailExists) {
            boolean isFixedUser = ("khanh123@gmail.com".equals(email) && "123".equals(matkhau))
                    || ("danh123@gmail.com".equals(email) && "123".equals(matkhau));
            if (isFixedUser) {
                dangkytaikhoan fixedUser = new dangkytaikhoan();
                fixedUser.setEmail(email);
                fixedUser.setMatkhau(matkhau);
                batDauPhienKhach(request, fixedUser);
                response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
            } else {
                baoLoi(request, response, "Sai mật khẩu");
            }
            return;
        }

        // ----- Tài khoản người dùng thường trong DB -----
        dangkytaikhoan userByEmail = dangnhapdao.findByEmail(email);

        if (userByEmail == null) {
            baoLoi(request, response, "Tài khoản không tồn tại");
            return;
        }

        if (DA_KHOA.equals(userByEmail.getTrangthai())) {
            baoLoi(request, response, "Tài khoản đã bị khóa");
            return;
        }

        if (khopMatKhau(userByEmail.getMatkhau(), matkhau)) {
            batDauPhienKhach(request, userByEmail);
            response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
        } else {
            baoLoi(request, response, "Sai mật khẩu");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
