-- =========================================================================
-- MIGRATION: MODULE QUẢN LÝ TRÒ CHUYỆN
--   * Lưu lịch sử chat giữa khách hàng và chatbot (để admin xem lại)
--   * Admin trả lời trực tiếp khách
--   * Admin tắt / bật chatbot riêng cho từng khách
-- Chạy file này SAU KHI đã có database "datn". Chỉ THÊM bảng mới,
-- KHÔNG xóa hay đụng tới dữ liệu hiện có. Chạy nhiều lần cũng không lỗi.
-- =========================================================================
USE datn;
GO

-- 1) Mỗi khách (hoặc mỗi phiên của khách chưa đăng nhập) có 1 cuộc trò chuyện
IF OBJECT_ID('chat_hoithoai', 'U') IS NULL
BEGIN
    CREATE TABLE chat_hoithoai (
        id            INT IDENTITY(1,1) PRIMARY KEY,
        khoa          NVARCHAR(150) NOT NULL UNIQUE,   -- email khách (viết thường) hoặc 'khach:<mã phiên>' nếu chưa đăng nhập
        email         NVARCHAR(100) NULL,
        tenkhach      NVARCHAR(100) NOT NULL,
        bot_bat       BIT NOT NULL DEFAULT 1,          -- 1 = chatbot tự trả lời, 0 = chỉ admin trả lời
        chua_doc      INT NOT NULL DEFAULT 0,          -- số tin của khách mà admin chưa xem
        tin_cuoi      NVARCHAR(200) NULL,              -- tóm tắt tin nhắn cuối (hiện ở danh sách)
        thoigian_cuoi DATETIME NOT NULL DEFAULT GETDATE(),
        ngaytao       DATETIME NOT NULL DEFAULT GETDATE(),
        khach_tu_id   BIGINT NOT NULL DEFAULT 0        -- khách bấm "Cuộc trò chuyện mới": chỉ hiện tin có id lớn hơn mốc này (admin vẫn thấy hết)
    );
END
GO

-- 2) Các tin nhắn của từng cuộc trò chuyện
IF OBJECT_ID('chat_tinnhan', 'U') IS NULL
BEGIN
    CREATE TABLE chat_tinnhan (
        id          BIGINT IDENTITY(1,1) PRIMARY KEY,
        hoithoai_id INT NOT NULL,
        nguoigui    VARCHAR(10) NOT NULL,              -- 'khach' | 'bot' | 'admin'
        noidung     NVARCHAR(MAX) NOT NULL,            -- HTML đã được escape an toàn
        thoigian    DATETIME NOT NULL DEFAULT GETDATE(),
        CONSTRAINT FK_chat_tinnhan_hoithoai FOREIGN KEY (hoithoai_id)
            REFERENCES chat_hoithoai(id) ON DELETE CASCADE,
        CONSTRAINT CK_chat_tinnhan_nguoigui CHECK (nguoigui IN ('khach', 'bot', 'admin'))
    );
    CREATE INDEX IX_chat_tinnhan_hoithoai ON chat_tinnhan(hoithoai_id, id);
END
GO

-- Kiểm tra nhanh
SELECT 'chat_hoithoai' AS bang, COUNT(*) AS sodong FROM chat_hoithoai
UNION ALL
SELECT 'chat_tinnhan', COUNT(*) FROM chat_tinnhan;
GO
