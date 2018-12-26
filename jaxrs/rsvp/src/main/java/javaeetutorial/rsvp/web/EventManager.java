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
