<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Liên hệ - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="lienhe"/></jsp:include>
<main class="container">
  <div class="page-head"><h2 class="section-title">Liên hệ với chúng tôi</h2><p>Cần tư vấn size, đặt in tên số áo hay kiểm tra đơn hàng? LODI luôn sẵn sàng hỗ trợ 24/7.</p></div>
  <div class="contact">
    <div class="box">
      <h2>Thông tin cửa hàng</h2>
      <c:if test="${not empty sessionScope.user}"><p class="hello-line">Xin chào, <b>${fn:escapeXml(sessionScope.user.hoten)}</b></p></c:if>
      <ul class="info-list">
        <li><span class="ic">📍</span><div><b>Địa chỉ</b><span>Ngõ 27 Tạ Quang Bửu, Phường Bạch Mai, Hà Nội</span></div></li>
        <li><span class="ic">📞</span><div><b>Hotline</b><span>0352 328 899</span></div></li>
        <li><span class="ic">✉️</span><div><b>Email</b><span>anhldth08719@gmail.com</span></div></li>
        <li><span class="ic">🕘</span><div><b>Giờ làm việc</b><span>08:00 - 23:00, tất cả các ngày trong tuần</span></div></li>
      </ul>
      <div class="map"><img src="${pageContext.request.contextPath}/image/map.svg" alt="Bản đồ vị trí cửa hàng LODI AUTHENTIC STORE"></div>
    </div>
    <div class="box">
      <h2>Kết nối với LODI</h2>
      <p style="color:var(--muted);margin-bottom:14px">Kết nối qua các nền tảng bên dưới để được tư vấn nhanh nhất về áo đấu Authentic, đơn hàng và ưu đãi mới nhất.</p>
      <div class="links-container">
        <a href="https://zalo.me/g/gxizmmsudpjibttywgv5" target="_blank" class="btn">Zalo</a>
        <a href="https://www.facebook.com/duy.khanh.bui.450782/" target="_blank" class="btn dark">Facebook</a>
        <a href="https://www.tiktok.com/@duykhnhbi064" target="_blank" class="btn dark">TikTok</a>
        <a href="https://www.instagram.com/l.ducanh69" target="_blank" class="btn dark">Instagram</a>
        <a href="tel:0352328899" class="btn ghost">Hotline: 0352 328 899</a>
      </div>
      <div class="stat-strip">
        <div class="stat-box"><b>24/7</b><span>Hỗ trợ</span></div>
        <div class="stat-box"><b>&lt;5p</b><span>Phản hồi</span></div>
        <div class="stat-box"><b>100%</b><span>Authentic</span></div>
      </div>
    </div>
  </div>
  <div class="faq">
    <h2 class="section-title">Câu hỏi thường gặp</h2>
    <details><summary>Áo bên LODI có chính hãng không?</summary><p>Tất cả sản phẩm là hàng Authentic, đầy đủ tem mác.</p></details>
    <details><summary>Tôi có thể in tên và số áo không?</summary><p>Có. Hãy liên hệ hotline hoặc Zalo để được hỗ trợ.</p></details>
    <details><summary>Bao lâu tôi nhận được phản hồi?</summary><p>LODI phản hồi trong thời gian sớm nhất, thường dưới 5 phút qua Zalo.</p></details>
  </div>
</main>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
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

    // Trang Liên hệ là trang tĩnh (không qua servlet) nên lấy số lượng giỏ
    // hàng thật qua AJAX để badge giỏ hàng luôn khớp với các trang khác.
    (function () {
        const badge = document.getElementById("cartCountBadge");
        if (!badge) { return; }
        fetch("${pageContext.request.contextPath}/nguoidungservlet?action=cartcount")
            .then(r => r.json())
            .then(data => { badge.textContent = data.count || 0; })
            .catch(function () {});
    })();

    // ============ TÌM KIẾM GỢI Ý SẢN PHẨM (đồng bộ với Sản phẩm / Giỏ hàng /
    // Đơn hàng / Chi tiết sản phẩm / Giới thiệu) ============
    // Trang Liên hệ không tự có danh sách sản phẩm nên lấy qua AJAX từ
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
</body>
</html>
