
package hailandbank.utils;

import com.password4j.Hash;
import com.password4j.HashChecker;
import com.password4j.Password;
import hailandbank.entities.User;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class Helpers {
    
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
    
    
    private static final HashMap<String, String> lang = new HashMap<>();
    
    static {
        lang.put("app_name", "HailAndBank");
        
        lang.put("messages.input_fields_error", "Input fields have errors.");
        lang.put("messages.insert_user_error", "User failed to be inserted into the database.");
        
        lang.put("success.signup", "Welcome to HailAndBank.");
        lang.put("success.credentials", "Credientials are correct.");
        lang.put("success.forgotpin", "Pin reset request has been sent.");
        lang.put("success.pinreset", "Pin has been reset.");
        lang.put("success.address_updated", "Address has been updated.");
        lang.put("success.pin_updated", "Pin has been updated.");
        
        lang.put("errors.no_request_body", "Request do not have a body.");
        lang.put("errors.insert_account_error", "User acount failed to be inserted into the database.");
        lang.put("errors.insert_pin_reset_error", "User pin reset failed to be inserted into the database.");
        lang.put("errors.unknown", "An unknown error occured.");
        lang.put("errors.unauthenticated", "Authentication failed.");
        lang.put("errors.credentials", "Credientials are incorrect.");
        lang.put("errors.user_not_found", "User do not exist.");
        lang.put("errors.required_field", "Field is required.");
        lang.put("errors.user_type_invalid", "Type is invalid.");
        lang.put("errors.phone_number_invalid", "Phone number is invalid.");
        lang.put("errors.pin_invalid", "Pin is invalid.");
        lang.put("errors.pin_reset_token_invalid", "Pin reset token is invalid");
        lang.put("errors.pin_reset_failed", "Pin reset failed.");
        lang.put("errors.address_street_invalid", "Street address is invalid");
        lang.put("errors.address_city_invalid", "City is invalid");
        lang.put("errors.address_state_invalid", "State is invalid");
        lang.put("errors.address_update_failed", "Address failed to be updated in the database.");
        
    }
    
    
    public static String __(String s) {
        return (String) lang.getOrDefault(s, s);
    }
    
    
}


