
package hailandbank.entities;


import java.time.LocalDateTime;
import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "merchant")
public class Merchant extends User {
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE = "merchants";
    
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String TABLE_COLUMNS = 
            "merchants.id AS mid, "
            + "merchants.user_id, "
            + "merchants.name, "
            + "merchants.code, "
            + "merchants.level, "
            + "merchants.transaction_limit, "
            + "merchants.status, "
            + "merchants.status_updated_at";
    
    
    public static final int CODE_LEN = 6;
    
    public static final String ALLOWED_CODE_CHARS = "1234567890";
    
    public static final String STATUS_ACTIVE = "active";
    
    public static final String STATUS_INACTIVE = "inactive";
    
    public static enum Level {
        
        ONE(1, 100000),
        
        TWO(2, 1000000),
        
        THREE(3, -1);
        
        private int value;
        
        private double transactionLimit;

        private Level(int value, double transactionLimit) {
            this.value = value;
            this.transactionLimit = transactionLimit;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public double getTransactionLimit() {
            return transactionLimit;
        }

        public void setTransactionLimit(double transactionLimit) {
            this.transactionLimit = transactionLimit;
        }
        
    }
    
    @JsonbProperty("merchant_id")
    @XmlElement(name = "merchant_id")
    private long merchantId;
    
    private String name;
    
    private String code;
    
    private int level;
    
    @JsonbProperty("transaction_limit")
    @XmlElement(name = "transaction_limit")
    private double transactionLimit;
    
    private String status;
    
    @JsonbProperty("status_updated_at")
    @XmlElement(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;
    
    
    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(double transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }
    
    
}



