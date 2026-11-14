<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Thông tin khách hàng - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="giohang"/></jsp:include>
<main class="container"><div class="form-box wide"><h2>Thông tin khách hàng</h2>
<p class="form-intro">Điền thông tin nhận hàng để hoàn tất đơn hàng của bạn.</p>
<% if(request.getParameter("error")!=null){ %>
<div class="alert error" style="display:flex"><span class="ico">!</span><div>Mã sản phẩm không tồn tại trong hệ thống.</div></div>
<% } %>
<c:if test="${not empty sessionScope.error}">
<div class="alert error" style="display:flex"><span class="ico">!</span><div>${sessionScope.error}</div></div>
<% session.removeAttribute("error"); %>
</c:if>
<form id="formDatHang" action="${pageContext.request.contextPath}/donhangservlet" method="post" onsubmit="return khoaNutDatHang(this);">
  <div class="field"><label>Họ và tên</label><input type="text" name="hoten" placeholder="Nhập họ và tên của bạn" value="${thongTinKH.hoten}" required></div>
  <div class="field"><label>Mã sản phẩm trong giỏ hàng</label>
    <c:forEach var="maSP" items="${listMaSP}">
      <input type="text" value="${maSP}" readonly class="masp-row" style="margin-bottom:6px">
      <input type="hidden" name="masanpham" value="${maSP}">
    </c:forEach>
  </div>
  <div class="field"><label>Số điện thoại</label><input type="text" name="sodienthoai" placeholder="Nhập số điện thoại liên hệ" value="${thongTinKH.sodienthoai}" required></div>
  <div class="field"><label>Email</label><input type="email" name="email" placeholder="Nhập địa chỉ email nhận hóa đơn" value="${sessionScope.email}"></div>
  <div class="field"><label>Tỉnh/Thành phố nhận hàng</label><select name="tinhthanh" required>
      <option value="">-- Chọn Tỉnh/Thành phố --</option>
      <%@ include file="_tinhthanh_options.jspf" %>
    </select></div>
  <div class="field"><label>Địa chỉ nhận hàng</label><textarea name="diachi" placeholder="Nhập số nhà, tên đường, phường/xã, quận/huyện..." required>${thongTinKH.diachi}</textarea></div>
  <div class="field"><label>Ghi chú đơn hàng</label><textarea name="ghichu" placeholder="Ví dụ: Giao giờ hành chính, đóng gói cẩn thận..."></textarea></div>
  <div class="summary" style="box-shadow:none;border:1px solid var(--line);margin:6px 0 14px;padding:14px 18px">
    <div class="row total" style="border:0;padding:0"><span>Tổng thanh toán</span><span><fmt:formatNumber value="${tongTien}" type="number" groupingUsed="true"/>đ</span></div>
  </div>
  <button type="submit" class="btn">Xác nhận đặt hàng ngay</button>
  <p class="note"><a href="${pageContext.request.contextPath}/giohangservlet">&larr; Quay lại giỏ hàng</a></p>
</form></div></main>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>

<script>
  // Chống bấm đúp "Xác nhận đặt hàng" (trước đây bấm 2 lần có thể tạo 2 đơn).
  function khoaNutDatHang(form) {
    var btn = form.querySelector('button[type=submit]');
    if (btn) {
      if (btn.disabled) { return false; }
      btn.disabled = true;
      btn.textContent = 'Đang xử lý...';
    }
    return true;
  }
  // Quay lại bằng nút Back thì mở khóa lại nút.
  window.addEventListener('pageshow', function () {
    var btn = document.querySelector('#formDatHang button[type=submit]');
    if (btn) { btn.disabled = false; btn.textContent = 'Xác nhận đặt hàng ngay'; }
  });
</script>
</body>
</html>
