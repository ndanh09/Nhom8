<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản Lý Trò Chuyện - LODI AUTHENTIC STORE</title>
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
    
        <style>
            /* ===== Quản lý trò chuyện ===== */
            .tc-error { background:#fdecea; border:1px solid #e0a39c; color:#8a2a1f; border-radius:10px; padding:14px 18px; margin-bottom:20px; font-size:14px; line-height:1.6; }
            .tc-layout { display:grid; grid-template-columns:340px minmax(0,1fr); gap:22px; align-items:start; }
            .tc-list, .tc-thread { background:#f2efe4; border:2px solid #c9a24b; border-radius:12px; box-shadow:0 8px 30px rgba(40,30,0,0.14); overflow:hidden; }
            .tc-search { display:flex; gap:8px; padding:14px; border-bottom:1px solid rgba(201,162,75,0.4); }
            .tc-search input { flex:1; min-width:0; padding:10px 12px; border:1px solid #d8cfae; border-radius:8px; background:#fff; font-size:14px; outline:none; }
            .tc-search input:focus { border-color:#c9a24b; }
            .tc-search button { padding:0 14px; border:0; border-radius:8px; background:#0d0d0e; color:#c9a24b; cursor:pointer; }
            .tc-items { max-height:calc(100vh - 290px); min-height:200px; overflow-y:auto; }
            .tc-item { display:block; padding:13px 16px; border-bottom:1px solid rgba(201,162,75,0.28); text-decoration:none; color:#1c1b18; }
            .tc-item:hover { background:#ebe5cf; }
            .tc-item.active { background:#e3d9b4; border-left:4px solid #8a6a1c; padding-left:12px; }
            .tc-top { display:flex; justify-content:space-between; align-items:center; gap:8px; }
            .tc-top b { font-size:14.5px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
            .tc-badge { background:#c0392b; color:#fff; font-size:11px; font-weight:700; border-radius:20px; padding:2px 8px; flex:none; }
            .tc-sub { font-size:12px; color:#7a7565; margin-top:2px; }
            .tc-last { font-size:13px; color:#3b392f; margin-top:6px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
            .tc-meta { display:flex; justify-content:space-between; align-items:center; margin-top:7px; font-size:11.5px; color:#7a7565; }
            .tc-pill { padding:2px 9px; border-radius:20px; font-weight:700; font-size:11px; }
            .tc-pill.on { background:rgba(34,197,94,0.14); color:#2f7d4a; border:1px solid rgba(34,197,94,0.4); }
            .tc-pill.off { background:rgba(192,57,43,0.12); color:#a5321f; border:1px solid rgba(192,57,43,0.4); }
            .tc-empty { padding:40px 20px; text-align:center; color:#7a7565; font-size:14px; line-height:1.7; }
            .tc-thead { display:flex; justify-content:space-between; align-items:center; gap:14px; flex-wrap:wrap; padding:14px 18px; background:#0d0d0e; border-bottom:2px solid #c9a24b; }
            .tc-thead b { display:block; font-size:16px; color:#c9a24b; letter-spacing:.5px; }
            .tc-thead small { display:block; margin-top:2px; font-size:12px; color:#a3a3a3; }
            .tc-thead form { margin:0; }
            .tc-toggle { padding:9px 16px; border-radius:30px; border:1px solid; font-weight:700; font-size:12.5px; cursor:pointer; }
            .tc-toggle.on { background:rgba(34,197,94,0.15); color:#6fe39b; border-color:rgba(34,197,94,0.6); }
            .tc-toggle.off { background:rgba(192,57,43,0.18); color:#ff9d8f; border-color:rgba(192,57,43,0.7); }
            .tc-toggle:hover { filter:brightness(1.15); }
            .tc-msgs { height:calc(100vh - 430px); min-height:240px; overflow-y:auto; padding:18px; display:flex; flex-direction:column; gap:12px; background:#f6f3ec; }
            .tc-msg { max-width:72%; padding:10px 14px; border-radius:14px; font-size:14px; line-height:1.6; word-wrap:break-word; }
            .tc-msg .tc-who { font-size:11px; font-weight:700; letter-spacing:.6px; text-transform:uppercase; margin-bottom:3px; opacity:.75; }
            .tc-msg .tc-time { font-size:10.5px; margin-top:4px; opacity:.6; text-align:right; }
            .tc-msg.khach { align-self:flex-start; background:#fff; border:1px solid #d8cfae; border-bottom-left-radius:4px; }
            .tc-msg.bot { align-self:flex-end; background:#0d0d0e; color:#e8e2cd; border-bottom-right-radius:4px; }
            .tc-msg.bot a { color:#c9a24b; }
            .tc-msg.admin { align-self:flex-end; background:linear-gradient(135deg,#f6edcf 0%,#c9a24b 60%,#a8812c 100%); color:#1b1405; font-weight:600; border-bottom-right-radius:4px; }
            .tc-reply { padding:14px 18px; border-top:1px solid rgba(201,162,75,0.4); background:#f2efe4; }
            .tc-reply textarea { width:100%; min-height:78px; resize:vertical; padding:11px 13px; border:1px solid #d8cfae; border-radius:8px; background:#fff; font:14px 'Inter', sans-serif; outline:none; }
            .tc-reply textarea:focus { border-color:#c9a24b; }
            .tc-reply-row { display:flex; justify-content:space-between; align-items:center; gap:12px; flex-wrap:wrap; margin-top:10px; }
            .tc-reply-row label { font-size:13px; color:#3b392f; display:flex; align-items:center; gap:7px; }
            .tc-send { padding:11px 26px; border:0; border-radius:8px; cursor:pointer; font-weight:700; font-size:12.5px; text-transform:uppercase; letter-spacing:.5px; background:linear-gradient(135deg,#f6edcf 0%,#c9a24b 60%,#a8812c 100%); color:#1b1405; }
            .tc-send:hover { filter:brightness(1.07); }
            .tc-hint { padding:12px 18px; font-size:12.5px; color:#7a7565; background:#ece6d0; border-top:1px dashed rgba(201,162,75,0.5); line-height:1.6; }
            @media (max-width:1100px) { .tc-layout { grid-template-columns:1fr; } .tc-items { max-height:300px; } }
        </style>
    </head>

    <body>

        <div class="page-header">
            <h1>Quản lý trò chuyện</h1>
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
                <c:if test="${sessionScope.vaiTro == 'QUANLY'}"><li><a href="${pageContext.request.contextPath}/nguoibanservlet"><i class="fa-solid fa-user-tag icon"></i><span>Quản lý sản phẩm</span></a></li></c:if>
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
                <c:if test="${sessionScope.vaiTro == 'DOANHTHU'}"><li class="active"><a href="${pageContext.request.contextPath}/quanlytrochuyen"><i class="fa-solid fa-comments icon"></i><span>Quản lý trò chuyện</span></a></li></c:if>
                <li><a href="${pageContext.request.contextPath}/dangxuatservlet?admin=1"><i class="fa-solid fa-right-from-bracket icon"></i><span>Đăng xuất</span></a></li>
            </ul>
        </div>

        <div class="container">
            <h1>Quản lý trò chuyện</h1>
            <div class="title-line"></div>

            <c:if test="${not empty loi}"><div class="tc-error"><c:out value="${loi}"/></div></c:if>

            <div class="tc-layout">
                <%-- ===== Cột trái: danh sách cuộc trò chuyện ===== --%>
                <aside class="tc-list">
                    <form class="tc-search" method="get" action="${pageContext.request.contextPath}/quanlytrochuyen">
                        <c:if test="${not empty hoiThoai}"><input type="hidden" name="id" value="${hoiThoai.id}"></c:if>
                        <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="Tìm tên, email, nội dung...">
                        <button type="submit" title="Tìm"><i class="fa-solid fa-magnifying-glass"></i></button>
                    </form>
                    <div class="tc-items">
                        <c:forEach var="c" items="${dsHoiThoai}">
                            <c:url var="linkChat" value="/quanlytrochuyen">
                                <c:param name="id" value="${c.id}"/>
                                <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
                            </c:url>
                            <a class="tc-item ${hoiThoai.id == c.id ? 'active' : ''}" href="${linkChat}">
                                <div class="tc-top">
                                    <b><c:out value="${c.tenkhach}"/></b>
                                    <c:if test="${c.chuaDoc > 0}"><span class="tc-badge">${c.chuaDoc} mới</span></c:if>
                                </div>
                                <div class="tc-sub"><c:out value="${empty c.email ? 'Khách chưa đăng nhập' : c.email}"/></div>
                                <div class="tc-last"><c:out value="${c.tinCuoi}"/></div>
                                <div class="tc-meta">
                                    <span>${c.thoigianCuoi}</span>
                                    <span class="tc-pill ${c.botBat ? 'on' : 'off'}">${c.botBat ? 'Bot bật' : 'Bot tắt'}</span>
                                </div>
                            </a>
                        </c:forEach>
                        <c:if test="${empty dsHoiThoai}">
                            <div class="tc-empty">Chưa có cuộc trò chuyện nào.<br>Khi khách nhắn tin ở trang chatbot, cuộc trò chuyện sẽ hiện ở đây.</div>
                        </c:if>
                    </div>
                </aside>

                <%-- ===== Cột phải: lịch sử chat + trả lời ===== --%>
                <section class="tc-thread">
                    <c:choose>
                        <c:when test="${not empty hoiThoai}">
                            <div class="tc-thead">
                                <div>
                                    <b><c:out value="${hoiThoai.tenkhach}"/></b>
                                    <small><c:out value="${empty hoiThoai.email ? 'Khách chưa đăng nhập (theo phiên truy cập)' : hoiThoai.email}"/></small>
                                </div>
                                <form method="post" action="${pageContext.request.contextPath}/quanlytrochuyen">
                                    <input type="hidden" name="action" value="bot">
                                    <input type="hidden" name="id" value="${hoiThoai.id}">
                                    <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>">
                                    <input type="hidden" name="bat" value="${hoiThoai.botBat ? '0' : '1'}">
                                    <button type="submit" class="tc-toggle ${hoiThoai.botBat ? 'on' : 'off'}">
                                        <i class="fa-solid ${hoiThoai.botBat ? 'fa-robot' : 'fa-headset'}"></i>
                                        ${hoiThoai.botBat ? 'Chatbot đang BẬT · bấm để TẮT với khách này' : 'Chatbot đang TẮT · bấm để BẬT lại'}
                                    </button>
                                </form>
                            </div>

                            <div class="tc-msgs" id="tcMsgs">
                                <c:forEach var="m" items="${tinNhan}">
                                    <%-- m[0]=id, m[1]=khach|bot|admin, m[2]=nội dung HTML (đã escape khi lưu), m[3]=giờ --%>
                                    <div class="tc-msg ${m[1]}">
                                        <div class="tc-who">
                                            <c:choose>
                                                <c:when test="${m[1] == 'khach'}"><c:out value="${hoiThoai.tenkhach}"/></c:when>
                                                <c:when test="${m[1] == 'admin'}">Nhân viên (admin)</c:when>
                                                <c:otherwise>Chatbot</c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>${m[2]}</div>
                                        <div class="tc-time">${m[3]}</div>
                                    </div>
                                </c:forEach>
                                <c:if test="${empty tinNhan}"><div class="tc-empty">Cuộc trò chuyện này chưa có tin nhắn.</div></c:if>
                            </div>

                            <form class="tc-reply" method="post" action="${pageContext.request.contextPath}/quanlytrochuyen">
                                <input type="hidden" name="action" value="reply">
                                <input type="hidden" name="id" value="${hoiThoai.id}">
                                <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>">
                                <textarea id="tcReply" name="noidung" maxlength="1000" required placeholder="Nhập nội dung trả lời khách..."></textarea>
                                <div class="tc-reply-row">
                                    <label>
                                        <input type="checkbox" name="tatbot" value="1" ${hoiThoai.botBat ? '' : 'disabled'}>
                                        <c:choose>
                                            <c:when test="${hoiThoai.botBat}">Tắt chatbot với khách này sau khi gửi</c:when>
                                            <c:otherwise>Chatbot đã tắt với khách này</c:otherwise>
                                        </c:choose>
                                    </label>
                                    <button type="submit" class="tc-send"><i class="fa-solid fa-paper-plane"></i> Gửi trả lời</button>
                                </div>
                            </form>
                            <div class="tc-hint">
                                Tin của bạn hiện ngay trong khung chat của khách (tự cập nhật sau vài giây). Khi chatbot đang tắt, tin khách gửi vẫn được lưu nhưng chatbot không trả lời - chỉ bạn trả lời.
                                Khách bấm "Cuộc trò chuyện mới" chỉ ẩn tin cũ phía khách, bạn vẫn xem được toàn bộ lịch sử ở đây.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="tc-empty" style="padding:90px 20px">
                                <i class="fa-solid fa-comments" style="font-size:38px;color:#c9a24b"></i><br><br>
                                Chọn một cuộc trò chuyện ở bên trái để xem lịch sử,<br>trả lời khách hoặc tắt chatbot riêng cho khách đó.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </div>
        </div>

        <script>
            (function () {
                var box = document.getElementById('tcMsgs');
                if (box) { box.scrollTop = box.scrollHeight; }          // mở ra là ở tin nhắn mới nhất

                // Cứ 3 giây hỏi server "có tin mới chưa" (1 câu SQL rất nhẹ). Có tin mới thì chỉ thay danh sách
                // cuộc trò chuyện + khung tin nhắn - KHÔNG tải lại trang, không mất chữ đang gõ trong ô trả lời.
                var sig = ${empty chatSig ? 0 : chatSig};
                var base = '${pageContext.request.contextPath}/quanlytrochuyen';

                function capNhat() {
                    fetch(location.href, { cache: 'no-store', credentials: 'same-origin' })
                        .then(function (r) { return r.text(); })
                        .then(function (html) {
                            var doc = new DOMParser().parseFromString(html, 'text/html');
                            var dsMoi = doc.querySelector('.tc-items'), ds = document.querySelector('.tc-items');
                            if (dsMoi && ds) { var top = ds.scrollTop; ds.innerHTML = dsMoi.innerHTML; ds.scrollTop = top; }
                            var mMoi = doc.getElementById('tcMsgs'), m = document.getElementById('tcMsgs');
                            if (mMoi && m) {
                                var cuoi = m.scrollHeight - m.scrollTop - m.clientHeight < 80;   // đang xem cuối thì tự cuộn xuống
                                m.innerHTML = mMoi.innerHTML;
                                if (cuoi) { m.scrollTop = m.scrollHeight; }
                            }
                        })
                        .catch(function () {});
                }

                function kiemTra() {
                    if (document.hidden) { return; }
                    fetch(base + '?action=poll', { cache: 'no-store', credentials: 'same-origin' })
                        .then(function (r) {
                            if (r.status === 401) { location.href = '${pageContext.request.contextPath}/view/dangnhap.jsp'; return null; }
                            return r.json();
                        })
                        .then(function (d) {
                            if (d && d.sig !== sig) { sig = d.sig; capNhat(); }
                        })
                        .catch(function () {});
                }
                setInterval(kiemTra, 3000);
                document.addEventListener('visibilitychange', function () { if (!document.hidden) { kiemTra(); } });
            })();
        </script>
    </body>
</html>
