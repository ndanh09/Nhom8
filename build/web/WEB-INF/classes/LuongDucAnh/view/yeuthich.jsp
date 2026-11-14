<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- LUỒNG KHÁCH HÀNG: danh sách áo yêu thích của tôi. Dữ liệu do YeuThichServlet (/yeuthichservlet) nạp: dsYeuThich --%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Yêu thích - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-chatbot.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="yeuthich"/></jsp:include>
<main class="container">
  <div class="fav-head">
    <h2 class="section-title">Áo yêu thích của tôi<small>DANH SÁCH BẠN ĐÃ LƯU</small></h2>
    <p><c:out value="${fn:length(dsYeuThich)}"/> sản phẩm</p>
  </div>

  <div class="grid" id="favGrid">
    <c:forEach var="t" items="${dsYeuThich}">
      <div class="card ${t.trangthai=='0'?'disabled-card':''}" data-magoc="${fn:escapeXml(t.magoc)}">
        <a href="${pageContext.request.contextPath}/chitietsanpham?ma=${t.masanpham}" class="thumb">
          <c:choose>
            <c:when test="${t.trangthai == '0'}"><span class="tag out">NGỪNG BÁN</span></c:when>
            <c:when test="${t.tongsoluong <= 0}"><span class="tag out">HẾT HÀNG</span></c:when>
            <c:otherwise><span class="tag">AUTHENTIC</span></c:otherwise>
          </c:choose>
          <img src="${pageContext.request.contextPath}/image/${not empty t.hinhanh ? t.hinhanh : 'default-prod.png'}" alt="${fn:escapeXml(t.tensanpham)}">
        </a>
        <%-- Ở trang này nút tim luôn "đã yêu thích"; bấm = bỏ yêu thích (JS bên dưới, gửi magoc trực tiếp) --%>
        <button type="button" class="fav-btn on fav-remove" data-magoc="${fn:escapeXml(t.magoc)}" aria-label="Bỏ yêu thích" title="Bỏ khỏi danh sách yêu thích">&#9829;</button>
        <div class="info">
          <h3><a href="${pageContext.request.contextPath}/chitietsanpham?ma=${t.masanpham}">${t.tensanpham}</a></h3>
          <div class="sub">${not empty t.danhmuc ? t.danhmuc : 'Authentic'} · Thêm lúc ${t.ngaythem}</div>
          <div class="price"><fmt:formatNumber value="${t.giasanpham}" type="number" groupingUsed="true"/>đ</div>
          <div class="love">&#9829; <span class="fav-count">${t.soluotyeuthich}</span> người yêu thích</div>
          <c:choose>
            <c:when test="${t.trangthai == '0'}"><span class="stock off">Ngừng bán</span></c:when>
            <c:when test="${t.tongsoluong > 0}"><span class="stock in">Còn hàng</span></c:when>
            <c:otherwise><span class="stock out">Hết hàng</span></c:otherwise>
          </c:choose>
          <div class="actions">
            <a href="${pageContext.request.contextPath}/chitietsanpham?ma=${t.masanpham}" class="btn dark">Chi tiết</a>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>
  <p class="grid-empty" id="favEmpty" style="${empty dsYeuThich ? '' : 'display:none;'}">
    Bạn chưa yêu thích áo nào. Bấm biểu tượng &#9829; trên sản phẩm để lưu vào đây.
    <br><a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn" style="display:inline-block;margin-top:14px;width:auto">Xem sản phẩm</a>
  </p>
</main>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script>
  // Ô tìm kiếm ở header: Enter / bấm "Tìm" -> sang trang Sản phẩm và tô sáng sản phẩm cần tìm
  (function () {
    var inp = document.getElementById('searchInput');
    if (!inp) { return; }
    function tim() {
      var v = inp.value.trim();
      if (v) { window.location.href = "${pageContext.request.contextPath}/nguoidungservlet?highlight=" + encodeURIComponent(v); }
    }
    inp.addEventListener('keydown', function (e) { if (e.key === 'Enter') { e.preventDefault(); tim(); } });
    var b = inp.parentNode.querySelector('button');
    if (b) { b.addEventListener('click', tim); }
  })();
  (function () {
    var url = "${pageContext.request.contextPath}/yeuthichservlet";
    document.getElementById('favGrid').addEventListener('click', function (e) {
      var btn = e.target.closest('.fav-remove');
      if (!btn) { return; }
      e.preventDefault();
      btn.disabled = true;
      fetch(url, {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
        body: 'action=toggle&magoc=' + encodeURIComponent(btn.getAttribute('data-magoc'))
      }).then(function (r) { return r.json(); }).then(function (d) {
        if (!d || !d.success) { btn.disabled = false; alert((d && d.message) || 'Có lỗi xảy ra, vui lòng thử lại.'); return; }
        if (d.liked) { btn.disabled = false; return; }          // (hiếm) bấm lại khi đã bỏ -> vừa thêm lại: giữ nguyên thẻ
        var card = btn.closest('.card');
        card.parentNode.removeChild(card);
        var con = document.querySelectorAll('#favGrid .card').length;
        var dem = document.querySelector('.fav-head p');
        if (dem) { dem.textContent = con + ' sản phẩm'; }
        if (con === 0) { document.getElementById('favEmpty').style.display = ''; }
      }).catch(function () { btn.disabled = false; alert('Không kết nối được máy chủ, vui lòng thử lại.'); });
    });
  })();
</script>
</body>
</html>
