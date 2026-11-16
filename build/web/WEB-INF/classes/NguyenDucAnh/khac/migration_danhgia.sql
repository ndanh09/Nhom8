-- =========================================================================
-- MIGRATION: MODULE QUẢN LÝ ĐÁNH GIÁ VÀ NHẬN XÉT SẢN PHẨM
-- Chạy file này SAU KHI đã có database "datn". Chỉ THÊM bảng mới, KHÔNG đụng
-- tới dữ liệu hiện có. Chạy lại nhiều lần vẫn an toàn.
--
-- Đối chiếu với DB thực tế của dự án (khác với bản đặc tả):
--   * Khách hàng được định danh bằng EMAIL (dangky.email, NVARCHAR(100)),
--     không có mã khách hàng kiểu INT  -> cột makhachhang NVARCHAR(100).
--   * Mỗi size của 1 áo là 1 dòng nguoiban/nguoidung với masanpham riêng
--     (vd mu1-M, mu1-L) và cùng 1 MÃ GỐC (nguoiban.magoc = mu1).
--     Đánh giá tính theo SẢN PHẨM (mã gốc) chứ không theo từng size, nên mua
--     size nào cũng đánh giá được và điểm trung bình gộp mọi size
--     -> cột magoc VARCHAR(50) (cùng cách bảng yeuthich đang dùng).
--   * Đơn "giao thành công" = trangthai.tinhtrang = 2 ("Đã nhận"), giống cách
--     module doanh thu / lương đang tính.
--   * Không đặt khóa ngoại tới nguoiban vì magoc không phải khóa chính, và
--     quản lý được phép xóa sản phẩm; dòng đánh giá mồ côi sẽ tự không hiển thị.
-- =========================================================================
USE datn;
GO

IF OBJECT_ID('danhgia', 'U') IS NULL
BEGIN
    CREATE TABLE danhgia (
        madanhgia   INT IDENTITY(1,1) NOT NULL,
        makhachhang NVARCHAR(100)     NOT NULL,           -- email khách hàng
        magoc       VARCHAR(50)       NOT NULL,           -- mã gốc sản phẩm (gộp mọi size)
        sosao       INT               NULL,               -- 1..5, cho phép NULL (chỉ nhận xét)
        noidung     NVARCHAR(MAX)     NULL,               -- nhận xét, cho phép NULL (chỉ chấm sao)
        trangthai   BIT               NOT NULL CONSTRAINT DF_danhgia_trangthai DEFAULT 1,  -- 1 = hiển thị, 0 = đã ẩn
        ngaydanhgia DATETIME          NOT NULL CONSTRAINT DF_danhgia_ngay DEFAULT GETDATE(),

        CONSTRAINT PK_danhgia PRIMARY KEY (madanhgia),

        -- Ràng buộc 1: mỗi khách chỉ đánh giá 1 lần / 1 sản phẩm (chặn cả khi gửi trùng đồng thời)
        CONSTRAINT UQ_danhgia_khach_sanpham UNIQUE (makhachhang, magoc),

        -- Ràng buộc 2: số sao hợp lệ
        CONSTRAINT CK_danhgia_sosao CHECK (sosao IS NULL OR sosao BETWEEN 1 AND 5),

        -- Ràng buộc 3: không được bỏ trống cả số sao lẫn nhận xét
        CONSTRAINT CK_danhgia_co_noidung CHECK (
            sosao IS NOT NULL
            OR NULLIF(LTRIM(RTRIM(noidung)), '') IS NOT NULL
        ),

        -- Khách hàng bị xóa tài khoản thì đánh giá đi theo
        CONSTRAINT FK_danhgia_dangky FOREIGN KEY (makhachhang)
            REFERENCES dangky(email) ON DELETE CASCADE
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_danhgia_magoc' AND object_id = OBJECT_ID('danhgia'))
    CREATE INDEX IX_danhgia_magoc ON danhgia(magoc, trangthai) INCLUDE (sosao, ngaydanhgia);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_danhgia_ngay' AND object_id = OBJECT_ID('danhgia'))
    CREATE INDEX IX_danhgia_ngay ON danhgia(ngaydanhgia DESC);
GO

-- -------------------------------------------------------------------------
-- DỮ LIỆU MẪU (tùy chọn): chỉ sinh cho khách đã THỰC SỰ nhận hàng
-- (trangthai.tinhtrang = 2), nên luôn hợp lệ với quy tắc nghiệp vụ.
-- Nếu chưa có đơn nào "Đã nhận" thì không chèn gì.
-- -------------------------------------------------------------------------
INSERT INTO danhgia (makhachhang, magoc, sosao, noidung, trangthai)
SELECT TOP 6 x.makhachhang, x.magoc,
       CASE x.rn % 3 WHEN 0 THEN NULL ELSE 5 - (x.rn % 3) END,
       CASE x.rn % 3 WHEN 1 THEN NULL
            ELSE N'Áo đẹp, vải mát, đúng mô tả. Giao hàng nhanh.' END,
       1
FROM (
    SELECT t.makhachhang, n.magoc,
           ROW_NUMBER() OVER (ORDER BY MIN(t.matrangthai)) AS rn
    FROM trangthai t
    JOIN nguoiban n ON n.masanpham = t.masanpham
    JOIN dangky   d ON d.email     = t.makhachhang
    WHERE t.tinhtrang = 2 AND n.magoc IS NOT NULL
    GROUP BY t.makhachhang, n.magoc
) x
WHERE NOT EXISTS (SELECT 1 FROM danhgia g WHERE g.makhachhang = x.makhachhang AND g.magoc = x.magoc);
GO

SELECT * FROM danhgia;
GO
