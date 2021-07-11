
package hailandbank.utils;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class InputData {
    
    private String value;
    
    private String error;
    
    public InputData(String value, String error) {
        this.setValue(value);
        this.error = error;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? "" : value;
    }
    
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    public static InputData with(String value, String error) {
        return new InputData(value, error);
    }
    
}

