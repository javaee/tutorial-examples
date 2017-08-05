/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.synchconsumer;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * The SynchConsumer class consists only of a main method, which fetches one or
 * more messages from a queue or topic using synchronous message delivery. Run
 * this program in conjunction with Producer. Specify "queue" or "topic" on the
 * command line when you run the program.
 */
public class SynchConsumer {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/MyQueue")
    private static Queue queue;
    @Resource(lookup = "jms/MyTopic")
    private static Topic topic;

    /**
     * Main method.
     *
     * @param args the destination name and type used by the example
     */
    public static void main(String[] args) {
        String destType;
        Destination dest = null;
        JMSConsumer consumer;

        if (args.length != 1) {
            System.err.println("Program takes one argument: <dest_type>");
            System.exit(1);
        }

        destType = args[0];
        System.out.println("Destination type is " + destType);

        if (!(destType.equals("queue") || destType.equals("topic"))) {
            System.err.println("Argument must be \"queue\" or \"topic\"");
            System.exit(1);
        }

        try {
            if (destType.equals("queue")) {
                dest = (Destination) queue;
            } else {
                dest = (Destination) topic;
            }
        } catch (JMSRuntimeException e) {
            System.err.println("Error setting destination: " + e.toString());
            System.exit(1);
        }

        /*
         * In a try-with-resources block, create context.
         * Create consumer.
         * Receive all text messages from destination until
         * a non-text message is received indicating end of
         * message stream.
         */
        try (JMSContext context = connectionFactory.createContext();) {
            consumer = context.createConsumer(dest);
            int count = 0;
            
            while (true) {
                Message m = consumer.receive(1000);

                if (m != null) {
                    if (m instanceof TextMessage) {
                        // Comment out the following two lines to receive
                        // a large volume of messages
                        System.out.println(
                                "Reading message: " + m.getBody(String.class));
                        count += 1;
                    } else {
                        break;
                    }
                }
            }
            System.out.println("Messages received: " + count);
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
            System.exit(1);
        }
        System.exit(0);
    }
}
