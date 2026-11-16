package model;

/**
 * Số liệu thống kê đánh giá. DÙNG CHUNG cho trang quản trị và trang sản phẩm công khai
 * nên hai nơi luôn tính điểm theo cùng một nguyên tắc:
 *
 *  - Đánh giá ĐÃ ẨN không tham gia bất kỳ thống kê công khai nào.
 *  - tongLuotDanhGia   = số đánh giá đang hiển thị (gồm cả loại chỉ có nhận xét).
 *  - tongLuotChamSao   = số đánh giá đang hiển thị CÓ chấm sao.
 *  - diemTrungBinh     = tổng số sao đang hiển thị / tongLuotChamSao (0 nếu chưa có ai chấm sao).
 */
public class ThongKeDanhGia {

    private int tongLuotDanhGia;
    private int tongLuotChamSao;
    private long tongSoSao;
    private int soDaAn;
    private final int[] soLuongTheoSao = new int[6];   // chỉ số 1..5 được dùng

    public int getTongLuotDanhGia() { return tongLuotDanhGia; }
    public void setTongLuotDanhGia(int v) { this.tongLuotDanhGia = v; }

    public int getTongLuotChamSao() { return tongLuotChamSao; }
    public void setTongLuotChamSao(int v) { this.tongLuotChamSao = v; }

    public long getTongSoSao() { return tongSoSao; }
    public void setTongSoSao(long v) { this.tongSoSao = v; }

    public int getSoDaAn() { return soDaAn; }
    public void setSoDaAn(int v) { this.soDaAn = v; }

    public int[] getSoLuongTheoSao() { return soLuongTheoSao; }

    public void setSoLuongSao(int sao, int soLuong) {
        if (sao >= 1 && sao <= 5) {
            soLuongTheoSao[sao] = soLuong;
        }
    }

    public int getSao1() { return soLuongTheoSao[1]; }
    public int getSao2() { return soLuongTheoSao[2]; }
    public int getSao3() { return soLuongTheoSao[3]; }
    public int getSao4() { return soLuongTheoSao[4]; }
    public int getSao5() { return soLuongTheoSao[5]; }

    /** Điểm trung bình làm tròn 1 chữ số thập phân; 0 nếu chưa có đánh giá nào có sao đang hiển thị. */
    public double getDiemTrungBinh() {
        if (tongLuotChamSao <= 0) {
            return 0;
        }
        return Math.round(tongSoSao * 10.0 / tongLuotChamSao) / 10.0;
    }

    /** Tỉ lệ % (0..100) của 1 mức sao trên tổng lượt chấm sao - dùng vẽ thanh phân bố. */
    public int phanTram(int sao) {
        if (tongLuotChamSao <= 0 || sao < 1 || sao > 5) {
            return 0;
        }
        return (int) Math.round(soLuongTheoSao[sao] * 100.0 / tongLuotChamSao);
    }

    public int getPt1() { return phanTram(1); }
    public int getPt2() { return phanTram(2); }
    public int getPt3() { return phanTram(3); }
    public int getPt4() { return phanTram(4); }
    public int getPt5() { return phanTram(5); }
}
