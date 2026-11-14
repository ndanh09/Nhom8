package model;

import java.util.Date;

/**
 * Bản ghi lương của 1 nhân viên bán hàng trong 1 tháng.
 *
 * Quy tắc tính:
 *   - luongcoban: lương cố định 7.000.000đ / tháng.
 *   - hoahong: 10% trên tổng giá trị (giaban*soluong) các đơn được gán cho nhân viên
 *     và đã ở trạng thái "Đã nhận" (trangthai.tinhtrang = 2) trong tháng đó.
 *   - tongluong = luongcoban + hoahong.
 *
 * daChot = false: số liệu TẠM TÍNH real-time (chưa lưu DB), để admin xem trước khi "Chốt lương".
 * daChot = true : số liệu đã LƯU vào bảng luongnhanvienbanhang (đã chốt sổ).
 */
public class LuongNhanVienBanHang {

    private int maluong;
    private String email;
    private String hoten;       // chỉ để hiển thị (join bảng nhanvienbanhang), không lưu ở bảng lương
    private int thang;
    private int nam;
    private long luongcoban;
    private int sodonhoanthanh;
    private long tonghoadon;
    private long hoahong;
    private long tongluong;
    private String trangthai;   // Chưa chốt / Chưa thanh toán / Đã thanh toán
    private Date ngaychot;
    private Date ngaythanhtoan;
    private boolean daChot;

    public LuongNhanVienBanHang() {
    }

    public int getMaluong() { return maluong; }
    public void setMaluong(int maluong) { this.maluong = maluong; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }

    public int getThang() { return thang; }
    public void setThang(int thang) { this.thang = thang; }

    public int getNam() { return nam; }
    public void setNam(int nam) { this.nam = nam; }

    public long getLuongcoban() { return luongcoban; }
    public void setLuongcoban(long luongcoban) { this.luongcoban = luongcoban; }

    public int getSodonhoanthanh() { return sodonhoanthanh; }
    public void setSodonhoanthanh(int sodonhoanthanh) { this.sodonhoanthanh = sodonhoanthanh; }

    public long getTonghoadon() { return tonghoadon; }
    public void setTonghoadon(long tonghoadon) { this.tonghoadon = tonghoadon; }

    public long getHoahong() { return hoahong; }
    public void setHoahong(long hoahong) { this.hoahong = hoahong; }

    public long getTongluong() { return tongluong; }
    public void setTongluong(long tongluong) { this.tongluong = tongluong; }

    public String getTrangthai() { return trangthai; }
    public void setTrangthai(String trangthai) { this.trangthai = trangthai; }

    public Date getNgaychot() { return ngaychot; }
    public void setNgaychot(Date ngaychot) { this.ngaychot = ngaychot; }

    public Date getNgaythanhtoan() { return ngaythanhtoan; }
    public void setNgaythanhtoan(Date ngaythanhtoan) { this.ngaythanhtoan = ngaythanhtoan; }

    public boolean isDaChot() { return daChot; }
    public void setDaChot(boolean daChot) { this.daChot = daChot; }
}
