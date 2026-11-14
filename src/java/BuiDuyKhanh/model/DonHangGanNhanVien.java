package model;

import java.util.Date;

/**
 * 1 đơn hàng (gộp theo madonhang) kèm nhân viên bán hàng đang phụ trách đơn đó.
 * Dùng cho bảng "Gán đơn hàng cho nhân viên" ở trang lương nhân viên bán hàng.
 */
public class DonHangGanNhanVien {

    private int madonhang;
    private String hoten;           // tên khách đặt hàng
    private Date ngaymua;
    private long tongdon;           // tổng giá trị các sản phẩm CHƯA bị hủy
    private long danhan;            // phần giá trị đã ở trạng thái "Đã nhận" (phần được tính hoa hồng)
    private String emailNhanVien;   // nhân viên phụ trách (null = chưa gán)

    public DonHangGanNhanVien() {
    }

    public int getMadonhang() { return madonhang; }
    public void setMadonhang(int madonhang) { this.madonhang = madonhang; }

    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }

    public Date getNgaymua() { return ngaymua; }
    public void setNgaymua(Date ngaymua) { this.ngaymua = ngaymua; }

    public long getTongdon() { return tongdon; }
    public void setTongdon(long tongdon) { this.tongdon = tongdon; }

    public long getDanhan() { return danhan; }
    public void setDanhan(long danhan) { this.danhan = danhan; }

    public String getEmailNhanVien() { return emailNhanVien; }
    public void setEmailNhanVien(String emailNhanVien) { this.emailNhanVien = emailNhanVien; }
}
