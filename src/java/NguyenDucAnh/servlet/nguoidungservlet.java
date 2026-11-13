package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import model.*;
import modeldao.*;

@WebServlet(name = "nguoidungservlet", urlPatterns = {"/nguoidungservlet"})
public class nguoidungservlet extends HttpServlet {

    nguoidungdao dao = new nguoidungdao();
    GioHangDAO ghDAO = new GioHangDAO();

    // FIX: gom danh sách sản phẩm về 1 dòng đại diện / 1 tên sản phẩm,
    // giống cách trang chủ đang hiển thị - vì bảng nguoidung có nhiều dòng
    // cho cùng 1 sản phẩm (mỗi size 1 dòng). Việc chọn size cụ thể sẽ
    // chuyển sang làm ở trang chi tiết sản phẩm (chitietsanpham) hoặc giỏ hàng.
    //
    // FIX MỚI: trước đây luôn lấy dòng (size) ĐẦU TIÊN gặp trong DB làm đại
    // diện, bất kể còn hàng hay không -> nếu size đầu tiên đó hết hàng thì
    // card báo "HẾT HÀNG" dù các size khác của sản phẩm vẫn còn. Nay ưu
    // tiên chọn 1 size CÒN HÀNG làm đại diện (nếu có); chỉ khi TẤT CẢ size
    // đều hết/ngừng bán thì mới coi là hết hàng.
    private ArrayList<nguoidung> gomTheoTenSanPham(ArrayList<nguoidung> dsGoc) {
        LinkedHashMap<String, nguoidung> unique = new LinkedHashMap<>();
        for (nguoidung sp : dsGoc) {
            String ten = sp.getTensanpham();
            boolean conHang = "1".equals(sp.getTrangthai()) && sp.getSoluongsanpham() > 0;
            nguoidung daCo = unique.get(ten);
            if (daCo == null) {
                unique.put(ten, sp);
            } else {
                boolean daCoConHang = "1".equals(daCo.getTrangthai()) && daCo.getSoluongsanpham() > 0;
                if (!daCoConHang && conHang) {
                    unique.put(ten, sp);
                }
            }
        }
        return new ArrayList<>(unique.values());
    }

    // FIX: thoát ký tự đặc biệt khi nhúng chuỗi vào JSON thủ công. Trước đây ghép thẳng
    // e.getMessage() (có thể chứa dấu " hoặc null) làm JSON hỏng -> JS phía giao diện không parse được.
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

    private static String jsonLoi(String message) {
        return "{\"success\":false,\"message\":\"" + jsonEscape(message) + "\"}";
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet nguoidungservlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet nguoidungservlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // Trả về JSON danh sách tên sản phẩm (đã gom theo tên) để các trang
        // không đi qua servlet này (Giới thiệu, Liên hệ) có dữ liệu cho ô
        // tìm kiếm gợi ý, đồng bộ với trang Sản phẩm / Giỏ hàng / Đơn hàng.
        if ("suggest".equals(action)) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ArrayList<nguoidung> listGoiY = gomTheoTenSanPham(dao.sp());

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < listGoiY.size(); i++) {
                String ten = listGoiY.get(i).getTensanpham();
                if (ten == null) {
                    ten = "";
                }
                ten = ten.replace("\\", "\\\\").replace("\"", "\\\"");
                if (i > 0) {
                    json.append(",");
                }
                json.append("\"").append(ten).append("\"");
            }
            json.append("]");

            try (PrintWriter out = response.getWriter()) {
                out.print(json.toString());
                out.flush();
            }
            return;
        }

        // Trả về JSON số lượng sản phẩm trong giỏ hàng, để các trang tĩnh
        // (Trang chủ, Giới thiệu, Liên hệ) cập nhật đúng badge giỏ hàng ở
        // header, đồng bộ với các trang có sẵn dữ liệu giỏ (đi qua servlet).
        if ("cartcount".equals(action)) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession();
            String maKH = (String) session.getAttribute("email");
            int count = (maKH == null || maKH.trim().isEmpty()) ? 0 : ghDAO.countCart(maKH);

            try (PrintWriter out = response.getWriter()) {
                out.print("{\"count\":" + count + "}");
                out.flush();
            }
            return;
        }

        ArrayList<nguoidung> listsp = gomTheoTenSanPham(dao.sp());

        request.setAttribute("listsp", listsp);

        // Số lượng sản phẩm trong giỏ hàng, để badge giỏ hàng ở header luôn
        // hiển thị đúng số thật, đồng bộ với các trang khác.
        HttpSession session = request.getSession();
        String maKH = (String) session.getAttribute("email");
        int cartCount = (maKH == null || maKH.trim().isEmpty()) ? 0 : ghDAO.countCart(maKH);
        request.setAttribute("cartCount", cartCount);

        request.getRequestDispatcher("view/nguoidung.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("addToCart".equals(action)) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter out = response.getWriter()) {

                // FIX: giá, trạng thái, size và tồn kho nay lấy từ DB theo masanpham, KHÔNG tin các
                // tham số giasanpham/trangthai/kichco do trình duyệt gửi (trước đây sửa request là
                // mua được giá 1đ hoặc bỏ qua trạng thái "Ngừng bán").
                String masanpham = request.getParameter("masanpham");

                int soluong;
                try {
                    soluong = Integer.parseInt(request.getParameter("soluong").trim());
                } catch (Exception e) {
                    soluong = 0;
                }

                // FIX: số lượng 0/âm trước đây vẫn được thêm vào giỏ (0 làm thanh toán bị chia cho 0).
                if (soluong < 1) {
                    out.print(jsonLoi("Số lượng không hợp lệ!"));
                    out.flush();
                    return;
                }

                nguoidung sp = (masanpham == null) ? null : dao.layTheoMa(masanpham);

                if (sp == null) {
                    out.print(jsonLoi("Sản phẩm không tồn tại!"));
                    out.flush();
                    return;
                }

                int tonKho = sp.getSoluongsanpham();

                if ("0".equals(sp.getTrangthai())) {
                    out.print(jsonLoi("Sản phẩm này đã ngừng bán!"));
                    out.flush();
                    return;
                }

                if ("2".equals(sp.getTrangthai()) || tonKho <= 0) {
                    out.print(jsonLoi("Sản phẩm này đã hết hàng!"));
                    out.flush();
                    return;
                }

                if (soluong > tonKho) {
                    out.print(jsonLoi("Không đủ sản phẩm để mua! Hiện chỉ còn " + tonKho + " sản phẩm."));
                    out.flush();
                    return;
                }

                HttpSession session = request.getSession();
                String makhachhang = (String) session.getAttribute("email");

                if (makhachhang == null || makhachhang.trim().isEmpty()) {
                    out.print(jsonLoi("Bạn cần đăng nhập trước khi thêm vào giỏ hàng!"));
                    out.flush();
                    return;
                }

                try {

                    String kichco = sp.getKichco() != null ? sp.getKichco() : request.getParameter("kichco");

                    // Cộng dồn với số lượng ĐANG CÓ trong giỏ (activeThemGioHang() cộng dồn chứ
                    // không ghi đè) rồi so với đúng tồn kho.
                    int soLuongDangCoTrongGio = ghDAO.getSoLuongTrongGio(makhachhang, masanpham, kichco);

                    if (soLuongDangCoTrongGio + soluong > tonKho) {
                        out.print(jsonLoi("Không đủ hàng! Bạn đang có " + soLuongDangCoTrongGio
                                + " sản phẩm này trong giỏ, kho chỉ còn " + tonKho + " sản phẩm."));
                        out.flush();
                        return;
                    }

                    GioHang item = new GioHang(
                            makhachhang,
                            masanpham,
                            soluong,
                            kichco,
                            sp.getGiasanpham()
                    );

                    if (ghDAO.activeThemGioHang(item)) {
                        out.print("{\"success\":true}");
                    } else {
                        out.print(jsonLoi("Lỗi hệ thống: Không thể lưu dữ liệu vào bảng giỏ hàng."));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    out.print(jsonLoi("Có lỗi xảy ra, vui lòng thử lại."));
                }

                out.flush();
            }

            return;
        }

        ArrayList<nguoidung> listsp = gomTheoTenSanPham(dao.sp());

        request.setAttribute("listsp", listsp);

        HttpSession sessionCart = request.getSession();
        String maKHCart = (String) sessionCart.getAttribute("email");
        int cartCountPost = (maKHCart == null || maKHCart.trim().isEmpty()) ? 0 : ghDAO.countCart(maKHCart);
        request.setAttribute("cartCount", cartCountPost);

        request.getRequestDispatcher("view/nguoidung.jsp")
                .forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}