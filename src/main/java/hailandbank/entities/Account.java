
package hailandbank.entities;


import hailandbank.utils.Helpers;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Account extends Entity {
    
    public static final String TABLE = "accounts";
    
    public static final String TYPE_SAVINGS = "savings";
    
    protected static final int NUMBER_LEN = 10;
    
    protected static final String ALLOWED_NUMBER_CHARS = "1234567890";
    
    private long id;
    
    private User user;
    
    private String type;
    
    private String number;
    
    private Date createdAt;
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public void generateNumber() {
        String newNumber = "00"+Helpers.generateToken(Account.NUMBER_LEN-2, Account.ALLOWED_NUMBER_CHARS);
        this.setNumber(newNumber);
    }
    
    public static Account form(ResultSet result) throws SQLException{
        Account a = new Account();
        a.setId(result.getLong("id"));
        a.setType(result.getString("type"));
        a.setNumber(result.getString("number"));
        a.setCreatedAt(result.getDate("created_at"));
        return a;
    }
    
    public void insert() throws SQLException {
       
        String sql = 
                "INSERT INTO "+TABLE+" "
                +"(user_id, "
                + "type, "
                + "number) "
                + "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, getUser().getId());
            pstmt.setString(2, getType());
            pstmt.setString(3, getNumber());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not inserted for account: "+rows);
            
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.insert_account"));
        }
    }
    
    
    
}






