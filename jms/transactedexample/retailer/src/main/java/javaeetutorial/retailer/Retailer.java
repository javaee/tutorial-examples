/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.retailer;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;

/**
 * The Retailer class orders a number of computers by sending a message to a
 * vendor. It then waits for the order to be confirmed.
 *
 * In this example, the Retailer places two orders, one for the quantity
 * specified on the command line and one for twice that number.
 *
 * This class does not use transactions.
 */
public class Retailer {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/AQueue")
    private static Queue vendorOrderQueue;
    @Resource(lookup = "jms/BQueue")
    private static Queue retailerConfirmQueue;
    static int quantity = 0;

    public static void main(String[] args) {
        MapMessage outMessage;
        JMSConsumer orderConfirmReceiver;
        MapMessage inMessage;

        if (args.length != 1) {
            System.out.println("Error: Program takes numerical argument.");
            System.exit(1);
        }

        quantity = (new Integer(args[0])).intValue();
        System.out.println("Retailer: Quantity to be ordered is " + quantity);
        /*
         * Create non-transacted context and sender for
         * vendor order queue.
         * Create message to vendor, setting item and
         * quantity values.
         * Send message.
         * Create receiver for retailer confirmation queue.
         * Get message and report result.
         * Send an end-of-message-stream message so vendor
         * will stop processing orders.
         */
        try (JMSContext context = connectionFactory.createContext();) {
            outMessage = context.createMapMessage();
            outMessage.setString("Item", "Computer(s)");
            outMessage.setInt("Quantity", quantity);
            outMessage.setJMSReplyTo(retailerConfirmQueue);
            context.createProducer().send(vendorOrderQueue, outMessage);
            System.out.println(
                    "Retailer: Ordered " + quantity + " computer(s)");

            orderConfirmReceiver = context.createConsumer(
                    retailerConfirmQueue);
            inMessage = (MapMessage) orderConfirmReceiver.receive();

            if (inMessage.getBoolean("OrderAccepted") == true) {
                System.out.println("Retailer: Order filled");
            } else {
                System.out.println("Retailer: Order not filled");
            }

            System.out.println("Retailer: Placing another order");
            outMessage.setInt("Quantity", quantity * 2);
            context.createProducer().send(vendorOrderQueue, outMessage);
            System.out.println(
                    "Retailer: Ordered " + outMessage.getInt("Quantity")
                    + " computer(s)");
            inMessage = (MapMessage) orderConfirmReceiver.receive();

            if (inMessage.getBoolean("OrderAccepted") == true) {
                System.out.println("Retailer: Order filled");
            } else {
                System.out.println("Retailer: Order not filled");
            }

            /*
             * Send a non-text control message indicating end
             * of messages.
             */
            context.createProducer().send(vendorOrderQueue,
                    context.createMessage());
        } catch (JMSException e) {
            System.err.println(
                    "Retailer: Exception occurred: " + e.toString());
        }

    }
}
