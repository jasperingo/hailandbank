
package hailandbank.filters;

import hailandbank.entities.AuthToken;
import hailandbank.entities.User;
import hailandbank.utils.MyResponse;
import static hailandbank.utils.Helpers.__;
import java.sql.SQLException;
import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;


@Auth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    private static final String SCHEME = "Bearer";
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (!isFormat(authHeader)) {
            abort(requestContext);
        } else {
        
            String token = authHeader.substring(SCHEME.length()).trim();

            int result = validate(token, requestContext);


            if (result == 1) {
                abort(requestContext);
            } else if (result == 2) {
                abortServer(requestContext);
            }
        }
    }
    
    private boolean isFormat(String auth) {
        return auth != null && auth.toLowerCase().startsWith(SCHEME.toLowerCase()+" ");
    }
    
    private void abort(ContainerRequestContext requestContext) {
        System.out.println(" i dot get AFTER");
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, SCHEME+" realm=\"jwt\"")
                        .entity(MyResponse.error(__("errors.unauthenticated")))
                        .build()
        );
    }
    
    private void abortServer(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(MyResponse.error(__("errors.unknown")))
                        .build()
        );
    }
    
    private int validate(String token, ContainerRequestContext requestContext){
        // JWT VALIDATION
        try {
            User user = AuthToken.findUserWhenNotExpired(token);
            requestContext.setProperty("user", user);
            return 0;
        } catch (NotFoundException ex) {
            return 1;
        } catch (SQLException ex) {
            return 2;
        }
    }

    
    
}


