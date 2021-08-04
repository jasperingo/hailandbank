
package hailandbank.resources;

import hailandbank.db.AccountDb;
import hailandbank.db.TransactionDb;
import hailandbank.entities.Account;
import hailandbank.entities.Transaction;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.InputData;
import hailandbank.utils.InputErrorException;
import hailandbank.utils.MyResponse;
import hailandbank.utils.MyUtils;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;


public class TransactionResource extends Resource {
    
    public static TransactionResource get() {
        return new TransactionResource();
    }
    
    public static TransactionResource with(User user) {
        TransactionResource res = new TransactionResource();
        res.setAuthUser(user);
        return res;
    }
    
    
    public Response add(Transaction trans, boolean fund) throws InternalServerErrorException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (trans.getAmount() < 100) {
            errors.put("amount", InputData.with(
                    String.valueOf(trans.getAmount()), 
                    AppStrings.get("errors.amount_less_than_minimum")
            ));
        }
        
        if (trans.getAccount() == null || trans.getAccount().getNumber() == null || 
                trans.getAccount().getNumber().length() != Account.NUMBER_LEN) {
            errors.put("account", InputData.with("", AppStrings.get("errors.account_is_invalid")));
        } else {
            
            Account acct = AccountDb.findByUser(getAuthUser().getId());
            
            if (!acct.getNumber().equals(trans.getAccount().getNumber())) {
                errors.put("account", InputData.with(
                        trans.getAccount().getNumber(),
                        AppStrings.get("errors.account_is_invalid")
                ));
                
            } else if (!fund && acct.getBalance() < trans.getAmount()) {
                
                errors.put("amount", InputData.with(
                        String.valueOf(trans.getAmount()), 
                        AppStrings.get("errors.amount_more_than_maximum")
                ));
                
            } else {
                acct.setUser(getAuthUser());
                trans.setAccount(acct);
            }
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        if (!fund) {
            trans.setAmount(-1*trans.getAmount());
        }
        
        trans.setType(fund ? Transaction.TYPE_DEPOSIT : Transaction.TYPE_WITHDRAW);
        
        trans.setStatus(Transaction.STATUS_APPROVED);
        trans.setReferenceCode(MyUtils.generateToken(
                Transaction.REFERENCE_CODE_LEN, 
                Transaction.ALLOWED_REFERENCE_CODE_CHARS
        ));
        
        if (!fund) {
            // get settlement account and send the monry to it :)
        }
        
        TransactionDb.insert(trans);
        
        return Response.ok(
                MyResponse.success(AppStrings.get("success.transaction_inserted"), trans)
        ).build();
    }
    
    
    public Response getList(int pageStart, int pageLimit) {
        List<Transaction> list = TransactionDb.findAllByUser(getAuthUser(), pageStart, pageLimit);
        return Response.ok(MyResponse.success(list)).build();
    }
    
    
    
}





