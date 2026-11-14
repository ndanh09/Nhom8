-- =========================================================================
-- MIGRATION: LƯƠNG NHÂN VIÊN BÁN HÀNG (luồng ADMIN)
-- Chạy 1 lần trên database "datn", SAU migration_nhanvienbanhang.sql.
-- An toàn để chạy lại.
--
-- Quy tắc lương: 7.000.000đ cố định / người / tháng
--                + 10% giá trị mỗi đơn mà nhân viên phụ trách và khách đã nhận hàng.
-- =========================================================================
USE datn
GO

-- 1) Mỗi đơn hàng (donhang) có thể gán cho 1 nhân viên bán hàng phụ trách.
--    NULL = chưa gán (không ai được hoa hồng đơn đó).
IF COL_LENGTH('donhang', 'emailnhanvienbanhang') IS NULL
BEGIN
    ALTER TABLE donhang ADD emailnhanvienbanhang NVARCHAR(100) NULL;
END
GO

-- 2) Bảng lương đã CHỐT theo tháng (đóng băng số liệu, dùng làm căn cứ thanh toán).
IF OBJECT_ID('luongnhanvienbanhang', 'U') IS NULL
BEGIN
    CREATE TABLE luongnhanvienbanhang (
        maluong        INT IDENTITY(1,1) PRIMARY KEY,
        email          NVARCHAR(100) NOT NULL,      -- nhanvienbanhang.email
        thang          INT           NOT NULL,
        nam            INT           NOT NULL,
        luongcoban     BIGINT        NOT NULL,
        sodonhoanthanh INT           NOT NULL DEFAULT 0,
        tonghoadon     BIGINT        NOT NULL DEFAULT 0,
        hoahong        BIGINT        NOT NULL DEFAULT 0,
        tongluong      BIGINT        NOT NULL,
        trangthai      NVARCHAR(50)  NOT NULL DEFAULT N'Chưa thanh toán',
        ngaychot       DATETIME      NULL,
        ngaythanhtoan  DATETIME      NULL,
        CONSTRAINT UQ_luongnhanvienbanhang UNIQUE (email, thang, nam)   -- mỗi người chỉ chốt 1 lần / tháng
    );
END
GO

SELECT * FROM luongnhanvienbanhang;
GO
