
package hailandbank.entities;


import hailandbank.utils.Helpers;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;


@XmlRootElement
public class User extends Entity {
    
    public static final String TABLE = "users";
    
    public static final String TABLE_COLUMNS = 
            "users.id, "
            + "users.type, "
            + "users.first_name, "
            + "users.last_name, "
            + "users.middle_name, "
            + "users.phone_number, "
            + "users.email, "
            + "users.pin, "
            + "users.photo, "
            + "users.address_street, "
            + "users.address_city, "
            + "users.address_state, "
            + "users.updated_at, "
            + "users.created_at";
    
    public static final String TYPE_CUSTOMER = "customer";
    
    public static final String TYPE_MERCHANT = "merchant";
    
    public static final String USERS_IMG_PATH = "/path/to/users/imgs/";
    
    public static final String USERS_DEFAULT_IMG = "user.jpg";
    
    
    private long id;
    
    private String photo; 
    private String type; 
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private String pin;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    
    private Date updatedAt;
    private Date createdAt;
    
    private AuthToken authToken;
    private List<AuthToken> authTokens;
    private List<Account> accounts;
    private PinReset pinReset;
    
    
    private String phoneNumberVerificationOTP;
    private String newPin;
    
    
    public User() {
        
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto() {
        return photo;
    }
    
    public void setPhoto(String photo) {
        this.photo = USERS_IMG_PATH+(photo==null?USERS_DEFAULT_IMG:photo);
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = StringUtils.capitalize(firstName.trim());
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = StringUtils.capitalize(lastName.trim());
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName == null ? null : StringUtils.capitalize(middleName.trim());
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
    
    public List<AuthToken> getAuthTokens() {
        return authTokens;
    }

    public void setAuthTokens(List<AuthToken> authTokens) {
        this.authTokens = authTokens;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    public Account getAccount(int i) {
        if (this.accounts == null)
            return null;
        return accounts.get(i);
    }
    
    public void addAccount(Account account) {
        if (this.accounts == null)
            this.accounts = new ArrayList<>();
        this.accounts.add(account);
    }
    
    public PinReset getPinReset() {
        return pinReset;
    }

    public void setPinReset(PinReset pinReset) {
        this.pinReset = pinReset;
    }
    
    
    
    public String getPhoneNumberVerificationOTP() {
        return phoneNumberVerificationOTP;
    }

    public void setPhoneNumberVerificationOTP(String phoneNumberVerificationOTP) {
        this.phoneNumberVerificationOTP = phoneNumberVerificationOTP;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
    
    
    
    
    public static User find(int id) throws SQLException, NotFoundException {
        return find("id", String.valueOf(id));
    }
    
    public static User find(String selection, String selectionArg) throws SQLException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT * FROM users WHERE %s = ?", selection)
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
    
    public static User form(ResultSet result) throws SQLException {
        return form(result, new User());
    }
    
    public static User form(ResultSet result, String type) throws SQLException {
        switch (type) {
            case TYPE_CUSTOMER:
                return form(result, new Customer());
            case TYPE_MERCHANT:
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
        user.setUpdatedAt(result.getDate("updated_at"));
        user.setCreatedAt(result.getDate("created_at"));
        return user;
    }
    
    
    public void insert() throws SQLException {
        
        String sql = 
                "INSERT INTO "+TABLE+" "
                +"(type, "
                + "first_name, "
                + "last_name, "
                + "phone_number, "
                + "pin) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, type);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, pin);
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not inserted for user: "+rows);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) setId(keys.getLong(1));
            
            this.getAccount(0).insert();
            
            this.getAuthToken().insert();
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.insert_user"));
        }
    }
    
    public void updatePin() throws SQLException {
        updatePin(false);
    }
    
    public void updatePin(boolean viaReset) throws SQLException {
        
        String sql = "UPDATE "+TABLE+" SET pin = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, getPin());
            pstmt.setLong(2, getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not updated: "+rows+". With user id "+getId());
            
            if (viaReset) {
                getPinReset().setUser(this);
                getPinReset().delete();
                
                ActionLog.log(this, Action.RESET_PIN);
            } else {
                ActionLog.log(this, Action.UPDATE_PIN);
            }
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            Helpers.stackTracer(ex);
            String msg = viaReset ? __("errors.pin_reset_failed") : __("errors.update_pin_failed");
            throw new SQLException(msg);
        }
    }
    
    public void updateAddress(long uid) throws SQLException {
        
        String sql = "UPDATE "+TABLE+" SET address_street = ?, address_city = ?, address_state = ? WHERE id = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
            
        pstmt.setString(1, getAddressStreet());
        pstmt.setString(2, getAddressCity());
        pstmt.setString(3, getAddressState());
        pstmt.setLong(4, uid);
            
        int rows = pstmt.executeUpdate();
            
        if (rows == 0) 
            throw new SQLException("Rows is not updated: "+rows+". With user id "+getId());
        
    }
    
    public void findAccounts(long uid) throws SQLException {
        
        String sql = "SELECT * FROM "+Account.TABLE+" WHERE user_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, uid);
            
            ResultSet result = pstmt.executeQuery();
            
            while (result.next()) {
                addAccount(Account.form(result));
            }
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.unknown"));
        }
        
    }
    
    
    
    
}




