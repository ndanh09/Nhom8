package util;

import java.text.Normalizer;
import java.util.*;

/**
 * Bộ xử lý chatbot FAQ cho LODI AUTHENTIC STORE (không cần API ngoài).
 * Câu trả lời là HTML; chuỗi "{ctx}" sẽ được servlet thay bằng contextPath.
 * Sửa nội dung: chỉnh hằng số SHOP_* và danh sách FAQ trong khối static bên dưới.
 */
public final class ChatbotEngine {

    // ===== Thông tin shop (lấy từ thongtinlienhe.jsp / chitietsanpham.jsp) =====
    private static final String ADDRESS = "Ngõ 27 Tạ Quang Bửu, Phường Bạch Mai, Hà Nội";
    private static final String HOURS = "08:00 – 23:00, tất cả các ngày trong tuần";
    private static final String FREESHIP = "2.000.000đ";
    private static final String ZALO = "https://zalo.me/g/gxizmmsudpjibttywgv5";
    private static final String FACEBOOK = "https://www.facebook.com/duy.khanh.bui.450782/";
    private static final String TIKTOK = "https://www.tiktok.com/@duykhnhbi064";
    private static final String INSTAGRAM = "https://www.instagram.com/l.ducanh69";
    private static final String HOTLINE = "<a href=\"tel:0352328899\">0352 328 899</a>";
    private static final String MAIL = "<a href=\"mailto:anhldth08719@gmail.com\">anhldth08719@gmail.com</a>";

    private static String a(String path, String text) { return "<a href=\"{ctx}" + path + "\">" + text + "</a>"; }
    private static final String SP = a("/nguoidungservlet", "trang Sản phẩm");
    private static final String CART = a("/giohangservlet", "Giỏ hàng");
    private static final String ORDERS = a("/trangthaiservlet", "Đơn hàng");
    private static final String PROMO = a("/magiamgiaservlet", "Khuyến mãi");
    private static final String LOGIN = a("/view/dangnhap.jsp", "Đăng nhập");
    private static final String REGISTER = a("/view/dangky.jsp", "Đăng ký");
    private static final String PROFILE = a("/view/thongtinkhachhang.jsp", "Thông tin cá nhân");
    private static final String SOCIAL_ZALO = "<a href=\"" + ZALO + "\" target=\"_blank\" rel=\"noopener\">Zalo</a>";

    /** Nhóm câu hỏi: id -> tên hiển thị (giữ thứ tự). */
    public static final Map<String, String> GROUPS = new LinkedHashMap<>();

    /** Một mục FAQ. */
    private static final class Faq {
        final String group, id, answer;
        final String[] questions;
        private List<List<String>> tokens;   // tạo lười (sau khi ALIAS/STOP đã khởi tạo xong)
        Faq(String group, String id, String[] questions, String answer) {
            this.group = group; this.id = id; this.questions = questions; this.answer = answer;
        }
        synchronized List<List<String>> tokens() {
            if (tokens == null) {
                tokens = new ArrayList<>();
                for (String q : questions) tokens.add(tokenize(q));
            }
            return tokens;
        }
    }

    private static final List<Faq> FAQS = new ArrayList<>();

    /** Kết quả trả về cho servlet. */
    public static final class Reply {
        public final String answerHtml;
        public final List<String> suggestions;
        public final boolean matched;
        Reply(String answerHtml, List<String> suggestions, boolean matched) {
            this.answerHtml = answerHtml; this.suggestions = suggestions; this.matched = matched;
        }
    }

    private static void add(String g, String id, String[] q, String ans) { FAQS.add(new Faq(g, id, q, ans)); }
    private static String[] q(String... s) { return s; }

    static {
        GROUPS.put("sp", "Sản phẩm"); GROUPS.put("gia", "Giá"); GROUPS.put("size", "Size");
        GROUPS.put("gh", "Giỏ hàng & đặt hàng"); GROUPS.put("mgg", "Mã giảm giá");
        GROUPS.put("tt", "Thanh toán"); GROUPS.put("vc", "Vận chuyển");
        GROUPS.put("dt", "Đổi trả"); GROUPS.put("tk", "Tài khoản"); GROUPS.put("lh", "Liên hệ");

        // 1. SẢN PHẨM
        add("sp", "sp_loai", q("Shop bán những loại sản phẩm nào?", "Shop có bán áo bóng đá không?", "Shop có những mẫu áo bóng đá mới nào?"),
            "Có ạ! LODI AUTHENTIC STORE chuyên <b>áo đấu bóng đá Authentic chính hãng</b>, gồm 3 danh mục:<br>• <b>Áo CLB</b><br>• <b>Áo ĐTQG</b><br>• <b>Áo Retro ĐTQG &amp; CLB</b><br>Mẫu mới được cập nhật tại " + SP + ".");
        add("sp", "sp_clb", q("Shop có áo đấu câu lạc bộ nào?", "Shop có áo Manchester United không?", "Shop có áo Real Madrid không?", "Shop có áo Barcelona không?"),
            "Shop có danh mục <b>Áo CLB</b> với nhiều đội bóng. Số lượng từng mẫu thay đổi theo kho, bạn gõ tên đội vào <b>ô tìm kiếm</b> đầu trang hoặc xem tại " + SP + " để biết mẫu nào còn hàng nhé.");
        add("sp", "sp_dtqg", q("Shop có áo đội tuyển quốc gia không?"),
            "Có ạ, shop có danh mục <b>Áo ĐTQG</b> và <b>Áo Retro ĐTQG &amp; CLB</b>. Bạn xem tại " + SP + ".");
        add("sp", "sp_chinhhang", q("Sản phẩm của shop có phải hàng chính hãng không?"),
            "Đúng ạ, shop cam kết <b>Authentic chính hãng</b>. Sản phẩm lỗi do nhà sản xuất được đổi trả trong 7 ngày.");
        add("sp", "sp_chitiet", q("Làm thế nào để xem chi tiết sản phẩm?"),
            "Vào " + SP + " (hoặc Trang chủ), bấm nút <b>“Chi tiết”</b> trên thẻ sản phẩm để xem ảnh, giá, size còn hàng.");
        add("sp", "sp_khac", q("Shop có bán giày bóng đá không?", "Shop có bán phụ kiện bóng đá không?"),
            "Hiện shop tập trung vào <b>áo đấu bóng đá Authentic</b>, chưa có giày hay phụ kiện trên website. Cần tư vấn thêm, gọi " + HOTLINE + " nhé.");

        // 2. GIÁ
        add("gia", "gia_xem", q("Áo bóng đá có giá bao nhiêu?", "Giá áo Real Madrid là bao nhiêu?", "Giá áo Manchester United là bao nhiêu?", "Tôi có thể xem giá sản phẩm ở đâu?"),
            "Giá mỗi mẫu khác nhau tuỳ đội bóng và dòng sản phẩm. Bạn xem <b>giá chính xác trên thẻ sản phẩm</b> tại " + SP + " hoặc gõ tên áo vào ô tìm kiếm.");
        add("gia", "gia_re", q("Shop có áo bóng đá giá rẻ không?", "Sản phẩm nào đang có giá tốt?", "Shop có chương trình giảm giá không?"),
            "Shop thường có <b>mã giảm giá</b>! Xem các mã đang chạy tại " + PROMO + ". Đơn từ <b>" + FREESHIP + "</b> được miễn phí vận chuyển.");
        add("gia", "gia_ship", q("Giá sản phẩm đã bao gồm phí vận chuyển chưa?"),
            "Giá niêm yết là giá sản phẩm. Đơn từ <b>" + FREESHIP + "</b> được <b>miễn phí vận chuyển</b>; đơn thấp hơn, vui lòng gọi " + HOTLINE + " để được báo phí giao hàng.");

        // 3. SIZE
        add("size", "size_list", q("Shop có những size áo nào?", "Áo có size S không?", "Áo có size M không?", "Áo có size L không?", "Áo có size XL không?", "Một sản phẩm có những kích cỡ nào?"),
            "Áo có các size <b>S, M, L, XL, XXL</b>. Mỗi mẫu có thể chỉ còn một số size – bạn mở trang chi tiết sản phẩm để xem size còn hàng.");
        add("size", "size_chon", q("Làm thế nào để chọn size áo phù hợp?"),
            "Hãy lấy một chiếc áo đang mặc vừa người để so ngang ngực và chiều dài. Áo đấu thường ôm hơn áo thun, nếu phân vân nên chọn <b>size lớn hơn</b>. Cần tư vấn kỹ, gọi " + HOTLINE + ".");
        add("size", "size_doi", q("Tôi có thể đổi size sau khi mua không?", "Tôi có thể đổi size áo không?"),
            "Việc đổi size tuỳ tình trạng sản phẩm và kho. Bạn liên hệ sớm qua " + HOTLINE + " hoặc " + MAIL + " kèm mã đơn hàng nhé.");
        add("size", "size_conhang", q("Size tôi muốn mua hiện còn hàng không?"),
            "Mở trang chi tiết sản phẩm: size còn hàng chọn được, size hết hàng sẽ báo “Hết hàng”. Tồn kho cập nhật theo thời gian thực.");

        // 4. GIỎ HÀNG & ĐẶT HÀNG
        add("gh", "gh_them", q("Làm thế nào để thêm sản phẩm vào giỏ hàng?", "Tôi có thể mua nhiều sản phẩm cùng lúc không?"),
            "Vào trang chi tiết → chọn <b>size</b> và <b>số lượng</b> → bấm <b>“Thêm vào giỏ”</b>. Bạn có thể thêm nhiều sản phẩm và thanh toán cùng lúc.");
        add("gh", "gh_xem", q("Làm thế nào để xem giỏ hàng?"), "Bấm biểu tượng <b>Giỏ hàng</b> ở đầu trang hoặc vào " + CART + ".");
        add("gh", "gh_xoa", q("Làm thế nào để xóa sản phẩm khỏi giỏ hàng?"), "Trong " + CART + ", bấm nút <b>xóa</b> ở dòng sản phẩm muốn bỏ.");
        add("gh", "gh_sl", q("Làm thế nào để thay đổi số lượng sản phẩm?"), "Trong " + CART + ", dùng nút <b>+ / −</b> ở cột số lượng. Tổng tiền tự cập nhật.");
        add("gh", "gh_thanhtoan", q("Làm thế nào để tiến hành thanh toán?"),
            "1) Vào " + CART + "<br>2) <b>Tích chọn</b> sản phẩm muốn mua<br>3) (Tuỳ chọn) áp <b>mã giảm giá</b><br>4) Bấm <b>“Tiến hành thanh toán”</b>, điền họ tên, số điện thoại, địa chỉ và xác nhận.");
        add("gh", "gh_login", q("Tôi có cần đăng nhập để đặt hàng không?", "Tôi có thể mua hàng khi chưa có tài khoản không?"),
            "Bạn cần <b>đăng nhập</b> để thêm giỏ và đặt hàng. Chưa có tài khoản? " + REGISTER + " chỉ mất ít phút, hoặc " + LOGIN + " nếu đã có.");
        add("gh", "gh_kiemtra", q("Làm thế nào để kiểm tra đơn hàng đã đặt?", "Tôi có thể xem lịch sử mua hàng ở đâu?"),
            "Vào mục " + ORDERS + " (cần đăng nhập) để xem toàn bộ đơn đã đặt và trạng thái.");
        add("gh", "gh_huy", q("Tôi có thể hủy đơn hàng không?"),
            "Được ạ, khi đơn <b>chưa giao</b> bạn vào " + ORDERS + " và bấm <b>“Hủy đơn hàng”</b>. Hàng (và mã giảm giá nếu có) sẽ được hoàn lại.");

        // 5. MÃ GIẢM GIÁ
        add("mgg", "mgg_co", q("Shop hiện có mã giảm giá nào không?", "Làm thế nào để xem các mã giảm giá đang có?"),
            "Xem toàn bộ mã đang hoạt động tại " + PROMO + " – có mức giảm, đơn tối thiểu, giảm tối đa và thời hạn.");
        add("mgg", "mgg_lay", q("Làm thế nào để lấy mã giảm giá?"),
            "Vào " + PROMO + ", chọn mã bạn muốn và sao chép. Shop cũng đăng mã mới trên Facebook, Zalo, TikTok.");
        add("mgg", "mgg_dung", q("Làm thế nào để sử dụng mã giảm giá?"),
            "Trong " + CART + ", tích chọn sản phẩm, nhập mã vào ô <b>“Mã giảm giá”</b> rồi bấm áp dụng.");
        add("mgg", "mgg_loi", q("Tại sao mã giảm giá không sử dụng được?"),
            "Mã thường không dùng được khi:<br>• Chưa đạt <b>đơn tối thiểu</b> (chỉ tính sản phẩm đã tích chọn)<br>• Mã <b>hết hạn</b> hoặc chưa tới ngày bắt đầu<br>• Mã <b>hết lượt</b> hoặc đang bị khóa<br>• Nhập sai mã<br>Kiểm tra điều kiện tại " + PROMO + ".");
        add("mgg", "mgg_sl", q("Mỗi đơn hàng có thể sử dụng bao nhiêu mã giảm giá?"), "Mỗi đơn hàng áp dụng được <b>1 mã giảm giá</b>.");
        add("mgg", "mgg_hsd", q("Mã giảm giá có thời hạn sử dụng không?"),
            "Có ạ. Mỗi mã có <b>ngày bắt đầu – kết thúc</b> và số lượt dùng giới hạn, ghi rõ tại " + PROMO + ".");
        add("mgg", "mgg_dk", q("Đơn hàng tối thiểu để sử dụng mã giảm giá là bao nhiêu?", "Tôi có thể sử dụng mã giảm giá cho tất cả sản phẩm không?"),
            "Mức đơn tối thiểu <b>khác nhau theo từng mã</b>, ghi trên thẻ mã tại " + PROMO + ". Mã chỉ tính trên sản phẩm bạn <b>tích chọn</b>; một số mã có mức giảm tối đa.");

        // 6. THANH TOÁN
        add("tt", "tt_hinhthuc", q("Shop hỗ trợ những hình thức thanh toán nào?", "Tôi cần thanh toán như thế nào?", "Thanh toán khi nhận hàng được không?"),
            "Shop áp dụng <b>thanh toán khi nhận hàng (COD)</b>: kiểm tra hàng rồi trả tiền cho nhân viên giao hàng. Cần hình thức khác, gọi " + HOTLINE + ".");
        add("tt", "tt_tong", q("Tôi có thể kiểm tra tổng tiền đơn hàng ở đâu?", "Tại sao tổng tiền thanh toán bị thay đổi?"),
            "Tổng tiền hiển thị ở " + CART + " (trước khi đặt) và hóa đơn tại " + ORDERS + ". Tổng có thể đổi do <b>mã giảm giá</b>, <b>đổi số lượng</b>, hoặc <b>tích chọn/bỏ chọn</b> sản phẩm.");
        add("tt", "tt_phi", q("Phí vận chuyển được tính như thế nào?", "Phí giao hàng là bao nhiêu?"),
            "Đơn từ <b>" + FREESHIP + "</b> được <b>miễn phí vận chuyển</b> toàn quốc. Đơn thấp hơn, phí tuỳ khu vực – gọi " + HOTLINE + " để được báo chính xác.");

        // 7. VẬN CHUYỂN
        add("vc", "vc_toanquoc", q("Shop có giao hàng toàn quốc không?", "Shop có giao hàng đến Hà Nội không?"), "Có ạ, shop <b>giao hàng toàn quốc</b>, bao gồm cả Hà Nội.");
        add("vc", "vc_free", q("Shop có miễn phí vận chuyển không?", "Đơn hàng bao nhiêu tiền thì được miễn phí vận chuyển?"),
            "Có! Đơn từ <b>" + FREESHIP + "</b> được <b>freeship toàn quốc</b>.");
        add("vc", "vc_time", q("Bao lâu thì tôi nhận được hàng?"),
            "Thời gian giao tuỳ khu vực của bạn. Theo dõi tiến độ tại " + ORDERS + " hoặc gọi " + HOTLINE + " để biết thời gian cụ thể.");
        add("vc", "vc_theodoi", q("Làm thế nào để theo dõi đơn hàng?", "Tôi có thể kiểm tra trạng thái vận chuyển ở đâu?"),
            "Vào " + ORDERS + " để xem trạng thái: <i>chờ xử lý → đang giao → đã giao</i> (hoặc đã hủy).");
        add("vc", "vc_diachi", q("Tôi có thể thay đổi địa chỉ nhận hàng không?", "Làm thế nào để cập nhật địa chỉ nhận hàng?"),
            "Địa chỉ nhập ngay ở bước đặt hàng (sửa được mỗi lần) và cập nhật sẵn trong " + PROFILE + ". Đơn đã đặt cần đổi địa chỉ, gọi ngay " + HOTLINE + " trước khi giao.");
        add("vc", "vc_shipper", q("Tôi có thể liên hệ shipper để hỏi về đơn hàng không?"),
            "Bạn gọi " + HOTLINE + " cung cấp mã đơn, shop sẽ liên hệ shipper phụ trách và phản hồi nhanh nhất.");

        // 8. ĐỔI TRẢ
        add("dt", "dt_chinhsach", q("Shop có hỗ trợ đổi trả sản phẩm không?", "Thời gian đổi trả là bao lâu?", "Sản phẩm bị lỗi do nhà sản xuất có được đổi trả không?"),
            "Có ạ. Shop <b>đổi trả trong 7 ngày</b> nếu sản phẩm <b>lỗi do nhà sản xuất</b>. Bạn nên giữ tem mác và chụp ảnh lỗi gửi shop.");
        add("dt", "dt_loi", q("Sản phẩm bị lỗi thì phải làm thế nào?", "Tôi nhận được sản phẩm không đúng mẫu thì phải làm sao?"),
            "Rất xin lỗi bạn! Hãy <b>chụp ảnh/quay video</b> sản phẩm và mã đơn rồi gửi qua " + HOTLINE + ", " + SOCIAL_ZALO + " hoặc " + MAIL + ". Shop sẽ kiểm tra và đổi cho bạn trong 7 ngày.");
        add("dt", "dt_lienhe", q("Làm thế nào để liên hệ hỗ trợ đổi trả?"),
            "Liên hệ " + HOTLINE + " (" + HOURS + "), email " + MAIL + " hoặc " + SOCIAL_ZALO + ".");

        // 9. TÀI KHOẢN
        add("tk", "tk_dangky", q("Làm thế nào để đăng ký tài khoản?"), "Vào trang " + REGISTER + ", điền họ tên, email, số điện thoại, mật khẩu rồi xác nhận.");
        add("tk", "tk_dangnhap", q("Làm thế nào để đăng nhập?"), "Bấm <b>Đăng nhập</b> ở góc trên bên phải hoặc vào trang " + LOGIN + ", nhập email và mật khẩu.");
        add("tk", "tk_quenmk", q("Tôi quên mật khẩu thì phải làm sao?"),
            "Bạn liên hệ shop qua " + HOTLINE + " hoặc " + MAIL + " kèm email đăng ký để được hỗ trợ lấy lại tài khoản.");
        add("tk", "tk_dangxuat", q("Làm thế nào để đăng xuất tài khoản?"), "Bấm vào <b>tên tài khoản</b> ở góc trên bên phải → <b>Đăng xuất</b>.");
        add("tk", "tk_thongtin", q("Tôi có thể thay đổi thông tin cá nhân không?"),
            "Được ạ. Sau khi đăng nhập, vào " + PROFILE + " để cập nhật họ tên, số điện thoại, địa chỉ.");

        // 10. CỬA HÀNG & LIÊN HỆ
        add("lh", "lh_diachi", q("Shop nằm ở đâu?", "Địa chỉ cửa hàng ở đâu?"), "📍 LODI AUTHENTIC STORE<br><b>" + ADDRESS + "</b>");
        add("lh", "lh_giolam", q("Shop mở cửa từ mấy giờ?", "Shop có làm việc vào cuối tuần không?"),
            "🕗 Shop mở cửa <b>" + HOURS + "</b> – làm việc cả thứ Bảy và Chủ nhật.");
        add("lh", "lh_lienhe", q("Làm thế nào để liên hệ với shop?"),
            "📞 " + HOTLINE + "<br>✉️ " + MAIL + "<br>💬 " + SOCIAL_ZALO + " · <a href=\"" + FACEBOOK + "\" target=\"_blank\" rel=\"noopener\">Facebook</a> · " + a("/view/thongtinlienhe.jsp", "Trang Liên hệ"));
        add("lh", "lh_sdt", q("Số điện thoại của shop là gì?"), "📞 Hotline: <b>" + HOTLINE + "</b> (" + HOURS + ").");
        add("lh", "lh_email", q("Email của shop là gì?"), "✉️ Email: <b>" + MAIL + "</b>");
        add("lh", "lh_fb", q("Shop có Facebook không?"),
            "Có ạ! <a href=\"" + FACEBOOK + "\" target=\"_blank\" rel=\"noopener\">Facebook</a> · <a href=\"" + TIKTOK + "\" target=\"_blank\" rel=\"noopener\">TikTok</a> · <a href=\"" + INSTAGRAM + "\" target=\"_blank\" rel=\"noopener\">Instagram</a> · " + SOCIAL_ZALO);
        add("lh", "lh_nhanvien", q("Tôi muốn gặp nhân viên tư vấn thì làm thế nào?", "Shop có hỗ trợ khách hàng trực tuyến không?"),
            "Shop hỗ trợ qua " + SOCIAL_ZALO + ", Facebook và hotline " + HOTLINE + " (" + HOURS + "). Bạn cũng có thể đến: <b>" + ADDRESS + "</b>.");
    }

    // ===== Chuẩn hóa & so khớp =====
    private static final Map<String, String> ALIAS = new HashMap<>();
    private static final Set<String> STOP = new HashSet<>(Arrays.asList(
        "shop co khong la gi the nao nhung cua toi ban duoc de nhu ma va o mot cac nay ah a oi ha nhe voi cho minh".split(" ")));
    static {
        String[][] al = {{"ko", "khong"}, {"k", "khong"}, {"hok", "khong"}, {"dc", "duoc"}, {"hn", "ha noi"},
            {"mu", "manchester united"}, {"sp", "san pham"}, {"ship", "giao hang"}, {"shipper", "giao hang"},
            {"cod", "thanh toan khi nhan hang"}, {"mgg", "ma giam gia"}, {"voucher", "ma giam gia"},
            {"freeship", "mien phi van chuyen"}, {"sz", "size"}};
        for (String[] p : al) ALIAS.put(p[0], p[1]);
    }
    private static final double THRESHOLD = 0.34;

    /** Bỏ dấu tiếng Việt, chữ thường, chỉ giữ a-z 0-9 và khoảng trắng. */
    public static String normalize(String s) {
        if (s == null) return "";
        String t = s.toLowerCase(Locale.ROOT).replace('đ', 'd');
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return t.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    private static List<String> tokenize(String s) {
        List<String> words = new ArrayList<>();
        for (String w : normalize(s).split(" ")) {
            if (w.isEmpty()) continue;
            String r = ALIAS.getOrDefault(w, w);
            for (String x : r.split(" ")) if (!x.isEmpty() && !STOP.contains(x)) words.add(x);
        }
        List<String> out = new ArrayList<>(words);               // unigram
        for (int i = 0; i + 1 < words.size(); i++) out.add(words.get(i) + "_" + words.get(i + 1)); // bigram
        return out;
    }

    private static double dice(List<String> a, List<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0;
        Map<String, Integer> cnt = new HashMap<>();
        for (String x : b) cnt.merge(x, 1, Integer::sum);
        int hit = 0;
        for (String x : a) {
            Integer c = cnt.get(x);
            if (c != null && c > 0) { hit++; cnt.put(x, c - 1); }
        }
        return 2.0 * hit / (a.size() + b.size());
    }

    private static final String[][] SMALLTALK = {
        {"^(hi|hello|helo|alo|xin chao|chao|hey)\\b.*", "Xin chào bạn! 👋 Mình là trợ lý của LODI AUTHENTIC STORE. Bạn muốn hỏi về chủ đề nào ạ?"},
        {".*(cam on|thanks|thank you|tks).*", "Rất vui được hỗ trợ bạn! Cần gì thêm cứ hỏi mình nhé ⚽"},
        {"^(bye|tam biet|het roi)\\b.*", "Cảm ơn bạn đã ghé LODI AUTHENTIC STORE. Chúc bạn một ngày tốt lành!"}
    };

    /** Trả lời một câu hỏi tự do. */
    public static Reply ask(String question) {
        String n = normalize(question);
        if (n.isEmpty()) return new Reply("Bạn hãy nhập câu hỏi nhé!", new ArrayList<String>(), false);

        List<String> qt = tokenize(question);
        Faq best = null; double bestScore = 0;
        List<double[]> scores = new ArrayList<>();
        for (int i = 0; i < FAQS.size(); i++) {
            double s = 0;
            for (List<String> t : FAQS.get(i).tokens()) s = Math.max(s, dice(qt, t));
            scores.add(new double[]{i, s});
            if (s > bestScore) { bestScore = s; best = FAQS.get(i); }
        }
        if (best != null && bestScore >= THRESHOLD) {
            List<String> sug = new ArrayList<>();
            for (Faq f : FAQS) if (f.group.equals(best.group) && f != best && sug.size() < 3) sug.add(f.questions[0]);
            return new Reply(best.answer, sug, true);
        }
        for (String[] st : SMALLTALK) if (n.matches(st[0])) return new Reply(st[1], new ArrayList<String>(), true);

        Collections.sort(scores, new Comparator<double[]>() {
            public int compare(double[] x, double[] y) { return Double.compare(y[1], x[1]); }
        });
        List<String> sug = new ArrayList<>();
        for (double[] s : scores) if (s[1] > 0.12 && sug.size() < 3) sug.add(FAQS.get((int) s[0]).questions[0]);
        return new Reply("Xin lỗi, mình chưa hiểu rõ câu hỏi này 🙏. Bạn thử chọn gợi ý bên dưới hoặc gọi " + HOTLINE
            + " (" + HOURS + ") để được nhân viên hỗ trợ trực tiếp nhé.", sug, false);
    }

    private static Set<String> faqWords;

    /** Mọi từ (đã chuẩn hóa) xuất hiện trong các câu hỏi FAQ: từ nào ngoài tập này là từ lạ (vd tên đội chưa có). */
    public static synchronized Set<String> faqWords() {
        if (faqWords == null) {
            faqWords = new HashSet<>();
            for (Faq f : FAQS) for (String q : f.questions) faqWords.addAll(Arrays.asList(normalize(q).split(" ")));
        }
        return faqWords;
    }

    /** Danh sách câu hỏi mẫu của một nhóm (để hiện thành nút gợi ý). */
    public static List<String> questionsOf(String groupId) {
        List<String> out = new ArrayList<>();
        for (Faq f : FAQS) if (f.group.equals(groupId)) out.addAll(Arrays.asList(f.questions));
        return out;
    }
}
