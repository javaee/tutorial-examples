/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.rsvp.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.rsvp.entity.Response;
import javaeetutorial.rsvp.util.ResponseEnum;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ievans
 */
@Stateless
@Path("/{eventId}/{inviteId}")
public class ResponseBean {

    @PersistenceContext
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(ResponseBean.class.getName());
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getResponse(@PathParam("eventId") Long eventId,
            @PathParam("inviteId") Long personId) {
        Response response = (Response)
                em.createNamedQuery("rsvp.entity.Response.findResponseByEventAndPerson")
                .setParameter("eventId", eventId)
                .setParameter("personId", personId)
                .getSingleResult();
        return response;
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void putResponse(String userResponse,
            @PathParam("eventId") Long eventId,
            @PathParam("inviteId") Long personId) {
        logger.log(Level.INFO, 
                "Updating status to {0} for person ID {1} for event ID {2}", 
                new Object[]{userResponse, 
                    eventId, 
                    personId});
         Response response = (Response)
                em.createNamedQuery("rsvp.entity.Response.findResponseByEventAndPerson")
                .setParameter("eventId", eventId)
                .setParameter("personId", personId)
                .getSingleResult();
        if (userResponse.equals(ResponseEnum.ATTENDING.getLabel()) && !response.getResponse().equals(ResponseEnum.ATTENDING)) {
            response.setResponse(ResponseEnum.ATTENDING);
            em.merge(response);
        } else if (userResponse.equals(ResponseEnum.NOT_ATTENDING.getLabel()) && !response.getResponse().equals(ResponseEnum.NOT_ATTENDING)) {
            response.setResponse(ResponseEnum.NOT_ATTENDING);
            em.merge(response);
        } else if (userResponse.equals(ResponseEnum.MAYBE_ATTENDING.getLabel()) && !response.getResponse().equals(ResponseEnum.MAYBE_ATTENDING)) {
            response.setResponse(ResponseEnum.MAYBE_ATTENDING);
            em.merge(response);
        }
    }
 
}
