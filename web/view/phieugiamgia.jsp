<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý mã giảm giá - Lodi Authentic Store</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
        
        <style>
            * { margin:0; padding:0; box-sizing:border-box; font-family:'Inter', sans-serif; }
            html { scroll-behavior:smooth; }
            body { background:#e7e2d2; color:#1c1b18; display:flex; flex-direction:column; min-height:100vh; position:relative; -webkit-font-smoothing:antialiased; }
            
            body::before { content:""; position:fixed; top:0; left:0; width:100%; height:100%; background-image:#ffffff; background-size:60px 60px; z-index:0; pointer-events:none; }

            /* HEADER */
            .page-header { height:70px; display:flex; justify-content:space-between; align-items:center; padding:0 35px; background:linear-gradient(135deg, #f6edcf 0%, #c9a24b 50%, #8a6a1c 100%); border-bottom:2px solid #8a6a1c; box-shadow:0 4px 25px rgba(40,30,0,0.14); position:fixed; top:0; left:0; width:100%; z-index:1000; }
            .page-header h1 { margin:0; font-family:'Playfair Display', serif; font-size:22px; font-weight:700; color:#111; letter-spacing:2px; text-transform:uppercase; text-shadow:0 1px 1px rgba(255,255,255,0.4); }
            .header-admin { display:flex; align-items:center; gap:8px; background:rgba(15, 15, 15, 0.85); padding:8px 18px; border-radius:20px; color:#c9a24b; font-size:13px; font-weight:600; text-transform:uppercase; border:1px solid rgba(201,162,75,0.4); box-shadow:inset 0 1px 3px rgba(0,0,0,0.5); }
            .header-admin i { color:#c9a24b; font-size:15px; }

            /* SIDEBAR */
            .sidebar { width:275px; flex-shrink:0; background-color:#0d0d0e; position:fixed; top:70px; left:0; bottom:0; z-index:999; border-right:2px solid #c9a24b; display:flex; flex-direction:column; align-items:center; box-shadow:5px 0 25px rgba(0,0,0,0.5); overflow-y:auto; }
            .sidebar::-webkit-scrollbar { width:5px; }
            .sidebar::-webkit-scrollbar-thumb { background:#0d0d0e; border-radius:10px; }
            .sidebar::-webkit-scrollbar-thumb:hover { background:#c9a24b; }

            .sidebar-logo { width:100%; display:flex; justify-content:center; align-items:center; padding:22px 15px; border-bottom:2px solid #c9a24b; margin-bottom:15px; background:rgba(255,255,255,0.01); }
            .sidebar-logo img { max-width:130px; transition:transform 0.3s ease; filter:drop-shadow(0px 0px 10px rgba(201,162,75,0.4)); }
            .sidebar-logo img:hover { transform:scale(1.05); }

            .sidebar ul { list-style:none; width:100%; padding:0 12px; }
            .sidebar ul li { margin-bottom:4px; }
            .sidebar ul li a { display:flex; align-items:center; gap:14px; padding:12px 16px; border-radius:10px; color:#a3a3a3; text-decoration:none; transition:all 0.25s ease-in-out; font-size:13.5px; font-weight:500; white-space:nowrap; }
            .sidebar ul li a:hover { background:rgba(201,162,75,0.08); color:#ead594; transform:translateX(4px); }
            .sidebar ul li a:hover .icon { color:#c9a24b; text-shadow:0 0 10px rgba(201,162,75,0.5); }
            .sidebar ul li.active a { background:linear-gradient(135deg, rgba(201,162,75,0.2), rgba(201,162,75,0.05)); color:#ffffff; font-weight:600; border:1px solid rgba(201,162,75,0.5); box-shadow:0 4px 15px rgba(0,0,0,0.3); }
            .sidebar ul li.active a .icon { color:#c9a24b; text-shadow:0 0 10px rgba(201,162,75,0.6); }
            .icon { width:20px; text-align:center; font-size:15px; color:#8a6a1c; transition:color 0.25s ease; flex-shrink:0; }

            /* CONTAINER & CONTENT */
            .container { margin-left:275px; width:calc(100% - 275px); padding:40px 45px; position:relative; z-index:2; margin-top:70px; max-width:none !important; }
            .container h1 { font-family:'Playfair Display', serif; font-size:32px; letter-spacing:1px; text-transform:uppercase; color:#1c1b18; font-weight:700; margin-bottom:10px; background:none; -webkit-background-clip:text; -webkit-text-fill-color:#0d0d0e; }
            .title-line { height:2px; background:linear-gradient(90deg, #c9a24b, transparent); margin-bottom:30px; }

            /* FORM STYLING */
            .admin-form { background:#f2efe4; border:2px solid #c9a24b; border-radius:12px; padding:30px; display:grid; grid-template-columns:1fr 1fr; gap:20px 30px; margin-bottom:35px; box-shadow:0 8px 30px rgba(40,30,0,0.14); width:100%; }
            .form-group { display:flex; flex-direction:column; gap:6px; }
            .form-group label { font-size:12px; font-weight:600; color:#7d7a70; text-transform:uppercase; letter-spacing:0.5px; }
            .form-group input, .form-group select { padding:12px 15px; background:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; color:#1c1b18; font-size:13.5px; outline:none; transition:all 0.3s ease; width:100%; }
            .form-group input:focus, .form-group select:focus { border-color:#c9a24b; box-shadow:0 0 0 3px rgba(201,162,75,0.15); background:#ebe6d6; }
            .form-group input::placeholder { color:#555; }

            /* BUTTONS */
            .btn-group { grid-column:span 2; display:flex; gap:15px; border-top:1px solid rgba(201,162,75,0.3); padding-top:20px; }
            .btn-action { padding:11px 24px; border:none; border-radius:8px; cursor:pointer; font-weight:700; font-size:12.5px; text-transform:uppercase; letter-spacing:0.5px; transition:all 0.3s ease; }
            .btn-them { background:linear-gradient(135deg, #c9a24b, #8a6a1c); color:#000; box-shadow:0 4px 15px rgba(201,162,75,0.3); }
            .btn-them:hover { opacity:0.9; transform:translateY(-2px); }
            .btn-sua { background:transparent; border:1px solid #c9a24b; color:#8a6a1c; }
            .btn-sua:hover { background:rgba(201,162,75,0.1); color:#8a6a1c; }
            
            /* NÚT LÀM MỚI ĐÃ ĐƯỢC THAY ĐỔI MÀU SANG TRỌNG & HỢP TÔNG HƠN */
            .btn-reset { background:linear-gradient(135deg, #2a2a2a, #f2efe4); border:1px solid rgba(201,162,75,0.35); color:#1c1b18; box-shadow:0 4px 15px rgba(40,30,0,0.14); }
            .btn-reset:hover { background:linear-gradient(135deg, #333333, #f2efe4); border-color:#c9a24b; color:#8a6a1c; transform:translateY(-2px); }

            /* TABLE STYLING */
            .table-container { border-radius:12px; overflow-x:auto; border:2px solid #c9a24b; background:#f2efe4; box-shadow:0 8px 30px rgba(40,30,0,0.14); width:100%; }
            table { width:100%; border-collapse:collapse; background:#f2efe4; min-width:1000px; }
            table th { background:#0d0d0e; color:#c9a24b; padding:16px 18px; border-bottom:2px solid #c9a24b; border-right:1px solid rgba(201,162,75,0.2); text-transform:uppercase; font-size:12px; letter-spacing:0.5px; text-align:center; }
            table th:last-child { border-right:none; }
            table td { padding:15px 18px; border-bottom:1px solid rgba(201,162,75,0.2); border-right:1px solid rgba(201,162,75,0.15); color:#1c1b18; text-align:center; font-size:13.5px; white-space:nowrap; }
            table td:last-child { border-right:none; }
            table tr:last-child td { border-bottom:none; }
            table tr:hover { background:rgba(201,162,75,0.03); }

            /* STATUS BADGES */
            .badge-active { background:rgba(34, 197, 94, 0.1); color:#2f7d4a; border:1px solid rgba(34, 197, 94, 0.4); padding:5px 12px; border-radius:20px; font-size:11px; font-weight:700; text-transform:uppercase; }
            .badge-stop { background:rgba(239, 68, 68, 0.1); color:#b3261e; border:1px solid rgba(239, 68, 68, 0.4); padding:5px 12px; border-radius:20px; font-size:11px; font-weight:700; text-transform:uppercase; }

            @media (max-width: 992px) {
                .container { margin-left:0; width:100%; padding:20px; }
                .sidebar { display:none; }
                .admin-form { grid-template-columns:1fr; }
                .btn-group { grid-column:span 1; }
            }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>

    <body>

        <div class="page-header">
            <h1>Quản lý mã giảm giá</h1>
            <div class="header-admin">
                <i class="fa-solid fa-crown"></i>
                <span>Xin chào, Admin</span>
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
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li class="active"><a href="${pageContext.request.contextPath}/phieugiamgiaservlet"><i class="fa-solid fa-ticket icon"></i><span>Quản lý mã giảm giá</span></a></li></c:if>
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

        <div class="container">
            <h1>Mã giảm giá</h1>
            <div class="title-line"></div>

            <form class="admin-form" action="${pageContext.request.contextPath}/phieugiamgiaservlet" method="post">
                <div class="form-group">
                    <label>Mã giảm giá</label>
                    <input type="text" name="magiamgia" value="${pgg.magiamgia}" placeholder="VD: SALE2026" required>
                </div>

                <div class="form-group">
                    <label>Tên phiếu</label>
                    <input type="text" name="tenphieu" value="${pgg.tenphieu}" placeholder="VD: Giảm giá mùa hè" required>
                </div>

                <div class="form-group">
                    <label>Loại giảm</label>
                    <select name="loaigiam" id="loaigiamSelect" onchange="capNhatDonViGiam()">
                        <option value="0" <c:if test="${pgg.loaigiam == 0}">selected</c:if>>Giảm theo tiền</option>
                        <option value="1" <c:if test="${pgg.loaigiam == 1}">selected</c:if>>Giảm theo %</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Giá trị giảm (<span id="donViGiam">đ</span>)</label>
                    <input type="number" name="giatrigiam" id="giatrigiamInput" min="0" value="${pgg.giatrigiam}" placeholder="0">
                </div>

                <div class="form-group" id="giamToiDaGroup">
                    <label>Giảm tối đa <span style="font-weight:400;text-transform:none;color:#7b808a;">(chỉ áp dụng cho giảm %)</span></label>
                    <input type="number" name="giamtoida" value="${pgg.giamtoida}" placeholder="0">
                </div>

                <div class="form-group">
                    <label>Đơn tối thiểu</label>
                    <input type="number" name="dieukien" value="${pgg.dieukien}" placeholder="0">
                </div>

                <div class="form-group">
                    <label>Số lượng</label>
                    <input type="number" name="soluong" value="${pgg.soluong}" placeholder="0">
                </div>

                <div class="form-group">
                    <label>Ngày bắt đầu</label>
                    <input type="date" name="ngaybatdau" value="${pgg.ngaybatdau}">
                </div>

                <div class="form-group">
                    <label>Ngày kết thúc</label>
                    <input type="date" name="ngayketthuc" value="${pgg.ngayketthuc}">
                </div>

                <div class="form-group">
                    <label>Trạng thái</label>
                    <select name="trangthai">
                        <option value="1" <c:if test="${pgg.trangthai}">selected</c:if>>Hoạt động</option>
                        <option value="0" <c:if test="${!pgg.trangthai}">selected</c:if>>Ngừng</option>
                    </select>
                </div>

                <div class="btn-group">
                    <button class="btn-action btn-them" type="submit" name="action" value="add">
                        <i class="fa-solid fa-plus me-1"></i> Thêm phiếu
                    </button>
                    <button class="btn-action btn-sua" type="submit" name="action" value="update">
                        <i class="fa-solid fa-pen-to-square me-1"></i> Cập nhật
                    </button>
                    <button class="btn-action btn-reset" type="reset">
                        <i class="fa-solid fa-rotate-left me-1"></i> Làm mới
                    </button>
                </div>
            </form>

            <form action="${pageContext.request.contextPath}/phieugiamgiaservlet" method="GET" style="display:flex;gap:15px;align-items:flex-end;margin-bottom:20px;">
                <div class="form-group">
                    <label>Tìm kiếm</label>
                    <input type="text" name="keyword" value="${keyword}" placeholder="Mã hoặc tên phiếu giảm giá..." style="width:280px;">
                </div>
                <button type="submit" class="btn-action btn-them"><i class="fa-solid fa-magnifying-glass"></i> Tìm</button>
            </form>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Mã</th>
                            <th>Tên phiếu</th>
                            <th>Loại</th>
                            <th>Giá trị</th>
                            <th>Đơn tối thiểu</th>
                            <th>Số lượng</th>
                            <th>Ngày bắt đầu</th>
                            <th>Ngày kết thúc</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty listPhieu}">
                            <tr><td colspan="10" style="color:#7d7a70; padding:35px !important;">Không có mã giảm giá nào được tìm thấy.</td></tr>
                        </c:if>
                        <c:forEach items="${listPhieu}" var="v">
                            <tr>
                                <td><strong style="color:#8a6a1c;">${v.magiamgia}</strong></td>
                                <td>${v.tenphieu}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${v.loaigiam == 0}">Giảm tiền</c:when>
                                        <c:otherwise>Giảm %</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${v.loaigiam == 0}">${v.giatrigiam}đ</c:when>
                                        <c:otherwise>${v.giatrigiam} %</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${v.dieukien}đ</td>
                                <td>${v.soluong}</td>
                                <td>${v.ngaybatdau}</td>
                                <td>${v.ngayketthuc}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${v.trangthai}"><span class="badge-active">Hoạt động</span></c:when>
                                        <c:otherwise><span class="badge-stop">Ngừng</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div style="display:flex; justify-content:center; align-items:center; gap:6px;">
                                        <a href="${pageContext.request.contextPath}/phieugiamgiaservlet?action=edit&id=${v.magiamgia}">
                                            <button type="button" class="btn-action btn-sua" style="padding:7px 12px; font-size:11px;">Sửa</button>
                                        </a>
                                        <c:choose>
                                            <c:when test="${v.trangthai}">
                                                <a href="${pageContext.request.contextPath}/phieugiamgiaservlet?action=khoa&id=${v.magiamgia}&keyword=${keyword}" onclick="return confirm('Khóa (ngừng) phiếu giảm giá này? Có thể mở khóa lại sau.');">
                                                    <button type="button" class="btn-action" style="padding:7px 12px; font-size:11px; background:rgba(239, 68, 68, 0.1); color:#b3261e; border:1px solid rgba(239, 68, 68, 0.4);">Khóa</button>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/phieugiamgiaservlet?action=mokhoa&id=${v.magiamgia}&keyword=${keyword}">
                                                    <button type="button" class="btn-action" style="padding:7px 12px; font-size:11px; background:rgba(34, 197, 94, 0.1); color:#2f7d4a; border:1px solid rgba(34, 197, 94, 0.4);">Mở khóa</button>
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

        <script>
            function capNhatDonViGiam() {
                var loai = document.getElementById('loaigiamSelect').value;
                var donVi = document.getElementById('donViGiam');
                var input = document.getElementById('giatrigiamInput');

                if (loai === '1') {
                    donVi.textContent = '%';
                    input.setAttribute('max', '100');
                } else {
                    donVi.textContent = 'đ';
                    input.removeAttribute('max');
                }
            }
            capNhatDonViGiam();
        </script>
    </body>
</html>