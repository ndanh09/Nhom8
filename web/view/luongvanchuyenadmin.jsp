<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý lương thưởng vận chuyển</title>
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

            .quy-tac-box { background:rgba(201,162,75,0.06); border:1px solid rgba(201,162,75,0.4); border-radius:12px; padding:18px 22px; margin-bottom:26px; font-size:13px; color:#1c1b18; line-height:1.7; box-shadow:0 4px 15px rgba(40,30,0,0.14); }
            .quy-tac-box strong { color:#8a6a1c; }

            form.filter-form { background-color:#f2efe4; padding:22px 28px; border:2px solid #c9a24b; border-radius:12px; display:flex; align-items:flex-end; gap:20px; margin-bottom:30px; flex-wrap:wrap; box-shadow:0 8px 30px rgba(40,30,0,0.14); }
            .form-group { display:flex; flex-direction:column; gap:6px; }
            form label { font-weight:600; color:#7d7a70; font-size:12px; text-transform:uppercase; letter-spacing:0.5px; }
            form select, form input { padding:11px 15px; background-color:#ebe6d6; border:1px solid rgba(201,162,75,0.4); border-radius:8px; color:#1c1b18; outline:none; font-family:'Inter', sans-serif; font-size:13.5px; transition:all 0.3s ease; }
            form select:focus, form input:focus { border-color:#c9a24b; box-shadow:0 0 0 3px rgba(201,162,75,0.15); background-color:#ebe6d6; }
            
            .btn-action { padding:12px 24px; font-size:12.5px; font-weight:700; text-transform:uppercase; cursor:pointer; border-radius:8px; transition:all 0.3s ease; letter-spacing:0.5px; border:none; }
            .btn-them { background:linear-gradient(135deg, #c9a24b, #8a6a1c); color:#000000; box-shadow:0 4px 15px rgba(201,162,75,0.3); }
            .btn-them:hover { opacity:0.9; transform:translateY(-2px); box-shadow:0 6px 20px rgba(201,162,75,0.4); }

            .btn-chot { background:rgba(56, 189, 248, 0.1); border:1px solid rgba(56, 189, 248, 0.4); color:#1f5fa8; padding:6px 14px; font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; transition:all 0.2s ease; }
            .btn-chot:hover { background:#38bdf8; color:#1c1b18; box-shadow:0 0 10px rgba(56, 189, 248, 0.4); }

            .btn-thanhtoan { background:rgba(34, 197, 94, 0.1); border:1px solid rgba(34, 197, 94, 0.4); color:#2f7d4a; padding:6px 14px; font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; transition:all 0.2s ease; }
            .btn-thanhtoan:hover { background:#2f7d4a; color:#1c1b18; box-shadow:0 0 10px rgba(34, 197, 94, 0.4); }

            .badge-chuachot { color:#7d7a70; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(157, 157, 157, 0.1); border:1px solid rgba(157, 157, 157, 0.4); padding:4px 10px; border-radius:20px; }
            .badge-chuathanhtoan { color:#f59e0b; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(245, 158, 11, 0.1); border:1px solid rgba(245, 158, 11, 0.4); padding:4px 10px; border-radius:20px; }
            .badge-dathanhtoan { color:#2f7d4a; font-weight:700; font-size:11px; text-transform:uppercase; background:rgba(34, 197, 94, 0.1); border:1px solid rgba(34, 197, 94, 0.4); padding:4px 10px; border-radius:20px; }

            .table-container { background-color:#f2efe4; border:2px solid #c9a24b; border-radius:12px; overflow:hidden; overflow-x:auto; box-shadow:0 8px 30px rgba(40,30,0,0.14); margin-bottom:40px; }
            table { width:100%; border-collapse:collapse; }
            table th { background-color:#0d0d0e; color:#c9a24b; padding:15px 18px; text-transform:uppercase; font-size:11.5px; letter-spacing:0.5px; border-bottom:2px solid #c9a24b; border-right:1px solid rgba(201,162,75,0.2); white-space:nowrap; }
            table th:last-child { border-right:none; }
            table td { padding:15px 18px; text-align:center; color:#1c1b18; font-size:13px; border-bottom:1px solid rgba(201,162,75,0.2); border-right:1px solid rgba(201,162,75,0.15); vertical-align:middle; white-space:nowrap; }
            table td:last-child { border-right:none; }
            table tr:last-child td { border-bottom:none; }
            table tr:hover { background-color:rgba(201,162,75,0.03); }

            .txt-gold { color:#8a6a1c; font-weight:700; }
            .empty-row td { color:#6f6f6f; padding:24px; font-style:italic; }
        
            /* Tab chuyển giữa lương vận chuyển / lương nhân viên bán hàng */
            .tabs { display:flex; gap:10px; margin-bottom:26px; flex-wrap:wrap; }
            .tabs a.tab { display:inline-flex; align-items:center; gap:8px; padding:11px 22px; border-radius:8px; text-decoration:none; font-size:12.5px; font-weight:700; text-transform:uppercase; letter-spacing:0.5px; color:#8a6a1c; background:#f2efe4; border:1px solid rgba(201,162,75,0.5); transition:all 0.25s ease; }
            .tabs a.tab:hover { background:rgba(201,162,75,0.15); }
            .tabs a.tab.active { background:linear-gradient(135deg, #c9a24b, #8a6a1c); color:#000000; border-color:#c9a24b; box-shadow:0 4px 15px rgba(201,162,75,0.3); }
            .warn-box { color:#8a5a00; background:rgba(245,158,11,0.08); border:1px solid rgba(245,158,11,0.5); padding:14px 20px; margin-bottom:26px; font-size:13.5px; line-height:1.7; border-radius:8px; }
            .sel-nv { min-width:230px; }
            .btn-luu { background:rgba(201,162,75,0.12); border:1px solid rgba(201,162,75,0.5); color:#8a6a1c; padding:7px 16px; font-size:11.5px; font-weight:700; border-radius:6px; cursor:pointer; transition:all 0.2s ease; }
            .btn-luu:hover { background:#c9a24b; color:#000000; }
            .form-gan { display:flex; align-items:center; justify-content:center; gap:10px; margin:0; }
        
/* LODI: chữ trong ô chọn luôn đọc được, nền dịu mắt */
select option,select optgroup{background:#f2efe4;color:#1c1b18}
input,select,textarea{color:#1c1b18}
input::placeholder,textarea::placeholder{color:#8f8b7d}
</style>
    </head>
    <body>
        <div class="header">
            <div class="title-page">Quản lý nhân sự và lương</div>
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
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li class="active"><a href="${pageContext.request.contextPath}/luongvanchuyenadmin"><i class="fa-solid fa-sack-dollar icon"></i><span>Quản lý nhân sự và lương</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN'}"><li><a href="${pageContext.request.contextPath}/quanlybanner"><i class="fa-solid fa-images icon"></i><span>Quản lý banner</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'ADMIN' || sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/quanlydanhgia"><i class="fa-solid fa-star icon"></i><span>Quản lý đánh giá</span></a></li></c:if>
                <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
            </ul>
        </div>

        <div class="container">
            <h1>Lương Thưởng Vận Chuyển</h1>
            <div class="title-line"></div>

            <div class="tabs">
                <a class="tab active" href="${pageContext.request.contextPath}/luongvanchuyenadmin"><i class="fa-solid fa-truck-fast"></i> Người vận chuyển</a>
                <a class="tab" href="${pageContext.request.contextPath}/luongnhanvienbanhang"><i class="fa-solid fa-user-tie"></i> Nhân viên bán hàng</a>
            </div>

            <c:if test="${not empty sessionScope.success}">
                <div style="color:#2f7d4a; background:rgba(46, 204, 113, 0.05); border:1px solid rgba(46, 204, 113, 0.4); padding:14px 20px; margin-bottom:30px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-check"></i> <span>${sessionScope.success}</span>
                </div>
                <% session.removeAttribute("success"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div style="color:#b3261e; background:rgba(239, 68, 68, 0.05); border:1px solid rgba(239, 68, 68, 0.4); padding:14px 20px; margin-bottom:30px; font-size:14px; display:flex; align-items:center; gap:10px; border-radius:8px;">
                    <i class="fa-solid fa-circle-exclamation"></i> <span>${sessionScope.error}</span>
                </div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <div class="quy-tac-box">
                <i class="fa-solid fa-circle-info"></i> Quy tắc tính lương: <strong>lương cố định ${luongCoBan}đ / người / tháng</strong>,
                cộng thêm <strong>hoa hồng ${tiLeHoaHong}%</strong> trên giá trị mỗi đơn giao thành công trong tháng.
                Số liệu bên dưới được tính tự động theo tháng đã chọn; bấm <strong>"Chốt lương"</strong> để lưu cố định số liệu tháng đó
                (dùng làm căn cứ thanh toán), sau đó bấm <strong>"Đánh dấu đã thanh toán"</strong> khi đã trả lương xong.
            </div>

            <form action="${pageContext.request.contextPath}/luongvanchuyenadmin" method="GET" class="filter-form">
                <div class="form-group">
                    <label>Tháng:</label>
                    <select name="thang">
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${m == thang ? 'selected' : ''}>Tháng ${m}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Năm:</label>
                    <input type="number" name="nam" value="${nam}" style="width:110px;">
                </div>
                <div class="form-group">
                    <label>Tìm kiếm:</label>
                    <input type="text" name="keyword" value="${keyword}" placeholder="Tên hoặc email nhân viên..." style="width:220px;">
                </div>
                <button type="submit" class="btn-action btn-them"><i class="fa-solid fa-magnifying-glass"></i> Xem</button>
            </form>

            <h2 class="section-title"><i class="fa-solid fa-sack-dollar"></i> Bảng lương tháng ${thang}/${nam}</h2>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Nhân viên</th>
                            <th>Số đơn hoàn thành</th>
                            <th>Tổng hóa đơn</th>
                            <th>Lương cố định</th>
                            <th>Hoa hồng (${tiLeHoaHong}%)</th>
                            <th>Tổng lương</th>
                            <th>Trạng thái</th>
                            <th>Chức năng</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="lvc" items="${dsLuong}">
                            <tr>
                                <td style="text-align:left;">${lvc.hoten}<br><strong class="txt-gold">${lvc.email}</strong></td>
                                <td>${lvc.sodonhoanthanh}</td>
                                <td><fmt:formatNumber value="${lvc.tonghoadon}" pattern="#,###"/>đ</td>
                                <td><fmt:formatNumber value="${lvc.luongcoban}" pattern="#,###"/>đ</td>
                                <td><fmt:formatNumber value="${lvc.hoahong}" pattern="#,###"/>đ</td>
                                <td><strong class="txt-gold"><fmt:formatNumber value="${lvc.tongluong}" pattern="#,###"/>đ</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${!lvc.daChot}"><span class="badge-chuachot">Chưa chốt</span></c:when>
                                        <c:when test="${lvc.trangthai == 'Đã thanh toán'}"><span class="badge-dathanhtoan">Đã thanh toán</span></c:when>
                                        <c:otherwise><span class="badge-chuathanhtoan">Chưa thanh toán</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${!lvc.daChot}">
                                            <form action="${pageContext.request.contextPath}/luongvanchuyenadmin" method="POST" style="margin:0;">
                                                <input type="hidden" name="email" value="${lvc.email}">
                                                <input type="hidden" name="thang" value="${thang}">
                                                <input type="hidden" name="nam" value="${nam}">
                                                <button type="submit" name="action" value="chotluong" class="btn-chot"
                                                        onclick="return confirm('Chốt lương tháng ${thang}/${nam} cho ${lvc.email}? Số liệu sẽ được lưu cố định.')">Chốt lương</button>
                                            </form>
                                        </c:when>
                                        <c:when test="${lvc.trangthai != 'Đã thanh toán'}">
                                            <form action="${pageContext.request.contextPath}/luongvanchuyenadmin" method="POST" style="margin:0;">
                                                <input type="hidden" name="maluong" value="${lvc.maluong}">
                                                <input type="hidden" name="thang" value="${thang}">
                                                <input type="hidden" name="nam" value="${nam}">
                                                <button type="submit" name="action" value="thanhtoan" class="btn-thanhtoan"
                                                        onclick="return confirm('Xác nhận đã thanh toán lương tháng ${thang}/${nam} cho ${lvc.email}?')">Đã thanh toán</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty dsLuong}">
                            <tr class="empty-row"><td colspan="8">Chưa có nhân viên vận chuyển nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>