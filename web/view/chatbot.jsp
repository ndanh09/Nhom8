<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- MÀN HÌNH CHATBOT LODI (thuần Java: ChatbotServlet xử lý, trang này chỉ hiển thị bằng JSTL).
     Mở qua /chatbot (nút "Chat với trợ lý" ở trang Sản phẩm). Nếu mở thẳng file này thì tự chuyển sang /chatbot. --%>
<c:if test="${empty chatMsgs}"><c:redirect url="/chatbot"/></c:if>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Trợ lý LODI - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${cp}/image/favicon.png">
<link rel="stylesheet" href="${cp}/css/fonts.css">
<link rel="stylesheet" href="${cp}/css/lodi.css">
<link rel="stylesheet" href="${cp}/css/lodi-extra.css">
<link rel="stylesheet" href="${cp}/css/lodi-chatbot.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="sanpham"/></jsp:include>
<main class="container">
  <div class="page-head chat-page-head"><h2 class="section-title">Trợ lý LODI<small>HỖ TRỢ KHÁCH HÀNG</small></h2><p>Hỏi về giá, size, tình trạng hàng, đơn hàng, mã giảm giá, vận chuyển, đổi trả...</p></div>

  <div class="chat-layout">
    <section class="chat-box${chatBot ? '' : ' human'}" id="chatbox">
      <div class="chat-head">
        <div class="chat-avatar">L</div>
        <div><b>Trợ lý LODI</b><small>LODI Authentic Store</small></div>
        <a class="chat-reset" href="${cp}/chatbot?action=reset">Cuộc trò chuyện mới</a>
      </div>
      <%-- Hiện khi admin đã tắt chatbot với khách này (cũng tự hiện/ẩn theo kiểm tra tin mới bên dưới) --%>
      <div class="chat-banner">👤 Nhân viên LODI đang trực tiếp hỗ trợ bạn nên chatbot tự động đã tạm tắt. Bạn cứ nhắn, nhân viên sẽ trả lời sớm nhất có thể.</div>

      <div class="chat-msgs"><div class="chat-flow">
        <c:forEach var="m" items="${chatMsgs}">
          <%-- m[0] = "u" (khách) | "b" (bot) | "a" (nhân viên LODI trả lời); m[1] = HTML (tin của khách đã được escape ở servlet) --%>
          <div class="chat-msg ${m[0]}">${m[1]}</div>
        </c:forEach>

        <div class="chat-chips">
          <c:choose>
            <c:when test="${not empty chatSug}">
              <c:forEach var="s" items="${chatSug}">
                <form method="post" action="${cp}/chatbot">
                  <input type="hidden" name="q" value="<c:out value='${s}'/>">
                  <button type="submit"><c:out value="${s}"/></button>
                </form>
              </c:forEach>
              <a href="${cp}/chatbot?action=groups">Chủ đề khác</a>
            </c:when>
            <c:otherwise>
              <c:forEach var="g" items="${chatGroups}">
                <a href="${cp}/chatbot?group=<c:out value='${g.key}'/>"><c:out value="${g.value}"/></a>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
      </div></div>

      <form class="chat-in" method="post" action="${cp}/chatbot" autocomplete="off">
        <input type="text" name="q" maxlength="200" placeholder="${chatBot ? 'Nhập câu hỏi, ví dụ: giá áo Real Madrid...' : 'Nhắn cho nhân viên LODI...'}" aria-label="Nhập câu hỏi" autofocus required>
        <button type="submit">Gửi</button>
      </form>
    </section>

    <aside class="chat-side">
      <div class="box">
        <h3>Bạn có thể hỏi</h3>
        <ul>
          <li><b>Sản phẩm:</b> "giá áo Real Madrid", "áo Barcelona còn size M không"</li>
          <li><b>Đơn hàng:</b> "đơn hàng của tôi" (cần đăng nhập)</li>
          <li><b>Cửa hàng:</b> ship, đổi trả, thanh toán, mã giảm giá, chọn size</li>
        </ul>
      </div>
      <div class="box">
        <h3>Cần gặp nhân viên?</h3>
        <p>LODI hỗ trợ 24/7, thường phản hồi dưới 5 phút qua Zalo.</p>
        <a href="https://zalo.me/g/gxizmmsudpjibttywgv5" target="_blank" class="btn">Chat Zalo</a>
        <a href="tel:0352328899" class="btn dark">Hotline: 0352 328 899</a>
      </div>
      <a href="${cp}/nguoidungservlet" class="btn dark">&larr; Quay lại Sản phẩm</a>
    </aside>
  </div>
</main>

<jsp:include page="_lodi_footer.jsp"/>
<%-- Script chung của header (menu người dùng, số giỏ hàng, ô tìm kiếm gợi ý) - giống các trang khác, KHÔNG liên quan chatbot --%>
<script>
    const userMenu = document.getElementById("userMenu");
    if (userMenu) {
        userMenu.addEventListener("click", function (e) {
            e.stopPropagation();
            this.classList.toggle("active");
        });
        document.addEventListener("click", function () {
            userMenu.classList.remove("active");
        });
    }

    // Lấy số lượng giỏ hàng thật qua AJAX để badge giỏ hàng khớp với các trang khác.
    (function () {
        const badge = document.getElementById("cartCountBadge");
        if (!badge) { return; }
        fetch("${pageContext.request.contextPath}/nguoidungservlet?action=cartcount")
            .then(r => r.json())
            .then(data => { badge.textContent = data.count || 0; })
            .catch(function () {});
    })();

    // ============ TÌM KIẾM GỢI Ý SẢN PHẨM (dùng chung với các trang khác) ============
    // Màn hình chat không tự có danh sách sản phẩm nên lấy qua AJAX từ
    // nguoidungservlet. Gõ tên -> gợi ý -> bấm gợi ý sẽ nhảy sang trang Sản
    // phẩm (nguoidung) và nhấp nháy vàng đúng sản phẩm đó.
    (function () {
        const searchInput = document.getElementById("searchInput");
        const searchResult = document.getElementById("searchResult");
        if (!searchInput || !searchResult) {
            return;
        }

        let allProductNames = [];
        fetch("${pageContext.request.contextPath}/nguoidungservlet?action=suggest")
            .then(r => r.json())
            .then(data => { allProductNames = data || []; })
            .catch(function () { allProductNames = []; });

        function removeVietnamese(str) {
            return str
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "")
                .toLowerCase();
        }

        searchInput.addEventListener("keyup", function () {
            let keyword = removeVietnamese(this.value.trim());
            searchResult.innerHTML = "";
            if (keyword === "") {
                searchResult.style.display = "none";
                return;
            }
            let count = 0;
            allProductNames.forEach(function (name) {
                let nameSearch = removeVietnamese(name);
                if (nameSearch.includes(keyword)) {
                    let item = document.createElement("div");
                    item.className = "search-item";
                    item.innerHTML = name;
                    item.onclick = function () {
                        window.location.href = "${pageContext.request.contextPath}/nguoidungservlet?highlight=" + encodeURIComponent(name);
                    };
                    searchResult.appendChild(item);
                    count++;
                }
            });
            if (count > 0) {
                searchResult.style.display = "block";
            } else {
                searchResult.innerHTML = '<div class="search-item">Không tìm thấy sản phẩm phù hợp</div>';
                searchResult.style.display = "block";
            }
        });

        document.addEventListener("click", function (e) {
            if (!searchInput.contains(e.target) && !searchResult.contains(e.target)) {
                searchResult.style.display = "none";
            }
        });
    })();
</script>

<%-- Kiểm tra tin mới mỗi 3 giây: nhân viên trả lời hiện ngay, không cần tải lại trang (không mất chữ đang gõ).
     Chỉ chạy khi đã có cuộc trò chuyện. --%>
<c:if test="${chatLive}">
<script>
    (function () {
        var flow = document.querySelector(".chat-flow");
        var box = document.getElementById("chatbox");
        if (!flow || !box) { return; }
        var last = ${chatLastId};
        function tick() {
            if (document.hidden) { return; }
            fetch("${cp}/chatbot?action=poll&after=" + last, { cache: "no-store" })
                .then(function (r) { return r.json(); })
                .then(function (d) {
                    box.classList.toggle("human", d.bot === false);
                    var chips = flow.querySelector(".chat-chips");
                    (d.msgs || []).forEach(function (m) {
                        if (m.id <= last) { return; }
                        last = m.id;
                        var el = document.createElement("div");
                        el.className = "chat-msg " + m.who;
                        el.innerHTML = m.html;
                        flow.insertBefore(el, chips);
                    });
                })
                .catch(function () {});
        }
        setInterval(tick, 3000);
    })();
</script>
</c:if>
</body>
</html>
