
package hailandbank.entities;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ActionLog {
    
    public static final String TABLE = "action_logs";
    
    private long id;
    
    private Action action;
            
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    
    
    
}
