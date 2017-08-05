/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.durableconsumer;

import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;
import javax.jms.Topic;

public class DurableConsumer {

    @Resource(lookup = "jms/DurableConnectionFactory")
    private static ConnectionFactory durableConnectionFactory;
    @Resource(lookup = "jms/MyTopic")
    private static Topic topic;

    public static void main(String[] args) {
        TextListener listener;
        JMSConsumer consumer;

        InputStreamReader inputStreamReader;
        char answer = '\0';

        /*
         * In a try-with-resources block, create context.
         * Create durable consumer, if it does not exist already.
         * Register message listener (TextListener).
         * Receive text messages from destination.
         * When all messages have been received, enter Q to quit.
         */
        try (JMSContext context = durableConnectionFactory.createContext();) {
            System.out.println("Creating consumer for topic");
            context.stop();
            consumer = context.createDurableConsumer(topic, "MakeItLast");
            listener = new TextListener();
            consumer.setMessageListener(listener);
            System.out.println("Starting consumer");
            context.start();

            System.out.println("To end program, enter Q or q, then <return>");
            inputStreamReader = new InputStreamReader(System.in);

            while (!((answer == 'q') || (answer == 'Q'))) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    System.err.println("I/O exception: " + e.toString());
                }
            }
        } catch (JMSRuntimeException e) {
            System.err.println("Exception occurred: " + e.toString());
            System.exit(1);
        }
        System.exit(0);
    }
}
