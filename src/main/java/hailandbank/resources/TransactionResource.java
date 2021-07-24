
package hailandbank.resources;

import hailandbank.entities.Transaction;
import hailandbank.entities.User;


public class TransactionResource extends Resource {
    
    public static TransactionResource get() {
        return new TransactionResource();
    }
    
    public static TransactionResource with(User user) {
        TransactionResource res = new TransactionResource();
        res.setAuthUser(user);
        return res;
    }
    
    
    public void add(Transaction trans) {
        
    }
    
}


