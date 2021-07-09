
package hailandbank.filters;


import static hailandbank.utils.Helpers.__;
import hailandbank.utils.MyResponse;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;


@Provider
public class RequestEntityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if ((requestContext.getMethod().equals(HttpMethod.POST) || requestContext.getMethod().equals(HttpMethod.PUT) ||
                requestContext.getMethod().equals(HttpMethod.DELETE)) && !requestContext.getUriInfo().getPath().equals("users/signout"))
            validate(requestContext);
    }

    private void validate(ContainerRequestContext requestContext) {
        try {
            String json = IOUtils.toString(requestContext.getEntityStream(), "UTF-8");
            JSONValue.parseWithException(json);
            InputStream in = IOUtils.toInputStream(json, "UTF-8");
            requestContext.setEntityStream(in);
        } catch (IOException ex) {
            abortServer(requestContext);
        } catch (ParseException ex) {
            abort(requestContext);
        }
    }
    
    private void abort(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.BAD_REQUEST)
                        .entity(MyResponse.error(__("errors.no_request_body")))
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
}







