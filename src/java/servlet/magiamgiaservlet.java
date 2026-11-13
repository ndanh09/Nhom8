package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import model.PhieuGiamGia;
import model.nguoidung;
import modeldao.GioHangDAO;
import modeldao.PhieuGiamGiaDAO;
import modeldao.nguoidungdao;

// Servlet dành cho NGƯỜI DÙNG xem danh sách mã giảm giá đang khả dụng
// (khác với phieugiamgiaservlet là servlet quản trị CRUD mã giảm giá)
@WebServlet(name = "magiamgiaservlet", urlPatterns = {"/magiamgiaservlet"})
public class magiamgiaservlet extends HttpServlet {

    private PhieuGiamGiaDAO pggDAO = new PhieuGiamGiaDAO();
    private nguoidungdao spDAO = new nguoidungdao();
    private GioHangDAO ghDAO = new GioHangDAO();

    // Gom danh sách sản phẩm về 1 dòng đại diện / 1 tên sản phẩm,
    // dùng cho ô tìm kiếm gợi ý sang trang Sản phẩm (giống nguoidungservlet).
    private ArrayList<nguoidung> gomTheoTenSanPham(ArrayList<nguoidung> dsGoc) {
        LinkedHashMap<String, nguoidung> unique = new LinkedHashMap<>();
        for (nguoidung sp : dsGoc) {
            if (!unique.containsKey(sp.getTensanpham())) {
                unique.put(sp.getTensanpham(), sp);
            }
        }
        return new ArrayList<>(unique.values());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ArrayList<PhieuGiamGia> listMa = pggDAO.getDanhSachMaGiamGiaKhaDung();

        request.setAttribute("listMa", listMa);

        // Toàn bộ danh sách sản phẩm (bên trang nguoidung) để ô tìm kiếm
        // gợi ý theo sản phẩm toàn hệ thống.
        ArrayList<nguoidung> listsp = gomTheoTenSanPham(spDAO.sp());

        request.setAttribute("listsp", listsp);

        // Số lượng sản phẩm trong giỏ hàng, để badge giỏ hàng ở header
        // luôn hiển thị đúng số thật, đồng bộ với các trang khác.
        HttpSession session = request.getSession();
        String maKH = (String) session.getAttribute("email");
        int cartCount = (maKH == null || maKH.trim().isEmpty()) ? 0 : ghDAO.countCart(maKH);
        request.setAttribute("cartCount", cartCount);

        request.getRequestDispatcher("view/magiamgia.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Ma Giam Gia Servlet (nguoi dung)";
    }
}
