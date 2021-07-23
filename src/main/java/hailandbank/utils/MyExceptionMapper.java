
package hailandbank.utils;


import javax.json.bind.JsonbException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class MyExceptionMapper implements ExceptionMapper<Exception>{
    
    @Context 
    public HttpHeaders headers;
    
    @Override
    public Response toResponse(Exception e) {
        
        MediaType m = MyUtils.getRequestAcceptedMedia(headers);
        
        if (e instanceof WebApplicationException) {
            return Response.status(
                        ((WebApplicationException) e).getResponse().getStatus()
                    )
                    .entity(MyResponse.error(e.getMessage()))
                    .type(m)
                    .build();
        }
        
        if (e != null && e.getCause() instanceof JsonbException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MyResponse.error("HTTP 400 Bad Request"))
                    .type(m)
                    .build();
        }
        
        MyUtils.exceptionLogger(e, MyExceptionMapper.class.getName());
        
        return Response.serverError()
                .entity(MyResponse.error(e == null ? "Error!!!": e.getMessage()))
                .type(m)
                .build();
        
    }
    
}


