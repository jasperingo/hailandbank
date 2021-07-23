
package hailandbank.entities;



import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "action")
public class Action extends Entity {
    
    public static Action SIGN_UP = new Action(1);
        
    public static Action SIGN_IN = new Action(2);
        
    public static Action SIGN_OUT = new Action(3);
        
    public static Action FORGOT_PIN = new Action(4);
        
    public static Action RESET_PIN = new Action(5);
        
    public static Action UPDATE_ADDRESS = new Action(6);
        
    public static Action UPDATE_PIN = new Action(7);
    
    public static Action UPDATE_MERCHANT_NAME = new Action(8);
    
    
    public static final String TABLE = "actions";
    
    
    private String type;
    
    private String description;
    
    
    private Action(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
}





