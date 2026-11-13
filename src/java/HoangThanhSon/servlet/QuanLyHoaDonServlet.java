package servlet;

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

/**
 * Trang QUẢN LÝ HÓA ĐƠN (vai trò QUANLY): danh sách hóa đơn (mỗi hóa đơn = 1 đơn hàng/madonhang)
 * và nút "Xem hóa đơn" -> /hoadonservlet (hóa đơn tổng), từ đó bấm "Xem chi tiết" từng sản phẩm -> /chitiethoadonservlet. Tách riêng khỏi trang "Cập nhật trạng thái
 * đơn hàng" nhưng vẫn cùng luồng quản lý đơn hàng (cùng vai trò, cùng menu).
 * Chỉ xem: không sửa dữ liệu, nên đơn đang bị khóa vẫn xem được hóa đơn.
 */
@WebServlet("/quanlyhoadonservlet")
public class QuanLyHoaDonServlet extends HttpServlet {

    private final quanlykhachhangdao dao = new quanlykhachhangdao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String keyword = request.getParameter("keyword");
        List<quanlykhachhang> list = dao.getAllKhachHang(keyword);

        // Tìm theo mã hóa đơn: "12", "#ORD-12", "ord-12"
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            if (kw.startsWith("#")) {
                kw = kw.substring(1);
            }
            if (kw.startsWith("ord-")) {
                kw = kw.substring(4);
            }
            try {
                int ma = Integer.parseInt(kw);
                for (quanlykhachhang k : dao.getAllKhachHang()) {
                    if (k.getMadonhang() == ma && list.stream().noneMatch(x -> x.getMadonhang() == ma)) {
                        list.add(0, k);
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }

        request.setAttribute("listHoaDon", list);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/view/quanlyhoadon.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
