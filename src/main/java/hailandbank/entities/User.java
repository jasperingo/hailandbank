
package hailandbank.entities;


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
    
    public static final int TYPE_CUSTOMER = 0;
    
    public static final int TYPE_MERCHANT = 1;
    
    
    private long id;
    
    private int type; 
    private int photo;    
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private String pin;
    private String phoneNumberVerificationOTP;
    private Date updatedAt;
    private Date createdAt;
    
    private AuthToken authToken;
    private List<AuthToken> authTokens;
    private List<Account> accounts;
    private PinReset pinReset;
    
    
    public User() {
        
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
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
    
    public String getPhoneNumberVerificationOTP() {
        return phoneNumberVerificationOTP;
    }

    public void setPhoneNumberVerificationOTP(String phoneNumberVerificationOTP) {
        this.phoneNumberVerificationOTP = phoneNumberVerificationOTP;
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
    
    
    
    
    public void insert() throws SQLException {
        
        String sql = 
                "INSERT INTO users "
                +"(type, "
                + "first_name, "
                + "last_name, "
                + "phone_number, "
                + "pin) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setInt(1, TYPE_CUSTOMER);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, pin);
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) {
                throw new SQLException();
            }
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) setId(keys.getLong(1));
            
            this.getAccount(0).insert();
            
            this.getAuthToken().insert();
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            throw new SQLException(__("messages.insert_user_error"));
        }
    }
    
    
    public static User getUser(int id) throws SQLException {
        return getUser("id", String.valueOf(id));
    }
    
    public static User getUser(String selection, String selectionArg) throws SQLException, NotFoundException {
        
        try {
            
            PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT * FROM users WHERE %s = ?", selection)
            );
            
            pstmt.setString(1, selectionArg);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return form(result);
            } else {
                throw new NotFoundException(__("errors.user_not_found"));
            }
            
        } catch (SQLException ex) {
            //throw ex;
            throw new SQLException(__("errors.unknown"));
        }
    }
    
    public static User form(ResultSet result) throws SQLException {
        User user = new User();
        user.setId(result.getLong("id"));
        user.setType(result.getInt("type"));
        user.setFirstName(result.getString("first_name"));
        user.setLastName(result.getString("last_name"));
        user.setMiddleName(result.getString("middle_name"));
        user.setPhoneNumber(result.getString("phone_number"));
        user.setEmail(result.getString("email"));
        user.setPin(result.getString("pin"));
        user.setPhoto(result.getInt("photo"));
        user.setUpdatedAt(result.getDate("updated_at"));
        user.setCreatedAt(result.getDate("created_at"));
        return user;
    }
    
    public void updatePassword() throws SQLException {
        updatePassword(false);
    }
    
    public void updatePassword(boolean viaReset) throws SQLException {
        
        String sql = "UPDATE "+TABLE+" SET pin = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, getPin());
            pstmt.setLong(2, getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) {
                throw new SQLException();
            }
            
            getPinReset().setUser(this);
            getPinReset().delete();
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            throw new SQLException(__("errors.pin_reset_failed"));
        }
    }
    
    
    
}




