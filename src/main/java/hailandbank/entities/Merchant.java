
package hailandbank.entities;


import hailandbank.utils.Helpers;
import static hailandbank.utils.Helpers.__;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Merchant extends User {
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE = "merchants";
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE_COLUMNS = 
            "merchants.id AS mid, "
            + "merchants.user_id, "
            + "merchants.name, "
            + "merchants.code, "
            + "merchants.level, "
            + "merchants.transaction_limit, "
            + "merchants.status, "
            + "merchants.status_updated_at";
    
    public static final int CODE_LEN = 6;
    
    public static final String ALLOWED_CODE_CHARS = "1234567890";
    
    public static final String STATUS_ACTIVE = "active";
    
    public static final String STATUS_INACTIVE = "inactive";
    
    public static enum Level {
        
        ONE(1, 100000),
        
        TWO(2, 1000000),
        
        THREE(3, -1);
        
        private int value;
        
        private double transactionLimit;

        private Level(int value, double transactionLimit) {
            this.value = value;
            this.transactionLimit = transactionLimit;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public double getTransactionLimit() {
            return transactionLimit;
        }

        public void setTransactionLimit(double transactionLimit) {
            this.transactionLimit = transactionLimit;
        }
        
    }
    
    
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
    
    
    public static Merchant find(int id) throws SQLException, NotFoundException {
        return find("id", String.valueOf(id));
    }
    
    public static Merchant find(String selection, String selectionArg) throws SQLException, NotFoundException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT %s, %s "
                        + "FROM %s INNER JOIN %s "
                        + "ON merchants.user_id = users.id "
                        + "WHERE merchants.%s = ?", 
                        User.TABLE_COLUMNS, TABLE_COLUMNS, TABLE, User.TABLE, selection)
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
    
    public static Merchant form(ResultSet result) throws SQLException {
        Merchant user = (Merchant)User.form(result, TYPE_MERCHANT);
        user.setId(result.getLong("mid"));
        user.setUserId(result.getLong("user_id"));
        user.setName(result.getString("name"));
        user.setCode(result.getString("code"));
        user.setLevel(result.getInt("level"));
        user.setTransactionLimit(result.getDouble("transaction_limit"));
        user.setStatus(result.getString("status"));
        user.setStatusUpdatedAt(result.getDate("status_updated_at"));
        return user;
    }
    
    @Override
    public void insert() throws SQLException {
        
        String sql = 
                "INSERT INTO "+TABLE+" "
                +"(user_id, "
                + "phone_number, "
                + "code, "
                + "level, "
                + "transaction_limit, "
                + "status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            super.insert();
            
            setUserId(getId());
            
            pstmt.setLong(1, getUserId());
            pstmt.setString(2, getPhoneNumber());
            pstmt.setString(3, getCode());
            pstmt.setInt(4, getLevel());
            pstmt.setDouble(5, getTransactionLimit());
            pstmt.setString(6, getStatus());
            
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
    
    public void updateName() throws SQLException {
        
        String sql = "UPDATE "+TABLE+" SET name = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, getName());
            pstmt.setLong(2, getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) throw new SQLException("Rows is not updated: "+rows+". With user id "+getId());
            
            ActionLog.log(this, Action.UPDATE_MERCHANT_NAME);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.update_merchant_name_failed"));
        }
    }
    
    public void updateAddress() throws SQLException {
        
        try {
            
            getConnection().setAutoCommit(false);
            
            super.updateAddress(getUserId());
            
            if (getStatus().equals(STATUS_INACTIVE) && getName() != null) {
                updateStatus(STATUS_ACTIVE);
            }
            
            ActionLog.log(this, Action.UPDATE_ADDRESS);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            getConnection().rollback();
            Helpers.stackTracer(ex);
            throw new SQLException(__("errors.update_address_failed"));
        }
        
    }
    
    private void updateStatus(String value) throws SQLException {
        
        String sql = "UPDATE "+TABLE+" SET status = ? WHERE id = ?";
        
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        
        pstmt.setString(1, value);
        pstmt.setLong(4, getId());
            
        int rows = pstmt.executeUpdate();
            
        if (rows == 0) 
            throw new SQLException("Rows is not updated: "+rows+". With user id "+getId());
        
    }
    
    
}




