
package hailandbank.db;


import hailandbank.entities.Action;
import hailandbank.entities.Customer;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;


public class CustomerDb extends Database {
    
    public static Customer find(int id) throws InternalServerErrorException, NotFoundException {
        return find("id", String.valueOf(id));
    }
    
    public static Customer find(String phone) throws InternalServerErrorException, NotFoundException {
        return find("phone_number", phone);
    }
    
    public static Customer find(String selection, String selectionArg) 
            throws InternalServerErrorException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT %s, %s "
                        + "FROM %s INNER JOIN %s "
                        + "ON customers.user_id = users.id "
                        + "WHERE customers.%s = ?", 
                        User.TABLE_COLUMNS, Customer.TABLE_COLUMNS, Customer.TABLE, User.TABLE, selection)
            )) {
            
            pstmt.setString(1, selectionArg);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return form(result);
            } else {
                throw new NotFoundException(AppStrings.get("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, CustomerDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    public static Customer form(ResultSet result) throws SQLException {
        
        Customer user = (Customer)UserDb.form(result, User.TYPE_CUSTOMER);
        
        user.setCustomerId(result.getLong("cid"));
        user.setPreferredMerchant(result.getLong("preferred_merchant_id"));
        
        return user;
    }
    
    public static void insert(Customer user) throws InternalServerErrorException {
        
        String sql = "INSERT INTO "+Customer.TABLE+" (user_id, phone_number) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            UserDb.insert(user);
            
            pstmt.setLong(1, user.getId());
            pstmt.setString(2, user.getPhoneNumber());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for user(customer): "+rows);
            
            ActionLogDb.log(user, Action.SIGN_UP);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) user.setCustomerId(keys.getLong(1));
            
            getConnection().commit();
            
        } catch (SQLException ex) {
             try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, CustomerDb.class.getName());
            }
            MyUtils.exceptionLogger(ex, CustomerDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_user"));
        }
        
    }
    
    
    public static void updateAddress(Customer user) throws InternalServerErrorException {
        
        try {
            
            getConnection().setAutoCommit(false);
            
            UserDb.updateAddress(user);
            
            ActionLogDb.log(user, Action.UPDATE_ADDRESS);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, CustomerDb.class.getName());
            }
            MyUtils.exceptionLogger(ex, CustomerDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.update_address_failed"));
        }
    }
    
    
}


