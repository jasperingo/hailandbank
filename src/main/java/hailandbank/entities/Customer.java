
package hailandbank.entities;


import static hailandbank.entities.Entity.getConnection;
import static hailandbank.entities.User.form;
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
    
    
    public static User find(String selection, String selectionArg) throws SQLException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT %s, a.id, b.user_id, b.preferred_merchant_id "
                        + "FROM customers AS a INNER JOIN users AS b "
                        + "ON a.user_id = b.id"
                        + "WHERE %s = ?", USER_COLUMNS_FOR_JOINS, selection)
            )) {
            
            pstmt.setString(1, selectionArg);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                Customer user = new Customer();
                form(result, user);
                return user;
            } else {
                throw new NotFoundException(__("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.unknown"));
        }
    }
    
    public static void form(ResultSet result, Customer user) throws SQLException {
        User.form(result, user);
        user.setUserId(result.getLong("user_id"));
        user.setCreatedAt(result.getDate("preferred_merchant_id"));
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
    
    
    
    
}







