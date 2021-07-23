
package hailandbank.db;


import hailandbank.entities.Account;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.InternalServerErrorException;


public class AccountDb extends Database {
    
    public static Account form(ResultSet result) throws SQLException{
        Account a = new Account();
        a.setId(result.getLong("id"));
        a.setType(result.getString("type"));
        a.setNumber(result.getString("number"));
        a.setCreatedAt((LocalDateTime)result.getObject("created_at"));
        return a;
    }
    
    public static void insert(Account account) throws InternalServerErrorException {
       
        String sql = 
                "INSERT INTO "+Account.TABLE+" "
                +"(user_id, "
                + "type, "
                + "number) "
                + "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, account.getUser().getId());
            pstmt.setString(2, account.getType());
            pstmt.setString(3, account.getNumber());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for account: "+rows);
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, AccountDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_account"));
        }
    }
    
    
    public static List<Account> findAllByUser(long userId) throws InternalServerErrorException {
        
        String sql = "SELECT * FROM "+Account.TABLE+" WHERE user_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            
            ResultSet result = pstmt.executeQuery();
            
            List<Account> accounts = new ArrayList<>();
            
            while (result.next()) {
                accounts.add(form(result));
            }
            
            return accounts;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, AccountDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
        
    }
    
    
}


