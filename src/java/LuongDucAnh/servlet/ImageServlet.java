package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Phục vụ ảnh cho toàn bộ trang qua đường dẫn /image/<tên file>.
 *
 * Ưu tiên tìm trong thư mục upload bền vững (util.ImageStorage) - nơi chứa
 * ảnh do người bán tải lên, KHÔNG bị mất khi rebuild/deploy lại project.
 * Nếu không thấy, mới rơi về các ảnh mặc định có sẵn trong project
 * (web/image, ví dụ logo.png, banner.png, mu.jpg...).
 */
@WebServlet(name = "ImageServlet", urlPatterns = {"/image/*"})
public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Bỏ dấu "/" ở đầu, và chặn ../ để tránh truy cập ra ngoài thư mục ảnh
        String filename = pathInfo.substring(1).replace("\\", "/");
        if (filename.contains("..") || filename.contains("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File file = new File(util.ImageStorage.getUploadDir(), filename);

        if (!file.exists() || !file.isFile()) {
            // Rơi về ảnh mặc định đóng gói sẵn trong project (web/image)
            String staticPath = getServletContext().getRealPath("/image/" + filename);
            file = (staticPath != null) ? new File(staticPath) : null;
        }

        if (file == null || !file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = layContentTypeMacDinh(file.getName());
        }
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());

        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int soByteDoc;
            while ((soByteDoc = in.read(buffer)) != -1) {
                out.write(buffer, 0, soByteDoc);
            }
        }
    }

    // Dự phòng cho các Tomcat cũ chưa có sẵn mime-type của webp trong cấu hình
    private String layContentTypeMacDinh(String tenFile) {
        String ten = tenFile.toLowerCase();
        if (ten.endsWith(".webp")) return "image/webp";
        if (ten.endsWith(".png")) return "image/png";
        if (ten.endsWith(".jpg") || ten.endsWith(".jpeg")) return "image/jpeg";
        if (ten.endsWith(".gif")) return "image/gif";
        if (ten.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
