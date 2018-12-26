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

package javaeetutorial.web.dukeetf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns={"/dukeetf"}, asyncSupported=true)
public class DukeETFServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger("DukeETFServlet");
    private static final long serialVersionUID = 2114153638027156979L;
    private Queue<AsyncContext> requestQueue;
    @EJB private PriceVolumeBean pvbean; 
    
    @Override
    public void init(ServletConfig config) {
        /* Queue for requests */
        requestQueue = new ConcurrentLinkedQueue<>();
        /* Register with the bean that provides price/volume updates */
        pvbean.registerServlet(this);
    }
    
    /* PriceVolumeBean calls this method every second to send updates */
    public void send(double price, int volume) {
        /* Send update to all connected clients */
        for (AsyncContext acontext : requestQueue) {
            try {
                String msg = String.format("%.2f / %d", price, volume);
                PrintWriter writer = acontext.getResponse().getWriter();
                writer.write(msg);
                logger.log(Level.INFO, "Sent: {0}", msg);
                /* Close the connection
                 * The client (JavaScript) makes a new one instantly */
                acontext.complete();
            } catch (IOException ex) {
                logger.log(Level.INFO, ex.toString());
            }
        }
    }
    
    /* Service method */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        /* Put request in async mode. */
        final AsyncContext acontext = request.startAsync();
        /* Remove from the queue when done */
        acontext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent ae) throws IOException {
                requestQueue.remove(acontext);
                logger.log(Level.INFO, "Connection closed.");
            }
            @Override
            public void onTimeout(AsyncEvent ae) throws IOException {
                requestQueue.remove(acontext);
                logger.log(Level.INFO, "Connection timeout.");
            }
            @Override
            public void onError(AsyncEvent ae) throws IOException {
                requestQueue.remove(acontext);
                logger.log(Level.INFO, "Connection error.");
            }
            @Override
            public void onStartAsync(AsyncEvent ae) throws IOException { }
        });
        /* Add to the queue */
        requestQueue.add(acontext);
        logger.log(Level.INFO, "Connection open.");
    }
}
