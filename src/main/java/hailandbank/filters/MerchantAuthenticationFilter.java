
package hailandbank.filters;

import hailandbank.db.AuthTokenDb;
import hailandbank.entities.Merchant;
import javax.annotation.Priority;
import javax.ws.rs.InternalServerErrorException;
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
            Merchant user = AuthTokenDb.findMerchantWhenNotExpired(token);
            requestContext.setProperty("user", user);
            return 0;
        } catch (NotFoundException ex) {
            return 1;
        } catch (InternalServerErrorException ex) {
            return 2;
        }
    }

    
}


