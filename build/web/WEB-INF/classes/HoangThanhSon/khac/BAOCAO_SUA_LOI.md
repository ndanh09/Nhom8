# Đợt sửa 2 – Mua nhiều sản phẩm chỉ ra 1 đơn hàng

## Nguyên nhân
Mỗi lần thanh toán tạo 1 dòng `donhang` (mã đơn thật = `madonhang`) và mỗi sản phẩm là 1 dòng `trangthai`.
Các trang "Đơn hàng", admin, vận chuyển lại in `#ORD-<matrangthai>` (mã của TỪNG DÒNG sản phẩm) nên mua 2 sản
phẩm thì thấy 2 mã khác nhau (trong khi hóa đơn dùng mã `madonhang`, nên mã cũng không khớp nhau). Thông báo
"Đặt hàng thành công" còn liệt kê cả 2 mã. Ngoài ra bấm đúp "Xác nhận đặt hàng" có thể tạo đơn trùng.

## Đã sửa
- Mọi nơi (khách, admin, vận chuyển, thông báo, hóa đơn) giờ chỉ dùng **một mã `#ORD-<madonhang>`**.
- Trang **Đơn hàng của bạn**: các sản phẩm cùng 1 lần mua được gom dưới 1 mã đơn, có dòng "Tổng đơn hàng".
  Mới: `model/NhomDonHang.java`; sửa `TrangThaiServlet`, `TrangThaiDAO.getDanhSachDonHang`, `donhang.jsp`.
- `DonHangServlet`: chỉ 1 yêu cầu thanh toán/khách tại một thời điểm; không còn rơi về "mua cả giỏ hàng" khi
  session đã hết (gửi lặp không còn mua nhầm sản phẩm chưa chọn); kiểm tra tồn kho theo tổng số lượng cùng
  sản phẩm; xóa đơn rỗng nếu không dòng nào đặt được; sửa lại tổng tiền/mã SP nếu chỉ đặt được một phần.
- `DonHangDAO`: chuỗi mã sản phẩm tự cắt cho vừa cột `donhang.masanpham` (mua nhiều sản phẩm không còn bị
  "Đặt hàng thất bại"); lưu mã giảm giá/tiền giảm vào đơn (`luuThongTinGiamGia`, làm riêng nên nếu DB thiếu
  cột thì đơn vẫn đặt được); `HoaDonServlet` hiển thị tổng tiền hàng đúng khi có giảm giá.
- `thongtinkhachhang.jsp`: chặn bấm đúp nút đặt hàng; hiện được thông báo "Đặt hàng thất bại".
- Thông báo lỗi từ `DonHangServlet` gửi về giỏ hàng trước đây không hiện (sai tên thuộc tính session).
- `CapNhatTrangThaiServlet`: ô tìm kiếm của admin (mã đơn / khách hàng / mã SP) trước đây không có tác dụng.
- Thông báo của admin/vận chuyển không còn in mã dòng `matrangthai`.

## Đã kiểm thử
- Biên dịch toàn bộ Java (JDK 21 + Servlet API 6): 0 lỗi. Test logic gom đơn: 2 sản phẩm cùng đơn -> 1 mã.
- KHÔNG chạy được JSP/Tomcat và SQL Server thật trong môi trường này: hãy thử trên máy bạn
  (mua 2 sản phẩm cùng lúc -> chỉ 1 mã #ORD; mua có mã giảm giá -> xem hóa đơn; hủy; bấm đúp nút đặt hàng).

---

# Báo cáo sửa lỗi – SD2001_PRO230_nhom4

## Cách áp dụng
1. Giải nén file zip này **đè lên thư mục dự án** (giữ nguyên cấu trúc thư mục). Chỉ chứa các file đã sửa/thêm mới (38 file sửa, 2 file mới).
2. Trong NetBeans: **Clean and Build** (thư mục `build/` cũ còn chứa class biên dịch từ code cũ), rồi Run.
3. Không cần thay đổi CSDL ngoài `migration_khoa_taikhoan.sql` bạn đã có.

## Thay đổi
### Phân quyền / đăng nhập
- **Mới `util/AdminGuard.java`**; gắn vào 7 servlet admin trước đây ai cũng truy cập được: `DoanhThuServlet`, `CapNhatTrangThaiServlet`, `QuanLySizeServlet`, `nguoibanservlet`, `phieugiamgiaservlet`, `quanlykhachhangservlet`, `quanlykhohangservlet`. Chưa đăng nhập admin -> chuyển về trang đăng nhập (cả GET lẫn POST).
- **Đăng xuất admin**: 10 JSP admin nay trỏ tới `/dangxuatservlet?admin=1`; `DangXuatServlet` xóa thêm `isAdmin` (trước đây "đăng xuất" không hề đăng xuất).
- **`dangnhap.java`**: đổi session id khi đăng nhập (chống session fixation); gỡ quyền/phiên của vai trò cũ (admin <-> khách) để không bị mang quyền admin sang tài khoản khách; so sánh mật khẩu an toàn với null; `doGet` chuyển về trang đăng nhập thay vì trang trắng. Thông báo lỗi giữ nguyên.
- **`quantrivien.java` (/qtv)**: khóa/mở khóa báo đúng kết quả; không cho admin tự khóa tài khoản đang đăng nhập.
- **`TrangThaiServlet`**: chỉ chủ đơn mới hủy được đơn; **`giohangservlet` + `GioHangDAO.deleteSanPhamKhoiGio(id, makhachhang)`**: chỉ xóa được giỏ của chính mình.
- **Upload ảnh** (`nguoibanservlet`, `QuanLyVanChuyenAdminServlet`) qua `ImageStorage.luuAnh()`: chống path traversal, chỉ nhận png/jpg/jpeg/gif/webp/bmp/avif (không nhận svg).

### Đặt hàng / kho
- **`nguoidungservlet` addToCart**: giá, trạng thái, size, tồn kho lấy từ DB (không tin tham số từ trình duyệt); chặn số lượng <= 0; JSON luôn hợp lệ.
- **`DonHangServlet`**: kiểm tra tồn kho trước khi lưu đơn; trừ kho trước và kiểm tra kết quả; mã sản phẩm lấy từ giỏ trên server; chặn tiền giảm âm.
- **Hoàn kho** (`TrangThaiDAO.hoanKho` dùng chung): khách hủy / khách hoàn trả / **admin hủy** / **shipper báo giao thất bại** đều hoàn kho đúng 1 lần, trong 1 transaction, chỉ khi đổi trạng thái thành công; admin hủy và shipper báo thất bại cũng hoàn lượt mã giảm giá như khách tự hủy. (Trước đây admin hủy đơn ở trạng thái 0/1/4 và shipper báo thất bại làm mất hàng khỏi kho.)
- `GioHangDAO.truTonKho/congTonKho`, `nguoibandao.congSoLuong`: không còn bật lại sản phẩm "Ngừng bán" thành "Còn hàng".
- `quanlykhohangdao`: `CURRENT_DATE` -> `CAST(GETDATE() AS DATE)` (T-SQL); cộng dồn kho ngay trong SQL.

### Ổn định
- `dangkydao`, `nguoibandao`, `quanlykhachhangdao`, `quanlykhohangdao`: đóng Connection/Statement/ResultSet (try-with-resources); bỏ connection/list dùng chung giữa các request (`quanlykhohangdao` trước đây giữ **1 connection duy nhất** cho cả ứng dụng). `nguoidungdao`, `quantriviendao`: list cục bộ.
- `dangkydao.dk()` trả boolean (servlet không còn báo "thành công" khi lưu lỗi); `check()` bằng 1 câu SELECT, không NPE với null.
- `dangky.java`: `setCharacterEncoding("UTF-8")`, bỏ việc nạp cả bảng tài khoản (kèm mật khẩu) vào request, không hiện chi tiết lỗi hệ thống.
- `quanlykhachhangservlet`, `quanlykhohangservlet`, `nguoibanservlet`, `QuanLySizeServlet`: tham số sai không còn gây lỗi 500; action lạ không còn ra trang trắng; giá nhập dạng `1.500.000` được chấp nhận; redirect mã hóa URL; không lộ `e.getMessage()` ra giao diện.

### Lỗi nhỏ
- `/dangnhap.jsp` (404) -> `/view/dangnhap.jsp` (`giohangservlet`); bỏ link `/css/gioithieu.css` không tồn tại; thêm `web/image/default.png` (ảnh mặc định khi thêm sản phẩm không có ảnh, trước đây file này không tồn tại).

## Đã kiểm thử
- Biên dịch toàn bộ Java (JDK 21, Tomcat 10.1 API) và toàn bộ JSP (Jasper/JspC): 0 lỗi.
- Chạy thật trên Tomcat nhúng: chặn 8 trang admin khi chưa đăng nhập (GET + POST), đăng nhập/đăng xuất admin, không leo thang quyền admin -> khách, session id đổi sau đăng nhập, `addToCart` (giá giả, số lượng 0/âm/chữ, ngừng bán, hết hàng, vượt kho, cộng dồn), thanh toán bị chặn khi kho vừa giảm (không tạo đơn, giỏ còn nguyên), xóa giỏ/hủy đơn của người khác bị từ chối, upload ảnh (`../`, `..\`, `.jsp`).
- Chạy các câu SQL của DAO trên H2 (chế độ SQL Server): 32 kiểm tra đạt trên bản sửa; **cùng bộ test trên code gốc cho 10 lỗi + 1 NPE**. Hoàn trả hàng với 2 request đồng thời: gốc cộng sai kho ở 22/300 vòng, bản sửa 0/300.

## Chưa kiểm thử (không có SQL Server thật trong môi trường này)
- Chạy với SQL Server thật; đường **đặt hàng thành công** (`DonHangDAO.themDonHang` dùng cú pháp `OUTPUT INSERTED ... INTO @table` riêng của T-SQL).
- Hãy thử trên máy bạn: đặt hàng -> hủy đơn (khách/admin) -> kiểm tra tồn kho; nhập thêm kho ở trang Quản lý kho.

## Chưa thay đổi (nên xử lý sau)
- Tài khoản admin/khách cố định (`admin123`, `123`) trong `dangnhap.java`/`quantrivien.java`, mật khẩu `sa` trong `dbconnect.java`, mật khẩu lưu plaintext.
- Trang "Quản lý khách hàng" khóa theo dòng `donhang`, không chặn đăng nhập (chỉ khóa ở `/qtv` mới chặn). Người đang đăng nhập mà bị khóa vẫn giữ phiên đến khi đăng xuất.
- Mã giảm giá bị trừ lượt ngay khi bấm "Áp dụng"; thanh toán nhiều bước chưa nằm trong 1 transaction DB.
