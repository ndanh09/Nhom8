package model;

/** Một cuộc trò chuyện giữa khách hàng và chatbot / admin (bảng chat_hoithoai). */
public class ChatHoiThoai {

    private final int id;
    private final String khoa;
    private final String email;
    private final String tenkhach;
    private final boolean botBat;
    private final int chuaDoc;
    private final String tinCuoi;
    private final String thoigianCuoi;
    private final long khachTuId;

    public ChatHoiThoai(int id, String khoa, String email, String tenkhach, boolean botBat,
            int chuaDoc, String tinCuoi, String thoigianCuoi, long khachTuId) {
        this.id = id;
        this.khoa = khoa;
        this.email = email;
        this.tenkhach = tenkhach;
        this.botBat = botBat;
        this.chuaDoc = chuaDoc;
        this.tinCuoi = tinCuoi;
        this.thoigianCuoi = thoigianCuoi;
        this.khachTuId = khachTuId;
    }

    public int getId() { return id; }
    public String getKhoa() { return khoa; }
    /** Email khách; null nếu khách chưa đăng nhập. */
    public String getEmail() { return email; }
    public String getTenkhach() { return tenkhach; }
    /** true = chatbot đang tự trả lời khách này; false = đã tắt, chỉ admin trả lời. */
    public boolean isBotBat() { return botBat; }
    /** Số tin của khách mà admin chưa xem. */
    public int getChuaDoc() { return chuaDoc; }
    public String getTinCuoi() { return tinCuoi; }
    public String getThoigianCuoi() { return thoigianCuoi; }
    /** Khách chỉ nhìn thấy các tin có id lớn hơn mốc này (sau khi bấm "Cuộc trò chuyện mới"). */
    public long getKhachTuId() { return khachTuId; }
}
