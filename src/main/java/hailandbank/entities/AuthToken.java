
package hailandbank.entities;


import static hailandbank.entities.Entity.getConnection;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class AuthToken extends UserToken {
    
    public static final String TABLE = "auth_tokens";
    
    public static final int TOKEN_LEN = 20;
    
    public static final int TOKEN_DURATION = (1000*60*60*24)*7; //days
    
    
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
            
            pstmt.setLong(1, getUser().getId());
            pstmt.setString(2, getToken());
            pstmt.setObject(3, getExpires());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0)
                throw new SQLException();
            
        } catch (SQLException ex) {
            throw new SQLException(__("messages.insert_account_error"));
        }
    }
    
    public void delete() throws SQLException {
        
        String sql = "DELETE FROM "+TABLE+" WHERE id = ?";
        
        try {
            
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            
            pstmt.setLong(1, getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException();
            
        } catch (SQLException ex) {
            throw new SQLException(__("errors.unknown"));
        }
    }
    
    
    
    public static User findUserWhenNotExpired(String token) throws SQLException, NotFoundException {
        
        String sql = "SELECT "+User.USER_COLUMNS_FOR_JOINS+", a.id AS auth_id "
                + "FROM "+TABLE+" AS a INNER JOIN "+User.TABLE+" AS b "
                + "ON a.user_id = b.id "
                + "WHERE a.token = ? AND TIMESTAMP(a.expires) > NOW()";
        
        try {
            
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            
            pstmt.setString(1, token);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                User user = User.form(result);
                AuthToken auth = new AuthToken();
                auth.setId(result.getLong("auth_id"));
                auth.setToken(token);
                user.setAuthToken(auth);
                return user;
            } else {
                throw new NotFoundException(__("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            throw new SQLException(__("errors.unknown"));
        }
    }
    
    
}






