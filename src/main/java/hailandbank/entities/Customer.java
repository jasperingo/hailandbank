
package hailandbank.entities;


import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "customer")
public class Customer extends User {
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE = "customers";
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE_COLUMNS = 
            "customers.id AS cid, "
            + "customers.user_id, "
            + "customers.preferred_merchant_id";
    
    
    @JsonbProperty("customer_id")
    @XmlElement(name = "customer_id")
    private long customerId;
    
    @JsonbProperty("preferred_merchant")
    @XmlElement(name = "preferred_merchant")
    private Merchant preferredMerchant;
    
    
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }
    
    public Merchant getPreferredMerchant() {
        return preferredMerchant;
    }

    public void setPreferredMerchant(Merchant preferredMerchant) {
        this.preferredMerchant = preferredMerchant;
    }
    
    public void setPreferredMerchant(long merchantId) {
        if (merchantId > 0) {
            Merchant m = new Merchant();
            m.setId(merchantId);
            this.preferredMerchant = m;
        }
    }
   
    
}


