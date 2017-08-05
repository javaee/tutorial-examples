/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.genericsupplier;

import java.util.Random;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * The GenericSupplier class receives an item order from the vendor and sends a
 * message accepting or refusing it.
 */
public class GenericSupplier {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/OTopic")
    private static Topic supplierOrderTopic;
    static String PRODUCT_NAME;
    static boolean ready = false;
    static int quantity = 0;

    /**
     * Constructor. Instantiates the supplier as the supplier for the kind of
     * item to be ordered.
     *
     * @param itemName the name of the item being ordered
     */
    public GenericSupplier(String itemName) {
        PRODUCT_NAME = itemName;
    }

    /**
     * Timer method. Completes when ready is set to true, after context is
     * started. Sleep prevents supplier from getting ahead of itself on fast
     * machines.
     */
    void waitForTopicConsumer() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        while (!(ready)) {
        }
    }

    /**
     * Checks to see if there are enough items in inventory. Rather than go to a
     * database, it generates a random number related to the order quantity, so
     * that some of the time there won't be enough in stock.
     *
     * @return the number of items in inventory
     */
    public static int checkInventory() {
        Random rgen = new Random();

        return (rgen.nextInt(quantity * 5));
    }

    public static void main(String[] args) {
        {
            JMSConsumer receiver;
            Message inMessage;
            MapMessage orderMessage;
            MapMessage outMessage;

            if (args.length != 1) {
                System.out.println("Program takes string argument, either CPU or HD");
                System.exit(1);
            }
            PRODUCT_NAME = args[0];
            if ("HD".equals(PRODUCT_NAME)) {
                PRODUCT_NAME = "Hard Drive";
            }
            System.out.println("Starting " + PRODUCT_NAME + " supplier");

            /*
             * Create context, then create receiver for order topic, which 
             * starts message delivery.
             */
            try (JMSContext context = connectionFactory.createContext(
                    JMSContext.SESSION_TRANSACTED);) {
                receiver = context.createConsumer(supplierOrderTopic);

                // Context has started, set ready to true
                ready = true;

                /*
                 * Keep checking supplier order topic for order
                 * request until end-of-message-stream message is
                 * received. Receive order and send an order
                 * confirmation as one transaction.
                 */
                while (true) {
                    try {
                        inMessage = receiver.receive();

                        if (inMessage instanceof MapMessage) {
                            orderMessage = (MapMessage) inMessage;
                        } else {
                            /*
                             * Message is an end-of-message-stream
                             * message. Send a similar message to
                             * reply queue, commit transaction, then
                             * stop processing orders by breaking out
                             * of loop.
                             */
                            context.createProducer().send(inMessage.getJMSReplyTo(),
                                    context.createMessage());
                            context.commit();

                            break;
                        }

                        /*
                         * Extract quantity ordered from order
                         * message.
                         */
                        quantity = orderMessage.getInt("Quantity");
                        System.out.println(
                                PRODUCT_NAME + " Supplier: Vendor ordered "
                                + quantity + " " + PRODUCT_NAME + "(s)");

                        /*
                         * Create sender and message for reply queue.
                         * Set order number and item; check inventory
                         * and set quantity available.
                         * Send message to vendor and commit
                         * transaction.
                         */
                        outMessage = context.createMapMessage();
                        outMessage.setInt(
                                "VendorOrderNumber",
                                orderMessage.getInt("VendorOrderNumber"));
                        outMessage.setString("Item", PRODUCT_NAME);

                        int numAvailable = checkInventory();

                        if (numAvailable >= quantity) {
                            outMessage.setInt("Quantity", quantity);
                        } else {
                            outMessage.setInt("Quantity", numAvailable);
                        }

                        context.createProducer().send(
                                (Queue) orderMessage.getJMSReplyTo(),
                                outMessage);
                        System.out.println(
                                PRODUCT_NAME + " Supplier: Sent "
                                + outMessage.getInt("Quantity") + " "
                                + outMessage.getString("Item") + "(s)");
                        context.commit();
                        System.out.println(
                                "  " + PRODUCT_NAME
                                + " Supplier: Committed transaction");
                    } catch (Exception e) {
                        System.err.println(
                                PRODUCT_NAME + " Supplier: Exception occurred: "
                                + e.toString());
                    }
                }
            } catch (JMSRuntimeException ee) {
                System.err.println(
                        PRODUCT_NAME + " Supplier: Exception occurred: "
                        + ee.toString());
            }
        }
    }
}
