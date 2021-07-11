
package hailandbank.entities;


import hailandbank.utils.Helpers;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Merchant extends User {
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE = "merchants";
    
    public static final int CODE_LEN = 6;
    
    public static final String ALLOWED_CODE_CHARS = "1234567890";
    
    public static final String STATUS_ACTIVE = "active";
    
    public static final String STATUS_INACTIVE = "inactive";
    
    public static final int LEVEL_ONE = 1;
    
    
    private long id;
    
    private long userId;
    
    private String name;
    
    private String code;
    
    private int level;
    
    private double transactionLimit;
    
    private String status;
    
    private Date statusUpdatedAt;
    
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
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(double transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(Date statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }
    
    
    @Override
    public void insert() throws SQLException {
        
        String sql = 
                "INSERT INTO "+TABLE+" "
                +"(user_id, "
                + "phone_number, "
                + "code, "
                + "level, "
                + "status) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            super.insert();
            
            setUserId(getId());
            
            pstmt.setLong(1, getUserId());
            pstmt.setString(2, getPhoneNumber());
            pstmt.setString(3, getCode());
            pstmt.setInt(4, getLevel());
            pstmt.setString(5, getStatus());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not inserted for user(merchant): "+rows);
            
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




