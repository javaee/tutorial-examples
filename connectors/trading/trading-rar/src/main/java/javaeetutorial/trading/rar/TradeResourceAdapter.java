/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.trading.rar;

import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

@Connector(
        displayName = "TradeResourceAdapter",
        vendorName = "Java EE Tutorial", 
        version = "7.0"
)
public class TradeResourceAdapter implements ResourceAdapter {
   
    private static final Logger log = Logger.getLogger("TradeResourceAdapter");

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        log.info("[TradeResourceAdapter] start()");
    }

    @Override
    public void stop() {
        log.info("[TradeResourceAdapter] stop()");
    }

    /* These are called for inbound connectors */
    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) throws ResourceException {
        log.info("[TradeResourceAdapter] endpointActivation()");       
    }
    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        log.info("[TradeResourceAdapter] endpointDeactivation()");
    }

    /* This connector does not use transactions */
    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return null;
    }
    
}
