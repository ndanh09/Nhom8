<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đăng ký - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value=""/></jsp:include>
<main class="container"><div class="form-box"><h2>Tạo tài khoản</h2>
<p class="form-intro">Điền thông tin bên dưới để bắt đầu mua sắm cùng LODI.</p>
<c:if test="${error != null}"><div class="alert error" style="display:flex"><span class="ico">!</span><div>${error}</div></div></c:if>
<c:if test="${success != null}"><div class="alert success" style="display:flex"><span class="ico">&#10003;</span><div>${success}</div></div></c:if>
<form action="${pageContext.request.contextPath}/dangkysvl" method="post">
  <div class="field"><label for="email">Email <span class="req">*</span></label><input id="email" name="email" type="email" placeholder="email@example.com" required autocomplete="off"></div>
  <div class="field"><label for="hoten">Họ và tên <span class="req">*</span></label><input id="hoten" name="hoten" type="text" placeholder="Nguyễn Văn A" required autocomplete="off"></div>
  <div class="field"><label for="sodienthoai">Số điện thoại <span class="req">*</span></label><input id="sodienthoai" name="sodienthoai" type="text" placeholder="09xxxxxxxx" required autocomplete="off"></div>
  <div class="field"><label for="diachi">Địa chỉ</label><input id="diachi" name="diachi" type="text" placeholder="Địa chỉ nhận hàng" autocomplete="off"></div>
  <div class="field"><label for="matkhau">Mật khẩu <span class="req">*</span></label><div class="pw-wrap"><input id="matkhau" name="matkhau" type="password" placeholder="Nhập mật khẩu" required autocomplete="off"><button type="button" class="pw-toggle" data-target="matkhau">Hiện</button></div></div>
  <button type="submit" name="action" value="đăng ký" class="btn">Đăng ký</button>
  <p class="note">Đã có tài khoản? <a href="${pageContext.request.contextPath}/view/dangnhap.jsp">Đăng nhập ngay</a></p>
</form></div></main>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script>
document.querySelectorAll('.pw-toggle').forEach(function(b){
  b.addEventListener('click',function(){
    var i=document.getElementById(this.dataset.target);
    var show=i.type==='password'; i.type=show?'text':'password'; this.textContent=show?'Ẩn':'Hiện';
  });
});
</script>
</body>
</html>
