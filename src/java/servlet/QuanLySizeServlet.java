package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import model.nguoiban;
import model.nhomsanpham;
import modeldao.nguoibandao;
import modeldao.quanlykhohangdao;
import util.AdminGuard;

// Trang "Quản lý size": xem chi tiết TỪNG size (số lượng, giá riêng...) của
// 1 sản phẩm đã được gộp bên trang Quản lý sản phẩm. Truy cập bằng
// quanlysizeservlet?magoc=<mã gốc>, ví dụ quanlysizeservlet?magoc=mu1.
@WebServlet(name = "QuanLySizeServlet", urlPatterns = {"/quanlysizeservlet"})
public class QuanLySizeServlet extends HttpServlet {

    nguoibandao dao = new nguoibandao();
    quanlykhohangdao khoDao = new quanlykhohangdao();

    private static final String[] TAT_CA_SIZE = {"S", "M", "L", "XL", "XXL", "XXXL"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        String magoc = request.getParameter("magoc");

        if (magoc == null || magoc.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Vui lòng chọn 1 sản phẩm để xem size!");
            response.sendRedirect("nguoibanservlet");
            return;
        }

        nhomsanpham dt = dao.layNhomTheoMaGoc(magoc);
        ArrayList<nguoiban> dsSize = dao.layTheoMaGoc(magoc);

        if (dt == null || dsSize.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Không tìm thấy sản phẩm với mã \"" + magoc + "\"!");
            response.sendRedirect("nguoibanservlet");
            return;
        }

        request.setAttribute("magoc", magoc);
        request.setAttribute("daidien", dt);
        request.setAttribute("dssize", dsSize);
        request.setAttribute("tatcasize", TAT_CA_SIZE);
        request.getRequestDispatcher("/view/quanlysize.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        String magoc = request.getParameter("magoc");

        // FIX: thiếu mã gốc thì không có sản phẩm nào để thao tác (trước đây
        // redirect sang "quanlysizeservlet?magoc=null").
        if (magoc == null || magoc.trim().isEmpty()) {
            session.setAttribute("error", "Thiếu mã sản phẩm!");
            response.sendRedirect("nguoibanservlet");
            return;
        }

        try {
            if ("add".equals(action)) {
                // Thêm 1 size mới cho sản phẩm đang xem (thừa hưởng tên/ảnh/giá/danh
                // mục/trạng thái/ngày bán từ dòng đại diện, chỉ cần chọn size + số lượng).
                String kichco = request.getParameter("kichco");
                String soluongStr = request.getParameter("soluong");
                int soluong = 0;
                if (soluongStr != null && !soluongStr.trim().isEmpty()) {
                    try {
                        soluong = Integer.parseInt(soluongStr.trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (soluong < 0) {
                    soluong = 0;
                }

                if (kichco == null || kichco.trim().isEmpty()) {
                    session.setAttribute("error", "Vui lòng chọn size cần thêm!");
                } else {
                    nhomsanpham dt = dao.layNhomTheoMaGoc(magoc);

                    String maMoi = magoc + "-" + kichco;
                    if (dao.check(maMoi)) {
                        session.setAttribute("error", "Size \"" + kichco + "\" đã tồn tại cho sản phẩm này!");
                    } else if (dt != null) {
                        nguoiban spMoi = new nguoiban(maMoi, magoc, dt.getTensanpham(), dt.getHinhanh(), soluong,
                                kichco, dt.getGiasanpham(), dt.getNgayban(), dt.getTrangthai(), dt.getDanhmuc());
                        dao.them(spMoi);
                        khoDao.themHoacCongDon(maMoi, soluong, "Người bán");
                        session.setAttribute("success", "Thêm size \"" + kichco + "\" thành công!");
                    }
                }
            } else if ("update".equals(action)) {
                // Sửa số lượng / giá riêng của 1 size cụ thể (không đụng đến size khác).
                String masanpham = request.getParameter("masanpham");
                String soluongStr = request.getParameter("soluong");
                String giaStr = request.getParameter("giasanpham");

                nguoiban spCu = dao.layTheoMa(masanpham);
                if (spCu == null) {
                    session.setAttribute("error", "Không tìm thấy size cần sửa!");
                } else {
                    int soluongMoi = spCu.getSoluongsanpham();
                    if (soluongStr != null && !soluongStr.trim().isEmpty()) {
                        try {
                            soluongMoi = Integer.parseInt(soluongStr.trim());
                            if (soluongMoi < 0) {
                                soluongMoi = 0;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    int giaMoi = spCu.getGiasanpham();
                    if (giaStr != null && !giaStr.trim().isEmpty()) {
                        try {
                            giaMoi = Integer.parseInt(giaStr.replaceAll("\\D", ""));
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    nguoiban spCapNhat = new nguoiban(masanpham, spCu.getMagoc(), spCu.getTensanpham(), spCu.getHinhanh(),
                            soluongMoi, spCu.getKichco(), giaMoi, spCu.getNgayban(), spCu.getTrangthai(), spCu.getDanhmuc());
                    dao.sua(spCapNhat);

                    // Chỉ đồng bộ Kho hàng khi số lượng TĂNG (coi như 1 đợt nhập hàng mới).
                    int chenhLech = soluongMoi - spCu.getSoluongsanpham();
                    if (chenhLech > 0) {
                        khoDao.themHoacCongDon(masanpham, chenhLech, "Người bán");
                    }
                    session.setAttribute("success", "Cập nhật size \"" + spCu.getKichco() + "\" thành công!");
                }
            } else if ("delete".equals(action)) {
                // Xoá 1 size riêng lẻ. Nếu đây là size CUỐI CÙNG của sản phẩm thì
                // xoá xong sẽ không còn dòng nào để gộp -> quay lại trang danh sách.
                String masanpham = request.getParameter("masanpham");
                dao.xoa(masanpham);
                session.setAttribute("success", "Xoá size thành công!");

                ArrayList<nguoiban> conLai = dao.layTheoMaGoc(magoc);
                if (conLai.isEmpty()) {
                    response.sendRedirect("nguoibanservlet");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại.");
        }

        // FIX: mã hoá magoc (có thể chứa khoảng trắng/ký tự tiếng Việt/&...) khi đưa lên URL.
        response.sendRedirect("quanlysizeservlet?magoc=" + java.net.URLEncoder.encode(magoc, "UTF-8"));
    }
}
