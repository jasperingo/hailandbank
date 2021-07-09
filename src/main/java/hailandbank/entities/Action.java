
package hailandbank.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Action {
    
    public static final String TABLE = "actions";
    
    private long id;
    
    private String type;
    
    private String description;
    
    private Date createdAt;
    
    
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
