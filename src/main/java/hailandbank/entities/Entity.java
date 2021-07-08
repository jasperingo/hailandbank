
package hailandbank.entities;

import hailandbank.utils.Helpers;

import java.sql.Connection;


public class Entity {
    
    private static Connection connection = null;

    protected static Connection getConnection() {
        if (connection == null) {
            connection = Helpers.getDBConnection();
        }
        
        return connection;
    }
    
}


