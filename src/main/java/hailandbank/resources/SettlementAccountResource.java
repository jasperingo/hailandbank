
package hailandbank.resources;

import hailandbank.db.SettlementAccountDb;
import hailandbank.entities.SettlementAccount;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.InputData;
import hailandbank.utils.InputErrorException;
import hailandbank.utils.MyResponse;
import java.util.HashMap;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;


public class SettlementAccountResource extends Resource {
    
    public static SettlementAccountResource get() {
        return new SettlementAccountResource();
    }
    
    public static SettlementAccountResource with(User user) {
        SettlementAccountResource res = new SettlementAccountResource();
        res.setAuthUser(user);
        return res;
    }
    
    public Response add(SettlementAccount sa) throws InputErrorException, InternalServerErrorException {
        
        final HashMap<String, InputData> errors = new HashMap<>();
        
        // VALIDATE THAT BANK ACCOUNT EXISTS :)
        if (sa.getBankName() == null || sa.getBankName().isEmpty()) {
            errors.put("bank_name", InputData.with(sa.getBankName(), AppStrings.get("errors.invalid_field")));
        }
        
        if (sa.getNumber() == null || sa.getNumber().length() != 10) {
            errors.put("number", InputData.with(sa.getNumber(), AppStrings.get("errors.invalid_field")));
        }
        
        if (sa.getType() == null || sa.getType().isEmpty()) {
            errors.put("type", InputData.with(sa.getType(), AppStrings.get("errors.invalid_field")));
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        sa.setMerchant(getAuthMerchant());
        
        SettlementAccountDb.upsert(sa);
        
        sa.setMerchant(null);
        
        return Response.ok(
                MyResponse.success(AppStrings.get("success.credentials"), sa)
        ).build();
    }
    
    
    
}

