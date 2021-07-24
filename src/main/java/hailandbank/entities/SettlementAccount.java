
package hailandbank.entities;


import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "settlement_account")
public class SettlementAccount extends Entity {
    
    public static final String TABLE = "settlement_accounts";
    
    
    private Merchant merchant;
    
    @JsonbProperty("bank_name")
    @XmlElement(name = "bank_name")
    private String bankName;
    
    private String number;
    
    private String type;
    

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}

