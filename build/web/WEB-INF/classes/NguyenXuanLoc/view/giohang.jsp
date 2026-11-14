<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Giỏ hàng - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="giohang"/></jsp:include>
<main class="container">
  <c:if test="${not empty sessionScope.toastMessage}">
    <div class="alert ${sessionScope.toastType=='success'?'success':(sessionScope.toastType=='info'?'info':'error')}" style="display:flex">
      <span class="ico">${sessionScope.toastType=='info'?'i':(sessionScope.toastType=='success'?'&#10003;':'!')}</span><div>${sessionScope.toastMessage}</div>
    </div>
    <c:remove var="toastMessage" scope="session"/>
    <c:remove var="toastType" scope="session"/>
  </c:if>

  <h2 class="section-title">Giỏ hàng của bạn</h2>

  <%-- Form ẩn: gửi danh sách magiohang đang được tích chọn khi khách bấm "Xóa sản phẩm đã chọn" --%>
  <form id="formXoaDaChon" action="${pageContext.request.contextPath}/giohangservlet" method="post" style="display:none;">
    <input type="hidden" name="action" value="deleteSelected">
  </form>
  <%-- Form ẩn: mỗi khi tích/bỏ tích 1 sản phẩm, gửi danh sách magiohang đang chọn lên server (lưu session)
       để dùng khi áp mã giảm giá và khi thanh toán. --%>
  <form id="selectForm" action="${pageContext.request.contextPath}/giohangservlet" method="post" style="display:none;">
    <input type="hidden" name="action" value="selectItems">
  </form>

  <c:choose>
  <c:when test="${not empty listCart}">
  <div class="cart">
    <div>
      <div class="cart-tools">
        <span class="hint-line">Chỉ những sản phẩm được tích chọn mới được tính vào mã giảm giá và thanh toán. Sản phẩm không chọn vẫn được giữ lại trong giỏ hàng.</span>
        <button type="button" class="btn dark" id="btnXoaDaChon" onclick="xoaSanPhamDaChon()">Xóa sản phẩm đã chọn</button>
      </div>
      <div class="table-scroll">
      <table id="cartTable">
        <thead>
          <tr>
            <th style="width:44px;"><input type="checkbox" id="chkAll" class="chk-master" ${allSelected ? 'checked' : ''} onclick="toggleAll(this)" title="Chọn tất cả"></th>
            <th>Sản phẩm</th>
            <th>Size</th>
            <th>Đơn giá</th>
            <th>Số lượng</th>
            <th>Tồn kho</th>
            <th>Thành tiền</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="item" items="${listCart}">
            <tr class="cart-row" data-name="${not empty item.tensanpham ? item.tensanpham : item.masanpham}">
              <td><input type="checkbox" class="chk-item" data-magiohang="${item.magiohang}" ${selectedMap[item.magiohang] ? 'checked' : ''} onclick="onToggleItem()"></td>
              <td>
                <div class="pi"><div>
                  <b><c:choose><c:when test="${not empty item.tensanpham}">${item.tensanpham}</c:when><c:otherwise>${item.masanpham}</c:otherwise></c:choose></b><br>
                  <small>Mã: ${item.maSanPhamGoc}</small>
                </div></div>
              </td>
              <td>
                <select id="kichco_${item.magiohang}" name="kichco" class="size-select">
                  <c:forEach var="sz" items="${sizeMap[item.magiohang]}">
                    <option value="${sz}" ${item.kichco==sz?"selected":""}>${sz}</option>
                  </c:forEach>
                </select>
              </td>
              <td><fmt:formatNumber value="${item.giaban}" type="number" groupingUsed="true"/>đ</td>
              <td>
                <input type="number" id="soluong_${item.magiohang}" name="soluong" value="${item.soluong}" min="1" max="${item.tonKho}"
                       onchange="kiemTraTonKho(this, ${item.tonKho})" class="qty input-qty">
              </td>
              <td>
                <span class="${item.tonKho <= 0 ? 'text-danger' : (item.tonKho < 10 ? 'text-warning' : 'text-success')}">
                  <c:choose><c:when test="${item.tonKho > 0}">Còn ${item.tonKho}</c:when><c:otherwise>Hết hàng</c:otherwise></c:choose>
                </span>
              </td>
              <td><b class="line-total"><fmt:formatNumber value="${item.giaban * item.soluong}" type="number" groupingUsed="true"/>đ</b></td>
              <td class="row-actions">
                <form action="${pageContext.request.contextPath}/giohangservlet" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="update">
                  <input type="hidden" name="magiohang" value="${item.magiohang}">
                  <input type="hidden" id="hiddenSize_${item.magiohang}" name="kichco">
                  <input type="hidden" id="hiddenQty_${item.magiohang}" name="soluong">
                  <button type="submit" class="btn mini" onclick="copyValue('${item.magiohang}')">Cập nhật</button>
                </form>
                <form action="${pageContext.request.contextPath}/giohangservlet" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="magiohang" value="${item.magiohang}">
                  <button type="submit" class="del">Xóa</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      </div>
    </div>

    <div class="summary">
      <h3>Tóm tắt đơn hàng</h3>

      <%-- Mã giảm giá --%>
      <form class="voucher-form" action="${pageContext.request.contextPath}/giohangservlet" method="post">
        <input type="hidden" name="action" value="applyVoucher">
        <label class="vlabel" for="magiamgia">Mã giảm giá</label>
        <div class="vrow">
          <input type="text" id="magiamgia" name="magiamgia" class="voucher-input" placeholder="Nhập mã giảm giá..."
                 value="${not empty prefillMaGiamGia ? prefillMaGiamGia : sessionScope.maGiamGia}">
          <button type="submit" class="btn dark mini">Áp dụng</button>
        </div>
      </form>
      <c:if test="${not empty prefillMaGiamGia}">
        <p class="vnote">Hãy tích chọn sản phẩm muốn dùng mã ở bảng bên trái rồi bấm <b>Áp dụng</b> để hoàn tất.</p>
      </c:if>
      <c:if test="${not empty voucher}">
        <div class="voucher-success">&#10003; Đang sử dụng mã: <b>${voucher.magiamgia}</b></div>
      </c:if>
      <a href="${pageContext.request.contextPath}/magiamgiaservlet" class="vlink">Xem các mã giảm giá đang khả dụng</a>

      <div class="row" style="margin-top:10px"><span>Tổng tiền hàng</span><span><fmt:formatNumber value="${totalCartPrice}" type="number" groupingUsed="true"/>đ</span></div>
      <div class="row"><span>Giảm giá</span><span class="discount">- <fmt:formatNumber value="${tienGiam}" type="number" groupingUsed="true"/>đ</span></div>
      <div class="row total"><span>Thành tiền</span><span><fmt:formatNumber value="${thanhTien}" type="number" groupingUsed="true"/>đ</span></div>

      <form action="${pageContext.request.contextPath}/giohangservlet" method="post" onsubmit="return checkCoChonSanPham(event)">
        <input type="hidden" name="action" value="checkout">
        <button type="submit" class="btn btn-checkout" style="width:100%;margin-top:16px">Tiến hành thanh toán</button>
      </form>
      <a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn dark">Tiếp tục mua sắm</a>
    </div>
  </div>
  </c:when>
  <c:otherwise>
    <div class="cart-empty">
      <div class="big">&#128722;</div>
      <h3>Giỏ hàng của bạn đang trống</h3>
      <p>Hãy chọn cho mình một chiếc áo đấu Authentic nhé.</p>
      <a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn">Tiếp tục mua sắm</a>
    </div>
  </c:otherwise>
  </c:choose>
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

            function copyValue(id) {
                document.getElementById("hiddenSize_" + id).value =
                        document.getElementById("kichco_" + id).value;
                document.getElementById("hiddenQty_" + id).value =
                        document.getElementById("soluong_" + id).value;
            }

            // ============ TÍCH CHỌN SẢN PHẨM ĐỂ THANH TOÁN ============
            // Mỗi khi khách tích/bỏ tích 1 sản phẩm (hoặc bấm "chọn tất cả"),
            // gom lại danh sách magiohang đang được chọn và submit lên
            // server (action=selectItems) để lưu vào session. Trang sẽ tải
            // lại và tính đúng tổng tiền/giảm giá/thanh toán chỉ trên các
            // sản phẩm đã chọn.
            function submitSelection() {
                const form = document.getElementById("selectForm");
                // Xoá các input chkItem cũ (nếu có) trước khi thêm lại
                form.querySelectorAll('input[name="chkItem"]').forEach(el => el.remove());

                document.querySelectorAll(".chk-item:checked").forEach(chk => {
                    const input = document.createElement("input");
                    input.type = "hidden";
                    input.name = "chkItem";
                    input.value = chk.getAttribute("data-magiohang");
                    form.appendChild(input);
                });

                form.submit();
            }

            function onToggleItem() {
                submitSelection();
            }

            function toggleAll(masterChk) {
                document.querySelectorAll(".chk-item").forEach(chk => {
                    chk.checked = masterChk.checked;
                });
                submitSelection();
            }

            // Chặn bấm "Tiến hành thanh toán" khi chưa tích chọn sản phẩm nào
            function checkCoChonSanPham(e) {
                const daChon = document.querySelectorAll(".chk-item:checked").length;
                if (daChon === 0) {
                    e.preventDefault();
                    alert("Vui lòng chọn ít nhất 1 sản phẩm để thanh toán!");
                    return false;
                }
                return true;
            }

            // FIX MỚI: nút "Xóa sản phẩm đã chọn" ở phía trên bảng - dùng
            // chung checkbox với "Chọn tất cả"/tích từng dòng để xoá hàng
            // loạt, tiện cho khách khi lỡ tích nhiều sản phẩm không muốn
            // mua nữa mà không phải xoá từng dòng một (nút xoá từng dòng ở
            // cột "Hành động" vẫn giữ nguyên để xoá lẻ khi cần).
            function xoaSanPhamDaChon() {
                const dsDaChon = document.querySelectorAll(".chk-item:checked");

                if (dsDaChon.length === 0) {
                    alert("Vui lòng chọn ít nhất 1 sản phẩm muốn xóa!");
                    return;
                }

                if (!confirm("Bạn có chắc chắn muốn xóa " + dsDaChon.length + " sản phẩm đã chọn khỏi giỏ hàng không?")) {
                    return;
                }

                const form = document.getElementById("formXoaDaChon");
                form.querySelectorAll('input[name="magiohang"]').forEach(el => el.remove());

                dsDaChon.forEach(chk => {
                    const input = document.createElement("input");
                    input.type = "hidden";
                    input.name = "magiohang";
                    input.value = chk.getAttribute("data-magiohang");
                    form.appendChild(input);
                });

                form.submit();
            }

            // Không cho số lượng mua vượt quá tồn kho, và không cho phép dưới 1.
            // Tồn kho có bao nhiêu thì cho mua tối đa bấy nhiêu (không giới
            // hạn cứng ở 10 nữa).
            function kiemTraTonKho(input, tonKho) {
                let n = parseInt(input.value) || 1;
                if (n < 1) {
                    n = 1;
                }
                if (tonKho <= 0) {
                    alert("Sản phẩm này đã hết hàng!");
                    n = 1;
                } else if (n > tonKho) {
                    alert("Số lượng vượt quá tồn kho (còn " + tonKho + " sản phẩm)!");
                    n = tonKho;
                }
                input.value = n;
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
            // chỉ sản phẩm đã có trong giỏ hàng) -> bấm vào gợi ý sẽ nhảy sang trang
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
