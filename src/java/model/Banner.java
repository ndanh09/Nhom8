package model;

import java.sql.Timestamp;

/**
 * Banner hiển thị ở trang chủ (vitri = TRANGCHU) hoặc trang sản phẩm (vitri = SANPHAM).
 * Admin quản lý ở trang /quanlybanner.
 */
public class Banner {

    public static final String TRANG_CHU = "TRANGCHU";
    public static final String SAN_PHAM = "SANPHAM";

    private int id;
    private String vitri;
    private String nhan;      // dòng nhỏ phía trên tiêu đề (VD: LA LIGA · 2026-27)
    private String tieude;
    private String mota;
    private String hinhanh;   // tên file ảnh (phục vụ qua /image/<tên file>)
    private String lienket;   // đường dẫn nội bộ (VD: nguoidungservlet) hoặc http(s)://...
    private String nuttext;   // chữ trên nút
    private int thutu;        // nhỏ hiện trước
    private boolean hienthi;
    private Timestamp ngaytao;

    public Banner() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVitri() { return vitri; }
    public void setVitri(String vitri) { this.vitri = vitri; }

    public String getNhan() { return nhan; }
    public void setNhan(String nhan) { this.nhan = nhan; }

    public String getTieude() { return tieude; }
    public void setTieude(String tieude) { this.tieude = tieude; }

    public String getMota() { return mota; }
    public void setMota(String mota) { this.mota = mota; }

    public String getHinhanh() { return hinhanh; }
    public void setHinhanh(String hinhanh) { this.hinhanh = hinhanh; }

    public String getLienket() { return lienket; }
    public void setLienket(String lienket) { this.lienket = lienket; }

    public String getNuttext() { return nuttext; }
    public void setNuttext(String nuttext) { this.nuttext = nuttext; }

    public int getThutu() { return thutu; }
    public void setThutu(int thutu) { this.thutu = thutu; }

    public boolean isHienthi() { return hienthi; }
    public void setHienthi(boolean hienthi) { this.hienthi = hienthi; }

    public Timestamp getNgaytao() { return ngaytao; }
    public void setNgaytao(Timestamp ngaytao) { this.ngaytao = ngaytao; }

    /**
     * Link đã được làm sạch để đưa vào href: rỗng -> "", http(s) giữ nguyên,
     * còn lại coi là đường dẫn nội bộ (JSP sẽ ghép contextPath ở phía trước).
     */
    public boolean isLienketNgoai() {
        return lienket != null && (lienket.startsWith("http://") || lienket.startsWith("https://"));
    }
}
