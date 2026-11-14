<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hóa Đơn #ORD-${donHang.madonhang}</title>
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
        </style>
    </head>
    <body>
        <div class="header">
            <div class="role-title">Hóa Đơn</div>
            <div class="actions">
                <c:choose>
                    <c:when test="${isAdmin}">
                        <a href="${pageContext.request.contextPath}/quanlyhoadonservlet"><i class="fa-solid fa-arrow-left"></i> Quay lại</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/trangthaiservlet"><i class="fa-solid fa-arrow-left"></i> Quay lại</a>
                    </c:otherwise>
                </c:choose>
                <button onclick="window.print()"><i class="fa-solid fa-print"></i> In hóa đơn</button>
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
                        <h2>Hóa Đơn Bán Hàng</h2>
                        <p>Mã đơn hàng: <strong style="color:#8a6a1c;">#ORD-${donHang.madonhang}</strong></p>
                        <p>Ngày đặt: <fmt:formatDate value="${donHang.ngaydat}" pattern="dd/MM/yyyy HH:mm"/></p>
                    </div>
                </div>

                <div class="info-grid">
                    <div class="info-block">
                        <h4>Thông tin khách hàng</h4>
                        <p><strong>${donHang.hoten}</strong></p>
                        <p>SĐT: ${donHang.sodienthoai}</p>
                        <p>Email: ${donHang.email}</p>
                        <p>Địa chỉ: ${donHang.diachi}</p>
                    </div>
                    <div class="info-block">
                        <h4>Thông tin thanh toán</h4>
                        <p>Phương thức: <strong>${donHang.phuongthucthanhtoan}</strong></p>
                        <c:if test="${not empty donHang.magiamgia}">
                            <p>Mã giảm giá: <strong>${donHang.magiamgia}</strong></p>
                        </c:if>
                        <c:if test="${not empty donHang.ghichu}">
                            <p>Ghi chú: ${donHang.ghichu}</p>
                        </c:if>
                    </div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th style="width:38%;">Sản phẩm</th>
                            <th>Kích cỡ</th>
                            <th>Số lượng</th>
                            <th class="text-right">Đơn giá</th>
                            <th class="text-right">Thành tiền</th>
                            <th class="no-print" style="text-align:center;">Chi tiết</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ct" items="${chiTiet}">
                            <tr>
                                <td>
                                    <div class="prod-cell">
                                        <img src="${pageContext.request.contextPath}/image/${not empty ct.hinhanh ? ct.hinhanh : 'default-prod.png'}" alt="${ct.tensanpham}" onerror="this.style.display='none'">
                                        <div>
                                            <div>${not empty ct.tensanpham ? ct.tensanpham : ct.masanpham}</div>
                                            <div style="color:#5a574d; font-size:11px;">Mã SP: ${ct.masanpham}</div>
                                        </div>
                                    </div>
                                </td>
                                <td>${ct.kichco}</td>
                                <td>${ct.soluong}</td>
                                <td class="text-right"><fmt:formatNumber value="${ct.giaban}" type="number" groupingUsed="true"/>đ</td>
                                <td class="text-right" style="color:#8a6a1c; font-weight:600;">
                                    <fmt:formatNumber value="${ct.giaban * ct.soluong}" type="number" groupingUsed="true"/>đ
                                </td>
                                <td class="no-print" style="text-align:center;">
                                    <a class="btn-detail" href="${pageContext.request.contextPath}/chitiethoadonservlet?matrangthai=${ct.matrangthai}">Xem chi tiết</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty chiTiet}">
                            <tr>
                                <td colspan="6" style="text-align:center; color:#5a574d; font-style:italic;">Không có sản phẩm nào trong đơn hàng này.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>

                <div class="summary">
                    <div>
                        <span>Tổng tiền hàng</span>
                        <span><fmt:formatNumber value="${tongTienHang}" type="number" groupingUsed="true"/>đ</span>
                    </div>
                    <c:if test="${donHang.sotiengiam > 0}">
                        <div>
                            <span>Giảm giá</span>
                            <span>- <fmt:formatNumber value="${donHang.sotiengiam}" type="number" groupingUsed="true"/>đ</span>
                        </div>
                    </c:if>
                    <div class="total">
                        <span>Tổng thanh toán</span>
                        <span>
                            <fmt:formatNumber value="${donHang.tongtiensaugiam > 0 ? donHang.tongtiensaugiam : donHang.tongtien}" type="number" groupingUsed="true"/>đ
                        </span>
                    </div>
                </div>

                <div class="footer-note">
                    Cảm ơn quý khách đã mua hàng tại Lodi Authentic Store!
                </div>
            </div>
        </div>
    </body>
</html>
