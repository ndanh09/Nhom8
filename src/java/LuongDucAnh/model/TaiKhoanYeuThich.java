package model;

/** 1 tài khoản khách hàng kèm số sản phẩm đang yêu thích (trang admin "Yêu thích theo tài khoản"). */
public class TaiKhoanYeuThich {

    private String email;
    private String hoten;
    private String sodienthoai;
    private String trangthai;        // "Đang hoạt động" / "Đã khóa"
    private int soyeuthich;
    private String ngaygannhat;      // lần thêm yêu thích gần nhất

    public TaiKhoanYeuThich() {
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }

    public String getSodienthoai() { return sodienthoai; }
    public void setSodienthoai(String sodienthoai) { this.sodienthoai = sodienthoai; }

    public String getTrangthai() { return trangthai; }
    public void setTrangthai(String trangthai) { this.trangthai = trangthai; }

    public int getSoyeuthich() { return soyeuthich; }
    public void setSoyeuthich(int soyeuthich) { this.soyeuthich = soyeuthich; }

    public String getNgaygannhat() { return ngaygannhat; }
    public void setNgaygannhat(String ngaygannhat) { this.ngaygannhat = ngaygannhat; }
}
