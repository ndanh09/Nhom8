<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Khuyến mãi - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="khuyenmai"/></jsp:include>
<main class="container">
  <div class="page-head">
    <h2 class="section-title">Mã giảm giá<small>ƯU ĐÃI ĐANG KHẢ DỤNG</small></h2>
    <p>Danh sách mã giảm giá đang khả dụng - áp dụng ngay khi thanh toán để nhận ưu đãi.</p>
  </div>
  <div class="voucher-search-box">
    <input type="text" id="searchVoucher" placeholder="Tìm theo mã hoặc tên phiếu giảm giá...">
  </div>

  <c:choose>
    <c:when test="${not empty listMa}">
      <div class="coupon-grid" id="couponGrid">
        <c:forEach var="ma" items="${listMa}">
          <div class="coupon-card" data-code="${fn:toLowerCase(ma.magiamgia)}" data-name="${fn:toLowerCase(ma.tenphieu)}">
            <div class="coupon-side">
              <c:choose>
                <c:when test="${ma.loaigiam == 0}">
                  <b><fmt:formatNumber value="${ma.giatrigiam}" type="number" groupingUsed="true"/>đ</b>
                  <small>Giảm trực tiếp</small>
                </c:when>
                <c:otherwise>
                  <b>${ma.giatrigiam}%</b>
                  <small>Giảm giá</small>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="coupon-body">
              <div class="coupon-code-row"><div class="coupon-code">${ma.magiamgia}</div></div>
              <div class="coupon-name">${ma.tenphieu}</div>
              <ul class="coupon-info">
                <c:if test="${ma.dieukien > 0}">
                  <li>Đơn tối thiểu <fmt:formatNumber value="${ma.dieukien}" type="number" groupingUsed="true"/>đ</li>
                </c:if>
                <c:if test="${ma.loaigiam != 0 && ma.giamtoida > 0}">
                  <li>Giảm tối đa <fmt:formatNumber value="${ma.giamtoida}" type="number" groupingUsed="true"/>đ</li>
                </c:if>
                <li>Còn ${ma.soluong} lượt</li>
                <li>HSD: <fmt:formatDate value="${ma.ngayketthuc}" pattern="dd/MM/yyyy"/></li>
              </ul>
              <div class="coupon-actions">
                <button type="button" class="btn ghost mini" onclick="copyCode('${ma.magiamgia}')">Sao chép</button>
                <%-- Không áp mã ngay: chỉ đưa khách sang giỏ hàng kèm mã, khách tự tích chọn sản phẩm rồi bấm ÁP DỤNG ở đó. --%>
                <a href="${pageContext.request.contextPath}/giohangservlet?magiamgia=${fn:escapeXml(ma.magiamgia)}" class="btn mini">Dùng ngay</a>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
      <div class="cart-empty search-empty" id="searchEmpty">
        <h3>Không tìm thấy mã giảm giá phù hợp</h3>
        <p>Thử tìm với từ khoá khác nhé.</p>
      </div>
    </c:when>
    <c:otherwise>
      <div class="cart-empty">
        <div class="big">&#127903;</div>
        <h3>Hiện chưa có mã giảm giá nào khả dụng</h3>
        <p>Hãy quay lại sau để không bỏ lỡ những ưu đãi hấp dẫn từ LODI.</p>
      </div>
    </c:otherwise>
  </c:choose>
</main>
<div id="toasts"><div class="toast success copy-toast" id="copyToast" style="display:none" data-lodi="1"><span class="ico">&#10003;</span><div class="txt"><b>Đã sao chép mã giảm giá!</b></div></div></div>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script>
            function copyCode(code) {
                navigator.clipboard.writeText(code).then(function () {
                    var toast = document.getElementById("copyToast");
                    toast.classList.add("show");
                    setTimeout(function () {
                        toast.classList.remove("show");
                    }, 1800);
                }).catch(function () {
                    alert("Mã giảm giá: " + code);
                });
            }

            var voucherSearchInput = document.getElementById("searchVoucher");
            if (voucherSearchInput) {
                voucherSearchInput.addEventListener("input", function () {
                    var keyword = this.value.trim().toLowerCase();
                    var cards = document.querySelectorAll("#couponGrid .coupon-card");
                    var visibleCount = 0;
                    cards.forEach(function (card) {
                        var code = card.getAttribute("data-code") || "";
                        var name = card.getAttribute("data-name") || "";
                        var match = code.indexOf(keyword) !== -1 || name.indexOf(keyword) !== -1;
                        card.style.display = match ? "" : "none";
                        if (match) {
                            visibleCount++;
                        }
                    });
                    var emptyBox = document.getElementById("searchEmpty");
                    if (emptyBox) {
                        emptyBox.classList.toggle("show", visibleCount === 0);
                    }
                });
            }

            // ============ TÌM KIẾM GỢI Ý SẢN PHẨM (toàn hệ thống, bên trang Sản phẩm) ============
            // Gõ tên sản phẩm -> gợi ý ra sản phẩm bên trang "nguoidung" -> bấm vào
            // gợi ý sẽ nhảy sang trang Sản phẩm và nhấp nháy vàng đúng sản phẩm đó.
            (function () {
                var searchInput = document.getElementById("searchInput");
                var searchResult = document.getElementById("searchResult");
                var allProducts = document.querySelectorAll("#allProductsData .sp-item");

                function removeVietnamese(str) {
                    return str
                        .normalize("NFD")
                        .replace(/[\u0300-\u036f]/g, "")
                        .toLowerCase();
                }

                if (searchInput) {
                    searchInput.addEventListener("keyup", function () {
                        var keyword = removeVietnamese(this.value.trim());
                        searchResult.innerHTML = "";
                        if (keyword === "") {
                            searchResult.style.display = "none";
                            return;
                        }
                        var count = 0;
                        allProducts.forEach(function (sp) {
                            var name = sp.getAttribute("data-name");
                            if (name) {
                                var nameSearch = removeVietnamese(name);
                                if (nameSearch.includes(keyword)) {
                                    var item = document.createElement("div");
                                    item.className = "search-item";
                                    item.innerHTML = name;
                                    item.onclick = function () {
                                        // Nhảy sang trang Sản phẩm (nguoidung) và nhấp nháy vàng đúng sản phẩm đó
                                        window.location.href = "${pageContext.request.contextPath}/nguoidungservlet?highlight=" + encodeURIComponent(name);
                                    };
                                    searchResult.appendChild(item);
                                    count++;
                                }
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
                }
            })();

            var userMenu = document.getElementById("userMenu");
            if (userMenu) {
                userMenu.addEventListener("click", function (e) {
                    e.stopPropagation();
                    this.classList.toggle("active");
                });
                document.addEventListener("click", function () {
                    userMenu.classList.remove("active");
                });
            }
        </script>
</body>
</html>
