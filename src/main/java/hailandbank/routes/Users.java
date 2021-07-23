
package hailandbank.routes;


import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
import hailandbank.entities.User;
import hailandbank.filters.Auth;
import hailandbank.filters.CAuth;
import hailandbank.filters.MAuth;
import hailandbank.resources.UserResource;
import hailandbank.utils.InputErrorException;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
    
    @GET
    @Path("holla")
    public String home() {
        return "running";
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
    @Path("customer/signin")
    public Response signIn(Customer user) throws InputErrorException, SQLException {
        return resource.signIn(user);
    }
    
    @POST
    @Path("merchant/signin")
    public Response signIn(Merchant user) throws InputErrorException, SQLException {
        return resource.signIn(user);
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
    @Path("update-pin")
    public Response updatePin(User user) throws InputErrorException, SQLException {
        return putAuthUser().updatePin(user);
    }
    
    @MAuth
    @PUT
    @Path("merchant/update-name")
    public Response updateName(Merchant user) throws InputErrorException, SQLException {
        return putAuthUser().updateName(user);
    }
    
    @MAuth
    @PUT
    @Path("merchant/update-address")
    public Response updateAddress(Merchant user) throws InputErrorException, SQLException {
        return putAuthUser().updateAddress(user);
    }
    
    @CAuth
    @PUT
    @Path("customer/update-address")
    public Response updateAddress(Customer user) throws InputErrorException, SQLException {
        return putAuthUser().updateAddress(user);
    }
    
    @MAuth
    @GET
    @Path("merchant")
    public Response getMerchantData() throws SQLException {
        return putAuthUser().getMerchantData();
    }
    
    @CAuth
    @GET
    @Path("customer")
    public Response getCustomerData() throws SQLException {
        return putAuthUser().getCustomerData();
    }
    
    
    
    
}






