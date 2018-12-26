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
