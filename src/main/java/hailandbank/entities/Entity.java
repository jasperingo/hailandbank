
package hailandbank.entities;

import java.time.LocalDateTime;
import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
abstract public class Entity {
    
    protected long id;
    
    @JsonbProperty("updated_at")
    @XmlElement(name = "updated_at")
    protected LocalDateTime updatedAt;
    
    @JsonbProperty("created_at")
    @XmlElement(name = "created_at")
    protected LocalDateTime createdAt;
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
}

