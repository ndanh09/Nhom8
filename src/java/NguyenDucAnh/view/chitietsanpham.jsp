<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Chi tiết sản phẩm - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-danhgia.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="sanpham"/></jsp:include>
<main class="container">
  <div class="breadcrumb">
    <a href="${pageContext.request.contextPath}/view/trangchu.jsp">Trang chủ</a> &rsaquo;
    <a href="${pageContext.request.contextPath}/nguoidungservlet">Sản phẩm</a> &rsaquo;
    <span>${sp.tensanpham}</span>
  </div>

  <c:if test="${empty sp}">
    <div class="cart-empty"><div class="big">&#9888;</div><h3>Không tìm thấy sản phẩm.</h3><p>Sản phẩm có thể đã bị xóa hoặc đường dẫn không đúng.</p><a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn">Về trang Sản phẩm</a></div>
  </c:if>

  <c:if test="${not empty sp}">
    <div class="detail">
      <div>
        <div class="main-img"><img id="mainImg" src="${pageContext.request.contextPath}/image/${not empty sp.hinhanh ? sp.hinhanh : 'default-prod.png'}" alt="${fn:escapeXml(sp.tensanpham)}"></div>
        <div class="thumbs" id="thumbStrip"></div>
        <div id="slideDots" style="display:none"></div>
      </div>
      <div>
        <h1 id="infoTitle">${sp.tensanpham}</h1>
        <p class="meta">Mã SP: <span id="infoMa">${sp.masanpham}</span> | Tình trạng: <span class="info-stock-badge" id="infoStockBadge"></span></p>
        <p class="meta" id="infoStockQty"></p>
        <div class="price" id="infoGia"><fmt:formatNumber value="${sp.giasanpham}" pattern="#,###"/>đ</div>
        <ul>
          <li>Sản phẩm chính hãng 100% Authentic, đầy đủ tem nhãn.</li>
          <li>Chất liệu vải cao cấp, thoáng khí, thấm hút mồ hôi tốt.</li>
          <li>Đổi trả trong 7 ngày nếu sản phẩm lỗi do nhà sản xuất.</li>
          <li>Giao hàng toàn quốc, freeship cho đơn từ 2.000.000đ.</li>
        </ul>
        <div class="options"><b>Size: </b><div class="size-options" id="sizeOptions"></div></div>
        <div class="options"><b>Số lượng: </b>
          <span class="qtybox"><button type="button" id="qtyMinus">&minus;</button><input type="text" id="qtyInput" class="qty" value="1" readonly><button type="button" id="qtyPlus">+</button></span>
        </div>
        <p style="margin-top:18px">
          <button type="button" class="btn" id="btnAddCart">Thêm vào giỏ hàng</button>
          <button type="button" class="fav-btn fav-wide" data-masanpham="${sp.masanpham}" aria-label="Yêu thích"><span class="fav-icon">&#9829;</span><span class="fav-text">Yêu thích</span></button>
          <a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn dark">Quay lại danh sách</a>
        </p>
      </div>
    </div>
    <div class="desc"><h2>Mô tả sản phẩm</h2><p style="margin-top:10px">Áo đấu Authentic chính hãng của LODI AUTHENTIC STORE, dành cho người hâm mộ yêu thích sự chỉn chu và đẳng cấp. Chất liệu thoáng khí, form áo thể thao vừa vặn, hoàn thiện tinh xảo - phù hợp cổ vũ, sưu tầm và mặc hằng ngày.</p></div>
    <jsp:include page="danhGia.jsp"/>
  </c:if>
</main>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script>
            const ctx = "${pageContext.request.contextPath}";
            const variants = [
                <c:forEach var="v" items="${dsSize}" varStatus="st">
                {
                    ma: "${v.masanpham}",
                    kichco: "${v.kichco}",
                    gia: ${v.giasanpham},
                    soluong: ${v.soluongsanpham},
                    trangthai: "${v.trangthai}",
                    hinhanh: "${not empty v.hinhanh ? v.hinhanh : 'default-prod.png'}"
                }<c:if test="${!st.last}">,</c:if>
                </c:forEach>
            ];
            const currentMa = "${sp.masanpham}";
            const standardSizes = ["S", "M", "L", "XL", "XXL", "XXXL"];
            const images = [...new Set(variants.map(v => v.hinhanh))];
            if (images.length === 0) {
                images.push("${not empty sp.hinhanh ? sp.hinhanh : 'default-prod.png'}");
            }
            let imgIndex = Math.max(0, images.indexOf(variants.find(v => v.ma === currentMa) ? variants.find(v => v.ma === currentMa).hinhanh : images[0]));
            let selected = variants.find(v => v.ma === currentMa) || variants[0];
            const mainImg = document.getElementById("mainImg");
            const thumbStrip = document.getElementById("thumbStrip");
            const slideDots = document.getElementById("slideDots");
            const sizeOptions = document.getElementById("sizeOptions");
            const infoGia = document.getElementById("infoGia");
            const infoMa = document.getElementById("infoMa");
            const infoStockBadge = document.getElementById("infoStockBadge");
            const infoStockQty = document.getElementById("infoStockQty");
            const qtyInput = document.getElementById("qtyInput");
            const btnAddCart = document.getElementById("btnAddCart");
            const cartCountBadge = document.getElementById("cartCountBadge");
            let cartItemCount = parseInt((document.getElementById("cartCountBadge")||{}).textContent) || 0;
            function formatVND(n) {
                return n.toLocaleString("vi-VN") + "đ";
            }
            function renderGallery() {
                mainImg.src = ctx + "/image/" + images[imgIndex];
                thumbStrip.innerHTML = "";
                slideDots.innerHTML = "";
                if (images.length > 1) {
                    images.forEach((img, i) => {
                        const th = document.createElement("div");
                        th.className = "thumb-item" + (i === imgIndex ? " active" : "");
                        th.innerHTML = '<img src="' + ctx + '/image/' + img + '">';
                        th.onclick = () => { imgIndex = i; renderGallery(); };
                        thumbStrip.appendChild(th);
                        const dot = document.createElement("span");
                        dot.className = i === imgIndex ? "active" : "";
                        dot.onclick = () => { imgIndex = i; renderGallery(); };
                        slideDots.appendChild(dot);
                    });
                }
            }
            function renderStockBadge(v) {
                infoStockBadge.className = "info-stock-badge";
                const soLuongConLai = Math.max(0, v.soluong);
                if (v.trangthai === "1" && v.soluong > 0) {
                    infoStockBadge.classList.add("stock-in");
                    infoStockBadge.innerHTML = 'CÒN HÀNG';
                    infoStockQty.textContent = "Còn lại: " + soLuongConLai + " sản phẩm (size " + v.kichco + ")";
                } else if (v.soluong === 0) {
                    infoStockBadge.classList.add("stock-out");
                    infoStockBadge.innerHTML = 'HẾT HÀNG';
                    infoStockQty.textContent = "Còn lại: 0 sản phẩm (size " + v.kichco + ")";
                } else {
                    infoStockBadge.classList.add("stock-stop");
                    infoStockBadge.innerHTML = 'NGỪNG BÁN';
                    infoStockQty.textContent = "Còn lại: " + soLuongConLai + " sản phẩm (size " + v.kichco + ")";
                }
            }
            function renderSizes() {
                sizeOptions.innerHTML = "";
                standardSizes.forEach(sizeName => {
                    const foundVar = variants.find(v => v.kichco.toUpperCase() === sizeName);
                    const btn = document.createElement("button");
                    btn.type = "button";
                    btn.textContent = sizeName;
                    if (foundVar) {
                        const unavailable = foundVar.soluong <= 0 || foundVar.trangthai !== "1";
                        btn.className = "size-btn" + (foundVar.ma === selected.ma ? " active" : "") + (unavailable ? " unavailable" : "");                
                        if (!unavailable) {
                            btn.onclick = () => {
                                selected = foundVar;
                                qtyInput.value = 1;
                                const idx = images.indexOf(foundVar.hinhanh);
                                if (idx >= 0) { imgIndex = idx; }
                                renderAll();
                            };
                        } else {
                            btn.title = "Size này đã hết hoặc ngừng bán";
                        }
                    } else {
                        btn.className = "size-btn unavailable";
                        btn.title = "Không có sẵn size này";
                    }
                    sizeOptions.appendChild(btn);
                });
            }
            function renderAll() {
                renderGallery();
                renderSizes();
                infoGia.textContent = formatVND(selected.gia);
                infoMa.textContent = selected.ma;
                renderStockBadge(selected);
                const disabled = selected.soluong <= 0 || selected.trangthai !== "1";
                btnAddCart.disabled = disabled;
                btnAddCart.innerHTML = disabled
                    ? 'HẾT HÀNG / KHÔNG KHẢ DỤNG'
                    : 'THÊM VÀO GIỎ HÀNG';
            }
            document.getElementById("qtyMinus").onclick = () => {
                let n = parseInt(qtyInput.value) || 1;
                if (n > 1) qtyInput.value = n - 1;
            };
            document.getElementById("qtyPlus").onclick = () => {
                let n = parseInt(qtyInput.value) || 1;
                // Cho phép cộng số lượng lên tới đúng số tồn kho hiện có
                // của size đang chọn (không giới hạn cứng ở 10 nữa).
                if (n < selected.soluong) {
                    qtyInput.value = n + 1;
                } else {
                    alert("Số lượng đã đạt tối đa tồn kho của size " + selected.kichco + " (" + selected.soluong + ")");
                }
            };
            btnAddCart.onclick = () => {
                const addQty = parseInt(qtyInput.value) || 1;
                const fd = new URLSearchParams();
                fd.append("action", "addToCart");
                fd.append("masanpham", selected.ma);
                fd.append("giasanpham", selected.gia);
                fd.append("kichco", selected.kichco);
                fd.append("soluong", addQty);
                fd.append("trangthai", selected.trangthai === "1" ? "" : (selected.soluong === 0 ? "Hết hàng" : "Ngừng bán"));
                fetch(ctx + "/nguoidungservlet", {
                    method: "POST",
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    body: fd
                })
                .then(r => r.json())
                .then(data => {
                    if (data.success) {
                        cartItemCount += addQty;
                        cartCountBadge.textContent = cartItemCount;
                        alert("Đã thêm " + addQty + " sản phẩm (Size " + selected.kichco + ") vào giỏ hàng thành công!");
                    } else {
                        alert(data.message || "Có lỗi xảy ra khi thêm vào giỏ hàng.");
                    }
                })
                .catch(() => {
                    cartItemCount += addQty;
                    cartCountBadge.textContent = cartItemCount;
                    alert("Đã thêm vào giỏ hàng thành công!");
                });
            };
            renderAll();
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
            // ============ BACK-TO-TOP (đồng bộ với các trang khác) ============
            window.addEventListener("scroll", function () {
                const backTop = document.querySelector(".back-top");
                if (!backTop) return;
                backTop.style.display = window.scrollY > 300 ? "flex" : "none";
            });
            // ============ TÌM KIẾM GỢI Ý SẢN PHẨM (toàn hệ thống, bên trang Sản phẩm) ============
            // Gõ tên sản phẩm -> gợi ý ra sản phẩm bên trang "nguoidung" -> bấm vào
            // gợi ý sẽ nhảy sang trang Sản phẩm và nhấp nháy vàng đúng sản phẩm đó.
            (function () {
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
                        const keyword = removeVietnamese(this.value.trim());
                        searchResult.innerHTML = "";
                        if (keyword === "") {
                            searchResult.style.display = "none";
                            return;
                        }
                        let count = 0;
                        allProducts.forEach(function (spEl) {
                            const name = spEl.getAttribute("data-name");
                            if (name) {
                                const nameSearch = removeVietnamese(name);
                                if (nameSearch.includes(keyword)) {
                                    const item = document.createElement("div");
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
        </script>
<script src="${pageContext.request.contextPath}/js/lodi-yeuthich.js" data-ctx="${pageContext.request.contextPath}"></script>
</body>
</html>
