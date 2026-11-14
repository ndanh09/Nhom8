<%@page contentType="text/html" pageEncoding="UTF-8" import="java.util.ArrayList, model.Banner, modeldao.BannerDAO"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
    // Banner trang sản phẩm do admin quản lý (menu "Quản lý banner"). Lỗi DB / không có banner -> không hiện gì.
    ArrayList<Banner> __banner = new BannerDAO().getBannerHienThi(Banner.SAN_PHAM);
    request.setAttribute("listBanner", __banner);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sản phẩm - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-chatbot.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="sanpham"/></jsp:include>
<main class="container">
  <span id="highlightParam" data-value="${fn:escapeXml(param.highlight)}" style="display:none;"></span>

  <c:if test="${not empty listBanner}">
  <section class="banner banner-sp" role="region" aria-roledescription="carousel" aria-label="Banner trang sản phẩm">
    <c:forEach var="b" items="${listBanner}" varStatus="st">
    <div class="slide ${st.first ? 'active' : ''}">
      <img src="${pageContext.request.contextPath}/image/${fn:escapeXml(b.hinhanh)}" alt="${fn:escapeXml(b.tieude)}" ${st.first ? '' : 'loading="lazy"'}>
      <c:if test="${not empty b.nhan or not empty b.tieude or not empty b.mota or (not empty b.nuttext and not empty b.lienket)}">
      <div class="caption">
        <c:if test="${not empty b.nhan}"><span class="eyebrow"><c:out value="${b.nhan}"/></span></c:if>
        <c:if test="${not empty b.tieude}"><h2><c:out value="${b.tieude}"/></h2></c:if>
        <c:if test="${not empty b.mota}"><p><c:out value="${b.mota}"/></p></c:if>
        <c:if test="${not empty b.nuttext and not empty b.lienket}"><a href="${b.lienketNgoai ? '' : pageContext.request.contextPath}${b.lienketNgoai ? '' : '/'}${fn:escapeXml(b.lienket)}"<c:if test="${b.lienketNgoai}"> target="_blank" rel="noopener"</c:if>><c:out value="${b.nuttext}"/></a></c:if>
      </div>
      </c:if>
    </div>
    </c:forEach>
    <c:if test="${fn:length(listBanner) > 1}">
    <button type="button" class="arrow prev" aria-label="Slide trước">&#10094;</button>
    <button type="button" class="arrow next" aria-label="Slide sau">&#10095;</button>
    <div class="dots"><c:forEach var="b" items="${listBanner}" varStatus="st"><button type="button" class="${st.first ? 'on' : ''}" aria-label="Slide ${st.count}"></button></c:forEach></div>
    <div class="progress"><i></i></div>
    </c:if>
  </section>
  </c:if>

  <div class="page-head page-head-chat">
    <h2 class="section-title">Sản phẩm nổi bật<small>BỘ SƯU TẬP 2026/27</small></h2>
    <%-- Nút mở màn hình chatbot riêng (ChatbotServlet -> view/chatbot.jsp) --%>
    <a href="${pageContext.request.contextPath}/chatbot" class="chat-cta" title="Hỏi trợ lý LODI về giá, size, đơn hàng..."><span class="dot"></span>Chat với trợ lý LODI</a>
  </div>

  <c:if test="${not empty success}">
    <div class="alert success" style="display:flex"><span class="ico">&#10003;</span><div>${success}</div></div>
  </c:if>

  <div class="chips" id="categoryFilter">
    <a href="javascript:void(0)" class="category-btn active" data-danhmuc="all">Tất cả</a>
    <a href="javascript:void(0)" class="category-btn" data-danhmuc="Áo ĐTQG">Áo ĐTQG</a>
    <a href="javascript:void(0)" class="category-btn" data-danhmuc="Áo CLB">Áo CLB</a>
    <a href="javascript:void(0)" class="category-btn" data-danhmuc="Áo Retro ĐTQG &amp; CLB">Áo Retro ĐTQG &amp; CLB</a>
    <a href="${pageContext.request.contextPath}/magiamgiaservlet">Mã giảm giá</a>
  </div>

  <div class="grid" id="productGrid">
    <c:forEach var="sp" items="${listsp}">
      <div class="card product-card ${sp.trangthai=='0'?'disabled-card':''}" data-name="${fn:escapeXml(sp.tensanpham)}" data-danhmuc="${fn:escapeXml(sp.danhmuc)}">
        <a href="${pageContext.request.contextPath}/chitietsanpham?ma=${sp.masanpham}" class="thumb">
          <c:choose>
            <c:when test="${sp.trangthai == '0'}"><span class="tag out">NGỪNG BÁN</span></c:when>
            <c:when test="${sp.soluongsanpham <= 0}"><span class="tag out">HẾT HÀNG</span></c:when>
            <c:otherwise><span class="tag">AUTHENTIC</span></c:otherwise>
          </c:choose>
          <img src="${pageContext.request.contextPath}/image/${not empty sp.hinhanh ? sp.hinhanh : 'default-prod.png'}" alt="${fn:escapeXml(sp.tensanpham)}">
        </a>
        <button type="button" class="fav-btn" data-masanpham="${sp.masanpham}" aria-label="Yêu thích" title="Thêm vào danh sách yêu thích">&#9829;</button>
        <div class="info">
          <h3><a href="${pageContext.request.contextPath}/chitietsanpham?ma=${sp.masanpham}">${sp.tensanpham}</a></h3>
          <div class="sub">
            <c:if test="${not empty sp.danhmuc}">${sp.danhmuc} · </c:if>Mã:
            <c:set var="sizeSuffix" value="-${sp.kichco}"/>
            ${fn:endsWith(sp.masanpham, sizeSuffix) ? fn:substring(sp.masanpham, 0, fn:length(sp.masanpham) - fn:length(sizeSuffix)) : sp.masanpham}
          </div>
          <div class="price"><fmt:formatNumber value="${sp.giasanpham}" pattern="#,###"/>đ</div>
          <c:choose>
            <c:when test="${sp.soluongsanpham > 0 && sp.trangthai=='1'}"><span class="stock in">Còn hàng</span></c:when>
            <c:when test="${sp.soluongsanpham == 0}"><span class="stock out">Hết hàng</span></c:when>
            <c:otherwise><span class="stock off">Ngừng bán</span></c:otherwise>
          </c:choose>
          <%-- Mỗi card là 1 sản phẩm đại diện theo tên/size. "Thêm vào giỏ" thêm thẳng size đang hiển thị;
               muốn đổi size thì bấm "Chi tiết". --%>
          <div class="actions">
            <button type="button" class="btn add-to-cart-btn"
                    data-masanpham="${sp.masanpham}"
                    data-gia="${sp.giasanpham}"
                    data-kichco="${sp.kichco}"
                    data-trangthai="${sp.soluongsanpham==0?'Hết hàng':(sp.trangthai!='1'?'Ngừng bán':'')}"
                    ${(sp.trangthai!='1' || sp.soluongsanpham==0)?'disabled':''}>Thêm vào giỏ</button>
            <a href="${pageContext.request.contextPath}/chitietsanpham?ma=${sp.masanpham}" class="btn dark">Chi tiết</a>
          </div>
        </div>
      </div>
    </c:forEach>
    <c:if test="${empty listsp}"><p class="grid-empty">Hiện chưa có sản phẩm nào.</p></c:if>
  </div>
</main>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script src="${pageContext.request.contextPath}/js/lodi-slideshow.js"></script>
<script>
            // "Thêm vào giỏ hàng" ở trang người dùng: nếu chưa đăng nhập thì
            // báo cần đăng nhập trước, không chuyển trang. Nếu đã đăng nhập
            // rồi thì mới chuyển sang trang chi tiết sản phẩm để chọn size
            // trước khi thêm vào giỏ ở đó.
            const daDangNhap = ${not empty sessionScope.user ? 'true' : 'false'};
            const cartCountBadgeNguoiDung = document.querySelector(".cart-count");
            document.querySelectorAll(".add-to-cart-btn").forEach(btn => {
                btn.addEventListener("click", function () {
                    if (this.hasAttribute("disabled")) return;
                    if (!daDangNhap) {
                        alert("Bạn cần đăng nhập trước khi thêm vào giỏ hàng!");
                        window.location.href = "${pageContext.request.contextPath}/view/dangnhap.jsp";
                        return;
                    }
                    const thisBtn = this;
                    thisBtn.disabled = true;
                    const fd = new URLSearchParams();
                    fd.append("action", "addToCart");
                    fd.append("masanpham", thisBtn.dataset.masanpham);
                    fd.append("giasanpham", thisBtn.dataset.gia);
                    fd.append("kichco", thisBtn.dataset.kichco);
                    fd.append("soluong", 1);
                    fd.append("trangthai", thisBtn.dataset.trangthai || "");
                    fetch("${pageContext.request.contextPath}/nguoidungservlet", {
                        method: "POST",
                        headers: {"Content-Type": "application/x-www-form-urlencoded"},
                        body: fd
                    })
                    .then(r => r.json())
                    .then(data => {
                        thisBtn.disabled = false;
                        if (data.success) {
                            if (cartCountBadgeNguoiDung) {
                                cartCountBadgeNguoiDung.textContent = (parseInt(cartCountBadgeNguoiDung.textContent) || 0) + 1;
                            }
                            alert("Đã thêm sản phẩm vào giỏ hàng thành công!");
                        } else {
                            alert(data.message || "Có lỗi xảy ra khi thêm vào giỏ hàng.");
                        }
                    })
                    .catch(() => {
                        thisBtn.disabled = false;
                        alert("Có lỗi xảy ra khi thêm vào giỏ hàng, vui lòng thử lại.");
                    });
                });
            });
            const searchInput = document.getElementById("searchInput");
            const searchResult = document.getElementById("searchResult");
            const products = document.querySelectorAll(".product-card");

            // ==== Lọc sản phẩm theo danh mục ====
            // FIX: bỏ lọc theo kích cỡ vì mỗi card giờ là 1 sản phẩm gộp
            // nhiều size, chọn size chuyển sang làm ở trang chi tiết.
            const categoryButtons = document.querySelectorAll("#categoryFilter .category-btn");

            let currentDanhmuc = "all";

            function applyFilter() {
                products.forEach(product => {
                    const matchDanhmuc = currentDanhmuc === "all" || product.getAttribute("data-danhmuc") === currentDanhmuc;
                    product.style.display = matchDanhmuc ? "" : "none";
                });
            }

            categoryButtons.forEach(btn => {
                btn.addEventListener("click", function () {
                    categoryButtons.forEach(b => b.classList.remove("active"));
                    this.classList.add("active");
                    currentDanhmuc = this.getAttribute("data-danhmuc");
                    applyFilter();
                });
            });
            // ==== Hết phần lọc danh mục ====

            function removeVietnamese(str){
                return str
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g,"")
                .toLowerCase();
            }
            searchInput.addEventListener("keyup",function(){
                let keyword = removeVietnamese(this.value.trim());
                searchResult.innerHTML="";
                if(keyword===""){
                    searchResult.style.display="none";
                    return;
                }
                let count=0;
                products.forEach(product=>{
                    let name = product.getAttribute("data-name");
                    if(name){
                        let nameSearch = removeVietnamese(name);
                        if(nameSearch.includes(keyword)){
                            let item=document.createElement("div");
                            item.className="search-item";
                            item.innerHTML=name;
                            item.onclick = function () {
                            searchInput.value = name;
                            searchResult.style.display = "none";
                            product.scrollIntoView({
                                behavior: "smooth",
                                block: "center"
                            });
                            // Xóa highlight cũ
                            document.querySelectorAll(".product-highlight").forEach(el=>{
                                el.classList.remove("product-highlight");
                            });
                            // Reset animation
                            void product.offsetWidth;
                            product.style.animation = "none";
                            product.offsetHeight;
                            product.style.animation = "";
                            product.classList.remove("product-highlight");
                            product.offsetHeight;
                            product.classList.add("product-highlight");
                        };
                            searchResult.appendChild(item);
                            count++;
                        }
                    }
                });
                if(count>0){
                    searchResult.style.display="block";
                }else{
                    searchResult.innerHTML=
                    `<div class="search-item">
                        Không tìm thấy sản phẩm
                    </div>`;
                    searchResult.style.display="block";
                }
            });

            // Nếu người dùng tìm kiếm từ trang chủ (có truyền ?keyword=...),
            // tự động chạy tìm kiếm và hiện kết quả ngay khi vào trang.
            if(searchInput.value.trim() !== ""){
                searchInput.dispatchEvent(new Event("keyup"));
                searchInput.focus();
            }

            // Nếu được điều hướng từ ô tìm kiếm ở trang Đơn hàng / Giỏ hàng
            // (có truyền ?highlight=...), tự động cuộn tới và nhấp nháy vàng
            // đúng sản phẩm đó.
            const highlightEl = document.getElementById("highlightParam");
            const highlightParam = highlightEl ? highlightEl.getAttribute("data-value") : "";
            if(highlightParam && highlightParam.trim() !== ""){
                let target = null;
                let targetKeyword = removeVietnamese(highlightParam.trim());
                products.forEach(function(p){
                    if(!target){
                        let n = p.getAttribute("data-name");
                        if(n && removeVietnamese(n) === targetKeyword){
                            target = p;
                        }
                    }
                });
                if(!target){
                    products.forEach(function(p){
                        if(!target){
                            let n = p.getAttribute("data-name");
                            if(n && removeVietnamese(n).includes(targetKeyword)){
                                target = p;
                            }
                        }
                    });
                }
                if(target){
                    setTimeout(function(){
                        target.scrollIntoView({behavior:"smooth", block:"center"});
                        document.querySelectorAll(".product-highlight").forEach(function(el){
                            el.classList.remove("product-highlight");
                        });
                        void target.offsetWidth;
                        target.style.animation = "none";
                        target.offsetHeight;
                        target.style.animation = "";
                        target.classList.remove("product-highlight");
                        target.offsetHeight;
                        target.classList.add("product-highlight");
                    }, 300);
                }
            }

            const userMenu = document.getElementById("userMenu");
            if(userMenu){
                userMenu.addEventListener("click",function(e){
                    e.stopPropagation();
                    this.classList.toggle("active");
                });
                document.addEventListener("click",function(){
                    userMenu.classList.remove("active");
                });
            }
        </script>
<script>
window.addEventListener("scroll",function(){var b=document.querySelector(".back-top");if(b){b.style.display=window.scrollY>300?"flex":"none";}});
</script>
<script src="${pageContext.request.contextPath}/js/lodi-yeuthich.js" data-ctx="${pageContext.request.contextPath}"></script>
</body>
</html>
