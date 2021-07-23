
package hailandbank.db;

import hailandbank.entities.Action;
import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;


public class UserDb extends Database {
    
    
    public static long findIdByPhoneNumber(String phone) 
            throws InternalServerErrorException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT id FROM %s WHERE phone_number = ?", User.TABLE)
            )) {
            
            pstmt.setString(1, phone);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return result.getLong("id");
            } else {
                throw new NotFoundException(AppStrings.get("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, UserDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    public static User find(long id) throws InternalServerErrorException, NotFoundException {
        return find("id", String.valueOf(id));
    }
    
    public static User find(String phone) throws InternalServerErrorException, NotFoundException {
        return find("phone_number", phone);
    }
    
    public static User find(String selection, String selectionArg) 
            throws InternalServerErrorException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT * FROM %s WHERE %s = ?", User.TABLE, selection)
            )) {
            
            pstmt.setString(1, selectionArg);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return form(result);
            } else {
                throw new NotFoundException(AppStrings.get("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, UserDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    public static User form(ResultSet result) throws SQLException {
        return form(result, new User());
    }
    
    public static User form(ResultSet result, String type) throws SQLException {
        switch (type) {
            case User.TYPE_CUSTOMER:
                return form(result, new Customer());
            case User.TYPE_MERCHANT:
                return form(result, new Merchant());
            default:
                return form(result, new User());
        }
    }
    
    public static User form(ResultSet result, User user) throws SQLException {
        user.setId(result.getLong("id"));
        user.setType(result.getString("type"));
        user.setFirstName(result.getString("first_name"));
        user.setLastName(result.getString("last_name"));
        user.setMiddleName(result.getString("middle_name"));
        user.setPhoneNumber(result.getString("phone_number"));
        user.setEmail(result.getString("email"));
        user.setPin(result.getString("pin"));
        user.setPhoto(result.getString("photo"));
        user.setAddressStreet(result.getString("address_street"));
        user.setAddressCity(result.getString("address_city"));
        user.setAddressState(result.getString("address_state"));
        user.setUpdatedAt((LocalDateTime)result.getObject("updated_at"));
        user.setCreatedAt((LocalDateTime)result.getObject("created_at"));
        return user;
    }
    
    
    public static void insert(User user) throws InternalServerErrorException {
        
        String sql = 
                "INSERT INTO "+User.TABLE+" "
                +"(type, "
                + "first_name, "
                + "last_name, "
                + "phone_number, "
                + "pin) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getType());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getPin());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for user: "+rows);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) user.setId(keys.getLong(1));
            
            AccountDb.insert(user.getAccount(0));
            
            AuthTokenDb.insert(user.getAuthToken());
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, UserDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_user"));
        }
    }
    
    public static void updatePin(User user) throws InternalServerErrorException {
        updatePin(user, false);
    }
    
    public static void updatePin(User user, boolean viaReset) throws InternalServerErrorException {
        
        String sql = "UPDATE "+User.TABLE+" SET pin = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, user.getPin());
            pstmt.setLong(2, user.getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not updated: "+rows+". With user id "+user.getId());
            
            if (viaReset) {
                
                user.getPinReset().setUser(user);
                PinResetDb.delete(user.getPinReset());
                
                ActionLogDb.log(user, Action.RESET_PIN);
            } else {
                ActionLogDb.log(user, Action.UPDATE_PIN);
            }
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, UserDb.class.getName());
            }
            
            MyUtils.exceptionLogger(ex, UserDb.class.getName());
            throw new InternalServerErrorException(viaReset ? AppStrings.get("errors.pin_reset_failed")
                        : AppStrings.get("errors.update_pin_failed"));
            
        }
        
    }
    
    public static void updateAddress(User user) throws SQLException {
        
        String sql = "UPDATE "+User.TABLE+" SET address_street = ?, address_city = ?, address_state = ? WHERE id = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
            
        pstmt.setString(1, user.getAddressStreet());
        pstmt.setString(2, user.getAddressCity());
        pstmt.setString(3, user.getAddressState());
        pstmt.setLong(4, user.getId());
            
        int rows = pstmt.executeUpdate();
            
        if (rows == 0) 
            throw new SQLException("Rows is not updated: "+rows+". With user id "+user.getId());
        
    }
    
    
}



