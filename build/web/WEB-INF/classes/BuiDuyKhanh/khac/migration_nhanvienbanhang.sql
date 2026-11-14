-- =========================================================================
-- MIGRATION: QUẢN LÝ NHÂN VIÊN BÁN HÀNG (luồng ADMIN)
-- Chạy 1 lần trên database "datn". An toàn để chạy lại.
-- =========================================================================
USE datn
GO

IF OBJECT_ID('nhanvienbanhang', 'U') IS NULL
BEGIN
    CREATE TABLE nhanvienbanhang (
        email       NVARCHAR(100) PRIMARY KEY,
        hoten       NVARCHAR(100) NOT NULL,
        matkhau     NVARCHAR(100) NOT NULL,
        sodienthoai VARCHAR(20)   NOT NULL,
        ngaydangky  VARCHAR(20),
        trangthai   NVARCHAR(50)  NOT NULL DEFAULT N'Đang hoạt động',
        anhcccd     VARCHAR(255)  NULL      -- tên file ảnh CCCD (lưu qua ImageStorage)
    );
END
GO

SELECT * FROM nhanvienbanhang;
GO
