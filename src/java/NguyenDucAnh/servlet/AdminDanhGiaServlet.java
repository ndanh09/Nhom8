package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import model.DanhGia;
import modeldao.DanhGiaDAO;
import util.AdminGuard;

/**
 * LUỒNG QUẢN TRỊ (vai trò ADMIN và QUANLY) - quản lý đánh giá & nhận xét sản phẩm.
 *
 *  GET  /quanlydanhgia                 : danh sách + thống kê + tìm kiếm / lọc / sắp xếp / phân trang
 *  GET  /quanlydanhgia?action=chitiet&id=... : xem chi tiết 1 đánh giá
 *  POST /quanlydanhgia action=an|hien  : ẩn / hiện lại 1 đánh giá
 *
 * Admin CHỈ được ẩn/hiện, không sửa nội dung, không tạo hộ, không xóa đánh giá.
 * Quyền ADMIN/QUANLY được kiểm tra ở phía server cho MỌI request (cả GET lẫn POST).
 */
@WebServlet(name = "AdminDanhGiaServlet", urlPatterns = {"/quanlydanhgia"})
public class AdminDanhGiaServlet extends HttpServlet {

    private static final int KICH_THUOC_TRANG = DanhGiaDAO.KICH_THUOC_TRANG_MAC_DINH;   // 10 đánh giá / trang

    private final DanhGiaDAO dao = new DanhGiaDAO();

    /**
     * Cho phép ADMIN và QUANLY (quản lý sản phẩm) vào khu quản lý đánh giá. Chưa đăng nhập -> trang
     * đăng nhập; vai trò khác (DOANHTHU) -> về trang chủ của vai trò đó. Kiểm tra ở phía server.
     */
    private static boolean kiemTraQuyen(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (AdminGuard.laAdmin(request) || AdminGuard.laQuanLy(request)) {
            return true;
        }
        HttpSession session = request.getSession(false);
        Object vt = session == null ? null : session.getAttribute(AdminGuard.VAI_TRO);
        String dich = AdminGuard.isAdmin(request) && vt != null
                ? AdminGuard.trangChu((String) vt) : "/view/dangnhap.jsp";
        response.sendRedirect(request.getContextPath() + dich);
        return false;
    }

    // =====================================================================
    // Đọc & chuẩn hóa tham số lọc (chỉ chấp nhận giá trị trong danh sách cho phép)
    // =====================================================================
    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    /** "1".."5" -> số sao; còn lại -> "" (không lọc). */
    private static String chuanHoaSao(String s) {
        s = trim(s);
        return s.matches("[1-5]") ? s : "";
    }

    /** "hien" | "an" | "" (tất cả). */
    private static String chuanHoaTrangThai(String s) {
        s = trim(s);
        return ("hien".equals(s) || "an".equals(s)) ? s : "";
    }

    /** "moi" (mặc định) | "cu". */
    private static String chuanHoaSapXep(String s) {
        return "cu".equals(trim(s)) ? "cu" : "moi";
    }

    private static int soTrang(String s) {
        try {
            return Math.max(1, Integer.parseInt(trim(s)));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /** Chuỗi query giữ nguyên các điều kiện lọc (KHÔNG có trang). Dùng cho link phân trang. */
    private static String queryLoc(String khach, String sanpham, String sao, String trangthai, String sapxep) {
        return "khach=" + enc(khach) + "&sanpham=" + enc(sanpham) + "&sao=" + sao
                + "&trangthai=" + trangthai + "&sapxep=" + sapxep;
    }

    // =====================================================================
    // GET
    // =====================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!kiemTraQuyen(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        if ("chitiet".equals(trim(request.getParameter("action")))) {
            hienChiTiet(request, response);
            return;
        }

        String khach = trim(request.getParameter("khach"));
        String sanpham = trim(request.getParameter("sanpham"));
        String sao = chuanHoaSao(request.getParameter("sao"));
        String trangthai = chuanHoaTrangThai(request.getParameter("trangthai"));
        String sapxep = chuanHoaSapXep(request.getParameter("sapxep"));
        int trang = soTrang(request.getParameter("trang"));

        DanhGiaDAO.BoLoc loc = new DanhGiaDAO.BoLoc()
                .setKhachHang(khach)
                .setSanPham(sanpham)
                .setSoSao(sao.isEmpty() ? null : Integer.valueOf(sao))
                .setHienThi(trangthai.isEmpty() ? null : Boolean.valueOf("hien".equals(trangthai)))
                .setMoiNhat("moi".equals(sapxep));

        // Phân trang trên TẬP KẾT QUẢ ĐÃ LỌC
        int tongKetQua = dao.demTongDanhGia(loc);
        int tongTrang = Math.max(1, (int) Math.ceil(tongKetQua / (double) KICH_THUOC_TRANG));
        if (trang > tongTrang) {
            trang = tongTrang;
        }

        request.setAttribute("dsDanhGia", dao.timKiemDanhGia(loc, trang, KICH_THUOC_TRANG));
        request.setAttribute("thongKe", dao.thongKeDanhGia());
        request.setAttribute("tongKetQua", tongKetQua);
        request.setAttribute("trang", trang);
        request.setAttribute("tongTrang", tongTrang);
        request.setAttribute("khach", khach);
        request.setAttribute("sanpham", sanpham);
        request.setAttribute("sao", sao);
        request.setAttribute("trangthai", trangthai);
        request.setAttribute("sapxep", sapxep);
        request.setAttribute("queryLoc", queryLoc(khach, sanpham, sao, trangthai, sapxep));
        String queryTrang = queryLoc(khach, sanpham, sao, trangthai, sapxep) + "&trang=" + trang;
        request.setAttribute("queryTrang", queryTrang);            // dùng cho input hidden (POST)
        request.setAttribute("queryTrangEnc", enc(queryTrang));    // dùng làm giá trị tham số trên link

        request.getRequestDispatcher("/view/quanLyDanhGia.jsp").forward(request, response);
    }

    private void hienChiTiet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DanhGia dg = null;
        try {
            dg = dao.layDanhGiaTheoMa(Integer.parseInt(trim(request.getParameter("id"))));
        } catch (NumberFormatException ignore) {
            // id sai -> coi như không tìm thấy
        }
        if (dg == null) {
            request.getSession().setAttribute("error", "Không tìm thấy đánh giá này!");
            response.sendRedirect(request.getContextPath() + "/quanlydanhgia");
            return;
        }
        request.setAttribute("dg", dg);
        // Nút "Quay lại danh sách" giữ đúng điều kiện lọc / trang đang xem
        String quayLai = trim(request.getParameter("quaylai"));
        request.setAttribute("queryQuayLai", quayLai.matches("[A-Za-z0-9%&=._~+-]*") ? quayLai : "");
        request.getRequestDispatcher("/view/chiTietDanhGia.jsp").forward(request, response);
    }

    // =====================================================================
    // POST: ẩn / hiện
    // =====================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra quyền Admin ở phía server trước khi đổi bất cứ dữ liệu nào
        if (!kiemTraQuyen(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String action = trim(request.getParameter("action"));
        int id = -1;
        try {
            id = Integer.parseInt(trim(request.getParameter("id")));
        } catch (NumberFormatException ignore) {
            // để id = -1 -> báo lỗi bên dưới
        }

        if (id > 0 && ("an".equals(action) || "hien".equals(action))) {
            boolean hien = "hien".equals(action);
            if (dao.capNhatTrangThai(id, hien)) {
                request.getSession().setAttribute("success",
                        hien ? "Đã hiện lại đánh giá #" + id + "." : "Đã ẩn đánh giá #" + id + ".");
            } else {
                request.getSession().setAttribute("error", "Không cập nhật được đánh giá #" + id + "!");
            }
        } else {
            request.getSession().setAttribute("error", "Yêu cầu không hợp lệ!");
        }

        // Quay lại đúng nơi vừa thao tác, giữ nguyên điều kiện lọc và trang
        String quayLai = trim(request.getParameter("quaylai"));
        if (!quayLai.matches("[A-Za-z0-9%&=._~+-]*")) {
            quayLai = "";
        }
        String ve = request.getContextPath() + "/quanlydanhgia";
        if ("chitiet".equals(trim(request.getParameter("ve"))) && id > 0) {
            ve += "?action=chitiet&id=" + id + (quayLai.isEmpty() ? "" : "&quaylai=" + enc(quayLai));
        } else if (!quayLai.isEmpty()) {
            ve += "?" + quayLai;
        }
        response.sendRedirect(ve);
    }

    @Override
    public String getServletInfo() {
        return "Servlet quản lý đánh giá và nhận xét sản phẩm (quản trị viên)";
    }
}
