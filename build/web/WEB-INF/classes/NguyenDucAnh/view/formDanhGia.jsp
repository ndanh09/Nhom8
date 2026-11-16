<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đánh giá sản phẩm - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-danhgia.css">
</head>
<body>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="sanpham"/></jsp:include>
<main class="container">
  <c:url var="uSanPham" value="/chitietsanpham"><c:param name="ma" value="${dgMaSanPham}"/></c:url>
  <div class="breadcrumb">
    <a href="${cp}/view/trangchu.jsp">Trang chủ</a> &rsaquo;
    <a href="${cp}/nguoidungservlet">Sản phẩm</a> &rsaquo;
    <a href="${uSanPham}#danhgia"><c:out value="${dgSanPham.tenSanPham}"/></a> &rsaquo;
    <span>Viết đánh giá</span>
  </div>

  <section class="dg-box" style="max-width:760px;margin:24px auto">
    <h2>Đánh giá sản phẩm</h2>

    <div class="dg-product">
      <img src="${cp}/image/${not empty dgSanPham.hinhAnh ? dgSanPham.hinhAnh : 'default.png'}" alt="" onerror="this.style.visibility='hidden'">
      <div><b><c:out value="${dgSanPham.tenSanPham}"/></b><br><span class="dg-note">Bạn chọn số sao, viết nhận xét hoặc làm cả hai. Đánh giá không thể sửa hay xóa sau khi gửi.</span></div>
    </div>

    <c:if test="${not empty dgLoiForm}">
      <div class="dg-flash err" role="alert">&#9888; <span><c:out value="${dgLoiForm}"/></span></div>
    </c:if>
    <div class="dg-flash err" id="dgLoiJs" role="alert" style="display:none"></div>

    <form action="${cp}/danhgia" method="post" id="dgForm" novalidate>
      <input type="hidden" name="action" value="gui">
      <input type="hidden" name="ma" value="<c:out value='${dgMaSanPham}'/>">
      <input type="hidden" name="magoc" value="<c:out value='${dgSanPham.maGoc}'/>">

      <div class="dg-field">
        <span class="lbl">Số sao <span class="hint">(không bắt buộc)</span></span>
        <div class="dg-rate-row">
          <div class="dg-rate" role="radiogroup" aria-label="Số sao">
            <c:forEach var="i" begin="1" end="5">
              <c:set var="s" value="${6 - i}"/>
              <input type="radio" name="sosao" id="sao${s}" value="${s}" ${dgSaoDaChon == s ? 'checked' : ''}>
              <label for="sao${s}" title="${s} sao">&#9733;</label>
            </c:forEach>
          </div>
          <span class="dg-rate-text" id="dgRateText"></span>
          <button type="button" class="dg-clear" id="dgClear">Bỏ chọn sao</button>
        </div>
      </div>

      <div class="dg-field">
        <label for="dgNoiDung">Nhận xét <span class="hint">(không bắt buộc)</span></label>
        <textarea id="dgNoiDung" name="noidung" maxlength="${dgToiDaKyTu}" placeholder="Chất vải, form áo, chất lượng in, giao hàng... bạn thấy thế nào?"><c:out value="${dgNoiDungDaNhap}"/></textarea>
        <div class="dg-counter"><span id="dgDem">0</span> / ${dgToiDaKyTu}</div>
      </div>

      <p class="dg-note" style="margin-bottom:16px">Bạn cần chọn <b>số sao</b> hoặc viết <b>nhận xét</b> (ít nhất một trong hai).</p>

      <div class="dg-actions">
        <button type="submit" class="btn">Gửi đánh giá</button>
        <a href="${uSanPham}#danhgia" class="btn dark">Hủy</a>
      </div>
    </form>
  </section>
</main>

<jsp:include page="_lodi_footer.jsp"/>
<script>
(function () {
  var form = document.getElementById('dgForm');
  var ta = document.getElementById('dgNoiDung');
  var dem = document.getElementById('dgDem');
  var txt = document.getElementById('dgRateText');
  var loi = document.getElementById('dgLoiJs');
  var nhan = ['', 'Rất tệ', 'Tệ', 'Bình thường', 'Tốt', 'Tuyệt vời'];
  var radios = form.querySelectorAll('input[name=sosao]');

  function saoDangChon() {
    for (var i = 0; i < radios.length; i++) { if (radios[i].checked) return parseInt(radios[i].value, 10); }
    return 0;
  }
  function capNhat() { txt.textContent = nhan[saoDangChon()] || ''; }
  function dung() { dem.textContent = ta.value.length; }

  for (var i = 0; i < radios.length; i++) { radios[i].addEventListener('change', capNhat); }
  document.getElementById('dgClear').addEventListener('click', function () {
    for (var j = 0; j < radios.length; j++) { radios[j].checked = false; }
    capNhat();
  });
  ta.addEventListener('input', dung);
  capNhat(); dung();

  form.addEventListener('submit', function (e) {
    // Kiểm tra phía trình duyệt cho tiện; server vẫn kiểm tra lại toàn bộ.
    if (saoDangChon() === 0 && ta.value.trim() === '') {
      e.preventDefault();
      loi.textContent = '\u26A0 Vui lòng chọn số sao hoặc viết nhận xét (ít nhất một trong hai).';
      loi.style.display = 'flex';
      loi.scrollIntoView({behavior: 'smooth', block: 'center'});
    }
  });
})();
</script>
</body>
</html>
