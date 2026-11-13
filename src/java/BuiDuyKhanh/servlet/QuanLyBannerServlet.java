package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.regex.Pattern;
import model.Banner;
import modeldao.BannerDAO;
import util.AdminGuard;
import util.ImageStorage;

/**
 * Trang admin: quản lý banner trang chủ (TRANGCHU) và banner trang sản phẩm (SANPHAM).
 * Thêm / sửa / ẩn-hiện / xóa banner, upload ảnh qua ImageStorage (lưu bền vững, không mất khi deploy lại).
 * Chỉ vai trò ADMIN được vào.
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB / ảnh
        maxRequestSize = 1024 * 1024 * 12
)
@WebServlet(name = "QuanLyBannerServlet", urlPatterns = {"/quanlybanner"})
public class QuanLyBannerServlet extends HttpServlet {

    private final BannerDAO dao = new BannerDAO();

    // Liên kết nội bộ: chỉ chữ, số và các ký tự đường dẫn/query thông thường (không có ":" nên không thể là javascript:...)
    private static final Pattern LIEN_KET_NOI_BO = Pattern.compile("^[A-Za-z0-9_\\-./?=&%#]+$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }

        String vitri = request.getParameter("vitri");
        if (!Banner.TRANG_CHU.equals(vitri) && !Banner.SAN_PHAM.equals(vitri)) {
            vitri = "";
        }

        if ("edit".equals(request.getParameter("action"))) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("banSua", dao.getBanner(id));
            } catch (NumberFormatException e) {
                // bỏ qua: hiện form thêm mới
            }
        }

        request.setAttribute("dsBanner", dao.getDanhSach(vitri));
        request.setAttribute("locVitri", vitri);
        request.getRequestDispatcher("/view/quanlybanner.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminGuard.kiemTraAdmin(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String quayLai = "";

        try {
            switch (action == null ? "" : action) {

                case "them":
                case "sua": {
                    boolean laSua = "sua".equals(action);
                    Banner b = new Banner();

                    String vitri = request.getParameter("vitri");
                    if (!Banner.TRANG_CHU.equals(vitri) && !Banner.SAN_PHAM.equals(vitri)) {
                        session.setAttribute("error", "Vị trí banner không hợp lệ.");
                        break;
                    }
                    b.setVitri(vitri);
                    b.setNhan(cat(request.getParameter("nhan"), 100));
                    b.setTieude(cat(request.getParameter("tieude"), 150));
                    b.setMota(cat(request.getParameter("mota"), 300));
                    b.setNuttext(cat(request.getParameter("nuttext"), 50));
                    b.setHienthi(request.getParameter("hienthi") != null);

                    String thutu = request.getParameter("thutu");
                    b.setThutu(thutu == null || thutu.trim().isEmpty() ? 0 : Integer.parseInt(thutu.trim()));

                    String lienket = cat(request.getParameter("lienket"), 300);
                    if (lienket != null && !lienKetHopLe(lienket)) {
                        session.setAttribute("error", "Liên kết không hợp lệ. Nhập đường dẫn nội bộ (VD: nguoidungservlet) hoặc địa chỉ bắt đầu bằng http:// hoặc https://.");
                        break;
                    }
                    if (lienket != null && !lienket.startsWith("http")) {
                        lienket = lienket.replaceFirst("^/+", "");   // link nội bộ: JSP tự ghép contextPath + "/"
                    }
                    b.setLienket(lienket);

                    Part partAnh = request.getPart("hinhanh");
                    boolean coAnh = partAnh != null && partAnh.getSize() > 0;

                    if (laSua) {
                        b.setId(Integer.parseInt(request.getParameter("id")));
                        quayLai = "?action=edit&id=" + b.getId();
                        if (coAnh) {
                            b.setHinhanh(ImageStorage.luuAnh(partAnh, "banner_"));
                        }
                        boolean ok = dao.sua(b);
                        session.setAttribute(ok ? "success" : "error",
                                ok ? "Đã cập nhật banner." : "Không cập nhật được banner (không tìm thấy hoặc lỗi cơ sở dữ liệu).");
                        if (ok) {
                            quayLai = "";
                        }
                    } else {
                        if (!coAnh) {
                            session.setAttribute("error", "Vui lòng chọn ảnh cho banner.");
                            break;
                        }
                        b.setHinhanh(ImageStorage.luuAnh(partAnh, "banner_"));
                        boolean ok = dao.them(b);
                        session.setAttribute(ok ? "success" : "error",
                                ok ? "Đã thêm banner mới." : "Thêm banner thất bại. Kiểm tra đã chạy migration_banner.sql chưa.");
                    }
                    break;
                }

                case "an":
                case "hien": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    boolean hien = "hien".equals(action);
                    boolean ok = dao.capNhatHienThi(id, hien);
                    session.setAttribute(ok ? "success" : "error",
                            ok ? (hien ? "Đã hiện banner." : "Đã ẩn banner.") : "Không tìm thấy banner.");
                    break;
                }

                case "xoa": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    boolean ok = dao.xoa(id);
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Đã xóa banner." : "Không tìm thấy banner.");
                    break;
                }

                default:
                    break;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu nhập vào không hợp lệ.");
        } catch (IllegalArgumentException e) {
            // VD: file tải lên không phải ảnh -> thông báo đã viết sẵn trong ImageStorage
            session.setAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            // Vượt quá dung lượng cho phép của @MultipartConfig
            session.setAttribute("error", "Ảnh quá lớn (tối đa 10MB).");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu.");
        }

        response.sendRedirect(request.getContextPath() + "/quanlybanner" + quayLai);
    }

    private static String cat(String s, int max) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.isEmpty()) {
            return null;
        }
        return s.length() > max ? s.substring(0, max) : s;
    }

    private static boolean lienKetHopLe(String s) {
        if (s.startsWith("http://") || s.startsWith("https://")) {
            return !s.contains(" ") && !s.contains("\"") && !s.contains("<") && !s.contains(">");
        }
        return LIEN_KET_NOI_BO.matcher(s).matches() && !s.startsWith("//");
    }
}
