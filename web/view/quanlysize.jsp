<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý size sản phẩm</title>

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
                text-shadow:0 1px 1px rgba(255,255,255,0.4);
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

            form.form-nho{
                display:flex;
                flex-direction:column;
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

            table.bang-size {
                min-width:0;
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
            <div class="header-title">Quản lý size</div>
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
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/nguoibanservlet"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li class="active"><a href="#"><i class="fa-solid fa-ruler icon"></i><span>Quản lý size</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhachhangservlet"><i class="fa-solid fa-users icon"></i><span>Quản lý đơn hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhohangservlet"><i class="fa-solid fa-warehouse icon"></i><span>Quản lý kho hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/phieugiamgiaservlet"><i class="fa-solid fa-ticket icon"></i><span>Quản lý mã giảm giá</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/doanhthuservlet"><i class="fa-solid fa-chart-line icon"></i><span>Quản lý doanh thu</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/capnhattrangthaiservlet"><i class="fa-solid fa-box-open icon"></i><span>Cập nhật trạng thái đơn hàng</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlyhoadonservlet"><i class="fa-solid fa-file-invoice icon"></i><span>Quản lý hóa đơn</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyvanchuyenadmin"><i class="fa-solid fa-truck-fast icon"></i><span>Quản lý vận chuyển</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/luongvanchuyenadmin"><i class="fa-solid fa-sack-dollar icon"></i><span>Quản lý nhân sự và lương</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlybanner"><i class="fa-solid fa-images icon"></i><span>Quản lý banner</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                    <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
                </ul>
            </div>

            <div class="main-content">
                <div class="container">

                    <div style="margin-bottom:10px;">
                        <a href="${pageContext.request.contextPath}/nguoibanservlet" style="color:#8a6a1c; text-decoration:none; font-size:13.5px; font-weight:600; display:inline-flex; align-items:center; gap:8px;">
                            <i class="fa-solid fa-arrow-left"></i> Quay lại Quản lý sản phẩm
                        </a>
                    </div>

                    <c:if test="${not empty success || not empty error}">
                        <div style="padding:14px 20px; margin-bottom:25px; border-radius:8px; font-size:14px; display:flex; align-items:center; gap:10px; ${not empty success ? 'background:rgba(46, 204, 113, 0.05); color:#2f7d4a; border:1px solid rgba(46, 204, 113, 0.4);' : 'background:rgba(231, 76, 60, 0.05); color:#b3261e; border:1px solid rgba(231, 76, 60, 0.4);'}">
                            <i class="${not empty success ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-exclamation'}"></i>
                            <span>${not empty success ? success : error}</span>
                        </div>
                        <c:remove var="success" scope="session"/>
                        <c:remove var="error" scope="session"/>
                    </c:if>

                    <div class="form-section">
                        <h2>Thông tin sản phẩm</h2>
                        <div style="display:flex; gap:20px; align-items:flex-start;">
                            <img src="${pageContext.request.contextPath}/image/${daidien.hinhanh}" style="width:90px; height:90px; object-fit:cover; border-radius:8px; border:2px solid rgba(201,162,75,0.4); flex-shrink:0;">
                            <div>
                                <div style="color:#1c1b18; font-size:18px; font-weight:700; margin-bottom:10px; font-family:'Playfair Display', serif;">${daidien.tensanpham}</div>
                                <div style="color:#7d7a70; font-size:13.5px; line-height:1.8;">
                                    Mã sản phẩm gốc: <b style="color:#8a6a1c;">${magoc}</b><br>
                                    Danh mục: ${daidien.danhmuc}<br>
                                    Giá bán: <span class="format-price">${daidien.giasanpham}</span>đ<br>
                                    Tổng số lượng (tất cả size): <b style="color:#1c1b18;">${daidien.tongsoluong}</b>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="list-section">
                        <h2>Chi tiết theo size</h2>
                        <table class="bang-size">
                            <thead>
                                <tr><th>Mã đầy đủ</th><th>Size</th><th>Số lượng</th><th>Giá</th><th>Trạng thái</th><th>Hành động</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${dssize}">
                                    <tr>
                                        <td>${s.masanpham}</td>
                                        <td><b>${s.kichco}</b></td>
                                        <td>${s.soluongsanpham}</td>
                                        <td><span class="format-price">${s.giasanpham}</span>đ</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${s.trangthai == '1'}">
                                                    <span class="badge badge-dangban">Còn hàng</span>
                                                </c:when>
                                                <c:when test="${s.trangthai == '2'}">
                                                    <span class="badge badge-hethang">Hết hàng</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-ngungban">Ngừng bán</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <button class="btn-table btn-table-edit" onclick="suaSize(this)"
                                                    data-ma="${s.masanpham}" data-size="${s.kichco}"
                                                    data-soluong="${s.soluongsanpham}" data-gia="${s.giasanpham}">Sửa</button>
                                            <button class="btn-table btn-table-del" onclick="xoaSize('${s.masanpham}')">Xoá</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="form-section" style="max-width:450px;">
                        <h2>Thêm size mới</h2>
                        <form id="addSizeForm" class="form-nho" action="${pageContext.request.contextPath}/quanlysizeservlet" method="POST">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="magoc" value="${magoc}">
                            <div class="form-group">
                                <label>Size:</label>
                                <select name="kichco" required>
                                    <c:forEach var="sz" items="${tatcasize}">
                                        <option value="${sz}">${sz}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Số lượng:</label>
                                <input type="number" name="soluong" min="0" required>
                            </div>
                            <div class="btn-group" style="border-top:none; padding-top:0; margin-top:0;">
                                <button type="submit" class="btn-action btn-them">Thêm size</button>
                            </div>
                        </form>
                    </div>

                    <div class="form-section" id="suaSizeBox" style="display:none; max-width:450px;">
                        <h2>Sửa size <span id="suaSizeTen" style="color:#8a6a1c;"></span></h2>
                        <form id="editSizeForm" class="form-nho" action="${pageContext.request.contextPath}/quanlysizeservlet" method="POST">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="magoc" value="${magoc}">
                            <input type="hidden" name="masanpham" id="editMasanpham">
                            <div class="form-group">
                                <label>Số lượng:</label>
                                <input type="number" name="soluong" id="editSoluong" min="0" required>
                            </div>
                            <div class="form-group">
                                <label>Giá bán:</label>
                                <div class="price-input">
                                    <input type="text" name="giasanpham" id="editGia" required>
                                    <span>đ</span>
                                </div>
                            </div>
                            <div class="btn-group">
                                <button type="submit" class="btn-action btn-sua">Lưu</button>
                                <button type="button" class="btn-action btn-clear" onclick="document.getElementById('suaSizeBox').style.display='none';">Huỷ</button>
                            </div>
                        </form>
                    </div>

                    <form id="deleteSizeForm" action="${pageContext.request.contextPath}/quanlysizeservlet" method="POST" style="display:none;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="magoc" value="${magoc}">
                        <input type="hidden" name="masanpham" id="deleteMasanpham">
                    </form>

                </div>
            </div>
        </div>

        <script>
            function suaSize(btn) {
                document.getElementById('editMasanpham').value = btn.getAttribute('data-ma');
                document.getElementById('editSoluong').value = btn.getAttribute('data-soluong');
                document.getElementById('editGia').value = Number(btn.getAttribute('data-gia')).toLocaleString('vi-VN');
                document.getElementById('suaSizeTen').innerText = btn.getAttribute('data-size');
                document.getElementById('suaSizeBox').style.display = 'block';
                document.getElementById('suaSizeBox').scrollIntoView({behavior: 'smooth'});
            }

            function xoaSize(masanpham) {
                if (!confirm("Xoá size \"" + masanpham + "\"?")) {
                    return;
                }
                document.getElementById('deleteMasanpham').value = masanpham;
                document.getElementById('deleteSizeForm').submit();
            }

            const editGiaInput = document.getElementById('editGia');
            editGiaInput.addEventListener('input', function () {
                let value = this.value.replace(/\D/g, "");
                this.value = value === "" ? "" : Number(value).toLocaleString('vi-VN');
            });
            document.getElementById('editSizeForm').addEventListener('submit', function () {
                editGiaInput.value = editGiaInput.value.replace(/\./g, "");
            });

            document.addEventListener("DOMContentLoaded", function () {
                document.querySelectorAll('.format-price').forEach(el => {
                    let price = el.innerText;
                    if (price && !isNaN(price)) {
                        el.innerText = Number(price).toLocaleString('vi-VN');
                    }
                });
            });
        </script>
    </body>
</html>