<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý đánh giá</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <style>
            * { margin:0; padding:0; box-sizing:border-box; font-family:'Inter', sans-serif; }
            body { background-color:#e7e2d2; color:#1c1b18; display:flex; flex-direction:column; min-height:100vh; position:relative; -webkit-font-smoothing:antialiased; }
            
            body::before { content:""; position:fixed; top:0; left:0; width:100%; height:100%; background-image:#ffffff; background-size:60px 60px; z-index:0; pointer-events:none; }

            .header { background:#0d0d0e; padding:0 35px; display:flex; justify-content:space-between; align-items:center; position:fixed; top:0; left:0; width:100%; height:70px; z-index:1000; border-bottom:2px solid #c9a24b; box-shadow:0 4px 25px rgba(0, 0, 0, 0.4); }
            .header .title-page { color:#ead594; font-family:'Playfair Display', serif; font-size:22px; font-weight:700; letter-spacing:2px; text-transform:uppercase; text-shadow:none; }
            .header .user { background:rgba(15, 15, 15, 0.85); padding:8px 18px; border-radius:20px; color:#c9a24b; font-size:13px; font-weight:600; text-transform:uppercase; display:flex; align-items:center; gap:8px; border:1px solid rgba(201,162,75,0.4); box-shadow:inset 0 1px 3px rgba(0,0,0,0.5); }

            /* Sidebar với viền ngăn cách màu vàng gold */
            .sidebar { width:275px; background-color:#0d0d0e; position:fixed; top:70px; left:0; bottom:0; z-index:999; border-right:2px solid #c9a24b; overflow-y:auto; display:flex; flex-direction:column; align-items:center; box-shadow:5px 0 25px rgba(0,0,0,0.5); }
            .sidebar::-webkit-scrollbar { width:5px; }
            .sidebar::-webkit-scrollbar-thumb { background:#0d0d0e; border-radius:10px; }
            .sidebar::-webkit-scrollbar-thumb:hover { background:#c9a24b; }

            .sidebar .sidebar-logo { width:100%; display:flex; justify-content:center; align-items:center; padding:22px 15px; border-bottom:2px solid #c9a24b; margin-bottom:15px; background:rgba(255,255,255,0.01); }
            .sidebar .sidebar-logo img { max-width:130px; filter:drop-shadow(0px 0px 10px rgba(201,162,75,0.4)); transition:transform 0.3s ease; }
            .sidebar .sidebar-logo img:hover { transform:scale(1.05); }

            .sidebar ul { list-style:none; width:100%; padding:0 12px; }
            .sidebar ul li { margin-bottom:4px; }
            .sidebar ul li a { display:flex; align-items:center; padding:12px 16px; text-decoration:none; font-size:13.5px; font-weight:500; color:#a3a3a3; white-space:nowrap; border-radius:10px; transition:all 0.25s ease-in-out; }
            .sidebar ul li a .icon { margin-right:14px; width:22px; text-align:center; font-size:15px; color:#c9a24b; transition:color 0.25s ease; }
            
            .sidebar ul li a:hover { background:rgba(201,162,75,0.08); color:#ead594; transform:translateX(4px); }
            .sidebar ul li a:hover .icon { color:#c9a24b; text-shadow:0 0 10px rgba(201,162,75,0.5); }
            
            .sidebar ul li.active a { background:linear-gradient(135deg, rgba(201,162,75,0.2), rgba(201,162,75,0.05)); color:#ffffff; font-weight:600; border:1px solid rgba(201,162,75,0.5); box-shadow:0 4px 15px rgba(0,0,0,0.3); }
            .sidebar ul li.active a .icon { color:#c9a24b; text-shadow:0 0 10px rgba(201,162,75,0.6); }

            .container { margin-top:70px; margin-left:275px; padding:40px 45px; width:calc(100% - 275px); position:relative; z-index:1; }
            .container h1 { font-family:'Playfair Display', serif; font-size:32px; font-weight:700; letter-spacing:1px; text-transform:uppercase; background:none; -webkit-background-clip:text; -webkit-text-fill-color:#0d0d0e; margin-bottom:8px; }
            .title-line { height:2px; background:linear-gradient(90deg, #c9a24b, rgba(201,162,75,0.1)); margin-bottom:30px; border-radius:2px; }

            form.admin-form { background-color:#f2efe4; padding:28px; border:2px solid #c9a24b; border-radius:12px; display:grid; grid-template-columns:1fr 1fr; gap:20px; margin-bottom:35px; box-shadow:0 8px 30px rgba(40,30,0,0.14); }
            .form-group { display:flex; flex-direction:column; gap:6px; }
            form.admin-form label { font-weight:600; color:#7d7a70; font-size:12px; text-transform:uppercase; letter-spacing:0.5px; }
            form.admin-form input, form.admin-form select { width:100%; padding:11px 15px; background-color:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; color:#1c1b18; outline:none; font-family:'Inter', sans-serif; font-size:13.5px; transition:all 0.3s ease; }
            form.admin-form input:focus { border-color:#c9a24b; box-shadow:0 0 0 3px rgba(201,162,75,0.15); background-color:#ebe6d6; }
            
            .btn-group { grid-column:span 2; display:flex; gap:15px; border-top:1px solid rgba(201,162,75,0.3); padding-top:20px; margin-top:5px; }
            .btn-action { padding:12px 24px; font-size:12.5px; font-weight:700; text-transform:uppercase; cursor:pointer; border-radius:8px; transition:all 0.3s ease; letter-spacing:0.5px; }
            .btn-them { background:linear-gradient(135deg, #c9a24b, #8a6a1c); color:#000000; border:none; box-shadow:0 4px 15px rgba(201,162,75,0.3); }
            .btn-them:hover { opacity:0.9; transform:translateY(-2px); box-shadow:0 6px 20px rgba(201,162,75,0.4); }
            .btn-sua { background:transparent; color:#8a6a1c; border:1px solid #c9a24b !important; }
            .btn-sua:hover { background:rgba(201,162,75,0.1); }

            .btn-update { background:transparent; color:#8a6a1c; border:1px solid rgba(201,162,75,0.6); font-size:11.5px; font-weight:700; cursor:pointer; border-radius:6px; transition:all 0.2s ease; }
            .btn-update:hover { background:#c9a24b; color:#000; box-shadow:0 0 10px rgba(201,162,75,0.5); }
            .btn-update, .btn-xoa, .btn-mokhoa { width:70px; height:28px; padding:0; display:flex; align-items:center; justify-content:center; }
            .btn-xoa { background:rgba(239, 68, 68, 0.1); color:#b3261e; border:1px solid rgba(239, 68, 68, 0.4); font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; transition:all 0.2s ease; }
            .btn-xoa:hover { background:#b3261e; color:#1c1b18; box-shadow:0 0 10px rgba(239, 68, 68, 0.4); }
            .btn-mokhoa { background:rgba(34, 197, 94, 0.1); color:#2f7d4a; border:1px solid rgba(34, 197, 94, 0.4); font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; transition:all 0.2s ease; }
            .btn-mokhoa:hover { background:#2f7d4a; color:#1c1b18; box-shadow:0 0 10px rgba(34, 197, 94, 0.4); }

            .badge-qtv { color:#8a6a1c; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(201,162,75,0.1); border:1px solid rgba(201,162,75,0.4); padding:4px 10px; border-radius:20px; }
            .badge-kh { color:#2f7d4a; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(34, 197, 94, 0.1); border:1px solid rgba(34, 197, 94, 0.4); padding:4px 10px; border-radius:20px; }
            .badge-hoatdong { color:#2f7d4a; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(34, 197, 94, 0.1); border:1px solid rgba(34, 197, 94, 0.4); padding:4px 10px; border-radius:20px; }
            .badge-khoa { color:#b3261e; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(239, 68, 68, 0.1); border:1px solid rgba(239, 68, 68, 0.4); padding:4px 10px; border-radius:20px; }

            .search-form { background-color:#f2efe4; padding:18px 22px; border:2px solid #c9a24b; border-radius:12px; display:flex; align-items:flex-end; gap:15px; margin-bottom:20px; }
            .search-form input { padding:11px 15px; background-color:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; color:#1c1b18; outline:none; font-family:'Inter', sans-serif; font-size:13.5px; width:280px; }

            /* Bảng dữ liệu có đầy đủ viền ngăn cách các hàng và các cột */
            .table-container { background-color:#f2efe4; border:2px solid #c9a24b; border-radius:12px; overflow:hidden; box-shadow:0 8px 30px rgba(40,30,0,0.14); }
            table { width:100%; border-collapse:collapse; }
            table th { background-color:#0d0d0e; color:#c9a24b; padding:15px 18px; text-transform:uppercase; font-size:11.5px; letter-spacing:0.5px; border-bottom:2px solid #c9a24b; border-right:1px solid rgba(201,162,75,0.2); }
            table th:last-child { border-right:none; }
            table td { padding:15px 18px; text-align:center; color:#1c1b18; font-size:13px; border-bottom:1px solid rgba(201,162,75,0.2); border-right:1px solid rgba(201,162,75,0.15); }
            table td:last-child { border-right:none; }
            table tr:last-child td { border-bottom:none; }
            table tr:hover { background-color:rgba(201,162,75,0.03); }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}

            /* ===== Yêu thích: thẻ thống kê, ảnh nhỏ, nhãn trạng thái ===== */
            .stat-row { display:grid; grid-template-columns:repeat(auto-fit,minmax(210px,1fr)); gap:16px; margin-bottom:26px; }
            .stat { background-color:#f2efe4; border:2px solid #c9a24b; border-radius:12px; padding:16px 20px; box-shadow:0 8px 30px rgba(40,30,0,0.10); }
            .stat .n { font-family:'Playfair Display', serif; font-size:30px; font-weight:700; color:#8a6a1c; }
            .stat .l { font-size:12px; text-transform:uppercase; letter-spacing:0.5px; color:#7d7a70; font-weight:600; }
            .thumb-sm { width:56px; height:56px; object-fit:cover; border-radius:8px; border:1px solid rgba(201,162,75,0.4); background:#ebe6d6; }
            .rank { font-family:'Playfair Display', serif; font-size:20px; font-weight:700; color:#8a6a1c; }
            .rank.r1 { color:#b3261e; }
            .sp-name { font-weight:700; color:#1c1b18; }
            .sp-code { font-size:11.5px; color:#7d7a70; }
            .love-n { font-weight:700; color:#b3261e; }
            .badge { display:inline-block; font-weight:700; font-size:11px; text-transform:uppercase; padding:4px 10px; border-radius:20px; border:1px solid; margin:2px 0; }
            .badge-top { color:#2f7d4a; background:rgba(34,197,94,0.1); border-color:rgba(34,197,94,0.4); }
            .badge-pin { color:#8a6a1c; background:rgba(201,162,75,0.12); border-color:rgba(201,162,75,0.5); }
            .badge-hide { color:#b3261e; background:rgba(239,68,68,0.1); border-color:rgba(239,68,68,0.4); }
            .badge-muted { color:#6b685f; background:#f1eee5; border-color:#d8d3c4; }
            .act { display:flex; flex-wrap:wrap; justify-content:center; align-items:center; gap:6px; }
            .act form { margin:0; display:flex; align-items:center; gap:6px; }
            .act input[type=number] { width:64px; padding:5px 8px; background-color:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:6px; font-size:12.5px; }
            .btn-wide { width:auto !important; padding:0 12px !important; white-space:nowrap; }
            .empty-row { padding:30px; color:#7d7a70; }
            .panel-title { font-family:'Playfair Display', serif; font-size:22px; font-weight:700; margin:34px 0 14px; color:#0d0d0e; }
            .row-selected { background-color:rgba(201,162,75,0.15) !important; }
            .check-inline { display:flex; align-items:center; gap:8px; font-size:13px; color:#1c1b18; padding-bottom:10px; }
            .check-inline input { width:auto !important; }

            /* ===== Quản lý đánh giá ===== */
            .stars { display:inline-block; position:relative; font-size:16px; line-height:1; letter-spacing:2px; color:#d8d3c4; white-space:nowrap; }
            .stars .fill { position:absolute; left:0; top:0; overflow:hidden; color:#c9a24b; white-space:nowrap; }
            .no-star { color:#7d7a70; font-style:italic; font-size:12.5px; }
            .stat-row.small { grid-template-columns:repeat(5,minmax(110px,1fr)); }
            .stat-row.small .stat { padding:12px 16px; }
            .stat-row.small .n { font-size:24px; }
            .filter-form { background-color:#f2efe4; padding:18px 22px; border:2px solid #c9a24b; border-radius:12px; display:grid; grid-template-columns:repeat(auto-fit,minmax(170px,1fr)); gap:14px 16px; align-items:end; margin-bottom:18px; }
            .filter-form .form-group label { font-weight:600; color:#7d7a70; font-size:12px; text-transform:uppercase; letter-spacing:0.5px; }
            .filter-form input, .filter-form select { width:100%; padding:10px 14px; background-color:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; color:#1c1b18; outline:none; font-size:13.5px; }
            .filter-form input:focus, .filter-form select:focus { border-color:#c9a24b; box-shadow:0 0 0 3px rgba(201,162,75,0.15); }
            .filter-actions { display:flex; gap:10px; align-items:center; }
            .link-reset { font-size:12.5px; font-weight:600; color:#8a6a1c; text-decoration:underline; white-space:nowrap; }
            .result-info { margin:0 0 12px; color:#7d7a70; font-size:13px; }
            .cmt { max-width:320px; margin:0 auto; text-align:left; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
            .btn-view { background:transparent; color:#1c1b18; border:1px solid rgba(201,162,75,0.6); font-size:11.5px; font-weight:700; border-radius:6px; text-decoration:none; padding:0 12px; height:28px; display:inline-flex; align-items:center; white-space:nowrap; }
            .btn-view:hover { background:#c9a24b; color:#000; }
            .btn-small { height:28px; padding:0 12px; font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; white-space:nowrap; }
            .btn-hide { background:rgba(239,68,68,0.1); color:#b3261e; border:1px solid rgba(239,68,68,0.4); }
            .btn-hide:hover { background:#b3261e; color:#fff; }
            .btn-show { background:rgba(34,197,94,0.1); color:#2f7d4a; border:1px solid rgba(34,197,94,0.4); }
            .btn-show:hover { background:#2f7d4a; color:#fff; }
            .pager { display:flex; flex-wrap:wrap; justify-content:center; align-items:center; gap:8px; margin:22px 0 6px; }
            .pager a, .pager span.cur, .pager span.dis { min-width:38px; height:36px; padding:0 14px; display:inline-flex; align-items:center; justify-content:center; border-radius:8px; border:1px solid rgba(201,162,75,0.5); font-size:13px; font-weight:600; text-decoration:none; color:#1c1b18; background:#f2efe4; }
            .pager a:hover { background:#c9a24b; color:#000; }
            .pager span.cur { background:#0d0d0e; color:#ead594; border-color:#0d0d0e; }
            .pager span.dis { opacity:0.4; }
            .pager-info { text-align:center; color:#7d7a70; font-size:13px; }
            .detail-card { background-color:#f2efe4; border:2px solid #c9a24b; border-radius:12px; padding:28px; box-shadow:0 8px 30px rgba(40,30,0,0.14); max-width:860px; }
            .detail-grid { display:grid; grid-template-columns:190px 1fr; gap:0; }
            .detail-grid > div { padding:13px 4px; border-bottom:1px solid rgba(201,162,75,0.25); font-size:14px; }
            .detail-grid > .k { color:#7d7a70; font-weight:600; font-size:12px; text-transform:uppercase; letter-spacing:0.5px; padding-top:16px; }
            .detail-grid > .v { color:#1c1b18; word-break:break-word; }
            .detail-grid > .v.text { white-space:pre-line; line-height:1.6; }
            .detail-prod { display:flex; align-items:center; gap:14px; }
            .note-box { margin-top:20px; padding:12px 16px; border:1px dashed rgba(201,162,75,0.7); border-radius:8px; color:#7d7a70; font-size:12.5px; }
            .btn-row { display:flex; flex-wrap:wrap; gap:12px; margin-top:22px; align-items:center; }
            .btn-row form { margin:0; }
            .btn-back { text-decoration:none; display:inline-flex; align-items:center; }
</style>
    </head>
    <body>
        <div class="header">
            <div class="title-page">Quản lý đánh giá</div>
            <div class="user">
                <i class="fa-solid fa-crown"></i> Xin chào, Admin
            </div>
        </div>

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
                <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/doanhthuservlet"><i class="fa-solid fa-chart-line icon"></i><span>Quản lý doanh thu</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/capnhattrangthaiservlet"><i class="fa-solid fa-box-open icon"></i><span>Cập nhật trạng thái đơn hàng</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlyhoadonservlet"><i class="fa-solid fa-file-invoice icon"></i><span>Quản lý hóa đơn</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyvanchuyenadmin"><i class="fa-solid fa-truck-fast icon"></i><span>Quản lý vận chuyển</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlynhanvienbanhang"><i class="fa-solid fa-user-tie icon"></i><span>Quản lý nhân viên bán hàng</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/luongvanchuyenadmin"><i class="fa-solid fa-sack-dollar icon"></i><span>Quản lý nhân sự và lương</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlybanner"><i class="fa-solid fa-images icon"></i><span>Quản lý banner</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN' || sessionScope.vaiTro == 'QUANLY'}"><li class="active"><a href="${pageContext.request.contextPath}/quanlydanhgia"><i class="fa-solid fa-star icon"></i><span>Quản lý đánh giá</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
            </ul>
        </div>


        <div class="container">
            <h1>Quản Lý Đánh Giá &amp; Nhận Xét Sản Phẩm</h1>
            <div class="title-line"></div>

            <c:if test="${not empty sessionScope.success}">
                <div style="color:#2f7d4a; background:rgba(46, 204, 113, 0.05); border:1px solid rgba(46, 204, 113, 0.4); padding:14px 20px; margin-bottom:24px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-check"></i> <span><c:out value="${sessionScope.success}"/></span>
                </div>
                <c:remove var="success" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div style="color:#b3261e; background:rgba(231, 76, 60, 0.05); border:1px solid rgba(231, 76, 60, 0.4); padding:14px 20px; margin-bottom:24px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-exclamation"></i> <span><c:out value="${sessionScope.error}"/></span>
                </div>
                <c:remove var="error" scope="session"/>
            </c:if>

            <%-- Thống kê: cùng nguyên tắc tính điểm với trang sản phẩm công khai (đánh giá đã ẩn không được tính) --%>
            <div class="stat-row">
                <div class="stat"><div class="n">${thongKe.tongLuotDanhGia}</div><div class="l">Tổng lượt đánh giá đang hiển thị</div></div>
                <div class="stat"><div class="n">
                        <c:choose>
                            <c:when test="${thongKe.tongLuotChamSao > 0}"><fmt:formatNumber value="${thongKe.diemTrungBinh}" minFractionDigits="1" maxFractionDigits="1"/> / 5</c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </div><div class="l">Điểm đánh giá trung bình<c:if test="${thongKe.tongLuotChamSao == 0}"> (chưa có đánh giá)</c:if></div></div>
                <div class="stat"><div class="n">${thongKe.tongLuotChamSao}</div><div class="l">Tổng lượt chấm sao đang hiển thị</div></div>
                <div class="stat"><div class="n" style="color:#b3261e;">${thongKe.soDaAn}</div><div class="l">Đánh giá đã bị ẩn</div></div>
            </div>
            <div class="stat-row small">
                <c:forEach var="i" begin="1" end="5">
                    <c:set var="s" value="${6 - i}"/>
                    <div class="stat"><div class="n">${thongKe.soLuongTheoSao[s]}</div><div class="l">${s} sao</div></div>
                </c:forEach>
            </div>

            <form action="${pageContext.request.contextPath}/quanlydanhgia" method="GET" class="filter-form">
                <div class="form-group">
                    <label>Khách hàng</label>
                    <input type="text" name="khach" value="<c:out value='${khach}'/>" placeholder="Tìm theo tên khách hàng...">
                </div>
                <div class="form-group">
                    <label>Sản phẩm</label>
                    <input type="text" name="sanpham" value="<c:out value='${sanpham}'/>" placeholder="Tìm theo tên sản phẩm...">
                </div>
                <div class="form-group">
                    <label>Số sao</label>
                    <select name="sao">
                        <option value="">Tất cả</option>
                        <c:forEach var="s" begin="1" end="5">
                            <option value="${s}" ${sao == s ? 'selected' : ''}>${s} sao</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Trạng thái</label>
                    <select name="trangthai">
                        <option value="" ${empty trangthai ? 'selected' : ''}>Tất cả</option>
                        <option value="hien" ${trangthai == 'hien' ? 'selected' : ''}>Đang hiển thị</option>
                        <option value="an" ${trangthai == 'an' ? 'selected' : ''}>Đã ẩn</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Sắp xếp</label>
                    <select name="sapxep">
                        <option value="moi" ${sapxep == 'moi' ? 'selected' : ''}>Mới nhất đến cũ nhất</option>
                        <option value="cu" ${sapxep == 'cu' ? 'selected' : ''}>Cũ nhất đến mới nhất</option>
                    </select>
                </div>
                <div class="filter-actions">
                    <button type="submit" class="btn-action btn-them"><i class="fa-solid fa-magnifying-glass"></i> Tìm</button>
                    <a class="link-reset" href="${pageContext.request.contextPath}/quanlydanhgia">Xóa lọc</a>
                </div>
            </form>

            <p class="result-info">Tìm thấy <b>${tongKetQua}</b> đánh giá &middot; Trang <b>${trang}</b> / <b>${tongTrang}</b></p>

            <div class="table-container">
                <table>
                    <thead>
                        <tr><th>Mã</th><th>Khách hàng</th><th>Sản phẩm</th><th>Số sao</th><th>Nhận xét</th><th>Ngày đánh giá</th><th>Trạng thái</th><th>Thao tác</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="dg" items="${dsDanhGia}">
                            <tr>
                                <td><strong style="color:#8a6a1c;">#${dg.maDanhGia}</strong></td>
                                <td>
                                    <div class="sp-name"><c:out value="${empty dg.tenKhachHang ? '(không rõ)' : dg.tenKhachHang}"/></div>
                                    <div class="sp-code"><c:out value="${dg.maKhachHang}"/></div>
                                </td>
                                <td>
                                    <div class="sp-name"><c:out value="${empty dg.tenSanPham ? '(Sản phẩm đã bị xóa)' : dg.tenSanPham}"/></div>
                                    <div class="sp-code"><c:out value="${dg.maGoc}"/></div>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${dg.coSao}">
                                            <span class="stars" title="${dg.soSao} sao"><span class="fill" style="width:${dg.soSao * 20}%">&#9733;&#9733;&#9733;&#9733;&#9733;</span>&#9733;&#9733;&#9733;&#9733;&#9733;</span>
                                        </c:when>
                                        <c:otherwise><span class="no-star">Không có</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${dg.coNoiDung}"><div class="cmt" title="<c:out value='${dg.noiDung}'/>"><c:out value="${dg.noiDung}"/></div></c:when>
                                        <c:otherwise><span class="no-star">Không có</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td><fmt:formatDate value="${dg.ngayDanhGia}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${dg.hienThi}"><span class="badge badge-top">Đang hiển thị</span></c:when>
                                        <c:otherwise><span class="badge badge-hide">Đã ẩn</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="act">
                                        <a class="btn-view" href="${pageContext.request.contextPath}/quanlydanhgia?action=chitiet&amp;id=${dg.maDanhGia}&amp;quaylai=${queryTrangEnc}">Xem chi tiết</a>
                                        <form action="${pageContext.request.contextPath}/quanlydanhgia" method="POST">
                                            <input type="hidden" name="id" value="${dg.maDanhGia}">
                                            <input type="hidden" name="quaylai" value="<c:out value='${queryTrang}'/>">
                                            <c:choose>
                                                <c:when test="${dg.hienThi}">
                                                    <input type="hidden" name="action" value="an">
                                                    <button type="submit" class="btn-small btn-hide" onclick="return confirm('Ẩn đánh giá #${dg.maDanhGia}? Đánh giá sẽ không còn hiển thị công khai và không được tính vào điểm trung bình.');">Ẩn</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="action" value="hien">
                                                    <button type="submit" class="btn-small btn-show">Hiện</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty dsDanhGia}">
                            <tr><td colspan="8" class="empty-row">Không có đánh giá nào phù hợp với điều kiện tìm kiếm.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- Phân trang: các link luôn mang theo toàn bộ điều kiện tìm kiếm / lọc / sắp xếp hiện tại --%>
            <c:if test="${tongTrang > 1}">
                <div class="pager">
                    <c:choose>
                        <c:when test="${trang > 1}">
                            <c:url var="uTruoc" value="/quanlydanhgia"><c:param name="khach" value="${khach}"/><c:param name="sanpham" value="${sanpham}"/><c:param name="sao" value="${sao}"/><c:param name="trangthai" value="${trangthai}"/><c:param name="sapxep" value="${sapxep}"/><c:param name="trang" value="${trang - 1}"/></c:url>
                            <a href="${uTruoc}">&laquo; Trang trước</a>
                        </c:when>
                        <c:otherwise><span class="dis">&laquo; Trang trước</span></c:otherwise>
                    </c:choose>

                    <c:forEach var="p" begin="${trang - 2 < 1 ? 1 : trang - 2}" end="${trang + 2 > tongTrang ? tongTrang : trang + 2}">
                        <c:choose>
                            <c:when test="${p == trang}"><span class="cur">${p}</span></c:when>
                            <c:otherwise>
                                <c:url var="uTrang" value="/quanlydanhgia"><c:param name="khach" value="${khach}"/><c:param name="sanpham" value="${sanpham}"/><c:param name="sao" value="${sao}"/><c:param name="trangthai" value="${trangthai}"/><c:param name="sapxep" value="${sapxep}"/><c:param name="trang" value="${p}"/></c:url>
                                <a href="${uTrang}">${p}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${trang < tongTrang}">
                            <c:url var="uSau" value="/quanlydanhgia"><c:param name="khach" value="${khach}"/><c:param name="sanpham" value="${sanpham}"/><c:param name="sao" value="${sao}"/><c:param name="trangthai" value="${trangthai}"/><c:param name="sapxep" value="${sapxep}"/><c:param name="trang" value="${trang + 1}"/></c:url>
                            <a href="${uSau}">Trang sau &raquo;</a>
                        </c:when>
                        <c:otherwise><span class="dis">Trang sau &raquo;</span></c:otherwise>
                    </c:choose>
                </div>
            </c:if>
            <div class="pager-info">Trang ${trang} / ${tongTrang}</div>
        </div>
    </body>
</html>
