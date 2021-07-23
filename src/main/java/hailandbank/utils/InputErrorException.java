
package hailandbank.utils;

import hailandbank.locales.AppStrings;
import java.util.HashMap;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;


public class InputErrorException extends BadRequestException {
    
    public InputErrorException(final HashMap<String, InputData> errors) {
        super(
            Response.status(Response.Status.BAD_REQUEST)
                    .entity(MyResponse.error(AppStrings.get("errors.input_fields"), errors))
                    .build()
        );
    }
    
}

