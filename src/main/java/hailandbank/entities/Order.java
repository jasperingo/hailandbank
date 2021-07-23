
package hailandbank.entities;

import java.util.Date;


public class Order {
    
    
    private long id;
    
    private Account account;
    
    private String type;
    
    private String mode;
    
    private String referenceCode;
    
    private String status;
    
    private double amount;
    
    private String addressStreet;
    
    private String addressCity;
    
    private String addressState;
    
    private double addressX;
    
    private double addressY;
    
    private Date createdAt;
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
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

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public double getAddressX() {
        return addressX;
    }

    public void setAddressX(double addressX) {
        this.addressX = addressX;
    }

    public double getAddressY() {
        return addressY;
    }

    public void setAddressY(double addressY) {
        this.addressY = addressY;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    
    
    
    
}


