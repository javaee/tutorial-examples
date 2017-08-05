/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.trading.rar.outbound;

import java.io.IOException;
import java.util.logging.Logger;
import javaeetutorial.trading.rar.api.TradeConnection;
import javaeetutorial.trading.rar.api.TradeOrder;
import javaeetutorial.trading.rar.api.TradeProcessingException;
import javaeetutorial.trading.rar.api.TradeResponse;
import javax.resource.ResourceException;

/* An application-level connection handle used by clients to access
 * the physical connection. The physical connection is represented by
 * a ManagedConnection instance */
public class TradeConnectionImpl implements TradeConnection {
    
    private static final Logger log = Logger.getLogger("TradeConnectionImpl");
    private TradeManagedConnection mconnection;
    private boolean valid;
    
    /* */
    TradeConnectionImpl(TradeManagedConnection mconnection) {
        this.mconnection = mconnection;
        valid = true;
    }
    
    /* Called by the managed connection to dis/associate this handle. */
    TradeManagedConnection getManagedConnection() {
        return mconnection;
    }
    void setManagedConnection(TradeManagedConnection mconnection) {
        this.mconnection = mconnection;
    }
    
    /* Called by the managed connection to invalidate this handle */
    void invalidate() {
        valid = false;
    }

    /* Submits a trade order to the EIS */
    @Override
    public TradeResponse submitOrder(TradeOrder order) 
                                     throws TradeProcessingException {
        log.info("[TradeConnectionImpl] submitOrder()");
        if (valid) {
            try {
                String resp = mconnection.sendCommandToEIS(order.toString());
                return new TradeResponse(resp);
            } catch (IOException e) {
                throw new TradeProcessingException(e.getMessage());
            }
        } else
            throw new TradeProcessingException("Connection handle is invalid");
    }
    
    /* Closes the connection handle */
    @Override
    public void close() throws ResourceException {
        log.info("[TradeConnectionImpl] close()");
        valid = false;
        mconnection.disassociateConnection();
    }
    
}
