<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đánh giá của tôi - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-danhgia.css">
</head>
<body>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="donhang"/></jsp:include>
<main class="container">
  <div class="breadcrumb">
    <a href="${cp}/view/trangchu.jsp">Trang chủ</a> &rsaquo; <span>Đánh giá của tôi</span>
  </div>

  <section class="dg-box" style="margin-top:24px">
    <h2>Đánh giá của tôi</h2>
    <p class="dg-note">Lịch sử các đánh giá bạn đã gửi. Mỗi sản phẩm chỉ được đánh giá một lần và không thể sửa hay xóa.</p>

    <c:if test="${not empty sessionScope.dgThongBao}">
      <div class="dg-flash ok">&#10004; <span><c:out value="${sessionScope.dgThongBao}"/></span></div>
      <c:remove var="dgThongBao" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.dgLoi}">
      <div class="dg-flash err">&#9888; <span><c:out value="${sessionScope.dgLoi}"/></span></div>
      <c:remove var="dgLoi" scope="session"/>
    </c:if>

    <c:choose>
      <c:when test="${empty dsDanhGia}">
        <div class="dg-empty">
          Bạn chưa có đánh giá nào.<br>
          Sau khi nhận hàng, hãy vào trang sản phẩm để chia sẻ cảm nhận của bạn.<br><br>
          <a class="btn" href="${cp}/trangthaiservlet">Xem đơn hàng của tôi</a>
        </div>
      </c:when>
      <c:otherwise>
        <c:forEach var="dg" items="${dsDanhGia}">
          <c:url var="uSp" value="/chitietsanpham"><c:param name="ma" value="${dg.maSanPhamDaiDien}"/></c:url>
          <div class="dg-mine ${dg.hienThi ? '' : 'is-hidden'}">
            <img src="${cp}/image/${not empty dg.hinhAnh ? dg.hinhAnh : 'default.png'}" alt="" onerror="this.style.visibility='hidden'">
            <div>
              <div class="title">
                <c:choose>
                  <c:when test="${not empty dg.maSanPhamDaiDien}"><a href="${uSp}#danhgia"><c:out value="${dg.tenSanPham}"/></a></c:when>
                  <c:otherwise><span class="dg-nostar">(Sản phẩm không còn trong cửa hàng)</span></c:otherwise>
                </c:choose>
              </div>
              <div class="meta">
                <c:choose>
                  <c:when test="${dg.coSao}">
                    <span class="dg-stars" title="${dg.soSao} sao"><span class="fill" style="width:${dg.soSao * 20}%">&#9733;&#9733;&#9733;&#9733;&#9733;</span>&#9733;&#9733;&#9733;&#9733;&#9733;</span>
                  </c:when>
                  <c:otherwise><span class="dg-nostar">Chỉ nhận xét</span></c:otherwise>
                </c:choose>
                <span><fmt:formatDate value="${dg.ngayDanhGia}" pattern="dd/MM/yyyy HH:mm"/></span>
                <c:choose>
                  <c:when test="${dg.hienThi}"><span class="dg-tag show">Đang hiển thị</span></c:when>
                  <c:otherwise><span class="dg-tag hide">Đã ẩn</span></c:otherwise>
                </c:choose>
              </div>
              <c:if test="${dg.coNoiDung}"><p style="white-space:pre-line;word-break:break-word"><c:out value="${dg.noiDung}"/></p></c:if>
              <c:if test="${not dg.hienThi}">
                <div class="dg-hidden-note" style="margin-left:0">&#9888; Đánh giá đã bị ẩn bởi quản trị viên. Đánh giá này không hiển thị công khai và không được tính vào điểm trung bình của sản phẩm.</div>
              </c:if>
            </div>
          </div>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </section>
</main>
<jsp:include page="_lodi_footer.jsp"/>
</body>
</html>
