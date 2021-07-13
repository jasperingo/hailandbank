
package hailandbank.filters;

import hailandbank.entities.AuthToken;
import hailandbank.entities.Merchant;
import hailandbank.utils.Helpers;
import java.sql.SQLException;
import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;


@MAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class MerchantAuthenticationFilter extends AuthenticationFilter {
    
    @Override
    protected int validate(String token, ContainerRequestContext requestContext){
        // JWT VALIDATION
        try {
            Merchant user = AuthToken.findMerchantWhenNotExpired(token);
            requestContext.setProperty("user", user);
            return 0;
        } catch (NotFoundException ex) {
            return 1;
        } catch (SQLException ex) {
            Helpers.stackTracer(ex);
            return 2;
        }
    }

    
}
