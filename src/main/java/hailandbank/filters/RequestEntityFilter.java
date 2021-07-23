package hailandbank.filters;


import hailandbank.utils.MyResponse;
import hailandbank.utils.MyUtils;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;


@Provider
public class RequestEntityFilter extends MyFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (requestContext.getMethod().equals(HttpMethod.POST) || 
                requestContext.getMethod().equals(HttpMethod.PUT) ||
                requestContext.getMethod().equals(HttpMethod.DELETE))
            validate(requestContext);
    }
    
    private void validate(ContainerRequestContext requestContext) {
        try {
            String json = IOUtils.toString(requestContext.getEntityStream(), "UTF-8");
            if (json == null || json.isEmpty()) {
                throw new BadRequestException();
            }
            InputStream in = IOUtils.toInputStream(json, "UTF-8");
            requestContext.setEntityStream(in);
        } catch (IOException ex) {
            abortServer(requestContext);
        } catch (BadRequestException ex) {
            abort(requestContext);
        }
    }
    
    private void abort(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.BAD_REQUEST)
                        .entity(MyResponse.error("HTTP 400 Bad Request"))
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
}

