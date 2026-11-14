package connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class dbconnect {
    public static final String HOSTNAME = "localhost";
    public static final String PORT = "1433";
    public static final String DBNAME = "datn";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "1";

    // Pool kết nối do Tomcat quản lý (khai báo trong web/META-INF/context.xml, tên jdbc/datn).
    // Mở kết nối SQL Server mới mỗi lần gọi rất chậm; dùng pool thì chỉ lần đầu mới chậm.
    private static volatile DataSource pool;
    private static volatile boolean poolDaThu;

    private static DataSource pool() {
        if (!poolDaThu) {
            synchronized (dbconnect.class) {
                if (!poolDaThu) {
                    try {
                        Context env = (Context) new InitialContext().lookup("java:comp/env");
                        pool = (DataSource) env.lookup("jdbc/datn");
                    } catch (Exception e) {
                        pool = null;   // không có pool (chạy ngoài Tomcat) -> dùng DriverManager như cũ
                    }
                    poolDaThu = true;
                }
            }
        }
        return pool;
    }

    /**
     * Get connection to MSSQL Server
     *
     * @return Connection
     */
    public static Connection getConnection() {
        DataSource ds = pool();
        if (ds != null) {
            try {
                return ds.getConnection();
            } catch (SQLException e) {
                e.printStackTrace(System.out);   // pool lỗi -> thử kết nối trực tiếp bên dưới
            }
        }

        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://" + HOSTNAME + ":" + PORT + ";"
                + "databaseName=" + DBNAME + ";encrypt=false";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(connectionUrl, USERNAME, PASSWORD);
        } // Handle any errors that may have occurred.
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getConnection());
    }
}
