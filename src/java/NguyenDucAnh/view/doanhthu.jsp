<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>LODI - Quản Lý Doanh Thu Theo Ngày</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"
                onerror="this.onerror=null; var s=document.createElement('script'); s.src='https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.4/chart.umd.min.js'; document.head.appendChild(s);"></script>

        <style>
            * {
                margin:0;
                padding:0;
                box-sizing:border-box;
                font-family:'Inter', sans-serif;
            }
            body {
                background:#e7e2d2;
                color:#1c1b18;
                -webkit-font-smoothing:antialiased;
                display:flex;
                flex-direction:column;
                min-height:100vh;
                position:relative;
            }

            body::before {
                content:"";
                position:fixed;
                top:0;
                left:0;
                width:100%;
                height:100%;
                background-image:#ffffff;
                background-size:60px 60px;
                z-index:0;
                pointer-events:none;
            }

            /* HEADER MẠ VÀNG */
            .top-header {
                width:100%;
                height:70px;
                background:#0d0d0e;
                display:flex;
                justify-content:space-between;
                align-items:center;
                padding:0 35px;
                box-shadow:0 4px 25px rgba(40,30,0,0.14);
                position:fixed;
                top:0;
                left:0;
                z-index:1000;
                border-bottom:2px solid #8a6a1c;
            }
            .top-header .header-title {
                font-family:'Playfair Display', serif;
                font-size:22px;
                font-weight:700;
                color:#ead594;
                letter-spacing:2px;
                text-transform:uppercase;
                text-shadow:none;
            }
            .header-admin {
                display:flex;
                align-items:center;
                gap:8px;
                background:rgba(15, 15, 15, 0.85);
                padding:8px 18px;
                border-radius:20px;
                color:#c9a24b;
                font-size:13px;
                font-weight:600;
                text-transform:uppercase;
                border:1px solid rgba(201,162,75,0.4);
                box-shadow:inset 0 1px 3px rgba(0,0,0,0.5);
            }
            .header-admin i {
                color:#c9a24b;
                font-size:15px;
            }

            .wrapper {
                display:flex;
                margin-top:70px;
                min-height:calc(100vh - 70px);
                position:relative;
                z-index:2;
            }

            /* SIDEBAR ĐỒNG BỘ */
            .sidebar {
                width:275px;
                background:#0d0d0e;
                border-right:2px solid #c9a24b;
                flex-shrink:0;
                position:fixed;
                top:70px;
                left:0;
                bottom:0;
                z-index:999;
                overflow-y:auto;
                box-shadow:5px 0 25px rgba(0,0,0,0.5);
            }
            .sidebar::-webkit-scrollbar { width:5px; }
            .sidebar::-webkit-scrollbar-thumb { background:#0d0d0e; border-radius:10px; }
            .sidebar::-webkit-scrollbar-thumb:hover { background:#c9a24b; }

            .sidebar-logo {
                text-align:center;
                padding:22px 15px;
                border-bottom:2px solid #c9a24b;
                display:flex;
                justify-content:center;
                align-items:center;
                background:rgba(255,255,255,0.01);
                margin-bottom:15px;
            }
            .sidebar-logo img {
                max-width:130px;
                height:auto;
                object-fit:contain;
                filter:drop-shadow(0px 0px 10px rgba(201,162,75,0.4));
                transition:transform 0.3s ease;
            }
            .sidebar-logo img:hover { transform:scale(1.05); }

            .sidebar ul {
                list-style:none;
                width:100%;
                padding:0 12px;
            }
            .sidebar ul li {
                margin-bottom:4px;
            }
            .sidebar ul li a {
                display:flex;
                align-items:center;
                gap:14px;
                padding:12px 16px;
                color:#a3a3a3;
                font-size:13.5px;
                font-weight:500;
                text-decoration:none;
                transition:all 0.25s ease-in-out;
                border-radius:10px;
                white-space:nowrap;
            }
            .sidebar ul li a .icon {
                font-size:15px;
                width:20px;
                text-align:center;
                color:#c9a24b;
                flex-shrink:0;
                transition:color 0.25s ease;
            }
            .sidebar ul li a:hover {
                background:rgba(201,162,75,0.08);
                color:#ead594;
                transform:translateX(4px);
            }
            .sidebar ul li a:hover .icon {
                color:#c9a24b;
                text-shadow:0 0 10px rgba(201,162,75,0.5);
            }
            .sidebar ul li.active a {
                background:linear-gradient(135deg, rgba(201,162,75,0.2), rgba(201,162,75,0.05));
                color:#ffffff;
                font-weight:600;
                border:1px solid rgba(201,162,75,0.5);
                box-shadow:0 4px 15px rgba(0,0,0,0.3);
            }
            .sidebar ul li.active a .icon {
                color:#c9a24b;
                text-shadow:0 0 10px rgba(201,162,75,0.6);
            }

            /* VÙNG NỘI DUNG CHÍNH */
            .content {
                margin-left:275px;
                width:calc(100% - 275px);
                padding:40px 45px;
                background:#f2efe4;
            }

            .main-container-box {
                border:2px solid #c9a24b;
                border-radius:12px;
                padding:35px;
                background:#f2efe4;
                box-shadow:0 8px 30px rgba(40,30,0,0.14);
                width:100%;
            }

            .main-container-box h2 {
                text-align:left;
                color:#1c1b18;
                font-family:'Playfair Display', serif;
                font-size:32px;
                font-weight:700;
                letter-spacing:1px;
                margin-bottom:10px;
                text-transform:uppercase;
                background:none;
                -webkit-background-clip:text;
                -webkit-text-fill-color:#0d0d0e;
            }
            .title-line {
                height:2px;
                background:linear-gradient(90deg, #c9a24b, transparent);
                margin-bottom:30px;
            }

            /* FORM LỌC */
            .card-form {
                background:transparent;
                padding:0 0 30px 0;
                border-bottom:1px solid rgba(201,162,75,0.2);
                margin-bottom:35px;
            }
            .form-grid {
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:20px 30px;
            }
            .form-group {
                display:flex;
                flex-direction:column;
                gap:6px;
            }
            .form-group label {
                font-size:12px;
                font-weight:600;
                color:#7d7a70;
                text-transform:uppercase;
                letter-spacing:0.5px;
            }
            .form-group input {
                background:#ebe6d6;
                border:1px solid rgba(201,162,75,0.4);
                padding:12px 15px;
                color:#1c1b18;
                border-radius:8px;
                font-size:13.5px;
                outline:none;
                transition:all 0.3s ease;
                width:100%;
            }
            .form-group input:focus {
                border-color:#c9a24b;
                box-shadow:0 0 0 3px rgba(201,162,75,0.15);
                background:#ebe6d6;
            }
            .btn-group {
                margin-top:25px;
                display:flex;
                gap:15px;
                justify-content:flex-start;
            }

            /* NÚT THAO TÁC SANG TRỌNG */
            .btn-gold {
                background:linear-gradient(135deg, #c9a24b, #8a6a1c);
                color:#000;
                border:none;
                padding:11px 24px;
                font-weight:700;
                font-size:12.5px;
                text-transform:uppercase;
                cursor:pointer;
                border-radius:8px;
                letter-spacing:0.5px;
                transition:all 0.3s ease;
                box-shadow:0 4px 15px rgba(201,162,75,0.3);
            }
            .btn-gold:hover {
                opacity:0.9;
                transform:translateY(-2px);
            }
            .btn-outline {
                background:linear-gradient(135deg, #2a2a2a, #f2efe4);
                color:#1c1b18;
                border:1px solid rgba(201,162,75,0.35);
                padding:11px 24px;
                font-weight:700;
                font-size:12.5px;
                text-transform:uppercase;
                cursor:pointer;
                border-radius:8px;
                letter-spacing:0.5px;
                transition:all 0.3s ease;
                box-shadow:0 4px 15px rgba(40,30,0,0.14);
            }
            .btn-outline:hover {
                background:linear-gradient(135deg, #333333, #f2efe4);
                border-color:#c9a24b;
                color:#8a6a1c;
                transform:translateY(-2px);
            }

            /* KHUNG BIỂU ĐỒ */
            .chart-container {
                background:#f2efe4;
                border:2px solid #c9a24b;
                border-radius:12px;
                padding:25px;
                margin-bottom:35px;
                box-shadow:0 8px 30px rgba(40,30,0,0.14);
                width:100%;
            }
            .chart-container h3 {
                color:#8a6a1c;
                font-family:'Playfair Display', serif;
                font-size:20px;
                font-weight:600;
                letter-spacing:1px;
                margin-bottom:20px;
                text-transform:uppercase;
            }
            .chart-summary {
                display:flex;
                gap:20px;
                margin-bottom:20px;
                flex-wrap:wrap;
            }
            .chart-summary .summary-item {
                background:rgba(201,162,75,0.05);
                border:1px solid rgba(201,162,75,0.2);
                border-radius:8px;
                padding:15px 20px;
                flex:1;
                min-width:200px;
            }
            .chart-summary .summary-item .label {
                font-size:11px;
                color:#7d7a70;
                text-transform:uppercase;
                letter-spacing:0.5px;
                margin-bottom:6px;
                font-weight:600;
            }
            .chart-summary .summary-item .value {
                font-size:18px;
                font-weight:700;
                color:#1c1b18;
            }
            .chart-summary .summary-item .value.up {
                color:#2f7d4a;
            }
            .chart-summary .summary-item .value.down {
                color:#b3261e;
            }
            .chart-wrap {
                position:relative;
                height:320px;
                width:100%;
            }
            .chart-empty {
                padding:50px;
                text-align:center;
                color:#7d7a70;
                font-style:italic;
            }

            /* BẢNG BIỂU DOANH THU */
            .table-container {
                background:#f2efe4;
                border:2px solid #c9a24b;
                border-radius:12px;
                overflow-x:auto;
                box-shadow:0 8px 30px rgba(40,30,0,0.14);
                width:100%;
            }
            table {
                width:100%;
                border-collapse:collapse;
                min-width:900px;
            }
            th {
                background:#0d0d0e;
                color:#c9a24b;
                padding:16px 18px;
                font-size:12px;
                font-weight:600;
                text-transform:uppercase;
                letter-spacing:0.5px;
                text-align:center;
                border-bottom:2px solid #c9a24b;
                border-right:1px solid rgba(201,162,75,0.2);
            }
            th:last-child {
                border-right:none;
            }
            td {
                padding:15px 18px;
                color:#1c1b18;
                font-size:13.5px;
                font-weight:500;
                text-align:center;
                border-bottom:1px solid rgba(201,162,75,0.2);
                border-right:1px solid rgba(201,162,75,0.15);
                white-space:nowrap;
            }
            td:last-child {
                border-right:none;
            }
            tr:last-child td {
                border-bottom:none !important;
            }
            tr:hover td {
                background:rgba(201,162,75,0.03);
                color:#1c1b18;
            }
            .txt-gold {
                color:#8a6a1c;
                font-weight:700;
            }
            .status-badge-custom {
                display:inline-block;
                background:rgba(201,162,75,0.1);
                color:#8a6a1c !important;
                padding:5px 14px;
                border-radius:20px;
                font-size:12px;
                font-weight:700;
                border:1px solid rgba(201,162,75,0.4);
            }

            @media (max-width: 992px) {
                .content { margin-left:0; width:100%; padding:20px; }
                .sidebar { display:none; }
                .form-grid { grid-template-columns:1fr; }
            }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>
    <body>

        <div class="top-header">
            <div class="header-title">Quản Lý Doanh Thu</div>
            <div class="header-admin">
                <i class="fa-solid fa-crown"></i>
                <span>Xin chào, Admin</span>
            </div>
        </div>

        <div class="wrapper">
            <div class="sidebar">
                <div class="sidebar-logo">
                    <img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo">
                </div>
                <ul>                   
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/qtv"><i class="fa-solid fa-user-shield icon"></i><span>Quản trị viên</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyyeuthichtaikhoan"><i class="fa-solid fa-heart-circle-check icon"></i><span>Yêu thích theo tài khoản</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/nguoibanservlet"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlytopyeuthich"><i class="fa-solid fa-heart icon"></i><span>Top sản phẩm yêu thích</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhachhangservlet"><i class="fa-solid fa-users icon"></i><span>Quản lý đơn hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhohangservlet"><i class="fa-solid fa-warehouse icon"></i><span>Quản lý kho hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/phieugiamgiaservlet"><i class="fa-solid fa-ticket icon"></i><span>Quản lý mã giảm giá</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li class="active"><a href="${pageContext.request.contextPath}/doanhthuservlet"><i class="fa-solid fa-chart-line icon"></i><span>Quản lý doanh thu</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/capnhattrangthaiservlet"><i class="fa-solid fa-box-open icon"></i><span>Cập nhật trạng thái đơn hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlyhoadonservlet"><i class="fa-solid fa-file-invoice icon"></i><span>Quản lý hóa đơn</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyvanchuyenadmin"><i class="fa-solid fa-truck-fast icon"></i><span>Quản lý vận chuyển</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlynhanvienbanhang"><i class="fa-solid fa-user-tie icon"></i><span>Quản lý nhân viên bán hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/luongvanchuyenadmin"><i class="fa-solid fa-sack-dollar icon"></i><span>Quản lý nhân sự và lương</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlybanner"><i class="fa-solid fa-images icon"></i><span>Quản lý banner</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN' || sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlydanhgia"><i class="fa-solid fa-star icon"></i><span>Quản lý đánh giá</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                    <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
                </ul>
            </div>

            <div class="content">               
                <div class="main-container-box">                  
                    <h2>Doanh Thu Theo Ngày</h2>                  
                    <div class="title-line"></div>

                    <div class="card-form">
                        <form action="${pageContext.request.contextPath}/doanhthuservlet" method="GET">
                            <div class="form-grid">
                                <div class="form-group">
                                    <label>Từ ngày:</label>
                                    <input type="date" name="tuNgay" value="${param.tuNgay}">
                                </div>
                                <div class="form-group">
                                    <label>Đến ngày:</label>
                                    <input type="date" name="denNgay" value="${param.denNgay}">
                                </div>
                            </div>
                            <div class="btn-group">
                                <button type="submit" class="btn-gold"><i class="fa-solid fa-filter me-1"></i> Lọc dữ liệu</button>
                                <button type="reset" class="btn-outline"><i class="fa-solid fa-rotate-left me-1"></i> Làm mới</button>
                            </div>
                        </form>
                    </div>

                    <div class="chart-container">
                        <h3><i class="fa-solid fa-chart-line"></i>&nbsp; Biểu Đồ Tăng Trưởng Doanh Thu</h3>

                        <c:if test="${not empty listDoanhThu}">
                            <div class="chart-summary">
                                <div class="summary-item">
                                    <div class="label">Tổng Doanh Thu Khoảng Đã Chọn</div>
                                    <div class="value" id="sumTotal">0đ</div>
                                </div>
                                <div class="summary-item">
                                    <div class="label">Trung Bình / Ngày</div>
                                    <div class="value" id="sumAvg">0đ</div>
                                </div>
                                <div class="summary-item">
                                    <div class="label">Tăng Trưởng (Ngày Đầu → Ngày Cuối)</div>
                                    <div class="value" id="sumGrowth">0%</div>
                                </div>
                            </div>
                            <div class="chart-wrap">
                                <canvas id="revenueChart"></canvas>
                            </div>
                        </c:if>

                        <c:if test="${empty listDoanhThu}">
                            <div class="chart-empty">
                                <i class="fa-solid fa-chart-simple" style="font-size:24px; color:#8a6a1c; margin-bottom:10px; display:block;"></i>
                                Không có dữ liệu để hiển thị biểu đồ.
                            </div>
                        </c:if>
                    </div>

                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th style="width:25%;">Ngày Bán Hàng</th>
                                    <th style="width:25%;">Tổng Số Đơn Hàng Thành Công</th>
                                    <th style="width:25%;">Tổng Số Lượng Sản Phẩm</th>
                                    <th style="width:25%;">Tổng Doanh Thu Đạt Được</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${listDoanhThu}">
                                    <tr>
                                        <td>
                                            <strong style="color:#1c1b18;"><fmt:formatDate value="${item.ngayBan}" pattern="dd / MM / yyyy"/></strong>
                                        </td>
                                        <td>
                                            <span class="status-badge-custom">${item.tongSoDonHang}</span>
                                        </td>
                                        <td style="font-weight:600;">${item.tongSoLuongBan}</td>
                                        <td>
                                            <span class="txt-gold">
                                                <fmt:formatNumber value="${item.tongDoanhThuNgay}" type="number"/>đ
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listDoanhThu}">
                                    <tr>
                                        <td colspan="4" style="padding:50px; color:#7d7a70; font-style:italic; border-bottom:none;">
                                            <i class="fa-solid fa-folder-open" style="font-size:24px; color:#8a6a1c; margin-bottom:10px; display:block;"></i>
                                            Không tìm thấy dữ liệu doanh thu hợp lệ trong khoảng thời gian này.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div> 
            </div>
        </div>

        <c:if test="${not empty listDoanhThu}">
        <script>
            var chartLabels = [];
            var chartData = [];

            <c:forEach var="item" items="${listDoanhThu}">
            chartLabels.push("<fmt:formatDate value="${item.ngayBan}" pattern="dd/MM/yyyy"/>");
            chartData.push(${item.tongDoanhThuNgay});
            </c:forEach>

            chartLabels.reverse();
            chartData.reverse();

            var total = chartData.reduce(function (a, b) { return a + b; }, 0);
            var avg = total / chartData.length;
            var first = chartData[0];
            var last = chartData[chartData.length - 1];
            var growthPercent = first > 0 ? ((last - first) / first) * 100 : 0;

            function formatVND(num) {
                return Math.round(num).toLocaleString('vi-VN') + 'đ';
            }

            document.getElementById('sumTotal').textContent = formatVND(total);
            document.getElementById('sumAvg').textContent = formatVND(avg);

            var growthEl = document.getElementById('sumGrowth');
            var growthText = (growthPercent >= 0 ? '+' : '') + growthPercent.toFixed(1) + '%';
            growthEl.textContent = growthText;
            growthEl.classList.add(growthPercent >= 0 ? 'up' : 'down');

            function waitForChartJsAndDraw(retriesLeft) {
                if (typeof Chart !== 'undefined') {
                    drawRevenueChart();
                    return;
                }
                if (retriesLeft <= 0) {
                    console.error('Không thể tải thư viện Chart.js.');
                    return;
                }
                setTimeout(function () {
                    waitForChartJsAndDraw(retriesLeft - 1);
                }, 200);
            }

            function drawRevenueChart() {
                var ctx = document.getElementById('revenueChart').getContext('2d');
                var goldGradient = ctx.createLinearGradient(0, 0, 0, 320);
                goldGradient.addColorStop(0, 'rgba(201,162,75,0.35)');
                goldGradient.addColorStop(1, 'rgba(201,162,75,0)');

                new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: chartLabels,
                        datasets: [{
                            label: 'Doanh thu theo ngày (đ)',
                            data: chartData,
                            borderColor: '#c9a24b',
                            backgroundColor: goldGradient,
                            borderWidth: 2,
                            pointBackgroundColor: '#0c0c0c',
                            pointBorderColor: '#c9a24b',
                            pointBorderWidth: 2,
                            pointRadius: 4,
                            pointHoverRadius: 6,
                            tension: 0.3,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        interaction: { mode: 'index', intersect: false },
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                backgroundColor: '#121212',
                                titleColor: '#c9a24b',
                                bodyColor: '#ffffff',
                                borderColor: 'rgba(201,162,75,0.4)',
                                borderWidth: 1,
                                padding: 12,
                                callbacks: {
                                    label: function (context) {
                                        return formatVND(context.parsed.y);
                                    }
                                }
                            }
                        },
                        scales: {
                            x: {
                                ticks: { color: '#9d9d9d', font: { size: 11 } },
                                grid: { color: 'rgba(201,162,75,0.08)' }
                            },
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    color: '#9d9d9d',
                                    font: { size: 11 },
                                    callback: function (value) {
                                        return value.toLocaleString('vi-VN');
                                    }
                                },
                                grid: { color: 'rgba(201,162,75,0.08)' }
                            }
                        }
                    }
                });
            }

            waitForChartJsAndDraw(25);
        </script>
        </c:if>
    </body>
</html>