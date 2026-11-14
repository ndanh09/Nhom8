-- =========================================================================
-- MIGRATION: QUẢN LÝ BANNER (trang chủ + trang sản phẩm) - luồng ADMIN
-- Chạy 1 lần trên database "datn". An toàn để chạy lại (có kiểm tra đã tồn tại chưa).
-- =========================================================================
USE datn
GO

IF OBJECT_ID('banner', 'U') IS NULL
BEGIN
    CREATE TABLE banner (
        id        INT IDENTITY(1,1) PRIMARY KEY,
        vitri     VARCHAR(20)   NOT NULL,              -- 'TRANGCHU' | 'SANPHAM'
        nhan      NVARCHAR(100) NULL,                  -- dòng nhỏ phía trên tiêu đề (VD: PREMIER LEAGUE · 2026-27)
        tieude    NVARCHAR(150) NULL,
        mota      NVARCHAR(300) NULL,
        hinhanh   NVARCHAR(200) NOT NULL,              -- tên file ảnh (ImageStorage / web/image)
        lienket   NVARCHAR(300) NULL,                  -- đường dẫn nội bộ (nguoidungservlet) hoặc http(s)://...
        nuttext   NVARCHAR(50)  NULL,                  -- chữ trên nút (VD: XEM NGAY)
        thutu     INT NOT NULL DEFAULT 0,              -- nhỏ hiện trước
        hienthi   BIT NOT NULL DEFAULT 1,              -- 1 = hiện, 0 = ẩn
        ngaytao   DATETIME NOT NULL DEFAULT GETDATE(),
        CONSTRAINT CK_banner_vitri CHECK (vitri IN ('TRANGCHU', 'SANPHAM'))
    );
    CREATE INDEX IX_banner_vitri ON banner(vitri, hienthi, thutu);

    -- Dữ liệu mẫu = 5 banner đang dùng ở trang chủ
    INSERT INTO banner (vitri, nhan, tieude, mota, hinhanh, lienket, nuttext, thutu) VALUES
    ('TRANGCHU', N'LA LIGA · 2026-27', N'Real Madrid · Barcelona', N'Real Madrid sân nhà, sân khách cùng FC Barcelona - 4 mẫu áo đấu của bóng đá Tây Ban Nha.', 'banner-laliga.jpg', 'nguoidungservlet', N'XEM LA LIGA', 1),
    ('TRANGCHU', N'PREMIER LEAGUE · 2026-27', N'Man United · Man City', N'Quỷ đỏ Manchester United cùng Manchester City và Arsenal - 6 mẫu áo đấu Ngoại hạng Anh.', 'banner-premier.jpg', 'nguoidungservlet', N'XEM PREMIER LEAGUE', 2),
    ('TRANGCHU', N'SERIE A · 2026-27', N'AC Milan · Inter · Juventus', N'Những ông lớn của bóng đá Ý - 6 mẫu áo đấu Serie A mùa giải mới.', 'banner-seriea.jpg', 'nguoidungservlet', N'XEM SERIE A', 3),
    ('TRANGCHU', N'BUNDESLIGA · 2026-27', N'Bayern · Dortmund', N'Bayern Munich và Borussia Dortmund - 4 mẫu áo đấu Bundesliga mùa giải mới.', 'banner-bundes.jpg', 'nguoidungservlet', N'XEM BUNDESLIGA', 4),
    ('TRANGCHU', N'LIGUE 1 · 2026-27', N'Paris Saint-Germain', N'Sân nhà và sân khách của PSG - 2 mẫu áo đấu Ligue 1 mùa giải mới.', 'banner-ligue1.jpg', 'nguoidungservlet', N'XEM LIGUE 1', 5);
END
GO

SELECT * FROM banner ORDER BY vitri DESC, thutu, id;
GO
