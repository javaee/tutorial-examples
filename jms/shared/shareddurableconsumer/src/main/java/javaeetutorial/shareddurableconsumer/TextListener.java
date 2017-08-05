/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.shareddurableconsumer;

import java.util.concurrent.atomic.AtomicLong;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * The TextListener class implements the MessageListener interface by defining
 * an onMessage method that displays the contents of a TextMessage.
 *
 * This class acts as the listener for the AsynchConsumer class.
 */
public class TextListener implements MessageListener {

    AtomicLong count = new AtomicLong(0);

    /**
     * Displays the message text.
     *
     * @param message the incoming message
     */
    @Override
    public void onMessage(Message m) {
        long i;

        try {
            if (m instanceof TextMessage) {
                i = count.incrementAndGet();
                // Comment out the following line to receive many messages 
                System.out.println("Reading message: " + m.getBody(String.class));
            } else {
                System.out.println("Message is not a TextMessage");
            }
        } catch (JMSException | JMSRuntimeException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } 
    }

    /* 
     * Retrieve the count value.
     */
    public long getCount() {
        return count.get();
    }
}
