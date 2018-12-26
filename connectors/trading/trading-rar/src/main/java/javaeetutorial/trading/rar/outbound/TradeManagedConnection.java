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

package javaeetutorial.trading.rar.outbound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

/* Represents a physical connection to the EIS.
 * The container maintains a pool of instances of this class */
public class TradeManagedConnection implements ManagedConnection {

    private static final Logger log = Logger.getLogger("TradeManagedConnection");
       
    private TradeConnectionImpl connection;
    private List<TradeConnectionImpl> createdConnections;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private PrintWriter logwriter;
    
    /* Called by the container from 
     * TradeManagedConnectionFactory.createManagedConnection
     * Creates a physical connection to the EIS */
    TradeManagedConnection(String host, String port) throws IOException {
        
        log.info("[TradeManagedConnection] Constructor");
        createdConnections = new ArrayList<>();
        
        /* EIS-specific procedure to obtain a new connection */
        int portnum = Integer.parseInt(port);
        log.info(String.format("Connecting to %s on port %s...", host, port));
        socket = new Socket(host, portnum);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        /* Skip greeting */
        in.readLine(); in.readLine();
        log.info("Connected!");
    }
    
    String sendCommandToEIS(String command) throws IOException {
        out.println(command);
        return in.readLine();
    }
    
    /* Called by the container to return a new connection handle. */
    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cinfo) 
                                throws ResourceException {
        /* This example does not use security (Subject) */
        log.info("[TradeManagedConnection] getConnection()");
        connection = new TradeConnectionImpl(this);
        return connection;
    }

    /* Called by the container to destroy a physical connection. */
    @Override
    public void destroy() throws ResourceException {
        try {
            log.info("[TradeManagedConnection] destroy()");
            socket.close();
        } catch (IOException e) {}
        
    }

    /* Called by the container to clean up client-specific state. */
    @Override
    public void cleanup() throws ResourceException { 
        log.info("[TradeManagedConnection] cleanup()");
        for (TradeConnectionImpl con : createdConnections)
            if (con != null)
                con.invalidate();
    }

    /* Called by the container to associate a different connection handle */
    @Override
    public void associateConnection(Object connection) throws ResourceException {
        log.info("[TradeManagedConnection] associateConnection()");
        this.connection = (TradeConnectionImpl) connection;
        this.connection.setManagedConnection(this);
    }
    
    public void disassociateConnection() {
        this.connection = null;
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {}

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {}

    @Override
    public XAResource getXAResource() throws ResourceException {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new ManagedConnectionMetaData() {
            @Override
            public String getEISProductName() throws ResourceException {
                return "MegaTrade Execution Platform";
            }
            @Override
            public String getEISProductVersion() throws ResourceException {
                return "7.0";
            }
            @Override
            public int getMaxConnections() throws ResourceException {
                return 0;
            }
            @Override
            public String getUserName() throws ResourceException {
                return "defaultUser";
            }
        };
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logwriter = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logwriter;
    }   
}
