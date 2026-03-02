package com.oceanview.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.oceanview.webservice.AuthResource;
import com.oceanview.webservice.ReservationResource;
import com.oceanview.webservice.GuestResource;
import com.oceanview.webservice.RoomResource;
import com.oceanview.webservice.BillResource;
import com.oceanview.webservice.HelpResource;
import com.oceanview.webservice.ReportResource;

/**
 * JAX-RS Application Configuration.
 * Configures the RESTful web services for the application.
 * 
 * @author Ocean View Resort Development Team
 * @version 1.0
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // Register REST resources
        resources.add(AuthResource.class);
        resources.add(ReservationResource.class);
        resources.add(GuestResource.class);
        resources.add(RoomResource.class);
        resources.add(BillResource.class);
        resources.add(HelpResource.class);
        resources.add(ReportResource.class);
        
        return resources;
    }
}
