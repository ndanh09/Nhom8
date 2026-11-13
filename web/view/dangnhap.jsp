<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đăng nhập - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value=""/></jsp:include>
<main class="container"><div class="form-box"><h2>Đăng nhập</h2>
<p class="form-intro">Nhập thông tin của bạn để tiếp tục mua sắm tại LODI AUTHENTIC STORE.</p>
<c:if test="${error != null}"><div class="alert error" style="display:flex"><span class="ico">!</span><div>${error}</div></div></c:if>
<form action="${pageContext.request.contextPath}/dangnhapsvl" method="post">
  <div class="field"><label for="email">Email <span class="req">*</span></label><input id="email" name="email" type="email" placeholder="email@example.com" required autocomplete="off"></div>
  <div class="field"><label for="matkhau">Mật khẩu <span class="req">*</span></label><div class="pw-wrap"><input id="matkhau" name="matkhau" type="password" placeholder="Nhập mật khẩu" required autocomplete="off"><button type="button" class="pw-toggle" data-target="matkhau">Hiện</button></div></div>
  <div class="field"><label class="check"><input type="checkbox"> Ghi nhớ đăng nhập</label></div>
  <button type="submit" name="action" class="btn">Đăng nhập</button>
  <p class="note">Chưa có tài khoản? <a href="${pageContext.request.contextPath}/dangkysvl">Đăng ký ngay</a></p>
  <p class="note">Bạn là đối tác vận chuyển? <a href="${pageContext.request.contextPath}/dangnhapvanchuyensvl">Đăng nhập tại đây</a></p>
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
