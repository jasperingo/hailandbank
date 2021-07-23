
package hailandbank.db;

import hailandbank.entities.Action;
import hailandbank.entities.PinReset;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;


public class PinResetDb extends Database {
    
    public static void insert(PinReset pinReset) throws InternalServerErrorException {
       
        String sql = 
                "INSERT INTO "+PinReset.TABLE+" "
                +"(user_id, "
                + "token, "
                + "expires) "
                + "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            delete(pinReset);
            
            pstmt.setLong(1, pinReset.getUser().getId());
            pstmt.setString(2, pinReset.getToken());
            pstmt.setObject(3, pinReset.getExpires());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for pin reset: "+rows);
            
            ActionLogDb.log(pinReset.getUser(), Action.FORGOT_PIN);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                 MyUtils.exceptionLogger(ex1, PinResetDb.class.getName());
            }
            MyUtils.exceptionLogger(ex, PinResetDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_pin_reset"));
        }
    }
    
    public static void delete(PinReset pinReset) throws SQLException {
        
        String deleteSql = "DELETE FROM "+PinReset.TABLE+" WHERE user_id = ?";
        
        PreparedStatement deletePstmt = getConnection().prepareStatement(deleteSql);
            
        deletePstmt.setLong(1, pinReset.getUser().getId());
            
        deletePstmt.executeUpdate();
    }
    
    public static long findUserIdWhenNotExpired(String token, String userPhoneNumber) 
            throws InternalServerErrorException, NotFoundException  {
        
        String sql = "SELECT a.user_id "
                + "FROM "+PinReset.TABLE+" AS a INNER JOIN "+User.TABLE+" AS b "
                + "ON a.user_id = b.id "
                + "WHERE a.token = ? AND b.phone_number = ? AND TIMESTAMP(a.expires) > NOW()";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            pstmt.setString(2, userPhoneNumber);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return result.getLong("user_id");
            } else {
                throw new NotFoundException(AppStrings.get("errors.reset_pin_token"));
            }
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, PinResetDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    
}


