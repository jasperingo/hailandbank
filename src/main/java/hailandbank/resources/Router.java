
package hailandbank.resources;


import hailandbank.entities.Customer;
import hailandbank.entities.Merchant;
import hailandbank.entities.Order;
import hailandbank.entities.SettlementAccount;
import hailandbank.entities.Transaction;
import hailandbank.entities.User;
import hailandbank.filters.Auth;
import hailandbank.filters.CAuth;
import hailandbank.filters.MAuth;
import hailandbank.utils.InputErrorException;
import hailandbank.utils.MyResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("v1")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class Router {
    
    @Context 
    private ContainerRequestContext requestContainer;
    
    
    public Router() {
        
    }
    
    public User getAuthUser() {
        Object user = requestContainer.getProperty("user");
        User auth = (user != null) ? (User) user : null;
        return auth;
    }
    
    @GET
    @Path("home")
    public MyResponse home() {
        return MyResponse.success("Welcome");
    }
    
    @POST
    @Path("customer/signup")
    public Response signUp(Customer user) throws InputErrorException, InternalServerErrorException {
        return UserResource.get().signUp(user);
    }
    
    @POST
    @Path("merchant/signup")
    public Response signUp(Merchant user) throws InputErrorException, InternalServerErrorException {
        return UserResource.get().signUp(user);
    }
    
    @POST
    @Path("customer/signin")
    public Response signIn(Customer user) throws BadRequestException, InternalServerErrorException {
        return UserResource.get().signIn(user);
    }
    
    @POST
    @Path("merchant/signin")
    public Response signIn(Merchant user) throws BadRequestException, InternalServerErrorException {
        return UserResource.get().signIn(user);
    }
    
    @POST
    @Path("forgotpin")
    public Response forgotPin(User user) throws BadRequestException, InternalServerErrorException {
        return UserResource.get().forgotPin(user);
    }
    
    @POST
    @Path("resetpin")
    public Response resetPin(User user) throws InputErrorException, InternalServerErrorException {
        return UserResource.get().resetPin(user);
    }
    
    
    @Auth
    @PUT
    @Path("update-pin")
    public Response updatePin(User user) throws InputErrorException, InternalServerErrorException {
        return UserResource.with(getAuthUser()).updatePin(user);
    }
    
    @MAuth
    @PUT
    @Path("merchant/update-name")
    public Response updateName(Merchant user) throws InputErrorException, InternalServerErrorException {
        return UserResource.with(getAuthUser()).updateName(user);
    }
    
    @MAuth
    @PUT
    @Path("merchant/update-address")
    public Response updateAddress(Merchant user) throws InputErrorException, InternalServerErrorException {
        return UserResource.with(getAuthUser()).updateAddress(user);
    }
    
    @CAuth
    @PUT
    @Path("customer/update-address")
    public Response updateAddress(Customer user) throws InputErrorException, InternalServerErrorException {
        return UserResource.with(getAuthUser()).updateAddress(user);
    }
    
    @MAuth
    @GET
    @Path("merchant")
    public Response getMerchantData() throws InternalServerErrorException {
        return UserResource.with(getAuthUser()).getMerchantData();
    }
    
    @CAuth
    @GET
    @Path("customer")
    public Response getCustomerData() throws InternalServerErrorException {
        return UserResource.with(getAuthUser()).getCustomerData();
    }
    
    @MAuth
    @POST
    @Path("settlement-account/add")
    public Response addMerchantSettlementAccount(SettlementAccount sa) 
            throws InputErrorException, InternalServerErrorException {
        return SettlementAccountResource.with(getAuthUser()).add(sa);
    }
    
    @Auth
    @POST
    @Path("transaction/fund")
    public Response fundAccount(Transaction t) {
        return TransactionResource.with(getAuthUser()).add(t, true);
    }
    
    @MAuth
    @POST
    @Path("transaction/withdraw")
    public Response withdrawFromMerchantAccount(Transaction t) {
        return TransactionResource.with(getAuthUser()).add(t, false);
    }
    
    @Auth
    @GET
    @Path("transactions")
    public Response getTransactions(@QueryParam("page_start") int pageStart, @QueryParam("page_limit") int pageLimit) {
        return TransactionResource.with(getAuthUser()).getList(pageStart, pageLimit);
    }
    
    @Auth
    @POST
    @Path("order/place")
    public Response placeOrder(Order order) {
        return OrderResource.with(getAuthUser()).place(order);
    }
    
    @Auth
    @GET
    @Path("orders")
    public Response getOrders(@QueryParam("page_start") int pageStart, @QueryParam("page_limit") int pageLimit) {
        return OrderResource.with(getAuthUser()).getList(pageStart, pageLimit);
    }
    
    @MAuth
    @GET
    @Path("orders/customers")
    public Response getCustomersOrders(@QueryParam("page_start") int pageStart, @QueryParam("page_limit") int pageLimit) {
        return OrderResource.with(getAuthUser()).getCustomersList(pageStart, pageLimit);
    }
    
    @MAuth
    @GET
    @Path("orders/pending")
    public Response getPendingOrders(@QueryParam("page_start") int pageStart, @QueryParam("page_limit") int pageLimit) {
        return OrderResource.with(getAuthUser()).getPendingList(pageStart, pageLimit);
    }
    
    @MAuth
    @GET
    @Path("orders/pending/count")
    public Response getPendingOrdersCount() {
        return OrderResource.with(getAuthUser()).getPendingCount();
    }
    
    @MAuth
    @PUT
    @Path("order/process")
    public Response processOrder(Order order) {
        return OrderResource.with(getAuthUser()).process(order);
    }
    
    @MAuth
    @PUT
    @Path("order/fulfil/merchant")
    public Response fulfilOrderMerchant(Order order) {
        return OrderResource.with(getAuthUser()).fulfilForMerchant(order);
    }
    
    @CAuth
    @PUT
    @Path("order/fulfil/customer")
    public Response fulfilOrderCustomer(Order order) {
        return OrderResource.with(getAuthUser()).fulfilForCustomer(order);
    }
    
    
    
    
}






