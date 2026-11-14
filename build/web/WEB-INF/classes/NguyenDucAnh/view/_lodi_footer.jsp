<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- Footer theo đúng cấu trúc LODI: 3 cột (giới thiệu + logo | liên kết | tài khoản) + dòng bản quyền --%>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<footer>
  <div class="container foot">
    <div class="about"><img src="${cp}/image/logo.png" alt="LODI AUTHENTIC STORE" onerror="this.style.display='none'"><div><h4>LODI AUTHENTIC STORE</h4><p>Chuyên áo đấu bóng đá Authentic chính hãng của các câu lạc bộ hàng đầu châu Âu - mùa giải 2026-2027.</p></div></div>
    <div><h4>Liên kết</h4><ul><li><a href="${cp}/view/trangchu.jsp">Trang chủ</a></li><li><a href="${cp}/nguoidungservlet">Sản phẩm</a></li><li><a href="${cp}/giohangservlet">Giỏ hàng</a></li><li><a href="${cp}/view/thongtinlienhe.jsp">Liên hệ</a></li></ul></div>
    <div><h4>Tài khoản</h4><ul>
      <c:choose>
        <c:when test="${not empty sessionScope.user or sessionScope.isAdmin}"><li><a href="${cp}/trangthaiservlet">Đơn hàng</a></li><li><a href="${cp}/dangxuatservlet">Đăng xuất</a></li></c:when>
        <c:otherwise><li><a href="${cp}/view/dangnhap.jsp">Đăng nhập</a></li><li><a href="${cp}/view/dangky.jsp">Đăng ký</a></li></c:otherwise>
      </c:choose>
    </ul></div>
  </div>
  <div class="container copy">© 2026 LODI AUTHENTIC STORE. Chuyên áo đấu bóng đá Authentic chính hãng.</div>
</footer>
