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
