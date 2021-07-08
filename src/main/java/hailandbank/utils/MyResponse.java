
package hailandbank.utils;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class MyResponse {
    
    public static final String TYPE_ERROR = "error";
    
    public static final String TYPE_SUCCESS = "success";
    
    private String status;
    
    private String message;
    
    private Object data;
    
    private List<Object> dataList;
    
    
    public MyResponse() {
        
    }
    
    public MyResponse(String status) {
        this.status = status;
    }
    
    public MyResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }
    
    public static MyResponse error(String message) {
        return error(message, null, null);
    }
    
    public static MyResponse error(Object data) {
        return error(null, data, null);
    }
    
    public static MyResponse error(List<Object> dataList) {
        return error(null, null, dataList);
    }
    
    public static MyResponse error(String message, Object data) {
        return error(message, data, null);
    }
    
    public static MyResponse error(String message, List<Object> dataList) {
        return error(message, null, dataList);
    }
    
    public static MyResponse error(String message, Object data, List<Object> dataList) {
        return instance(TYPE_ERROR, message, data, dataList);
    }
    
    
    public static MyResponse success(String message) {
        return success(message, null, null);
    }
    
    public static MyResponse success(Object data) {
        return success(null, data, null);
    }
    
    public static MyResponse success(List<Object> dataList) {
        return success(null, null, dataList);
    }
    
    public static MyResponse success(String message, Object data) {
        return success(message, data, null);
    }
    
    public static MyResponse success(String message, List<Object> dataList) {
        return success(message, null, dataList);
    }
    
    public static MyResponse success(String message, Object data, List<Object> dataList) {
        return instance(TYPE_SUCCESS, message, data, dataList);
    }
    
    
    public static MyResponse instance(String status, String message, Object data, List<Object> dataList) {
        
        MyResponse response = new MyResponse(status, message);
        
        response.setData(data);
        
        response.setDataList(dataList);
        
        return response;
    }
    
}




