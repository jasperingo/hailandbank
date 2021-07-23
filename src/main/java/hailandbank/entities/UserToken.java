
package hailandbank.entities;


import java.time.LocalDateTime;


abstract public class UserToken extends Entity {
    
    public static final String ALLOWED_TOKEN_CHARS = 
            "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    protected User user; 
    
    protected String token;
    
    protected LocalDateTime expires;
    
    
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

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }
        
}

