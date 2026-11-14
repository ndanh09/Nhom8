/* LODI - nút tim "Yêu thích" cho khách hàng (luồng khách hàng).
   Dùng: thêm  <button type="button" class="fav-btn" data-masanpham="MÃ"> vào thẻ sản phẩm,
   nạp file này với  <script src=".../js/lodi-yeuthich.js" data-ctx="${pageContext.request.contextPath}">.
   - Khi trang tải xong: hỏi server mã nào đã được yêu thích để tô đỏ tim.
   - Khi bấm: gọi /yeuthichservlet (action=toggle); chưa đăng nhập thì chuyển tới trang đăng nhập. */
(function () {
    var self = document.currentScript;
    var ctx = (self && self.getAttribute('data-ctx')) || '';
    var url = ctx + '/yeuthichservlet';

    // tự nạp CSS của chức năng (không phải sửa phần <head> của từng trang)
    var lk = document.createElement('link');
    lk.rel = 'stylesheet';
    lk.href = ctx + '/css/lodi-yeuthich.css';
    document.head.appendChild(lk);

    function nutTim() { return Array.prototype.slice.call(document.querySelectorAll('.fav-btn[data-masanpham]')); }

    function dat(btn, liked) {
        btn.classList.toggle('on', !!liked);
        btn.setAttribute('aria-pressed', liked ? 'true' : 'false');
        btn.title = liked ? 'Bỏ khỏi danh sách yêu thích' : 'Thêm vào danh sách yêu thích';
        var t = btn.querySelector('.fav-text');
        if (t) { t.textContent = liked ? 'Đã yêu thích' : 'Yêu thích'; }
    }

    function post(body) {
        return fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: body
        }).then(function (r) { return r.json(); });
    }

    function taiTrangThai() {
        var nut = nutTim();
        if (!nut.length) { return; }
        var parts = ['action=status'];
        nut.forEach(function (b) { parts.push('ma=' + encodeURIComponent(b.getAttribute('data-masanpham'))); });
        post(parts.join('&')).then(function (d) {
            if (!d || !d.success || !d.fav) { return; }
            nut.forEach(function (b) { dat(b, d.fav.indexOf(b.getAttribute('data-masanpham')) >= 0); });
        }).catch(function () { /* lỗi mạng: giữ nguyên tim trống */ });
    }

    document.addEventListener('click', function (e) {
        var btn = e.target.closest ? e.target.closest('.fav-btn[data-masanpham]') : null;
        if (!btn) { return; }
        e.preventDefault();
        e.stopPropagation();
        if (btn.disabled) { return; }
        btn.disabled = true;
        post('action=toggle&masanpham=' + encodeURIComponent(btn.getAttribute('data-masanpham')))
            .then(function (d) {
                btn.disabled = false;
                if (!d || !d.success) {
                    if (d && d.login) {
                        alert(d.message);
                        window.location.href = ctx + '/view/dangnhap.jsp';
                    } else {
                        alert((d && d.message) || 'Có lỗi xảy ra, vui lòng thử lại.');
                    }
                    return;
                }
                dat(btn, d.liked);
                var the = btn.closest('.card');
                var dem = the && the.querySelector('.fav-count');
                if (dem && typeof d.count === 'number') { dem.textContent = d.count; }
                // 1 sản phẩm có thể xuất hiện ở nhiều chỗ trên trang (Top + lưới sản phẩm): đồng bộ lại tất cả tim
                taiTrangThai();
            })
            .catch(function () { btn.disabled = false; alert('Không kết nối được máy chủ, vui lòng thử lại.'); });
    });

    if (document.readyState === 'loading') { document.addEventListener('DOMContentLoaded', taiTrangThai); }
    else { taiTrangThai(); }
})();
