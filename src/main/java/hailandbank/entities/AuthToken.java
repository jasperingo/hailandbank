
package hailandbank.entities;


import hailandbank.utils.Helpers;
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
    
    public static final int TOKEN_DURATION = (1000*60*60*12); //12 hours
    
    
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
            
            if (rows == 0) throw new SQLException("Rows is not inserted for auth token: "+rows);
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.insert_auth_token"));
        }
    }
    
    public void insertWithAction() throws SQLException {
        
        try {
            
           getConnection().setAutoCommit(false);
           
           insert();
           
           ActionLog.log(getUser(), Action.SIGN_IN);
           
           getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.insert_auth_token"));
        }
        
    }
    
    public static AuthToken form(ResultSet result, String token) throws SQLException {
        AuthToken auth = new AuthToken();
        auth.setId(result.getLong("auth_id"));
        auth.setToken(token);
        return auth;
    }
    
    public static User findUserWhenNotExpired(String token) throws SQLException, NotFoundException {
        
        String sql = String.format("SELECT %s, a.id AS auth_id "
                + "FROM %S AS a INNER JOIN %S AS b "
                + "ON a.user_id = b.id "
                + "WHERE a.token = ? AND TIMESTAMP(a.expires) > NOW()", 
                User.TABLE_COLUMNS, TABLE, User.TABLE);
        
        return findUser(sql, token, "");
    }
    
    public static Merchant findMerchantWhenNotExpired(String token) throws SQLException, NotFoundException {
        
        String sql = String.format("SELECT %s, %s, a.id AS auth_id "
                + "FROM %s AS a INNER JOIN %s "
                + "ON a.user_id = merchants.user_id "
                + "INNER JOIN %s "
                + "ON merchants.user_id = users.id "
                + "WHERE a.token = ? AND TIMESTAMP(a.expires) > NOW()", 
                User.TABLE_COLUMNS, Merchant.TABLE_COLUMNS, TABLE, Merchant.TABLE, User.TABLE);
       
        return (Merchant)findUser(sql, token, User.TYPE_MERCHANT);
    }
    
    public static Customer findCustomerWhenNotExpired(String token) throws SQLException, NotFoundException {
        
        String sql = String.format("SELECT %s, %s, a.id AS auth_id "
                + "FROM %s AS a INNER JOIN %s "
                + "ON a.user_id = customers.user_id "
                + "INNER JOIN %s "
                + "ON customers.user_id = users.id "
                + "WHERE a.token = ? AND TIMESTAMP(a.expires) > NOW()", 
                User.TABLE_COLUMNS, Customer.TABLE_COLUMNS, TABLE, Customer.TABLE, User.TABLE);
        
        return (Customer)findUser(sql, token, User.TYPE_CUSTOMER);
    }
    
    private static User findUser(String sql, String token, String type) throws SQLException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                
                User user;
                
                switch (type) {
                    case User.TYPE_CUSTOMER :
                        user = Customer.form(result);
                        break;
                    case User.TYPE_MERCHANT :
                        user = Merchant.form(result);
                        break;
                    default :
                        user = User.form(result);
                }
                
                user.setAuthToken(form(result, token));
                return user;
            } else {
                throw new NotFoundException(__("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.unknown"));
        }
    }
    
    
}






