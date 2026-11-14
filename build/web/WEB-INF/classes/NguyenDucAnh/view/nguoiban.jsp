<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý sản phẩm</title>

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

            html {
                scroll-behavior:smooth;
            }

            body {
                background:#e7e2d2;
                color:#1c1b18;
                min-height:100vh;
                display:flex;
                flex-direction:column;
                position:relative;
                -webkit-font-smoothing:antialiased;
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

            .header{
                height:70px;
                display:flex;
                justify-content:space-between;
                align-items:center;
                padding:0 35px;
                background:#0d0d0e;
                border-bottom:2px solid #c9a24b;
                box-shadow:0 4px 25px rgba(0, 0, 0, 0.4);
                position:fixed;
                top:0;
                left:0;
                width:100%;
                z-index:1000;
            }

            .header-title{
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

            .wrapper{
                display:flex;
                margin-top:70px;
                min-height:calc(100vh - 70px);
                position:relative;
                z-index:1;
            }

            .sidebar{
                width:275px;
                flex-shrink:0;
                background-color:#0d0d0e;
                position:fixed;
                top:70px;
                left:0;
                bottom:0;
                z-index:999;
                border-right:2px solid #c9a24b;
                display:flex;
                flex-direction:column;
                align-items:center;
                box-shadow:5px 0 25px rgba(0,0,0,0.5);
                overflow-y:auto;
            }

            .sidebar::-webkit-scrollbar { width:5px; }
            .sidebar::-webkit-scrollbar-thumb { background:#0d0d0e; border-radius:10px; }
            .sidebar::-webkit-scrollbar-thumb:hover { background:#c9a24b; }

            .sidebar-logo{
                width:100%;
                display:flex;
                justify-content:center;
                align-items:center;
                padding:22px 15px;
                border-bottom:2px solid #c9a24b;
                margin-bottom:15px;
                background:rgba(255,255,255,0.01);
            }

            .sidebar-logo img{
                max-width:130px;
                transition:transform 0.3s ease;
                filter:drop-shadow(0px 0px 10px rgba(201,162,75,0.4));
            }

            .sidebar-logo img:hover{
                transform:scale(1.05);
            }

            .sidebar ul{
                list-style:none;
                width:100%;
                padding:0 12px;
            }

            .sidebar ul li{
                margin-bottom:4px;
            }

            .sidebar ul li a{
                display:flex;
                align-items:center;
                gap:14px;
                padding:12px 16px;
                border-radius:10px;
                color:#a3a3a3;
                text-decoration:none;
                transition:all 0.25s ease-in-out;
                font-size:13.5px;
                font-weight:500;
                white-space:nowrap;
            }

            .sidebar ul li a:hover{
                background:rgba(201,162,75,0.08);
                color:#ead594;
                transform:translateX(4px);
            }

            .sidebar ul li a:hover .icon {
                color:#c9a24b;
                text-shadow:0 0 10px rgba(201,162,75,0.5);
            }

            .sidebar ul li.active a{
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

            .icon{
                width:20px;
                text-align:center;
                font-size:15px;
                color:#8a6a1c;
                transition:color 0.25s ease;
                flex-shrink:0;
            }

            .main-content{
                flex:1;
                margin-left:275px;
                padding:40px 45px;
                width:calc(100% - 275px);
            }

            .container{
                display:flex;
                flex-direction:column;
                gap:35px;
            }

            .form-section,
            .list-section{
                background-color:#f2efe4;
                border-radius:12px;
                padding:28px;
                border:2px solid #c9a24b;
                box-shadow:0 8px 30px rgba(40,30,0,0.14);
            }

            h2{
                font-family:'Playfair Display', serif;
                font-size:32px;
                color:#1c1b18;
                letter-spacing:1px;
                text-transform:uppercase;
                margin-bottom:25px;
                background:none;
                -webkit-background-clip:text;
                -webkit-text-fill-color:#0d0d0e;
            }

            form{
                display:grid;
                grid-template-columns:repeat(2, minmax(280px, 1fr));
                gap:20px;
            }

            .form-group{
                display:flex;
                flex-direction:column;
                gap:6px;
            }

            .form-group label{
                color:#7d7a70;
                font-weight:600;
                font-size:12px;
                letter-spacing:0.5px;
                text-transform:uppercase;
            }

            .form-group input,
            .form-group select{
                height:44px;
                padding:0 15px;
                border-radius:8px;
                border:1px solid rgba(201,162,75,0.4);
                background-color:#ebe6d6;
                color:#1c1b18;
                transition:all 0.3s ease;
                font-size:13.5px;
                font-family:'Inter', sans-serif;
            }

            .form-group input:focus,
            .form-group select:focus{
                outline:none;
                border-color:#c9a24b;
                box-shadow:0 0 0 3px rgba(201,162,75,0.15);
                background-color:#ebe6d6;
            }

            input[type=file]{
                padding:8px;
                height:auto;
                cursor:pointer;
            }

            #formImgPreview{
                width:90px;
                height:90px;
                margin-top:10px;
                object-fit:cover;
                border-radius:8px;
                border:2px solid rgba(201,162,75,0.4);
            }

            .price-input{
                position:relative;
            }

            .price-input input{
                width:100%;
                padding-right:75px;
            }

            .price-input span{
                position:absolute;
                top:50%;
                right:18px;
                transform:translateY(-50%);
                color:#8a6a1c;
                font-weight:700;
            }

            .btn-group,
            #sizeQtyContainer,
            .form-group.full-width{
                grid-column:1 / span 2;
            }

            .size-row-group{
                display:grid;
                grid-template-columns:repeat(2, 1fr);
                gap:16px;
                margin-top:10px;
            }

            .size-row{
                display:flex;
                align-items:center;
                gap:14px;
                padding:12px 16px;
                border-radius:8px;
                background:#f2efe4;
                border:1px solid rgba(201,162,75,0.3);
                transition:all 0.3s ease;
            }

            .size-row:hover{
                border-color:#c9a24b;
                transform:translateY(-2px);
                box-shadow:0 8px 16px rgba(40,30,0,0.14);
            }

            .size-check-item{
                display:flex !important;
                align-items:center;
                gap:8px;
                min-width:95px;
                color:#ececec;
                cursor:pointer;
                user-select:none;
                font-size:13px;
                font-weight:600;
            }

            .size-check-item input{
                width:18px;
                height:18px;
                accent-color:#c9a24b;
                cursor:pointer;
            }

            .size-qty-input{
                flex:1;
                height:38px;
                border-radius:6px;
                border:1px solid rgba(201,162,75,0.3);
                background:#ebe6d6;
                color:#1c1b18;
                padding:0 12px;
                font-size:13px;
            }

            .size-qty-input:focus{
                outline:none;
                border-color:#c9a24b;
            }

            .btn-group{
                display:flex;
                gap:15px;
                border-top:1px solid rgba(201,162,75,0.3);
                padding-top:20px;
                margin-top:10px;
            }

            .btn-action{
                flex:1;
                border:none;
                border-radius:8px;
                padding:12px 24px;
                font-size:12.5px;
                font-weight:700;
                cursor:pointer;
                transition:all 0.3s ease;
                text-transform:uppercase;
                letter-spacing:0.5px;
            }

            .btn-action:hover{
                transform:translateY(-2px);
                box-shadow:0 6px 20px rgba(201,162,75,0.3);
            }

            .btn-them{
                background:linear-gradient(135deg, #c9a24b, #8a6a1c);
                color:#000000;
            }

            .btn-them:hover{
                opacity:0.9;
            }

            .btn-sua{
                background:transparent;
                color:#8a6a1c;
                border:1px solid #c9a24b !important;
            }

            .btn-sua:hover{
                background:rgba(201,162,75,0.1);
            }

            .btn-clear{
                background:#e4dfd0;
                color:#1c1b18;
                border:1px solid rgba(0,0,0,.10);
            }

            .btn-clear:hover{
                background:#282e3a;
            }

            .list-section{
                overflow:auto;
            }

            table{
                width:100%;
                border-collapse:collapse;
            }

            table th{
                position:sticky;
                top:0;
                z-index:5;
                background-color:#0d0d0e;
                color:#c9a24b;
                padding:15px 18px;
                border-bottom:2px solid #c9a24b;
                border-right:1px solid rgba(201,162,75,0.2);
                font-size:11.5px;
                letter-spacing:0.5px;
                text-transform:uppercase;
                white-space:nowrap;
            }

            table th:last-child { 
                border-right:none; 
            }

            table tr{
                transition:background-color 0.2s ease;
            }

            table tr:hover{
                background-color:rgba(201,162,75,0.03);
            }

            td{
                padding:15px 18px;
                border-bottom:1px solid rgba(201,162,75,0.2);
                border-right:1px solid rgba(201,162,75,0.15);
                text-align:center;
                vertical-align:middle;
                color:#1c1b18;
                font-size:13px;
                white-space:nowrap;
            }

            table td:last-child { 
                border-right:none; 
            }

            table tr:last-child td { 
                border-bottom:none; 
            }

            .product-img{
                width:58px;
                height:58px;
                object-fit:cover;
                border-radius:8px;
                border:1px solid rgba(201,162,75,0.4);
                transition:transform 0.3s ease;
            }

            .product-img:hover{
                transform:scale(1.08);
            }

            .btn-table{
                border:none;
                border-radius:6px;
                padding:7px 14px;
                font-size:11.5px;
                font-weight:700;
                cursor:pointer;
                transition:all 0.2s ease;
            }

            .btn-table:hover{
                transform:translateY(-2px);
            }

            .btn-table-edit{
                background:rgba(47, 141, 228, 0.15);
                color:#1f5fa8;
                border:1px solid rgba(47, 141, 228, 0.4);
            }

            .btn-table-edit:hover{
                background:#2f8de4;
                color:#1c1b18;
                box-shadow:0 0 10px rgba(47, 141, 228, 0.4);
            }

            .btn-table-del{
                background:rgba(239, 68, 68, 0.1);
                color:#b3261e;
                border:1px solid rgba(239, 68, 68, 0.4);
            }

            .btn-table-del:hover{
                background:#b3261e;
                color:#1c1b18;
                box-shadow:0 0 10px rgba(239, 68, 68, 0.4);
            }

            .btn-table-size {
                background:rgba(201,162,75,0.1);
                color:#8a6a1c;
                border:1px solid rgba(201,162,75,0.4);
            }

            .btn-table-size:hover {
                background:#c9a24b;
                color:#000;
                box-shadow:0 0 10px rgba(201,162,75,0.5);
            }

            .badge{
                display:inline-flex;
                align-items:center;
                justify-content:center;
                min-width:90px;
                padding:4px 10px;
                border-radius:20px;
                font-size:11px;
                font-weight:700;
                text-transform:uppercase;
            }

            .badge-dangban{
                background:rgba(34, 197, 94, 0.1);
                color:#2f7d4a;
                border:1px solid rgba(34, 197, 94, 0.4);
            }

            .badge-hethang{
                background:rgba(243, 156, 18, 0.1);
                color:#f1b53c;
                border:1px solid rgba(243, 156, 18, 0.4);
            }

            .badge-ngungban{
                background:rgba(239, 68, 68, 0.1);
                color:#b3261e;
                border:1px solid rgba(239, 68, 68, 0.4);
            }

            ::-webkit-scrollbar{
                width:5px;
                height:5px;
            }

            ::-webkit-scrollbar-track{
                background:#0d0d0e;
            }

            ::-webkit-scrollbar-thumb{
                background:#0d0d0e;
                border-radius:10px;
            }

            ::-webkit-scrollbar-thumb:hover{
                background:#c9a24b;
            }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>
    <body>
        <div class="header">
            <div class="header-title">Quản lý sản phẩm</div>
            <div class="header-admin">
                <i class="fa-solid fa-crown"></i>
                <span>Xin chào, Admin</span>
            </div>
        </div>
        
        <div class="wrapper">
            <div class="sidebar">
                <div class="sidebar-logo"><img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo"></div>
                <ul>             
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/qtv"><i class="fa-solid fa-user-shield icon"></i><span>Quản trị viên</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyyeuthichtaikhoan"><i class="fa-solid fa-heart-circle-check icon"></i><span>Yêu thích theo tài khoản</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li class="active"><a href="#"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlytopyeuthich"><i class="fa-solid fa-heart icon"></i><span>Top sản phẩm yêu thích</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlysizeservlet"><i class="fa-solid fa-ruler icon"></i><span>Quản lý size</span></a></li></c:if>
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
                    <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                    <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
                </ul>
            </div>

            <div class="main-content">
                <div class="container">
                    <div class="form-section">
                        <h2>Thông tin sản phẩm</h2>
                        <c:if test="${not empty success || not empty error}">
                            <div style="padding:14px 20px; margin-bottom:25px; border-radius:8px; font-size:14px; display:flex; align-items:center; gap:10px; ${not empty success ? 'background:rgba(46, 204, 113, 0.05); color:#2f7d4a; border:1px solid rgba(46, 204, 113, 0.4);' : 'background:rgba(231, 76, 60, 0.05); color:#b3261e; border:1px solid rgba(231, 76, 60, 0.4);'}">
                                <i class="${not empty success ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-exclamation'}"></i>
                                <span>${not empty success ? success : error}</span>
                            </div>
                            <c:remove var="success" scope="session"/>
                            <c:remove var="error" scope="session"/>
                        </c:if>

                        <form id="productForm" action="nguoibanservlet" method="POST" enctype="multipart/form-data">
                            <input type="hidden" id="formAction" name="action" value="add">
                            <input type="hidden" id="magoc" name="magoc" value="">
                            <div class="form-group"><label>Mã sản phẩm:</label><input type="text" id="masanpham" name="masanpham" required></div>
                            <div class="form-group"><label>Tên sản phẩm:</label><input type="text" id="tensanpham" name="tensanpham" required></div>
                            <div class="form-group">
                                <label>Danh mục:</label>
                                <select id="danhmuc" name="danhmuc" required>
                                    <option value="">-- Chọn danh mục --</option>
                                    <option value="Áo ĐTQG">Áo ĐTQG</option>
                                    <option value="Áo CLB">Áo CLB</option>
                                    <option value="Áo Retro ĐTQG & CLB">Áo Retro ĐTQG & CLB</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Hình ảnh:</label>
                                <input type="hidden" id="hinhanh_cu" name="hinhanh_cu">
                                <input type="file" id="hinhanh" name="hinhanh" accept="image/*" onchange="previewImage(this)">
                                <img id="formImgPreview" style="display:none;">
                            </div>
                            <div id="sizeSectionWrap" style="grid-column:1 / span 2; display:flex; flex-direction:column; gap:20px;">
                                <div class="form-group" style="grid-column:auto;">
                                    <label id="kichcoLabel">Kích cỡ &amp; số lượng theo từng size:</label>
                                    <div class="size-row-group" id="sizeCheckboxGroup">
                                        <div class="size-row" data-size-row="S">
                                            <label class="size-check-item"><input type="checkbox" class="size-cb" name="kichco" value="S"> Size S</label>
                                            <input type="number" min="0" class="size-qty-input" name="soluong_S" data-size="S" placeholder="Số lượng S">
                                        </div>
                                        <div class="size-row" data-size-row="M">
                                            <label class="size-check-item"><input type="checkbox" class="size-cb" name="kichco" value="M"> Size M</label>
                                            <input type="number" min="0" class="size-qty-input" name="soluong_M" data-size="M" placeholder="Số lượng M">
                                        </div>
                                        <div class="size-row" data-size-row="L">
                                            <label class="size-check-item"><input type="checkbox" class="size-cb" name="kichco" value="L"> Size L</label>
                                            <input type="number" min="0" class="size-qty-input" name="soluong_L" data-size="L" placeholder="Số lượng L">
                                        </div>
                                        <div class="size-row" data-size-row="XL">
                                            <label class="size-check-item"><input type="checkbox" class="size-cb" name="kichco" value="XL"> Size XL</label>
                                            <input type="number" min="0" class="size-qty-input" name="soluong_XL" data-size="XL" placeholder="Số lượng XL">
                                        </div>
                                        <div class="size-row" data-size-row="XXL">
                                            <label class="size-check-item"><input type="checkbox" class="size-cb" name="kichco" value="XXL"> Size XXL</label>
                                            <input type="number" min="0" class="size-qty-input" name="soluong_XXL" data-size="XXL" placeholder="Số lượng XXL">
                                        </div>
                                        <div class="size-row" data-size-row="XXXL">
                                            <label class="size-check-item"><input type="checkbox" class="size-cb" name="kichco" value="XXXL"> Size XXXL</label>
                                            <input type="number" min="0" class="size-qty-input" name="soluong_XXXL" data-size="XXXL" placeholder="Số lượng XXXL">
                                        </div>
                                    </div>
                                    <div id="sizeHint" style="color:#7d7a70; font-size:12px; margin-top:6px;">Chọn size cho sản phẩm mới. Muốn thêm/sửa/xoá size cho sản phẩm đã có sẵn, dùng nút "Xem size" ở bảng bên phải.</div>
                                    <div id="sizeError" style="color:#b3261e; font-size:13px; margin-top:6px; display:none;">Vui lòng chọn ít nhất 1 kích cỡ.</div>
                                </div>
                                <div class="form-group" id="soluongMacDinhGroup" style="grid-column:auto;">
                                    <label>Số lượng mặc định (tuỳ chọn):</label>
                                    <input type="number" id="soluongsanpham" name="soluongsanpham" min="0">
                                </div>
                            </div>
                            <div id="sizeEditNote" style="display:none; color:#7d7a70; font-size:13px; grid-column:1 / span 2; margin-top:-10px;">
                                Đang sửa thông tin CHUNG của sản phẩm. Muốn sửa số lượng/thêm/xoá từng size, vào
                                <a href="#" id="linkQuanLySize" style="color:#8a6a1c;">trang Quản lý size</a>.
                            </div>
                            <div class="form-group">
                                <label>Giá bán:</label>
                                <div class="price-input">
                                    <input type="text" id="giasanpham" name="giasanpham" required>
                                    <span>đ</span>
                                </div>
                            </div>
                            <div class="form-group"><label>Ngày mở bán:</label><input type="date" id="ngayban" name="ngayban" required></div>
                            <div class="form-group" style="grid-column:1 / span 2;">
                                <label>Trạng thái:</label>
                                <select id="trangthai" name="trangthai">
                                    <option value="1">Còn hàng</option>
                                    <option value="2">Hết hàng</option>
                                    <option value="0">Ngừng bán</option>
                                </select>
                            </div>
                            <div class="btn-group">
                                <button type="submit" id="btnSubmit" class="btn-action btn-them">Thêm sản phẩm mới</button>
                                <button type="button" class="btn-action btn-clear" onclick="resetForm()">Làm mới</button>
                            </div>
                        </form>
                    </div>

                    <div class="list-section">
                        <h2>Danh sách sản phẩm</h2>

                        <div style="margin-bottom:20px;">
                            <input type="text" id="searchSanPham" placeholder="Tìm theo mã hoặc tên sản phẩm..."
                                   oninput="locSanPham()"
                                   style="width:100%; max-width:400px; height:44px; padding:0 15px; border-radius:8px;
                                          border:1px solid rgba(201,162,75,0.4); background:#ffffff; color:#1c1b18; font-size:13.5px; outline:none;">
                        </div>

                        <table>
                            <thead>
                                <tr><th>Mã</th><th>Ảnh</th><th>Tên</th><th>Danh mục</th><th>Size hiện có</th><th>Giá</th><th>Tổng số lượng</th><th>Ngày bán</th><th>Trạng thái</th><th>Hành động</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${dsnhom}">
                                    <tr>
                                        <td>${p.magoc}</td>
                                        <td><img src="${pageContext.request.contextPath}/image/${p.hinhanh}" class="product-img"></td>
                                        <td>${p.tensanpham}</td>
                                        <td>${p.danhmuc}</td>
                                        <td>${p.dssize} <span style="color:#7d7a70;">(${p.sosize} size)</span></td>
                                        <td><span class="format-price">${p.giasanpham}</span>đ</td>
                                        <td>${p.tongsoluong}</td>
                                        <td class="format-date" data-date="${p.ngayban}"></td>
                                        <td>                                      
                                            <c:choose>
                                                <c:when test="${p.trangthai == '1'}">
                                                    <span class="badge badge-dangban">Còn hàng</span>
                                                </c:when>
                                                <c:when test="${p.trangthai == '2'}">
                                                    <span class="badge badge-hethang">Hết hàng</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-ngungban">Ngừng bán</span>
                                                </c:otherwise>
                                            </c:choose>                                     
                                        </td>
                                        <td style="display:flex; justify-content:center; align-items:center; gap:6px; border:none; padding-top:22px;">
                                            <a class="btn-table btn-table-size" style="display:inline-block; text-decoration:none; text-align:center;"
                                               href="${pageContext.request.contextPath}/quanlysizeservlet?magoc=${p.magoc}">
                                                <i class="fa-solid fa-shirt"></i> Xem size
                                            </a>
                                            <button class="btn-table btn-table-edit" onclick="fillToForm(this)" 
                                                    data-magoc="${p.magoc}" data-ten="${p.tensanpham}" data-anh="${p.hinhanh}" 
                                                    data-gia="${p.giasanpham}" data-ngay="${p.ngayban}" data-trangthai="${p.trangthai}"
                                                    data-danhmuc="${p.danhmuc}">Sửa</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <form id="deleteForm" action="nguoibanservlet" method="POST" style="display:none;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="magoc" id="deleteMagoc">
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script>
            const sizeCheckboxes = document.querySelectorAll('.size-cb');
            const soluongMacDinh = document.getElementById('soluongsanpham');

            function getQtyInput(size) {
                return document.querySelector(`.size-qty-input[data-size="${size}"]`);
            }

            function syncQtyInputState(cb) {
                const qtyInput = getQtyInput(cb.value);
                if (!qtyInput) return;
                if (!cb.checked) {
                    qtyInput.value = "";
                }
            }

            sizeCheckboxes.forEach(cb => {
                cb.addEventListener('change', function () {
                    syncQtyInputState(cb);
                    document.getElementById('sizeError').style.display = "none";
                });
            });

            document.querySelectorAll('.size-qty-input').forEach(input => {
                input.addEventListener('input', function () {
                    document.getElementById('sizeError').style.display = "none";
                });
            });
            soluongMacDinh.addEventListener('input', function () {
                document.getElementById('sizeError').style.display = "none";
            });

            function fillToForm(btn) {
                const magoc = btn.getAttribute('data-magoc');

                document.getElementById('masanpham').value = magoc;
                document.getElementById('masanpham').readOnly = true;
                document.getElementById('masanpham').required = false;
                document.getElementById('magoc').value = magoc;
                document.getElementById('tensanpham').value = btn.getAttribute('data-ten');
                document.getElementById('danhmuc').value = btn.getAttribute('data-danhmuc');
                document.getElementById('hinhanh_cu').value = btn.getAttribute('data-anh');
                document.getElementById('giasanpham').value = Number(btn.getAttribute('data-gia')).toLocaleString("vi-VN");
                document.getElementById('ngayban').value = btn.getAttribute('data-ngay');
                document.getElementById('trangthai').value = btn.getAttribute('data-trangthai');
                document.getElementById('formAction').value = "update";

                document.getElementById('sizeSectionWrap').style.display = "none";
                const note = document.getElementById('sizeEditNote');
                note.style.display = "block";
                document.getElementById('linkQuanLySize').href =
                        "${pageContext.request.contextPath}/quanlysizeservlet?magoc=" + encodeURIComponent(magoc);

                const submitBtn = document.getElementById('btnSubmit');
                submitBtn.innerText = "Cập nhật sản phẩm";
                submitBtn.className = "btn-action btn-sua";
                const img = document.getElementById('formImgPreview');
                img.src = "${pageContext.request.contextPath}/image/" + btn.getAttribute('data-anh');
                img.style.display = "block";
                window.scrollTo({top: 0, behavior: 'smooth'});
            }

            function resetForm() {
                document.getElementById('productForm').reset();
                document.getElementById('masanpham').readOnly = false;
                document.getElementById('masanpham').required = true;
                document.getElementById('formAction').value = "add";
                document.getElementById('magoc').value = "";
                document.getElementById('formImgPreview').style.display = "none";
                document.getElementById('sizeSectionWrap').style.display = "flex";
                document.getElementById('sizeEditNote').style.display = "none";
                sizeCheckboxes.forEach(cb => {
                    cb.checked = false;
                    syncQtyInputState(cb);
                });
                const submitBtn = document.getElementById('btnSubmit');
                submitBtn.innerText = "Thêm sản phẩm mới";
                submitBtn.className = "btn-action btn-them";
            }

            function xoaSanPham(magoc) {
                if (!confirm("Xoá sản phẩm \"" + magoc + "\"? Toàn bộ các size của sản phẩm này sẽ bị xoá.")) {
                    return;
                }
                document.getElementById('deleteMagoc').value = magoc;
                document.getElementById('deleteForm').submit();
            }

            function previewImage(input) {
                if (input.files && input.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        const preview = document.getElementById('formImgPreview');
                        preview.src = e.target.result;
                        preview.style.display = "block";
                    }
                    reader.readAsDataURL(input.files[0]);
                }
            }

            document.addEventListener("DOMContentLoaded", function () {
                document.querySelectorAll('.format-date').forEach(el => {
                    let rawDate = el.getAttribute('data-date');
                    if (rawDate && rawDate.includes('-')) {
                        const parts = rawDate.split('-');
                        if (parts.length === 3)
                            el.innerText = parts[2] + '/' + parts[1] + '/' + parts[0];
                    } else {
                        el.innerText = "Chưa cập nhật";
                    }
                });
            });

            const giaInput = document.getElementById("giasanpham");
            giaInput.addEventListener("input", function () {
                let value = this.value.replace(/\D/g, "");
                if (value === "") {
                    this.value = "";
                    return;
                }
                this.value = Number(value).toLocaleString("vi-VN");
            });

            document.getElementById("productForm").addEventListener("submit", function (e) {
                const dangThem = document.getElementById('formAction').value === "add";

                if (dangThem) {
                    const anyChecked = Array.from(sizeCheckboxes).some(cb => cb.checked);
                    if (!anyChecked) {
                        e.preventDefault();
                        const err = document.getElementById('sizeError');
                        err.innerText = "Vui lòng chọn ít nhất 1 kích cỡ.";
                        err.style.display = "block";
                        return;
                    }

                    const defaultQty = soluongMacDinh.value.trim();
                    let thieuSoLuong = false;
                    sizeCheckboxes.forEach(cb => {
                        if (cb.checked) {
                            const input = getQtyInput(cb.value);
                            const val = input ? input.value.trim() : "";
                            if (val === "" && defaultQty === "") {
                                thieuSoLuong = true;
                            }
                        }
                    });
                    if (thieuSoLuong) {
                        e.preventDefault();
                        const err = document.getElementById('sizeError');
                        err.innerText = "Vui lòng nhập số lượng riêng cho từng size, hoặc nhập số lượng mặc định để áp dụng chung.";
                        err.style.display = "block";
                        return;
                    }
                }

                let gia = document.getElementById("giasanpham");
                gia.value = gia.value.replace(/\./g, "");
            });

            document.addEventListener("DOMContentLoaded", function () {
                document.querySelectorAll('.format-price').forEach(el => {
                    let price = el.innerText;
                    if (price && !isNaN(price)) {
                        el.innerText = Number(price).toLocaleString('vi-VN');
                    }
                });
            });

            function locSanPham() {
                const tuKhoa = document.getElementById('searchSanPham').value.trim().toLowerCase();
                const rows = document.querySelectorAll('.list-section tbody tr');

                rows.forEach(row => {
                    const ma = row.children[0].innerText.toLowerCase();
                    const ten = row.children[2].innerText.toLowerCase();
                    const hienThi = ma.includes(tuKhoa) || ten.includes(tuKhoa);
                    row.style.display = hienThi ? '' : 'none';
                });
            }
        </script>
    </body>
</html>