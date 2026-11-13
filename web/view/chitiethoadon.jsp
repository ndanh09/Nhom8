<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết sản phẩm - Hóa Đơn #ORD-${donHang.madonhang}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
<style>
            * {
                margin:0;
                padding:0;
                box-sizing:border-box;
                font-family:'Inter',sans-serif;
            }
            body {
                background:#f6f3ec;
                color:#1c1b18;
            }
            .header {
                height:60px;
                background:#0d0d0e;
                display:flex;
                justify-content:space-between;
                align-items:center;
                padding:0 40px;
                box-shadow:0 2px 10px rgba(0, 0, 0, 0.3);
            }
            .header .role-title {
                color:#ead594;
                font-family:'Playfair Display', serif;
                font-size:21px;
                font-weight:700;
                text-transform:uppercase;
                letter-spacing:2px;
            }
            .header .actions a, .header .actions button {
                background:#0d0d0e;
                color:#c9a24b;
                border:1px solid rgba(255,255,255,0.15);
                padding:8px 18px;
                border-radius:8px;
                font-size:12px;
                font-weight:bold;
                text-transform:uppercase;
                letter-spacing:1px;
                text-decoration:none;
                margin-left:10px;
                cursor:pointer;
            }
            .header .actions a:hover, .header .actions button:hover {
                background:#c9a24b;
                color:#0c0c0c;
            }
            .content {
                display:flex;
                justify-content:center;
                padding:40px 20px;
            }
            .invoice-box {
                width:100%;
                max-width:850px;
                background:#ffffff;
                border:2px solid #c9a24b;
                border-radius:12px;
                padding:40px;
                box-shadow:0 0 15px rgba(241, 202, 107, 0.2), inset 0 0 10px rgba(241, 202, 107, 0.05);
            }
            .invoice-top {
                display:flex;
                justify-content:space-between;
                align-items:flex-start;
                border-bottom:2px solid #ead594;
                padding-bottom:20px;
                margin-bottom:25px;
            }
            .invoice-top img {
                max-width:110px;
            }
            .invoice-top .store-name {
                font-family:'Playfair Display', serif;
                color:#8a6a1c;
                font-size:22px;
                font-weight:700;
                margin-top:8px;
            }
            .invoice-title {
                text-align:right;
            }
            .invoice-title h2 {
                font-family:'Playfair Display', serif;
                color:#8a6a1c;
                font-size:26px;
                text-transform:uppercase;
                letter-spacing:1.5px;
            }
            .invoice-title p {
                color:#5a574d;
                font-size:13px;
                margin-top:6px;
            }
            .info-grid {
                display:flex;
                justify-content:space-between;
                gap:20px;
                margin-bottom:30px;
                flex-wrap:wrap;
            }
            .info-block {
                flex:1;
                min-width:240px;
            }
            .info-block h4 {
                color:#8a6a1c;
                font-size:12px;
                text-transform:uppercase;
                letter-spacing:1px;
                margin-bottom:10px;
                border-bottom:1px solid rgba(156,127,67,0.3);
                padding-bottom:6px;
            }
            .info-block p {
                font-size:13px;
                color:#3a372f;
                line-height:1.8;
            }
            .info-block p strong {
                color:#1c1b18;
            }
            table {
                width:100%;
                border-collapse:collapse;
                margin-bottom:20px;
            }
            th {
                background:rgba(156, 127, 67, 0.12);
                color:#6b4f12;
                padding:12px;
                font-size:11px;
                font-weight:bold;
                text-transform:uppercase;
                letter-spacing:0.5px;
                border-bottom:1px solid #8a6a1c;
                text-align:left;
            }
            td {
                padding:12px;
                font-size:13px;
                color:#1c1b18;
                border-bottom:1px solid rgba(156, 127, 67, 0.2);
            }
            .prod-cell {
                display:flex;
                align-items:center;
                gap:10px;
            }
            .prod-cell img {
                width:42px;
                height:42px;
                object-fit:cover;
                border-radius:8px;
                border:1px solid rgba(241, 202, 107, 0.3);
            }
            .text-right { text-align:right; }
            .btn-detail {
                display:inline-block;
                background:#0d0d0e;
                color:#ead594;
                padding:6px 12px;
                border-radius:6px;
                font-size:11px;
                font-weight:700;
                text-transform:uppercase;
                letter-spacing:0.5px;
                text-decoration:none;
                white-space:nowrap;
            }
            .btn-detail:hover { background:#c9a24b; color:#0c0c0c; }
            .summary {
                margin-left:auto;
                width:320px;
            }
            .summary div {
                display:flex;
                justify-content:space-between;
                padding:8px 0;
                font-size:13px;
                color:#3a372f;
            }
            .summary div.total {
                border-top:2px solid #c9a24b;
                margin-top:6px;
                padding-top:14px;
                font-size:17px;
                font-weight:700;
                color:#8a6a1c;
            }
            .footer-note {
                margin-top:30px;
                text-align:center;
                color:#5a574d;
                font-style:italic;
                font-size:12.5px;
                border-top:1px dashed rgba(156,127,67,0.3);
                padding-top:16px;
            }
            @media print {
                .header { display:none; }
                .no-print { display:none; }
                body { background:#fff; color:#000; }
                .invoice-box { border-color:#e6dfcd; box-shadow:none; }
                .content { padding:0; }
            }
        
            .detail-grid { display:flex; gap:28px; margin-bottom:28px; flex-wrap:wrap; }
            .detail-img { width:220px; height:220px; object-fit:cover; border-radius:12px; border:1px solid rgba(156,127,67,0.4); background:#f2efe4; }
            .detail-info { flex:1; min-width:260px; }
            .detail-info h3 { font-family:'Playfair Display',serif; color:#1c1b18; font-size:22px; margin-bottom:6px; }
            .kv { display:flex; justify-content:space-between; gap:16px; padding:9px 0; border-bottom:1px dashed rgba(156,127,67,0.3); font-size:13.5px; color:#3a372f; }
            .kv span:last-child { color:#1c1b18; font-weight:600; text-align:right; }
            .kv.total span:last-child { color:#8a6a1c; font-size:16px; }
            .badge { display:inline-block; padding:4px 12px; border-radius:20px; font-size:11.5px; font-weight:700; text-transform:uppercase; }
            .st-0 { background:#fdecc8; color:#8a5a00; } .st-1 { background:#d9e8fb; color:#1f5fa8; }
            .st-4 { background:#e1d9f7; color:#4b2f9a; } .st-2 { background:#d4efdc; color:#1f6b3a; }
            .st-3 { background:#f8d7d4; color:#b3261e; }
            .steps { display:flex; justify-content:space-between; margin:6px 0 30px; position:relative; }
            .steps::before { content:""; position:absolute; top:15px; left:8%; right:8%; height:2px; background:#e1d6b8; }
            .step { position:relative; z-index:1; flex:1; text-align:center; font-size:12px; color:#5a574d; }
            .step i { display:flex; align-items:center; justify-content:center; width:32px; height:32px; margin:0 auto 6px; border-radius:50%; background:#fff; border:2px solid #e1d6b8; font-size:13px; font-style:normal; }
            .step.done { color:#1c1b18; font-weight:600; }
            .step.done i { background:#c9a24b; border-color:#c9a24b; color:#0c0c0c; }
            .lock-note { padding:10px 14px; border-radius:8px; background:#f8d7d4; color:#b3261e; font-size:13px; font-weight:600; margin-bottom:20px; }
        </style>
    </head>
    <body>
        <div class="header">
            <div class="role-title">Chi tiết sản phẩm</div>
            <div class="actions">
                <a href="${pageContext.request.contextPath}/hoadonservlet?madonhang=${donHang.madonhang}"><i class="fa-solid fa-arrow-left"></i> Quay lại hóa đơn</a>
                <button onclick="window.print()"><i class="fa-solid fa-print"></i> In</button>
            </div>
        </div>

        <div class="content">
            <div class="invoice-box">

                <div class="invoice-top">
                    <div>
                        <img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo" onerror="this.style.display='none'">
                        <div class="store-name">Lodi Authentic Store</div>
                    </div>
                    <div class="invoice-title">
                        <h2>Chi tiết sản phẩm</h2>
                        <p>Thuộc hóa đơn: <strong style="color:#8a6a1c;">#ORD-${donHang.madonhang}</strong></p>
                        <p>Ngày đặt: <fmt:formatDate value="${sp.ngaymua}" pattern="dd/MM/yyyy HH:mm"/></p>
                    </div>
                </div>

                <c:if test="${sp.donhangKhoa}">
                    <div class="lock-note"><i class="fa-solid fa-lock"></i> Đơn hàng này đang bị khóa, không thể thao tác cho tới khi mở khóa.</div>
                </c:if>

                <div class="detail-grid">
                    <img class="detail-img" src="${pageContext.request.contextPath}/image/${not empty sp.hinhanh ? sp.hinhanh : 'default-prod.png'}" alt="${sp.tensanpham}" onerror="this.style.display='none'">
                    <div class="detail-info">
                        <h3>${not empty sp.tensanpham ? sp.tensanpham : sp.masanpham}</h3>
                        <div style="margin-bottom:10px;">
                            <c:choose>
                                <c:when test="${sp.tinhtrang == 0}"><span class="badge st-0">Chờ xác nhận</span></c:when>
                                <c:when test="${sp.tinhtrang == 1}"><span class="badge st-1">Đang giao</span></c:when>
                                <c:when test="${sp.tinhtrang == 4}"><span class="badge st-4">Đã giao - chờ khách xác nhận</span></c:when>
                                <c:when test="${sp.tinhtrang == 2}"><span class="badge st-2">Đã nhận</span></c:when>
                                <c:otherwise><span class="badge st-3">Hủy đơn</span></c:otherwise>
                            </c:choose>
                        </div>
                        <div class="kv"><span>Mã sản phẩm</span><span>${sp.masanpham}</span></div>
                        <c:if test="${not empty sp.danhmuc}">
                            <div class="kv"><span>Danh mục</span><span>${sp.danhmuc}</span></div>
                        </c:if>
                        <div class="kv"><span>Kích cỡ</span><span>${sp.kichco}</span></div>
                        <div class="kv"><span>Số lượng</span><span>${sp.soluong}</span></div>
                        <div class="kv"><span>Đơn giá</span><span><fmt:formatNumber value="${sp.giaban}" type="number" groupingUsed="true"/>đ</span></div>
                        <div class="kv total"><span>Thành tiền</span><span><fmt:formatNumber value="${sp.giaban * sp.soluong}" type="number" groupingUsed="true"/>đ</span></div>
                        <c:if test="${donHang.sotiengiam > 0}">
                            <div class="kv"><span>Mã giảm giá của hóa đơn</span><span>${donHang.magiamgia} (giá trên đã tính giảm)</span></div>
                        </c:if>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${sp.tinhtrang == 3}">
                        <div class="steps" style="justify-content:center;"><div class="step"><i style="background:#b3261e;border-color:#b3261e;color:#fff;">&#10005;</i>Sản phẩm này đã bị hủy / hoàn trả</div></div>
                    </c:when>
                    <c:otherwise>
                        <div class="steps">
                            <div class="step done"><i>1</i>Đặt hàng</div>
                            <div class="step ${sp.tinhtrang == 1 || sp.tinhtrang == 4 || sp.tinhtrang == 2 ? 'done' : ''}"><i>2</i>Đang giao</div>
                            <div class="step ${sp.tinhtrang == 4 || sp.tinhtrang == 2 ? 'done' : ''}"><i>3</i>Đã giao</div>
                            <div class="step ${sp.tinhtrang == 2 ? 'done' : ''}"><i>4</i>Khách đã nhận</div>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="info-grid">
                    <div class="info-block">
                        <h4>Thông tin người nhận</h4>
                        <p><strong>${donHang.hoten}</strong></p>
                        <p>SĐT: ${donHang.sodienthoai}</p>
                        <p>Địa chỉ: ${donHang.diachi}</p>
                        <c:if test="${not empty donHang.ghichu}">
                            <p>Ghi chú: ${donHang.ghichu}</p>
                        </c:if>
                    </div>
                    <div class="info-block">
                        <h4>Giao hàng &amp; thanh toán</h4>
                        <p>Phương thức: <strong>${donHang.phuongthucthanhtoan}</strong></p>
                        <c:choose>
                            <c:when test="${vanChuyen != null}">
                                <p>Người vận chuyển: <strong>${vanChuyen.hoten}</strong></p>
                                <p>SĐT vận chuyển: ${vanChuyen.sodienthoai}</p>
                            </c:when>
                            <c:otherwise>
                                <p>Người vận chuyển: <strong>Chưa có người nhận đơn</strong></p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="footer-note">
                    Cảm ơn quý khách đã mua hàng tại Lodi Authentic Store!
                </div>
            </div>
        </div>
    </body>
</html>
