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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.security.auth.Subject;


/* The container's connection manager uses this class to create a pool
 * of managed connections, which are associated at times with physical ones */

/* Define classes an interfaces for the EIS physical connection */
@ConnectionDefinition(
    connectionFactory = ConnectionFactory.class,
    connectionFactoryImpl = TradeConnectionFactoryImpl.class,
    connection = Connection.class,
    connectionImpl = TradeConnectionImpl.class
)
public class TradeManagedConnectionFactory implements ManagedConnectionFactory,
                                                      ResourceAdapterAssociation,
                                                      Serializable,
                                                      Referenceable {
    
    private static final Logger log = Logger.getLogger("TradeManagedConnectionFactory");
    private static final long serialVersionUID = 7918855339952421358L;
    private ResourceAdapter ra;
    private Reference reference;
    private PrintWriter logWriter;
    private String host;
    private String port;
    
    public TradeManagedConnectionFactory() { }
    
    /* Configuration properties */
    @ConfigProperty(defaultValue = "localhost")
    public void setHost(String host) { this.host = host; }
    public String getHost() { return host; }
    @ConfigProperty(type = String.class, defaultValue = "4004")
    public void setPort(String port) { this.port = port; }
    public String getPort() { return port; }
    
    @Override
    public Object createConnectionFactory() throws ResourceException {
        log.info("[TradeManagedConnectionFactory] createConnectionFactory()-NM");
        /* Non-managed connections not supported */
        return null;
    }
    
    /* The application server provides a connection manager */
    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) 
                                          throws ResourceException {
        log.info("[TradeManagedConnectionFactory] createConnectionFactory()");
        return new TradeConnectionFactoryImpl(this, cxManager);
    }

    /* Called by the application server to create a managed connection */
    @Override
    public ManagedConnection createManagedConnection(Subject subject, 
                                                     ConnectionRequestInfo cxRequestInfo) 
                                                     throws ResourceException {
        log.info("[TradeManagedConnectionFactory] createManagedConnection()");
        try {
            return new TradeManagedConnection(getHost(), getPort());
        } catch (IOException e) {
            throw new ResourceException(e.getCause());
        }
    }

    /* When an application requests a connection, provide an existing one
     * from the pool if it matches the connection parameters */
    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, 
                                                     Subject subject, 
                                                     ConnectionRequestInfo cxRequestInfo) 
                                                     throws ResourceException {
        log.info("[TradeManagedConnectionFactory] matchManagedConnections()");
        /* This resource adapter does not use security (Subject) */
        TradeManagedConnection match = null;
        /* This resource adapter has no additional parameters for connections,
         * so any open connection can be used by an application */
        for (Object mco : connectionSet) {
            if (mco != null) { 
                match = (TradeManagedConnection) mco;
                log.info("Connection match!");
                break;
            }
        }
        return match;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }
    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    /* Called by the server to associate this object with the resource adapter */
    @Override
    public ResourceAdapter getResourceAdapter() {
        return ra;
    }
    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        this.ra = ra;
    }

    /* Called by the server to associate an RA with a JNDI resource name.
     * Applications access an RA via JNDI resource injection (@Resource) */
    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }
    @Override
    public Reference getReference() throws NamingException {
        return reference;
    }
    
}
