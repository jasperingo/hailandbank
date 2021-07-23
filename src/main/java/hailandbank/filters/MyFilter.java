
package hailandbank.filters;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;


public class MyFilter {
    
    @Context 
    protected HttpHeaders headers;
    
    
}
