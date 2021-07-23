
package hailandbank.db;

import hailandbank.entities.Action;
import hailandbank.entities.ActionLog;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ws.rs.InternalServerErrorException;


public class ActionLogDb extends Database {
    
    
     public static void log(User user, Action action) throws InternalServerErrorException {
        ActionLog log = new ActionLog();
        log.setUser(user);
        log.setAction(action);
        insert(log);
    }
    
    public static void insert(ActionLog actionLog) throws InternalServerErrorException {
        
        String sql = "INSERT INTO "+ActionLog.TABLE+" (user_id, action_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, actionLog.getUser().getId());
            
            pstmt.setLong(2, actionLog.getAction().getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for action log: "+rows);
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, ActionLogDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
        
    }
    
    
}
