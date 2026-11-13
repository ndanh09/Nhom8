package model;

import java.util.Date;

/**
 * Bản ghi lương thưởng của 1 người vận chuyển trong 1 tháng.
 *
 * Quy tắc tính:
 *   - luongcoban: lương cố định (mặc định 5.000.000đ).
 *   - hoahong: 10% trên tổng giá trị (giaban*soluong) của các đơn giao
 *     thành công (trangthai.tinhtrang = 2) trong tháng đó.
 *   - tongluong = luongcoban + hoahong.
 *
 * daChot = false: đây chỉ là số liệu TẠM TÍNH real-time (chưa lưu DB), dùng
 * để admin xem trước khi bấm "Chốt lương".
 * daChot = true: số liệu đã được LƯU vào bảng luongvanchuyen (đã chốt sổ).
 */
public class LuongVanChuyen {

    private int maluong;
    private String email;
    private String hoten; // chỉ dùng để hiển thị khi join với bảng vanchuyen, KHÔNG lưu ở bảng này
    private int thang;
    private int nam;
    private long luongcoban;
    private int sodonhoanthanh;
    private long tonghoadon;
    private long hoahong;
    private long tongluong;
    private String trangthai;
    private Date ngaychot;
    private Date ngaythanhtoan;
    private boolean daChot;

    public LuongVanChuyen() {
    }

    public int getMaluong() {
        return maluong;
    }

    public void setMaluong(int maluong) {
        this.maluong = maluong;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public long getLuongcoban() {
        return luongcoban;
    }

    public void setLuongcoban(long luongcoban) {
        this.luongcoban = luongcoban;
    }

    public int getSodonhoanthanh() {
        return sodonhoanthanh;
    }

    public void setSodonhoanthanh(int sodonhoanthanh) {
        this.sodonhoanthanh = sodonhoanthanh;
    }

    public long getTonghoadon() {
        return tonghoadon;
    }

    public void setTonghoadon(long tonghoadon) {
        this.tonghoadon = tonghoadon;
    }

    public long getHoahong() {
        return hoahong;
    }

    public void setHoahong(long hoahong) {
        this.hoahong = hoahong;
    }

    public long getTongluong() {
        return tongluong;
    }

    public void setTongluong(long tongluong) {
        this.tongluong = tongluong;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public Date getNgaychot() {
        return ngaychot;
    }

    public void setNgaychot(Date ngaychot) {
        this.ngaychot = ngaychot;
    }

    public Date getNgaythanhtoan() {
        return ngaythanhtoan;
    }

    public void setNgaythanhtoan(Date ngaythanhtoan) {
        this.ngaythanhtoan = ngaythanhtoan;
    }

    public boolean isDaChot() {
        return daChot;
    }

    public void setDaChot(boolean daChot) {
        this.daChot = daChot;
    }
}
