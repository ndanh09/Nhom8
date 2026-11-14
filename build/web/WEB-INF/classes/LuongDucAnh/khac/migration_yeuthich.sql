-- =========================================================================
-- MIGRATION: QUẢN LÝ DANH SÁCH TOP SẢN PHẨM (ÁO) YÊU THÍCH
-- Chạy 1 lần trên database "datn". An toàn để chạy lại (có kiểm tra đã tồn tại chưa).
--
-- Phân luồng chức năng:
--   * KHÁCH HÀNG : bấm tim yêu thích, trang "Yêu thích" của tôi, mục "Top áo yêu thích" ở trang chủ
--   * QUẢN LÝ (luồng sản phẩm)  : "Top sản phẩm yêu thích"  -> /quanlytopyeuthich
--   * QUẢN TRỊ VIÊN (tài khoản) : "Yêu thích theo tài khoản" -> /quanlyyeuthichtaikhoan
-- =========================================================================
USE datn
GO

-- 1) Mỗi dòng = 1 tài khoản khách hàng yêu thích 1 sản phẩm (tính theo MÃ GỐC, gộp mọi size).
IF OBJECT_ID('yeuthich', 'U') IS NULL
BEGIN
    CREATE TABLE yeuthich (
        makhachhang VARCHAR(100) NOT NULL,           -- email khách hàng (giống giohang.makhachhang)
        magoc       VARCHAR(50)  NOT NULL,           -- nguoiban.magoc (không phụ thuộc size / tên)
        ngaythem    DATETIME     NOT NULL DEFAULT GETDATE(),
        CONSTRAINT PK_yeuthich PRIMARY KEY (makhachhang, magoc)
    );
    CREATE INDEX IX_yeuthich_magoc ON yeuthich(magoc);
END
GO

-- 2) Cấu hình của quản lý cho danh sách Top (chỉ có dòng khi quản lý đã ghim hoặc ẩn sản phẩm).
IF OBJECT_ID('top_yeuthich', 'U') IS NULL
BEGIN
    CREATE TABLE top_yeuthich (
        magoc       VARCHAR(50) NOT NULL PRIMARY KEY,
        ghim        BIT NOT NULL DEFAULT 0,          -- 1 = quản lý ghim lên đầu danh sách Top
        thutu       INT NOT NULL DEFAULT 0,          -- thứ tự giữa các sản phẩm đã ghim (nhỏ đứng trước)
        an          BIT NOT NULL DEFAULT 0,          -- 1 = ẩn khỏi Top ở trang chủ
        ngaycapnhat DATETIME NOT NULL DEFAULT GETDATE()
    );
END
GO

-- 3) Cấu hình chung của mục Top (hiện chỉ có khóa TOP_HIEN_THI = số áo hiển thị ở trang chủ; không có dòng = 8).
IF OBJECT_ID('cauhinh_yeuthich', 'U') IS NULL
BEGIN
    CREATE TABLE cauhinh_yeuthich (
        khoa   VARCHAR(50) NOT NULL PRIMARY KEY,
        giatri INT NOT NULL
    );
END
GO

SELECT * FROM yeuthich;
SELECT * FROM top_yeuthich;
GO
