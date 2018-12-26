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

package com.forest.handlers;

import com.forest.ejb.OrderBean;
import com.forest.ejb.OrderJMSManager;
import com.forest.entity.CustomerOrder;
import com.forest.events.OrderEvent;
import com.forest.qualifiers.Paid;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

/**
 *
 * @author markito
 */
@Stateless
public class DeliveryHandler implements IOrderHandler, Serializable {

    private static final Logger logger = Logger.getLogger(DeliveryHandler.class.getCanonicalName());
    private static final long serialVersionUID = 4346750267714932851L;
    
    @EJB 
    OrderBean orderBean;
    @EJB
    OrderJMSManager orderPublisher;
    
    @Override
    @Asynchronous
    public void onNewOrder(@Observes @Paid OrderEvent event) {
        
        logger.log(Level.FINEST, "{0} Event being processed by DeliveryHandler", Thread.currentThread().getName());
       
        try {           
            logger.log(Level.INFO, "Order #{0} has been paid in the amount of {1}. Order is now ready for delivery!", new Object[]{event.getOrderID(), event.getAmount()});
                                    
            orderBean.setOrderStatus(event.getOrderID(), String.valueOf(OrderBean.Status.READY_TO_SHIP.getStatus()));
            CustomerOrder order = orderBean.getOrderById(event.getOrderID());
            if (order != null) {
                orderPublisher.sendMessage(order);
               
            } else {
                throw new Exception("The order does not exist");
            }
        } catch (Exception jex) {
            logger.log(Level.SEVERE, null, jex);
        }
    }
}
