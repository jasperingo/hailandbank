
package hailandbank.utils;

import com.password4j.Hash;
import com.password4j.Password;
import hailandbank.entities.User;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;


public class Helpers {
    
    private static final ResourceBundle lang = ResourceBundle.getBundle("hailandbank.locales.AppStrings");
    
    
    public static String __(String s) {
        try {
            return lang.getString(s);
        } catch (MissingResourceException e) {
            return s;
        }
    }
    
    public static Connection getDBConnection() {
        
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Properties p = new Properties();
            p.setProperty("user", "root");
            p.setProperty("password", "6332");
            p.setProperty("useSSL", "false");
            
            final Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hailandbank", p);
            
            return conn;
            
        } catch (SQLException | ClassNotFoundException e) {
            stackTracer(e);
            return null;
        }
        
    }
    
    
    public static void hashPassword(User user) {
        Hash hash = Password.hash(user.getPin()).withBCrypt();
        user.setPin(hash.getResult());
    }
    
    public static String hashPassword(String plain) {
        Hash hash = Password.hash(plain).withBCrypt();
        return hash.getResult();
    }
    
    public static boolean comparePassword(String plain, String hash) {
        return Password.check(plain, hash).withBCrypt();
    }
    
    public static String generateToken(int len, String chars) {
        SecureRandom rand = new SecureRandom();
        StringBuilder builder = new StringBuilder(len);
        for (int i=0; i<len; i++) 
            builder.append(chars.charAt(rand.nextInt(chars.length())));
        return builder.toString();
    }
    
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static void stackTracer(Exception ex) {
        ex.printStackTrace();
    }
    
}


