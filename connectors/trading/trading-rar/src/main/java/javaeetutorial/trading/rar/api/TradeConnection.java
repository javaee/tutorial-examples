/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.trading.rar.api;

import javax.resource.ResourceException;

public interface TradeConnection {

    /* Submits a trade order to the EIS */
    public TradeResponse submitOrder(TradeOrder order) 
                                     throws TradeProcessingException;
    /* Closes the connection handle */
    public void close() throws ResourceException;
    
}
