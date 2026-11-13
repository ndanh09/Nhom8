package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Danh sách câu lạc bộ / đội tuyển quốc gia để chatbot nhận ra TÊN ĐỘI trong câu hỏi
 * (vd "áo barca", "mu", "áo retro Brazil", "áo Việt Nam").
 * Muốn thêm đội: thêm một dòng club(...) hoặc nation(...) ở khối static bên dưới.
 * Tên gọi (alias) viết KHÔNG DẤU, chữ thường.
 */
public final class ChatbotTeams {

    private ChatbotTeams() {
    }

    public static final class Team {
        public final String name;      // tên hiển thị
        final String[] aliases;        // các cách gọi (không dấu, chữ thường)

        Team(String name, String... aliases) {
            this.name = name;
            this.aliases = aliases;
        }
    }

    private static final List<Object[]> ALIASES = new ArrayList<Object[]>();   // {alias, Team}

    /** Đội tuyển có tên tiếng Việt 1 từ (Pháp, Đức...): chỉ nhận khi đi kèm "áo", "retro" hoặc "tuyển" để khỏi nhầm với từ thường. */
    private static void nation(String name, String vi, String... others) {
        String[] al = Arrays.copyOf(others, others.length + 3);
        al[others.length] = "ao " + vi;
        al[others.length + 1] = "retro " + vi;
        al[others.length + 2] = "tuyen " + vi;
        team(name, al);
    }

    private static void team(String name, String... aliases) {
        Team t = new Team(name, aliases);
        for (String a : aliases) ALIASES.add(new Object[]{a, t});
    }

    static {
        // ---- Câu lạc bộ ----
        team("Real Madrid", "real madrid", "real", "madrid");
        team("Barcelona", "barcelona", "barca", "fc barcelona");
        team("Atletico Madrid", "atletico madrid", "atletico");
        team("Manchester United", "manchester united", "man united", "man utd", "mu", "mun");
        team("Manchester City", "manchester city", "man city", "mancity", "mci");
        team("Liverpool", "liverpool");
        team("Chelsea", "chelsea");
        team("Arsenal", "arsenal", "phao thu");
        team("Tottenham", "tottenham", "spurs");
        team("Newcastle", "newcastle");
        team("Aston Villa", "aston villa");
        team("West Ham", "west ham");
        team("Bayern Munich", "bayern munich", "bayern munchen", "bayern");
        team("Borussia Dortmund", "borussia dortmund", "dortmund", "bvb");
        team("Bayer Leverkusen", "bayer leverkusen", "leverkusen");
        team("RB Leipzig", "rb leipzig", "leipzig");
        team("Juventus", "juventus", "juve");
        team("AC Milan", "ac milan", "acm", "milan");
        team("Inter Milan", "inter milan", "inter");
        team("Inter Miami", "inter miami", "miami");
        team("Napoli", "napoli");
        team("AS Roma", "as roma", "roma");
        team("Lazio", "lazio");
        team("Atalanta", "atalanta");
        team("Paris Saint-Germain", "paris saint germain", "paris sg", "psg", "paris");
        team("Marseille", "marseille");
        team("Lyon", "lyon");
        team("Monaco", "monaco");
        team("Ajax", "ajax");
        team("Benfica", "benfica");
        team("Porto", "porto");
        team("Sporting Lisbon", "sporting lisbon", "sporting");
        team("Al Nassr", "al nassr", "nassr");
        team("Galatasaray", "galatasaray");
        // ---- Đội tuyển quốc gia ----
        team("Việt Nam", "viet nam", "vietnam");
        team("Brazil", "brazil", "brasil");
        team("Argentina", "argentina");
        nation("Pháp", "phap", "france");
        nation("Đức", "duc", "germany", "deutschland");
        nation("Anh", "anh", "england");
        team("Ý", "italy", "azzurri", "tuyen y", "retro y");   // không nhận "áo y" vì dễ nhầm
        team("Tây Ban Nha", "tay ban nha", "spain");
        team("Bồ Đào Nha", "bo dao nha", "portugal");
        team("Hà Lan", "ha lan", "netherlands", "holland");
        team("Bỉ", "belgium", "tuyen bi", "retro bi");   // không nhận "áo bi" vì trùng "áo bị"
        team("Croatia", "croatia");
        team("Nhật Bản", "nhat ban", "japan");
        team("Hàn Quốc", "han quoc", "korea");
        team("Thái Lan", "thai lan", "thailand");
        team("Indonesia", "indonesia");
        team("Trung Quốc", "trung quoc", "china");
        nation("Mỹ", "my", "usa", "united states");
        team("Mexico", "mexico");
        team("Uruguay", "uruguay");
        team("Colombia", "colombia");
        team("Maroc", "maroc", "morocco");

        // Cụm dài khớp trước (vd "inter milan" trước "milan", "atletico madrid" trước "madrid")
        Collections.sort(ALIASES, new Comparator<Object[]>() {
            public int compare(Object[] x, Object[] y) {
                return ((String) y[0]).length() - ((String) x[0]).length();
            }
        });
    }

    /** Các đội được nhắc tới trong chuỗi đã chuẩn hóa (ChatbotEngine.normalize). */
    public static List<Team> mentioned(String normalized) {
        List<Team> out = new ArrayList<Team>();
        String s = " " + normalized + " ";
        for (Object[] e : ALIASES) {
            String key = " " + e[0] + " ";
            if (s.contains(key)) {
                Team t = (Team) e[1];
                if (!out.contains(t)) out.add(t);
                s = s.replace(key, " # ");   // đã dùng cụm này, các alias ngắn hơn không khớp lại
            }
        }
        return out;
    }

    private static boolean has(String normalized, String... phrases) {
        String s = " " + normalized + " ";
        for (String p : phrases) if (s.contains(" " + p + " ")) return true;
        return false;
    }

    /** Khách có hỏi áo Retro không. */
    public static boolean wantsRetro(String normalized) {
        return has(normalized, "retro", "vintage", "co dien", "classic");
    }

    /** "CLB" / "DTQG" nếu câu hỏi nhắc đúng một loại; null nếu không nhắc hoặc nhắc cả hai. */
    public static String categoryWord(String normalized) {
        boolean clb = has(normalized, "clb", "cau lac bo");
        boolean nat = has(normalized, "dtqg", "doi tuyen quoc gia", "tuyen quoc gia", "doi tuyen");
        if (clb == nat) return null;
        return clb ? "CLB" : "DTQG";
    }

    /** Danh mục của sản phẩm (cột danhmuc): "RETRO", "CLB", "DTQG" hoặc "". Danh mục Retro chứa cả chữ CLB và ĐTQG nên xét Retro trước. */
    public static String categoryOf(String danhmuc) {
        String d = ChatbotEngine.normalize(danhmuc == null ? "" : danhmuc);
        if (d.contains("retro")) return "RETRO";
        if (d.contains("dtqg")) return "DTQG";
        if (d.contains("clb")) return "CLB";
        return "";
    }

    private static final Set<String> SHIRT_CONTEXT = new HashSet<String>(Arrays.asList(
            "ao", "dtqg", "clb", "retro", "mau", "gia", "size", "mua", "tuyen"));

    /** Câu có từ cho thấy đang hỏi về áo (hoặc câu rất ngắn, vd chỉ gõ tên đội). */
    public static boolean looksLikeShirtQuestion(String normalized) {
        String[] w = normalized.split(" ");
        if (w.length <= 4) return true;
        for (String t : w) if (SHIRT_CONTEXT.contains(t)) return true;
        return false;
    }
}
