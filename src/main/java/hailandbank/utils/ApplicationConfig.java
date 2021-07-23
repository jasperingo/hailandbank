
package hailandbank.utils;

import org.glassfish.jersey.jackson.JacksonFeature;
import java.util.Set;
import org.glassfish.jersey.server.ResourceConfig;


public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig(Set<Class<?>> classes) {
        super(classes);
        register(JacksonFeature.class);
    }

}


