
package hailandbank.resources;

import hailandbank.db.AccountDb;
import hailandbank.db.AuthTokenDb;
import hailandbank.db.CustomerDb;
import hailandbank.db.MerchantDb;
import hailandbank.db.PinResetDb;
import hailandbank.db.SettlementAccountDb;
import hailandbank.db.UserDb;
import hailandbank.entities.Account;
import hailandbank.entities.AuthToken;
import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
import hailandbank.entities.PinReset;
import hailandbank.entities.SettlementAccount;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyUtils;
import javax.ws.rs.core.Response;
import hailandbank.utils.InputData;
import hailandbank.utils.InputErrorException;
import hailandbank.utils.MyResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;


public class UserResource extends Resource {
    
    public static UserResource get() {
        return new UserResource();
    }
    
    public static UserResource with(User user) {
        UserResource res = new UserResource();
        res.setAuthUser(user);
        return res;
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
                ((user instanceof Merchant && user.getType().equals(User.TYPE_MERCHANT)) || 
                (user instanceof Customer && user.getType().equals(User.TYPE_CUSTOMER)));
    }
    
    private void generateAccount(User user) {
        Account account = new Account();
        account.setUser(user);
        account.setType(Account.TYPE_SAVINGS);
        account.setNumber(MyUtils.generateAccountNumber());
        user.addAccount(account);
    }
    
    
    public Response signUp(User user) throws InputErrorException, InternalServerErrorException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (!typeIsValid(user)) {
            errors.put("type", InputData.with(user.getType(), AppStrings.get("errors.user_type_invalid")));
        }
        
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            errors.put("first_name", InputData.with(user.getFirstName(), AppStrings.get("errors.required_field")));
        }
        
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            errors.put("last_name", InputData.with(user.getLastName(), AppStrings.get("errors.required_field")));
        }
        
        if (!phoneNumberIsValid(user)) {
            errors.put("phone_number", InputData.with(user.getPhoneNumber(), AppStrings.get("errors.phone_number_invalid")));
        }
        
        if (!pinIsValid(user)) {
            errors.put("pin", InputData.with(user.getPin(), AppStrings.get("errors.pin_invalid")));
        }
        
        //validate PHONE NUMBER OTP
        
        if (!errors.containsKey("phone_number")) {
            try {
                UserDb.findIdByPhoneNumber(user.getPhoneNumber());
                errors.put("phone_number", InputData.with(user.getPhoneNumber(), AppStrings.get("errors.phone_number_invalid")));
            } catch (NotFoundException ex) {}
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        MyUtils.hashPassword(user);
        
        generateAccount(user);
        
        generatedAuthToken(user);
        
        if (user.getType().equals(User.TYPE_MERCHANT)) {
            
            ((Merchant)user).setLevel(Merchant.Level.ONE.getValue());
            ((Merchant)user).setTransactionLimit(Merchant.Level.ONE.getTransactionLimit());
            ((Merchant)user).setStatus(Merchant.STATUS_INACTIVE);
            ((Merchant)user).setCode(MyUtils.generateToken(Merchant.CODE_LEN, Merchant.ALLOWED_CODE_CHARS));
            
            MerchantDb.insert((Merchant)user);
            
        } else {
            
            CustomerDb.insert((Customer)user);
        }
        
        user.getAuthToken().setUser(null);
        
        return Response.created(URI.create("v1/"+user.getType()+"/"+user.getId()))
                .entity(MyResponse.success(AppStrings.get("success.signup"), user.getAuthToken()))
                .build();
    }
    
    
    public Response signIn(User user) throws BadRequestException, InternalServerErrorException {
        
        BadRequestException inputException = new BadRequestException(AppStrings.get("errors.credentials"));
        
        if (!phoneNumberIsValid(user) || !pinIsValid(user) || !typeIsValid(user)) {
            throw inputException;
        }
        
        String pin = user.getPin();
        
        try {
            
            if (user.getType().equals(User.TYPE_MERCHANT)) {
                user = MerchantDb.find(user.getPhoneNumber());
            } else {
                user = CustomerDb.find(user.getPhoneNumber());
            }
             
        } catch (NotFoundException ex) {
            throw inputException;
        }
        
        if (!MyUtils.comparePassword(pin, user.getPin())) {
            throw inputException;
        }
        
        generatedAuthToken(user);
        
        AuthTokenDb.insertWithAction(user.getAuthToken());
        
        user.getAuthToken().setUser(null);
        
        return Response.ok(MyResponse.success(AppStrings.get("success.credentials"), user.getAuthToken())).build();
    }
    
    public void generatedAuthToken(User user) {
        //implement JWT AUTH
        AuthToken auth = new AuthToken();
        auth.setUser(user);
        auth.setToken(MyUtils.generateToken(AuthToken.TOKEN_LEN, AuthToken.ALLOWED_TOKEN_CHARS));
        auth.setExpires(MyUtils.generateExpiringDate(AuthToken.TOKEN_DURATION));
        user.setAuthToken(auth);
    }
    
    public Response forgotPin(User user) throws BadRequestException, InternalServerErrorException {
        
        BadRequestException inputException = new BadRequestException(AppStrings.get("errors.phone_number_invalid"));
        
        if (!phoneNumberIsValid(user)) {
            throw inputException;
        }
        
        try {
            user = UserDb.find(user.getPhoneNumber());
        } catch (NotFoundException ex) {
            throw inputException;
        }
        
        PinReset pr = new PinReset();
        pr.setUser(user);
        pr.setToken(MyUtils.generateToken(PinReset.TOKEN_LEN, PinReset.ALLOWED_TOKEN_CHARS));
        pr.setExpires(MyUtils.generateExpiringDate(PinReset.TOKEN_DURATION));
        
        PinResetDb.insert(pr);
        
        //SEND TOKEN TO PHONE NUMBER VIA SMS
        
        pr.setUser(null);
        pr.setToken(null);
        
        return Response.ok(MyResponse.success(AppStrings.get("success.forgotpin"), pr)).build();
        
    }
    
    
    public Response resetPin(User user) throws InputErrorException, InternalServerErrorException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (!phoneNumberIsValid(user)) {
            errors.put("phone_number", InputData.with(user.getPhoneNumber(), AppStrings.get("errors.phone_number_invalid")));
        }
        
        if (!pinIsValid(user)) {
            errors.put("pin", InputData.with(user.getPin(), AppStrings.get("errors.pin_invalid")));
        }
        
        String token = (user.getPinReset() == null ? "" : user.getPinReset().getToken());
        
        if (token == null || token.length() != PinReset.TOKEN_LEN) {
            errors.put("token", InputData.with(token, AppStrings.get("errors.pin_reset_token_invalid")));
        }
        
        if (!errors.containsKey("token") && !errors.containsKey("phoneNumber")) {
            try {
                long userId = PinResetDb.findUserIdWhenNotExpired(token, user.getPhoneNumber());
                user.setId(userId);
            } catch (NotFoundException ex) {
                errors.put("token", InputData.with(token, AppStrings.get("errors.pin_reset_token_invalid")));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        MyUtils.hashPassword(user);
        
        UserDb.updatePin(user, true);
        
        return Response.ok(MyResponse.success(AppStrings.get("success.pinreset"))).build();
    }
    
    public Response updatePin(User data) throws InputErrorException, InternalServerErrorException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (!pinIsValid(data) || !MyUtils.comparePassword(data.getPin(), getAuthUser().getPin())) {
            errors.put("pin", InputData.with(data.getPin(), AppStrings.get("errors.pin_invalid")));
        }
        
        if (!pinIsValid(data.getNewPin())) {
            errors.put("new_pin", InputData.with(data.getPin(), AppStrings.get("errors.pin_invalid")));
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        getAuthUser().setPin(MyUtils.hashPassword(data.getNewPin()));
        
        UserDb.updatePin(getAuthUser());
        
        return Response.ok(MyResponse.success(AppStrings.get("success.pin_updated"))).build();
    }
    
    
    public Response updateAddress(User data) throws InputErrorException, InternalServerErrorException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (data.getAddressStreet() == null || data.getAddressStreet().isEmpty()) {
            errors.put("address_street", InputData.with(data.getAddressStreet(), AppStrings.get("errors.address_street_invalid")));
        }
        
        if (data.getAddressCity() == null || data.getAddressCity().isEmpty()) {
            errors.put("address_city", InputData.with(data.getAddressCity(), AppStrings.get("errors.address_city_invalid")));
        }
        
        if (data.getAddressState() == null || data.getAddressState().isEmpty()) {
            errors.put("address_state", InputData.with(data.getAddressState(), AppStrings.get("errors.address_state_invalid")));
        }
        
        //verify address via a web service :)
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        getAuthUser().setAddressStreet(data.getAddressStreet());
        getAuthUser().setAddressCity(data.getAddressCity());
        getAuthUser().setAddressState(data.getAddressState());
        
        if (getAuthUser().getType().equals(User.TYPE_MERCHANT)) {
            MerchantDb.updateAddress(getAuthMerchant());
        } else if (getAuthUser().getType().equals(User.TYPE_CUSTOMER))
           CustomerDb.updateAddress(getAuthCustomer());
        
        return Response.ok(MyResponse.success(AppStrings.get("success.address_updated"))).build();
    }
    
    
    public Response updateName(Merchant data) throws InputErrorException, InternalServerErrorException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        if (data.getName() == null || data.getName().length() < 3) {
            errors.put("name", InputData.with(data.getName(), AppStrings.get("errors.merchant_name_invalid")));
        }
        
        if (errors.isEmpty()) {
            try {
                MerchantDb.find("name", data.getName());
                errors.put("name", InputData.with(data.getName(), AppStrings.get("errors.merchant_name_invalid")));
            } catch (NotFoundException ex) {}
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        getAuthMerchant().setName(data.getName());
        
        MerchantDb.updateName(getAuthMerchant());
        
        return Response.ok(MyResponse.success(AppStrings.get("success.merchant_name_updated"))).build();
    }
    
    public Response getMerchantData() throws InternalServerErrorException {
        List<SettlementAccount> sAccounts = SettlementAccountDb.findAllByMerchant(getAuthMerchant().getMerchantId());
        List<Account> accounts = AccountDb.findAllByUser(getAuthMerchant().getId());
        getAuthMerchant().setAccounts(accounts);
        getAuthMerchant().setSettlementAccounts(sAccounts);
        return getData();
    }
    
    public Response getCustomerData() throws InternalServerErrorException {
        List<Account> accounts = AccountDb.findAllByUser(getAuthCustomer().getId());
        getAuthCustomer().setAccounts(accounts);
        return getData();
    }
    
    public Response getData() {
        getAuthUser().setPin(null);
        getAuthUser().setAuthToken(null);
        return Response.ok(MyResponse.success(getAuthUser())).build();
    }
    
    
    
    
}



