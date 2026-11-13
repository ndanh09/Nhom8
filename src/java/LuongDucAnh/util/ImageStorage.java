package util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Nơi lưu ảnh sản phẩm do người bán upload.
 *
 * QUAN TRỌNG: Thư mục này KHÔNG được đặt trong thư mục web đã deploy
 * (getServletContext().getRealPath("")), vì thư mục đó bị xoá và tạo lại
 * từ source code mỗi khi Clean & Build / deploy lại project, khiến mọi
 * ảnh đã upload trước đó bị mất dù dữ liệu trong SQL vẫn còn.
 *
 * Thay vào đó, ảnh được lưu vào một thư mục cố định NẰM NGOÀI thư mục
 * webapp, dựa trên thư mục cài đặt Tomcat (catalina.base) nếu có, để nó
 * tồn tại độc lập và không bị build/deploy đụng vào.
 */
public class ImageStorage {

    private static final String THU_MUC_CON = "lodi_uploads" + File.separator + "image";

    // Chỉ cho phép các định dạng ảnh thông thường. KHÔNG cho svg vì svg có thể
    // chứa script và ImageServlet phục vụ file cùng origin với website (XSS).
    private static final Set<String> DUOI_ANH_HOP_LE = new HashSet<>(
            Arrays.asList("png", "jpg", "jpeg", "gif", "webp", "bmp", "avif"));

    /**
     * Lưu file ảnh upload vào thư mục upload bền vững một cách an toàn và trả về
     * TÊN FILE đã lưu (để ghi vào DB).
     *
     * FIX: trước đây servlet ghép thẳng getSubmittedFileName() do trình duyệt
     * gửi lên vào đường dẫn ghi file, nên tên kiểu "..\\..\\x.jsp" hoặc đường
     * dẫn tuyệt đối có thể ghi file ra ngoài thư mục upload (path traversal),
     * và không hề kiểm tra phần mở rộng. Nay: chỉ lấy tên file cuối, thay ký
     * tự lạ bằng "_", bắt buộc đuôi ảnh hợp lệ và kiểm tra đường dẫn cuối cùng
     * vẫn nằm trong thư mục upload.
     *
     * @throws IllegalArgumentException nếu file không phải ảnh hợp lệ
     */
    public static String luuAnh(Part part, String tienTo) throws IOException {

        String goc = part.getSubmittedFileName();
        if (goc == null) {
            throw new IllegalArgumentException("Tên file ảnh không hợp lệ!");
        }

        goc = goc.replace('\\', '/');
        goc = goc.substring(goc.lastIndexOf('/') + 1);

        int cham = goc.lastIndexOf('.');
        String duoi = cham >= 0 ? goc.substring(cham + 1).toLowerCase(Locale.ROOT) : "";
        if (!DUOI_ANH_HOP_LE.contains(duoi)) {
            throw new IllegalArgumentException(
                    "Chỉ chấp nhận ảnh định dạng png, jpg, jpeg, gif, webp, bmp hoặc avif!");
        }

        String ten = goc.substring(0, cham).replaceAll("[^A-Za-z0-9_-]", "_");
        if (ten.length() > 60) {
            ten = ten.substring(0, 60);
        }
        if (ten.isEmpty()) {
            ten = "anh";
        }

        String tenFile = (tienTo == null ? "" : tienTo) + System.currentTimeMillis() + "_" + ten + "." + duoi;

        File dir = getUploadDir();
        File dich = new File(dir, tenFile);
        if (!dich.getCanonicalPath().startsWith(dir.getCanonicalPath() + File.separator)) {
            throw new IllegalArgumentException("Đường dẫn file không hợp lệ!");
        }

        part.write(dich.getAbsolutePath());
        return tenFile;
    }

    public static File getUploadDir() {

        String base = System.getProperty("catalina.base");

        if (base == null || base.trim().isEmpty()) {
            base = System.getProperty("user.home");
        }

        File dir = new File(base, THU_MUC_CON);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }
}
