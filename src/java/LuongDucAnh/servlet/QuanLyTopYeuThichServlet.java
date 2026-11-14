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
import model.SanPhamYeuThich;
import modeldao.YeuThichDAO;
import util.AdminGuard;

/**
 * LUỒNG SẢN PHẨM (vai trò QUANLY, cùng nhóm với Quản lý sản phẩm / kho / mã giảm giá):
 * quản lý danh sách TOP sản phẩm (áo) yêu thích.
 *
 *  - Xem bảng xếp hạng theo số lượt yêu thích (lọc theo tên / mã / danh mục)
 *  - Ghim sản phẩm lên đầu Top + đổi thứ tự ghim
 *  - Ẩn / hiện sản phẩm khỏi mục Top ở trang chủ
 *  - Đưa về xếp hạng tự động
 * Phần xem/xóa yêu thích THEO TÀI KHOẢN nằm ở luồng quản trị viên (QuanLyYeuThichTaiKhoanServlet).
 */
@WebServlet(name = "QuanLyTopYeuThichServlet", urlPatterns = {"/quanlytopyeuthich"})
public class QuanLyTopYeuThichServlet extends HttpServlet {

    private final YeuThichDAO dao = new YeuThichDAO();

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }
        String keyword = request.getParameter("keyword");
        String danhmuc = request.getParameter("danhmuc");

        request.setAttribute("dsTop", dao.layBangQuanLy(keyword, danhmuc));
        request.setAttribute("dsUngVien", dao.layUngVienGhim());
        request.setAttribute("tongLuot", dao.tongLuot());
        request.setAttribute("soTaiKhoan", dao.soTaiKhoanCoYeuThich());
        request.setAttribute("topHienThi", dao.layTopHienThiSo());
        request.setAttribute("topToiDa", YeuThichDAO.TOP_TOI_DA);
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("danhmucChon", danhmuc == null ? "" : danhmuc);
        request.getRequestDispatcher("/view/quanlytopyeuthich.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String magoc = request.getParameter("magoc");

        try {
            if ("capnhatso".equals(action)) {
                // Chỉnh số áo hiển thị ở mục Top trang chủ (không cần mã sản phẩm).
                int so;
                try {
                    so = Integer.parseInt(request.getParameter("so") == null ? "" : request.getParameter("so").trim());
                } catch (NumberFormatException e) {
                    so = -1;
                }
                if (so < 1 || so > YeuThichDAO.TOP_TOI_DA) {
                    session.setAttribute("error", "Số áo hiển thị phải từ 1 đến " + YeuThichDAO.TOP_TOI_DA + "!");
                } else if (dao.datTopHienThiSo(so)) {
                    session.setAttribute("success", "Mục Top ở trang chủ sẽ hiển thị " + so + " áo.");
                } else {
                    session.setAttribute("error", "Không lưu được số áo hiển thị. Hãy kiểm tra đã chạy migration_yeuthich.sql chưa.");
                }
            } else if (action == null || magoc == null || !dao.tonTaiMagoc(magoc)) {
                session.setAttribute("error", "Sản phẩm không tồn tại hoặc yêu cầu không hợp lệ!");
            } else {
                magoc = magoc.trim();
                switch (action) {
                    case "ghim": {
                        int thutu = docThuTu(request.getParameter("thutu"));
                        if (dao.ghim(magoc, thutu)) {
                            session.setAttribute("success", "Đã ghim sản phẩm lên Top (thứ tự " + thutu + ")!");
                        } else {
                            session.setAttribute("error", "Không ghim được sản phẩm. Hãy kiểm tra đã chạy migration_yeuthich.sql chưa.");
                        }
                        break;
                    }
                    case "boghim":
                        if (dao.boGhim(magoc)) {
                            session.setAttribute("success", "Đã bỏ ghim - sản phẩm quay về xếp hạng theo lượt yêu thích.");
                        } else {
                            session.setAttribute("error", "Không bỏ ghim được sản phẩm này!");
                        }
                        break;
                    case "datthutu": {
                        int thutu = docThuTu(request.getParameter("thutu"));
                        if (dao.datThuTu(magoc, thutu)) {
                            session.setAttribute("success", "Đã đổi thứ tự ghim thành " + thutu + "!");
                        } else {
                            session.setAttribute("error", "Không đổi được thứ tự!");
                        }
                        break;
                    }
                    case "an":
                        if (dao.datAn(magoc, true)) {
                            session.setAttribute("success", "Đã ẩn sản phẩm khỏi mục Top ở trang chủ.");
                        } else {
                            session.setAttribute("error", "Không ẩn được sản phẩm này!");
                        }
                        break;
                    case "hien":
                        if (dao.datAn(magoc, false)) {
                            session.setAttribute("success", "Đã hiện lại sản phẩm trong xếp hạng Top.");
                        } else {
                            session.setAttribute("error", "Không hiện lại được sản phẩm này!");
                        }
                        break;
                    case "datlai":
                        if (dao.datLai(magoc)) {
                            session.setAttribute("success", "Đã đưa sản phẩm về xếp hạng tự động.");
                        } else {
                            session.setAttribute("error", "Không đặt lại được sản phẩm này!");
                        }
                        break;
                    default:
                        session.setAttribute("error", "Thao tác không hợp lệ!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại.");
        }

        // Giữ nguyên bộ lọc đang xem sau khi thao tác.
        String keyword = request.getParameter("keyword");
        String danhmuc = request.getParameter("danhmuc");
        response.sendRedirect(request.getContextPath() + "/quanlytopyeuthich?keyword=" + enc(keyword)
                + "&danhmuc=" + enc(danhmuc));
    }

    /** Thứ tự ghim: số nguyên 1..999 (mặc định 1). */
    private static int docThuTu(String s) {
        try {
            int v = Integer.parseInt(s == null ? "" : s.trim());
            return Math.max(1, Math.min(999, v));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    @Override
    public String getServletInfo() {
        return "Quản lý Top sản phẩm yêu thích - luồng sản phẩm (QUANLY)";
    }
}
