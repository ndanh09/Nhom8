<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<<<<<<< HEAD
<title>Xin chào - LODI AUTHENTIC STORE</title>
=======
<title>Xin chào - LODI AUTHENTIC STORE</title>
>>>>>>> b47938d62a8bf035c22d569b2e0ab2a8818526a4
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lodi-extra.css">
</head>
<body>
<jsp:include page="_lodi_header.jsp"><jsp:param name="active" value="gioithieu"/></jsp:include>
<main class="container about-page">
  <section class="about-hero">
    <div class="hero-content">
      <span class="eyebrow">THƯƠNG HIỆU ÁO BÓNG ĐÁ CAO CẤP AUTHENTIC</span>
      <h1>LODI <em>AUTHENTIC STORE</em></h1>
      <p>Nơi hội tụ những mẫu áo đấu, giày bóng đá và phụ kiện thể thao chính hãng dành cho những người yêu bóng đá đích thực.</p>
      <a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn">Khám phá sản phẩm</a> <a href="#about" class="btn ghost">Tìm hiểu thêm</a>
    </div>
    <div class="hero-image"><img src="${pageContext.request.contextPath}/image/about-banner.png" alt="LODI Football"><div class="hero-badge"><b>5+</b><span>Năm phát triển</span></div></div>
  </section>

  <h2 class="section-title" id="about">Về chúng tôi<small>LODI - ĐAM MÊ BÓNG ĐÁ</small></h2>
  <div class="box about-box">
    <div class="about-image"><img src="${pageContext.request.contextPath}/image/store.png" alt="LODI Store"><span class="lightbox-trigger media-overlay" data-full="${pageContext.request.contextPath}/image/store.png" data-caption="LODI Store">Xem ảnh</span></div>
    <div class="about-info">
      <h2>Đồ bóng chính hãng - chất lượng tạo niềm tin</h2>
      <p>LODI AUTHENTIC STORE chuyên cung cấp các sản phẩm bóng đá cao cấp như áo đấu câu lạc bộ, đội tuyển quốc gia, giày bóng đá, phụ kiện tập luyện và các sản phẩm thể thao chính hãng.</p>
      <p>Với phong cách phục vụ chuyên nghiệp cùng sự lựa chọn sản phẩm đa dạng, LODI luôn mong muốn mang đến trải nghiệm mua sắm tốt nhất cho mọi tín đồ bóng đá.</p>
      <ul><li>Sản phẩm chính hãng</li><li>Kiểm tra chất lượng</li><li>Hỗ trợ khách hàng 24/7</li><li>Giao hàng toàn quốc</li></ul>
    </div>
  </div>

  <h2 class="section-title">Vì sao chọn LODI?<small>GIÁ TRỊ CỐT LÕI</small></h2>
  <div class="value-grid">
    <div class="value-card"><h3>Chính hãng 100%</h3><p>LODI cam kết cung cấp các sản phẩm bóng đá chất lượng cao, nguồn gốc rõ ràng và đảm bảo tiêu chuẩn.</p></div>
    <div class="value-card"><h3>Chất lượng hàng đầu</h3><p>Mỗi sản phẩm đều được lựa chọn kỹ lưỡng nhằm mang lại sự thoải mái và hiệu suất tốt nhất khi sử dụng.</p></div>
    <div class="value-card"><h3>Cộng đồng bóng đá</h3><p>Không chỉ bán hàng, LODI xây dựng một cộng đồng dành cho những người có chung niềm đam mê bóng đá.</p></div>
  </div>

  <div class="mission-grid">
    <div class="box"><h2>Sứ mệnh</h2><p>Mang những sản phẩm bóng đá chính hãng đến gần hơn với người chơi bóng tại Việt Nam, từ cầu thủ phong trào đến những người yêu bóng đá chuyên nghiệp.</p></div>
    <div class="box"><h2>Tầm nhìn</h2><p>Trở thành một trong những thương hiệu thể thao bóng đá uy tín, hiện đại và được yêu thích nhất trong cộng đồng bóng đá Việt Nam.</p></div>
  </div>

  <div class="stats-strip">
    <div><b>1000+</b><span>Sản phẩm</span></div><div><b>5000+</b><span>Khách hàng</span></div><div><b>50+</b><span>Mẫu áo đấu</span></div><div><b>99%</b><span>Hài lòng</span></div>
  </div>

  <h2 class="section-title">Câu chuyện LODI<small>HÀNH TRÌNH PHÁT TRIỂN</small></h2>
  <div class="timeline">
    <div class="timeline-item"><div class="timeline-year">2021</div><div class="timeline-content"><h3>Khởi đầu</h3><p>LODI AUTHENTIC STORE được thành lập với mong muốn mang đến những sản phẩm bóng đá chất lượng cho người chơi tại Việt Nam.</p></div></div>
    <div class="timeline-item"><div class="timeline-year">2022</div><div class="timeline-content"><h3>Mở rộng sản phẩm</h3><p>Bổ sung nhiều dòng sản phẩm như áo đấu câu lạc bộ, đội tuyển, giày bóng đá và phụ kiện thể thao.</p></div></div>
    <div class="timeline-item"><div class="timeline-year">2024</div><div class="timeline-content"><h3>Xây dựng cộng đồng</h3><p>LODI phát triển mạnh mẽ hơn với cộng đồng khách hàng yêu bóng đá và những người chơi thể thao.</p></div></div>
    <div class="timeline-item"><div class="timeline-year">2026</div><div class="timeline-content"><h3>Định hướng tương lai</h3><p>Tiếp tục nâng cấp trải nghiệm mua sắm, hướng đến trở thành thương hiệu bóng đá cao cấp hàng đầu.</p></div></div>
  </div>

  <h2 class="section-title">Đội ngũ LODI<small>NHỮNG NGƯỜI ĐƯA ĐAM MÊ ĐẾN BẠN</small></h2>
  <div class="team-grid">
    <div class="team-card"><div class="team-image"><img src="${pageContext.request.contextPath}/image/team1.png" alt="Đội ngũ tư vấn"><span class="lightbox-trigger media-overlay" data-full="${pageContext.request.contextPath}/image/team1.png" data-caption="Đội ngũ tư vấn">Xem ảnh</span></div><div class="team-info"><h3>Đội ngũ tư vấn</h3><span>Tư vấn sản phẩm bóng đá</span><p>Giúp khách hàng lựa chọn sản phẩm phù hợp nhất với nhu cầu sử dụng.</p></div></div>
    <div class="team-card"><div class="team-image"><img src="${pageContext.request.contextPath}/image/team2.png" alt="Bộ phận chăm sóc"><span class="lightbox-trigger media-overlay" data-full="${pageContext.request.contextPath}/image/team2.png" data-caption="Bộ phận chăm sóc">Xem ảnh</span></div><div class="team-info"><h3>Bộ phận chăm sóc</h3><span>Hỗ trợ khách hàng</span><p>Luôn đồng hành cùng khách hàng trước và sau khi mua sản phẩm.</p></div></div>
    <div class="team-card"><div class="team-image"><img src="${pageContext.request.contextPath}/image/team3.png" alt="Cộng đồng LODI"><span class="lightbox-trigger media-overlay" data-full="${pageContext.request.contextPath}/image/team3.png" data-caption="Cộng đồng LODI">Xem ảnh</span></div><div class="team-info"><h3>Cộng đồng LODI</h3><span>Football Lifestyle</span><p>Kết nối những người có cùng niềm đam mê với trái bóng.</p></div></div>
  </div>

  <h2 class="section-title">Không gian bóng đá<small>HÌNH ẢNH LODI</small></h2>
  <div class="gallery-grid">
    <div class="gallery-item"><img src="${pageContext.request.contextPath}/image/gallery1.png" alt="LODI Store"><span class="lightbox-trigger gallery-overlay" data-full="${pageContext.request.contextPath}/image/gallery1.png" data-caption="LODI Store">Xem ảnh</span></div><div class="gallery-item"><img src="${pageContext.request.contextPath}/image/gallery2.png" alt="Football Jersey"><span class="lightbox-trigger gallery-overlay" data-full="${pageContext.request.contextPath}/image/gallery2.png" data-caption="Football Jersey">Xem ảnh</span></div><div class="gallery-item"><img src="${pageContext.request.contextPath}/image/gallery3.png" alt="Football Shoes"><span class="lightbox-trigger gallery-overlay" data-full="${pageContext.request.contextPath}/image/gallery3.png" data-caption="Football Shoes">Xem ảnh</span></div><div class="gallery-item"><img src="${pageContext.request.contextPath}/image/gallery4.png" alt="Football Lifestyle"><span class="lightbox-trigger gallery-overlay" data-full="${pageContext.request.contextPath}/image/gallery4.png" data-caption="Football Lifestyle">Xem ảnh</span></div>
  </div>

  <div class="cta-box">
    <h2>Sẵn sàng cháy hết mình cùng LODI?</h2>
    <p>Khám phá ngay những sản phẩm bóng đá chất lượng và nâng cấp phong cách thi đấu của bạn.</p>
    <a href="${pageContext.request.contextPath}/nguoidungservlet" class="btn">Mua sắm ngay</a>
  </div>
</main>
<div class="lightbox-modal" id="lightboxModal">
  <div class="lightbox-close" id="lightboxClose">&times;</div>
  <div class="lightbox-content"><img id="lightboxImg" src="" alt=""><div class="lightbox-caption" id="lightboxCaption"></div></div>
</div>

<jsp:include page="_lodi_footer.jsp"/>
<div class="back-top" onclick="window.scrollTo({top:0,behavior:'smooth'})">&#8593;</div>
<script>
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

            // ============ LIGHTBOX XEM ẢNH LỚN (bấm nút vuông vàng ở gallery) ============
            (function(){
                const modal = document.getElementById("lightboxModal");
                const modalImg = document.getElementById("lightboxImg");
                const modalCaption = document.getElementById("lightboxCaption");
                const closeBtn = document.getElementById("lightboxClose");
                if(!modal || !modalImg){ return; }

                function openLightbox(src, caption){
                    modalImg.src = src;
                    modalImg.alt = caption || "";
                    modalCaption.textContent = caption || "";
                    modal.classList.add("active");
                    document.body.style.overflow = "hidden";
                }
                function closeLightbox(){
                    modal.classList.remove("active");
                    document.body.style.overflow = "";
                }

                document.querySelectorAll(".lightbox-trigger").forEach(function(trigger){
                    trigger.addEventListener("click", function(e){
                        e.stopPropagation();
                        openLightbox(this.dataset.full, this.dataset.caption);
                    });
                });

                closeBtn.addEventListener("click", closeLightbox);
                modal.addEventListener("click", function(e){
                    if(e.target === modal){ closeLightbox(); }
                });
                document.addEventListener("keydown", function(e){
                    if(e.key === "Escape"){ closeLightbox(); }
                });
            })();

            // Trang Giới thiệu là trang tĩnh (không qua servlet) nên lấy số
            // lượng giỏ hàng thật qua AJAX để badge giỏ hàng luôn khớp với
            // các trang khác.
            (function(){
                const badge = document.getElementById("cartCountBadge");
                if(!badge){ return; }
                fetch("${pageContext.request.contextPath}/nguoidungservlet?action=cartcount")
                    .then(r => r.json())
                    .then(data => { badge.textContent = data.count || 0; })
                    .catch(function(){});
            })();
            document.addEventListener("DOMContentLoaded",function(){
                const backTop=document.querySelector(".back-top");
                window.addEventListener("scroll",function(){
                if(window.scrollY>300){
                backTop.style.display="flex";
                }else{
                backTop.style.display="none";
                }
                });
                if(backTop){
                backTop.addEventListener("click",function(){
                window.scrollTo({
                top:0,
                behavior:"smooth"
                });
                });
                }
                const revealElements=document.querySelectorAll(".value-card,.mission-box,.timeline-item,.team-card,.gallery-item,.stat-item,.commit-box,.cta-container");
                const revealObserver=new IntersectionObserver(function(entries){
                entries.forEach(function(entry){
                if(entry.isIntersecting){
                entry.target.classList.add("fade-up");
                }
                });
                },{
                threshold:.15
                });
                revealElements.forEach(function(el){
                revealObserver.observe(el);
                });
                const counters=document.querySelectorAll(".stat-item h2");
                counters.forEach(function(counter){
                let target=parseInt(counter.innerText.replace(/\D/g,""));
                let current=0;
                let speed=target/80;
                let started=false;
                const counterObserver=new IntersectionObserver(function(entries){
                entries.forEach(function(entry){
                if(entry.isIntersecting&&!started){
                started=true;
                let update=setInterval(function(){
                current+=Math.ceil(speed);
                if(current>=target){
                current=target;
                clearInterval(update);
                }
                counter.innerText=current+"+";
                },25);
                }
                });
                },{
                threshold:.7
                });
                counterObserver.observe(counter);
                });
                const buttons=document.querySelectorAll(".btn-gold,.btn-outline");
                buttons.forEach(function(btn){
                btn.addEventListener("mouseenter",function(){
                btn.style.transform="translateY(-5px)";
                });
                btn.addEventListener("mouseleave",function(){
                btn.style.transform="translateY(0)";
                });
                });
                const galleryItems=document.querySelectorAll(".gallery-item");
                galleryItems.forEach(function(item){
                item.addEventListener("mousemove",function(e){
                let rect=item.getBoundingClientRect();
                let x=e.clientX-rect.left;
                let y=e.clientY-rect.top;
                item.style.transform=`perspective(600px) rotateX(${-(y-rect.height/2)/20}deg) rotateY(${(x-rect.width/2)/20}deg) scale(1.03)`;
                });
                item.addEventListener("mouseleave",function(){
                item.style.transform="translateY(0)";
                });
                });
                });

            // ============ TÌM KIẾM GỢI Ý SẢN PHẨM (đồng bộ với Sản phẩm / Giỏ
            // hàng / Đơn hàng / Chi tiết sản phẩm) ============
            // Trang Giới thiệu không tự có danh sách sản phẩm nên lấy qua AJAX
            // từ nguoidungservlet. Gõ tên -> gợi ý -> bấm gợi ý sẽ nhảy sang
            // trang Sản phẩm (nguoidung) và nhấp nháy vàng đúng sản phẩm đó.
            (function(){
                const searchInput = document.getElementById("searchInput");
                const searchResult = document.getElementById("searchResult");
                if(!searchInput || !searchResult){ return; }

                let allProductNames = [];
                fetch("${pageContext.request.contextPath}/nguoidungservlet?action=suggest")
                    .then(r => r.json())
                    .then(data => { allProductNames = data || []; })
                    .catch(function(){ allProductNames = []; });

                function removeVietnamese(str){
                    return str
                    .normalize("NFD")
                    .replace(/[\u0300-\u036f]/g,"")
                    .toLowerCase();
                }

                searchInput.addEventListener("keyup", function(){
                    let keyword = removeVietnamese(this.value.trim());
                    searchResult.innerHTML = "";
                    if(keyword === ""){
                        searchResult.style.display = "none";
                        return;
                    }
                    let count = 0;
                    allProductNames.forEach(function(name){
                        let nameSearch = removeVietnamese(name);
                        if(nameSearch.includes(keyword)){
                            let item = document.createElement("div");
                            item.className = "search-item";
                            item.innerHTML = name;
                            item.onclick = function(){
                                window.location.href = "${pageContext.request.contextPath}/nguoidungservlet?highlight=" + encodeURIComponent(name);
                            };
                            searchResult.appendChild(item);
                            count++;
                        }
                    });
                    if(count > 0){
                        searchResult.style.display = "block";
                    }else{
                        searchResult.innerHTML = '<div class="search-item">Không tìm thấy sản phẩm phù hợp</div>';
                        searchResult.style.display = "block";
                    }
                });

                document.addEventListener("click", function(e){
                    if(!searchInput.contains(e.target) && !searchResult.contains(e.target)){
                        searchResult.style.display = "none";
                    }
                });
            })();
        </script>
</body>
</html>
