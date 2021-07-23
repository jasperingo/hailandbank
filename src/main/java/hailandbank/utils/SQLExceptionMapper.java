
package hailandbank.utils;

import java.sql.SQLException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

    @Override
    public Response toResponse(SQLException e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(MyResponse.error(e.getMessage()))
                .build();
    }
    
}




