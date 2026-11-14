/* ============================================================
   MIGRATION: Bổ sung cột "trangthai" để hỗ trợ tính năng
   KHÓA / MỞ KHÓA (thay cho xóa hẳn dữ liệu) ở các trang admin:
     - Quản lý khách hàng   (bảng donhang)
     - Quản trị viên        (bảng quantrivien, dangky)

   Chạy đoạn SQL này 1 lần trên SQL Server trước khi build/deploy
   lại ứng dụng có tính năng khóa/mở khóa mới. An toàn để chạy
   nhiều lần (có kiểm tra cột đã tồn tại chưa).

   Lưu ý: bảng "phieugiamgia" (mã giảm giá) và "vanchuyen"
   (nhân viên vận chuyển) đã có sẵn cột trangthai từ trước nên
   KHÔNG cần migration gì thêm cho 2 bảng đó.
   ============================================================ */

IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('donhang') AND name = 'trangthai'
)
BEGIN
    ALTER TABLE donhang ADD trangthai NVARCHAR(20) NOT NULL DEFAULT N'Đang hoạt động';
END

IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('quantrivien') AND name = 'trangthai'
)
BEGIN
    ALTER TABLE quantrivien ADD trangthai NVARCHAR(20) NOT NULL DEFAULT N'Đang hoạt động';
END

IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('dangky') AND name = 'trangthai'
)
BEGIN
    ALTER TABLE dangky ADD trangthai NVARCHAR(20) NOT NULL DEFAULT N'Đang hoạt động';
END
