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

package com.forest.shipment.ejb;

import com.forest.entity.CustomerOrder;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

@Stateless
public class OrderBrowser {

    private static final Logger logger = Logger.getLogger(OrderBrowser.class.getCanonicalName());
    @Inject
    private JMSContext context;
    @Resource(mappedName = "java:global/jms/OrderQueue")
    private Queue queue;
    private QueueBrowser browser;

    public Map<String, CustomerOrder> getOrders() {
        browser = context.createBrowser(queue);
        Enumeration msgs;
        try {
            msgs = browser.getEnumeration();

            if (!msgs.hasMoreElements()) {
                logger.log(Level.INFO, "No messages on the queue!");
            } else {

                Map<String, CustomerOrder> result = new LinkedHashMap<>();
                while (msgs.hasMoreElements()) {
                    Message msg = (Message) msgs.nextElement();

                    logger.log(Level.INFO, "Message ID: {0}", msg.getJMSMessageID());
                    CustomerOrder order = msg.getBody(CustomerOrder.class);
                    result.put(msg.getJMSMessageID(), order);
                }
                return result;
            }
        } catch (JMSException ex) {
            Logger.getLogger(OrderBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public CustomerOrder processOrder(String OrderMessageID) {

        logger.log(Level.INFO, "Processing Order {0}", OrderMessageID);
        JMSConsumer consumer = context.createConsumer(queue, "JMSMessageID='" + OrderMessageID + "'");

        CustomerOrder order = consumer.receiveBody(CustomerOrder.class, 1);
        return order;
    }
}
