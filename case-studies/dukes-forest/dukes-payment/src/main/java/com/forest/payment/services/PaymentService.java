/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package com.forest.payment.services;

import com.forest.events.OrderEvent;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/pay")
public class PaymentService {

    private static final Logger logger = Logger.getLogger("PaymentService");
    
    public PaymentService() { }
    
    @POST
    @Consumes("application/xml")
    public Response processPayment(OrderEvent order) {
        logger.info("Amount: "+order.getAmount());
        if (order.getAmount() < 1000) {
            return Response.ok().build();
        } else {
            return Response.status(401).build();
        }
    }
    
    @GET
    @Produces("text/html")
    public String getHtml() {
        return "PaymentService";
    }
}
