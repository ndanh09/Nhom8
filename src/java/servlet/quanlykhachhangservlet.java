package servlet;

import connect.dbconnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.quanlykhachhang;
import modeldao.quanlykhachhangdao;
import util.AdminGuard;

@WebServlet("/quanlykhachhangservlet")
public class quanlykhachhangservlet extends HttpServlet {

    quanlykhachhangdao dao = new quanlykhachhangdao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        String keyword = request.getParameter("keyword");

        if (action == null) {
            action = "list";
        }

        // FIX: id thiếu/sai định dạng trước đây ném NumberFormatException -> lỗi 500.
        // Với các action cần id, id không hợp lệ thì quay về danh sách.
        if (action.equals("khoa") || action.equals("mokhoa") || action.equals("edit")) {
            try {
                Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendRedirect("quanlykhachhangservlet");
                return;
            }
        }

        switch (action) {

            case "khoa": {

                int idKhoa = Integer.parseInt(request.getParameter("id"));
                dao.capNhatTrangThai(idKhoa, "Đã khóa");
                String redirect = "quanlykhachhangservlet";
                if (keyword != null && !keyword.trim().isEmpty()) {
                    redirect += "?keyword=" + java.net.URLEncoder.encode(keyword.trim(), "UTF-8");
                }
                response.sendRedirect(redirect);
                break;
            }

            case "mokhoa": {

                int idMoKhoa = Integer.parseInt(request.getParameter("id"));
                dao.capNhatTrangThai(idMoKhoa, "Đang hoạt động");
                String redirect = "quanlykhachhangservlet";
                if (keyword != null && !keyword.trim().isEmpty()) {
                    redirect += "?keyword=" + java.net.URLEncoder.encode(keyword.trim(), "UTF-8");
                }
                response.sendRedirect(redirect);
                break;
            }

            case "edit":

                int idEdit = Integer.parseInt(request.getParameter("id"));

                // Đơn đang bị khóa: không được mở form cập nhật cho tới khi mở khóa.
                if (modeldao.TrangThaiDAO.donHangBiKhoaTheoMaDon(idEdit)) {
                    request.getSession().setAttribute("error",
                            "Đơn hàng #ORD-" + idEdit + " đang bị khóa nên không thể cập nhật. Hãy mở khóa trước.");
                    response.sendRedirect("quanlykhachhangservlet");
                    break;
                }

                quanlykhachhang kh = dao.timTheoID(idEdit);

                request.setAttribute("kh", kh);

                List<quanlykhachhang> list = dao.getAllKhachHang(keyword);

                request.setAttribute("listKhachHang", list);
                request.setAttribute("keyword", keyword);

                request.getRequestDispatcher("view/quanlykhachhang.jsp")
                        .forward(request, response);

                break;

            default:

                List<quanlykhachhang> listKhachHang = dao.getAllKhachHang(keyword);

                request.setAttribute("listKhachHang", listKhachHang);
                request.setAttribute("keyword", keyword);

                request.getRequestDispatcher("view/quanlykhachhang.jsp")
                        .forward(request, response);

                break;

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

        String madonhang = request.getParameter("madonhang");
        String makhachhang = request.getParameter("makhachhang");
        String hoten = request.getParameter("hoten");
        String sodienthoai = request.getParameter("sodienthoai");
        String diachi = request.getParameter("diachi");
        String email = request.getParameter("email");
        String ghichu = request.getParameter("ghichu");
        String phuongthucthanhtoan = request.getParameter("phuongthucthanhtoan");
        String tongtien = request.getParameter("tongtien");
        String masanpham = request.getParameter("masanpham");

        quanlykhachhang kh = new quanlykhachhang();

        kh.setMakhachhang(makhachhang);
        kh.setHoten(hoten);
        kh.setSodienthoai(sodienthoai);
        kh.setDiachi(diachi);
        kh.setEmail(email);
        kh.setGhichu(ghichu);
        kh.setPhuongthucthanhtoan(phuongthucthanhtoan);

        // FIX: tổng tiền/mã đơn nhập sai định dạng trước đây gây lỗi 500.
        try {
            if (tongtien == null || tongtien.trim().isEmpty()) {
                kh.setTongtien(0);
            } else {
                kh.setTongtien(Integer.parseInt(tongtien.trim()));
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("quanlykhachhangservlet");
            return;
        }

        kh.setMasanpham(masanpham);

        // Trang Quản lý đơn hàng KHÔNG còn chức năng thêm: chỉ cập nhật đơn đã có.
        // Thiếu / sai mã đơn hàng thì bỏ qua, không tạo đơn mới.
        if (madonhang == null || madonhang.trim().isEmpty()) {
            response.sendRedirect("quanlykhachhangservlet");
            return;
        }
        try {
            kh.setMadonhang(Integer.parseInt(madonhang.trim()));
        } catch (NumberFormatException e) {
            response.sendRedirect("quanlykhachhangservlet");
            return;
        }

        // Đơn đang bị khóa: không được cập nhật (kiểm tra cả ở SQL trong DAO).
        if (modeldao.TrangThaiDAO.donHangBiKhoaTheoMaDon(kh.getMadonhang())) {
            request.getSession().setAttribute("error",
                    "Đơn hàng #ORD-" + kh.getMadonhang() + " đang bị khóa nên không thể cập nhật. Hãy mở khóa trước.");
            response.sendRedirect("quanlykhachhangservlet");
            return;
        }

        dao.capNhatKhachHang(kh);

        response.sendRedirect("quanlykhachhangservlet");

    }

}