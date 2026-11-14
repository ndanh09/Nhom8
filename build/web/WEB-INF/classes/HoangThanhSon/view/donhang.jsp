<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đơn hàng - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="donhang"/></jsp:include>
<main class="container">
  <h2 class="section-title">Đơn hàng của bạn<small>TRẠNG THÁI ĐƠN HÀNG</small></h2>
  <c:if test="${not empty message}">
    <div class="alert success" style="display:flex"><span class="ico">&#10003;</span><div>${message}</div></div>
    <% session.removeAttribute("message"); %>
  </c:if>
  <c:if test="${not empty error}">
    <div class="alert error" style="display:flex"><span class="ico">!</span><div>${error}</div></div>
    <% session.removeAttribute("error"); %>
  </c:if>
  <div class="table-scroll">
    <table id="orderTable" class="data-table">
      <thead>
        <tr>
          <th>Mã đơn hàng</th>
          <th>Sản phẩm</th>
          <th>Size</th>
          <th>Số lượng</th>
          <th>Tổng giá</th>
          <th>Trạng thái</th>
          <th>Ngày mua</th>
          <th>Chức năng</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="grp" items="${listNhomDon}">
          <c:forEach var="order" items="${grp.dong}" varStatus="st">
            <tr class="order-row" data-name="${not empty order.tensanpham ? order.tensanpham : order.masanpham}">
              <c:if test="${st.first}">
                <td rowspan="${grp.soDong + 1}" style="vertical-align:top;">
                  <b class="gold">#ORD-${grp.maHienThi}</b>
                  <div style="margin-top:8px;">
                    <a href="${pageContext.request.contextPath}/hoadonservlet?madonhang=${grp.madonhang}" class="btn mini dark">Xem hóa đơn</a>
                  </div>
                </td>
              </c:if>
              <td class="pname">
                <c:choose>
                  <c:when test="${not empty order.tensanpham}">${order.tensanpham}</c:when>
                  <c:otherwise>${order.masanpham}</c:otherwise>
                </c:choose>
              </td>
              <td>${order.kichco}</td>
              <td>${order.soluong}</td>
              <td class="gold"><b><fmt:formatNumber value="${order.giaban * order.soluong}" type="number" groupingUsed="true"/>đ</b></td>
              <td>
                <c:choose>
                  <c:when test="${order.tinhtrang == 0}"><span class="badge status-pending">Chờ xác nhận</span></c:when>
                  <c:when test="${order.tinhtrang == 1}"><span class="badge status-shipping">Đang giao</span></c:when>
                  <c:when test="${order.tinhtrang == 4}"><span class="badge status-delivered">Đã giao - chờ bạn xác nhận</span></c:when>
                  <c:when test="${order.tinhtrang == 2}"><span class="badge status-completed">Đã nhận</span></c:when>
                  <c:otherwise><span class="badge status-cancelled">Hủy đơn</span></c:otherwise>
                </c:choose>
              </td>
              <c:if test="${st.first}">
                <td rowspan="${grp.soDong + 1}" style="vertical-align:top;">
                  <fmt:formatDate value="${grp.ngaymua}" pattern="dd/MM/yyyy HH:mm"/>
                </td>
              </c:if>
              <td>
                <c:if test="${order.donhangKhoa}">
                  <span class="badge status-cancelled">Đơn đang bị khóa</span>
                </c:if>
                <c:if test="${!order.donhangKhoa && (order.tinhtrang == 0 || order.tinhtrang == 1)}">
                  <form action="${pageContext.request.contextPath}/trangthaiservlet" method="POST" style="margin: 0;" onsubmit="return handleCancelOrder(event);">
                    <input type="hidden" name="action" value="cancel" />
                    <input type="hidden" name="matrangthai" value="${order.matrangthai}" />
                    <button type="submit" class="btn mini ghost">${grp.soDong > 1 ? 'Hủy sản phẩm này' : 'Hủy đơn hàng'}</button>
                  </form>
                </c:if>
                <c:if test="${!order.donhangKhoa && order.tinhtrang == 4}">
                  <form action="${pageContext.request.contextPath}/trangthaiservlet" method="POST" style="margin: 0;" onsubmit="return confirm('Xác nhận bạn đã nhận được hàng?');">
                    <input type="hidden" name="action" value="confirmReceived" />
                    <input type="hidden" name="matrangthai" value="${order.matrangthai}" />
                    <button type="submit" class="btn mini">Đã nhận hàng</button>
                  </form>
                </c:if>
                <c:if test="${!order.donhangKhoa && order.tinhtrang == 2}">
                  <form action="${pageContext.request.contextPath}/trangthaiservlet" method="POST" style="margin: 0;" onsubmit="return confirm('Xác nhận hoàn trả sản phẩm này? Số lượng sản phẩm sẽ được cập nhật lại như trước khi mua.');">
                    <input type="hidden" name="action" value="hoantra" />
                    <input type="hidden" name="matrangthai" value="${order.matrangthai}" />
                    <button type="submit" class="btn mini ghost">Hoàn trả hàng</button>
                  </form>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          <tr class="order-total">
            <td colspan="3" style="text-align:right;"><b>Tổng đơn hàng #ORD-${grp.maHienThi}</b></td>
            <td class="gold"><b><fmt:formatNumber value="${grp.tongTien}" type="number" groupingUsed="true"/>đ</b></td>
            <td></td>
            <td></td>
          </tr>
        </c:forEach>
        <c:if test="${empty listNhomDon}">
          <tr>
            <td colspan="8" class="empty-text">Bạn chưa có đơn hàng nào. <a href="${pageContext.request.contextPath}/nguoidungservlet" class="gold"><b>Mua sắm ngay</b></a></td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>
</main>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script>
            window.addEventListener("scroll", function () {
                const btn = document.querySelector(".back-top");
                if (window.scrollY > 300) {
                    btn.style.display = "flex";
                } else {
                    btn.style.display = "none";
                }
            });

            function handleCancelOrder(event) {
                var check = confirm("Bạn có chắc chắn muốn hủy đơn hàng này không?");
                if (check) {
                    alert("Hủy thành công!");
                    return true;
                }
                event.preventDefault();
                return false;
            }

            // ============ USER MENU DROPDOWN (đồng bộ) ============
            const userMenu = document.getElementById("userMenu");
            if (userMenu) {
                userMenu.addEventListener("click", function (e) {
                    e.stopPropagation();
                    this.classList.toggle("active");
                });
                document.addEventListener("click", function () {
                    userMenu.classList.remove("active");
                });
            }

            // ============ TÌM KIẾM GỢI Ý SẢN PHẨM (toàn hệ thống, bên trang Sản phẩm) ============
            // Gõ tên sản phẩm -> gợi ý ra sản phẩm bên trang "nguoidung" (không phải
            // chỉ sản phẩm đã có trong đơn hàng) -> bấm vào gợi ý sẽ nhảy sang trang
            // Sản phẩm và nhấp nháy vàng đúng sản phẩm đó.
            const searchInput = document.getElementById("searchInput");
            const searchResult = document.getElementById("searchResult");
            const allProducts = document.querySelectorAll("#allProductsData .sp-item");

            function removeVietnamese(str) {
                return str
                    .normalize("NFD")
                    .replace(/[\u0300-\u036f]/g, "")
                    .toLowerCase();
            }

            if (searchInput) {
                searchInput.addEventListener("keyup", function () {
                    let keyword = removeVietnamese(this.value.trim());
                    searchResult.innerHTML = "";
                    if (keyword === "") {
                        searchResult.style.display = "none";
                        return;
                    }
                    let count = 0;
                    allProducts.forEach(sp => {
                        let name = sp.getAttribute("data-name");
                        if (name) {
                            let nameSearch = removeVietnamese(name);
                            if (nameSearch.includes(keyword)) {
                                let item = document.createElement("div");
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
                        searchResult.innerHTML = `<div class="search-item">Không tìm thấy sản phẩm phù hợp</div>`;
                        searchResult.style.display = "block";
                    }
                });

                document.addEventListener("click", function (e) {
                    if (!searchInput.contains(e.target) && !searchResult.contains(e.target)) {
                        searchResult.style.display = "none";
                    }
                });
            }
        </script>
</body>
</html>
