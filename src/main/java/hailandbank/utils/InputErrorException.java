
package hailandbank.utils;

import static hailandbank.utils.Helpers.__;
import java.util.HashMap;


public class InputErrorException extends Exception {
    
    public final HashMap<String, InputData> errors;
    
    public InputErrorException(String message) {
        super(message);
        errors = null;
    }
    
    public InputErrorException(HashMap<String, InputData> errors) {
        super(__("errors.input_fields"));
        this.errors = errors;
    }
    
    
}

