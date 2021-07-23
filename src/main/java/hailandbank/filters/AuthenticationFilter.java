
package hailandbank.filters;

import hailandbank.db.AuthTokenDb;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.utils.MyResponse;
import hailandbank.utils.MyUtils;
import javax.annotation.Priority;
import javax.ws.rs.InternalServerErrorException;
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
public class AuthenticationFilter extends MyFilter implements ContainerRequestFilter {
    
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
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, SCHEME+" realm=\"jwt\"")
                        .entity(MyResponse.error(AppStrings.get("errors.unauthenticated")))
                        .type(MyUtils.getRequestAcceptedMedia(headers))
                        .build()
        );
    }
    
    private void abortServer(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(MyResponse.error("HTTP 500 Internal Server Error"))
                        .type(MyUtils.getRequestAcceptedMedia(headers))
                        .build()
        );
    }
    
    protected int validate(String token, ContainerRequestContext requestContext){
        // JWT VALIDATION
        try {
            User user = AuthTokenDb.findUserWhenNotExpired(token);
            requestContext.setProperty("user", user);
            return 0;
        } catch (NotFoundException ex) {
            return 1;
        } catch (InternalServerErrorException ex) {
            return 2;
        }
    }
    
}


