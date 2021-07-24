
package hailandbank.entities;


import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "auth_token")
public class AuthToken extends UserToken {
    
    public static final String TABLE = "auth_tokens";
    
    public static final int TOKEN_LEN = 20;
    
    public static final int TOKEN_DURATION = (1000*60*60*6); //6 hours
    
    public static final String TABLE_COLUMNS = 
            "auth_tokens.id AS auth_id, "
            + "auth_tokens.user_id, "
            + "auth_tokens.token, "
            + "auth_tokens.expires, "
            + "auth_tokens.created_at";
    
    
}


