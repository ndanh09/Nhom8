<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>LODI - Cập Nhật Trạng Thái Đơn Hàng</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
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

            /* LAYOUT CHÍNH */
            .wrapper {
                display:flex;
                margin-top:70px;
                min-height:calc(100vh - 70px);
                position:relative;
                z-index:2;
            }

            /* SIDEBAR ĐỒNG BỘ */
            .sidebar {
                width:310px; /* Tăng từ 290px lên 310px hoặc 320px */
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
                gap:12px; /* Tăng khoảng cách icon với chữ một chút cho thoáng */
                padding:12px 14px;
                color:#a3a3a3;
                font-size:14.5px; /* Đã tăng kích thước chữ to rõ hơn */
                font-weight:500;
                text-decoration:none;
                transition:all 0.25s ease-in-out;
                border-radius:10px;
                white-space:nowrap; 
            }

            .sidebar ul li a span:last-child {
                white-space:nowrap; /* Đảm bảo không bị ghi đè xuống dòng */
                line-height:normal;
            }
            .sidebar ul li a .icon {
                font-size:16px; /* Tăng kích thước icon từ 15px lên 16px */
                width:22px;
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

            /* VÙNG NỘI DUNG CHÍNH (FULL WIDTH, KHÔNG BỊ HỞ LỀ) */
            .content {
                margin-left:310px; /* Phải khớp với chiều rộng của sidebar */
                width:calc(100% - 310px);
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

            /* THÔNG BÁO */
            .alert-box {
                margin-bottom:25px;
                padding:14px 18px;
                border-radius:8px;
                font-weight:600;
                font-size:13.5px;
                display:flex;
                align-items:center;
                gap:10px;
            }
            .alert-success { background:rgba(74, 222, 128, 0.1); border:1px solid #2f7d4a; color:#2f7d4a; }
            .alert-error { background:rgba(248, 113, 113, 0.1); border:1px solid #a12a2a; color:#a12a2a; }

            /* THANH CÔNG CỤ / TÌM KIẾM */
            .toolbar {
                display:flex;
                justify-content:flex-end;
                margin-bottom:25px;
            }
            .search-form {
                display:flex;
                align-items:center;
                gap:12px;
                flex-wrap:wrap;
            }
            .search-box {
                position:relative;
            }
            .search-box i {
                position:absolute;
                left:15px;
                top:50%;
                transform:translateY(-50%);
                color:#8a6a1c;
                font-size:14px;
                pointer-events:none;
            }
            .search-box input {
                width:320px;
                max-width:100%;
                padding:12px 15px 12px 42px;
                background:#ebe6d6;
                color:#1c1b18;
                border:1px solid rgba(201,162,75,0.4);
                border-radius:8px;
                font-size:13.5px;
                outline:none;
                transition:all 0.3s ease;
            }
            .search-box input::placeholder { color:#7d7a70; }
            .search-box input:focus {
                border-color:#c9a24b;
                box-shadow:0 0 0 3px rgba(201,162,75,0.15);
                background:#ebe6d6;
            }
            .btn-search {
                background:linear-gradient(135deg, #c9a24b, #8a6a1c);
                color:#000;
                border:none;
                padding:12px 22px;
                border-radius:8px;
                font-weight:700;
                font-size:13px;
                cursor:pointer;
                transition:all 0.3s ease;
                box-shadow:0 4px 15px rgba(201,162,75,0.3);
            }
            .btn-search:hover { opacity:0.9; transform:translateY(-2px); }
            .btn-clear {
                background:linear-gradient(135deg, #2a2a2a, #f2efe4);
                color:#1c1b18;
                border:1px solid rgba(201,162,75,0.35);
                border-radius:8px;
                padding:11px 18px;
                font-size:13px;
                font-weight:700;
                text-decoration:none;
                transition:all 0.3s ease;
            }
            .btn-clear:hover { border-color:#c9a24b; color:#8a6a1c; transform:translateY(-2px); }

            /* BẢNG DỮ LIỆU */
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
                min-width:950px;
            }
            th {
                background:#0d0d0e;
                color:#c9a24b;
                padding:16px 14px;
                font-size:12px;
                font-weight:600;
                text-transform:uppercase;
                letter-spacing:0.5px;
                text-align:center;
                border-bottom:2px solid #c9a24b;
                border-right:1px solid rgba(201,162,75,0.2);
            }
            th:last-child { border-right:none; }
            td {
                padding:15px 14px;
                color:#1c1b18;
                font-size:13.5px;
                text-align:center;
                border-bottom:1px solid rgba(201,162,75,0.2);
                border-right:1px solid rgba(201,162,75,0.15);
            }
            td:last-child { border-right:none; }
            tbody tr:last-child td { border-bottom:none !important; }
            tbody tr:hover td { background:rgba(201,162,75,0.03); color:#1c1b18; }
            .empty-row td { color:#7d7a70; padding:40px; font-style:italic; }

            .badge-size {
                padding:4px 10px;
                border-radius:8px;
                font-size:11px;
                background:rgba(201,162,75,0.1);
                color:#8a6a1c;
                border:1px solid rgba(201,162,75,0.3);
                font-weight:600;
            }
            .select-custom {
                padding:8px 12px;
                border-radius:8px;
                background-color:#ebe6d6;
                color:#1c1b18;
                font-weight:600;
                cursor:pointer;
                border:1px solid;
                font-size:13px;
                outline:none;
            }
            .color-status-0 { color:#8a6a1c; border-color:#c9a24b; }
            .color-status-1 { color:#1f5fa8; border-color:#1f5fa8; }
            .color-status-2 { color:#2f7d4a; border-color:#2f7d4a; }
            .color-status-3 { color:#a12a2a; border-color:#a12a2a; }
            .color-status-4 { color:#c084fc; border-color:#c084fc; }

            .btn-save {
                background:linear-gradient(135deg, #c9a24b, #8a6a1c);
                color:#000;
                border:none;
                padding:8px 16px;
                border-radius:8px;
                cursor:pointer;
                font-weight:700;
                font-size:12px;
                text-transform:uppercase;
                transition:all 0.2s ease;
                box-shadow:0 2px 8px rgba(201,162,75,0.2);
            }
            .btn-save:hover { opacity:0.9; transform:translateY(-1px); }
            
            .action-cell {
                display:flex;
                flex-direction:column;
                align-items:center;
                gap:8px;
            }
            .action-cell form {
                display:flex;
                align-items:center;
                gap:8px;
                justify-content:center;
                flex-wrap:wrap;
            }
            .txt-gold { color:#8a6a1c; font-weight:700; }
            .status-note { font-size:11.5px; font-weight:600; margin-top:2px; }

            @media (max-width: 992px) {
                .content { margin-left:0; width:100%; padding:20px; }
                .sidebar { display:none; }
                .toolbar { justify-content:stretch; }
                .search-form { width:100%; }
                .search-box { flex:1; }
                .search-box input { width:100%; }
            }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>
    <body>
        <div class="top-header">
            <div class="header-title">Cập Nhật Trạng Thái</div>
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
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/nguoibanservlet"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhachhangservlet"><i class="fa-solid fa-users icon"></i><span>Quản lý đơn hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhohangservlet"><i class="fa-solid fa-warehouse icon"></i><span>Quản lý kho hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/phieugiamgiaservlet"><i class="fa-solid fa-ticket icon"></i><span>Quản lý mã giảm giá</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/doanhthuservlet"><i class="fa-solid fa-chart-line icon"></i><span>Quản lý doanh thu</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li class="active"><a href="${pageContext.request.contextPath}/capnhattrangthaiservlet"><i class="fa-solid fa-box-open icon"></i><span>Cập nhật trạng thái đơn hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlyhoadonservlet"><i class="fa-solid fa-file-invoice icon"></i><span>Quản lý hóa đơn</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyvanchuyenadmin"><i class="fa-solid fa-truck-fast icon"></i><span>Quản lý vận chuyển</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/luongvanchuyenadmin"><i class="fa-solid fa-sack-dollar icon"></i><span>Quản lý nhân sự và lương</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlybanner"><i class="fa-solid fa-images icon"></i><span>Quản lý banner</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                    <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
                </ul>
            </div>

            <div class="content">
                <div class="main-container-box">
                    <h2>Cập Nhật Trạng Thái Đơn Hàng</h2>
                    <div class="title-line"></div>

                    <c:if test="${not empty message}">
                        <div id="toast-message" class="alert-box alert-success">
                            <i class="fa-solid fa-check-circle"></i> ${message}
                        </div>
                        <script>
                            setTimeout(function () { document.getElementById('toast-message').style.display = 'none'; }, 3000);
                        </script>
                        <% session.removeAttribute("message"); %>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div id="toast-error" class="alert-box alert-error">
                            <i class="fa-solid fa-circle-exclamation"></i> ${error}
                        </div>
                        <script>
                            setTimeout(function () { document.getElementById('toast-error').style.display = 'none'; }, 4000);
                        </script>
                        <% session.removeAttribute("error"); %>
                    </c:if>

                    <div class="toolbar">
                        <form action="${pageContext.request.contextPath}/capnhattrangthaiservlet" method="GET" class="search-form">
                            <div class="search-box">
                                <i class="fa-solid fa-magnifying-glass"></i>
                                <input type="text" name="keyword" placeholder="Tìm theo mã đơn, khách hàng, mã SP..." value="${keyword}">
                            </div>
                            <button type="submit" class="btn-search">Tìm kiếm</button>
                            <c:if test="${not empty keyword}">
                                <a href="${pageContext.request.contextPath}/capnhattrangthaiservlet" class="btn-clear">Xóa lọc</a>
                            </c:if>
                        </form>
                    </div>

                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Mã đơn hàng</th>
                                    <th>Khách hàng</th>
                                    <th>Mã sản phẩm</th>
                                    <th>Kích cỡ</th>
                                    <th>Số lượng</th>
                                    <th>Tổng tiền</th>
                                    <th>Ngày đặt mua</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty listOrder}">
                                    <tr class="empty-row"><td colspan="8"><i class="fa-solid fa-folder-open" style="font-size:24px; color:#8a6a1c; margin-bottom:10px; display:block;"></i>Không tìm thấy đơn hàng nào phù hợp.</td></tr>
                                </c:if>
                                <c:forEach var="order" items="${listOrder}">
                                    <tr>
                                        <td><strong class="txt-gold">#ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}</strong></td>
                                        <td>${order.makhachhang}</td>
                                        <td>${order.masanpham}</td>
                                        <td><span class="badge-size">${order.kichco}</span></td>
                                        <td style="font-weight:600;">${order.soluong}</td>
                                        <td><span class="txt-gold"><fmt:formatNumber value="${order.giaban * order.soluong}" type="number"/>đ</span></td>
                                        <td><fmt:formatDate value="${order.ngaymua}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td>
                                            <div class="action-cell">
                                                <c:choose>
                                                    <c:when test="${order.donhangKhoa}">
                                                        <span class="select-custom color-status-3" style="display:inline-block; padding:4px 10px;">
                                                            <i class="fa-solid fa-lock"></i> Đơn đang bị khóa
                                                        </span>
                                                        <div class="status-note">Mở khóa ở trang Quản lý đơn hàng để thao tác</div>
                                                    </c:when>
                                                    <c:when test="${order.tinhtrang == 0}">
                                                        <form action="${pageContext.request.contextPath}/capnhattrangthaiservlet" method="POST">
                                                            <input type="hidden" name="matrangthai" value="${order.matrangthai}" />
                                                            <select name="tinhtrangMoi" class="select-custom color-status-0" onchange="this.className = 'select-custom color-status-' + this.value">
                                                                <option value="1">Đang giao</option>
                                                                <option value="3">Hủy đơn</option>
                                                            </select>
                                                            <button type="submit" name="action" value="capnhatTrangthai" class="btn-save">Cập nhật</button>
                                                        </form>
                                                        <div class="status-note color-status-0">Hiện tại: Chờ xác nhận</div>
                                                    </c:when>
                                                    <c:when test="${order.tinhtrang == 1}">
                                                        <form action="${pageContext.request.contextPath}/capnhattrangthaiservlet" method="POST">
                                                            <input type="hidden" name="matrangthai" value="${order.matrangthai}" />
                                                            <input type="hidden" name="tinhtrangMoi" value="3" />
                                                            <button type="submit" name="action" value="capnhatTrangthai" class="btn-save" onclick="return confirm('Xác nhận hủy sản phẩm này trong đơn #ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}?')">Hủy đơn</button>
                                                        </form>
                                                        <div class="status-note color-status-1">Hiện tại: Đang giao (chờ vận chuyển)</div>
                                                    </c:when>
                                                    <c:when test="${order.tinhtrang == 4}">
                                                        <form action="${pageContext.request.contextPath}/capnhattrangthaiservlet" method="POST">
                                                            <input type="hidden" name="matrangthai" value="${order.matrangthai}" />
                                                            <input type="hidden" name="tinhtrangMoi" value="3" />
                                                            <button type="submit" name="action" value="capnhatTrangthai" class="btn-save" onclick="return confirm('Xác nhận hủy sản phẩm này trong đơn #ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}?')">Hủy đơn</button>
                                                        </form>
                                                        <div class="status-note color-status-4">Hiện tại: Đã giao (chờ khách xác nhận)</div>
                                                    </c:when>
                                                    <c:when test="${order.tinhtrang == 2}">
                                                        <form action="${pageContext.request.contextPath}/capnhattrangthaiservlet" method="POST">
                                                            <input type="hidden" name="matrangthai" value="${order.matrangthai}" />
                                                            <input type="hidden" name="tinhtrangMoi" value="3" />
                                                            <button type="submit" name="action" value="capnhatTrangthai" class="btn-save" onclick="return confirm('Đơn hàng này đã ở trạng thái Đã nhận. Xác nhận hủy/hoàn trả đơn này? Số lượng sẽ được cộng trả lại kho.')">Hủy đơn</button>
                                                        </form>
                                                        <div class="status-note color-status-2">Hiện tại: Đã nhận</div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="select-custom color-status-3" style="display:inline-block; padding:4px 10px;">Hủy đơn</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>