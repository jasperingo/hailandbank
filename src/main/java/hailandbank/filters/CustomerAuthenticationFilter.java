
package hailandbank.filters;

import hailandbank.db.AuthTokenDb;
import hailandbank.entities.Customer;
import java.sql.SQLException;
import javax.annotation.Priority;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;



@CAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class CustomerAuthenticationFilter extends AuthenticationFilter {
    
    @Override
    protected int validate(String token, ContainerRequestContext requestContext){
        // JWT VALIDATION
        try {
            Customer user = AuthTokenDb.findCustomerWhenNotExpired(token);
            requestContext.setProperty("user", user);
            return 0;
        } catch (NotFoundException ex) {
            return 1;
        } catch (InternalServerErrorException ex) {
            return 2;
        }
    }

    
}

