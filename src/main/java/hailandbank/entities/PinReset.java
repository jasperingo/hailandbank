
package hailandbank.entities;


import hailandbank.utils.Helpers;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class PinReset extends UserToken {
    
    public static final String TABLE = "pin_resets";
    
    public static final int TOKEN_LEN = 6;
    
    public static final int TOKEN_DURATION = (1000*60*15); //15 minutes
    
    
    public void generateExpiringDate() {
        generateExpiringDate(TOKEN_DURATION);
    }
    
    public void insert() throws SQLException {
       
        String sql = 
                "INSERT INTO "+TABLE+" "
                +"(user_id, "
                + "token, "
                + "expires) "
                + "VALUES (?, ?, ?)";
        
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            delete();
            
            pstmt.setLong(1, getUser().getId());
            pstmt.setString(2, getToken());
            pstmt.setObject(3, getExpires());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not inserted for pin reset: "+rows);
            
            ActionLog.log(getUser(), Action.FORGOT_PIN);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.insert_pin_reset"));
        }
    }
    
    public void delete() throws SQLException {
        
        String deleteSql = "DELETE FROM "+TABLE+" WHERE user_id = ?";
        
        PreparedStatement deletePstmt = getConnection().prepareStatement(deleteSql);
            
        deletePstmt.setLong(1, getUser().getId());
            
        deletePstmt.executeUpdate();
    }
    
    public static long findUserIdWhenNotExpired(String token, String userPhoneNumber) throws SQLException, NotFoundException  {
        
        String sql = "SELECT a.user_id "
                + "FROM "+TABLE+" AS a INNER JOIN "+User.TABLE+" AS b "
                + "ON a.user_id = b.id "
                + "WHERE a.token = ? AND b.phone_number = ? AND TIMESTAMP(a.expires) > NOW()";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            pstmt.setString(2, userPhoneNumber);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return result.getLong("user_id");
            } else {
                throw new NotFoundException(__("errors.reset_pin_token"));
            }
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.unknown"));
        }
    }
    
    
    
}



