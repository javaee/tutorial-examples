/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.taskcreator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Startup
@Singleton
@LocalBean
@Path("/taskinfo")
public class TaskEJB {
    
    private static final Logger log = Logger.getLogger("TaskEJB");
    
    /* Inject the default managed executor from the app server */
    @Resource(name="java:comp/DefaultManagedExecutorService")
    ManagedExecutorService mExecService;
    /* Inject the default managed scheduled executor from the app server */
    @Resource(name="java:comp/DefaultManagedScheduledExecutorService")
    ManagedScheduledExecutorService sExecService;
    
    /* Keep track of periodic tasks so we can kill them later */
    private Map<String, ScheduledFuture<?>> periodicTasks;
    /* Keep the log (textarea content) for all clients in this EJB */
    private String infoField;
    /* Fire CDI events for the WebSocket endpoint */
    @Inject
    private Event<String> events;

    @PostConstruct
    public void init() {
        periodicTasks = new HashMap<>();
        infoField = "";
    }
    
    @PreDestroy
    public void destroy() {
        /* Cancel periodic tasks */
        log.info("[TaskEJB] Cancelling periodic tasks");
        for (ScheduledFuture<?> fut : periodicTasks.values())
            fut.cancel(true);
        mExecService.shutdownNow();
        sExecService.shutdownNow();
    }
    
    public void submitTask(Task task, String type) {
        /* Use the managed executor objects from the app server
         * to schedule the tasks */
        switch (type) {
            case "IMMEDIATE":
                mExecService.submit(task);
                break;
            case "DELAYED":
                sExecService.schedule(task, 3, TimeUnit.SECONDS);
                break;
            case "PERIODIC":
                ScheduledFuture<?> fut;
                fut = sExecService.scheduleAtFixedRate(task, 0, 8, 
                        TimeUnit.SECONDS);
                periodicTasks.put(task.getName(), fut);
                break;
        }
    }
    
    public void cancelPeriodicTask(String name) {
        /* Cancel a periodic task */
        if (periodicTasks.containsKey(name)) {
            log.log(Level.INFO, "[TaskEJB] Cancelling task {0}", name);
            periodicTasks.get(name).cancel(true);
            periodicTasks.remove(name);
            /* Notify the WebSocket endpoint to update the client's task list */
            events.fire("tasklist");
        }
    }
    
    @POST
    @Consumes("text/html")
    /* The tasks post updates to this JAX-RS endpoint */
    public void addToInfoField(String msg) {
        /* Update the log */
        infoField = msg + "\n" + infoField;
        log.log(Level.INFO, "[TaskEJB] Added message {0}", msg);
        /* Notify the WebSocket endpoint to update the client's task log */
        events.fire("infobox");
    }
    
    /* Provide the execution log for the client's pages */
    public String getInfoField() {
        return infoField;
    }
    
    public void clearInfoField() {
        infoField = "";
    }
    
    /* Provide the list of running tasks */
    public Set<String> getPeriodicTasks() {
        return periodicTasks.keySet();
    }
}
