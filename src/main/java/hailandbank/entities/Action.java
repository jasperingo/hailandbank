
package hailandbank.entities;

//import static hailandbank.entities.Entity.getConnection;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public enum Action {
    
    SIGN_UP(1),
        
    SIGN_IN(2),
        
    SIGN_OUT(3),
        
    FORGOT_PIN(4),
        
    RESET_PIN(5),
        
    UPDATE_ADDRESS(6),
        
    UPDATE_PIN(7),
    
    UPDATE_MERCHANT_NAME(8);
    
    
    public static final String TABLE = "actions";
    
    private long id;
    
    private String type;
    
    private String description;
    
    private Date createdAt;
    
    
    private Action(long id) {
        this.id = id;
    }
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    
    
    
}





