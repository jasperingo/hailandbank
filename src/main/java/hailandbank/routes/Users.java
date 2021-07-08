
package hailandbank.routes;


import hailandbank.entities.User;
import hailandbank.filters.Auth;
import hailandbank.resources.UserResource;
import hailandbank.utils.InputErrorException;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Users {
    
    private final UserResource resource;
    
    public Users() {
        this.resource = new UserResource();
    }
    
    @POST
    @Path("signup")
    public Response signUp(User user) throws InputErrorException, SQLException {
        return resource.signUp(user);
    }
    
    @POST
    @Path("signin")
    public Response signIn(User user) throws InputErrorException, SQLException {
        return resource.signIn(user);
    }
    
    @Auth
    @DELETE
    @Path("signout")
    public Response signout(@Context ContainerRequestContext req) throws SQLException {
        return resource.signout((User)req.getProperty("user"));
    }
    
    @POST
    @Path("forgotpin")
    public Response forgotPin(User user) throws InputErrorException, SQLException {
        return resource.forgotPin(user);
    }
    
    
    @POST
    @Path("resetpin")
    public Response resetPin(User user) throws InputErrorException, SQLException {
        return resource.resetPin(user);
    }
    
    
    
}






