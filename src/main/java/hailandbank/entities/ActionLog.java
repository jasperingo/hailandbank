
package hailandbank.entities;


import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "action_log")
public class ActionLog extends Entity {
    
    public static final String TABLE = "action_logs";
    
    private Action action;
    
    private User user;

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


