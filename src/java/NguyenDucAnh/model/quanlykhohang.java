package model;

import java.sql.Date;

public class quanlykhohang {

    private int makho;
    private String masanpham;
    private int soluongnhap;
    private int soluongton;
    private String vitri;
    private Date ngaynhapkho;
    private String nhacungcap;

    public quanlykhohang() {
    }

    public quanlykhohang(int makho, String masanpham, int soluongnhap,
            int soluongton, String vitri, Date ngaynhapkho, String nhacungcap) {

        this.makho = makho;
        this.masanpham = masanpham;
        this.soluongnhap = soluongnhap;
        this.soluongton = soluongton;
        this.vitri = vitri;
        this.ngaynhapkho = ngaynhapkho;
        this.nhacungcap = nhacungcap;
    }

    public int getMakho() {
        return makho;
    }

    public void setMakho(int makho) {
        this.makho = makho;
    }

    public String getMasanpham() {
        return masanpham;
    }

    public void setMasanpham(String masanpham) {
        this.masanpham = masanpham;
    }

    public int getSoluongnhap() {
        return soluongnhap;
    }

    public void setSoluongnhap(int soluongnhap) {
        this.soluongnhap = soluongnhap;
    }

    public int getSoluongton() {
        return soluongton;
    }

    public void setSoluongton(int soluongton) {
        this.soluongton = soluongton;
    }

    public String getVitri() {
        return vitri;
    }

    public void setVitri(String vitri) {
        this.vitri = vitri;
    }

    public Date getNgaynhapkho() {
        return ngaynhapkho;
    }

    public void setNgaynhapkho(Date ngaynhapkho) {
        this.ngaynhapkho = ngaynhapkho;
    }

    public String getNhacungcap() {
        return nhacungcap;
    }

    public void setNhacungcap(String nhacungcap) {
        this.nhacungcap = nhacungcap;
    }

}