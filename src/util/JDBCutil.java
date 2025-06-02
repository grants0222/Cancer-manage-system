package util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCutil {
    public static Connection getConnection() {
        Connection connection = null;
        InputStream inputStream = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // 用 ClassLoader 讀取 classpath 下的 jdbc.properties
            inputStream = JDBCutil.class.getClassLoader().getResourceAsStream("resource/jdbc.properties");

            if (inputStream == null) {
                throw new RuntimeException("找不到 jdbc.properties，請確認 resource 資料夾已正確打包進 JAR");
            }

            Properties properties = new Properties();
            properties.load(inputStream);
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            String url = properties.getProperty("url");

            connection = DriverManager.getConnection(url, user, password);
            boolean status = !connection.isClosed();
            System.out.println("連線狀態: " + status);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static void closeResource(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResource(Connection connection, Statement statement) {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResource(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
