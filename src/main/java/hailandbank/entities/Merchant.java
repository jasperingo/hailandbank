
package hailandbank.entities;


import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Merchant extends User {
    
    public static final String TABLE = "merchants";
    
    private long id;
    
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
    
    
    public void insert(int userId, String phoneNumber) throws SQLException {
        
        String sql = 
                "INSERT INTO "+TABLE+" "
                +"(user_id, "
                + "phone_number, "
                + "code, "
                + "level, "
                + "status) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            
            pstmt.setLong(1, userId);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, getCode());
            pstmt.setInt(4, getLevel());
            pstmt.setString(5, getStatus());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) {
                throw new SQLException();
            }
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) setId(keys.getLong(1));
            
        } catch (SQLException ex) {
            throw new SQLException(__("messages.insert_user_error"));
        }
        
    }
    
    
    
}




