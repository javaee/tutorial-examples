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

package javaeetutorial.vendor;

import java.util.Random;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * The Vendor class uses one transaction to receive the computer order from the
 * retailer and order the needed number of CPUs and disk drives from its
 * suppliers. At random intervals, it throws an exception to simulate a database
 * problem and cause a rollback.
 *
 * The class uses an asynchronous message listener to process replies from
 * suppliers. When all outstanding supplier inquiries complete, it sends a
 * message to the Retailer accepting or refusing the order.
 */
public class Vendor {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/AQueue")
    private static Queue vendorOrderQueue;
    @Resource(lookup = "jms/CQueue")
    private static Queue vendorConfirmQueue;
    @Resource(lookup = "jms/OTopic")
    private static Topic supplierOrderTopic;
    static Random rgen = new Random();
    static int throwException = 1;

    public static void main(String[] args) {
        JMSConsumer vendorOrderReceiver;
        MapMessage orderMessage;
        JMSConsumer vendorConfirmReceiver;
        VendorMessageListener listener;
        Message inMessage;
        MapMessage vendorOrderMessage;
        Message endOfMessageStream;
        Order order;
        int quantity;

        System.out.println("Starting vendor");

        try (JMSContext context =
                connectionFactory.createContext(JMSContext.SESSION_TRANSACTED);
                JMSContext asyncContext =
                context.createContext(JMSContext.SESSION_TRANSACTED);) {
            /*
             * Create receiver for vendor order queue, sender
             * for supplier order topic, and message to send
             * to suppliers.
             */
            vendorOrderReceiver = context.createConsumer(vendorOrderQueue);
            orderMessage = context.createMapMessage();

            /*
             * Configure an asynchronous message listener to
             * process supplier replies to inquiries for
             * parts to fill order.  Start delivery.
             */
            vendorConfirmReceiver = asyncContext.createConsumer(
                    vendorConfirmQueue);
            listener = new VendorMessageListener(asyncContext, 2);
            vendorConfirmReceiver.setMessageListener(listener);

            /*
             * Process orders in vendor order queue.
             * Use one transaction to receive order from
             * order queue and send message to suppliers'
             * order topic to order components to fulfill
             * the order placed with the vendor.
             */
            while (true) {
                try {
                    // Receive an order from a retailer.
                    inMessage = vendorOrderReceiver.receive();

                    if (inMessage instanceof MapMessage) {
                        vendorOrderMessage = (MapMessage) inMessage;
                    } else {
                        /*
                         * Message is an end-of-message-
                         * stream message from retailer.
                         * Send similar messages to
                         * suppliers, then break out of
                         * processing loop.
                         */
                        endOfMessageStream = context.createMessage();
                        endOfMessageStream.setJMSReplyTo(
                                vendorConfirmQueue);
                        context.createProducer().send(supplierOrderTopic,
                                endOfMessageStream);
                        context.commit();

                        break;
                    }

                    /*
                     * A real application would check an
                     * inventory database and order only the
                     * quantities needed.  Throw an exception
                     * every few times to simulate a database
                     * concurrent-access exception and cause
                     * a rollback.
                     */
                    if (rgen.nextInt(4) == throwException) {
                        throw new JMSException(
                                "Simulated database concurrent access "
                                + "exception");
                    }

                    /*
                     * Record retailer order as a pending
                     * order.
                     */
                    order = new Order(vendorOrderMessage);

                    /*
                     * Set order number and reply queue for
                     * outgoing message.
                     */
                    orderMessage.setInt(
                            "VendorOrderNumber",
                            order.orderNumber);
                    orderMessage.setJMSReplyTo(vendorConfirmQueue);
                    quantity = vendorOrderMessage.getInt("Quantity");
                    System.out.println(
                            "Vendor: Retailer ordered " + quantity
                            + " " + vendorOrderMessage.getString("Item"));

                    // Send message to supplier topic.
                    // Item is not used by supplier.
                    orderMessage.setString("Item", "");
                    orderMessage.setInt("Quantity", quantity);
                    context.createProducer().send(supplierOrderTopic,
                            orderMessage);
                    System.out.println(
                            "Vendor: Ordered " + quantity
                            + " CPU(s) and hard drive(s)");

                    // Commit session.
                    context.commit();
                    System.out.println(
                            "  Vendor: Committed transaction 1");
                } catch (JMSException e) {
                    System.err.println(
                            "Vendor: JMSException occurred: "
                            + e.toString());
                    context.rollback();
                    System.err.println(
                            "  Vendor: Rolled back transaction 1");
                }
            }

            // Wait till suppliers get back with answers.
            listener.monitor.waitTillDone();
        } catch (JMSRuntimeException e) {
            System.err.println(
                    "Vendor: Exception occurred: " + e.toString());
        }
    }
}
