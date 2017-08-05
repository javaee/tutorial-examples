/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.billpayment.listener;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.billpayment.event.PaymentEvent;
import javaeetutorial.billpayment.interceptor.Logged;
import javaeetutorial.billpayment.payment.Credit;
import javaeetutorial.billpayment.payment.Debit;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;

/**
 * Handler for the two kinds of PaymentEvent.
 */
@Logged
@SessionScoped
public class PaymentHandler implements Serializable {

    private static final Logger logger =
            Logger.getLogger(PaymentHandler.class.getCanonicalName());
    private static final long serialVersionUID = 2013564481486393525L;

    public PaymentHandler() {
        logger.log(Level.INFO, "PaymentHandler created.");
    }

    public void creditPayment(@Observes @Credit PaymentEvent event) {
        logger.log(Level.INFO, "PaymentHandler - Credit Handler: {0}",
                event.toString());

        // call a specific Credit handler class...
    }

    public void debitPayment(@Observes @Debit PaymentEvent event) {
        logger.log(Level.INFO, "PaymentHandler - Debit Handler: {0}",
                event.toString());
 
        // call a specific Debit handler class...
    }
}
