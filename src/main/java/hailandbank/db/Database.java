
package hailandbank.db;


import hailandbank.utils.MyUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    
    private static Connection connection = null;

    protected static Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = createConnection();
        }
        
        return connection;
    }
    
    private static Connection createConnection() throws SQLException {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            MyUtils.exceptionLogger(ex, Database.class.getName());
        }

        Properties p = new Properties();
        p.setProperty("user", "root");
        p.setProperty("password", "6509");
        p.setProperty("useSSL", "false");

        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hailandbank", p);
    }
    
}



