package servlet;

import java.io.IOException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.quanlykhohang;
import modeldao.quanlykhohangdao;
import modeldao.nguoibandao;
import util.AdminGuard;

@WebServlet("/quanlykhohangservlet")
public class quanlykhohangservlet extends HttpServlet {

    quanlykhohangdao dao = new quanlykhohangdao();
    // Dùng để đồng bộ ngược: khi nhập thêm hàng ở trang Kho hàng, số lượng
    // riêng theo từng size bên bảng nguoiban (Quản lý size/Chi tiết sản
    // phẩm) cũng phải tăng theo, không thì kho tổng và kho theo size sẽ
    // lệch nhau (kho tổng tăng nhưng từng size vẫn đứng yên).
    nguoibandao nbDao = new nguoibandao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        // FIX: action lạ trước đây không rơi vào nhánh nào -> trả về trang trắng.
        if (action != null && !action.equals("search") && !action.equals("delete") && !action.equals("edit")) {
            action = null;
        }

        if (action == null) {

            ArrayList<quanlykhohang> list = dao.getAll();

            request.setAttribute("listKho", list);

            request.getRequestDispatcher("view/quanlykhohang.jsp").forward(request, response);

        } else if (action.equals("search")) {

            String keyword = request.getParameter("keyword");

            ArrayList<quanlykhohang> list;
            if (keyword == null || keyword.trim().isEmpty()) {
                list = dao.getAll();
            } else {
                list = dao.search(keyword.trim());
            }

            request.setAttribute("listKho", list);
            request.setAttribute("keyword", keyword);

            request.getRequestDispatcher("view/quanlykhohang.jsp").forward(request, response);

        } else if (action.equals("delete")) {

            HttpSession session = request.getSession();

            try {
                int id = Integer.parseInt(request.getParameter("id"));

                quanlykhohang kho = dao.getByID(id);
                if (kho == null) {
                    session.setAttribute("error", "Không tìm thấy dòng kho cần xoá!");
                } else {
                    boolean ok = dao.delete(id);
                    session.setAttribute(ok ? "success" : "error",
                            ok ? "Xoá dòng kho \"" + kho.getMasanpham() + "\" thành công!"
                               : "Xoá thất bại, vui lòng thử lại.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Mã dòng kho không hợp lệ!");
            }

            response.sendRedirect("quanlykhohangservlet");

        } else if (action.equals("edit")) {

            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendRedirect("quanlykhohangservlet");
                return;
            }

            quanlykhohang kho = dao.getByID(id);

            request.setAttribute("kho", kho);

            ArrayList<quanlykhohang> list = dao.getAll();
            request.setAttribute("listKho", list);

            request.getRequestDispatcher("view/quanlykhohang.jsp").forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        // Trang Quản lý kho hàng KHÔNG còn chức năng thêm: chỉ cập nhật (nhập thêm) /
        // cập nhật hàng loạt dòng kho đã có. Action "add" hoặc thiếu action bị bỏ qua.
        if (action == null) {

            // không làm gì, quay về danh sách

        } else if (action.equals("update")) {

            try {
                int id = Integer.parseInt(request.getParameter("makho"));

                quanlykhohang hienTai = dao.getByID(id);

                if (hienTai == null) {
                    session.setAttribute("error", "Không tìm thấy dòng kho cần cập nhật!");
                    response.sendRedirect("quanlykhohangservlet");
                    return;
                }

                String masp = request.getParameter("masanpham");

                // SỬA: "soluongnhap" trong form Cập nhật giờ là SỐ LƯỢNG NHẬP THÊM của đợt
                // này (không phải tổng mới), để tránh admin phải tự cộng tay số cũ + số mới.
                String slnThemStr = request.getParameter("soluongnhap");
                int slnThem = 0;
                if (slnThemStr != null && !slnThemStr.trim().isEmpty()) {
                    slnThem = Integer.parseInt(slnThemStr.trim());
                }
                // Không cho phép nhập thêm số lượng âm
                if (slnThem < 0) {
                    slnThem = 0;
                }

                String vitri = request.getParameter("vitri");

                String ncc = request.getParameter("nhacungcap");

                // Số lượng nhập thêm được tự động cộng vào cả tổng SL Nhập lẫn SL Tồn,
                // thay vì phải ghi đè thủ công.
                int tongSoLuongNhap = hienTai.getSoluongnhap() + slnThem;
                int tongSoLuongTon = hienTai.getSoluongton() + slnThem;

                quanlykhohang k = new quanlykhohang();

                k.setMakho(id);
                k.setMasanpham(masp);
                k.setSoluongnhap(tongSoLuongNhap);
                k.setSoluongton(tongSoLuongTon);
                k.setVitri(vitri);
                k.setNhacungcap(ncc);

                boolean ok = dao.update(k);

                if (ok) {
                    // FIX: nhập thêm hàng ở đây (khohang) trước đây chỉ tăng SL Nhập/SL Tồn
                    // TỔNG bên bảng khohang, không đụng tới số lượng riêng của size đó bên
                    // bảng nguoiban (nơi trang Quản lý size/Chi tiết sản phẩm đọc dữ liệu).
                    // Kết quả: kho tổng thì nhảy lên (VD 50 + 15 = 65) nhưng số lượng hiển
                    // thị theo từng size vẫn đứng yên (vẫn 50). Nay cộng thêm đúng số lượng
                    // slnThem vào nguoiban theo masanpham (đã gồm size, vd "mu1-M") để 2 bên
                    // luôn khớp nhau.
                    nbDao.congSoLuong(masp, slnThem);

                    session.setAttribute("success", slnThem > 0
                            ? "Nhập thêm " + slnThem + " sản phẩm \"" + masp + "\" thành công!"
                            : "Cập nhật dòng kho \"" + masp + "\" thành công!");
                } else {
                    session.setAttribute("error", "Cập nhật thất bại, vui lòng thử lại.");
                }

            } catch (NumberFormatException e) {
                session.setAttribute("error", "Số lượng nhập thêm phải là số hợp lệ!");
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại.");
            }

        } else if (action.equals("batchupdate")) {

            String[] ids = request.getParameterValues("ids");
            String vitri = request.getParameter("vitri");
            String ncc = request.getParameter("nhacungcap");

            if (ids == null || ids.length == 0) {

                session.setAttribute("error", "Vui lòng chọn ít nhất 1 dòng để cập nhật!");

            } else if ((vitri == null || vitri.trim().isEmpty())
                    && (ncc == null || ncc.trim().isEmpty())) {

                session.setAttribute("error", "Vui lòng nhập Vị trí hoặc Nhà cung cấp cần cập nhật!");

            } else {

                int soDongThanhCong = 0;

                for (String idStr : ids) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        if (dao.updateViTriNhaCungCap(id, vitri, ncc)) {
                            soDongThanhCong++;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (soDongThanhCong > 0) {
                    session.setAttribute("success", "Đã cập nhật hàng loạt cho " + soDongThanhCong + " dòng kho!");
                } else {
                    session.setAttribute("error", "Cập nhật hàng loạt thất bại, vui lòng thử lại.");
                }
            }
        }

        response.sendRedirect("quanlykhohangservlet");
    }

}
