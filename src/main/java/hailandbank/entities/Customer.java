
package hailandbank.entities;


import hailandbank.utils.Helpers;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Customer extends User {
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE = "customers";
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
     public static final String TABLE_COLUMNS = 
            "customers.id AS cid, "
            + "customers.user_id, "
            + "customers.preferred_merchant_id";
    
    private long id;
    
    private long userId;
    
    private Merchant preferredMerchant;
    
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public Merchant getPreferredMerchant() {
        return preferredMerchant;
    }

    public void setPreferredMerchant(Merchant preferredMerchant) {
        this.preferredMerchant = preferredMerchant;
    }
    
    public static Customer find(int id) throws SQLException, NotFoundException {
        return find("id", String.valueOf(id));
    }
    
    public static Customer find(String selection, String selectionArg) throws SQLException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT %s, %s "
                        + "FROM %s INNER JOIN %s "
                        + "ON customers.user_id = users.id "
                        + "WHERE customers.%s = ?", 
                        User.TABLE_COLUMNS,TABLE_COLUMNS, TABLE, User.TABLE, selection)
            )) {
            
            pstmt.setString(1, selectionArg);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return form(result);
            } else {
                throw new NotFoundException(__("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.unknown"));
        }
    }
    
    public static Customer form(ResultSet result) throws SQLException {
        Customer user = (Customer)User.form(result, TYPE_CUSTOMER);
        user.setId(result.getLong("cid"));
        user.setUserId(result.getLong("user_id"));
        
        long prefMerchant = result.getLong("preferred_merchant_id");
        if (prefMerchant > 0) {
            Merchant m = new Merchant();
            m.setId(prefMerchant);
            user.setPreferredMerchant(m);
        }
        
        return user;
    }
    
    
    @Override
    public void insert() throws SQLException {
        
        String sql = "INSERT INTO "+TABLE+" (user_id, phone_number) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            super.insert();
            
            setUserId(getId());
            
            pstmt.setLong(1, getUserId());
            pstmt.setString(2, getPhoneNumber());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not inserted for user(customer): "+rows);
            
            ActionLog.log(this, Action.SIGN_UP);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) setId(keys.getLong(1));
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.insert_user"));
        }
        
    }
    
    
    public void updateAddress() throws SQLException {
        
        try {
            
            getConnection().setAutoCommit(false);
            
            super.updateAddress(getUserId());
            
            ActionLog.log(this, Action.UPDATE_ADDRESS);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.update_address_failed"));
        }
    }
    
    
}


