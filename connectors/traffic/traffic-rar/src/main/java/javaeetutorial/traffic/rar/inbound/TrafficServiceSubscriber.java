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

package javaeetutorial.traffic.rar.inbound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.work.Work;

/* The RA runs this class to connect to the traffic information system
 * EIS and invoke methods on TrafficMdb */
public class TrafficServiceSubscriber implements Work {

    private static final Logger log = Logger.getLogger("TrafficServiceSocket");
    private MessageEndpoint mdb;
    private TrafficActivationSpec spec;
    private Socket socket;
    private volatile boolean listen;

    public TrafficServiceSubscriber(TrafficActivationSpec spec,
                                    MessageEndpoint mdb) {
        this.mdb = mdb;
        this.spec = spec;
        listen = true;
    }

    @Override
    public void run() {
        
        BufferedReader in;
        String jsonLine;
        String key;
        JsonParser parser;
        
        try {
            /* Connect to the traffic EIS */
            int port = Integer.parseInt(spec.getPort());
            log.info("[TrafficServiceSubscriber] Connecting...");
            socket = new Socket("localhost", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            log.info("[TrafficServiceSubscriber] Connected");

            while (listen) {
                jsonLine = in.readLine();
                parser = Json.createParser(new StringReader(jsonLine));
                if (parser.hasNext() && parser.next() == Event.START_OBJECT &&
                    parser.hasNext() && parser.next() == Event.KEY_NAME) {
                    
                    key = parser.getString();
                    /* Does the MDB support this message? */
                    if (spec.getCommands().containsKey(key)) {
                        Method mdbMethod = spec.getCommands().get(key);
                        /* Invoke the method of the MDB */
                        callMdb(mdb, mdbMethod, jsonLine);
                    } else
                        log.info("[TrafficServerSubscriber] Unknown message");
                } else
                    log.info("[TrafficServiceSubscriber] Wrong message format");
                
            }
        } catch (IOException | ResourceException ex) {
            log.log(Level.INFO, "[TrafficServiceSubscriber] Error - {0}", ex.getMessage());
        }
    }
    
    /* Invoke a method from the MDB */
    private String callMdb(MessageEndpoint mdb, Method command, String... params) 
                           throws ResourceException {
        String resp;
        try {
            log.info("[TrafficServiceSubscriber] callMdb()");
            mdb.beforeDelivery(command);
            Object ret = command.invoke(mdb, (Object[]) params);
            resp = (String) ret;
        } catch (NoSuchMethodException | ResourceException | 
                 IllegalAccessException | IllegalArgumentException | 
                 InvocationTargetException ex) {
            log.info(String.format("Invocation error %s", ex.getMessage()));
            resp = "ERROR Invocation error - " + ex.getMessage();
        }
        mdb.afterDelivery();
        return resp;
    }

    @Override
    public void release() {
        log.info("[TrafficServiceSubscriber] release()");
        try {
            listen = false;
            socket.close();
        } catch (IOException ex) { }
    }
}
