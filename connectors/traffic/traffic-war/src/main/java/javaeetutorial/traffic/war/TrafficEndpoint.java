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

package javaeetutorial.traffic.war;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

/* This endpoint forwards to web clients the JSON messages 
 * received by the WebMDB bean from the JMS topic */
@ServerEndpoint("/wstraffic")
public class TrafficEndpoint {
    
    /* Queue for all open WebSocket sessions */
    static Queue<Session> queue = new ConcurrentLinkedQueue<>();
    
    private static final Logger log = Logger.getLogger("TrafficEndpoint");
    
    /* Called by WebMDB when it receives messages from the JMS topic */
    public static synchronized void sendAll(String msg) {
        log.info("[TrafficEndpoint] sendAll()");
        try {
            /* Send messages from the JMS queue to all sessions */
            for (Session session : queue) {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(msg);
                    log.log(Level.INFO, "[TrafficEndpoint] Sent: {0}", msg);
                }
            }
        } catch (IOException e) {
            log.log(Level.INFO, "[TrafficEndpoint] Exception: {0}", e.getMessage());
        }
    }
    
    /* Add and remove sesions from the queue */
    @OnOpen
    public void openConnection(Session session) {
        log.info("[TrafficEndpoint] openConnection()");
        queue.add(session);
    }
    
    @OnClose
    public void closedConnection(Session session) {
        log.info("[TrafficEndpoint] closedConnection()");
        queue.remove(session);
    }
    
    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        log.info("[TrafficEndpoint] error()");
    }
}
