
package hailandbank.entities;


import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "transaction")
public class Transaction extends Entity {
    
    public static final String TABLE = "transactions";
    
    public static final String STATUS_PENDING = "pending";
    
    public static final String STATUS_FAILED = "failed";
    
    public static final String STATUS_PROCESSING = "processing";
    
    public static final String STATUS_CANCELLED = "cancelled";
    
    public static final String STATUS_APPROVED = "approved";
    
    
    @JsonbProperty("reference_code")
    @XmlElement(name = "reference_code")
    private String referenceCode;
    
    private Order order;
    
    private Account account;
    
    private String type;
    
    private String status;
    
    private double amount;
    
    
    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

   
    
}


