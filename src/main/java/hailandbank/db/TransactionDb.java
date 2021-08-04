
package hailandbank.db;


import hailandbank.entities.Account;
import hailandbank.entities.Action;
import hailandbank.entities.Transaction;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.InternalServerErrorException;


public class TransactionDb extends Database {
    
    
    public static void insert(Transaction t) throws InternalServerErrorException {
          
        switch (t.getType()) {
            case Transaction.TYPE_DEPOSIT :
                insert(t, Action.FUND_ACCOUNT);
                break;
            
            case Transaction.TYPE_WITHDRAW :
                insert(t, Action.WITHDRAW_FROM_ACCOUNT);
                break;
                
            default:
                throw new InternalServerErrorException();
                
        }
        
    }
    
    public static void insert(Transaction t, Action a) throws InternalServerErrorException {
        
        String sql = 
                "INSERT INTO "+Transaction.TABLE+" "
                +"(reference_code, "
                + "account_id, "
                + "type, "
                + "status, "
                + "amount) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, t.getReferenceCode());
            pstmt.setLong(2, t.getAccount().getId());
            pstmt.setString(3, t.getType());
            pstmt.setString(4, t.getStatus());
            pstmt.setDouble(5, t.getAmount());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for transactions: "+rows);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) t.setId(keys.getLong(1));
            
            
            ActionLogDb.log(t.getAccount().getUser(), a);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, TransactionDb.class.getName());
            }
            
            MyUtils.exceptionLogger(ex,TransactionDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_transaction"));
        }
    }
    
    public static void insertForOrder(Transaction t) throws InternalServerErrorException {
        
        String sql = 
                "INSERT INTO "+Transaction.TABLE+" "
                +"(reference_code, "
                + "order_id, "
                + "account_id, "
                + "type, "
                + "status, "
                + "amount) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, t.getReferenceCode());
            pstmt.setLong(2, t.getOrder().getId());
            pstmt.setLong(3, t.getAccount().getId());
            pstmt.setString(4, t.getType());
            pstmt.setString(5, t.getStatus());
            pstmt.setDouble(6, t.getAmount());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for transactions: "+rows);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) t.setId(keys.getLong(1));
            
            
            //ActionLogDb.log(t.getAccount().getUser(), a);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, TransactionDb.class.getName());
            }
            
            MyUtils.exceptionLogger(ex,TransactionDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_transaction"));
        }
    }
    
    
    
    public static Transaction form(ResultSet result) throws SQLException {
        Transaction t = new Transaction();
        t.setId(result.getLong("id"));
        t.setAmount(result.getDouble("amount"));
        t.setReferenceCode(result.getString("reference_code"));
        t.setType(result.getString("type"));
        t.setStatus(result.getString("status"));
        t.setCreatedAt((LocalDateTime)result.getObject("created_at"));
        return t;
    }
    
    public static List<Transaction> findAllByUser(User user, int pageStart, int pageLimit) 
            throws InternalServerErrorException {
        
        String sql = String.format(
                "SELECT %s FROM %s INNER JOIN %s "
                + "ON transactions.account_id = accounts.id "
                + "INNER JOIN %s "
                + "ON accounts.user_id = users.id "
                + "WHERE users.id = ? "
                + "ORDER BY created_at DESC "
                + "LIMIT ?, ?", 
                
                Transaction.TABLE_COLUMNS,
                Transaction.TABLE,
                Account.TABLE,
                User.TABLE
        );
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, user.getId());
            pstmt.setInt(2, pageStart);
            pstmt.setInt(3, pageLimit);
            
            ResultSet result = pstmt.executeQuery();
            
            List<Transaction> list = new ArrayList<>();
            
            while (result.next()) {
                list.add(form(result));
            } 
            
            return list;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, TransactionDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }

    
    
    
    
}




