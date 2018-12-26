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

package javaeetutorial.taskcreator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* The tasks just notify the JAX-RS web service in the EJB */
public class Task implements Runnable {

    private static final Logger log = Logger.getLogger("Task");
    private static final String WS_URL = 
            "http://localhost:8080/taskcreator/jaxrs/taskinfo";
    
    private final String name;
    private final String type;
    private final DateFormat dateFormat;
    private final Client client;
    private int counter;
    
    public Task(String n, String t) {
        name = n;
        type = t;
        counter = 1;
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        client = ClientBuilder.newClient();
        /* For delayed tasks, send
        /* Send: 14:15:45 - DELAYED Task ABCDE submitted */
        if (type.compareTo("DELAYED") == 0)
            sendToWebService("submitted");
    }
    
    @Override
    public void run() {
        /* Send: 14:15:45 - TASKTYPE Task ABCDE started */
        if (type.compareTo("PERIODIC") == 0)
            sendToWebService("started run #" + counter);
        else
            sendToWebService("started");
        
        try {
            Thread.sleep(1500);
        } catch (Exception e) { }
        
        /* Send: 14:15:47 - TASKTYPE Task ABCDE finished */
        if (type.compareTo("PERIODIC") == 0)
            sendToWebService("finished run #" + (counter++));
        else
            sendToWebService("finished");
    }
    
    /* Send: 14:15:47 - TASKTYPE Task ABCDE [details] */
    private void sendToWebService(String details) {
        String time = dateFormat.format(Calendar.getInstance().getTime());
        String msg = time + " - "  + type + " Task " + name + " " + details;
        Response resp = client.target(WS_URL)
                              .request(MediaType.TEXT_PLAIN)
                              .post(Entity.html(msg));
    }
    
    public String getName() {
        return name;
    }

}
