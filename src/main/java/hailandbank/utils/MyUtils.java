
package hailandbank.utils;

import com.password4j.Hash;
import com.password4j.Password;
import hailandbank.entities.Account;
import hailandbank.entities.User;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;


public class MyUtils {
    
    
    public static LocalDateTime generateExpiringDate(int duration) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MILLISECOND, duration);
        c.getTime();
        TimeZone tz = c.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        return LocalDateTime.ofInstant(c.toInstant(), zid);
    }
    
    
    public static MediaType getRequestAcceptedMedia(HttpHeaders headers) {
        MediaType m = headers.getMediaType();
        
        List<MediaType> accepts = headers.getAcceptableMediaTypes();
        if (accepts!=null && accepts.size() > 0) {
            m = accepts.get(0);
        }
        
        return m;
    }
    
    public static String generateAccountNumber() {
        return "00"+MyUtils.generateToken(Account.NUMBER_LEN-2, Account.ALLOWED_NUMBER_CHARS);
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
    
    public static void exceptionLogger(Exception ex, String className) {
        Logger.getLogger(className).log(Level.SEVERE, null, ex);
    }
    
}


