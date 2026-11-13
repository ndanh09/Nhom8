package model;

public class quantrivien {

    private String hoten;
    private String email;
    private String sodienthoai;
    private String ngaydangky;
    private String matkhau;
    private String trangthai;

    public quantrivien() {
    }

    public quantrivien(String hoten, String email, String sodienthoai, String ngaydangky, String matkhau) {
        this.hoten = hoten;
        this.email = email;
        this.sodienthoai = sodienthoai;
        this.ngaydangky = ngaydangky;
        this.matkhau = matkhau;
    }

    public quantrivien(String hoten, String email, String sodienthoai, String ngaydangky, String matkhau, String trangthai) {
        this.hoten = hoten;
        this.email = email;
        this.sodienthoai = sodienthoai;
        this.ngaydangky = ngaydangky;
        this.matkhau = matkhau;
        this.trangthai = trangthai;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getNgaydangky() {
        return ngaydangky;
    }

    public void setNgaydangky(String ngaydangky) {
        this.ngaydangky = ngaydangky;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

}