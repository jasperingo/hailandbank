
package hailandbank.locales;

import java.util.ListResourceBundle;


public class AppStrings extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        
        return new Object[][] {
            {"app_name", "HailAndBank"},

            {"success.signup", "Welcome to HailAndBank."},
            {"success.credentials", "Credientials are correct."},
            {"success.forgotpin", "Pin reset request has been sent."},
            {"success.pinreset", "Pin has been reset."},
            {"success.address_updated", "Address has been updated."},
            {"success.pin_updated", "Pin has been updated."},
            
            {"errors.request_body_invalid", "Request body is invalid."},
            {"errors.input_fields", "Input fields have errors."},
            {"errors.insert_user", "User failed to be inserted into the database."},
            {"errors.insert_auth_token", "Auth token failed to be inserted into the database."},
            {"errors.insert_account", "Account failed to be inserted into the database."},
            {"errors.insert_pin_reset", "User pin reset failed to be inserted into the database."},
            {"errors.unknown", "An unknown error occured."},
            {"errors.unauthenticated", "Authentication failed."},
            {"errors.credentials", "Credientials are incorrect."},
            {"errors.user_not_found", "User do not exist."},
            {"errors.required_field", "Field is required."},
            {"errors.user_type_invalid", "Type is invalid."},
            {"errors.phone_number_invalid", "Phone number is invalid."},
            {"errors.pin_invalid", "Pin is invalid."},
            {"errors.pin_reset_token_invalid", "Pin reset token is invalid"},
            {"errors.pin_reset_failed", "Pin reset failed."},
            {"errors.address_street_invalid", "Street address is invalid"},
            {"errors.address_city_invalid", "City is invalid"},
            {"errors.address_state_invalid", "State is invalid"},
            {"errors.address_update_failed", "Address failed to be updated in the database."},
        };
    }
    
}
