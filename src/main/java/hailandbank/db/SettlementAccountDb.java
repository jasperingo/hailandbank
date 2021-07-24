
package hailandbank.db;

import static hailandbank.db.Database.getConnection;
import hailandbank.entities.Action;
import hailandbank.entities.SettlementAccount;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.InternalServerErrorException;


public class SettlementAccountDb extends Database {
    
    
    public static void upsert(SettlementAccount sa) throws InternalServerErrorException {
        long saId = merchantHas(sa.getMerchant().getMerchantId());
        
        if (saId == 0) {
            insert(sa);
        } else {
            sa.setId(saId);
            update(sa);
        }
    }
    
    public static long merchantHas(long merchantId) throws InternalServerErrorException {
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                String.format("SELECT id FROM %s WHERE merchant_id = ?", SettlementAccount.TABLE)
            )) {
            
            pstmt.setLong(1, merchantId);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next())
                return result.getLong("id");
            else 
                return 0;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, SettlementAccountDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    public static void update(SettlementAccount sa) throws InternalServerErrorException {
        
        String sql = "UPDATE "+SettlementAccount.TABLE+" SET "
                        + "bank_name = ?,"
                        + "number = ?,"
                        + "type = ? "
                        + "WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, sa.getBankName());
            pstmt.setString(2, sa.getNumber());
            pstmt.setString(3, sa.getType());
            pstmt.setLong(4, sa.getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not updated: "+rows+". "
                        + "With settlment account id "+sa.getId());
            
            ActionLogDb.log(sa.getMerchant(), Action.UPDATE_SETTLEMENT_ACTION);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, SettlementAccountDb.class.getName());
            }
            
            MyUtils.exceptionLogger(ex, SettlementAccountDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
            
        }
        
    }
    
    
    public static void insert(SettlementAccount sa) throws InternalServerErrorException {
        
        String sql = 
                "INSERT INTO "+SettlementAccount.TABLE+" "
                +"(merchant_id, "
                + "bank_name, "
                + "number, "
                + "type) "
                + "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setLong(1, sa.getMerchant().getMerchantId());
            pstmt.setString(2, sa.getBankName());
            pstmt.setString(3, sa.getNumber());
            pstmt.setString(4, sa.getType());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for settlement account: "+rows);
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) sa.setId(keys.getLong(1));
            
            
            ActionLogDb.log(sa.getMerchant(), Action.ADD_SETTLEMENT_ACTION);
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, SettlementAccountDb.class.getName());
            }
            
            MyUtils.exceptionLogger(ex, SettlementAccountDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
        
    }
    
    
}


