/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.traffic.rar.inbound;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javaeetutorial.traffic.rar.api.TrafficCommand;
import javaeetutorial.traffic.rar.api.TrafficListener;
import javax.resource.ResourceException;
import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

/* The activation spec used by the MDB to configure the resource adapter */
@Activation(
        messageListeners = { TrafficListener.class }
)
public class TrafficActivationSpec implements ActivationSpec, Serializable {

    private ResourceAdapter ra;
    @ConfigProperty()
    private String port;
    private Class beanClass;
    private Map<String,Method> commands;
    private static final long serialVersionUID = 1674967719558213103L;
    private static final Logger log = Logger.getLogger("TrafficActivationSpec");
    
    public TrafficActivationSpec() throws InvalidPropertyException {
        commands = new HashMap<>();
    }
    
    /* Port is set by the MDB using @ActivationConfigProperty */
    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }
    
    /* Set from the RA class and accessed by the traffic subscriber thread */
    public void setBeanClass(Class c) { beanClass = c; }
    public Class getBeanClass() { return beanClass; }
    
    /* Inspect the MDB class for methods with a custom annotation.
     * This allows the MDB business interface to be emtpy */    
    public void findCommandsInMDB() {
        log.info("[TrafficActivationSpec] findCommandsInMDB()");
        for (Method method : beanClass.getMethods()) {
            if (method.isAnnotationPresent(TrafficCommand.class)) {
                TrafficCommand tCommand = method.getAnnotation(TrafficCommand.class);
                commands.put(tCommand.name(), method);
            }
        }
        
        if (commands.isEmpty())
            log.info("No command annotations in MDB.");
        
        for (Method m : commands.values()) {
            for (Class c : m.getParameterTypes())
                if (c != String.class)
                    log.info("Command args must be String.");
        }
    }
    
    /* Used by the subscriber thread to invoke the discovered commands on the MDB */
    public Map<String,Method> getCommands() { return commands; }
    
    @Override
    public void validate() throws InvalidPropertyException { }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return ra;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        log.info("[TrafficActivationSpec] setResourceAdapter()");
        this.ra = ra;
    }
    
}
