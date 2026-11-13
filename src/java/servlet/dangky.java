package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import model.*;
import modeldao.*;

@WebServlet(name="dangky", urlPatterns={"/dangkysvl"})
public class dangky extends HttpServlet {
    dangkydao dao = new dangkydao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // FIX: dangky.jsp không dùng "listdk"; trước đây nạp cả bảng tài khoản (kèm mật khẩu)
        // vào request mỗi lần mở trang đăng ký -> thừa và dễ rò rỉ dữ liệu.
        request.getRequestDispatcher("view/dangky.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // FIX: thiếu dòng này nên tên tiếng Việt bị lỗi font và (nếu container đọc body bằng
        // ISO-8859-1) action "đăng ký" không khớp -> bấm đăng ký mà không có gì xảy ra.
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("đăng ký".equals(action)) {
                String email = request.getParameter("email");
                String hoten = request.getParameter("hoten");
                String matkhau = request.getParameter("matkhau"); 
                String sodienthoai = request.getParameter("sodienthoai"); // Lấy thêm số điện thoại từ giao diện
                String diachi = request.getParameter("diachi"); // SỬA: lấy thêm địa chỉ từ giao diện

                if (email == null || email.trim().isEmpty() || matkhau == null || matkhau.isEmpty()
                        || hoten == null || hoten.trim().isEmpty()) {
                    request.setAttribute("error", "Vui lòng nhập đầy đủ họ tên, email và mật khẩu");
                } else if (dao.check(email.trim())) {
                    request.setAttribute("error", "Email đã được đăng ký");
                } else {
                    // Truyền thêm biến diachi vào khởi tạo đối tượng
                    dangkytaikhoan dk = new dangkytaikhoan(email.trim(), hoten.trim(), matkhau, sodienthoai, diachi);
                    // FIX: dao.dk() trước đây trả void và nuốt lỗi SQL nên luôn báo "thành công".
                    if (dao.dk(dk)) {
                        request.setAttribute("success", "Đã đăng ký tài khoản thành công");
                    } else {
                        request.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại sau");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // FIX: không đưa e.getMessage() (chi tiết lỗi SQL/hệ thống) ra giao diện.
            request.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại sau");
        }
        request.getRequestDispatcher("view/dangky.jsp").forward(request, response);
    }
}
