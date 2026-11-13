<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Đơn Hàng - LODI AUTHENTIC STORE</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <style>
            /*==============================
                RESET & BASE
            ==============================*/
            *{
                margin:0;
                padding:0;
                box-sizing:border-box;
                font-family:'Inter', sans-serif;
            }

            html{
                scroll-behavior:smooth;
            }

            body{
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

            /*==============================
                HEADER
            ==============================*/
            .header{
                height:70px;
                display:flex;
                justify-content:space-between;
                align-items:center;
                padding:0 35px;
                background:#0d0d0e;
                border-bottom:2px solid #8a6a1c;
                box-shadow:0 4px 25px rgba(40,30,0,0.14);
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

            /*==============================
                WRAPPER
            ==============================*/
            .wrapper{
                display:flex;
                margin-top:70px;
                min-height:calc(100vh - 70px);
                position:relative;
                z-index:1;
            }

            /*==============================
                SIDEBAR
            ==============================*/
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

            /*==============================
                MAIN CONTENT
            ==============================*/
            .main-content{
                flex:1;
                margin-left:275px;
                padding:40px 45px;
                width:calc(100% - 275px);
            }

            .container-fluid-custom{
                display:flex;
                flex-direction:column;
                gap:35px;
            }

            /*==============================
                CARDS & SECTIONS
            ==============================*/
            .form-section,
            .table-section{
                background-color:#f2efe4;
                border-radius:12px;
                padding:28px;
                border:2px solid #c9a24b;
                box-shadow:0 8px 30px rgba(40,30,0,0.14);
            }

            h1{
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

            /*==============================
                FORM STYLING
            ==============================*/
            .form-row{
                display:grid;
                grid-template-columns:repeat(2, minmax(280px, 1fr));
                gap:20px;
                margin-bottom:20px;
            }

            .group{
                display:flex;
                flex-direction:column;
                gap:6px;
            }

            .group label{
                color:#7d7a70;
                font-weight:600;
                font-size:12px;
                letter-spacing:0.5px;
                text-transform:uppercase;
            }

            .group input,
            .group select{
                height:44px;
                padding:0 15px;
                border-radius:8px;
                border:1px solid rgba(201,162,75,0.4);
                background-color:#ebe6d6;
                color:#1c1b18;
                transition:all 0.3s ease;
                font-size:13.5px;
            }

            .group input::placeholder{
                color:#555;
            }

            .group input:focus,
            .group select:focus{
                outline:none;
                border-color:#c9a24b;
                box-shadow:0 0 0 3px rgba(201,162,75,0.15);
                background-color:#ebe6d6;
            }

            /*==============================
                BUTTONS
            ==============================*/
            .btn-submit{
                border:none;
                border-radius:8px;
                padding:12px 24px;
                font-size:12.5px;
                font-weight:700;
                cursor:pointer;
                transition:all 0.3s ease;
                text-transform:uppercase;
                letter-spacing:0.5px;
                background:linear-gradient(135deg, #c9a24b, #8a6a1c);
                color:#000000;
                display:inline-block;
                text-align:center;
                text-decoration:none;
            }

            .btn-submit:hover{
                transform:translateY(-2px);
                box-shadow:0 6px 20px rgba(201,162,75,0.3);
                opacity:0.9;
                color:#000;
            }

            .btn-table-edit{
                background:rgba(47, 141, 228, 0.15);
                color:#1f5fa8;
                border:1px solid rgba(47, 141, 228, 0.4);
                padding:7px 14px;
                border-radius:6px;
                font-size:11.5px;
                font-weight:700;
                text-transform:uppercase;
                display:inline-block;
                transition:all 0.2s ease;
                text-decoration:none;
            }

            .btn-table-edit:hover{
                background:#2f8de4;
                color:#1c1b18;
                box-shadow:0 0 10px rgba(47, 141, 228, 0.4);
                transform:translateY(-2px);
            }

            .btn-xoa{
                background:rgba(239, 68, 68, 0.1);
                color:#b3261e;
                border:1px solid rgba(239, 68, 68, 0.4);
                padding:7px 14px;
                border-radius:6px;
                font-size:11.5px;
                font-weight:700;
                text-transform:uppercase;
                display:inline-block;
                transition:all 0.2s ease;
                text-decoration:none;
                cursor:pointer;
            }

            .btn-xoa:hover{
                background:#b3261e;
                color:#1c1b18;
                box-shadow:0 0 10px rgba(239, 68, 68, 0.4);
                transform:translateY(-2px);
            }

            /*==============================
                TABLE STYLING
            ==============================*/
            .table-section{
                overflow-x:auto;
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
                text-align:center;
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

            /*==============================
                SCROLLBAR
            ==============================*/
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

            /*==============================
                RESPONSIVE DESIGN
            ==============================*/
            @media(max-width: 1200px){
                .form-row{
                    grid-template-columns:1fr;
                }
            }

            @media(max-width: 768px){
                .main-content{
                    margin-left:0;
                    width:100%;
                    padding:20px;
                }
                .sidebar{
                    display:none; /* Can be handled via mobile toggle if required */
                }
            }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>
    <body>
        <div class="header">
            <div class="header-title">Quản lý đơn hàng</div>
            <div class="header-admin">
                <i class="fa-solid fa-crown"></i>
                <span>Xin chào, Admin</span>
            </div>
        </div>

        <div class="wrapper">
            <div class="sidebar">
                <div class="sidebar-logo">
                    <img src="${pageContext.request.contextPath}/image/logo.png" alt="Lodi Authentic Store">
                </div>

                <ul>                
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href=""><i class="fa-solid fa-user-shield icon"></i><span>Quản trị viên</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/nguoibanservlet"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li class="active"><a href="${pageContext.request.contextPath}/quanlykhachhangservlet"><i class="fa-solid fa-users icon"></i><span>Quản lý đơn hàng</span></a></li></c:if>
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
                <div class="container-fluid-custom">
                    <c:if test="${not empty error}">
                        <div style="padding:14px 20px;border-radius:8px;font-size:14px;background:rgba(231,76,60,0.05);color:#b3261e;border:1px solid rgba(231,76,60,0.4);">
                            <i class="fa-solid fa-circle-exclamation"></i> ${error}
                        </div>
                        <% session.removeAttribute("error"); %>
                    </c:if>
                    <%-- Không còn chức năng thêm: form chỉ hiện khi bấm "Sửa" một đơn hàng (kh != null) --%>
                    <c:if test="${kh != null}">
                    <h1>Thông tin khách hàng</h1>

                    <div class="form-section">
                        <form action="quanlykhachhangservlet" method="POST">
                            <input type="hidden" name="madonhang" value="${kh.madonhang}">
                            <div class="form-row">
                                <div class="group">
                                    <label>Mã khách hàng</label>
                                    <input type="text"
                                           name="makhachhang"
                                           value="${kh.makhachhang}"
                                           placeholder="VD: KH001"
                                           required>
                                </div>

                                <div class="group">
                                    <label>Họ và tên</label>
                                    <input type="text"
                                           name="hoten"
                                           value="${kh.hoten}"
                                           placeholder="Nhập họ tên"
                                           required>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="group">
                                    <label>Số điện thoại</label>
                                    <input type="text"
                                           name="sodienthoai"
                                           value="${kh.sodienthoai}"
                                           placeholder="09xxxxxxxx">
                                </div>

                                <div class="group">
                                    <label>Email</label>
                                    <input type="email"
                                           name="email"
                                           value="${kh.email}"
                                           placeholder="example@gmail.com">
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="group">
                                    <label>Địa chỉ</label>
                                    <input type="text"
                                           name="diachi"
                                           value="${kh.diachi}">
                                </div>

                                <div class="group">
                                    <label>Ghi chú</label>
                                    <input type="text"
                                           name="ghichu"
                                           value="${kh.ghichu}">
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="group">
                                    <label>Phương thức thanh toán</label>
                                    <input type="text"
                                           name="phuongthucthanhtoan"
                                           value="${kh.phuongthucthanhtoan}">
                                </div>

                                <div class="group">
                                    <label>Tổng tiền</label>
                                    <input type="number"
                                           name="tongtien"
                                           value="${kh.tongtien}">
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="group">
                                    <label>Mã sản phẩm</label>
                                    <input type="text"
                                           name="masanpham"
                                           value="${kh.masanpham}">
                                </div>
                            </div>

                            <button type="submit" class="btn-submit">
                                Cập nhật khách hàng
                            </button>

                        </form>
                    </div>
                    </c:if>

                    <div class="table-section">
                        <form action="quanlykhachhangservlet" method="GET" style="display:flex;gap:15px;align-items:flex-end;margin-bottom:20px;">
                            <div class="group" style="margin:0;">
                                <label>Tìm kiếm</label>
                                <input type="text" name="keyword" value="${keyword}" placeholder="Mã KH, họ tên, SĐT hoặc email...">
                            </div>
                            <button type="submit" class="btn-submit" style="height:fit-content;">Tìm</button>
                        </form>
                        <table>
                            <thead>
                                <tr>
                                    <th>Mã đơn hàng</th>
                                    <th>Mã khách hàng</th>
                                    <th>Họ tên khách hàng</th>
                                    <th>Số điện thoại</th>
                                    <th>Địa chỉ khách hàng</th>
                                    <th>Email khách hàng</th>
                                    <th>Ghi chú</th>
                                    <th>Thanh toán</th>
                                    <th>Ngày đặt hàng</th>
                                    <th>Tổng tiền</th>
                                    <th>Mã sản phẩm</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="kh" items="${listKhachHang}">
                                    <tr>
                                        <td>${kh.madonhang}</td>
                                        <td>${kh.makhachhang}</td>
                                        <td>${kh.hoten}</td>
                                        <td>${kh.sodienthoai}</td>
                                        <td>${kh.diachi}</td>
                                        <td>${kh.email}</td>
                                        <td>${kh.ghichu}</td>
                                        <td>${kh.phuongthucthanhtoan}</td>
                                        <td>${kh.ngaydat}</td>
                                        <td>${kh.tongtien}</td>
                                        <td>${kh.masanpham}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${kh.trangthai == 'Đã khóa'}">
                                                    <span style="color:#b3261e;font-weight:700;">Đã khóa</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color:#2f7d4a;font-weight:700;">Đang hoạt động</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div style="display:flex; justify-content:center; align-items:center; gap:6px;">
                                                <c:if test="${kh.trangthai != 'Đã khóa'}">
                                                    <a href="quanlykhachhangservlet?action=edit&id=${kh.madonhang}" class="btn-table-edit">
                                                        Cập nhật
                                                    </a>
                                                </c:if>
                                                <c:choose>
                                                    <c:when test="${kh.trangthai == 'Đã khóa'}">
                                                        <a href="quanlykhachhangservlet?action=mokhoa&id=${kh.madonhang}&keyword=${keyword}" class="btn-table-edit">
                                                            Mở khóa
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="quanlykhachhangservlet?action=khoa&id=${kh.madonhang}&keyword=${keyword}"
                                                           onclick="return confirm('Khóa đơn hàng này? Khi bị khóa sẽ không thể thao tác gì với đơn (cập nhật, đổi trạng thái, hủy, giao hàng...) cho tới khi mở khóa.')" class="btn-xoa">
                                                            Khóa
                                                        </a>
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