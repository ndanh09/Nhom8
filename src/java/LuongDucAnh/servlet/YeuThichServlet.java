package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import model.SanPhamYeuThich;
import model.nguoidung;
import modeldao.GioHangDAO;
import modeldao.YeuThichDAO;
import modeldao.nguoidungdao;

/**
 * LUỒNG KHÁCH HÀNG - yêu thích áo.
 *  GET  /yeuthichservlet               : trang "Yêu thích" của tôi
 *  POST /yeuthichservlet action=toggle : bật/tắt tim (JSON)  - tham số masanpham (hoặc magoc)
 *  POST /yeuthichservlet action=status : cho 1 loạt masanpham trên trang, trả về mã nào đã yêu thích (JSON)
 * Chỉ khách hàng đã đăng nhập mới dùng được (tài khoản quản trị không có yêu thích).
 */
@WebServlet(name = "YeuThichServlet", urlPatterns = {"/yeuthichservlet"})
public class YeuThichServlet extends HttpServlet {

    private final YeuThichDAO dao = new YeuThichDAO();
    private final nguoidungdao spDao = new nguoidungdao();
    private final GioHangDAO ghDAO = new GioHangDAO();

    /** Email khách hàng đang đăng nhập; null nếu chưa đăng nhập hoặc đang ở phiên quản trị. */
    private static String emailKhach(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null
                || Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return null;
        }
        String email = (String) session.getAttribute("email");
        return (email == null || email.trim().isEmpty()) ? null : email.trim();
    }

    private static String jsonEscape(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static void ghiJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    private static String jsonLoi(String message, boolean canDangNhap) {
        return "{\"success\":false,\"login\":" + canDangNhap + ",\"message\":\"" + jsonEscape(message) + "\"}";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = emailKhach(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        ArrayList<SanPhamYeuThich> ds = dao.layCuaKhach(email);
        request.setAttribute("dsYeuThich", ds);

        // Dữ liệu cho ô tìm kiếm gợi ý + badge giỏ hàng ở header (giống các trang khách khác).
        LinkedHashMap<String, nguoidung> theoTen = new LinkedHashMap<>();
        for (nguoidung sp : spDao.sp()) {
            if (!theoTen.containsKey(sp.getTensanpham())) {
                theoTen.put(sp.getTensanpham(), sp);
            }
        }
        request.setAttribute("listsp", new ArrayList<>(theoTen.values()));
        request.setAttribute("cartCount", ghDAO.countCart(email));

        request.getRequestDispatcher("/view/yeuthich.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String email = emailKhach(request);

        if ("status".equals(action)) {
            // Khách chưa đăng nhập: không có gì được tô đỏ, nhưng không phải lỗi.
            if (email == null) {
                ghiJson(response, "{\"success\":true,\"login\":false,\"fav\":[]}");
                return;
            }
            ArrayList<String> fav = dao.locMaSanPhamDaThich(email, request.getParameterValues("ma"));
            StringBuilder sb = new StringBuilder("{\"success\":true,\"login\":true,\"fav\":[");
            for (int i = 0; i < fav.size(); i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append('"').append(jsonEscape(fav.get(i))).append('"');
            }
            sb.append("]}");
            ghiJson(response, sb.toString());
            return;
        }

        if ("toggle".equals(action)) {
            if (email == null) {
                ghiJson(response, jsonLoi("Bạn cần đăng nhập tài khoản khách hàng để yêu thích sản phẩm!", true));
                return;
            }
            // Mã gốc lấy từ DB theo mã sản phẩm (không tin client); trang "Yêu thích" gửi thẳng magoc.
            String magoc = request.getParameter("magoc");
            if (magoc == null || magoc.trim().isEmpty()) {
                magoc = dao.magocTuMaSanPham(request.getParameter("masanpham"));
            }
            if (magoc == null || !dao.tonTaiMagoc(magoc)) {
                // Cho phép bỏ yêu thích 1 sản phẩm đã bị xóa khỏi cửa hàng (dòng cũ còn sót trong bảng yeuthich).
                String magocCu = request.getParameter("magoc");
                if (magocCu != null && dao.layMagocDaThich(email).contains(magocCu.trim())) {
                    dao.doiTrangThai(email, magocCu.trim());
                    ghiJson(response, "{\"success\":true,\"liked\":false,\"count\":0}");
                } else {
                    ghiJson(response, jsonLoi("Sản phẩm không tồn tại!", false));
                }
                return;
            }
            magoc = magoc.trim();
            int kq = dao.doiTrangThai(email, magoc);
            if (kq < 0) {
                ghiJson(response, jsonLoi("Có lỗi xảy ra, vui lòng thử lại.", false));
                return;
            }
            ghiJson(response, "{\"success\":true,\"liked\":" + (kq == 1)
                    + ",\"count\":" + dao.demLuotYeuThich(magoc) + "}");
            return;
        }

        ghiJson(response, jsonLoi("Yêu cầu không hợp lệ!", false));
    }

    @Override
    public String getServletInfo() {
        return "Yêu thích áo - luồng khách hàng";
    }
}
