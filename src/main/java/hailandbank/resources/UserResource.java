
package hailandbank.resources;

import hailandbank.entities.Account;
import hailandbank.entities.AuthToken;
import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
import hailandbank.entities.PinReset;
import hailandbank.entities.User;
import hailandbank.utils.Helpers;
import javax.ws.rs.core.Response;
import static hailandbank.utils.Helpers.__;
import hailandbank.utils.InputData;
import hailandbank.utils.InputErrorException;
import hailandbank.utils.MyResponse;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.ws.rs.NotFoundException;


public class UserResource {
    
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
    
    
    public boolean phoneNumberIsValid(User user) {
        return user.getPhoneNumber() != null && user.getPhoneNumber().length() == 11;
    }
    
    public boolean pinIsValid(User user) {
        return pinIsValid(user.getPin());
    }
    
    public boolean pinIsValid(String pin) {
        return pin != null && Pattern.matches("\\d{4}", pin);
    }
    
    public boolean typeIsValid(User user) {
        return user.getType() != null &&
                (user.getType().equals(User.TYPE_MERCHANT) || user.getType().equals(User.TYPE_CUSTOMER));
    }
    
    private void generateAccount(User user) {
        Account account = new Account();
        account.setType(Account.TYPE_SAVINGS);
        account.setUser(user);
        account.generateNumber();
        user.addAccount(account);
    }
    
    public Response signUp(User user) throws InputErrorException, SQLException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (!typeIsValid(user)) {
            errors.put("type", InputData.with(user.getType(), __("errors.user_type_invalid")));
        }
        
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            errors.put("firstName", InputData.with(user.getFirstName(), __("errors.required_field")));
        }
        
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            errors.put("lastName", InputData.with(user.getLastName(), __("errors.required_field")));
        }
        
        if (!phoneNumberIsValid(user)) {
            errors.put("phoneNumber", InputData.with(user.getPhoneNumber(), __("errors.phone_number_invalid")));
        }
        
        if (!pinIsValid(user)) {
            errors.put("pin", InputData.with(user.getPin(), __("errors.pin_invalid")));
        }
        
        //validate PHONE NUMBER OTP
        
        if (!errors.containsKey("phoneNumber")) {
            try {
                User.find("phone_number", user.getPhoneNumber());
                errors.put("phoneNumber", InputData.with(user.getPhoneNumber(), __("errors.phone_number_invalid")));
            } catch (NotFoundException ex) {}
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        Helpers.hashPassword(user);
        
        generateAccount(user);
        
        generatedAuthToken(user);
        
        if (user.getType().equals(User.TYPE_MERCHANT)) {
            ((Merchant)user).setLevel(Merchant.Level.ONE.getValue());
            ((Merchant)user).setTransactionLimit(Merchant.Level.ONE.getTransactionLimit());
            ((Merchant)user).setStatus(Merchant.STATUS_INACTIVE);
            ((Merchant)user).setCode(Helpers.generateToken(Merchant.CODE_LEN, Merchant.ALLOWED_CODE_CHARS));
        }
        
        user.insert();
        
        user.setPin(null);
        user.getAccount(0).setUser(null);
        user.getAuthToken().setUser(null);
        
        return Response.created(URI.create("/users/"+user.getType()+"/"+user.getId()))
                .entity(MyResponse.success(__("success.signup"), user))
                .build();
    }
    
    
    public Response signIn(User user) throws InputErrorException, SQLException {
        
        InputErrorException inputEx = new InputErrorException(__("errors.credentials"));
        
        if (!phoneNumberIsValid(user) || !pinIsValid(user) || !typeIsValid(user)) {
            throw inputEx;
        }
        
        String pin = user.getPin();
        
        try {
            switch (user.getType()) {
                case User.TYPE_CUSTOMER:
                    user = Customer.find("phone_number", user.getPhoneNumber());
                    break;
                case User.TYPE_MERCHANT:
                    user = Merchant.find("phone_number", user.getPhoneNumber());
                    break;
                default:
                    user = User.find("phone_number", user.getPhoneNumber());
            }
        } catch (NotFoundException ex) {
            throw inputEx;
        }
        
        if (!Helpers.comparePassword(pin, user.getPin())) {
            throw inputEx;
        }
        
        generatedAuthToken(user);
        
        user.getAuthToken().insertWithAction();
        
        user.setPin(null);
        
        user.getAuthToken().setUser(null);
        
        return Response.ok(MyResponse.success(__("success.credentials"), user)).build();
    }
    
    public void generatedAuthToken(User user) {
        //implement JWT AUTH
        AuthToken auth = new AuthToken();
        auth.setUser(user);
        auth.setToken(Helpers.generateToken(AuthToken.TOKEN_LEN, AuthToken.ALLOWED_TOKEN_CHARS));
        auth.generateExpiringDate();
        user.setAuthToken(auth);
    }
    
    public Response forgotPin(User user) throws InputErrorException, SQLException {
        
        InputErrorException inputException = new InputErrorException(__("errors.phone_number_invalid"));
        
        if (!phoneNumberIsValid(user)) {
            throw inputException;
        }
        
        try {
            user = User.find("phone_number", user.getPhoneNumber());
        } catch (NotFoundException ex) {
            throw inputException;
        }
        
        PinReset pr = new PinReset();
        pr.setUser(user);
        pr.setToken(Helpers.generateToken(PinReset.TOKEN_LEN, PinReset.ALLOWED_TOKEN_CHARS));
        pr.generateExpiringDate();
        
        pr.insert();
        
        //SEND TOKEN TO PHONE NUMBER VIA SMS
        
        pr.setUser(null);
        pr.setToken(null);
        
        return Response.ok(MyResponse.success(__("success.forgotpin"), pr)).build();
        
    }
    
    
    public Response resetPin(User user) throws InputErrorException, SQLException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (!phoneNumberIsValid(user)) {
            errors.put("phoneNumber", InputData.with(user.getPhoneNumber(), __("errors.phone_number_invalid")));
        }
        
        if (!pinIsValid(user)) {
            errors.put("pin", InputData.with(user.getPin(), __("errors.pin_invalid")));
        }
        
        String token = (user.getPinReset() == null ? "" : user.getPinReset().getToken());
        
        if (token == null || token.length() != PinReset.TOKEN_LEN) {
            errors.put("token", InputData.with(token, __("errors.pin_reset_token_invalid")));
        }
        
        if (!errors.containsKey("token") && !errors.containsKey("phoneNumber")) {
            try {
                long userId = PinReset.findUserIdWhenNotExpired(token, user.getPhoneNumber());
                user.setId(userId);
            } catch (NotFoundException ex) {
                errors.put("token", InputData.with(token, __("errors.pin_reset_token_invalid")));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        Helpers.hashPassword(user);
        
        user.updatePin(true);
        
        return Response.ok(MyResponse.success(__("success.pinreset"))).build();
    }
    
    public Response updatePin(User data) throws InputErrorException, SQLException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (!pinIsValid(data) || !Helpers.comparePassword(data.getPin(), getAuthUser().getPin())) {
            errors.put("pin", InputData.with(data.getPin(), __("errors.pin_invalid")));
        }
        
        if (!pinIsValid(data.getNewPin())) {
            errors.put("newPin", InputData.with(data.getPin(), __("errors.pin_invalid")));
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        getAuthUser().setPin(Helpers.hashPassword(data.getNewPin()));
        
        getAuthUser().updatePin();
        
        return Response.ok(MyResponse.success(__("success.pin_update"))).build();
    }
    
    
    public Response updateAddress(User data) throws InputErrorException, SQLException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (data.getAddressStreet() == null || data.getAddressStreet().isEmpty()) {
            errors.put("addressStreet", InputData.with(data.getAddressStreet(), __("errors.address_street_invalid")));
        }
        
        if (data.getAddressCity() == null || data.getAddressCity().isEmpty()) {
            errors.put("addressCity", InputData.with(data.getAddressCity(), __("errors.address_city_invalid")));
        }
        
        if (data.getAddressState() == null || data.getAddressState().isEmpty()) {
            errors.put("addressState", InputData.with(data.getAddressState(), __("errors.address_state_invalid")));
        }
        
        if (errors.isEmpty()) {
            //verify address via a web service :)
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        getAuthUser().setAddressStreet(data.getAddressStreet());
        getAuthUser().setAddressCity(data.getAddressCity());
        getAuthUser().setAddressState(data.getAddressState());
        
        if (getAuthUser().getType().equals(User.TYPE_MERCHANT))
            getAuthMerchant().updateAddress();
        else if (getAuthUser().getType().equals(User.TYPE_CUSTOMER))
            getAuthCustomer().updateAddress();
            
        return Response.ok(MyResponse.success(__("success.address_updated"))).build();
    }
    
    
    public Response updateName(Merchant data) throws InputErrorException, SQLException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (data.getName() == null || data.getName().isEmpty()) {
            errors.put("name", InputData.with(data.getName(), __("errors.merchant_name_invalid")));
        }
        
        if (errors.isEmpty()) {
            try {
                Merchant.find("name", data.getName());
                errors.put("name", InputData.with(data.getName(), __("errors.merchant_name_invalid")));
            } catch (NotFoundException ex) {}
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        getAuthMerchant().setName(data.getName());
        
        getAuthMerchant().updateName();
        
        return Response.ok(MyResponse.success(__("success.merchant_name_updated"))).build();
    }
    
    
}



