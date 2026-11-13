package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import model.ChatHoiThoai;
import modeldao.ChatDAO;
import util.AdminGuard;
import util.ChatbotLive;

/**
 * Trang admin "Quản lý trò chuyện".
 *
 *   GET  /quanlytrochuyen[?id=..&keyword=..]  -> danh sách cuộc trò chuyện + xem lịch sử chat của 1 khách
 *   POST /quanlytrochuyen  action=reply       -> admin trả lời khách (tuỳ chọn tatbot=1: tắt chatbot với khách này sau khi gửi)
 *   POST /quanlytrochuyen  action=bot&bat=0|1 -> tắt / bật chatbot riêng cho khách này
 *
 * Khi chatbot bị tắt, tin nhắn của khách vẫn được lưu nhưng chatbot không trả lời; chỉ admin trả lời.
 */
@WebServlet("/quanlytrochuyen")
public class QuanLyTroChuyenServlet extends HttpServlet {

    private static final int MAX_LEN = 1000;
    private static final int MAX_MSG = 300;
    private static final String VIEW = "/view/quanlytrochuyen.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("poll".equals(req.getParameter("action"))) {   // trang chat gọi định kỳ: có tin mới chưa?
            if (!AdminGuard.laDoanhThu(req)) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            long sig = 0;
            try {
                sig = ChatDAO.idMoiNhatTatCa();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resp.setContentType("application/json;charset=UTF-8");
            resp.setHeader("Cache-Control", "no-store");
            resp.getWriter().write("{\"sig\":" + sig + "}");
            return;
        }
        if (!AdminGuard.kiemTraDoanhThu(req, resp)) return;
        req.setCharacterEncoding("UTF-8");
        String keyword = req.getParameter("keyword");
        int id = parseId(req.getParameter("id"));
        try {
            req.setAttribute("chatSig", ChatDAO.idMoiNhatTatCa());
            if (id > 0) ChatDAO.daDoc(id);                 // mở cuộc trò chuyện = đã đọc
            List<ChatHoiThoai> ds = ChatDAO.danhSach(keyword);
            req.setAttribute("dsHoiThoai", ds);
            if (id > 0) {
                ChatHoiThoai h = ChatDAO.theoId(id);
                if (h != null) {
                    req.setAttribute("hoiThoai", h);
                    req.setAttribute("tinNhan", ChatDAO.tinNhan(id, 0, MAX_MSG));   // {id, nguoigui, html, giờ}
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("loi", "Chưa đọc được dữ liệu trò chuyện. Bạn đã chạy file migration_chat.sql trong SQL Server chưa? ("
                    + e.getMessage() + ")");
        }
        req.setAttribute("keyword", keyword == null ? "" : keyword);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AdminGuard.kiemTraDoanhThu(req, resp)) return;
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String keyword = req.getParameter("keyword");
        int id = parseId(req.getParameter("id"));
        if (id > 0) {
            try {
                if ("reply".equals(action)) {
                    String nd = req.getParameter("noidung");
                    nd = nd == null ? "" : nd.trim();
                    if (!nd.isEmpty()) {
                        if (nd.length() > MAX_LEN) nd = nd.substring(0, MAX_LEN);
                        // escape HTML rồi đổi xuống dòng thành <br> để hiển thị đúng bên khách
                        String html = ChatbotLive.esc(nd).replace("\r\n", "<br>").replace("\n", "<br>").replace("\r", "<br>");
                        ChatDAO.themTinNhan(id, "admin", html);
                    }
                    if ("1".equals(req.getParameter("tatbot"))) ChatDAO.datBot(id, false);
                } else if ("bot".equals(action)) {
                    ChatDAO.datBot(id, "1".equals(req.getParameter("bat")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        String to = req.getContextPath() + "/quanlytrochuyen" + (id > 0 ? "?id=" + id : "");
        if (keyword != null && !keyword.trim().isEmpty()) {
            to += (id > 0 ? "&" : "?") + "keyword=" + URLEncoder.encode(keyword.trim(), "UTF-8");
        }
        resp.sendRedirect(to);
    }

    private static int parseId(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
