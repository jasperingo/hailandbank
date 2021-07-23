
package hailandbank.entities;


import java.util.ArrayList;
import java.util.List;
import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;


@XmlRootElement(name = "user")
public class User extends Entity {
    
    public static final String TABLE = "users";
    
    public static final String TABLE_COLUMNS = 
            "users.id, "
            + "users.type, "
            + "users.first_name, "
            + "users.last_name, "
            + "users.middle_name, "
            + "users.phone_number, "
            + "users.email, "
            + "users.pin, "
            + "users.photo, "
            + "users.address_street, "
            + "users.address_city, "
            + "users.address_state, "
            + "users.updated_at, "
            + "users.created_at";
    
    public static final String TYPE_CUSTOMER = "customer";
    
    public static final String TYPE_MERCHANT = "merchant";
    
    public static final String USERS_IMG_PATH = "/path/to/users/imgs/";
    
    public static final String USERS_DEFAULT_IMG = "user.jpg";
    
    
    private String photo; 
    
    private String type; 
    
    @JsonbProperty("first_name")
    @XmlElement(name = "first_name")
    private String firstName;
    
    @JsonbProperty("last_name")
    @XmlElement(name = "last_name")
    private String lastName;
    
    @JsonbProperty("middle_name")
    @XmlElement(name = "middle_name")
    private String middleName;
    
    private String email;
    
    @JsonbProperty("phone_number")
    @XmlElement(name = "phone_number")
    private String phoneNumber;
    
    private String pin;
    
    @JsonbProperty("address_street")
    @XmlElement(name = "address_street")
    private String addressStreet;
    
    @JsonbProperty("address_city")
    @XmlElement(name = "address_city")
    private String addressCity;
    
    @JsonbProperty("address_state")
    @XmlElement(name = "address_state")
    private String addressState;
    
    @JsonbProperty("auth_token")
    @XmlElement(name = "auth_token")
    private AuthToken authToken;
    
    @JsonbProperty("auth_tokens")
    @XmlElement(name = "auth_tokens")
    private List<AuthToken> authTokens;
    
    @JsonbProperty("accounts")
    @XmlElement(name = "accounts")
    private List<Account> accounts;
    
    @JsonbProperty("pin_reset")
    @XmlElement(name = "pin_reset")
    private PinReset pinReset;
    
    @JsonbProperty("phone_number_verification_otp")
    @XmlElement(name = "phone_number_verification_otp")
    private String phoneNumberVerificationOTP;
    
    @JsonbProperty("new_pin")
    @XmlElement(name = "new_pin")
    private String newPin;
    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto() {
        return photo;
    }
    
    public void setPhoto(String photo) {
        this.photo = USERS_IMG_PATH+(photo==null?USERS_DEFAULT_IMG:photo);
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = StringUtils.capitalize(firstName.trim());
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = StringUtils.capitalize(lastName.trim());
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName == null ? null : StringUtils.capitalize(middleName.trim());
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
    
    public List<AuthToken> getAuthTokens() {
        return authTokens;
    }

    public void setAuthTokens(List<AuthToken> authTokens) {
        this.authTokens = authTokens;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    public Account getAccount(int i) {
        if (this.accounts == null)
            return null;
        return accounts.get(i);
    }
    
    public void addAccount(Account account) {
        if (this.accounts == null)
            this.accounts = new ArrayList<>();
        this.accounts.add(account);
    }
    
    public PinReset getPinReset() {
        return pinReset;
    }

    public void setPinReset(PinReset pinReset) {
        this.pinReset = pinReset;
    }
    
    
    
    public String getPhoneNumberVerificationOTP() {
        return phoneNumberVerificationOTP;
    }

    public void setPhoneNumberVerificationOTP(String phoneNumberVerificationOTP) {
        this.phoneNumberVerificationOTP = phoneNumberVerificationOTP;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
    
    
}


