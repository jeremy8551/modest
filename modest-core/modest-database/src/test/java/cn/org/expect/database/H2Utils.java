package cn.org.expect.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Utils {

    // 创建内存数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "user", "");
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = getConnection();
        DatabaseTypeSet types = Jdbc.getTypeInfo(conn);
        System.out.println(types);

        // 创建测试表
        try (Statement stat = conn.createStatement()) {
            stat.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(255))");
            stat.execute("INSERT INTO users (id, name) VALUES (1, 'Alice')");
        }

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = 1")) {
            rs.next();
            System.out.println(rs.getString("name"));
        }

        conn.close();
    }

}
