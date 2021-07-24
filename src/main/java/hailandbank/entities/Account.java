
package hailandbank.entities;


import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "account")
public class Account extends Entity {
    
    public static final String TABLE = "accounts";
    
    public static final String TYPE_SAVINGS = "savings";
    
    public static final int NUMBER_LEN = 10;
    
    public static final String ALLOWED_NUMBER_CHARS = "1234567890";
    
    private User user;
    
    private String type;
    
    private String number;
    
    

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    
}

