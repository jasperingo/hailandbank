
package hailandbank.routes;


import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
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
    
    @Context 
    private ContainerRequestContext requestContainer;
    
    private final UserResource resource;
    
    public Users() {
        this.resource = new UserResource();
    }
    
    public UserResource putAuthUser() {
        Object user = requestContainer.getProperty("user");
        User auth = (user != null) ? (User) user : null;
        resource.setAuthUser(auth);
        return resource;
    }
    
    @POST
    @Path("customer/signup")
    public Response signUp(Customer user) throws InputErrorException, SQLException {
        return resource.signUp(user);
    }
    
    @POST
    @Path("merchant/signup")
    public Response signUp(Merchant user) throws InputErrorException, SQLException {
        return resource.signUp(user);
    }
    
    @POST
    @Path("signin")
    public Response signIn(Customer user) throws InputErrorException, SQLException {
        return resource.signIn(user);
    }
    
    @POST
    @Path("signin")
    public Response signIn(Merchant user) throws InputErrorException, SQLException {
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
        return putAuthUser().updateAddress(user);
    }
    
    @Auth
    @PUT
    @Path("update-pin")
    public Response updatePin(User user) {
        return null;
    }
    
    
    
    
    
}






