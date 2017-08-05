/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.mood;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 */
@WebListener()
public class SimpleServletListener implements ServletContextListener,
        ServletContextAttributeListener {

    static final Logger log =
            Logger.getLogger("mood.web.SimpleServletListener");

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Context initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Context destroyed");
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        log.log(Level.INFO, "Attribute {0} has been added, with value: {1}", 
                new Object[]{event.getName(), event.getValue()});
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        log.log(Level.INFO, "Attribute {0} has been removed", 
                event.getName());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        log.log(Level.INFO, "Attribute {0} has been replaced, with value: {1}", 
                new Object[]{event.getName(), event.getValue()});
    }
}
