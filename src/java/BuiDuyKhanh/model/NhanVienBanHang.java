package model;

public class NhanVienBanHang {

    private String email;
    private String hoten;
    private String matkhau;
    private String sodienthoai;
    private String ngaydangky;
    private String trangthai;   // Đang hoạt động / Khóa
    private String anhcccd;     // Tên file ảnh căn cước công dân

    public NhanVienBanHang() {
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }

    public String getMatkhau() { return matkhau; }
    public void setMatkhau(String matkhau) { this.matkhau = matkhau; }

    public String getSodienthoai() { return sodienthoai; }
    public void setSodienthoai(String sodienthoai) { this.sodienthoai = sodienthoai; }

    public String getNgaydangky() { return ngaydangky; }
    public void setNgaydangky(String ngaydangky) { this.ngaydangky = ngaydangky; }

    public String getTrangthai() { return trangthai; }
    public void setTrangthai(String trangthai) { this.trangthai = trangthai; }

    public String getAnhcccd() { return anhcccd; }
    public void setAnhcccd(String anhcccd) { this.anhcccd = anhcccd; }
}
