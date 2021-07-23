
package hailandbank.db;

import hailandbank.entities.Action;
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


public class MerchantDb extends Database {
    
    public static Merchant find(int id) throws InternalServerErrorException, NotFoundException {
        return find("id", String.valueOf(id));
    }
    
    public static Merchant find(String phone) throws InternalServerErrorException, NotFoundException {
        return find("phone_number", phone);
    }
    
    public static Merchant find(String selection, String selectionArg) 
            throws InternalServerErrorException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT %s, %s "
                        + "FROM %s INNER JOIN %s "
                        + "ON merchants.user_id = users.id "
                        + "WHERE merchants.%s = ?", 
                        User.TABLE_COLUMNS, Merchant.TABLE_COLUMNS, Merchant.TABLE, User.TABLE, selection)
            )) {
            
            pstmt.setString(1, selectionArg);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return form(result);
            } else {
                throw new NotFoundException(AppStrings.get("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, MerchantDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    public static Merchant form(ResultSet result) throws SQLException {
        Merchant user = (Merchant) UserDb.form(result, Merchant.TYPE_MERCHANT);
        user.setMerchantId(result.getLong("mid"));
        user.setName(result.getString("name"));
        user.setCode(result.getString("code"));
        user.setLevel(result.getInt("level"));
        user.setTransactionLimit(result.getDouble("transaction_limit"));
        user.setStatus(result.getString("status"));
        user.setStatusUpdatedAt((LocalDateTime)result.getObject("status_updated_at"));
        return user;
    }
    
    
    public static void insert(Merchant user) throws InternalServerErrorException {
        
        String sql = 
                "INSERT INTO "+Merchant.TABLE+" "
                +"(user_id, "
                + "phone_number, "
                + "code, "
                + "level, "
                + "transaction_limit, "
                + "status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            UserDb.insert(user);
            
            pstmt.setLong(1, user.getId());
            pstmt.setString(2, user.getPhoneNumber());
            pstmt.setString(3, user.getCode());
            pstmt.setInt(4, user.getLevel());
            pstmt.setDouble(5, user.getTransactionLimit());
            pstmt.setString(6, user.getStatus());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for user(merchant): "+rows);
            
            ActionLogDb.log(user, Action.SIGN_UP);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) user.setMerchantId(keys.getLong(1));
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, MerchantDb.class.getName());
            }
            MyUtils.exceptionLogger(ex, MerchantDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_user"));
        }
        
    }
    
    public static void updateName(Merchant user) throws InternalServerErrorException {
        
        String sql = "UPDATE "+Merchant.TABLE+" SET name = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, user.getName());
            pstmt.setLong(2, user.getMerchantId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not updated: "+rows+". With user id "+user.getMerchantId());
            
            if (user.getStatus().equals(Merchant.STATUS_INACTIVE) && user.getAddressStreet()!= null) {
                updateStatus(user, Merchant.STATUS_ACTIVE);
            }
            
            ActionLogDb.log(user, Action.UPDATE_MERCHANT_NAME);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, MerchantDb.class.getName());
            }
            MyUtils.exceptionLogger(ex, MerchantDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.update_merchant_name_failed"));
        }
    }
    
    public static void updateAddress(Merchant user) throws InternalServerErrorException {
        
        try {
            
            getConnection().setAutoCommit(false);
            
            UserDb.updateAddress(user);
            
            if (user.getStatus().equals(Merchant.STATUS_INACTIVE) && user.getName() != null) {
                updateStatus(user, Merchant.STATUS_ACTIVE);
            }
            
            ActionLogDb.log(user, Action.UPDATE_ADDRESS);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, MerchantDb.class.getName());
            }
            MyUtils.exceptionLogger(ex, MerchantDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.update_address_failed"));
        }
        
    }
    
    private static void updateStatus(Merchant user, String value) throws SQLException {
        
        String sql = "UPDATE "+Merchant.TABLE+" SET status = ? WHERE id = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        
        pstmt.setString(1, value);
        pstmt.setLong(4, user.getMerchantId());
            
        int rows = pstmt.executeUpdate();
            
        if (rows == 0) 
            throw new SQLException("Rows is not updated: "+rows+". With user id "+user.getMerchantId());
        
    }
    
}


