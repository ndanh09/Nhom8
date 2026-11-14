<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="java.util.*, model.nguoidung, modeldao.nguoidungdao, model.Banner, modeldao.BannerDAO, model.SanPhamYeuThich, modeldao.YeuThichDAO"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
    nguoidungdao __dao = new nguoidungdao();
    ArrayList<nguoidung> __allsp = __dao.sp();

    // Gom các size về 1 dòng đại diện / 1 tên sản phẩm. FIX: trước đây luôn
    // lấy dòng (size) ĐẦU TIÊN gặp trong DB làm đại diện, bất kể còn hàng
    // hay không -> nếu size đầu tiên đó hết hàng thì card báo "HẾT HÀNG"
    // dù các size khác vẫn còn. Nay ưu tiên chọn 1 size CÒN HÀNG làm đại
    // diện (nếu có), chỉ khi TẤT CẢ size đều hết/ngừng bán mới giữ đại diện
    // là 1 dòng hết hàng để hiển thị đúng "HẾT HÀNG".
    LinkedHashMap<String, nguoidung> __unique = new LinkedHashMap<String, nguoidung>();
    for (nguoidung __sp : __allsp) {
        String __ten = __sp.getTensanpham();
        boolean __conHang = "1".equals(__sp.getTrangthai()) && __sp.getSoluongsanpham() > 0;
        nguoidung __daCo = __unique.get(__ten);
        if (__daCo == null) {
            __unique.put(__ten, __sp);
        } else {
            boolean __daCoConHang = "1".equals(__daCo.getTrangthai()) && __daCo.getSoluongsanpham() > 0;
            // Chỉ thay đại diện nếu đại diện hiện tại đang hết hàng còn size
            // này lại còn hàng (nâng cấp đại diện lên size còn hàng).
            if (!__daCoConHang && __conHang) {
                __unique.put(__ten, __sp);
            }
        }
    }
    ArrayList<nguoidung> __listNoiBat = new ArrayList<nguoidung>(__unique.values());
    request.setAttribute("listsp", new ArrayList<nguoidung>(__unique.values()));
    if (__listNoiBat.size() > 25) {
        __listNoiBat = new ArrayList<nguoidung>(__listNoiBat.subList(0, 14));
    }
    request.setAttribute("listNoiBat", __listNoiBat);

    // Banner trang chủ do admin quản lý (menu "Quản lý banner"). null = lỗi DB (vd: chưa chạy
    // migration_banner.sql) -> bên dưới dùng lại bộ banner mặc định cũ để trang không bị trống.
    ArrayList<Banner> __banner = new BannerDAO().getBannerHienThi(Banner.TRANG_CHU);
    request.setAttribute("bannerLoi", __banner == null);
    request.setAttribute("listBanner", __banner);

    // Top áo yêu thích (xếp hạng + ghim/ẩn do quản lý). DAO không ném lỗi: chưa chạy migration_yeuthich.sql
    // thì danh sách rỗng và mục Top tự ẩn.
    ArrayList<SanPhamYeuThich> __topYT = new YeuThichDAO().layTopHienThi();
    request.setAttribute("listTopYT", __topYT);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Trang chủ - LODI AUTHENTIC STORE</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="home"/></jsp:include>
<main class="container">
  <!-- ===== BANNER SLIDESHOW: dữ liệu từ bảng banner (admin: Quản lý banner), hiệu ứng ở js/lodi-slideshow.js ===== -->
  <c:choose>
  <c:when test="${not bannerLoi}">
  <c:if test="${not empty listBanner}">
  <section class="banner" role="region" aria-roledescription="carousel" aria-label="Banner trang chủ">
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
  </c:when>
  <c:otherwise>
  <!-- ===== BANNER SLIDESHOW (giữ nguyên như LODI): mỗi giải đấu 1 slide, hiệu ứng xử lý trong js/lodi-slideshow.js ===== -->
  <section class="banner" role="region" aria-roledescription="carousel" aria-label="Banner các giải đấu">
    <div class="slide active">
      <img src="${pageContext.request.contextPath}/image/banner-laliga.jpg" alt="Áo đấu Real Madrid, FC Barcelona - La Liga">
      <div class="caption"><span class="eyebrow">LA LIGA · 2026-27</span><h2>Real Madrid · Barcelona</h2><p>Real Madrid sân nhà, sân khách cùng FC Barcelona - 4 mẫu áo đấu của bóng đá Tây Ban Nha.</p><a href="${pageContext.request.contextPath}/nguoidungservlet">XEM LA LIGA</a></div>
    </div>
    <div class="slide">
      <img src="${pageContext.request.contextPath}/image/banner-premier.jpg" alt="Áo đấu Manchester United, Manchester City - Premier League" loading="lazy">
      <div class="caption"><span class="eyebrow">PREMIER LEAGUE · 2026-27</span><h2>Man United · Man City</h2><p>Quỷ đỏ Manchester United cùng Manchester City và Arsenal - 6 mẫu áo đấu Ngoại hạng Anh.</p><a href="${pageContext.request.contextPath}/nguoidungservlet">XEM PREMIER LEAGUE</a></div>
    </div>
    <div class="slide">
      <img src="${pageContext.request.contextPath}/image/banner-seriea.jpg" alt="Áo đấu AC Milan, Inter Milan, Juventus - Serie A" loading="lazy">
      <div class="caption"><span class="eyebrow">SERIE A · 2026-27</span><h2>AC Milan · Inter · Juventus</h2><p>Những ông lớn của bóng đá Ý - 6 mẫu áo đấu Serie A mùa giải mới.</p><a href="${pageContext.request.contextPath}/nguoidungservlet">XEM SERIE A</a></div>
    </div>
    <div class="slide">
      <img src="${pageContext.request.contextPath}/image/banner-bundes.jpg" alt="Áo đấu Bayern Munich, Borussia Dortmund - Bundesliga" loading="lazy">
      <div class="caption"><span class="eyebrow">BUNDESLIGA · 2026-27</span><h2>Bayern · Dortmund</h2><p>Bayern Munich và Borussia Dortmund - 4 mẫu áo đấu Bundesliga mùa giải mới.</p><a href="${pageContext.request.contextPath}/nguoidungservlet">XEM BUNDESLIGA</a></div>
    </div>
    <div class="slide">
      <img src="${pageContext.request.contextPath}/image/banner-ligue1.jpg" alt="Áo đấu Paris Saint-Germain - Ligue 1" loading="lazy">
      <div class="caption"><span class="eyebrow">LIGUE 1 · 2026-27</span><h2>Paris Saint-Germain</h2><p>Sân nhà và sân khách của PSG - 2 mẫu áo đấu Ligue 1 mùa giải mới.</p><a href="${pageContext.request.contextPath}/nguoidungservlet">XEM LIGUE 1</a></div>
    </div>
    <button type="button" class="arrow prev" aria-label="Slide trước">&#10094;</button>
    <button type="button" class="arrow next" aria-label="Slide sau">&#10095;</button>
    <div class="dots"><button type="button" class="on" aria-label="Slide 1"></button><button type="button" aria-label="Slide 2"></button><button type="button" aria-label="Slide 3"></button><button type="button" aria-label="Slide 4"></button><button type="button" aria-label="Slide 5"></button></div>
    <div class="progress"><i></i></div>
  </section>
  </c:otherwise>
  </c:choose>

  <div class="chips"><a href="${pageContext.request.contextPath}/nguoidungservlet">Premier League</a><a href="${pageContext.request.contextPath}/nguoidungservlet">La Liga</a><a href="${pageContext.request.contextPath}/nguoidungservlet">Serie A</a><a href="${pageContext.request.contextPath}/nguoidungservlet">Bundesliga</a><a href="${pageContext.request.contextPath}/nguoidungservlet">Ligue 1</a></div>

  <%-- ===== TOP ÁO YÊU THÍCH: xếp hạng theo lượt yêu thích, quản lý ghim/ẩn ở menu "Top sản phẩm yêu thích" (luồng sản phẩm) ===== --%>
  <c:if test="${not empty listTopYT}">
    <span id="topyeuthich"></span>
    <h2 class="section-title">Top áo yêu thích<small>ĐƯỢC KHÁCH HÀNG YÊU THÍCH NHẤT</small></h2>
    <div class="grid">
      <c:forEach var="t" items="${listTopYT}" varStatus="st">
        <div class="card ${t.trangthai=='0'?'disabled-card':''}">
          <a href="${pageContext.request.contextPath}/chitietsanpham?ma=${t.masanpham}" class="thumb">
            <span class="rank-badge ${t.hang == 1 ? 'top1' : ''}">#${t.hang}</span>
            <img src="${pageContext.request.contextPath}/image/${not empty t.hinhanh ? t.hinhanh : 'default-prod.png'}" alt="${fn:escapeXml(t.tensanpham)}">
          </a>
          <button type="button" class="fav-btn" data-masanpham="${t.masanpham}" aria-label="Yêu thích" title="Thêm vào danh sách yêu thích">&#9829;</button>
          <div class="info">
            <h3><a href="${pageContext.request.contextPath}/chitietsanpham?ma=${t.masanpham}">${t.tensanpham}</a></h3>
            <div class="sub">${not empty t.danhmuc ? t.danhmuc : 'Authentic'} · 2026-27</div>
            <div class="price"><fmt:formatNumber value="${t.giasanpham}" type="number" groupingUsed="true"/>đ</div>
            <div class="love">&#9829; <span class="fav-count">${t.soluotyeuthich}</span> lượt yêu thích</div>
            <c:choose>
              <c:when test="${t.tongsoluong > 0 and t.trangthai == '1'}"><span class="stock in">Còn hàng</span></c:when>
              <c:otherwise><span class="stock out">Hết hàng</span></c:otherwise>
            </c:choose>
            <div class="actions">
              <a href="${pageContext.request.contextPath}/chitietsanpham?ma=${t.masanpham}" class="btn dark">Chi tiết</a>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:if>

  <span id="sp"></span>
  <h2 class="section-title">Sản phẩm bán chạy<small>BỘ SƯU TẬP 2026/27</small></h2>
  <div class="grid">
    <c:forEach var="sp" items="${listNoiBat}">
      <c:set var="conHang" value="${sp.soluongsanpham > 0 and sp.trangthai == '1'}"/>
      <div class="card ${sp.trangthai=='0'?'disabled-card':''}">
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
          <div class="sub">${not empty sp.danhmuc ? sp.danhmuc : 'Authentic'} · 2026-27</div>
          <div class="price"><fmt:formatNumber value="${sp.giasanpham}" type="number" groupingUsed="true"/>đ</div>
          <c:choose>
            <c:when test="${conHang}"><span class="stock in">Còn hàng</span></c:when>
            <c:when test="${sp.trangthai == '0'}"><span class="stock off">Ngừng bán</span></c:when>
            <c:otherwise><span class="stock out">Hết hàng</span></c:otherwise>
          </c:choose>
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
    <c:if test="${empty listNoiBat}"><p class="grid-empty">Hiện chưa có sản phẩm nào.</p></c:if>
  </div>
  <c:if test="${not empty listsp and fn:length(listsp) > 14}">
    <div class="more"><a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn">Xem tất cả sản phẩm (${fn:length(listsp)})</a></div>
  </c:if>
</main>

<jsp:include page="_lodi_footer.jsp"/>

<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script src="${pageContext.request.contextPath}/js/lodi-slideshow.js"></script>
<script src="${pageContext.request.contextPath}/js/lodi-yeuthich.js" data-ctx="${pageContext.request.contextPath}"></script>
<script>
        window.addEventListener("scroll",function(){
            const btn=document.querySelector(".back-top");
            if(window.scrollY>300){
                btn.style.display="flex";
            }else{
                btn.style.display="none";
            }
        });

        const userMenu = document.getElementById("userMenu");
        if(userMenu){
            userMenu.addEventListener("click", function(e){
                e.stopPropagation();
                this.classList.toggle("active");
            });
            document.addEventListener("click", function(){
                userMenu.classList.remove("active");
            });
        }

        (function(){
            const badge = document.getElementById("cartCountBadge");
            if(!badge){ return; }
            fetch("${pageContext.request.contextPath}/nguoidungservlet?action=cartcount")
                .then(r => r.json())
                .then(data => { badge.textContent = data.count || 0; })
                .catch(function(){});
        })();

        const daDangNhap = ${not empty sessionScope.user ? 'true' : 'false'};
        document.querySelectorAll(".add-to-cart-btn").forEach(function(btn){
            btn.addEventListener("click", function(){
                if(this.hasAttribute("disabled")) return;
                if(!daDangNhap){
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
                    if(data.success){
                        const badge = document.getElementById("cartCountBadge");
                        if(badge){
                            badge.textContent = (parseInt(badge.textContent) || 0) + 1;
                        }
                        alert("Đã thêm sản phẩm vào giỏ hàng thành công!");
                    } else {
                        alert(data.message || "Có lỗi xảy ra khi thêm vào giỏ hàng.");
                    }
                })
                .catch(function(){
                    thisBtn.disabled = false;
                    alert("Có lỗi xảy ra khi thêm vào giỏ hàng, vui lòng thử lại.");
                });
            });
        });

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
                        searchResult.innerHTML = '<div class="search-item is-empty">Không tìm thấy sản phẩm phù hợp</div>';
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
</body>
</html>
