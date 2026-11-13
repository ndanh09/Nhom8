package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.TrangThai;
import model.nguoidung;
import modeldao.TrangThaiDAO;
import modeldao.nguoidungdao;

/**
 * Trả lời chatbot bằng DỮ LIỆU THẬT trong database "datn" (thuần Java):
 *   1) Tra cứu sản phẩm: giá, size còn hàng / hết hàng (bảng nguoidung).
 *   2) Tra cứu đơn hàng của chính khách đang đăng nhập (bảng trangthai).
 * Trả về chuỗi HTML (có "{ctx}" để servlet thay bằng contextPath) hoặc null
 * nếu câu hỏi không thuộc 2 loại trên (khi đó dùng FAQ trong ChatbotEngine).
 */
public final class ChatbotLive {

    private ChatbotLive() {
    }

    private static String a(String path, String text) {
        return "<a href=\"{ctx}" + path + "\">" + text + "</a>";
    }

    /** Các từ chung chung, không dùng để nhận diện một mẫu áo cụ thể. */
    private static final Set<String> GENERIC = new HashSet<String>(Arrays.asList(
            ("ao bong da dau chinh hang authentic clb dtqg retro mau moi size san pham gia cua shop ban "
            + "xl xxl s m l con het bao nhieu co khong la the nao cho toi minh mua dang doi tuyen quoc "
            + "cau lac bo nha khach").split(" ")));

    private static final String[] SIZE_ORDER = {"S", "M", "L", "XL", "XXL"};

    // ------------------------------------------------------------ cache sản phẩm
    private static Map<String, List<nguoidung>> cache = new LinkedHashMap<String, List<nguoidung>>();
    private static long cacheTime = 0;

    /** Toàn bộ sản phẩm gom theo tên (mỗi tên có nhiều dòng = nhiều size). Cache 30 giây. */
    private static synchronized Map<String, List<nguoidung>> products() {
        long now = System.currentTimeMillis();
        if (cache.isEmpty() || now - cacheTime > 30000L) {
            Map<String, List<nguoidung>> m = new LinkedHashMap<String, List<nguoidung>>();
            try {
                for (nguoidung p : new nguoidungdao().sp()) {
                    if (p.getTensanpham() == null) continue;
                    String key = p.getTensanpham().trim();
                    List<nguoidung> l = m.get(key);
                    if (l == null) {
                        l = new ArrayList<nguoidung>();
                        m.put(key, l);
                    }
                    l.add(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            cache = m;
            cacheTime = now;
        }
        return cache;
    }

    // ------------------------------------------------------------ điểm vào
    /**
     * @param email email khách đang đăng nhập (null nếu chưa đăng nhập hoặc là admin)
     */
    public static String tryAnswer(String question, String email) {
        String n = ChatbotEngine.normalize(question);
        if (n.isEmpty()) return null;
        if (isOrderQuestion(n)) return orderAnswer(email);
        Map<String, List<nguoidung>> all = products();
        String byTeam = teamAnswer(all, question);       // tên đội / Retro / CLB / ĐTQG -> dữ liệu thật hoặc "chưa nhập"
        if (byTeam != null) return byTeam;
        return ChatbotTeams.looksLikeShirtQuestion(n) ? productAnswer(all, question) : null;
    }

    // ------------------------------------------------------------ tra theo tên đội / danh mục
    /**
     * Khách gõ tên đội (CLB hoặc ĐTQG), "retro", "áo CLB", "áo ĐTQG"...:
     *  - có sản phẩm trong database -> trả giá, size còn hàng;
     *  - không có -> báo shop chưa nhập sản phẩm đó.
     * Trả null nếu câu hỏi không nhắc tới đội / danh mục nào.
     */
    static String teamAnswer(Map<String, List<nguoidung>> all, String question) {
        String n = ChatbotEngine.normalize(question);
        List<ChatbotTeams.Team> teams = ChatbotTeams.mentioned(n);
        boolean retro = ChatbotTeams.wantsRetro(n);
        String cat = ChatbotTeams.categoryWord(n);
        if (teams.isEmpty() && !retro && cat == null) return null;
        if (!ChatbotTeams.looksLikeShirtQuestion(n)) return null;

        // 1) lọc theo danh mục (Retro / CLB / ĐTQG)
        List<String> pool = new ArrayList<String>();
        for (Map.Entry<String, List<nguoidung>> e : all.entrySet()) {
            String c = ChatbotTeams.categoryOf(e.getValue().get(0).getDanhmuc());
            boolean isRetro = "RETRO".equals(c) || ChatbotTeams.wantsRetro(ChatbotEngine.normalize(e.getKey()));
            if (retro ? !isRetro : (cat != null && !cat.equals(c))) continue;
            pool.add(e.getKey());
        }

        // 2) lọc theo tên đội (nếu có)
        List<String> found = new ArrayList<String>(), missing = new ArrayList<String>();
        List<String> extra = teams.isEmpty() ? unknownNameTokens(n) : Collections.<String>emptyList();
        if (teams.isEmpty()) {
            for (String name : pool) {
                if (extra.isEmpty() || !Collections.disjoint(extra, Arrays.asList(ChatbotEngine.normalize(name).split(" ")))) found.add(name);
            }
        } else {
            for (ChatbotTeams.Team t : teams) {
                boolean any = false;
                for (String name : pool) {
                    if (ChatbotTeams.mentioned(ChatbotEngine.normalize(name)).contains(t)) {
                        if (!found.contains(name)) found.add(name);
                        any = true;
                    }
                }
                if (!any) missing.add(t.name);
            }
        }

        // 3) soạn câu trả lời
        List<String> labels = new ArrayList<String>();
        for (ChatbotTeams.Team t : teams) labels.add(t.name);
        String want = ("áo " + (retro ? "retro " : "")
                + (!labels.isEmpty() ? join(labels) : (cat != null ? ("DTQG".equals(cat) ? "ĐTQG" : cat) : ""))
                + " " + displayWords(question, extra)).trim().replaceAll("\\s+", " ");
        String sp = a("/nguoidungservlet", "trang Sản phẩm");
        if (found.isEmpty()) {
            return "Hiện shop <b>chưa nhập</b> sản phẩm <b>" + esc(want) + "</b> nên mình chưa có thông tin về mẫu này. "
                    + "Bạn xem các mẫu đang bán tại " + sp + " nhé.";
        }
        StringBuilder sb = new StringBuilder("Mình tìm thấy ").append(found.size() > 4 ? "nhiều" : String.valueOf(found.size()))
                .append(" mẫu ").append(esc(want)).append(":<br><br>");
        int shown = 0;
        for (String name : found) {
            if (shown >= 4) break;
            appendProduct(sb, name, all.get(name));
            shown++;
        }
        if (found.size() > shown) {
            sb.append("Còn ").append(found.size() - shown).append(" mẫu khác, xem tại ").append(sp).append(".<br>");
        }
        if (!missing.isEmpty()) {
            sb.append("Shop <b>chưa nhập</b> áo ").append(esc(join(missing))).append('.');
        }
        return sb.toString();
    }

    // Từ thông dụng không phải tên đội/sản phẩm (dùng cho unknownNameTokens)
    private static final Set<String> FUNC = new HashSet<String>(Arrays.asList(
            ("chua roi dang se nhe vay thi nay kia do day di hoi tim xem muon can giup ve tu den duoc tot dep nhat moi cu "
            + "em anh chi bi ra vao len xuong cua cai chiec nao sao ai dau rach hong bung bac phai nhao noi tieng hot hay re dat").split(" ")));

    /** Từ lạ (không phải từ khóa chung / từ trong FAQ) đứng ngay sau "áo / retro / CLB / ĐTQG" - thường là tên đội chưa biết. */
    static List<String> unknownNameTokens(String n) {
        Set<String> known = ChatbotEngine.faqWords();
        List<String> out = new ArrayList<String>();
        boolean after = false;
        for (String t : n.split(" ")) {
            if (t.equals("ao") || t.equals("retro") || t.equals("clb") || t.equals("dtqg")) { after = true; continue; }
            if (!after) continue;
            boolean noise = t.length() < 2 || t.matches("\\d+") || GENERIC.contains(t) || FUNC.contains(t) || known.contains(t);
            if (noise) { if (!out.isEmpty()) break; continue; }
            out.add(t);
        }
        return out.size() > 3 ? Collections.<String>emptyList() : out;
    }

    /** Lấy lại đúng chữ khách gõ (có dấu) của các từ đã chuẩn hóa trong left, để hiển thị cho dễ đọc. */
    private static String displayWords(String question, List<String> left) {
        if (left.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String raw : question.trim().split("\\s+")) {
            String r = raw.replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
            if (!r.isEmpty() && left.contains(ChatbotEngine.normalize(r))) sb.append(sb.length() > 0 ? " " : "").append(r);
        }
        return sb.length() > 0 ? sb.toString() : join(left);
    }

    /**
     * Câu hỏi "áo + một cái tên lạ" không có trong database và cũng không thuộc FAQ
     * -> báo shop chưa nhập sản phẩm đó. Trả null nếu không giống câu hỏi tìm sản phẩm.
     */
    public static String unknownProductAnswer(String question) {
        String n = ChatbotEngine.normalize(question);
        if (!Arrays.asList(n.split(" ")).contains("ao") || n.split(" ").length > 10) return null;
        List<String> left = unknownNameTokens(n);
        if (left.isEmpty()) return null;
        return "Hiện shop <b>chưa nhập</b> sản phẩm áo <b>" + esc(displayWords(question, left)) + "</b> nên mình chưa có thông tin về mẫu này. "
                + "Bạn xem các mẫu đang bán tại " + a("/nguoidungservlet", "trang Sản phẩm") + " nhé.";
    }

    // ------------------------------------------------------------ đơn hàng
    private static final String[] ORDER_KEYS = {
        "don hang cua toi", "don cua toi", "don hang cua minh", "don cua minh", "don toi da dat",
        "don hang toi da dat", "toi da dat", "trang thai don", "tinh trang don", "don hang toi", "lich su mua"
    };

    static boolean isOrderQuestion(String normalized) {
        for (String k : ORDER_KEYS) if (normalized.contains(k)) return true;
        return false;
    }

    static String statusLabel(int tinhtrang) {
        switch (tinhtrang) {
            case 0: return "Chờ xác nhận";
            case 1: return "Đang giao";
            case 4: return "Đã giao – chờ bạn xác nhận";
            case 2: return "Đã nhận";
            default: return "Đã hủy";
        }
    }

    private static String orderAnswer(String email) {
        String orders = a("/trangthaiservlet", "Đơn hàng");
        if (email == null || email.trim().isEmpty()) {
            return "Bạn cần " + a("/view/dangnhap.jsp", "đăng nhập") + " để mình tra cứu đơn hàng của bạn nhé. "
                    + "Chưa có tài khoản? " + a("/view/dangky.jsp", "Đăng ký") + " chỉ mất ít phút.";
        }
        List<TrangThai> list;
        try {
            list = new TrangThaiDAO().getDanhSachDonHang(email);
        } catch (Exception e) {
            e.printStackTrace();
            list = new ArrayList<TrangThai>();
        }
        if (list == null || list.isEmpty()) {
            return "Bạn chưa có đơn hàng nào. Xem sản phẩm tại " + a("/nguoidungservlet", "trang Sản phẩm") + " nhé!";
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder("Các đơn gần đây của bạn:<br>");
        int shown = 0;
        for (TrangThai t : list) {
            if (shown >= 5) break;
            String ten = t.getTensanpham() == null ? t.getMasanpham() : t.getTensanpham();
            sb.append("• ");
            if (t.getMadonhang() > 0) sb.append("<b>#").append(t.getMadonhang()).append("</b> · ");
            sb.append(esc(ten == null ? "" : ten));
            if (t.getKichco() != null && !t.getKichco().isEmpty()) sb.append(" (size ").append(esc(t.getKichco())).append(')');
            sb.append(" ×").append(t.getSoluong());
            if (t.getNgaymua() != null) sb.append(" · ").append(df.format(t.getNgaymua()));
            sb.append(" — <i>").append(statusLabel(t.getTinhtrang())).append("</i><br>");
            shown++;
        }
        if (list.size() > shown) sb.append("… và ").append(list.size() - shown).append(" sản phẩm khác.<br>");
        sb.append("Xem đầy đủ và quản lý tại ").append(orders).append('.');
        return sb.toString();
    }

    // ------------------------------------------------------------ sản phẩm
    /** Tách riêng (nhận Map) để kiểm thử được mà không cần database. */
    static String productAnswer(Map<String, List<nguoidung>> all, String question) {
        if (all == null || all.isEmpty()) return null;
        Set<String> qw = new HashSet<String>(Arrays.asList(ChatbotEngine.normalize(question).split(" ")));

        // từ chỉ xuất hiện ở vài sản phẩm là từ "đặc trưng" (thường là tên đội) -> chỉ cần khớp 1 từ
        Map<String, Integer> df = new HashMap<String, Integer>();
        for (String name : all.keySet()) {
            for (String t : new HashSet<String>(Arrays.asList(ChatbotEngine.normalize(name).split(" ")))) {
                Integer c = df.get(t);
                df.put(t, c == null ? 1 : c + 1);
            }
        }
        int rare = Math.max(2, all.size() / 4);

        final Map<String, double[]> score = new LinkedHashMap<String, double[]>();
        for (String name : all.keySet()) {
            Set<String> tokens = new LinkedHashSet<String>(Arrays.asList(ChatbotEngine.normalize(name).split(" ")));
            int total = 0, hit = 0;
            boolean strong = false;
            for (String t : tokens) {
                if (t.isEmpty() || GENERIC.contains(t)) continue;
                total++;
                if (qw.contains(t)) {
                    hit++;
                    if (t.length() >= 4 && !t.matches("\\d+") && df.get(t) <= rare) strong = true;
                }
            }
            if (hit >= 2 || (hit >= 1 && total <= 2) || strong) {
                score.put(name, new double[]{hit, total == 0 ? 0 : (double) hit / total});
            }
        }
        if (score.isEmpty()) return null;

        List<String> names = new ArrayList<String>(score.keySet());
        Collections.sort(names, new Comparator<String>() {
            public int compare(String x, String y) {
                double[] sx = score.get(x), sy = score.get(y);
                if (sx[0] != sy[0]) return Double.compare(sy[0], sx[0]);
                return Double.compare(sy[1], sx[1]);
            }
        });

        StringBuilder sb = new StringBuilder("Mình tìm thấy ").append(names.size() > 4 ? "nhiều" : String.valueOf(names.size()))
                .append(" mẫu phù hợp:<br><br>");
        int shown = 0;
        for (String name : names) {
            if (shown >= 4) break;
            appendProduct(sb, name, all.get(name));
            shown++;
        }
        if (names.size() > shown) {
            sb.append("Còn ").append(names.size() - shown).append(" mẫu khác, xem tại ")
              .append(a("/nguoidungservlet", "trang Sản phẩm")).append(" (gõ tên vào ô tìm kiếm).");
        }
        return sb.toString();
    }

    private static void appendProduct(StringBuilder sb, String name, List<nguoidung> variants) {
        int min = Integer.MAX_VALUE, max = 0;
        List<String> inStock = new ArrayList<String>(), out = new ArrayList<String>();
        for (nguoidung v : variants) {
            if (v.getGiasanpham() > 0) {
                min = Math.min(min, v.getGiasanpham());
                max = Math.max(max, v.getGiasanpham());
            }
            String size = v.getKichco() == null ? "" : v.getKichco().trim();
            boolean ok = "1".equals(v.getTrangthai()) && v.getSoluongsanpham() > 0;
            if (ok) inStock.add(size); else out.add(size);
        }
        sortSizes(inStock);
        sortSizes(out);
        sb.append("<b>").append(esc(name)).append("</b><br>");
        if (max > 0) {
            sb.append("Giá: ").append(money(min));
            if (max != min) sb.append(" – ").append(money(max));
            sb.append("<br>");
        }
        if (inStock.isEmpty()) {
            sb.append("Hiện <b>hết hàng</b> hoặc ngừng bán.<br>");
        } else {
            sb.append("Size còn hàng: ").append(esc(join(inStock)));
            if (!out.isEmpty()) sb.append(" · Hết: ").append(esc(join(out)));
            sb.append("<br>");
        }
        sb.append(a("/chitietsanpham?ma=" + enc(variants.get(0).getMasanpham()), "Xem chi tiết")).append("<br><br>");
    }

    // ------------------------------------------------------------ tiện ích
    private static void sortSizes(List<String> sizes) {
        final List<String> order = Arrays.asList(SIZE_ORDER);
        Collections.sort(sizes, new Comparator<String>() {
            public int compare(String x, String y) {
                int ix = order.indexOf(x.toUpperCase()), iy = order.indexOf(y.toUpperCase());
                if (ix < 0) ix = 99;
                if (iy < 0) iy = 99;
                return ix != iy ? Integer.compare(ix, iy) : x.compareTo(y);
            }
        });
    }

    private static String join(List<String> l) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(l.get(i));
        }
        return sb.toString();
    }

    static String money(int v) {
        return String.format(java.util.Locale.ROOT, "%,d", v).replace(',', '.') + "đ";
    }

    private static String enc(String s) {
        try {
            return java.net.URLEncoder.encode(s == null ? "" : s, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return "";
        }
    }

    /** Escape HTML: dữ liệu từ database luôn được escape trước khi chèn vào câu trả lời. */
    public static String esc(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
