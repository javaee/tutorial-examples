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

package javaeetutorial.traffic.rar;

import java.io.Serializable;
import java.util.logging.Logger;
import javaeetutorial.traffic.rar.inbound.ObtainEndpointWork;
import javaeetutorial.traffic.rar.inbound.TrafficActivationSpec;
import javaeetutorial.traffic.rar.inbound.TrafficServiceSubscriber;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

@Connector(
        displayName = "TrafficRA",
        vendorName = "Java EE Tutorial", 
        version = "7.0"
)
public class TrafficResourceAdapter implements ResourceAdapter, Serializable {
    
    private static final Logger log = Logger.getLogger("TrafficResourceAdapter");
    private static final long serialVersionUID = -2195736837440941558L;
    private TrafficActivationSpec tSpec;
    private WorkManager workManager;
    private Work tSubscriber;
    
    /* Make the activation configuration available elswhere */
    public TrafficActivationSpec getActivationSpec() {
        return tSpec;
    }
    
    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        log.info("[TrafficResourceAdapter] start()");
        /* Get the work manager from the container to submit tasks to
         * be executed in container-managed threads */
        workManager = ctx.getWorkManager();
    }

    @Override
    public void stop() {
        log.info("[TrafficResourceAdapter] stop()");
    }

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, 
                                   ActivationSpec spec) 
                                   throws ResourceException {
        
        log.info("[TrafficResourceAdapter] endpointActivation()"); 
        tSpec = (TrafficActivationSpec) spec;
        /* New in JCA 1.7 - Get the endpoint class implementation (i.e. the
         * MDB class). This allows looking at the MDB implementation for
         * annotations. */
        Class endpointClass = endpointFactory.getEndpointClass();
        tSpec.setBeanClass(endpointClass);
        tSpec.findCommandsInMDB();
        
        /* MessageEndpoint msgEndpoint = endpointFactory.createEndpoint(null);
         * but we need to do that in a different thread, otherwise the MDB
         * never deploys. */
        ObtainEndpointWork work = new ObtainEndpointWork(this, endpointFactory);
        workManager.scheduleWork(work);      
    }
    
    /* Called from ObtainEndpoint work after obtaining the endpoint */
    public void endpointAvailable(MessageEndpoint endpoint) {
        
        try {
            /* Start the traffic subscriber client in a new thread */
            tSubscriber = new TrafficServiceSubscriber(tSpec, endpoint);
            workManager.scheduleWork(tSubscriber);
        } catch (WorkException e) {
            log.info("[TrafficResourceAdapter] Can't start the subscriber");
            log.info(e.getMessage());
        }
    }
    
    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, 
                                     ActivationSpec spec) {
        log.info("[TrafficResourceAdapter] endpointDeactivation()");
        /* Stop listening */
        tSubscriber.release();
    }

    /* This connector does not use transactions */
    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) 
                                       throws ResourceException {
        return null;
    }
    
}
