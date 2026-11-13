package model;

import java.sql.Date;

public class PhieuGiamGia {

    private String magiamgia;
    private String tenphieu;
    private int loaigiam;
    private int giatrigiam;
    private int giamtoida;
    private int dieukien;
    private int soluong;
    private Date ngaybatdau;
    private Date ngayketthuc;
    private boolean trangthai;

    public PhieuGiamGia() {
    }

    public PhieuGiamGia(String magiamgia, String tenphieu, int loaigiam,
            int giatrigiam, int giamtoida, int dieukien,
            int soluong, Date ngaybatdau,
            Date ngayketthuc, boolean trangthai) {

        this.magiamgia = magiamgia;
        this.tenphieu = tenphieu;
        this.loaigiam = loaigiam;
        this.giatrigiam = giatrigiam;
        this.giamtoida = giamtoida;
        this.dieukien = dieukien;
        this.soluong = soluong;
        this.ngaybatdau = ngaybatdau;
        this.ngayketthuc = ngayketthuc;
        this.trangthai = trangthai;
    }

    public String getMagiamgia() {
        return magiamgia;
    }

    public void setMagiamgia(String magiamgia) {
        this.magiamgia = magiamgia;
    }

    public String getTenphieu() {
        return tenphieu;
    }

    public void setTenphieu(String tenphieu) {
        this.tenphieu = tenphieu;
    }

    public int getLoaigiam() {
        return loaigiam;
    }

    public void setLoaigiam(int loaigiam) {
        this.loaigiam = loaigiam;
    }

    public int getGiatrigiam() {
        return giatrigiam;
    }

    public void setGiatrigiam(int giatrigiam) {
        this.giatrigiam = giatrigiam;
    }

    public int getGiamtoida() {
        return giamtoida;
    }

    public void setGiamtoida(int giamtoida) {
        this.giamtoida = giamtoida;
    }

    public int getDieukien() {
        return dieukien;
    }

    public void setDieukien(int dieukien) {
        this.dieukien = dieukien;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public Date getNgaybatdau() {
        return ngaybatdau;
    }

    public void setNgaybatdau(Date ngaybatdau) {
        this.ngaybatdau = ngaybatdau;
    }

    public Date getNgayketthuc() {
        return ngayketthuc;
    }

    public void setNgayketthuc(Date ngayketthuc) {
        this.ngayketthuc = ngayketthuc;
    }

    public boolean isTrangthai() {
        return trangthai;
    }

    public void setTrangthai(boolean trangthai) {
        this.trangthai = trangthai;
    }

}