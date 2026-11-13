package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Một lần thanh toán = MỘT đơn hàng (madonhang), gồm nhiều dòng sản phẩm
 * (mỗi dòng là 1 bản ghi bảng trangthai). Lớp này chỉ dùng để HIỂN THỊ:
 * gom các dòng cùng madonhang lại để khách thấy đúng 1 mã đơn.
 */
public class NhomDonHang {

    private final int madonhang;
    private final List<TrangThai> dong = new ArrayList<>();

    public NhomDonHang(int madonhang) {
        this.madonhang = madonhang;
    }

    public void them(TrangThai tt) {
        dong.add(tt);
    }

    public int getMadonhang() {
        return madonhang;
    }

    /**
     * Mã hiển thị cho khách. Dữ liệu cũ không có madonhang (=0) thì dùng mã dòng.
     */
    public int getMaHienThi() {
        if (madonhang > 0 || dong.isEmpty()) {
            return madonhang;
        }
        return dong.get(0).getMatrangthai();
    }

    public List<TrangThai> getDong() {
        return dong;
    }

    public int getSoDong() {
        return dong.size();
    }

    public Date getNgaymua() {
        return dong.isEmpty() ? null : dong.get(0).getNgaymua();
    }

    /**
     * Tổng tiền đơn = tổng các dòng còn hiệu lực (không tính dòng đã hủy).
     * Nếu mọi dòng đều đã hủy thì trả về tổng ban đầu của cả đơn.
     */
    public int getTongTien() {
        int conHieuLuc = 0;
        int tatCa = 0;
        for (TrangThai tt : dong) {
            int tien = tt.getGiaban() * tt.getSoluong();
            tatCa += tien;
            if (tt.getTinhtrang() != 3) {
                conHieuLuc += tien;
            }
        }
        boolean tatCaDaHuy = true;
        for (TrangThai tt : dong) {
            if (tt.getTinhtrang() != 3) {
                tatCaDaHuy = false;
                break;
            }
        }
        return tatCaDaHuy ? tatCa : conHieuLuc;
    }
}
