
package hailandbank.entities;


import java.util.Calendar;
import java.util.Date;


abstract public class UserToken extends Entity {
    
    public static final String ALLOWED_TOKEN_CHARS = 
            "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    protected long id;
    
    protected User user; 
    
    protected String token;
    
    protected Date expires;
    
    protected Date createdAt;
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public void generateExpiringDate(int duration) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MILLISECOND, duration);
        setExpires(c.getTime());
    }
    
}



