/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.shareddurableconsumer;

import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;
import javax.jms.Topic;

/**
 * The SharedDurableSubscriberExample class consists only of a main method, 
 * which uses a shared durable topic subscriber to receive messages using 
 * asynchronous message delivery. It uses the message listener TextListener. 
 * Run this program in conjunction with Producer.
 *
 * To end the program, enter Q or q on the command line.
 */
public class SharedDurableConsumer {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/MyTopic")
    private static Topic topic;

    /**
     * Main method.
     *
     * @param args the destination name and type used by the example
     */
    public static void main(String[] args) {
        JMSConsumer consumer;
        TextListener listener;
        InputStreamReader inputStreamReader;
        char answer = '\0';

        /*
         * In a try-with-resources block, create context.
         * Create consumer.
         * Register message listener (TextListener).
         * Receive text messages from destination.
         * When all messages have been received, enter Q to quit.
         */
        try (JMSContext context = connectionFactory.createContext();) {
            consumer = context.createSharedDurableConsumer(topic, "MakeItLast");

            System.out.println("Waiting for messages on topic");

            listener = new TextListener();
            consumer.setMessageListener(listener);
            System.out.println("To end program, enter Q or q, then <return>");
            inputStreamReader = new InputStreamReader(System.in);

            while (!((answer == 'q') || (answer == 'Q'))) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    System.err.println("I/O exception: " + e.toString());
                }
            }
            System.out.println("Messages received: " + listener.getCount());
        } catch (JMSRuntimeException e) {
            System.err.println("Exception occurred: " + e.toString());
            System.exit(1);
        }
        System.exit(0);
    }
}
