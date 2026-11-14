package model;

/**
 * 1 sản phẩm (áo) ở dạng đã GỘP theo mã gốc (magoc), kèm số liệu yêu thích.
 * Dùng chung cho: Top ở trang chủ, trang "Yêu thích" của khách, trang quản lý Top (luồng sản phẩm)
 * và trang xem yêu thích theo tài khoản (luồng quản trị viên).
 */
public class SanPhamYeuThich {

    private String magoc;
    private String masanpham;      // mã 1 size đại diện (ưu tiên size còn hàng) - dùng cho link chi tiết
    private String tensanpham;
    private String hinhanh;
    private String danhmuc;
    private int giasanpham;
    private String trangthai;      // '1' còn bán, '0' ngừng bán, '2' hết hàng (theo đại diện)
    private int tongsoluong;
    private int soluotyeuthich;    // số tài khoản đang yêu thích
    private boolean ghim;
    private int thutu;
    private boolean an;
    private int hang;              // hạng trong danh sách Top (1 = đứng đầu), 0 = không xếp hạng
    private boolean trongTop;      // đang được hiển thị ở mục Top trang chủ
    private String ngaythem;       // ngày khách thêm vào yêu thích (trang của khách / admin)

    public SanPhamYeuThich() {
    }

    public String getMagoc() { return magoc; }
    public void setMagoc(String magoc) { this.magoc = magoc; }

    public String getMasanpham() { return masanpham; }
    public void setMasanpham(String masanpham) { this.masanpham = masanpham; }

    public String getTensanpham() { return tensanpham; }
    public void setTensanpham(String tensanpham) { this.tensanpham = tensanpham; }

    public String getHinhanh() { return hinhanh; }
    public void setHinhanh(String hinhanh) { this.hinhanh = hinhanh; }

    public String getDanhmuc() { return danhmuc; }
    public void setDanhmuc(String danhmuc) { this.danhmuc = danhmuc; }

    public int getGiasanpham() { return giasanpham; }
    public void setGiasanpham(int giasanpham) { this.giasanpham = giasanpham; }

    public String getTrangthai() { return trangthai; }
    public void setTrangthai(String trangthai) { this.trangthai = trangthai; }

    public int getTongsoluong() { return tongsoluong; }
    public void setTongsoluong(int tongsoluong) { this.tongsoluong = tongsoluong; }

    public int getSoluotyeuthich() { return soluotyeuthich; }
    public void setSoluotyeuthich(int soluotyeuthich) { this.soluotyeuthich = soluotyeuthich; }

    public boolean isGhim() { return ghim; }
    public void setGhim(boolean ghim) { this.ghim = ghim; }

    public int getThutu() { return thutu; }
    public void setThutu(int thutu) { this.thutu = thutu; }

    public boolean isAn() { return an; }
    public void setAn(boolean an) { this.an = an; }

    public int getHang() { return hang; }
    public void setHang(int hang) { this.hang = hang; }

    public boolean isTrongTop() { return trongTop; }
    public void setTrongTop(boolean trongTop) { this.trongTop = trongTop; }

    public String getNgaythem() { return ngaythem; }
    public void setNgaythem(String ngaythem) { this.ngaythem = ngaythem; }
}
