/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.clientackconsumer;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

public class ClientAckConsumer {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/MyQueue")
    private static Queue queue;

    public static void main(String[] args) {
        JMSConsumer consumer;

        /*
         * Create client-acknowledge context for receiver.
         * Receive message and process it.
         * Acknowledge message.
         */
        try (JMSContext context =
                connectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE);) {
            System.out.println(
                    "Created client-acknowledge JMSContext");
            consumer = context.createConsumer(queue);

            while (true) {
                Message m = consumer.receive(1000);

                if (m != null) {
                    if (m instanceof TextMessage) {
                        // Comment out the following two lines to receive
                        // a large volume of messages
                        System.out.println(
                                "Reading message: " + m.getBody(String.class));
                        System.out.println(
                                "Acknowledging TextMessage");
                        context.acknowledge();
                    } else {
                        System.out.println(
                                "Acknowledging non-text control message");
                        context.acknowledge();
                        break;
                    }
                }
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        }
    }
}
