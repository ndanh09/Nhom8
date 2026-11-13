package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.GioHang;
import model.PhieuGiamGia;
import model.nguoidung;

import modeldao.GioHangDAO;
import modeldao.PhieuGiamGiaDAO;
import modeldao.nguoidungdao;


@WebServlet(name = "giohangservlet", urlPatterns = {"/giohangservlet"})
public class giohangservlet extends HttpServlet {

    private GioHangDAO ghDAO = new GioHangDAO();
    private PhieuGiamGiaDAO pggDAO = new PhieuGiamGiaDAO();
    private nguoidungdao spDAO = new nguoidungdao();

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

    // ================== TÍCH CHỌN SẢN PHẨM ĐỂ THANH TOÁN ==================
    // FIX: Trước đây giỏ hàng không có khái niệm "chọn sản phẩm", mọi thao
    // tác (áp mã giảm giá, thanh toán) đều tính/trên TOÀN BỘ giỏ hàng. Nay
    // thêm session "selectedCartIds" (Set<Integer> các magiohang đang được
    // tích chọn) để khách có thể chỉ chọn 1 phần sản phẩm trong giỏ (vd giỏ
    // có 8 sp, chỉ tích 4 sp) rồi áp mã giảm giá / thanh toán CHỈ trên 4 sp
    // đã chọn đó, các sản phẩm không chọn vẫn nằm nguyên trong giỏ.
    //
    // Mặc định lần đầu (chưa từng tích gì) coi như TẤT CẢ sản phẩm đang có
    // trong giỏ đều được chọn, giữ đúng hành vi cũ (giữ tương thích ngược).
    @SuppressWarnings("unchecked")
    private Set<Integer> getSelectedIds(HttpSession session, ArrayList<GioHang> listCart) {

        Set<Integer> selectedIds = (Set<Integer>) session.getAttribute("selectedCartIds");

        if (selectedIds == null) {

            selectedIds = new HashSet<>();

            for (GioHang item : listCart) {
                selectedIds.add(item.getMagiohang());
            }

            session.setAttribute("selectedCartIds", selectedIds);
        }

        return selectedIds;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String makhachhang = (String) session.getAttribute("email");

        if (makhachhang == null || makhachhang.trim().isEmpty()) {
            // FIX: trước đây redirect tới /dangnhap.jsp (không tồn tại -> 404), trang đúng nằm trong /view/.
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        ArrayList<GioHang> listCart = ghDAO.getDanhSachGioHang(makhachhang);

        // FIX: với mỗi dòng trong giỏ, tra đúng danh sách size mà sản phẩm
        // đó (theo tensanpham) đang có trong bảng nguoidung -> đổ vào dropdown
        // size ở giỏ hàng, thay vì hardcode cứng S/M/L/XL cho mọi sản phẩm.
        Map<Integer, ArrayList<String>> sizeMap = new HashMap<>();

        for (GioHang item : listCart) {

            String tensanpham = item.getTensanpham();

            if (tensanpham != null && !sizeMap.containsKey(item.getMagiohang())) {
                sizeMap.put(item.getMagiohang(), ghDAO.layDanhSachSizeTheoTen(tensanpham));
            }
        }

        request.setAttribute("sizeMap", sizeMap);

        // Toàn bộ danh sách sản phẩm (bên trang nguoidung) để ô tìm kiếm
        // gợi ý theo sản phẩm toàn hệ thống, không chỉ sản phẩm trong giỏ.
        ArrayList<nguoidung> listsp = gomTheoTenSanPham(spDAO.sp());
        request.setAttribute("listsp", listsp);

        // FIX: Khi khách bấm "Dùng ngay" ở trang Mã giảm giá, KHÔNG áp
        // dụng mã luôn (trước đây bấm phát là áp dụng ngay trên toàn bộ
        // giỏ, khách không kịp chọn sản phẩm muốn dùng mã). Thay vào đó,
        // chỉ điền sẵn mã vào ô nhập ở giỏ hàng, khách phải tự tích chọn
        // sản phẩm rồi bấm "ÁP DỤNG" ở đây mới thực sự áp dụng mã.
        String prefillMa = request.getParameter("magiamgia");

        if (prefillMa != null && !prefillMa.trim().isEmpty()) {

            request.setAttribute("prefillMaGiamGia", prefillMa.trim());

            toastInfo(request, "Đã điền mã \"" + prefillMa.trim()
                    + "\". Hãy tích chọn sản phẩm bạn muốn áp dụng rồi bấm ÁP DỤNG.");
        }

        // Loại bỏ những magiohang đã chọn trước đó nhưng sản phẩm không còn
        // trong giỏ nữa (ví dụ vừa bị xoá), tránh selectedCartIds "rác".
        Set<Integer> selectedIds = getSelectedIds(session, listCart);

        Set<Integer> stillInCart = new HashSet<>();
        for (GioHang item : listCart) {
            stillInCart.add(item.getMagiohang());
        }
        selectedIds.retainAll(stillInCart);
        session.setAttribute("selectedCartIds", selectedIds);

        // Map để JSP dễ kiểm tra 1 dòng có đang được tích chọn hay không:
        // ${selectedMap[item.magiohang]}
        Map<Integer, Boolean> selectedMap = new HashMap<>();
        for (GioHang item : listCart) {
            selectedMap.put(item.getMagiohang(), selectedIds.contains(item.getMagiohang()));
        }
        request.setAttribute("selectedMap", selectedMap);

        boolean allSelected = !listCart.isEmpty() && selectedIds.size() == listCart.size();
        request.setAttribute("allSelected", allSelected);

        // FIX: tổng tiền hàng / giảm giá / thành tiền chỉ tính trên các sản
        // phẩm ĐANG ĐƯỢC CHỌN, không phải toàn bộ giỏ hàng.
        int totalCartPrice = 0;

        for (GioHang item : listCart) {
            if (selectedIds.contains(item.getMagiohang())) {
                totalCartPrice += item.getGiaban() * item.getSoluong();
            }
        }

        String maGiamGia = (String) session.getAttribute("maGiamGia");

        PhieuGiamGia voucher = null;
        int tienGiam = 0;

        if (maGiamGia != null && !maGiamGia.trim().isEmpty()) {

            voucher = pggDAO.kiemTraMa(maGiamGia);

            if (voucher != null) {
                tienGiam = pggDAO.tinhTienGiam(voucher, totalCartPrice);
            }
        }

        int thanhTien = totalCartPrice - tienGiam;

        if (thanhTien < 0) {
            thanhTien = 0;
        }

        session.setAttribute("thanhTien", thanhTien);

        request.setAttribute("listCart", listCart);
        request.setAttribute("totalCartPrice", totalCartPrice);
        request.setAttribute("voucher", voucher);
        request.setAttribute("tienGiam", tienGiam);
        request.setAttribute("thanhTien", thanhTien);

        request.getRequestDispatcher("view/giohang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        String makhachhang = (String) session.getAttribute("email");

        if (makhachhang == null || makhachhang.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/view/dangnhap.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {

            if ("update".equals(action)) {

                int magiohang = Integer.parseInt(request.getParameter("magiohang"));
                int soluong = Integer.parseInt(request.getParameter("soluong"));
                String kichco = request.getParameter("kichco");

                // Không giới hạn cứng số lượng nữa: tồn kho có bao nhiêu thì
                // cho phép mua tối đa bấy nhiêu, chỉ kiểm tra tồn kho bên dưới.
                if (soluong > 0) {

                    // Tìm sản phẩm tương ứng trong giỏ hàng của khách
                    // để lấy tên sản phẩm, phục vụ tra lại mã sản phẩm
                    // (masanpham) đúng theo SIZE MỚI khách vừa chọn.
                    ArrayList<GioHang> listCart = ghDAO.getDanhSachGioHang(makhachhang);

                    String tensanpham = null;

                    for (GioHang item : listCart) {
                        if (item.getMagiohang() == magiohang) {
                            tensanpham = item.getTensanpham();
                            break;
                        }
                    }

                    // FIX: trước đây kiểm tra tồn kho + cập nhật giỏ hàng vẫn
                    // dùng masanpham CŨ (của size cũ), nên khi khách đổi size
                    // (VD từ L sang XXL) thì hệ thống lại kiểm tra/trừ tồn
                    // kho của size L thay vì XXL. Nay tra đúng masanpham của
                    // SIZE MỚI (kichco) trước khi kiểm tra tồn kho.
                    String masanphamMoi = (tensanpham != null)
                            ? ghDAO.layMaSanPhamTheoSize(tensanpham, kichco)
                            : null;

                    boolean conHang = true;

                    if (masanphamMoi != null) {
                        int tonKho = ghDAO.getTonKho(masanphamMoi);
                        if (soluong > tonKho) {
                            conHang = false;
                        }
                    } else {
                        // Không tìm thấy size này cho sản phẩm -> chặn luôn,
                        // tránh lưu vào giỏ 1 size không tồn tại.
                        conHang = false;
                    }

                    if (!conHang) {
                        toastError(request, "Cập nhật giỏ hàng thất bại! Size đã chọn không đủ hàng.");
                    } else {

                        boolean capNhatOk = ghDAO.updateGioHang(magiohang, soluong, kichco, masanphamMoi);

                        if (capNhatOk) {
                            toastSuccess(request, "Cập nhật giỏ hàng thành công!");
                        } else {
                            toastError(request, "Cập nhật giỏ hàng thất bại!");
                        }
                    }

                } else {
                    toastError(request, "Cập nhật giỏ hàng thất bại! Số lượng không hợp lệ.");
                }

            } else if ("delete".equals(action)) {

                int magiohang = Integer.parseInt(request.getParameter("magiohang"));

                // FIX: chỉ xóa dòng giỏ hàng CỦA CHÍNH khách đang đăng nhập (trước đây chỉ cần biết
                // magiohang là xóa được giỏ của người khác).
                ghDAO.deleteSanPhamKhoiGio(magiohang, makhachhang);

                // Sản phẩm đã bị xoá thì cũng bỏ khỏi danh sách đang chọn
                // (nếu có) để tránh selectedCartIds giữ id không còn tồn tại.
                @SuppressWarnings("unchecked")
                Set<Integer> selectedIds = (Set<Integer>) session.getAttribute("selectedCartIds");
                if (selectedIds != null) {
                    selectedIds.remove(magiohang);
                }

                toastSuccess(request, "Xóa sản phẩm thành công!");

            } else if ("deleteSelected".equals(action)) {

                // FIX MỚI: xoá HÀNG LOẠT các sản phẩm khách đang tích chọn
                // (checkbox ở cột đầu bảng giỏ hàng), để tiện dùng chung với
                // nút "Chọn tất cả" khi khách lỡ tích nhầm/không muốn mua nữa
                // mà không cần xoá từng dòng một. Nút xoá từng dòng riêng lẻ
                // ở cột "Hành động" vẫn được giữ nguyên như cũ.
                String[] dsMaGioHang = request.getParameterValues("magiohang");

                int soLuongDaXoa = 0;

                @SuppressWarnings("unchecked")
                Set<Integer> selectedIdsXoa = (Set<Integer>) session.getAttribute("selectedCartIds");

                if (dsMaGioHang != null) {
                    for (String s : dsMaGioHang) {
                        try {
                            int id = Integer.parseInt(s);
                            if (ghDAO.deleteSanPhamKhoiGio(id, makhachhang)) {
                                soLuongDaXoa++;
                            }
                            if (selectedIdsXoa != null) {
                                selectedIdsXoa.remove(id);
                            }
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }

                if (soLuongDaXoa > 0) {
                    toastSuccess(request, "Đã xóa " + soLuongDaXoa + " sản phẩm đã chọn khỏi giỏ hàng!");
                } else {
                    toastError(request, "Bạn chưa chọn sản phẩm nào để xóa!");
                }

            } else if ("selectItems".equals(action)) {

                // FIX MỚI: nhận danh sách magiohang đang được tích chọn ở
                // giỏ hàng (checkbox), lưu vào session để dùng khi áp mã
                // giảm giá và khi thanh toán, KHÔNG tính/áp dụng trên toàn
                // bộ giỏ hàng nữa.
                String[] chkItems = request.getParameterValues("chkItem");

                Set<Integer> selectedIds = new HashSet<>();

                if (chkItems != null) {
                    for (String s : chkItems) {
                        try {
                            selectedIds.add(Integer.parseInt(s));
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }

                session.setAttribute("selectedCartIds", selectedIds);

            } else if ("applyVoucher".equals(action)) {

                String maGiamGia = request.getParameter("magiamgia");

                if (maGiamGia == null || maGiamGia.trim().isEmpty()) {

                    toastError(request, "Vui lòng nhập mã giảm giá!");
                    response.sendRedirect(request.getContextPath() + "/giohangservlet");
                    return;
                }

                PhieuGiamGia voucher = pggDAO.kiemTraMa(maGiamGia.trim());

                if (voucher != null) {

                    String maDangDung = (String) session.getAttribute("maGiamGia");

                    // Chỉ trừ số lượng khi đây thực sự là mã MỚI được
                    // thêm vào (tránh bị trừ nhiều lần nếu người dùng
                    // bấm "Áp dụng" lại đúng mã đang dùng hoặc load lại trang)
                    if (maDangDung == null || !maDangDung.equalsIgnoreCase(maGiamGia.trim())) {
                        pggDAO.giamLuotSuDung(maGiamGia.trim());
                    }

                    session.setAttribute("maGiamGia", maGiamGia.trim());

                    // FIX: mã giảm giá được áp dụng và tính tiền giảm dựa
                    // trên tổng tiền của các sản phẩm ĐANG ĐƯỢC CHỌN (xem
                    // doGet ở trên), không phải toàn bộ giỏ hàng.
                    toastSuccess(request, "Áp dụng mã giảm giá thành công! Mã chỉ áp dụng cho các sản phẩm đang được chọn.");

                } else {
                    toastError(request, "Mã giảm giá không tồn tại!");
                }

            } else if ("checkout".equals(action)) {

                ArrayList<GioHang> listCart = ghDAO.getDanhSachGioHang(makhachhang);

                if (listCart == null || listCart.isEmpty()) {

                    toastError(request, "Giỏ hàng đang trống!");
                    response.sendRedirect(request.getContextPath() + "/giohangservlet");
                    return;
                }

                // FIX: chỉ lấy ra những sản phẩm ĐANG ĐƯỢC TÍCH CHỌN để
                // thanh toán, các sản phẩm còn lại vẫn giữ nguyên trong giỏ.
                Set<Integer> selectedIds = getSelectedIds(session, listCart);

                ArrayList<GioHang> selectedCart = new ArrayList<>();

                for (GioHang item : listCart) {
                    if (selectedIds.contains(item.getMagiohang())) {
                        selectedCart.add(item);
                    }
                }

                if (selectedCart.isEmpty()) {

                    toastError(request, "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán!");
                    response.sendRedirect(request.getContextPath() + "/giohangservlet");
                    return;
                }

                // Kiểm tra tồn kho trước khi thanh toán (chỉ với sp đã chọn)
                for (GioHang item : selectedCart) {

                    int tonKho = ghDAO.getTonKho(item.getMasanpham());

                    if (item.getSoluong() > tonKho) {

                        toastError(request, "Sản phẩm " + item.getMasanpham() + " không đủ số lượng!");
                        response.sendRedirect(request.getContextPath() + "/giohangservlet");
                        return;
                    }
                }

                // FIX: lưu đúng danh sách sản phẩm ĐÃ CHỌN vào session để
                // DonHangServlet dùng khi tạo đơn (trước đây "listCart" lưu
                // ở đây bị bỏ quên, DonHangServlet tự query lại TOÀN BỘ giỏ
                // hàng nên không thể lọc theo lựa chọn của khách).
                session.setAttribute("listCart", selectedCart);

                String maGiamGia = (String) session.getAttribute("maGiamGia");

                int totalCartPrice = 0;

                for (GioHang item : selectedCart) {
                    totalCartPrice += item.getGiaban() * item.getSoluong();
                }

                int tienGiam = 0;

                if (maGiamGia != null && !maGiamGia.trim().isEmpty()) {

                    PhieuGiamGia voucher = pggDAO.kiemTraMa(maGiamGia);

                    if (voucher != null) {
                        tienGiam = pggDAO.tinhTienGiam(voucher, totalCartPrice);
                    }
                }

                int thanhTien = totalCartPrice - tienGiam;

                if (thanhTien < 0) {
                    thanhTien = 0;
                }

                session.setAttribute("totalCartPrice", totalCartPrice);
                session.setAttribute("tienGiam", tienGiam);
                session.setAttribute("thanhTien", thanhTien);

                // Chuyển sang bước nhập thông tin đơn hàng
                response.sendRedirect(request.getContextPath() + "/donhangservlet");
                return;
            }

        } catch (Exception e) {

            e.printStackTrace();
            toastError(request, "Có lỗi xảy ra!");
        }

        response.sendRedirect(request.getContextPath() + "/giohangservlet");
    }

    private void toastSuccess(HttpServletRequest request, String message) {
        request.getSession().setAttribute("toastType", "success");
        request.getSession().setAttribute("toastMessage", message);
    }

    private void toastError(HttpServletRequest request, String message) {
        request.getSession().setAttribute("toastType", "error");
        request.getSession().setAttribute("toastMessage", message);
    }

    private void toastInfo(HttpServletRequest request, String message) {
        request.getSession().setAttribute("toastType", "info");
        request.getSession().setAttribute("toastMessage", message);
    }

    @Override
    public String getServletInfo() {
        return "Gio Hang Servlet";
    }

}