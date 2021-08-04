
package hailandbank.db;


import hailandbank.entities.Account;
import hailandbank.entities.Action;
import hailandbank.entities.Order;
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


public class OrderDb extends Database {
    
    public static void insert(Order order) throws InternalServerErrorException {
        
        String sql = 
                "INSERT INTO "+Order.TABLE+" "
                +"(account_id, "
                + "type, "
                + "mode, "
                + "status, "
                + "amount, "
                + "charge, "
                + "address_street, "
                + "address_city, "
                + "address_state) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setLong(1, order.getAccount().getId());
            pstmt.setString(2, order.getType());
            pstmt.setString(3, order.getMode());
            pstmt.setString(4, order.getStatus());
            pstmt.setDouble(5, order.getAmount());
            pstmt.setDouble(6, order.getCharge());
            pstmt.setString(7, order.getAddressStreet());
            pstmt.setString(8, order.getAddressCity());
            pstmt.setString(9, order.getAddressState());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not inserted for order: "+rows);
            
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) order.setId(keys.getLong(1));
            
            ActionLogDb.log(order.getAccount().getUser(), Action.PLACE_ORDER);
            
            
            if (order.getType().equals(Order.Type.CASH_DELIVERY.getValue())) {
                
                Transaction trans = new Transaction();
                trans.setType(Transaction.TYPE_WITHDRAW);
                trans.setAmount(-1 * order.getAmount());
                trans.setOrder(order);
                trans.setAccount(order.getAccount());
                trans.setStatus(Transaction.STATUS_APPROVED);
                trans.setReferenceCode(MyUtils.generateToken(
                        Transaction.REFERENCE_CODE_LEN, 
                        Transaction.ALLOWED_REFERENCE_CODE_CHARS
                ));

                TransactionDb.insertForOrder(trans);

                
                Transaction chargeTrans = new Transaction();
                chargeTrans.setType(Transaction.TYPE_ORDER_CHARGE);
                chargeTrans.setAmount(-1 * Order.CHARGE);
                chargeTrans.setOrder(order);
                chargeTrans.setAccount(order.getAccount());
                chargeTrans.setStatus(Transaction.STATUS_APPROVED);
                chargeTrans.setReferenceCode(MyUtils.generateToken(
                        Transaction.REFERENCE_CODE_LEN, 
                        Transaction.ALLOWED_REFERENCE_CODE_CHARS
                ));
                
                TransactionDb.insertForOrder(chargeTrans);
            }
            
            getConnection().commit();
            
        } catch (SQLException ex) {
            
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                MyUtils.exceptionLogger(ex1, OrderDb.class.getName());
            }
            
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_order"));
        }
    }
    
    
    public static Order form(ResultSet result) throws SQLException {
        Order o = new Order();
        o.setId(result.getLong("id"));
        o.setAmount(result.getDouble("amount"));
        o.setCharge(result.getDouble("charge"));
        o.setType(result.getString("type"));
        o.setMode(result.getString("mode"));
        o.setStatus(result.getString("status"));
        o.setAddressX(result.getDouble("address_x"));
        o.setAddressY(result.getDouble("address_y"));
        o.setAddressCity(result.getString("address_city"));
        o.setAddressState(result.getString("address_state"));
        o.setAddressStreet(result.getString("address_street"));
        o.setCreatedAt((LocalDateTime)result.getObject("created_at"));
        return o;
    }
    
    
    public static List<Order> findList(User user, int pageStart, int pageLimit) 
            throws InternalServerErrorException {
        
        String sql = String.format(
                "SELECT %s "
                + "FROM %s INNER JOIN %s "
                + "ON accounts.id = orders.account_id "
                + "WHERE accounts.user_id = ? "
                + "ORDER BY orders.created_at DESC "
                + "LIMIT ?, ?", 
                
                Order.TABLE_COLUMNS,
                Order.TABLE,
                Account.TABLE
        );
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, user.getId());
            pstmt.setInt(2, pageStart);
            pstmt.setInt(3, pageLimit);
            
            ResultSet result = pstmt.executeQuery();
            
            List<Order> list = new ArrayList<>();
            
            while (result.next()) {
                list.add(form(result));
            } 
            
            return list;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    
    public static List<Order> findCustomersList(User user, int pageStart, int pageLimit) 
            throws InternalServerErrorException {
        
        String sql = String.format(
                "SELECT %s "
                + "FROM %s INNER JOIN %s "
                + "ON accounts.id = orders.merchant_account_id "
                + "WHERE accounts.user_id = ? "
                + "ORDER BY orders.created_at DESC "
                + "LIMIT ?, ?", 
                
                Order.TABLE_COLUMNS,
                Order.TABLE,
                Account.TABLE
        );
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, user.getId());
            pstmt.setInt(2, pageStart);
            pstmt.setInt(3, pageLimit);
            
            ResultSet result = pstmt.executeQuery();
            
            List<Order> list = new ArrayList<>();
            
            while (result.next()) {
                list.add(form(result));
            } 
            
            return list;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    
    public static List<Order> findPending(User user, int pageStart, int pageLimit) 
            throws InternalServerErrorException {
        
        String sql = String.format(
                "SELECT %s FROM %s "
                + "WHERE status = ? "
                + "AND address_state = ? "
                + "AND address_city = ? "
                + "ORDER BY created_at DESC "
                + "LIMIT ?, ?", 
                
                Order.TABLE_COLUMNS,
                Order.TABLE
        );
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, Order.STATUS_PENDING);
            pstmt.setString(2, user.getAddressState());
            pstmt.setString(3, user.getAddressCity());
            pstmt.setInt(4, pageStart);
            pstmt.setInt(5, pageLimit);
            
            ResultSet result = pstmt.executeQuery();
            
            List<Order> list = new ArrayList<>();
            
            while (result.next()) {
                list.add(form(result));
            } 
            
            return list;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    public static long countPending(User user) throws InternalServerErrorException {
        
        String sql = String.format(
                "SELECT COUNT(id) AS count FROM %s "
                + "WHERE status = ? "
                + "AND address_state = ? "
                + "AND address_city = ? ", 
                
                Order.TABLE
        );
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, Order.STATUS_PENDING);
            pstmt.setString(2, user.getAddressState());
            pstmt.setString(3, user.getAddressCity());
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                return result.getLong("count");
            } 
            
            return 0;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    public static Order findById(long id) throws InternalServerErrorException {
        
        String sql = String.format(
                "SELECT %s FROM %s "
                + "WHERE id = ? ", 
                
                Order.TABLE_COLUMNS,
                Order.TABLE
        );
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            ResultSet result = pstmt.executeQuery();
            
            if (result.next()) {
                Order o = form(result);
                
                Account ma = new Account();
                ma.setId(result.getLong("merchant_account_id"));
                o.setMerchantAccount(ma);
               
                Account a = new Account();
                a.setId(result.getLong("account_id"));
                o.setAccount(a);
                
                return o;
            } 
            
            return null;
            
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.unknown"));
        }
    }
    
    
    public static void updateToProcessing(Order order) throws InternalServerErrorException {
        
        String sql = 
                "UPDATE "+Order.TABLE+" "
                +"SET status = ?, merchant_account_id = ? "
                + "WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, Order.STATUS_PROCESSING);
            pstmt.setLong(2, order.getMerchantAccount().getId());
            pstmt.setLong(3, order.getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not updated for order: "+rows);
            
            //log action
            
                 
        } catch (SQLException ex) {
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_order"));
        }
    }
    
    
    public static void updateToFulfil(Order order) throws InternalServerErrorException {
        
        String sql = 
                "UPDATE "+Order.TABLE+" "
                +"SET status = ? "
                + "WHERE id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            
            getConnection().setAutoCommit(false);
            
            pstmt.setString(1, Order.STATUS_FULFILLED);
            pstmt.setLong(2, order.getId());
            
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) 
                throw new SQLException("Rows is not updated for order: "+rows);
            
            //log action
            
            
            if (order.getType().equals(Order.Type.CASH_DELIVERY.getValue())) {
                System.out.println("oojodjoisfjio");
                Transaction trans = new Transaction();
                trans.setType(Transaction.TYPE_ORDER_AMOUNT);
                trans.setAmount(order.getAmount());
                trans.setOrder(order);
                trans.setAccount(order.getMerchantAccount());
                trans.setStatus(Transaction.STATUS_APPROVED);
                trans.setReferenceCode(MyUtils.generateToken(
                        Transaction.REFERENCE_CODE_LEN, 
                        Transaction.ALLOWED_REFERENCE_CODE_CHARS
                ));

                TransactionDb.insertForOrder(trans);

                
                Transaction chargeTrans = new Transaction();
                chargeTrans.setType(Transaction.TYPE_ORDER_PROFIT);
                chargeTrans.setAmount(Order.CHARGE-Order.SERVICE_FEE);
                chargeTrans.setOrder(order);
                chargeTrans.setAccount(order.getMerchantAccount());
                chargeTrans.setStatus(Transaction.STATUS_APPROVED);
                chargeTrans.setReferenceCode(MyUtils.generateToken(
                        Transaction.REFERENCE_CODE_LEN, 
                        Transaction.ALLOWED_REFERENCE_CODE_CHARS
                ));
                
                TransactionDb.insertForOrder(chargeTrans);
            }
            
            
            if (order.getType().equals(Order.Type.CASH_PICK_UP.getValue())) {
                
                Transaction trans = new Transaction();
                trans.setType(Transaction.TYPE_DEPOSIT);
                trans.setAmount(order.getAmount());
                trans.setOrder(order);
                trans.setAccount(order.getAccount());
                trans.setStatus(Transaction.STATUS_APPROVED);
                trans.setReferenceCode(MyUtils.generateToken(
                        Transaction.REFERENCE_CODE_LEN, 
                        Transaction.ALLOWED_REFERENCE_CODE_CHARS
                ));

                TransactionDb.insertForOrder(trans);
                
                
                
                Transaction trans2 = new Transaction();
                trans2.setType(Transaction.TYPE_ORDER_AMOUNT);
                trans2.setAmount(-1 * order.getAmount());
                trans2.setOrder(order);
                trans2.setAccount(order.getMerchantAccount());
                trans2.setStatus(Transaction.STATUS_APPROVED);
                trans2.setReferenceCode(MyUtils.generateToken(
                        Transaction.REFERENCE_CODE_LEN, 
                        Transaction.ALLOWED_REFERENCE_CODE_CHARS
                ));

                TransactionDb.insertForOrder(trans2);

                
                Transaction chargeTrans = new Transaction();
                chargeTrans.setType(Transaction.TYPE_ORDER_SEEVICE_FEE);
                chargeTrans.setAmount(-1 * Order.SERVICE_FEE);
                chargeTrans.setOrder(order);
                chargeTrans.setAccount(order.getMerchantAccount());
                chargeTrans.setStatus(Transaction.STATUS_APPROVED);
                chargeTrans.setReferenceCode(MyUtils.generateToken(
                        Transaction.REFERENCE_CODE_LEN, 
                        Transaction.ALLOWED_REFERENCE_CODE_CHARS
                ));
                
                TransactionDb.insertForOrder(chargeTrans);
            }
            
            getConnection().commit();
                 
        } catch (SQLException ex) {
            
            try {
                getConnection().rollback();
            } catch (SQLException ex1) {
                 MyUtils.exceptionLogger(ex1, OrderDb.class.getName());
            }
            
            MyUtils.exceptionLogger(ex, OrderDb.class.getName());
            throw new InternalServerErrorException(AppStrings.get("errors.insert_order"));
        }
    }
    
}



