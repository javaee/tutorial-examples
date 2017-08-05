/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.taskcreator;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/* Notify the clients so that they can refresh the log textarea */
@Dependent
@ServerEndpoint("/wsinfo")
public class InfoEndpoint {

    private static final Logger log = Logger.getLogger("InfoEndpoint");
    /* Keep a list of clients */
    private static final Queue<Session> sessions =
            new ConcurrentLinkedQueue<>();
    
    @OnOpen
    public void onOpen(Session session) {
        log.info("[InfoEndpoint] Connection opened");
        sessions.add(session);
    }
    
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }
    
    @OnError
    public void onError(Session session, Throwable t) { }
    
    @OnMessage
    public void onMessage(String msg) { }
    
    /* Observe the event fired from the EJB and notify clients.
     * The clients use JavaScript to make a JSF AJAX request to refresh
     * the log textarea. */
    public static void pushAlert(@Observes String event) {
        for (Session s : sessions) {
            if (s.isOpen())
                try {
                    s.getBasicRemote().sendText(event);
                    log.info("[InfoEndpoint] Event sent");
                } catch (IOException ex) { }
        }
    }
}
