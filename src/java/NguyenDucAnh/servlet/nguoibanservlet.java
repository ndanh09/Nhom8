package servlet;
import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.*;
import modeldao.*;
import java.util.*;
import util.AdminGuard;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "nguoibanservlet", urlPatterns = {"/nguoibanservlet"})
public class nguoibanservlet extends HttpServlet {

    nguoibandao dao = new nguoibandao();
    modeldao.quanlykhohangdao khoDao = new modeldao.quanlykhohangdao();

    // Đọc giá tiền từ form: bỏ dấu chấm/phẩy ngăn cách ("1.500.000" -> 1500000).
    // Sai định dạng hoặc <= 0 thì ném IllegalArgumentException để hiện thông báo rõ ràng.
    private static int docGia(String s) {
        if (s == null || s.replaceAll("\\D", "").isEmpty()) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ!");
        }
        try {
            int gia = Integer.parseInt(s.replaceAll("\\D", ""));
            if (gia <= 0) {
                throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0!");
            }
            return gia;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ!");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // FIX: trang admin phải đăng nhập admin mới được vào/thao tác.
        if (!AdminGuard.kiemTraQuanLy(request, response)) {
            return;
        }

        // Danh sách đã GỘP theo mã gốc: mỗi sản phẩm chỉ còn 1 dòng đại diện,
        // không hiện riêng từng size nữa (xem chi tiết size ở trang Quản lý size).
        ArrayList<nhomsanpham> dsNhom = dao.layDanhSachNhom();
        request.setAttribute("dsnhom", dsNhom);
        request.getRequestDispatcher("/view/nguoiban.jsp").forward(request, response);
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
        jakarta.servlet.http.HttpSession session = request.getSession();
        String action = request.getParameter("action");
        try {
            if ("delete".equals(action)) {
                // Xoá theo mã gốc = xoá luôn TẤT CẢ size của sản phẩm đó.
                String magoc = request.getParameter("magoc");
                if (magoc == null || magoc.trim().isEmpty()) {
                    magoc = request.getParameter("masanpham");
                }
                dao.xoaNhom(magoc);
                session.setAttribute("success", "Xoá sản phẩm thành công (đã xoá toàn bộ các size)!");
            } else if ("update".equals(action)) {
                // Sửa ở trang Quản lý sản phẩm chỉ sửa thông tin CHUNG (tên, ảnh, danh
                // mục, giá, ngày bán, trạng thái) và áp dụng cho TẤT CẢ size cùng lúc.
                // Muốn sửa số lượng/thêm/xoá riêng từng size thì qua trang Quản lý size.
                String magoc = request.getParameter("magoc");
                String tensanpham = request.getParameter("tensanpham");
                int giasanpham = docGia(request.getParameter("giasanpham"));
                String ngayban = request.getParameter("ngayban");
                String trangthai = request.getParameter("trangthai");
                String danhmuc = request.getParameter("danhmuc");

                String filename = request.getParameter("hinhanh_cu");
                Part filePart = request.getPart("hinhanh");
                if (filePart != null && filePart.getSize() > 0) {
                    // FIX: lưu ảnh qua ImageStorage.luuAnh (chống path traversal, chỉ nhận file ảnh)
                    filename = util.ImageStorage.luuAnh(filePart, "");
                }

                dao.suaNhom(magoc, tensanpham, filename, danhmuc, giasanpham, ngayban, trangthai);
                session.setAttribute("success", "Cập nhật sản phẩm thành công (áp dụng cho tất cả size)!");
            } else if ("add".equals(action)) {
                String masanpham = request.getParameter("masanpham");
                String tensanpham = request.getParameter("tensanpham");
                // Số lượng mặc định không còn bắt buộc: nếu người bán đã nhập số lượng
                // riêng cho từng size thì giá trị này chỉ dùng làm phương án dự phòng.
                String soluongMacDinhStr = request.getParameter("soluongsanpham");
                int soluongsanpham = 0;
                if (soluongMacDinhStr != null && !soluongMacDinhStr.trim().isEmpty()) {
                    try {
                        soluongsanpham = Integer.parseInt(soluongMacDinhStr.trim());
                        if (soluongsanpham < 0) {
                            soluongsanpham = 0;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                String[] kichcoArr = request.getParameterValues("kichco");
                int giasanpham = docGia(request.getParameter("giasanpham"));
                String ngayban = request.getParameter("ngayban");
                String trangthai = request.getParameter("trangthai");
                String danhmuc = request.getParameter("danhmuc");
                String filename = "";
                Part filePart = request.getPart("hinhanh");
                if (filePart != null && filePart.getSize() > 0) {
                    // Lưu vào thư mục bền vững (util.ImageStorage), không lưu vào thư mục
                    // web đã deploy vì thư mục đó bị xoá mỗi khi rebuild.
                    // FIX: dùng ImageStorage.luuAnh để chặn path traversal / file không phải ảnh.
                    filename = util.ImageStorage.luuAnh(filePart, "");
                }

                if (kichcoArr == null || kichcoArr.length == 0) {
                    session.setAttribute("error", "Vui lòng chọn ít nhất 1 kích cỡ!");
                } else {
                    if (filename.isEmpty()) {
                        filename = "default.png";
                    }
                    // Thêm cùng lúc nhiều size cho 1 sản phẩm: mỗi size là 1 biến thể
                    // (mã sản phẩm riêng, cùng tên sản phẩm) để trang chi tiết sản phẩm
                    // gộp lại thành các lựa chọn size cho người mua.
                    java.util.List<String> daThem = new java.util.ArrayList<>();
                    java.util.List<String> trungMa = new java.util.ArrayList<>();
                    java.util.List<String> thatBai = new java.util.ArrayList<>();
                    for (String kichco : kichcoArr) {
                        String masanphamBienThe = kichcoArr.length > 1
                                ? masanpham + "-" + kichco
                                : masanpham;
                        if (dao.check(masanphamBienThe)) {
                            trungMa.add(masanphamBienThe);
                            continue;
                        }
                        int soluongSize = soluongsanpham;
                        String soluongParam = request.getParameter("soluong_" + kichco);
                        if (soluongParam != null && !soluongParam.trim().isEmpty()) {
                            try {
                                soluongSize = Integer.parseInt(soluongParam.trim());
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        if (soluongSize < 0) {
                            soluongSize = 0;
                        }
                        nguoiban spMoi = new nguoiban(masanphamBienThe, masanpham, tensanpham, filename, soluongSize, kichco, giasanpham, ngayban, trangthai, danhmuc);
                        // FIX: trước đây dao.them() nuốt lỗi SQL nên vẫn báo "thêm thành công" dù chưa lưu được.
                        if (!dao.them(spMoi)) {
                            thatBai.add(masanphamBienThe);
                            continue;
                        }
                        // Đồng bộ: sản phẩm/size vừa thêm sẽ tự tạo (hoặc cộng dồn) dòng
                        // tương ứng trong Kho hàng, SL Nhập lấy đúng số lượng người bán vừa nhập.
                        khoDao.themHoacCongDon(masanphamBienThe, soluongSize, "Người bán");
                        daThem.add(kichco);
                    }
                    if (!thatBai.isEmpty()) {
                        session.setAttribute("error", "Không lưu được: " + String.join(", ", thatBai)
                                + (daThem.isEmpty() ? "" : ". Đã thêm size: " + String.join(", ", daThem)));
                    } else if (!daThem.isEmpty() && trungMa.isEmpty()) {
                        session.setAttribute("success", "Thêm sản phẩm thành công với " + daThem.size() + " size: " + String.join(", ", daThem) + "!");
                    } else if (!daThem.isEmpty() && !trungMa.isEmpty()) {
                        session.setAttribute("success", "Đã thêm size: " + String.join(", ", daThem) + ". Bỏ qua mã đã tồn tại: " + String.join(", ", trungMa));
                    } else {
                        session.setAttribute("error", "Tất cả mã sản phẩm cho các size đã chọn đều đã tồn tại!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // FIX: chỉ hiện message tự viết (lỗi nhập liệu/ảnh), không lộ chi tiết lỗi SQL/hệ thống ra giao diện.
            session.setAttribute("error", (e instanceof IllegalArgumentException && !(e instanceof NumberFormatException))
                    ? e.getMessage() : "Có lỗi xảy ra, vui lòng thử lại.");
        }
        response.sendRedirect("nguoibanservlet");
    }
}
