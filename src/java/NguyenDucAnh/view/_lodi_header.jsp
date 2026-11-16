<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- Header + thanh menu theo đúng cấu trúc LODI (header đen, viền vàng, nav chữ hoa).
     Dùng: <jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="home"/></jsp:include>
     active = home | gioithieu | sanpham | khuyenmai | donhang | giohang | lienhe
     Giữ nguyên các id mà JS cũ cần: searchInput, searchResult, allProductsData, cartCountBadge, userMenu --%>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<header>
  <div class="container top">
    <a href="${cp}/view/trangchu.jsp" class="brand"><img src="${cp}/image/logo.png" alt="LODI AUTHENTIC STORE" onerror="this.style.display='none'"><span class="wordmark"><b>LODI</b><small>Authentic Store</small></span></a>
    <form class="search" onsubmit="return false;">
      <input id="searchInput" type="text" placeholder="Tìm áo đấu, câu lạc bộ..." autocomplete="off">
      <button type="button">Tìm</button>
      <div id="searchResult"></div>
    </form>
    <div id="allProductsData" style="display:none;">
      <c:forEach var="spItem" items="${listsp}">
        <span class="sp-item" data-name="${fn:escapeXml(spItem.tensanpham)}"></span>
      </c:forEach>
    </div>
    <div class="user">
      <c:choose>
        <c:when test="${not empty sessionScope.user}">
          <span class="hello" id="userMenu">Xin chào, <b>${fn:escapeXml(sessionScope.user.hoten)}</b></span>
          <a href="${cp}/danhgia?action=cuatoi">Đánh giá của tôi</a>
          <a href="${cp}/dangxuatservlet">Đăng xuất</a>
        </c:when>
        <c:when test="${sessionScope.isAdmin}">
          <span class="hello" id="userMenu">Xin chào, <b>${fn:escapeXml(sessionScope.email)}</b></span>
          <a href="${cp}/dangxuatservlet">Đăng xuất</a>
        </c:when>
        <c:otherwise>
          <a href="${cp}/view/dangnhap.jsp">Đăng nhập</a>
          <a href="${cp}/view/dangky.jsp">Đăng ký</a>
        </c:otherwise>
      </c:choose>
      <a href="${cp}/giohangservlet">🛒 Giỏ hàng<span class="cart-count" id="cartCountBadge">${empty cartCount ? 0 : cartCount}</span></a>
    </div>
  </div>
  <nav><div class="container"><ul>
    <li><a href="${cp}/view/trangchu.jsp" class="${param.active == 'home' ? 'active' : ''}">Trang chủ</a></li>
    <li><a href="${cp}/view/gioithieu.jsp" class="${param.active == 'gioithieu' ? 'active' : ''}">Giới thiệu</a></li>
    <li><a href="${cp}/nguoidungservlet" class="${param.active == 'sanpham' ? 'active' : ''}">Sản phẩm</a></li>
    <li><a href="${cp}/magiamgiaservlet" class="${param.active == 'khuyenmai' ? 'active' : ''}">Khuyến mãi</a></li>
    <li><a href="${cp}/yeuthichservlet" class="${param.active == 'yeuthich' ? 'active' : ''}">Yêu thích</a></li>
    <li><a href="${cp}/trangthaiservlet" class="${param.active == 'donhang' ? 'active' : ''}">Đơn hàng</a></li>
    <li><a href="${cp}/giohangservlet" class="${param.active == 'giohang' ? 'active' : ''}">Giỏ hàng</a></li>
    <li><a href="${cp}/view/thongtinlienhe.jsp" class="${param.active == 'lienhe' ? 'active' : ''}">Liên hệ</a></li>
  </ul></div></nav>
</header>
