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

package com.forest.shipment.web;

import com.forest.entity.CustomerOrder;
import com.forest.shipment.ejb.OrderBrowser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Named
@RequestScoped
public class ShippingBean implements Serializable {

    private static final Logger logger =
            Logger.getLogger(ShippingBean.class.getCanonicalName());
    private static final String SERVICE_ENDPOINT =
            "http://localhost:8080/dukes-store/services/orders";
    private static final String MEDIA_TYPE = MediaType.APPLICATION_JSON;
    private static final long serialVersionUID = -2526289536313985021L;
    protected Client client;
    @EJB
    OrderBrowser orderBrowser;

    @PostConstruct
    private void init() {
        client = ClientBuilder.newClient();
    }

    @PreDestroy
    private void clean() {
        client.close();
    }
    private Map<String, CustomerOrder> orders;

    /**
     * @return the orders
     */
    public Map<String, CustomerOrder> getOrders() {
        return orders;
    }

    /**
     * @param orders the orders to set
     */
    public void setOrders(Map<String, CustomerOrder> orders) {
        this.orders = orders;
    }

    public enum Status {

        PENDING_PAYMENT(2),
        READY_TO_SHIP(3),
        SHIPPED(4),
        CANCELLED_PAYMENT(5),
        CANCELLED_MANUAL(6);
        private int status;

        private Status(final int pStatus) {
            status = pStatus;
        }

        public int getStatus() {
            return status;
        }
    }

    public String getEndpoint() {
        return SERVICE_ENDPOINT;
    }

    public List<CustomerOrder> listByStatus(final Status status) {
        List<CustomerOrder> entity = (List<CustomerOrder>) client.target(SERVICE_ENDPOINT)
                .queryParam("status", String.valueOf(status.getStatus()))
                .request(MEDIA_TYPE)
                .get(new GenericType<List<CustomerOrder>>() {
        });

        return entity;
    }

    public void updateOrderStatus(final String messageID, final Status status) {
        // consume message
        CustomerOrder order = orderBrowser.processOrder(messageID);

        // call order service to update db in Store
        Response response = client.target(SERVICE_ENDPOINT)
                .path("/" + order.getId())
                .request(MEDIA_TYPE)
                .put(Entity.text(String.valueOf(status.getStatus())));

        logger.log(Level.FINEST, "PUT Status response: {0}", response.getStatus());
    }

    /**
     * @return the orders
     */
    public List<String> getPendingOrders() {
        Map<String, CustomerOrder> pendingOrders = orderBrowser.getOrders();

        if (pendingOrders == null) {
            return null;
        } else {
            // update current pending orders map
            setOrders(pendingOrders);
            return new ArrayList<>(getOrders().keySet());
        }
    }

    public List<CustomerOrder> getCompletedOrders() {
        return listByStatus(Status.SHIPPED);
    }
}
