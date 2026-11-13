<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý banner</title>
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

            /* Sidebar */
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
            
            h2.section-title { font-family:'Playfair Display', serif; font-size:20px; color:#8a6a1c; margin:35px 0 15px; display:flex; align-items:center; gap:10px; }
            h2.section-title:first-of-type { margin-top:0; }

            form.admin-form { background-color:#f2efe4; padding:28px; border:2px solid #c9a24b; border-radius:12px; display:grid; grid-template-columns:1fr 1fr; gap:20px; margin-bottom:10px; box-shadow:0 8px 30px rgba(40,30,0,0.14); }
            .form-group { display:flex; flex-direction:column; gap:6px; }
            form.admin-form label { font-weight:600; color:#7d7a70; font-size:12px; text-transform:uppercase; letter-spacing:0.5px; }
            form.admin-form input, form.admin-form select { width:100%; padding:11px 15px; background-color:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; color:#1c1b18; outline:none; font-family:'Inter', sans-serif; font-size:13.5px; transition:all 0.3s ease; }
            form.admin-form input:focus, form.admin-form select:focus { border-color:#c9a24b; box-shadow:0 0 0 3px rgba(201,162,75,0.15); background-color:#ebe6d6; }
            
            .btn-group { grid-column:span 2; display:flex; gap:15px; border-top:1px solid rgba(201,162,75,0.3); padding-top:20px; margin-top:5px; }
            .btn-action { padding:12px 24px; font-size:12.5px; font-weight:700; text-transform:uppercase; cursor:pointer; border-radius:8px; transition:all 0.3s ease; letter-spacing:0.5px; }
            .btn-them { background:linear-gradient(135deg, #c9a24b, #8a6a1c); color:#000000; border:none; box-shadow:0 4px 15px rgba(201,162,75,0.3); }
            .btn-them:hover { opacity:0.9; transform:translateY(-2px); box-shadow:0 6px 20px rgba(201,162,75,0.4); }
            
            .btn-save { background:transparent; color:#8a6a1c; border:1px solid rgba(201,162,75,0.6); font-size:11.5px; font-weight:700; cursor:pointer; border-radius:6px; transition:all 0.2s ease; padding:6px 12px; }
            .btn-save:hover { background:#c9a24b; color:#000; box-shadow:0 0 10px rgba(201,162,75,0.5); }

            .btn-khoa { background:rgba(239, 68, 68, 0.1); color:#b3261e; border:1px solid rgba(239, 68, 68, 0.4); padding:6px 16px; font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; transition:all 0.2s ease; }
            .btn-khoa:hover { background:#b3261e; color:#1c1b18; box-shadow:0 0 10px rgba(239, 68, 68, 0.4); }

            .btn-mokhoa { background:rgba(34, 197, 94, 0.1); color:#2f7d4a; border:1px solid rgba(34, 197, 94, 0.4); padding:6px 16px; font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; transition:all 0.2s ease; }
            .btn-mokhoa:hover { background:#2f7d4a; color:#1c1b18; box-shadow:0 0 10px rgba(34, 197, 94, 0.4); }

            .badge-hoatdong { color:#2f7d4a; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(34, 197, 94, 0.1); border:1px solid rgba(34, 197, 94, 0.4); padding:4px 10px; border-radius:20px; }
            .badge-khoa { color:#b3261e; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(239, 68, 68, 0.1); border:1px solid rgba(239, 68, 68, 0.4); padding:4px 10px; border-radius:20px; }

            .table-container { background-color:#f2efe4; border:2px solid #c9a24b; border-radius:12px; overflow:hidden; box-shadow:0 8px 30px rgba(40,30,0,0.14); margin-bottom:40px; }
            table { width:100%; border-collapse:collapse; }
            table th { background-color:#0d0d0e; color:#c9a24b; padding:15px 18px; text-transform:uppercase; font-size:11.5px; letter-spacing:0.5px; border-bottom:2px solid #c9a24b; border-right:1px solid rgba(201,162,75,0.2); }
            table th:last-child { border-right:none; }
            table td { padding:15px 18px; text-align:center; color:#1c1b18; font-size:13px; border-bottom:1px solid rgba(201,162,75,0.2); border-right:1px solid rgba(201,162,75,0.15); vertical-align:middle; }
            table td:last-child { border-right:none; }
            table tr:last-child td { border-bottom:none; }
            table tr:hover { background-color:rgba(201,162,75,0.03); }

            .anh-thumb { width:50px; height:50px; object-fit:cover; border-radius:6px; border:1px solid #c9a24b; transition:transform 0.2s; }
            .anh-thumb:hover { transform:scale(1.1); }
            .anh-cell { display:flex; gap:6px; justify-content:center; align-items:center; }
            .empty-row td { color:#6f6f6f; padding:24px; font-style:italic; }
            .ghi-chu-nho { font-size:12px; color:#7d7a70; margin-top:8px; margin-bottom:30px; font-style:italic; }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}

            /* ===== Quản lý banner ===== */
            form.admin-form .full { grid-column:span 2; }
            form.admin-form textarea { width:100%; padding:11px 15px; background-color:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; color:#1c1b18; outline:none; font-family:'Inter', sans-serif; font-size:13.5px; resize:vertical; min-height:70px; }
            form.admin-form textarea:focus { border-color:#c9a24b; box-shadow:0 0 0 3px rgba(201,162,75,0.15); }
            .check-row { flex-direction:row !important; align-items:center; gap:10px !important; }
            .check-row input[type=checkbox] { width:auto !important; }
            .check-row label { margin:0; }
            .banner-thumb { width:190px; height:78px; object-fit:cover; border-radius:6px; border:1px solid #c9a24b; display:block; margin:0 auto; }
            .anh-hien-tai { width:260px; max-width:100%; height:100px; object-fit:cover; border-radius:6px; border:1px solid #c9a24b; }
            .tab-loc { display:flex; gap:10px; margin-bottom:20px; flex-wrap:wrap; }
            .tab-loc a { padding:8px 18px; border:1px solid rgba(201,162,75,0.6); border-radius:20px; text-decoration:none; color:#8a6a1c; font-size:12.5px; font-weight:700; text-transform:uppercase; letter-spacing:.4px; }
            .tab-loc a.on, .tab-loc a:hover { background:#c9a24b; color:#000; }
            .badge-vitri { font-size:11px; font-weight:700; text-transform:uppercase; padding:4px 10px; border-radius:20px; border:1px solid rgba(201,162,75,0.6); color:#8a6a1c; background:rgba(201,162,75,0.1); white-space:nowrap; }
            .act-cell { display:flex; gap:6px; justify-content:center; flex-wrap:wrap; }
            .act-cell form { margin:0; }
            a.btn-save { text-decoration:none; display:inline-block; }
            .banner-sub { font-size:12px; color:#7d7a70; margin-top:4px; }
</style>
    </head>
    <body>
        <div class="header">
            <div class="title-page">Quản lý banner</div>
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
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/nguoibanservlet"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhachhangservlet"><i class="fa-solid fa-users icon"></i><span>Quản lý đơn hàng</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhohangservlet"><i class="fa-solid fa-warehouse icon"></i><span>Quản lý kho hàng</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/phieugiamgiaservlet"><i class="fa-solid fa-ticket icon"></i><span>Quản lý mã giảm giá</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/doanhthuservlet"><i class="fa-solid fa-chart-line icon"></i><span>Quản lý doanh thu</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/capnhattrangthaiservlet"><i class="fa-solid fa-box-open icon"></i><span>Cập nhật trạng thái đơn hàng</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlyhoadonservlet"><i class="fa-solid fa-file-invoice icon"></i><span>Quản lý hóa đơn</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyvanchuyenadmin"><i class="fa-solid fa-truck-fast icon"></i><span>Quản lý vận chuyển</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/luongvanchuyenadmin"><i class="fa-solid fa-sack-dollar icon"></i><span>Quản lý nhân sự và lương</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li class="active"><a href="${pageContext.request.contextPath}/quanlybanner"><i class="fa-solid fa-images icon"></i><span>Quản lý banner</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
            </ul>
        </div>

        <div class="container">
            <h1>Quản Lý Banner</h1>
            <div class="title-line"></div>

            <c:if test="${not empty sessionScope.success}">
                <div id="toast-message" style="color:#2f7d4a; background:rgba(46, 204, 113, 0.05); border:1px solid rgba(46, 204, 113, 0.4); padding:14px 20px; margin-bottom:30px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-check"></i> <span>${sessionScope.success}</span>
                </div>
                <% session.removeAttribute("success"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div id="error-message" style="color:#b3261e; background:rgba(231, 76, 60, 0.05); border:1px solid rgba(231, 76, 60, 0.4); padding:14px 20px; margin-bottom:30px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-exclamation"></i> <span>${sessionScope.error}</span>
                </div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <c:set var="ban" value="${banSua}"/>
            <h2 class="section-title">
                <i class="fa-solid ${empty ban ? 'fa-plus' : 'fa-pen-to-square'}"></i>
                <c:choose>
                    <c:when test="${empty ban}">Thêm banner mới</c:when>
                    <c:otherwise>Sửa banner #${ban.id}</c:otherwise>
                </c:choose>
            </h2>
            <form action="${pageContext.request.contextPath}/quanlybanner" method="POST" class="admin-form" enctype="multipart/form-data">
                <c:if test="${not empty ban}"><input type="hidden" name="id" value="${ban.id}"></c:if>

                <div class="form-group">
                    <label>Vị trí hiển thị:</label>
                    <select name="vitri" required>
                        <option value="TRANGCHU" ${ban.vitri == 'TRANGCHU' || (empty ban && locVitri != 'SANPHAM') ? 'selected' : ''}>Trang chủ</option>
                        <option value="SANPHAM" ${ban.vitri == 'SANPHAM' || (empty ban && locVitri == 'SANPHAM') ? 'selected' : ''}>Trang sản phẩm</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Thứ tự (số nhỏ hiện trước):</label>
                    <input type="number" name="thutu" value="${empty ban ? 0 : ban.thutu}" min="0" max="9999">
                </div>
                <div class="form-group">
                    <label>Dòng nhỏ phía trên tiêu đề:</label>
                    <input type="text" name="nhan" maxlength="100" value="<c:out value='${ban.nhan}'/>" placeholder="VD: PREMIER LEAGUE · 2026-27">
                </div>
                <div class="form-group">
                    <label>Tiêu đề:</label>
                    <input type="text" name="tieude" maxlength="150" value="<c:out value='${ban.tieude}'/>" placeholder="VD: Man United · Man City">
                </div>
                <div class="form-group full">
                    <label>Mô tả ngắn:</label>
                    <textarea name="mota" maxlength="300" placeholder="Tối đa 300 ký tự"><c:out value='${ban.mota}'/></textarea>
                </div>
                <div class="form-group">
                    <label>Chữ trên nút:</label>
                    <input type="text" name="nuttext" maxlength="50" value="<c:out value='${ban.nuttext}'/>" placeholder="VD: XEM NGAY (để trống = không có nút)">
                </div>
                <div class="form-group">
                    <label>Liên kết khi bấm nút:</label>
                    <input type="text" name="lienket" maxlength="300" value="<c:out value='${ban.lienket}'/>" placeholder="VD: nguoidungservlet hoặc https://...">
                </div>
                <div class="form-group full">
                    <label>Ảnh banner <c:if test="${empty ban}">(bắt buộc)</c:if><c:if test="${not empty ban}">(chọn ảnh mới nếu muốn thay)</c:if>:</label>
                    <input type="file" name="hinhanh" accept="image/*" ${empty ban ? 'required' : ''}>
                    <c:if test="${not empty ban}">
                        <div style="margin-top:8px;"><img class="anh-hien-tai" src="${pageContext.request.contextPath}/image/${ban.hinhanh}" alt="Ảnh hiện tại"></div>
                    </c:if>
                    <div class="banner-sub">Nên dùng ảnh ngang tỉ lệ khoảng 3:1 (VD 1600×530). Định dạng png, jpg, jpeg, gif, webp, bmp, avif; tối đa 10MB.</div>
                </div>
                <div class="form-group check-row full">
                    <input type="checkbox" id="hienthi" name="hienthi" ${empty ban || ban.hienthi ? 'checked' : ''}>
                    <label for="hienthi">Hiển thị banner này trên website</label>
                </div>
                <div class="btn-group">
                    <c:choose>
                        <c:when test="${empty ban}">
                            <button type="submit" name="action" value="them" class="btn-action btn-them">Thêm banner</button>
                        </c:when>
                        <c:otherwise>
                            <button type="submit" name="action" value="sua" class="btn-action btn-them">Lưu thay đổi</button>
                            <a href="${pageContext.request.contextPath}/quanlybanner" class="btn-action" style="text-decoration:none;color:#8a6a1c;border:1px solid rgba(201,162,75,0.6);">Hủy sửa</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </form>
            <p class="ghi-chu-nho">Trang chủ: các banner đang hiện chạy thành slideshow theo thứ tự. Trang sản phẩm: banner hiện ở đầu trang, trước danh sách sản phẩm. Nếu không có banner nào đang hiện thì trang sản phẩm sẽ không có banner.</p>

            <h2 class="section-title"><i class="fa-solid fa-images"></i> Danh sách banner (${dsBanner.size()})</h2>
            <div class="tab-loc">
                <a href="${pageContext.request.contextPath}/quanlybanner" class="${empty locVitri ? 'on' : ''}">Tất cả</a>
                <a href="${pageContext.request.contextPath}/quanlybanner?vitri=TRANGCHU" class="${locVitri == 'TRANGCHU' ? 'on' : ''}">Trang chủ</a>
                <a href="${pageContext.request.contextPath}/quanlybanner?vitri=SANPHAM" class="${locVitri == 'SANPHAM' ? 'on' : ''}">Trang sản phẩm</a>
            </div>
            <div class="table-container">
                <table>
                    <thead>
                        <tr><th>Ảnh</th><th>Nội dung</th><th>Vị trí</th><th>Thứ tự</th><th>Liên kết / Nút</th><th>Trạng thái</th><th>Chức năng</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${dsBanner}">
                            <tr>
                                <td><a href="${pageContext.request.contextPath}/image/${b.hinhanh}" target="_blank"><img class="banner-thumb" src="${pageContext.request.contextPath}/image/${b.hinhanh}" alt="Banner"></a></td>
                                <td style="text-align:left;">
                                    <c:if test="${not empty b.nhan}"><div class="banner-sub"><c:out value="${b.nhan}"/></div></c:if>
                                    <strong><c:out value="${empty b.tieude ? '(không có tiêu đề)' : b.tieude}"/></strong>
                                    <c:if test="${not empty b.mota}"><div class="banner-sub"><c:out value="${b.mota}"/></div></c:if>
                                </td>
                                <td><span class="badge-vitri">${b.vitri == 'TRANGCHU' ? 'Trang chủ' : 'Trang sản phẩm'}</span></td>
                                <td>${b.thutu}</td>
                                <td style="font-size:12px;">
                                    <c:out value="${empty b.lienket ? '—' : b.lienket}"/>
                                    <c:if test="${not empty b.nuttext}"><div class="banner-sub">Nút: <c:out value="${b.nuttext}"/></div></c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${b.hienthi}"><span class="badge-hoatdong">Đang hiện</span></c:when>
                                        <c:otherwise><span class="badge-khoa">Đã ẩn</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="act-cell">
                                        <a href="${pageContext.request.contextPath}/quanlybanner?action=edit&id=${b.id}&vitri=${locVitri}" class="btn-save">Sửa</a>
                                        <form action="${pageContext.request.contextPath}/quanlybanner" method="POST">
                                            <input type="hidden" name="id" value="${b.id}">
                                            <c:choose>
                                                <c:when test="${b.hienthi}"><button type="submit" name="action" value="an" class="btn-khoa">Ẩn</button></c:when>
                                                <c:otherwise><button type="submit" name="action" value="hien" class="btn-mokhoa">Hiện</button></c:otherwise>
                                            </c:choose>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/quanlybanner" method="POST" onsubmit="return confirm('Xóa hẳn banner này? Muốn giữ lại thì hãy dùng nút Ẩn.');">
                                            <input type="hidden" name="id" value="${b.id}">
                                            <button type="submit" name="action" value="xoa" class="btn-khoa">Xóa</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty dsBanner}">
                            <tr class="empty-row"><td colspan="7">Chưa có banner nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
