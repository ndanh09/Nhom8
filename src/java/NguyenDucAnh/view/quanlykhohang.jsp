<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý kho hàng - Lodi Authentic Store</title>
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
            .header { height:70px; display:flex; justify-content:space-between; align-items:center; padding:0 35px; background:#0d0d0e; border-bottom:2px solid #8a6a1c; box-shadow:0 4px 25px rgba(0, 0, 0, 0.4); position:fixed; top:0; left:0; width:100%; z-index:1000; }
            .header-title { font-family:'Playfair Display', serif; font-size:22px; font-weight:700; color:#ead594; letter-spacing:2px; text-transform:uppercase; text-shadow:none; }
            .header-admin { display:flex; align-items:center; gap:8px; background:rgba(15, 15, 15, 0.85); padding:8px 18px; border-radius:20px; color:#c9a24b; font-size:13px; font-weight:600; text-transform:uppercase; border:1px solid rgba(201,162,75,0.4); box-shadow:inset 0 1px 3px rgba(0,0,0,0.5); }
            .header-admin i { color:#c9a24b; font-size:15px; }

            /* WRAPPER & SIDEBAR */
            .wrapper { display:flex; margin-top:70px; min-height:calc(100vh - 70px); position:relative; z-index:1; width:100%; }
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

            /* MAIN CONTENT */
            .main-content { flex:1; margin-left:275px; padding:40px 45px; width:calc(100% - 275px); }
            .container-fluid-custom { display:flex; flex-direction:column; gap:35px; }

            /* CARD & TABLE SECTIONS */
            .card-body-custom { background-color:#f2efe4; border-radius:12px; padding:28px; border:2px solid #c9a24b; box-shadow:0 8px 30px rgba(40,30,0,0.14); }
            h2.page-heading { font-family:'Playfair Display', serif; font-size:32px; color:#1c1b18; letter-spacing:1px; text-transform:uppercase; margin-bottom:25px; background:none; -webkit-background-clip:text; -webkit-text-fill-color:#0d0d0e; }

            /* BUTTONS */
            .btn-primary { background:linear-gradient(135deg, #c9a24b, #8a6a1c) !important; color:#000000 !important; border:none !important; border-radius:8px; padding:11px 20px; font-size:12.5px; font-weight:700; text-transform:uppercase; letter-spacing:0.5px; transition:all 0.3s ease; box-shadow:0 4px 15px rgba(201,162,75,0.3); }
            .btn-primary:hover { opacity:0.9; transform:translateY(-2px); box-shadow:0 6px 20px rgba(201,162,75,0.4); }
            .btn-outline-warning { background:transparent !important; color:#8a6a1c !important; border:1px solid #c9a24b !important; border-radius:8px; font-weight:600; font-size:12px; padding:8px 14px; transition:all 0.2s ease; }
            .btn-outline-warning:hover { background:rgba(201,162,75,0.1) !important; color:#8a6a1c !important; }
            .btn-outline-danger { background:rgba(239, 68, 68, 0.1) !important; color:#b3261e !important; border:1px solid rgba(239, 68, 68, 0.4) !important; border-radius:8px; font-weight:600; font-size:12px; padding:8px 14px; transition:all 0.2s ease; }
            .btn-outline-danger:hover { background:#b3261e !important; color:#1c1b18 !important; box-shadow:0 0 10px rgba(239, 68, 68, 0.4); }
            .btn-outline-info { background:transparent !important; color:#1f5fa8 !important; border:1px solid rgba(47, 141, 228, 0.4) !important; border-radius:8px; font-weight:600; font-size:12.5px; padding:11px 18px; }
            .btn-outline-info:hover { background:rgba(47, 141, 228, 0.15) !important; color:#1f5fa8 !important; }
            .btn-outline-secondary { background:#e4dfd0 !important; border:1px solid rgba(0,0,0,.10) !important; color:#1c1b18 !important; border-radius:8px; font-size:13px; }

            /* TABLES */
            .table-responsive-custom { border-radius:12px; overflow:hidden; border:2px solid #c9a24b; background-color:#f2efe4; box-shadow:0 8px 30px rgba(40,30,0,0.14); margin-top:20px; }
            table { width:100%; border-collapse:collapse; margin:0 !important; }
            table th { background-color:#0d0d0e !important; color:#c9a24b !important; padding:15px 18px !important; text-transform:uppercase; font-size:11.5px !important; letter-spacing:0.5px; border-bottom:2px solid #c9a24b !important; border-right:1px solid rgba(201,162,75,0.2) !important; text-align:center; }
            table th:last-child { border-right:none !important; }
            table td { background-color:transparent !important; color:#1c1b18 !important; padding:15px 18px !important; text-align:center; vertical-align:middle; font-size:13px; border-bottom:1px solid rgba(201,162,75,0.2) !important; border-right:1px solid rgba(201,162,75,0.15) !important; white-space:nowrap; }
            table td:last-child { border-right:none !important; }
            table tr:last-child td { border-bottom:none !important; }
            table tbody tr:hover { background-color:rgba(201,162,75,0.03) !important; }

            /* BADGES */
            .badge { padding:5px 12px; border-radius:20px; font-size:11px; font-weight:700; text-transform:uppercase; }
            .badge.bg-success { background:rgba(34, 197, 94, 0.1) !important; color:#2f7d4a !important; border:1px solid rgba(34, 197, 94, 0.4); }
            .badge.bg-danger { background:rgba(239, 68, 68, 0.1) !important; color:#b3261e !important; border:1px solid rgba(239, 68, 68, 0.4); }

            /* MODALS */
            .modal-content { background-color:#f2efe4; border:2px solid #c9a24b; border-radius:12px; box-shadow:0 10px 40px rgba(40,30,0,0.14); color:#1c1b18; }
            .modal-header { background:#0d0d0e; border-bottom:2px solid #c9a24b; padding:18px 25px; }
            .modal-title { color:#8a6a1c; font-family:'Playfair Display', serif; font-size:20px; font-weight:700; text-transform:uppercase; letter-spacing:1px; }
            .modal-body { background:#f2efe4; padding:25px; }
            .modal-footer { background:#f2efe4; border-top:1px solid rgba(201,162,75,0.3); padding:15px 25px; }
            .btn-close { filter:invert(1) grayscale(100%) brightness(200%); }

            /* FORM CONTROLS */
            label, .form-label { color:#5b584f !important; font-weight:600 !important; font-size:12px !important; text-transform:uppercase !important; letter-spacing:0.5px !important; margin-bottom:6px !important; display:block; }
            .form-control, .form-select { height:44px; padding:0 15px; border-radius:8px !important; border:1px solid rgba(201,162,75,0.4) !important; background-color:#ebe6d6 !important; color:#1c1b18 !important; font-size:13.5px !important; font-family:'Inter', sans-serif !important; }
            .form-control:focus, .form-select:focus { outline:none; border-color:#c9a24b !important; box-shadow:0 0 0 3px rgba(201,162,75,0.15) !important; background-color:#ebe6d6 !important; }
            .form-control::placeholder { color:#555; }
            input:disabled, select:disabled { background:#0d0d0d !important; color:#666 !important; border-color:rgba(201,162,75,0.15) !important; }
            .form-text { color:#7d7a70 !important; font-size:12px; margin-top:4px; }
            
            /* SEARCH BOX */
            .search-input-group { background:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; overflow:hidden; padding:3px; }
            .search-input-group input { border:none !important; background:transparent !important; box-shadow:none !important; color:#1c1b18 !important; }

            @media(max-width: 992px) {
                .main-content { margin-left:0; width:100%; padding:20px; }
                .sidebar { display:none; }
            }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>
    <body>

        <!-- HEADER -->
        <div class="header">
            <div class="header-title">Quản lý kho hàng</div>
            <div class="header-admin">
                <i class="fa-solid fa-crown"></i>
                <span>Xin chào, Admin</span>
            </div>
        </div>

        <div class="wrapper">
            <!-- SIDEBAR -->
            <div class="sidebar">
                <div class="sidebar-logo">
                    <img src="${pageContext.request.contextPath}/image/logo.png" alt="Lodi Store">
                </div>
                <ul>                    
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/qtv"><i class="fa-solid fa-user-shield icon"></i><span>Quản trị viên</span></a></li></c:if>
                    <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlyyeuthichtaikhoan"><i class="fa-solid fa-heart-circle-check icon"></i><span>Yêu thích theo tài khoản</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/nguoibanservlet"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlytopyeuthich"><i class="fa-solid fa-heart icon"></i><span>Top sản phẩm yêu thích</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlykhachhangservlet"><i class="fa-solid fa-users icon"></i><span>Quản lý đơn hàng</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li class="active"><a href="${pageContext.request.contextPath}/quanlykhohangservlet"><i class="fa-solid fa-warehouse icon"></i><span>Quản lý kho hàng</span></a></li></c:if>
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

            <!-- MAIN CONTENT CONTAINER -->
            <div class="main-content">
                <div class="container-fluid-custom">
                    
                    <div class="card-body-custom">
                        <h2 class="page-heading">Kho hàng</h2>

                        <c:if test="${not empty success || not empty error}">
                            <div style="padding:14px 20px; margin-bottom:20px; border-radius:8px; font-size:14px; display:flex; align-items:center; gap:10px; ${not empty success ? 'background:rgba(46, 204, 113, 0.05); color:#2f7d4a; border:1px solid rgba(46, 204, 113, 0.4);' : 'background:rgba(231, 76, 60, 0.05); color:#b3261e; border:1px solid rgba(231, 76, 60, 0.4);'}">
                                <i class="${not empty success ? 'fa-solid fa-circle-check' : 'fa-solid fa-circle-exclamation'}"></i>
                                <span>${not empty success ? success : error}</span>
                            </div>
                            <c:remove var="success" scope="session"/>
                            <c:remove var="error" scope="session"/>
                        </c:if>

                        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
                            <div class="d-flex flex-wrap gap-2">
                                <button type="button" class="btn btn-outline-info" id="btnBatchUpdate" disabled data-bs-toggle="modal" data-bs-target="#batchModal">
                                    <i class="fa-solid fa-pen-to-square me-1"></i> Cập nhật hàng loạt (<span id="soLuongDaChon">0</span>)
                                </button>
                            </div>

                            <form action="${pageContext.request.contextPath}/quanlykhohangservlet" method="get" class="d-flex align-items-center" style="min-width:320px;">
                                <input type="hidden" name="action" value="search">
                                <div class="input-group search-input-group w-100">
                                    <input type="text" name="keyword" class="form-control" placeholder="Tìm theo Mã SP, Vị trí, NCC..." value="${keyword}">
                                    <button type="submit" class="btn btn-outline-warning border-0"><i class="fa-solid fa-magnifying-glass"></i></button>
                                </div>
                                <c:if test="${not empty keyword}">
                                    <a href="${pageContext.request.contextPath}/quanlykhohangservlet" class="btn btn-outline-secondary ms-2 text-nowrap d-flex align-items-center">Xóa lọc</a>
                                </c:if>
                            </form>
                        </div>

                        <div class="table-responsive-custom">
                            <table>
                                <thead>
                                    <tr>
                                        <th style="width:40px;"><input type="checkbox" id="chonTatCa" class="form-check-input" style="cursor:pointer;"></th>
                                        <th>ID</th>
                                        <th>Mã SP</th>
                                        <th>SL Nhập</th>
                                        <th>SL Tồn</th>
                                        <th>Vị trí</th>
                                        <th>Ngày nhập</th>
                                        <th>Nhà cung cấp</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty listKho}">
                                        <tr><td colspan="9" class="text-center" style="color:#7d7a70; padding:30px !important;">Không tìm thấy sản phẩm nào phù hợp trong kho.</td></tr>
                                    </c:if>
                                    <c:forEach var="item" items="${listKho}">
                                        <tr>
                                            <td><input type="checkbox" class="chonDong form-check-input" value="${item.makho}" onchange="capNhatSoLuongDaChon()" style="cursor:pointer;"></td>
                                            <td>${item.makho}</td>
                                            <td><strong style="color:#8a6a1c;">${item.masanpham}</strong></td>
                                            <td>${item.soluongnhap}</td>
                                            <td><span class="badge ${item.soluongton < 10 ? 'bg-danger' : 'bg-success'}">${item.soluongton}</span></td>
                                            <td>${item.vitri}</td>
                                            <td>${item.ngaynhapkho}</td>
                                            <td>${item.nhacungcap}</td>
                                            <td>
                                                <div style="display:flex; justify-content:center; align-items:center; gap:6px;">
                                                    <a href="quanlykhohangservlet?action=edit&id=${item.makho}" class="btn btn-sm btn-outline-warning">Cập nhật</a>                                                    
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
        </div>

        <!-- MODAL CẬP NHẬT KHO (không còn chức năng thêm) -->
        <div class="modal fade" id="khoModal" tabindex="-1">
            <div class="modal-dialog">
                <form action="quanlykhohangservlet" method="post" class="modal-content">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="makho" value="${kho.makho}">

                    <div class="modal-header">
                        <h5 class="modal-title">Cập nhật kho hàng</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label>Mã sản phẩm</label>
                            <input type="text" class="form-control" name="masanpham" value="${kho.masanpham}" placeholder="Nhập mã sản phẩm" required>
                        </div>
                        <div class="row g-2 mb-3">
                            <div class="col-md-6">
                                <label>SL nhập thêm</label>
                                <input type="number" class="form-control" name="soluongnhap" min="0" placeholder="Số lượng nhập bổ sung">
                            </div>
                            <div class="col-md-6">
                                <label>SL Tồn hiện tại</label>
                                <input type="text" class="form-control" value="${kho.soluongton}" disabled>
                                <div class="form-text">Tự động cộng dồn số lượng</div>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label>Vị trí</label>
                            <input type="text" class="form-control" name="vitri" value="${kho.vitri}" placeholder="Ví dụ: Kệ A1, Kho chính">
                        </div>
                        <div class="mb-3">
                            <label>Nhà cung cấp</label>
                            <input type="text" class="form-control" name="nhacungcap" value="${kho.nhacungcap}" placeholder="Tên nhà cung cấp">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL CẬP NHẬT HÀNG LOẠT -->
        <div class="modal fade" id="batchModal" tabindex="-1">
            <div class="modal-dialog">
                <form action="${pageContext.request.contextPath}/quanlykhohangservlet" method="post" class="modal-content" id="batchForm">
                    <input type="hidden" name="action" value="batchupdate">
                    <div id="batchIdsContainer"></div>

                    <div class="modal-header">
                        <h5 class="modal-title">Cập nhật nhanh (<span id="batchSoLuong">0</span> mục)</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-text mb-3" style="color:#7d7a70 !important;">Chỉ cập nhật Vị trí và Nhà cung cấp cho các dòng được chọn. Ô nào để trống sẽ được giữ nguyên giá trị cũ.</div>
                        <div class="mb-3">
                            <label>Vị trí mới</label>
                            <input type="text" class="form-control" name="vitri" placeholder="Ví dụ: Kệ B2">
                        </div>
                        <div class="mb-3">
                            <label>Nhà cung cấp mới</label>
                            <input type="text" class="form-control" name="nhacungcap" placeholder="Tên nhà cung cấp mới">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Áp dụng</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function capNhatSoLuongDaChon() {
                var checked = document.querySelectorAll(".chonDong:checked");
                var soLuong = checked.length;
                document.getElementById("soLuongDaChon").innerText = soLuong;
                document.getElementById("batchSoLuong").innerText = soLuong;
                document.getElementById("btnBatchUpdate").disabled = (soLuong === 0);
            }

            document.getElementById("chonTatCa").addEventListener("change", function () {
                var isChecked = this.checked;
                document.querySelectorAll(".chonDong").forEach(function (cb) {
                    cb.checked = isChecked;
                });
                capNhatSoLuongDaChon();
            });

            document.getElementById("batchForm").addEventListener("submit", function (e) {
                var checked = document.querySelectorAll(".chonDong:checked");
                if (checked.length === 0) {
                    e.preventDefault();
                    return;
                }
                var container = document.getElementById("batchIdsContainer");
                container.innerHTML = "";
                checked.forEach(function (cb) {
                    var input = document.createElement("input");
                    input.type = "hidden";
                    input.name = "ids";
                    input.value = cb.value;
                    container.appendChild(input);
                });
            });
        </script>
        <c:if test="${kho != null}">
            <script>
                window.onload = function () {
                    var modal = new bootstrap.Modal(document.getElementById("khoModal"));
                    modal.show();
                }
            </script>
        </c:if>
    </body>
</html>