
package hailandbank.entities;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Customer extends User {
    
    public static final String TABLE = "customers";
    
    private long id;
    
    private Merchant preferredMerchant;
    
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public Merchant getPreferredMerchant() {
        return preferredMerchant;
    }

    public void setPreferredMerchant(Merchant preferredMerchant) {
        this.preferredMerchant = preferredMerchant;
    }
    
    
    
}
