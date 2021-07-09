
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
import javax.ws.rs.PUT;
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
    
    public Users(@Context ContainerRequestContext requestContainer) {
        this.resource = new UserResource();
        Object user = requestContainer.getProperty("user");
        User authUser = (user != null) ? (User) user : null;
        this.resource.setAuthUser(authUser);
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
    public Response signout() throws SQLException {
        return resource.signout();
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
    
    @Auth
    @PUT
    @Path("update-address")
    public Response updateAddress(User user) throws InputErrorException, SQLException {
        return resource.updateAddress(user);
    }
    
    @Auth
    @PUT
    @Path("update-pin")
    public Response updatePin(User user) {
        return null;
    }
    
    
    
    
    
}






