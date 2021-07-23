
package hailandbank.entities;


import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "pin_reset")
public class PinReset extends UserToken {
    
    public static final String TABLE = "pin_resets";
    
    public static final int TOKEN_LEN = 6;
    
    public static final int TOKEN_DURATION = (1000*60*15); //15 minutes
    
    
}


