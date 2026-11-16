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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import model.DanhGia;
import model.ThongKeDanhGia;
import model.nguoidung;
import modeldao.DanhGiaDAO;
import modeldao.GioHangDAO;
import modeldao.nguoidungdao;

/**
 * LUỒNG KHÁCH HÀNG - đánh giá & nhận xét sản phẩm.
 *
 *  GET  /danhgia?ma=...&sao=...          : về mục đánh giá ở trang chi tiết sản phẩm (ai cũng xem được)
 *  GET  /danhgia?action=form&ma=...      : form gửi đánh giá (cần đăng nhập + đã nhận hàng + chưa đánh giá)
 *  POST /danhgia  action=gui             : gửi đánh giá
 *  GET  /danhgia?action=cuatoi           : "Đánh giá của tôi" (gồm cả đánh giá đã bị ẩn)
 *
 * Mọi điều kiện đánh giá đều kiểm tra lại từ Database (DanhGiaDAO), không tin dữ liệu form.
 * Danh sách đánh giá hiển thị trong trang chi tiết sản phẩm được nạp bởi
 * {@link #napDanhGiaSanPham(HttpServletRequest, String, String)} (gọi từ ChiTietSanPhamServlet).
 */
@WebServlet(name = "DanhGiaServlet", urlPatterns = {"/danhgia"})
public class DanhGiaServlet extends HttpServlet {

    /** Khóa flash message lưu trong session (hiển thị 1 lần rồi xóa ở JSP). */
    private static final String FLASH_OK = "dgThongBao";
    private static final String FLASH_LOI = "dgLoi";

    private final DanhGiaDAO dao = new DanhGiaDAO();
    private final nguoidungdao spDao = new nguoidungdao();
    private final GioHangDAO ghDAO = new GioHangDAO();

    // =====================================================================
    // Hỗ trợ
    // =====================================================================
    /** Email khách hàng đang đăng nhập; null nếu chưa đăng nhập hoặc đang ở phiên quản trị. */
    static String emailKhach(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null
                || Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return null;
        }
        String email = (String) session.getAttribute("email");
        return (email == null || email.trim().isEmpty()) ? null : email.trim();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static void flash(HttpServletRequest request, String key, String msg) {
        request.getSession().setAttribute(key, msg);
    }

    /** Dữ liệu dùng chung cho header khách hàng: ô tìm kiếm gợi ý + badge giỏ hàng. */
    private void napHeader(HttpServletRequest request, String email) {
        LinkedHashMap<String, nguoidung> theoTen = new LinkedHashMap<>();
        for (nguoidung sp : spDao.sp()) {
            if (!theoTen.containsKey(sp.getTensanpham())) {
                theoTen.put(sp.getTensanpham(), sp);
            }
        }
        request.setAttribute("listsp", new ArrayList<>(theoTen.values()));
        request.setAttribute("cartCount", email == null ? 0 : ghDAO.countCart(email));
    }

    /** Đường dẫn tới mục đánh giá trong trang chi tiết sản phẩm. */
    private String linkSanPham(HttpServletRequest request, String maSanPham) {
        return request.getContextPath() + "/chitietsanpham?ma=" + enc(maSanPham) + "#danhgia";
    }

    /** Mã sản phẩm (size) dùng để mở trang chi tiết: ưu tiên size khách đang xem, không có thì size đại diện. */
    private String maSanPhamDeMoTrang(String maThamSo, DanhGia sp) {
        if (maThamSo != null && !maThamSo.trim().isEmpty() && dao.magocTuMaSanPham(maThamSo) != null) {
            return maThamSo.trim();
        }
        return sp == null ? null : sp.getMaSanPhamDaiDien();
    }

    /** Xác định mã gốc từ tham số: ưu tiên "magoc", nếu không có thì suy từ "ma" (mã size). */
    private String xacDinhMaGoc(HttpServletRequest request) {
        String magoc = trim(request.getParameter("magoc"));
        if (!magoc.isEmpty()) {
            return dao.laySanPhamTheoMaGoc(magoc) != null ? magoc : null;
        }
        return dao.magocTuMaSanPham(request.getParameter("ma"));
    }

    // =====================================================================
    // Nạp dữ liệu mục đánh giá cho trang chi tiết sản phẩm (ai cũng xem được)
    // =====================================================================
    /**
     * Gắn vào request mọi thứ mà danhGia.jsp cần: điểm trung bình, số lượt, phân bố sao,
     * danh sách đánh giá ĐANG HIỂN THỊ (có thể lọc theo số sao) và trạng thái nút "Viết đánh giá".
     *
     * @param maSanPham mã sản phẩm (size) đang xem ở trang chi tiết
     * @param saoLoc    tham số lọc sao (1..5) hoặc null/rỗng = tất cả
     */
    public static void napDanhGiaSanPham(HttpServletRequest request, String maSanPham, String saoLoc) {
        DanhGiaDAO d = new DanhGiaDAO();
        String magoc = d.magocTuMaSanPham(maSanPham);
        if (magoc == null) {
            return;
        }
        Integer sao = null;
        try {
            int v = Integer.parseInt(saoLoc == null ? "" : saoLoc.trim());
            if (v >= 1 && v <= 5) {
                sao = v;
            }
        } catch (NumberFormatException ignore) {
            // tham số lọc sai -> bỏ qua, hiện tất cả
        }

        ThongKeDanhGia tk = d.thongKeSanPham(magoc);
        request.setAttribute("dgMaGoc", magoc);
        request.setAttribute("dgMaSanPham", maSanPham);
        request.setAttribute("dgThongKe", tk);
        request.setAttribute("dgSaoLoc", sao);
        request.setAttribute("dgDanhSach", d.layDanhGiaSanPham(magoc, sao));

        // Trạng thái nút gửi đánh giá
        HttpSession session = request.getSession(false);
        boolean quanTri = session != null && Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        String email = emailKhach(request);
        String nut;
        String thongBao = null;
        if (quanTri) {
            nut = "an";                      // tài khoản quản trị không đánh giá
        } else if (email == null) {
            nut = "dangnhap";                // khách chưa đăng nhập -> yêu cầu đăng nhập
        } else {
            int dk = d.kiemTraDieuKienDanhGia(email, magoc);
            if (dk == DanhGiaDAO.DK_DU_DIEU_KIEN) {
                nut = "duoc";
            } else if (dk == DanhGiaDAO.DK_DA_DANH_GIA) {
                nut = "dadanhgia";
                thongBao = DanhGiaDAO.thongBaoDieuKien(dk);
            } else {
                nut = "khong";
                thongBao = DanhGiaDAO.thongBaoDieuKien(dk);
            }
        }
        request.setAttribute("dgNut", nut);
        request.setAttribute("dgThongBaoNut", thongBao);
    }

    // =====================================================================
    // GET
    // =====================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = trim(request.getParameter("action"));

        if ("cuatoi".equals(action)) {
            hienDanhGiaCuaToi(request, response);
        } else if ("form".equals(action)) {
            hienForm(request, response);
        } else {
            // Mặc định: về mục đánh giá ở trang chi tiết sản phẩm (giữ bộ lọc sao nếu có)
            String ma = trim(request.getParameter("ma"));
            if (ma.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
                return;
            }
            String sao = trim(request.getParameter("sao"));
            response.sendRedirect(request.getContextPath() + "/chitietsanpham?ma=" + enc(ma)
                    + (sao.isEmpty() ? "" : "&sao=" + enc(sao)) + "#danhgia");
        }
    }

    private void hienDanhGiaCuaToi(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = emailKhach(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }
        request.setAttribute("dsDanhGia", dao.layDanhGiaCuaToi(email));
        napHeader(request, email);
        request.getRequestDispatcher("/view/danhGiaCuaToi.jsp").forward(request, response);
    }

    private void hienForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = emailKhach(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }
        String magoc = xacDinhMaGoc(request);
        if (magoc == null) {
            flash(request, FLASH_LOI, DanhGiaDAO.thongBaoDieuKien(DanhGiaDAO.DK_SAN_PHAM_KHONG_TON_TAI));
            response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
            return;
        }
        DanhGia sp = dao.laySanPhamTheoMaGoc(magoc);
        int dk = dao.kiemTraDieuKienDanhGia(email, magoc);
        if (dk != DanhGiaDAO.DK_DU_DIEU_KIEN) {
            flash(request, FLASH_LOI, DanhGiaDAO.thongBaoDieuKien(dk));
            String ma = maSanPhamDeMoTrang(request.getParameter("ma"), sp);
            response.sendRedirect(ma == null ? request.getContextPath() + "/nguoidungservlet" : linkSanPham(request, ma));
            return;
        }
        request.setAttribute("dgSanPham", sp);
        request.setAttribute("dgMaSanPham", maSanPhamDeMoTrang(request.getParameter("ma"), sp));
        request.setAttribute("dgToiDaKyTu", DanhGiaDAO.TOI_DA_KY_TU_NOI_DUNG);
        napHeader(request, email);
        request.getRequestDispatcher("/view/formDanhGia.jsp").forward(request, response);
    }

    // =====================================================================
    // POST
    // =====================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!"gui".equals(trim(request.getParameter("action")))) {
            response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
            return;
        }
        guiDanhGia(request, response);
    }

    private void guiDanhGia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1) Danh tính CHỈ lấy từ session, không bao giờ từ form
        String email = emailKhach(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }
        String magoc = xacDinhMaGoc(request);
        if (magoc == null) {
            flash(request, FLASH_LOI, DanhGiaDAO.thongBaoDieuKien(DanhGiaDAO.DK_SAN_PHAM_KHONG_TON_TAI));
            response.sendRedirect(request.getContextPath() + "/nguoidungservlet");
            return;
        }
        DanhGia sp = dao.laySanPhamTheoMaGoc(magoc);
        String maSanPham = maSanPhamDeMoTrang(request.getParameter("ma"), sp);

        // 2) Kiểm tra dữ liệu gửi lên
        String saoStr = trim(request.getParameter("sosao"));
        String noiDung = trim(request.getParameter("noidung"));
        String loi = null;
        Integer sao = null;
        if (!saoStr.isEmpty()) {
            try {
                sao = Integer.valueOf(saoStr);
                if (sao < 1 || sao > 5) {
                    loi = "Số sao chỉ được nằm trong khoảng từ 1 đến 5.";
                }
            } catch (NumberFormatException e) {
                loi = "Số sao không hợp lệ.";
            }
        }
        if (loi == null && sao == null && noiDung.isEmpty()) {
            loi = "Vui lòng chọn số sao hoặc viết nhận xét (ít nhất một trong hai).";
        }
        if (loi == null && noiDung.length() > DanhGiaDAO.TOI_DA_KY_TU_NOI_DUNG) {
            loi = "Nhận xét tối đa " + DanhGiaDAO.TOI_DA_KY_TU_NOI_DUNG + " ký tự.";
        }
        if (loi != null) {
            hienFormLoi(request, response, email, sp, maSanPham, loi, saoStr, noiDung);
            return;
        }

        // 3) Thêm đánh giá (DAO kiểm tra lại điều kiện mua/giao hàng ngay trong câu INSERT)
        DanhGia dg = new DanhGia(email, magoc, sao, noiDung.isEmpty() ? null : noiDung);
        int kq = dao.themDanhGia(dg);
        switch (kq) {
            case DanhGiaDAO.THEM_OK:
                flash(request, FLASH_OK, "Cảm ơn bạn! Đánh giá của bạn đã được gửi thành công.");
                response.sendRedirect(request.getContextPath() + "/danhgia?action=cuatoi");
                return;
            case DanhGiaDAO.THEM_TRUNG:
                flash(request, FLASH_LOI, DanhGiaDAO.thongBaoDieuKien(DanhGiaDAO.DK_DA_DANH_GIA));
                response.sendRedirect(request.getContextPath() + "/danhgia?action=cuatoi");
                return;
            case DanhGiaDAO.THEM_KHONG_DU_DIEU_KIEN:
                // Chưa mua / chưa nhận hàng: lấy đúng lý do từ DB để báo cho khách
                flash(request, FLASH_LOI, DanhGiaDAO.thongBaoDieuKien(dao.kiemTraDieuKienDanhGia(email, magoc)));
                response.sendRedirect(maSanPham == null ? request.getContextPath() + "/nguoidungservlet" : linkSanPham(request, maSanPham));
                return;
            case DanhGiaDAO.THEM_KHONG_HOP_LE:
                hienFormLoi(request, response, email, sp, maSanPham,
                        "Dữ liệu đánh giá không hợp lệ, vui lòng kiểm tra lại.", saoStr, noiDung);
                return;
            default:
                hienFormLoi(request, response, email, sp, maSanPham,
                        "Hệ thống đang gặp lỗi, vui lòng thử lại sau.", saoStr, noiDung);
        }
    }

    /** Hiện lại form kèm thông báo lỗi và giữ nguyên nội dung khách đã nhập. */
    private void hienFormLoi(HttpServletRequest request, HttpServletResponse response, String email,
            DanhGia sp, String maSanPham, String loi, String saoStr, String noiDung)
            throws ServletException, IOException {
        request.setAttribute("dgSanPham", sp);
        request.setAttribute("dgMaSanPham", maSanPham);
        request.setAttribute("dgToiDaKyTu", DanhGiaDAO.TOI_DA_KY_TU_NOI_DUNG);
        request.setAttribute("dgLoiForm", loi);
        request.setAttribute("dgSaoDaChon", saoStr);
        request.setAttribute("dgNoiDungDaNhap", noiDung);
        napHeader(request, email);
        request.getRequestDispatcher("/view/formDanhGia.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet đánh giá và nhận xét sản phẩm (khách hàng)";
    }
}
