<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>LODI - Quản Lý Vận Chuyển</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/image/favicon.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <style>
            * { margin:0; padding:0; box-sizing:border-box; font-family:'Inter', sans-serif; }
            body { background:#e7e2d2; color:#1c1b18; -webkit-font-smoothing:antialiased; }

            .top-header {
                width:100%; height:64px;
                background:#0d0d0e;
                display:flex; justify-content:space-between; align-items:center;
                padding:0 32px; box-shadow:0 4px 15px rgba(40,30,0,0.14);
                position:fixed; top:0; left:0; z-index:1001;
            ;border-bottom:2px solid #c9a24b}
            .top-header .header-title { font-family:'Playfair Display', serif; font-size:22px; font-weight:700; color:#ead594; letter-spacing:4px; text-transform:uppercase; }
            
            /* Khu vực xin chào thiết kế chuẩn theo hình mẫu 2 */
            .top-header .welcome-box {
                background:#f2efe4; color:#8a6a1c; font-size:11.5px; font-weight:700; padding:8px 20px;
                border-radius:20px; border:1px solid rgba(201,162,75,0.3); letter-spacing:0.5px; text-transform:uppercase; 
                display:flex; align-items:center; gap:8px; box-shadow:0 2px 8px rgba(40,30,0,0.14);
            }
            .top-header .welcome-box i { font-size:13px; color:#8a6a1c; }

            .wrapper { display:flex; min-height:100vh; padding-top:64px; }
            .sidebar {
                width:275px; background:#0d0d0e; border-right:2px solid #c9a24b;
                flex-shrink:0; position:fixed; top:64px; left:0; height:calc(100vh - 64px); overflow-y:auto;
                display:flex; flex-direction:column; align-items:center; box-shadow:5px 0 25px rgba(0,0,0,0.5);
            }
            .sidebar::-webkit-scrollbar { width:5px; }
            .sidebar::-webkit-scrollbar-thumb { background:#0d0d0e; border-radius:10px; }
            .sidebar::-webkit-scrollbar-thumb:hover { background:#c9a24b; }

            .sidebar-logo { width:100%; display:flex; justify-content:center; align-items:center; padding:22px 15px; border-bottom:2px solid #c9a24b; margin-bottom:15px; background:rgba(255,255,255,0.01); }
            .sidebar-logo img { max-width:130px; filter:drop-shadow(0px 0px 10px rgba(201,162,75,0.4)); transition:transform 0.3s ease; }
            .sidebar-logo img:hover { transform:scale(1.05); }

            /* Thiết kế từng mục menu giống mẫu ảnh 1 */
            .sidebar ul { list-style:none; width:100%; padding:0 12px; }
            .sidebar ul li { margin-bottom:8px; }
            
            .sidebar ul li a { 
                display:flex; align-items:center; justify-content:space-between; 
                padding:12px 16px; text-decoration:none; font-size:13px; font-weight:500; 
                color:#cccccc; white-space:nowrap; border-radius:12px; 
                border:1px solid rgba(201,162,75,0.2); background-color:#0d0d0e; 
                transition:all 0.25s ease-in-out; cursor:pointer; 
            }
            .sidebar ul li a .menu-left { display:flex; align-items:center; gap:14px; }
            .sidebar ul li a .icon { width:22px; text-align:center; font-size:15px; color:#c9a24b; }
            .sidebar ul li a .count-badge { background:#c9a24b; color:#0c0c0c; font-size:11px; font-weight:800; padding:2px 8px; border-radius:10px; }

            .sidebar ul li a:hover { 
                background:rgba(201,162,75,0.08); 
                color:#ffffff; 
                border-color:rgba(201,162,75,0.5); 
            }
            .sidebar ul li a:hover .icon { color:#c9a24b; }
            
            /* Trạng thái Active nổi bật tuyệt đối theo mẫu ảnh 1 */
            .sidebar ul li.active a { 
                background:linear-gradient(135deg, rgba(201,162,75,0.2), rgba(201,162,75,0.05)); 
                color:#ffffff; font-weight:600; border:1px solid #c9a24b; 
                box-shadow:0 0 15px rgba(201,162,75,0.25); 
            }
            .sidebar ul li.active a .icon { color:#c9a24b; }

            .content { flex:1; margin-left:275px; padding:32px; background:#090a0c; }
            .main-container-box { border:1px solid #c9a24b; border-radius:12px; padding:28px; margin-bottom:26px; }
            .main-container-box h2 {
                color:#8a6a1c; font-family:'Playfair Display', serif; font-size:22px;
                border-bottom:1px solid rgba(201,162,75,0.3); padding-bottom:14px; margin-bottom:20px; text-transform:uppercase;
                display:flex; align-items:center; gap:10px;
            }
            .section { display:none; }
            .section.active { display:block; }

            .table-container { background:#f2efe4; border:1px solid rgba(201,162,75,0.5); border-radius:8px; overflow:hidden; overflow-x:auto; }
            table { width:100%; border-collapse:collapse; }
            th { background:#0d0d0e; color:#c9a24b; padding:14px 12px; font-size:11.5px; text-transform:uppercase; letter-spacing:.5px; border-bottom:1px solid rgba(201,162,75,0.3); border-right:1px solid rgba(201,162,75,0.15); white-space:nowrap; }
            td { padding:14px 12px; color:#1c1b18; font-size:13.5px; text-align:center; border-bottom:1px solid rgba(201,162,75,0.1); border-right:1px solid rgba(201,162,75,0.1); }
            tbody tr:hover td { background:rgba(201,162,75,0.05); }
            .empty-row td { color:#7d7a70; padding:26px; }
            .txt-gold { color:#8a6a1c; font-weight:700; }
            .diachi-cell { max-width:220px; white-space:normal; text-align:left; }
            .badge-size { padding:3px 9px; border-radius:8px; font-size:10.5px; background:rgba(201,162,75,0.1); color:#8a6a1c; border:1px solid rgba(201,162,75,0.3); }

            .btn-save { background:linear-gradient(135deg, #f9f5e8 0%, #c9a24b 50%, #8a6a1c 100%); color:#0c0c0c; border:none; padding:8px 14px; border-radius:8px; cursor:pointer; font-weight:700; font-size:12.5px; }
            .btn-outline { background:transparent; color:#a12a2a; border:1px solid #a12a2a; padding:8px 14px; border-radius:8px; cursor:pointer; font-weight:700; font-size:12.5px; }
            .btn-danger { background:linear-gradient(135deg, #ff8a8a 0%, #a12a2a 60%, #e14848 100%); color:#1a0a0a; border:none; padding:8px 14px; border-radius:8px; cursor:pointer; font-weight:700; font-size:12.5px; }
            .action-cell { display:flex; flex-direction:column; align-items:center; gap:6px; }

            .color-status-1 { color:#1f5fa8; }
            .color-status-2 { color:#2f7d4a; }
            .color-status-3 { color:#a12a2a; }
            .color-status-4 { color:#c084fc; }

            .alert-box { margin-bottom:18px; padding:13px 16px; border-radius:8px; font-weight:600; font-size:13.5px; }
            .alert-success { background:rgba(74,222,128,0.1); border:1px solid #2f7d4a; color:#2f7d4a; }
            .alert-error { background:rgba(248,113,113,0.1); border:1px solid #a12a2a; color:#a12a2a; }

            @media (max-width: 900px) {
                .sidebar { width:220px; }
                .content { margin-left:220px; padding:20px; }
                table { display:block; overflow-x:auto; white-space:nowrap; }
            }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>
    <body>
        <div class="top-header">
            <div class="header-title">Quản Lý Vận Chuyển</div>
            <div class="welcome-box"><i class="fa-solid fa-crown"></i> XIN CHÀO, ${vanchuyen.hoten} — KHU VỰC: ${vanchuyen.khuvuc}</div>
        </div>
        <div class="wrapper">
            <div class="sidebar">
                <div class="sidebar-logo"><img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo"></div>
                <ul>
                    <li class="active">
                        <a onclick="showSection('doncangiao')">
                            <div class="menu-left"><span class="icon"><i class="fa-solid fa-boxes-packing"></i></span><span>Đơn cần giao</span></div>
                            <span class="count-badge">${fn:length(donCanGiao)}</span>
                        </a>
                    </li>
                    <li>
                        <a onclick="showSection('doncuatoi')">
                            <div class="menu-left"><span class="icon"><i class="fa-solid fa-truck-fast"></i></span><span>Đơn của tôi</span></div>
                            <span class="count-badge">${fn:length(donCuaToi)}</span>
                        </a>
                    </li>
                    <li>
                        <a onclick="showSection('lichsu')">
                            <div class="menu-left"><span class="icon"><i class="fa-solid fa-clock-rotate-left"></i></span><span>Lịch sử giao hàng</span></div>
                        </a>
                    </li>
                    <li>
                        <a onclick="showSection('luong')">
                            <div class="menu-left"><span class="icon"><i class="fa-solid fa-sack-dollar"></i></span><span>Lương của tôi</span></div>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/dangxuatvanchuyensvl">
                            <div class="menu-left"><span class="icon"><i class="fa-solid fa-arrow-right-from-bracket"></i></span><span>Đăng xuất</span></div>
                        </a>
                    </li>
                </ul>
            </div>
            <div class="content">

                <c:if test="${not empty message}">
                    <div id="toast-message" class="alert-box alert-success"><i class="fa-solid fa-check-circle"></i> ${message}</div>
                    <script>setTimeout(function(){ document.getElementById('toast-message').style.display='none'; }, 3000);</script>
                    <% session.removeAttribute("message"); %>
                </c:if>
                <c:if test="${not empty error}">
                    <div id="toast-error" class="alert-box alert-error"><i class="fa-solid fa-circle-exclamation"></i> ${error}</div>
                    <script>setTimeout(function(){ document.getElementById('toast-error').style.display='none'; }, 4000);</script>
                    <% session.removeAttribute("error"); %>
                </c:if>

                <!-- ĐƠN CẦN GIAO (chưa ai nhận) -->
                <div class="section active" id="sec-doncangiao">
                    <div class="main-container-box">
                        <h2><i class="fa-solid fa-boxes-packing"></i> Đơn hàng đang chờ giao tại ${vanchuyen.khuvuc} (chưa có người nhận)</h2>
                        <p style="color:#7d7a70;font-size:12.5px;margin:-10px 0 16px;">Chỉ hiển thị đơn hàng thuộc tỉnh/thành bạn đang phụ trách. Nếu khu vực chưa đúng, vui lòng liên hệ admin để được cập nhật lại.</p>
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr><th>Mã đơn</th><th>Sản phẩm</th><th>Size</th><th>SL</th><th>Người nhận</th><th>SĐT</th><th>Tỉnh/Thành</th><th>Địa chỉ giao</th><th>Ngày đặt</th><th>Hành động</th></tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty donCanGiao}">
                                        <tr class="empty-row"><td colspan="10">Hiện không có đơn hàng nào đang chờ giao tại khu vực của bạn.</td></tr>
                                    </c:if>
                                    <c:forEach var="order" items="${donCanGiao}">
                                        <tr>
                                            <td><strong class="txt-gold">#ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}</strong></td>
                                            <td>${order.tensanpham}</td>
                                            <td><span class="badge-size">${order.kichco}</span></td>
                                            <td>${order.soluong}</td>
                                            <td>${order.hotenNguoiNhan}</td>
                                            <td>${order.sdtNguoiNhan}</td>
                                            <td><span class="badge-size">${order.tinhThanhNguoiNhan}</span></td>
                                            <td class="diachi-cell">${order.diaChiNguoiNhan}</td>
                                            <td><fmt:formatDate value="${order.ngaymua}" pattern="dd/MM/yyyy HH:mm"/></td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/quanlyvanchuyenservlet" method="POST">
                                                    <input type="hidden" name="matrangthai" value="${order.matrangthai}">
                                                    <button type="submit" name="action" value="nhandon" class="btn-save">Nhận đơn</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- ĐƠN CỦA TÔI (đang giao) -->
                <div class="section" id="sec-doncuatoi">
                    <div class="main-container-box">
                        <h2><i class="fa-solid fa-truck-fast"></i> Đơn hàng tôi đang giao</h2>
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr><th>Mã đơn</th><th>Sản phẩm</th><th>Size</th><th>SL</th><th>Người nhận</th><th>SĐT</th><th>Tỉnh/Thành</th><th>Địa chỉ giao</th><th>Ghi chú</th><th>Hành động</th></tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty donCuaToi}">
                                        <tr class="empty-row"><td colspan="10">Bạn chưa nhận đơn hàng nào. Vào mục "Đơn cần giao" để bắt đơn.</td></tr>
                                    </c:if>
                                    <c:forEach var="order" items="${donCuaToi}">
                                        <tr>
                                            <td><strong class="txt-gold">#ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}</strong></td>
                                            <td>${order.tensanpham}</td>
                                            <td><span class="badge-size">${order.kichco}</span></td>
                                            <td>${order.soluong}</td>
                                            <td>${order.hotenNguoiNhan}</td>
                                            <td>${order.sdtNguoiNhan}</td>
                                            <td><span class="badge-size">${order.tinhThanhNguoiNhan}</span></td>
                                            <td class="diachi-cell">${order.diaChiNguoiNhan}</td>
                                            <td class="diachi-cell">${order.ghiChuDonHang}</td>
                                            <td>
                                                <div class="action-cell">
                                                    <c:choose>
                                                        <c:when test="${order.tinhtrang == 1}">
                                                            <form action="${pageContext.request.contextPath}/quanlyvanchuyenservlet" method="POST">
                                                                <input type="hidden" name="matrangthai" value="${order.matrangthai}">
                                                                <button type="submit" name="action" value="danggiao" class="btn-save" onclick="return confirm('Xác nhận ĐÃ GIAO tới nơi sản phẩm này của đơn #ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}? Đơn sẽ chuyển sang chờ khách hàng xác nhận.')">Đã giao</button>
                                                            </form>
                                                            <form action="${pageContext.request.contextPath}/quanlyvanchuyenservlet" method="POST">
                                                                <input type="hidden" name="matrangthai" value="${order.matrangthai}">
                                                                <button type="submit" name="action" value="giaothatbai" class="btn-danger" onclick="return confirm('Xác nhận giao thất bại / khách từ chối nhận sản phẩm này của đơn #ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}?')">Giao thất bại</button>
                                                            </form>
                                                            <form action="${pageContext.request.contextPath}/quanlyvanchuyenservlet" method="POST">
                                                                <input type="hidden" name="matrangthai" value="${order.matrangthai}">
                                                                <button type="submit" name="action" value="tralaidon" class="btn-outline">Trả lại đơn</button>
                                                            </form>
                                                        </c:when>
                                                        <c:when test="${order.tinhtrang == 4}">
                                                            <div class="color-status-4" style="font-size:11.5px; font-weight:600;">Đã giao - chờ khách hàng xác nhận đã nhận</div>
                                                        </c:when>
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

                <!-- LỊCH SỬ -->
                <div class="section" id="sec-lichsu">
                    <div class="main-container-box">
                        <h2><i class="fa-solid fa-clock-rotate-left"></i> Lịch sử giao hàng</h2>
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr><th>Mã đơn</th><th>Sản phẩm</th><th>Size</th><th>SL</th><th>Người nhận</th><th>Tỉnh/Thành</th><th>Địa chỉ</th><th>Ngày đặt</th><th>Kết quả</th></tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty lichSu}">
                                        <tr class="empty-row"><td colspan="9">Chưa có lịch sử giao hàng.</td></tr>
                                    </c:if>
                                    <c:forEach var="order" items="${lichSu}">
                                        <tr>
                                            <td><strong class="txt-gold">#ORD-${order.madonhang > 0 ? order.madonhang : order.matrangthai}</strong></td>
                                            <td>${order.tensanpham}</td>
                                            <td><span class="badge-size">${order.kichco}</span></td>
                                            <td>${order.soluong}</td>
                                            <td>${order.hotenNguoiNhan}</td>
                                            <td><span class="badge-size">${order.tinhThanhNguoiNhan}</span></td>
                                            <td class="diachi-cell">${order.diaChiNguoiNhan}</td>
                                            <td><fmt:formatDate value="${order.ngaymua}" pattern="dd/MM/yyyy HH:mm"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${order.tinhtrang == 2}"><span class="color-status-2"><i class="fa-solid fa-circle-check"></i> Đã giao thành công</span></c:when>
                                                    <c:otherwise><span class="color-status-3"><i class="fa-solid fa-circle-xmark"></i> Giao thất bại / Hủy</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- LƯƠNG CỦA TÔI -->
                <div class="section" id="sec-luong">
                    <div class="main-container-box">
                        <h2><i class="fa-solid fa-sack-dollar"></i> Lương tháng ${thangHienTai}/${namHienTai} (tạm tính)</h2>
                        <p style="color:#7d7a70;font-size:12.5px;margin:-10px 0 20px;">
                            Lương cố định <strong class="txt-gold"><fmt:formatNumber value="${luongCoBan}" pattern="#,###"/>đ</strong>/tháng
                            + hoa hồng <strong class="txt-gold">${tiLeHoaHong}%</strong> trên giá trị mỗi đơn giao thành công.
                            Số liệu tháng hiện tại sẽ được admin chốt vào cuối tháng làm căn cứ thanh toán.
                        </p>
                        <div style="display:flex; gap:20px; flex-wrap:wrap; margin-bottom:10px;">
                            <div style="flex:1; min-width:180px; background:#ffffff; border:1px solid rgba(201,162,75,0.3); border-radius:8px; padding:18px 20px;">
                                <div style="color:#7d7a70; font-size:11.5px; text-transform:uppercase; margin-bottom:8px;">Số đơn hoàn thành</div>
                                <div style="font-size:22px; font-weight:700; color:#1c1b18;">${luongThangNay.sodonhoanthanh}</div>
                            </div>
                            <div style="flex:1; min-width:180px; background:#ffffff; border:1px solid rgba(201,162,75,0.3); border-radius:8px; padding:18px 20px;">
                                <div style="color:#7d7a70; font-size:11.5px; text-transform:uppercase; margin-bottom:8px;">Lương cố định</div>
                                <div style="font-size:22px; font-weight:700; color:#1c1b18;"><fmt:formatNumber value="${luongThangNay.luongcoban}" pattern="#,###"/>đ</div>
                            </div>
                            <div style="flex:1; min-width:180px; background:#ffffff; border:1px solid rgba(201,162,75,0.3); border-radius:8px; padding:18px 20px;">
                                <div style="color:#7d7a70; font-size:11.5px; text-transform:uppercase; margin-bottom:8px;">Hoa hồng (${tiLeHoaHong}%)</div>
                                <div style="font-size:22px; font-weight:700; color:#1c1b18;"><fmt:formatNumber value="${luongThangNay.hoahong}" pattern="#,###"/>đ</div>
                            </div>
                            <div style="flex:1; min-width:180px; background:rgba(201,162,75,0.08); border:1px solid #c9a24b; border-radius:8px; padding:18px 20px;">
                                <div style="color:#8a6a1c; font-size:11.5px; text-transform:uppercase; margin-bottom:8px;">Tổng lương tạm tính</div>
                                <div style="font-size:22px; font-weight:800; color:#8a6a1c;"><fmt:formatNumber value="${luongThangNay.tongluong}" pattern="#,###"/>đ</div>
                            </div>
                        </div>
                    </div>

                    <div class="main-container-box">
                        <h2><i class="fa-solid fa-clock-rotate-left"></i> Lịch sử lương đã chốt</h2>
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr><th>Tháng/Năm</th><th>Số đơn</th><th>Lương cố định</th><th>Hoa hồng</th><th>Tổng lương</th><th>Ngày chốt</th><th>Trạng thái</th></tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty lichSuLuong}">
                                        <tr class="empty-row"><td colspan="7">Chưa có tháng lương nào được chốt.</td></tr>
                                    </c:if>
                                    <c:forEach var="lvc" items="${lichSuLuong}">
                                        <tr>
                                            <td><strong class="txt-gold">${lvc.thang}/${lvc.nam}</strong></td>
                                            <td>${lvc.sodonhoanthanh}</td>
                                            <td><fmt:formatNumber value="${lvc.luongcoban}" pattern="#,###"/>đ</td>
                                            <td><fmt:formatNumber value="${lvc.hoahong}" pattern="#,###"/>đ</td>
                                            <td><strong class="txt-gold"><fmt:formatNumber value="${lvc.tongluong}" pattern="#,###"/>đ</strong></td>
                                            <td><fmt:formatDate value="${lvc.ngaychot}" pattern="dd/MM/yyyy"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${lvc.trangthai == 'Đã thanh toán'}"><span class="color-status-2"><i class="fa-solid fa-circle-check"></i> Đã thanh toán</span></c:when>
                                                    <c:otherwise><span class="color-status-1"><i class="fa-solid fa-hourglass-half"></i> Chưa thanh toán</span></c:otherwise>
                                                </c:choose>
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

        <script>
            function showSection(name) {
                document.querySelectorAll('.section').forEach(function (s) { s.classList.remove('active'); });
                document.getElementById('sec-' + name).classList.add('active');
                document.querySelectorAll('.sidebar ul li').forEach(function (li) { li.classList.remove('active'); });
                event.currentTarget.closest('li').classList.add('active');
            }
        </script>

    </body>
</html>