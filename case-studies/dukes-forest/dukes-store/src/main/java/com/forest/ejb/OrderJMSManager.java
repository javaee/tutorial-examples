/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package com.forest.ejb;

import com.forest.entity.CustomerOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

@JMSDestinationDefinition(
        name = "java:global/jms/OrderQueue",
        interfaceName = "javax.jms.Queue",
        destinationName = "PhysicalOrderQueue")
@Stateless
public class OrderJMSManager {

    private static final Logger logger = Logger.getLogger(OrderJMSManager.class.getCanonicalName());
    @Inject
    private JMSContext context;
    
    @Resource(mappedName = "java:global/jms/OrderQueue")
    private Queue queue;
    private QueueBrowser browser;

    public void sendMessage(CustomerOrder customerOrder) {
        ObjectMessage msgObj = context.createObjectMessage();

        try {
            msgObj.setObject(customerOrder);
            msgObj.setStringProperty("OrderID", String.valueOf(customerOrder.getId()));

            context.createProducer().send(queue, msgObj);
        } catch (JMSException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void deleteMessage(int orderID) throws Exception {
        
        JMSConsumer consumer = context.createConsumer(queue, "OrderID='" + orderID + "'") ;
        
        CustomerOrder order = consumer.receiveBody(CustomerOrder.class, 1);
        
        if (order != null)
            logger.log(Level.INFO, "Order {0} removed from queue.", order.getId());
        else {
            logger.log(Level.SEVERE, "Order {0} was not removed from queue!", orderID); 
            throw new Exception("Order not removed from queue");
        }
        
    }
}
