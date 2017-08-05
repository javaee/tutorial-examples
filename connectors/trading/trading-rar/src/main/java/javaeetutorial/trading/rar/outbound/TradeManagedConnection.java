/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
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
