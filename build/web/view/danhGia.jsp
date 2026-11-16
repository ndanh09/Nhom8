<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- Mục ĐÁNH GIÁ & NHẬN XÉT trong trang chi tiết sản phẩm (ai cũng xem được).
     Được nhúng bằng <jsp:include page="danhGia.jsp"/> trong chitietsanpham.jsp; dữ liệu do
     DanhGiaServlet.napDanhGiaSanPham(...) gắn vào request. Chỉ hiển thị đánh giá đang hiển thị. --%>
<c:if test="${not empty dgThongKe}">
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<c:set var="tk" value="${dgThongKe}"/>
<section class="dg-box" id="danhgia">
  <h2>Đánh giá &amp; nhận xét</h2>

  <c:if test="${not empty sessionScope.dgThongBao}">
    <div class="dg-flash ok">&#10004; <span><c:out value="${sessionScope.dgThongBao}"/></span></div>
    <c:remove var="dgThongBao" scope="session"/>
  </c:if>
  <c:if test="${not empty sessionScope.dgLoi}">
    <div class="dg-flash err">&#9888; <span><c:out value="${sessionScope.dgLoi}"/></span></div>
    <c:remove var="dgLoi" scope="session"/>
  </c:if>

  <div class="dg-summary">
    <div class="dg-score">
      <c:choose>
        <c:when test="${tk.tongLuotChamSao > 0}">
          <div class="num"><fmt:formatNumber value="${tk.diemTrungBinh}" minFractionDigits="1" maxFractionDigits="1"/><small> / 5</small></div>
          <div class="dg-stars lg" title="${tk.diemTrungBinh} trên 5 sao"><span class="fill" style="width:${tk.diemTrungBinh * 20}%">&#9733;&#9733;&#9733;&#9733;&#9733;</span>&#9733;&#9733;&#9733;&#9733;&#9733;</div>
        </c:when>
        <c:otherwise>
          <div class="num" style="font-size:22px">Chưa có đánh giá</div>
        </c:otherwise>
      </c:choose>
      <div class="cnt"><b>${tk.tongLuotDanhGia}</b> lượt đánh giá &middot; <b>${tk.tongLuotChamSao}</b> lượt chấm sao</div>
    </div>
    <div class="dg-bars">
      <c:forEach var="i" begin="1" end="5">
        <c:set var="s" value="${6 - i}"/>
        <c:set var="so" value="${tk.soLuongTheoSao[s]}"/>
        <c:url var="uSao" value="/chitietsanpham"><c:param name="ma" value="${dgMaSanPham}"/><c:param name="sao" value="${s}"/></c:url>
        <div class="dg-bar">
          <a href="${uSao}#danhgia">${s} &#9733;</a>
          <div class="track"><i style="width:${tk.tongLuotChamSao > 0 ? so * 100 / tk.tongLuotChamSao : 0}%"></i></div>
          <span class="n">${so}</span>
        </div>
      </c:forEach>
    </div>
  </div>

  <div class="dg-cta">
    <c:choose>
      <c:when test="${dgNut == 'duoc'}">
        <c:url var="uForm" value="/danhgia"><c:param name="action" value="form"/><c:param name="ma" value="${dgMaSanPham}"/></c:url>
        <a class="btn" href="${uForm}">Viết đánh giá</a>
        <span class="dg-note">Bạn đã nhận sản phẩm này &mdash; hãy chia sẻ cảm nhận để giúp người mua sau.</span>
      </c:when>
      <c:when test="${dgNut == 'dangnhap'}">
        <a class="btn" href="${cp}/view/dangnhap.jsp">Đăng nhập để đánh giá</a>
        <span class="dg-note">Bạn cần đăng nhập và đã nhận hàng mới gửi được đánh giá.</span>
      </c:when>
      <c:when test="${dgNut == 'dadanhgia'}">
        <span class="dg-note"><c:out value="${dgThongBaoNut}"/> <a href="${cp}/danhgia?action=cuatoi">Xem đánh giá của tôi</a></span>
      </c:when>
      <c:when test="${dgNut == 'khong'}">
        <span class="dg-note"><c:out value="${dgThongBaoNut}"/></span>
      </c:when>
    </c:choose>
  </div>

  <div class="dg-filter">
    <c:url var="uTatCa" value="/chitietsanpham"><c:param name="ma" value="${dgMaSanPham}"/></c:url>
    <a href="${uTatCa}#danhgia" class="${empty dgSaoLoc ? 'on' : ''}">Tất cả (${tk.tongLuotDanhGia})</a>
    <c:forEach var="i" begin="1" end="5">
      <c:set var="s" value="${6 - i}"/>
      <c:url var="uLoc" value="/chitietsanpham"><c:param name="ma" value="${dgMaSanPham}"/><c:param name="sao" value="${s}"/></c:url>
      <a href="${uLoc}#danhgia" class="${dgSaoLoc == s ? 'on' : ''}">${s} sao (${tk.soLuongTheoSao[s]})</a>
    </c:forEach>
  </div>

  <c:choose>
    <c:when test="${empty dgDanhSach}">
      <div class="dg-empty">
        <c:choose>
          <c:when test="${not empty dgSaoLoc}">Chưa có đánh giá nào ở mức ${dgSaoLoc} sao.</c:when>
          <c:otherwise>Sản phẩm này chưa có đánh giá nào. Hãy là người đầu tiên!</c:otherwise>
        </c:choose>
      </div>
    </c:when>
    <c:otherwise>
      <c:forEach var="dg" items="${dgDanhSach}">
        <c:set var="ten" value="${empty dg.tenKhachHang ? 'Khách hàng' : dg.tenKhachHang}"/>
        <div class="dg-item">
          <div class="dg-head">
            <span class="dg-avatar">${fn:toUpperCase(fn:substring(ten, 0, 1))}</span>
            <span class="dg-name"><c:out value="${ten}"/></span>
            <c:choose>
              <c:when test="${dg.coSao}">
                <span class="dg-stars" title="${dg.soSao} sao"><span class="fill" style="width:${dg.soSao * 20}%">&#9733;&#9733;&#9733;&#9733;&#9733;</span>&#9733;&#9733;&#9733;&#9733;&#9733;</span>
              </c:when>
              <c:otherwise><span class="dg-nostar">Chỉ nhận xét</span></c:otherwise>
            </c:choose>
            <span class="dg-date"><fmt:formatDate value="${dg.ngayDanhGia}" pattern="dd/MM/yyyy HH:mm"/></span>
          </div>
          <c:if test="${dg.coNoiDung}"><p class="dg-text"><c:out value="${dg.noiDung}"/></p></c:if>
        </div>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</section>
</c:if>
