
package hailandbank.entities;


import hailandbank.utils.Helpers;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ActionLog extends Entity {
    
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
    
    
    public static void log(User user, Action action) throws SQLException {
        ActionLog log = new ActionLog();
        log.setUser(user);
        log.setAction(action);
        log.insert();
    }
    
    public void insert() throws SQLException {
        
        String sql = "INSERT INTO "+TABLE+" (user_id, action_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, getUser().getId());
            pstmt.setLong(2, getAction().getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not inserted for action log: "+rows);
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.unknown"));
        }
        
    }
    
    
    
    
}


