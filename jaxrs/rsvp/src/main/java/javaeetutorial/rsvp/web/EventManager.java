/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.rsvp.web;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.rsvp.entity.Event;
import javaeetutorial.rsvp.entity.Response;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ievans
 */
@Named
@SessionScoped
public class EventManager implements Serializable {

    private static final long serialVersionUID = -3240069895629955984L;
    private static final Logger logger = Logger.getLogger(EventManager.class.getName());
    protected Event currentEvent;
    private Response currentResponse;
    private Client client;
    private final String baseUri = "http://localhost:8080/rsvp/webapi/status/";
    private WebTarget target;

    /**
     * Default constructor that creates the JAX-RS client
     */
    public EventManager() {
        
    }

    @PostConstruct
    private void init() {
        this.client = ClientBuilder.newClient();
    }
    
    @PreDestroy
    private void clean() {
        client.close();
    }

    /**
     * Get the value of currentEvent
     *
     * @return the value of currentEvent
     */
    public Event getCurrentEvent() {

        return currentEvent;
    }

    /**
     * Set the value of currentEvent
     *
     * @param currentEvent new value of currentEvent
     */
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    /**
     * @return the currentResponse
     */
    public Response getCurrentResponse() {
        return currentResponse;
    }

    /**
     * @param currentResponse the currentResponse to set
     */
    public void setCurrentResponse(Response currentResponse) {
        this.currentResponse = currentResponse;
    }
    
    /**
     * Gets a collection of responses for the current event
     *
     * @return a List of responses
     */
    public List<Response> retrieveEventResponses() {
        if (this.currentEvent == null) {
            logger.log(Level.WARNING, "current event is null");
        }
        logger.log(Level.INFO, "getting responses for {0}", this.currentEvent.getName());
        try {
            Event event = client.target(baseUri)
                    .path(this.currentEvent.getId().toString())
                    .request(MediaType.APPLICATION_XML)
                    .get(Event.class);
            if (event == null) {
                logger.log(Level.WARNING, "returned event is null");
                return null;
            } else {
                return event.getResponses();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "an error occurred when getting event responses.");
            return null;
        }
    }

    /**
     * Sets the current event
     *
     * @param event the current event
     * @return a JSF action string
     */
    public String retrieveEventStatus(Event event) {
        this.setCurrentEvent(event);
        return "eventStatus";
    }
    
    /**
     * Sets the current response and sends the navigation case
     * 
     * @param response the response that will be viewed
     * @return the navigation case
     */
    public String viewResponse(Response response) {
        this.currentResponse = response;
        return "viewResponse";
    }

}
