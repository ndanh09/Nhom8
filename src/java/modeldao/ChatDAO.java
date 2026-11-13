package modeldao;

import connect.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.ChatHoiThoai;

/**
 * Lưu và đọc lịch sử trò chuyện (bảng chat_hoithoai, chat_tinnhan - xem migration_chat.sql).
 * Mọi hàm ném SQLException để nơi gọi tự quyết định xử lý (chatbot có phương án dự phòng).
 */
public class ChatDAO {

    private static final String COLS = "id, khoa, email, tenkhach, bot_bat, chua_doc, tin_cuoi, thoigian_cuoi, khach_tu_id";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    private static Connection conn() throws SQLException {
        Connection c = dbconnect.getConnection();
        if (c == null) throw new SQLException("Không kết nối được CSDL");
        return c;
    }

    private static String gio(Timestamp t) {
        return t == null ? "" : t.toLocalDateTime().format(FMT);
    }

    private static ChatHoiThoai map(ResultSet rs) throws SQLException {
        return new ChatHoiThoai(rs.getInt("id"), rs.getString("khoa"), rs.getString("email"), rs.getString("tenkhach"),
                rs.getBoolean("bot_bat"), rs.getInt("chua_doc"), rs.getString("tin_cuoi"),
                gio(rs.getTimestamp("thoigian_cuoi")), rs.getLong("khach_tu_id"));
    }

    // ------------------------------------------------------------ cuộc trò chuyện
    public static ChatHoiThoai theoKhoa(String khoa) throws SQLException {
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM chat_hoithoai WHERE khoa = ?")) {
            ps.setString(1, khoa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public static ChatHoiThoai theoId(int id) throws SQLException {
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM chat_hoithoai WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Lấy cuộc trò chuyện của khách; chưa có thì tạo mới (họ tên lấy từ bảng dangky nếu khách đã đăng nhập). */
    public static ChatHoiThoai layHoacTao(String khoa, String email, String tenMacDinh) throws SQLException {
        ChatHoiThoai h = theoKhoa(khoa);
        if (h != null) return h;
        String ten = tenMacDinh;
        if (email != null) {
            try (Connection c = conn();
                    PreparedStatement ps = c.prepareStatement("SELECT hoten FROM dangky WHERE email = ?")) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getString(1) != null && !rs.getString(1).trim().isEmpty()) ten = rs.getString(1);
                }
            } catch (SQLException ignore) {
                // không lấy được họ tên thì dùng tên mặc định
            }
        }
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement("INSERT INTO chat_hoithoai (khoa, email, tenkhach) VALUES (?, ?, ?)")) {
            ps.setString(1, khoa);
            ps.setString(2, email);
            ps.setString(3, ten);
            ps.executeUpdate();
        } catch (SQLException e) {
            // trùng khóa do 2 yêu cầu gửi cùng lúc: bên dưới đọc lại là được
        }
        h = theoKhoa(khoa);
        if (h == null) throw new SQLException("Không tạo được cuộc trò chuyện");
        return h;
    }

    /** Danh sách cuộc trò chuyện, mới nhất trước; keyword lọc theo tên / email / tin cuối. */
    public static List<ChatHoiThoai> danhSach(String keyword) throws SQLException {
        boolean loc = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT TOP 300 " + COLS + " FROM chat_hoithoai"
                + (loc ? " WHERE tenkhach LIKE ? OR email LIKE ? OR tin_cuoi LIKE ?" : "")
                + " ORDER BY thoigian_cuoi DESC";
        List<ChatHoiThoai> ds = new ArrayList<ChatHoiThoai>();
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (loc) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ds.add(map(rs));
            }
        }
        return ds;
    }

    public static void datBot(int id, boolean bat) throws SQLException {
        capNhat("UPDATE chat_hoithoai SET bot_bat = ? WHERE id = ?", bat ? 1 : 0, id);
    }

    public static void daDoc(int id) throws SQLException {
        capNhat("UPDATE chat_hoithoai SET chua_doc = 0 WHERE id = ?", id);
    }

    /** Khách bấm "Cuộc trò chuyện mới": từ giờ khách chỉ thấy các tin có id lớn hơn moc. */
    public static void datMoc(int id, long moc) throws SQLException {
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement("UPDATE chat_hoithoai SET khach_tu_id = ? WHERE id = ?")) {
            ps.setLong(1, moc);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private static void capNhat(String sql, int... args) throws SQLException {
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setInt(i + 1, args[i]);
            ps.executeUpdate();
        }
    }

    // ------------------------------------------------------------ tin nhắn
    /**
     * Thêm 1 tin nhắn. nguoigui: "khach" | "bot" | "admin". noidung là HTML đã escape an toàn.
     * Đồng thời cập nhật tóm tắt, giờ, số tin chưa đọc của cuộc trò chuyện.
     */
    public static long themTinNhan(int hoithoaiId, String nguoigui, String noidung) throws SQLException {
        long id = 0;
        try (Connection c = conn()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO chat_tinnhan (hoithoai_id, nguoigui, noidung) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, hoithoaiId);
                ps.setString(2, nguoigui);
                ps.setString(3, noidung);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) id = k.getLong(1);
                }
            }
            String chuaDoc = "khach".equals(nguoigui) ? "chua_doc + 1" : ("admin".equals(nguoigui) ? "0" : "chua_doc");
            try (PreparedStatement ps = c.prepareStatement("UPDATE chat_hoithoai SET tin_cuoi = ?, thoigian_cuoi = GETDATE(), chua_doc = "
                    + chuaDoc + " WHERE id = ?")) {
                ps.setString(1, tomTat(nguoigui, noidung));
                ps.setInt(2, hoithoaiId);
                ps.executeUpdate();
            }
        }
        return id;
    }

    /**
     * Tối đa toiDa tin MỚI NHẤT có id > sauId, xếp từ cũ đến mới.
     * Mỗi phần tử: {id, nguoigui, noidung(HTML), giờ}.
     */
    public static List<String[]> tinNhan(int hoithoaiId, long sauId, int toiDa) throws SQLException {
        List<String[]> ds = new ArrayList<String[]>();
        String sql = "SELECT TOP " + Math.max(1, toiDa) + " id, nguoigui, noidung, thoigian FROM chat_tinnhan "
                + "WHERE hoithoai_id = ? AND id > ? ORDER BY id DESC";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, hoithoaiId);
            ps.setLong(2, sauId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("nguoigui"),
                        rs.getString("noidung"), gio(rs.getTimestamp("thoigian"))});
                }
            }
        }
        Collections.reverse(ds);
        return ds;
    }

    /** id của tin nhắn mới nhất trong cuộc trò chuyện (0 nếu chưa có). */
    public static long idMoiNhat(int hoithoaiId) throws SQLException {
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement("SELECT ISNULL(MAX(id), 0) FROM chat_tinnhan WHERE hoithoai_id = ?")) {
            ps.setInt(1, hoithoaiId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    /** id tin nhắn mới nhất của MỌI cuộc trò chuyện - admin hỏi định kỳ, đổi số = có tin mới. */
    public static long idMoiNhatTatCa() throws SQLException {
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement("SELECT ISNULL(MAX(id), 0) FROM chat_tinnhan");
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    // ------------------------------------------------------------ tiện ích
    private static String tomTat(String nguoigui, String html) {
        String t = html.replaceAll("(?i)<br\\s*/?>", " ").replaceAll("<[^>]*>", "")
                .replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&")
                .replaceAll("\\s+", " ").trim();
        if (t.length() > 100) t = t.substring(0, 100) + "...";
        String nhan = "khach".equals(nguoigui) ? "Khách: " : ("admin".equals(nguoigui) ? "Admin: " : "Bot: ");
        return nhan + t;
    }
}
