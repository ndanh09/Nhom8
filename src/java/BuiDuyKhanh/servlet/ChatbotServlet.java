package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ChatHoiThoai;
import modeldao.ChatDAO;
import util.ChatbotEngine;
import util.ChatbotLive;

/**
 * Màn hình CHATBOT riêng của LODI.
 * Lịch sử chat được lưu vào database (bảng chat_hoithoai / chat_tinnhan - xem migration_chat.sql)
 * để admin xem lại, trả lời khách và tắt chatbot riêng cho từng khách ở trang "Quản lý trò chuyện".
 * Nếu chưa tạo bảng (DB lỗi) thì tạm lưu lịch sử trong HttpSession như bản cũ.
 *
 *   GET  /chatbot                 -> hiển thị màn hình chat (forward /view/chatbot.jsp)
 *   GET  /chatbot?group=sp        -> chọn chủ đề, hiện danh sách câu hỏi của chủ đề
 *   GET  /chatbot?action=groups   -> quay lại danh sách chủ đề
 *   GET  /chatbot?action=reset    -> bắt đầu cuộc trò chuyện mới (khách không thấy tin cũ; admin vẫn thấy)
 *   GET  /chatbot?action=poll&after=ID -> JSON các tin mới (admin trả lời / chatbot bị tắt), trang chat gọi định kỳ
 *   POST /chatbot  (q=câu hỏi)    -> lưu tin của khách; nếu chatbot đang bật thì trả lời (sản phẩm/đơn hàng từ DB, hoặc FAQ)
 */
@WebServlet(name = "ChatbotServlet", urlPatterns = {"/chatbot"})
public class ChatbotServlet extends HttpServlet {

    private static final int MAX_LEN = 200;
    private static final int MAX_MSG = 40;
    private static final String S_HIST = "lodiChatHist";   // dự phòng khi DB lỗi: List<String[]>{who, html}
    private static final String S_SUG = "lodiChatSug";     // List<String> gợi ý đang hiển thị
    private static final String VIEW = "/view/chatbot.jsp";
    private static final String WELCOME = "Xin chào! 👋 Mình là trợ lý của <b>LODI AUTHENTIC STORE</b>. "
            + "Bạn chọn một chủ đề hoặc gõ câu hỏi nhé — mình tra giá, size, tình trạng hàng và đơn hàng của bạn được luôn.";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession ss = req.getSession(true);
        String home = req.getContextPath() + "/chatbot#chatbox";
        String action = req.getParameter("action");
        String group = req.getParameter("group");

        if ("poll".equals(action)) {
            poll(req, resp, ss);
            return;
        }
        if ("reset".equals(action)) {
            ss.removeAttribute(S_HIST);
            ss.removeAttribute(S_SUG);
            try {
                ChatHoiThoai h = hoiThoai(ss, false);
                if (h != null) ChatDAO.datMoc(h.getId(), ChatDAO.idMoiNhat(h.getId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resp.sendRedirect(home);
            return;
        }
        if ("groups".equals(action)) {
            ss.removeAttribute(S_SUG);
            resp.sendRedirect(home);
            return;
        }
        if (group != null && ChatbotEngine.GROUPS.containsKey(group) && botDangBat(ss)) {
            luu(ss, "u", esc(ChatbotEngine.GROUPS.get(group)));
            luu(ss, "b", "Bạn muốn hỏi mục nào ạ?");
            ss.setAttribute(S_SUG, new ArrayList<String>(ChatbotEngine.questionsOf(group)));
            resp.sendRedirect(home);
            return;
        }

        // ---- hiển thị: lấy lịch sử từ DB (kèm tin admin trả lời); lỗi DB thì dùng danh sách trong session
        List<String[]> hist = new ArrayList<String[]>();   // {who, html, id}   who: u | b | a
        boolean botBat = true, live = false;
        long lastId = 0;
        try {
            ChatHoiThoai h = hoiThoai(ss, false);
            if (h != null) {
                live = true;
                botBat = h.isBotBat();
                for (String[] m : ChatDAO.tinNhan(h.getId(), h.getKhachTuId(), MAX_MSG)) {
                    hist.add(new String[]{who(m[1]), m[2], m[0]});
                    lastId = Long.parseLong(m[0]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            hist = history(ss);
        }
        if (hist.isEmpty()) hist.add(new String[]{"b", WELCOME, "0"});

        req.setAttribute("chatMsgs", hist);                       // List<String[]> {who, html, id}
        req.setAttribute("chatSug", ss.getAttribute(S_SUG));      // List<String> hoặc null
        req.setAttribute("chatGroups", ChatbotEngine.GROUPS);     // Map<key, tên chủ đề>
        req.setAttribute("chatBot", botBat);                      // false = admin đã tắt chatbot với khách này
        req.setAttribute("chatLive", live);                       // đã có cuộc trò chuyện trong DB -> trang chat kiểm tra tin mới
        req.setAttribute("chatLastId", lastId);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession ss = req.getSession(true);
        String ctx = req.getContextPath();
        String q = req.getParameter("q");
        q = q == null ? "" : q.trim();
        if (!q.isEmpty()) {
            if (q.length() > MAX_LEN) q = q.substring(0, MAX_LEN);
            ChatHoiThoai h = luu(ss, "u", esc(q));
            boolean botBat = h == null || h.isBotBat();   // admin đã tắt chatbot với khách này -> chỉ lưu tin, chờ admin trả lời
            if (botBat) {
                String email = email(ss);
                String live = null;
                try {
                    live = ChatbotLive.tryAnswer(q, email);   // dữ liệu thật: sản phẩm / đơn hàng
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (live != null) {
                    luu(ss, "b", live.replace("{ctx}", ctx));
                    ss.removeAttribute(S_SUG);               // hiện lại danh sách chủ đề
                } else {
                    ChatbotEngine.Reply r = ChatbotEngine.ask(q);
                    // FAQ không hiểu + câu hỏi có dạng "áo <tên lạ>" -> báo shop chưa nhập sản phẩm đó
                    String nf = r.matched ? null : ChatbotLive.unknownProductAnswer(q);
                    if (nf != null) {
                        luu(ss, "b", nf.replace("{ctx}", ctx));
                        ss.removeAttribute(S_SUG);
                    } else {
                        luu(ss, "b", r.answerHtml.replace("{ctx}", ctx));
                        ss.setAttribute(S_SUG, new ArrayList<String>(r.suggestions));
                    }
                }
            } else {
                ss.removeAttribute(S_SUG);
            }
        }
        resp.sendRedirect(ctx + "/chatbot#chatbox");   // Post-Redirect-Get: F5 không gửi lại câu hỏi
    }

    // ---------------------------------------------------------------- poll (JSON)
    /** Trả các tin mới của cuộc trò chuyện này (id > after) và trạng thái bật/tắt chatbot. */
    private void poll(HttpServletRequest req, HttpServletResponse resp, HttpSession ss) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        long after = 0;
        try {
            after = Long.parseLong(req.getParameter("after"));
        } catch (Exception ignore) {
        }
        boolean bot = true;
        StringBuilder msgs = new StringBuilder();
        try {
            ChatHoiThoai h = hoiThoai(ss, false);
            if (h != null) {
                bot = h.isBotBat();
                for (String[] m : ChatDAO.tinNhan(h.getId(), Math.max(after, h.getKhachTuId()), 50)) {
                    if (msgs.length() > 0) msgs.append(',');
                    msgs.append("{\"id\":").append(m[0]).append(",\"who\":\"").append(who(m[1]))
                            .append("\",\"html\":\"").append(json(m[2])).append("\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.getWriter().write("{\"bot\":" + bot + ",\"msgs\":[" + msgs + "]}");
    }

    // ---------------------------------------------------------------- helpers
    /** Khách đang đăng nhập (admin không tính). Session "email" do servlet dangnhap đặt. */
    private static String email(HttpSession ss) {
        return Boolean.TRUE.equals(ss.getAttribute("isAdmin")) ? null : (String) ss.getAttribute("email");
    }

    /** Mỗi khách đã đăng nhập có 1 cuộc trò chuyện theo email; khách chưa đăng nhập theo mã phiên. */
    private static String khoa(HttpSession ss) {
        String e = email(ss);
        return e != null ? e.trim().toLowerCase() : "khach:" + ss.getId();
    }

    private static ChatHoiThoai hoiThoai(HttpSession ss, boolean tao) throws SQLException {
        String e = email(ss);
        return tao ? ChatDAO.layHoacTao(khoa(ss), e, "Khách vãng lai") : ChatDAO.theoKhoa(khoa(ss));
    }

    private static boolean botDangBat(HttpSession ss) {
        try {
            ChatHoiThoai h = hoiThoai(ss, false);
            return h == null || h.isBotBat();
        } catch (SQLException e) {
            return true;
        }
    }

    /**
     * Lưu 1 tin (who: u | b) vào DB để admin xem được; DB lỗi thì tạm lưu trong session.
     * Trả về cuộc trò chuyện (null nếu DB lỗi).
     */
    private static ChatHoiThoai luu(HttpSession ss, String who, String html) {
        try {
            ChatHoiThoai h = hoiThoai(ss, true);
            ChatDAO.themTinNhan(h.getId(), "u".equals(who) ? "khach" : "bot", html);
            return h;
        } catch (SQLException e) {
            e.printStackTrace();
            List<String[]> hist = history(ss);
            hist.add(new String[]{who, html});
            while (hist.size() > MAX_MSG) hist.remove(0);
            return null;
        }
    }

    /** Mã người gửi trong DB -> class hiển thị: u (khách) | b (chatbot) | a (nhân viên). */
    private static String who(String nguoigui) {
        return "khach".equals(nguoigui) ? "u" : ("admin".equals(nguoigui) ? "a" : "b");
    }

    @SuppressWarnings("unchecked")
    private static List<String[]> history(HttpSession ss) {
        List<String[]> h = (List<String[]>) ss.getAttribute(S_HIST);
        if (h == null) {
            h = new ArrayList<String[]>();
            ss.setAttribute(S_HIST, h);
        }
        return h;
    }

    /** Tin nhắn của khách được escape HTML trước khi lưu; câu trả lời của bot là HTML do chính mình viết. */
    private static String esc(String s) {
        return ChatbotLive.esc(s);
    }

    private static String json(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }
}
