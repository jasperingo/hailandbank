
package hailandbank.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class InputErrorExceptionMapper implements ExceptionMapper<InputErrorException> {

    @Override
    public Response toResponse(InputErrorException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(MyResponse.error(e.getMessage(), e.errors))
                .build();
    }
    
}

