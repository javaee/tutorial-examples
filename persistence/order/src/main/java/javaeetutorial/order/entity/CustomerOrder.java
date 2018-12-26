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

package javaeetutorial.order.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.Transient;

@Entity
@Table(name="PERSISTENCE_ORDER_CUSTOMERORDER")
@NamedQuery(
    name="findAllOrders",
    query="SELECT co FROM CustomerOrder co " +
          "ORDER BY co.orderId"
)
public class CustomerOrder implements java.io.Serializable {
    private static final long serialVersionUID = 6582105865012174694L;
    private Integer orderId;
    private char status;
    private Date lastUpdate;
    private int discount;
    private String shipmentInfo;
    private Collection<LineItem> lineItems;
    
    public CustomerOrder() {
        this.lastUpdate = new Date();
        this.lineItems = new ArrayList<>();
    }
    
    public CustomerOrder(Integer orderId, char status, int discount, 
            String shipmentInfo) {
        this.orderId = orderId;
        this.status = status;
        this.discount = discount;
        this.shipmentInfo = shipmentInfo;
        this.lastUpdate = new Date();
        this.lineItems = new ArrayList<>();
    }
    
    @Id
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public char getStatus() {
        return status;
    }
    
    public void setStatus(char status) {
        this.status = status;
    }
    
    @Temporal(TIMESTAMP)
    public Date getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public int getDiscount() {
        return discount;
    }
    
    public void setDiscount(int discount) {
        this.discount = discount;
    }
    
    public String getShipmentInfo() {
        return shipmentInfo;
    }
    
    public void setShipmentInfo(String shipmentInfo) {
        this.shipmentInfo = shipmentInfo;
    }
    
    @OneToMany(cascade=ALL, mappedBy="customerOrder")
    public Collection<LineItem> getLineItems() {
        return lineItems;
    }
    
    public void setLineItems(Collection<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public double calculateAmmount() {
        double ammount = 0;
        Collection<LineItem> items = getLineItems();
        for (LineItem item : items) {
            VendorPart part = item.getVendorPart();
            ammount += part.getPrice() * item.getQuantity();
        }
        return (ammount * (100 - getDiscount()))/100;
    }

    public void addLineItem(LineItem lineItem) {
        this.getLineItems().add(lineItem);
    }
    
    @Transient
    public int getNextId() {
        return this.lineItems.size() + 1;
    }
}
