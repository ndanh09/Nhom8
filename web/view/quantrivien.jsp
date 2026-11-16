<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản trị viên</title>
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
</style>
    </head>
    <body>
        <div class="header">           
            <div class="title-page">Quản trị viên</div>
            <div class="user">
                <i class="fa-solid fa-crown"></i> Xin chào, Admin
            </div>
        </div>
        
        <div class="sidebar">
            <div class="sidebar-logo">
                <img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo">
            </div>           
            <ul>               
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li class="active"><a href=""><i class="fa-solid fa-user-shield icon"></i><span>Quản trị viên</span></a></li></c:if>
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
                <c:if test="${sessionScope.vaiTro == 'ADMIN' || sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlydanhgia"><i class="fa-solid fa-star icon"></i><span>Quản lý đánh giá</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
            </ul>
        </div>
        
        <div class="container">
            <h1>Quản Lý Tài Khoản</h1>
            <div class="title-line"></div>
            
            <%-- Thông báo --%>
            <c:if test="${not empty success}">
                <div id="toast-message" style="color:#2f7d4a; background:rgba(46, 204, 113, 0.05); border:1px solid rgba(46, 204, 113, 0.4); padding:14px 20px; margin-bottom:30px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-check"></i> <span>${success}</span>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div id="error-message" style="color:#b3261e; background:rgba(231, 76, 60, 0.05); border:1px solid rgba(231, 76, 60, 0.4); padding:14px 20px; margin-bottom:30px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-exclamation"></i> <span>${error}</span>
                </div>
            </c:if>

            <form action="qtv" method="POST" class="admin-form" id="formTaiKhoan">
                <input type="hidden" name="loaitk" value="kh">
                <input type="hidden" name="ngaydangky" id="f_ngaydangky" value="${ngaydangky}">
                <div class="form-group"><label>Họ và tên:</label><input type="text" name="hoten" id="f_hoten" value="${hoten}" placeholder="Nhập họ và tên" required></div>
                <div class="form-group"><label>Email:</label><input type="text" name="email" id="f_email" value="${email}" placeholder="Nhập email" required></div>
                <div class="form-group"><label>Số điện thoại:</label><input type="text" name="sodienthoai" id="f_sdt" value="${sodienthoai}" placeholder="Nhập số điện thoại" required></div>
                <div class="form-group" id="grp-diachi"><label>Địa chỉ:</label><input type="text" name="diachi" id="f_diachi" placeholder="Nhập địa chỉ (không bắt buộc)"></div>
                <div class="form-group"><label>Mật khẩu:</label><input type="password" name="matkhau" id="f_matkhau" value="${matkhau}" placeholder="Nhập mật khẩu" required></div>
                <div class="btn-group">
                    <button type="submit" name="action" value="add_taikhoan" class="btn-action btn-them">Thêm tài khoản</button>
                    <button type="submit" name="action" value="update" class="btn-action btn-sua">Cập nhật (chỉ áp dụng cho quản trị viên)</button>
                </div>
            </form>

            <form action="qtv" method="GET" class="search-form">
                <div class="form-group">
                    <label>Tìm kiếm</label>
                    <input type="text" name="keyword" value="${keyword}" placeholder="Tìm theo họ tên hoặc email...">
                </div>
                <button type="submit" class="btn-action btn-them"><i class="fa-solid fa-magnifying-glass"></i> Tìm</button>
            </form>

            <div class="table-container">
                <table>
                    <thead>
                        <tr><th>Loại</th><th>Họ và tên</th><th>Email</th><th>Số điện thoại</th><th>Ngày ĐK / Địa chỉ</th><th>Mật khẩu</th><th>Trạng thái</th><th>Chức năng</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="qtv" items="${listqtv}">
                            <tr>
                                <td><span class="badge-qtv">Quản trị viên</span></td>
                                <td>${qtv.hoten}</td>
                                <td><strong style="color:#8a6a1c;">${qtv.email}</strong></td>
                                <td>${qtv.sodienthoai}</td>
                                <td>
                                    <fmt:parseDate value="${qtv.ngaydangky}" var="parsedDate" pattern="yyyy-MM-dd"/>
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy"/>
                                </td>
                                <td>${qtv.matkhau}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${qtv.trangthai == 'Đã khóa'}"><span class="badge-khoa">Đã khóa</span></c:when>
                                        <c:otherwise><span class="badge-hoatdong">Đang hoạt động</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="display:flex;justify-content:center;align-items:center;gap:8px;">
                                    <form action="qtv" method="GET" style="margin:0;">
                                        <input type="hidden" name="action" value="edit">
                                        <input type="hidden" name="hoten" value="${qtv.hoten}">
                                        <input type="hidden" name="email" value="${qtv.email}">
                                        <input type="hidden" name="sodienthoai" value="${qtv.sodienthoai}">
                                        <input type="hidden" name="ngaydangky" value="${qtv.ngaydangky}">
                                        <input type="hidden" name="matkhau" value="${qtv.matkhau}">
                                        <button type="submit" class="btn-update">Sửa</button>
                                    </form>
                                    <form action="qtv" method="POST" style="margin:0;">
                                        <input type="hidden" name="email" value="${qtv.email}">
                                        <input type="hidden" name="loaitk" value="qtv">
                                        <c:choose>
                                            <c:when test="${qtv.trangthai == 'Đã khóa'}">
                                                <button type="submit" name="action" value="mokhoa_taikhoan" class="btn-mokhoa">Mở khóa</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" name="action" value="khoa_taikhoan" class="btn-xoa"
                                                        onclick="return confirm('Khóa tài khoản quản trị viên này? Có thể mở khóa lại sau.')">Khóa</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:forEach var="kh" items="${listkh}">
                            <tr>
                                <td><span class="badge-kh">Khách hàng</span></td>
                                <td>${kh.hoten}</td>
                                <td><strong style="color:#8a6a1c;">${kh.email}</strong></td>
                                <td>${kh.sodienthoai}</td>
                                <td>${kh.diachi}</td>
                                <td>${kh.matkhau}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${kh.trangthai == 'Đã khóa'}"><span class="badge-khoa">Đã khóa</span></c:when>
                                        <c:otherwise><span class="badge-hoatdong">Đang hoạt động</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="display:flex;justify-content:center;align-items:center;gap:8px;">
                                    <form action="qtv" method="POST" style="margin:0;">
                                        <input type="hidden" name="email" value="${kh.email}">
                                        <input type="hidden" name="loaitk" value="kh">
                                        <c:choose>
                                            <c:when test="${kh.trangthai == 'Đã khóa'}">
                                                <button type="submit" name="action" value="mokhoa_taikhoan" class="btn-mokhoa">Mở khóa</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" name="action" value="khoa_taikhoan" class="btn-xoa"
                                                        onclick="return confirm('Khóa tài khoản khách hàng này? Có thể mở khóa lại sau.')">Khóa</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>