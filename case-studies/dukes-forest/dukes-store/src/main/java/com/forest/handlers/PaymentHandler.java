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
