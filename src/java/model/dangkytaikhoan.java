package model;

public class dangkytaikhoan {
    private String email;
    private String hoten;
    private String matkhau;
    private String sodienthoai; // Thêm thuộc tính mới
    private String diachi; // Địa chỉ nhận hàng mặc định (lưu để tự điền lần thanh toán sau)
    private String trangthai; // Trạng thái tài khoản: "Đang hoạt động" / "Đã khóa"

    public dangkytaikhoan() {
    }

    // Cập nhật Constructor đầy đủ tham số
    public dangkytaikhoan(String email, String hoten, String matkhau, String sodienthoai) {
        this.email = email;
        this.hoten = hoten;
        this.matkhau = matkhau;
        this.sodienthoai = sodienthoai;
    }

    public dangkytaikhoan(String email, String hoten, String matkhau, String sodienthoai, String diachi) {
        this.email = email;
        this.hoten = hoten;
        this.matkhau = matkhau;
        this.sodienthoai = sodienthoai;
        this.diachi = diachi;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
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

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }
}