/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.simplemessage.appclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

public class SimpleMessageClient {

    static final Logger logger = Logger.getLogger("SimpleMessageClient");
    
    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/MyQueue")
    private static Queue queue;

    public static void main(String[] args) {
        String text;
        final int NUM_MSGS = 3;

        try (JMSContext context = connectionFactory.createContext();) {
            
            for (int i = 0; i < NUM_MSGS; i++) {
                text = "This is message " + (i + 1);
                System.out.println("Sending message: " + text);
                context.createProducer().send(queue, text);
            }

            System.out.println("To see if the bean received the messages,");
            System.out.println(
                    " check <install_dir>/domains/domain1/logs/server.log.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred: {0}", e.toString());
        } 
        System.exit(0);
    } // main
} // class

