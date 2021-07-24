
package hailandbank.resources;

import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
import hailandbank.entities.User;


abstract public class Resource {
    
    
    private User authUser;
    
    
    public void setAuthUser(User authUser) {
        this.authUser = authUser;
    }
    
    public User getAuthUser() {
        return authUser;
    }
    
    public Customer getAuthCustomer() {
        return (Customer) authUser;
    }
    
    public Merchant getAuthMerchant() {
        return (Merchant) authUser;
    }
    
    
}
