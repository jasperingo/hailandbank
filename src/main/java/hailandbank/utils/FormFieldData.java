
package hailandbank.utils;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "form_field_data")
public class FormFieldData {
    
    private String name;
    
    private String value;
    
    private String error;

    
    public FormFieldData(String name, String value, String error) {
        this.name = name;
        this.error = error;
        this.value = value == null ? "" : value;
    }
    
    public FormFieldData(String name, double value, String error) {
        this.name = name;
        this.error = error;
        this.value = String.valueOf(value);
    }
    
    public FormFieldData(String name, long value, String error) {
        this.name = name;
        this.error = error;
        this.value = String.valueOf(value);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    
}



