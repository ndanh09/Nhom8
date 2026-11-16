package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import model.nguoidung;
import modeldao.GioHangDAO;
import modeldao.nguoidungdao;

@WebServlet(name = "ChiTietSanPhamServlet", urlPatterns = {"/chitietsanpham"})
public class ChiTietSanPhamServlet extends HttpServlet {

    nguoidungdao dao = new nguoidungdao();
    GioHangDAO ghDAO = new GioHangDAO();

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

        String ma = request.getParameter("ma");

        if (ma == null || ma.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
            return;
        }

        // Sản phẩm đang được xem (theo mã / theo size đang chọn)
        nguoidung sp = dao.layTheoMa(ma);

        if (sp == null) {
            response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
            return;
        }

        // Danh sách các biến thể (size) khác nhau của cùng sản phẩm này
        ArrayList<nguoidung> dsSize = dao.layTheoTen(sp.getTensanpham());

        request.setAttribute("sp", sp);
        request.setAttribute("dsSize", dsSize);

        // Toàn bộ danh sách sản phẩm (bên trang nguoidung) để ô tìm kiếm
        // gợi ý theo sản phẩm toàn hệ thống.
        ArrayList<nguoidung> listsp = gomTheoTenSanPham(dao.sp());

        request.setAttribute("listsp", listsp);

        // Số lượng sản phẩm trong giỏ hàng, để badge giỏ hàng ở header luôn
        // hiển thị đúng số thật, đồng bộ với các trang khác.
        HttpSession session = request.getSession();
        String maKH = (String) session.getAttribute("email");
        int cartCount = (maKH == null || maKH.trim().isEmpty()) ? 0 : ghDAO.countCart(maKH);
        request.setAttribute("cartCount", cartCount);

        // Mục "Đánh giá & nhận xét": điểm trung bình, phân bố sao, danh sách (lọc theo ?sao=1..5)
        // và trạng thái nút "Viết đánh giá". Chỉ lấy đánh giá đang hiển thị.
        DanhGiaServlet.napDanhGiaSanPham(request, ma, request.getParameter("sao"));

        request.getRequestDispatcher("view/chitietsanpham.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet xem chi tiết sản phẩm";
    }
}
