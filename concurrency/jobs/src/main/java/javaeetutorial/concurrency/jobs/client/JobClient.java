/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.concurrency.jobs.client;


import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * Client to JAXRS service
 *
 * @author markito
 */
@Named
@RequestScoped
public class JobClient implements Serializable {
    private final static Logger logger = Logger.getLogger(JobClient.class.getCanonicalName());
    private static final long serialVersionUID = 16472027766900196L;

    private String token;
    private int jobID;

    private final String serviceEndpoint = "http://localhost:8080/jobs/webapi/JobService/process";

    public String submit() {
        final Client client = ClientBuilder.newClient();

        final Response response = client.target(serviceEndpoint)
                .queryParam("jobID", getJobID())
                .request()
                .header("X-REST-API-Key", token)
                .post(null);

        FacesMessage message;
        message = (response.getStatus() == 200)
                ? new FacesMessage(FacesMessage.SEVERITY_INFO, String.format("Job %d successfully submitted",getJobID()), null)
                : new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format("Job %d was NOT submitted",getJobID()), null);

        FacesContext.getCurrentInstance().addMessage(null, message);
        logger.info(message.getSummary());
        clear();
        return "";
    }
    private void clear() {
        this.token = "";
    }
    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the jobID
     */
    public int getJobID() {
        return jobID;
    }

    /**
     * @param jobID the jobID to set
     */
    public void setJobID(int jobID) {
        this.jobID = jobID;
    }
}
