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

package javaeetutorial.rsvp.web;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.rsvp.entity.Event;
import javaeetutorial.rsvp.entity.Person;
import javaeetutorial.rsvp.util.ResponseEnum;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ievans
 */
@Named
@SessionScoped
public class StatusManager implements Serializable {

    private static final long serialVersionUID = 1;
    private static final Logger logger = Logger.getLogger(StatusManager.class.getName());
    private Event event;
    private List<Event> events;
    private Client client;
    private final String baseUri = "http://localhost:8080/rsvp/webapi";
    private WebTarget target;

    /**
     * Default constructor creates the JAX-RS client
     */
    public StatusManager() {
        client = ClientBuilder.newClient();
    }

    @PreDestroy
    private void clean() {
        client.close();
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Sets the event
     *
     * @param event the current event
     * @return a JSF action string
     */
    public String getEventStatus(Event event) {
        this.setEvent(event);
        return "eventStatus";
    }

    /**
     * Get all the events
     *
     * @return all the events
     */
    public List<Event> getEvents() {
        List<Event> returnedEvents = null;
        try {
            returnedEvents = client.target(baseUri)
                    .path("/status/all")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<Event>>() {
                    });
            if (returnedEvents == null) {
                logger.log(Level.SEVERE, "Returned events null.");
            } else {
                logger.log(Level.INFO, "Events have been returned.");
            }
        } catch (WebApplicationException ex) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (ResponseProcessingException ex) {
            logger.log(Level.SEVERE, "ReponseProcessingException thrown.");
            logger.log(Level.SEVERE, "Error is {0}", ex.getMessage());
        } catch (ProcessingException ex) {
            logger.log(Level.SEVERE, "ProcessingException thrown.");
            logger.log(Level.SEVERE, "Error is {0}", ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error retrieving all events.");
            logger.log(Level.SEVERE, "base URI is {0}", baseUri);
            logger.log(Level.SEVERE, "path is {0}", "all");
            logger.log(Level.SEVERE, "Exception is {0}", ex.getMessage());
        }
        return returnedEvents;
    }

    /**
     * Setter for all the events
     *
     * @param events the events to set
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Retrieve the status values
     *
     * @return an array of response values
     */
    public ResponseEnum[] getStatusValues() {
        return ResponseEnum.values();
    }

    /**
     * Change the status of a user's response
     *
     * @param userResponse the new response
     * @param person the attendee
     * @param event the event
     * @return the navigation case
     */
    public String changeStatus(ResponseEnum userResponse, Person person, Event event) {
        String navigation;
        try {
            logger.log(Level.INFO,
                    "changing status to {0} for {1} {2} for event ID {3}.",
                    new Object[]{userResponse,
                        person.getFirstName(),
                        person.getLastName(),
                        event.getId().toString()});
            client.target(baseUri)
                    .path(event.getId().toString())
                    .path(person.getId().toString())
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.xml(userResponse.getLabel()));
            navigation = "changedStatus";
        } catch (ResponseProcessingException ex) {
            logger.log(Level.WARNING, "couldn''t change status for {0} {1}",
                    new Object[]{person.getFirstName(),
                        person.getLastName()});
            logger.log(Level.WARNING, ex.getMessage());
            navigation = "error";
        }
        return navigation;
    }
}
