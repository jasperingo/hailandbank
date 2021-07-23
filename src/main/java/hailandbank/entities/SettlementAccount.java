
package hailandbank.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "settlement_account")
public class SettlementAccount extends Entity {
    
    public static final String TABLE = "settlement_accounts";
    
    
    private Merchant merchant;
    
    private String bankName;
    
    private String number;
    
    private int type;
    

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    
}

