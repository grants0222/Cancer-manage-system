package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCutil {
	public static Connection getConnection() {
		FileInputStream fileInputStream =null;
		Connection connection=null;//jdbcLab\main\resource
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			fileInputStream = new FileInputStream("src/resource/jdbc.properties");
			Properties properties = new Properties();
			properties.load(fileInputStream);
			String user = properties.getProperty("user");
			String password = properties.getProperty("password");
			String url = properties.getProperty("url");
			connection = DriverManager.getConnection(url,user,password);
			boolean status = !connection.isClosed();
			System.out.println("連線狀態:"+status);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return connection;
	}
	
	public static void closeResource(Connection connection) {
		try {
			if(connection!=null) {
				connection.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void closeResource(Connection connection,Statement statment) {
		try {
			if(connection!=null) {
				connection.close();
			}
			if(statment!=null) {
				statment.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void closeResource(Connection connection,Statement statment,ResultSet resultSet) {
		try {
			if(connection!=null) {
				connection.close();
			}
			if(statment!=null) {
				statment.close();
			}
			if(resultSet!=null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
