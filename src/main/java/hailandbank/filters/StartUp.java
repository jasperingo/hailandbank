
package hailandbank.filters;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;



@Provider
public class StartUp implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        
    }
    
}


