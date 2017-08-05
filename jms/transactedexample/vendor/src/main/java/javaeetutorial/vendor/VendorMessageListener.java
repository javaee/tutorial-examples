/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.vendor;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;

/**
 * The VendorMessageListener class processes an order confirmation message from
 * a supplier to the vendor.
 *
 * It demonstrates the use of transactions within message listeners.
 */
public class VendorMessageListener implements MessageListener {

    final SampleUtilities.DoneLatch monitor =
            new SampleUtilities.DoneLatch();
    int numSuppliers;
    private final JMSContext context;

    /**
     * Constructor. Instantiates the message listener with the session of the
     * consuming class (the vendor).
     *
     * @param c the context of the consumer
     * @param numSuppliers the number of suppliers
     */
    public VendorMessageListener(
            JMSContext c,
            int numSuppliers) {
        this.context = c;
        this.numSuppliers = numSuppliers;
    }

    /**
     * Casts the message to a MapMessage and processes the order. A message that
     * is not a MapMessage is interpreted as the end of the message stream, and
     * the message listener sets its monitor state to all done processing
     * messages.
     *
     * Each message received represents a fulfillment message from a supplier.
     *
     * @param message the incoming message
     */
    @Override
    public void onMessage(Message message) {
        /*
         * If message is an end-of-message-stream message and
         * this is the last such message, set monitor status
         * to all done processing messages and commit
         * transaction.
         */
        if (!(message instanceof MapMessage)) {
            if (Order.outstandingOrders() == 0) {
                numSuppliers--;

                if (numSuppliers == 0) {
                    monitor.allDone();
                }
            }

            try {
                context.commit();
            } catch (JMSRuntimeException je) {
            }
            return;
        }

        /*
         * Message is an order confirmation message from a
         * supplier.
         */
        try {
            MapMessage component = (MapMessage) message;

            /*
             * Process the order confirmation message and
             * commit the transaction.
             */
            int orderNumber = component.getInt("VendorOrderNumber");

            Order order = Order.getOrder(orderNumber)
                    .processSubOrder(component);
            context.commit();

            /*
             * If this message is the last supplier message,
             * send message to Retailer and commit
             * transaction.
             */
            if (!order.isPending()) {
                System.out.println(
                        "Vendor: Completed processing for order "
                        + order.orderNumber);

                Queue replyQueue = (Queue) order.order.getJMSReplyTo();
                MapMessage retailerConfirmMessage = context.createMapMessage();

                if (order.isFulfilled()) {
                    retailerConfirmMessage.setBoolean(
                            "OrderAccepted",
                            true);
                    System.out.println(
                            "Vendor: Sent " + order.quantity
                            + " computer(s)");
                } else if (order.isCancelled()) {
                    retailerConfirmMessage.setBoolean(
                            "OrderAccepted",
                            false);
                    System.out.println(
                            "Vendor: Unable to send " + order.quantity
                            + " computer(s)");
                }

                context.createProducer()
                        .send(replyQueue, retailerConfirmMessage);
                context.commit();
                System.out.println(
                        "  Vendor: Committed transaction 2");
            }
        } catch (JMSException je) {
            System.out.println("JMSException: " + je.toString());

            try {
                context.rollback();
            } catch (JMSRuntimeException je2) {
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());

            try {
                context.rollback();
            } catch (Exception je2) {
            }
        }
    }
}
