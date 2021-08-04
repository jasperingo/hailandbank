
package hailandbank.resources;

import hailandbank.entities.Order;
import hailandbank.entities.User;
import hailandbank.locales.AppStrings;
import hailandbank.db.AccountDb;
import hailandbank.db.OrderDb;
import hailandbank.entities.Account;
import hailandbank.utils.FormFieldData;
import hailandbank.utils.MyUtils;
import hailandbank.utils.InputErrorException;
import hailandbank.utils.MyResponse;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;


public class OrderResource extends Resource {
    
    public static OrderResource get() {
        return new OrderResource();
    }
    
    public static OrderResource with(User user) {
        OrderResource res = new OrderResource();
        res.setAuthUser(user);
        return res;
    }
    
    
    public Response place(Order order) throws InputErrorException, InternalServerErrorException {
        
        List<FormFieldData> errors = new ArrayList<>();
        
        Account account = AccountDb.findByUser(getAuthUser().getId());
        account.setUser(getAuthUser());
        
        if (order.getType() == null || order.getType().isEmpty() 
                || order.getMode() == null || order.getMode().isEmpty()
                || !MyUtils.orderTypeIsValid(order.getType(), order.getMode())) {
            errors.add(new FormFieldData("type", order.getType(), AppStrings.get("errors.order_type_invalid")));
        }
        
        if (order.getType().equals(Order.Type.CASH_DELIVERY.getValue())) {
            if (order.getAmount() < Order.MINIMUM_AMOUNT) {
                errors.add(new FormFieldData("amount", order.getAmount(), AppStrings.get("errors.order_amount_minimum_invalid")));
            } else if (order.getAmount() > Order.MAXIMUM_AMOUNT) {
                errors.add(new FormFieldData("amount", order.getAmount(), AppStrings.get("errors.order_amount_maximum_invalid")));
            } else if ((order.getAmount()+Order.CHARGE) > account.getBalance()) {
                errors.add(new FormFieldData("amount", order.getAmount(), AppStrings.get("errors.order_amount_account_balance_invalid")));
            }
        }
        
        if (order.getAddressStreet() == null || order.getAddressStreet().isEmpty()) {
            errors.add(new FormFieldData("address_street", order.getAddressStreet(), AppStrings.get("errors.address_street_invalid")));
        }
        
        if (order.getAddressState() == null || order.getAddressState().isEmpty()) {
            errors.add(new FormFieldData("address_state", order.getAddressState(), AppStrings.get("errors.address_state_invalid")));
        }
        
        if (order.getAddressCity() == null || order.getAddressCity().isEmpty()) {
            errors.add(new FormFieldData("address_city", order.getAddressCity(), AppStrings.get("errors.address_city_invalid")));
        }
        
        if (order.getAccount() == null || order.getAccount().getId() < 1 ||  
                order.getAccount().getId() != account.getId()) {
            errors.add(new FormFieldData("account", (order.getAccount() == null ? 0 : order.getAccount().getId()), AppStrings.get("errors.account_is_invalid")));
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        order.setCharge(Order.CHARGE);
        order.setStatus(Order.STATUS_PENDING);
        order.setAccount(account);
        OrderDb.insert(order);
        
        order.setAccount(null);
        
        return Response.ok(MyResponse.success(AppStrings.get("success.order_inserted"), order)).build();
    }

    public Response getList(int pageStart, int pageLimit) {
        List<Order> list = OrderDb.findList(getAuthUser(), pageStart, pageLimit);
        return Response.ok(MyResponse.success(list)).build();
    }
    
    public Response getCustomersList(int pageStart, int pageLimit) {
        List<Order> list = OrderDb.findCustomersList(getAuthUser(), pageStart, pageLimit);
        return Response.ok(MyResponse.success(list)).build();
    }
    
    public Response getPendingList(int pageStart, int pageLimit) {
        List<Order> list = OrderDb.findPending(getAuthUser(), pageStart, pageLimit);
        return Response.ok(MyResponse.success(list)).build();
    }

    public Response getPendingCount() {
        long count = OrderDb.countPending(getAuthUser());
        return Response.ok(MyResponse.success(count)).build();
    }
    
    public Response process(Order order) {
        
        Account account = null;
                
        List<FormFieldData> errors = new ArrayList<>();
        
        if (order.getMerchantAccount() == null) {
            errors.add(new FormFieldData("account", 0, AppStrings.get("errors.account_is_invalid")));
        } else {
            account = AccountDb.findByUserAndId(getAuthUser().getId(), order.getMerchantAccount().getId());
            
            if (account == null) {
                errors.add(new FormFieldData("account", 0, AppStrings.get("errors.account_is_invalid")));
            }
        }
        
        order = OrderDb.findById(order.getId());
        
        if (order == null) {
            
            errors.add(new FormFieldData("order", 0, AppStrings.get("errors.order_invalid")));
            
        } else {
            
            if (order.getMerchantAccount().getId() > 1) {
                errors.add(new FormFieldData("order", order.getId(), AppStrings.get("errors.order_being_processed")));
            } else {
                order.setMerchantAccount(account);
            }

            if (account != null && order.getType().equals(Order.Type.CASH_PICK_UP.getValue()) && 
                    (order.getAmount()+Order.SERVICE_FEE) > account.getBalance()) {
                errors.add(new FormFieldData("account", account.getBalance(), AppStrings.get("errors.account_balance_is_low")));
            }
            
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        
        OrderDb.updateToProcessing(order);
        
        
        return Response.ok(MyResponse.success(AppStrings.get("success.order_processing"))).build();
    }
    
    
    public Response fulfilForMerchant(Order order) {
        
        List<FormFieldData> errors = new ArrayList<>();
        
        Account account = AccountDb.findByUser(getAuthMerchant().getId());
        
        order = OrderDb.findById(order.getId());
        
        if (order == null || !order.getStatus().equals(Order.STATUS_PROCESSING)) {
            
            errors.add(new FormFieldData("order", 0, AppStrings.get("errors.order_invalid")));
            
        } else {
            
            if (order.getMerchantAccount().getId() != account.getId()) {
                errors.add(new FormFieldData("order", order.getId(), AppStrings.get("errors.order_invalid")));
            }
            
            if (!order.getType().equals(Order.Type.CASH_PICK_UP.getValue())) {
                errors.add(new FormFieldData("account", account.getBalance(), AppStrings.get("errors.order_invalid")));
            }
            
            if ((order.getAmount()+Order.SERVICE_FEE) > account.getBalance()) {
                errors.add(new FormFieldData("account", account.getBalance(), AppStrings.get("errors.account_balance_is_low")));
            }
            
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        OrderDb.updateToFulfil(order);
        
       return Response.ok(MyResponse.success(AppStrings.get("success.order_fulfilled"))).build();
    }
    
    
    public Response fulfilForCustomer(Order order) {
        
        List<FormFieldData> errors = new ArrayList<>();
        
        Account account = AccountDb.findByUser(getAuthCustomer().getId());
        
        order = OrderDb.findById(order.getId());
        
        if (order == null || !order.getStatus().equals(Order.STATUS_PROCESSING)) {
            
            errors.add(new FormFieldData("order", 0, AppStrings.get("errors.order_invalid")));
            
        } else {
            
            if (order.getAccount().getId() != account.getId()) {
                errors.add(new FormFieldData("order", order.getId(), AppStrings.get("errors.order_invalid")));
            }
            
            if (!order.getType().equals(Order.Type.CASH_DELIVERY.getValue())) {
                errors.add(new FormFieldData("account", account.getBalance(), AppStrings.get("errors.order_invalid")));
            }
            
        }
        
        if (!errors.isEmpty()) {
            throw new InputErrorException(errors);
        }
        
        OrderDb.updateToFulfil(order);
        
       return Response.ok(MyResponse.success(AppStrings.get("success.order_fulfilled"))).build();
    }
    
    
    
    
    
}



