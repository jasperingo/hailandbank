
package hailandbank.db;

import hailandbank.entities.Action;
import hailandbank.entities.AuthToken;
import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;



public class AuthTokenDb extends Database {
    
    
    public static int insertIn(AuthToken authToken) throws SQLException {
       
        String sql = 
                "INSERT INTO "+AuthToken.TABLE+" "
                +"(user_id, "
                + "token, "
                + "expires) "
                + "VALUES (?, ?, ?)";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
            
        pstmt.setLong(1, authToken.getUser().getId());
        pstmt.setString(2, authToken.getToken());
        pstmt.setObject(3, authToken.getExpires());
            
        return pstmt.executeUpdate();
    }
    
    public static void insert(AuthToken authToken) throws InternalServerErrorException {
        
        try {
            
            int rows = insertIn(authToken);
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for auth token: "+rows);
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, AuthTokenDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_auth_token"));
        }
    }
    
    public static void insertWithAction(AuthToken authToken) throws InternalServerErrorException {
        
        try {
            
            getConnection().setAutoCommit(false);
           
            int rows = insertIn(authToken);
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for auth token: "+rows);
           
            ActionLogDb.log(authToken.getUser(), Action.SIGN_IN);
           
            getConnection().commit();
           
        } catch (SQLException ex) {
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                 MyUtils.exceptionLogger(ex1, AuthTokenDb.class.getName());
            }
            MyUtils.exceptionLogger(ex, AuthTokenDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_auth_token"));
        }
        
    }
    
    public static AuthToken form(ResultSet result) throws SQLException {
        AuthToken auth = new AuthToken();
        auth.setId(result.getLong("auth_id"));
        auth.setToken(result.getString("token"));
        return auth;
    }
    
    public static User findUserWhenNotExpired(String token) 
            throws InternalServerErrorException, NotFoundException {
        
        String sql = String.format("SELECT %s, %s"
                + "FROM %S INNER JOIN %S AS b "
                + "ON auth_tokens.user_id = b.id "
                + "WHERE auth_tokens.token = ? AND TIMESTAMP(auth_tokens.expires) > NOW()", 
                User.TABLE_COLUMNS, AuthToken.TABLE_COLUMNS, AuthToken.TABLE, User.TABLE);
        
        return findUser(sql, token, "");
    }
    
    public static Merchant findMerchantWhenNotExpired(String token) 
            throws InternalServerErrorException, NotFoundException {
        
        String sql = String.format("SELECT %s, %s, %s "
                + "FROM %s INNER JOIN %s "
                + "ON auth_tokens.user_id = merchants.user_id "
                + "INNER JOIN %s "
                + "ON merchants.user_id = users.id "
                + "WHERE auth_tokens.token = ? AND TIMESTAMP(auth_tokens.expires) > NOW()", 
                User.TABLE_COLUMNS, Merchant.TABLE_COLUMNS, AuthToken.TABLE_COLUMNS, AuthToken.TABLE, Merchant.TABLE, User.TABLE);
       
        return (Merchant)findUser(sql, token, User.TYPE_MERCHANT);
    }
    
    public static Customer findCustomerWhenNotExpired(String token) 
            throws InternalServerErrorException, NotFoundException {
        
        String sql = String.format("SELECT %s, %s, %s "
                + "FROM %s INNER JOIN %s "
                + "ON auth_tokens.user_id = customers.user_id "
                + "INNER JOIN %s "
                + "ON customers.user_id = users.id "
                + "WHERE auth_tokens.token = ? AND TIMESTAMP(auth_tokens.expires) > NOW()", 
                User.TABLE_COLUMNS, Customer.TABLE_COLUMNS, AuthToken.TABLE_COLUMNS, AuthToken.TABLE, Customer.TABLE, User.TABLE);
        
        return (Customer)findUser(sql, token, User.TYPE_CUSTOMER);
    }
    
    private static User findUser(String sql, String token, String type) 
            throws InternalServerErrorException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                
                User user;
                
                switch (type) {
                    case User.TYPE_CUSTOMER :
                        user = CustomerDb.form(result);
                        break;
                    case User.TYPE_MERCHANT :
                        user = MerchantDb.form(result);
                        break;
                    default :
                        user = UserDb.form(result);
                }
                
                user.setAuthToken(form(result));
                
                return user;
            } else {
                throw new NotFoundException(AppStrings.get("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, AuthTokenDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    
    
}

