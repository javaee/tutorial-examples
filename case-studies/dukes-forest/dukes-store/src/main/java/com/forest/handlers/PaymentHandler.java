/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package com.forest.handlers;

import com.forest.ejb.OrderBean;
import com.forest.events.OrderEvent;
import com.forest.qualifiers.New;
import com.forest.qualifiers.Paid;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

/**
 * CDI event handler that calls Payment service for new orders. It will
 * intercept (observe) an
 * <code>OrderEvent</code> with
 * <code>@New</code> <b>qualifier</b>.
 *
 * @author markito
 * @see com.forest.events.OrderEvent
 */
@Stateless
public class PaymentHandler implements IOrderHandler, Serializable {

    private static final Logger logger = Logger.getLogger(PaymentHandler.class.getCanonicalName());
    private static final long serialVersionUID = 4979287107039479577L;
    private static final String ENDPOINT = "http://localhost:8080/dukes-payment/payment/pay";
    @Inject
    @Paid
    Event<OrderEvent> eventManager;
    /**
     * Payment service endpoint
     */
    @EJB
    OrderBean orderBean;

    @Override
    @Asynchronous
    public void onNewOrder(@Observes @New OrderEvent event) {

        logger.log(Level.FINEST, "{0} Event being processed by PaymentHandler",
                Thread.currentThread().getName());

        if (processPayment(event)) {
            orderBean.setOrderStatus(event.getOrderID(),
                    String.valueOf(OrderBean.Status.PENDING_PAYMENT.getStatus()));
            logger.info("Payment Approved");
            eventManager.fire(event);
        } else {
            orderBean.setOrderStatus(event.getOrderID(),
                    String.valueOf(OrderBean.Status.CANCELLED_PAYMENT.getStatus()));
            logger.info("Payment Denied");
        }
    }

    private boolean processPayment(OrderEvent order) {

        boolean success = false;
        Client client = ClientBuilder.newClient();
        client.register(new AuthClientRequestFilter("jack@example.com", "1234"));
        Response resp = client.target(ENDPOINT)
                .request(MediaType.APPLICATION_XML)
                .post(Entity.entity(order, MediaType.APPLICATION_XML), Response.class);
        int status = resp.getStatus();
        if (status == 200) {
            success = true;
        }
        logger.log(Level.INFO, "[PaymentHandler] Response status {0}", status);
        client.close();
        return success;
    }
    
    /* Client filter for basic HTTP auth */
    class AuthClientRequestFilter implements ClientRequestFilter {
        private final String user;
        private final String password;
        public AuthClientRequestFilter(String user, String password) {
            this.user = user;
            this.password = password;
        }
        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            try {
                requestContext.getHeaders().add(
                        "Authorization",
                        "BASIC " + DatatypeConverter.printBase64Binary(
                                   (user+":"+password).getBytes("UTF-8"))
                );
            } catch (UnsupportedEncodingException ex) { }
        }
    }
}
