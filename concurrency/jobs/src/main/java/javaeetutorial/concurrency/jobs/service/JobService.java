/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
