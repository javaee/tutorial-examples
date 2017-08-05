/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.concurrency.jobs.service;

import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author markito
 */
@Stateless
@Path("/JobService")
public class JobService {

    private final static Logger logger = Logger.getLogger(JobService.class.getCanonicalName());
    // http header to check for valid tokens
    private final static String API_TOKEN_HEADER = "X-REST-API-Key";
    @Resource(lookup = "MES_High")
    private ManagedExecutorService highPrioExecutor;
    @Resource(lookup = "MES_Low")
    private ManagedExecutorService lowPrioExecutor;

    @EJB
    private TokenStore tokenStore;

    @GET
    @Path("/token")
    public Response getToken() {
        // static token + dynamic token
        final String token = "123X5-" + UUID.randomUUID().toString();
        tokenStore.put(token);
        return Response.status(200).entity(token).build();
    }

    @POST
    @Path("/process")
    public Response process(final @HeaderParam(API_TOKEN_HEADER) String token,
            final @QueryParam("jobID") int jobID) {

        try {
            if (token != null && tokenStore.isValid(token)) {
                logger.info("Token accepted. Execution with high priority.");
                highPrioExecutor.submit(new JobTask("HIGH-" + jobID));
            } else {
                logger.log(Level.INFO, "Invalid or missing token! {0}", token);
                // requests without token, will be executed but without priority
                lowPrioExecutor.submit(new JobTask("LOW-" + jobID));
            }
        } catch (RejectedExecutionException ree) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Job " + jobID + " NOT submitted. " + ree.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity("Job " + jobID + " successfully submitted.").build();
    }

    static class JobTask implements Runnable {

        private final String jobID;
        private final int JOB_EXECUTION_TIME= 10000;

        public JobTask(String id) {
            this.jobID = id;
        }

        @Override
        public void run() {
            try {
                logger.log(Level.INFO, "Task started {0}", jobID);
                Thread.sleep(JOB_EXECUTION_TIME); // 5 seconds per job
                logger.log(Level.INFO, "Task finished {0}", jobID);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }
}
