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

package javaeetutorial.trading.war;

import java.io.Serializable;
import java.util.logging.Logger;
import javaeetutorial.trading.rar.api.TradeConnection;
import javaeetutorial.trading.rar.api.TradeConnectionFactory;
import javaeetutorial.trading.rar.api.TradeOrder;
import javaeetutorial.trading.rar.api.TradeProcessingException;
import javaeetutorial.trading.rar.api.TradeResponse;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.resource.ConnectionFactoryDefinition;
import javax.resource.ResourceException;
import javax.resource.spi.TransactionSupport;

/* Managed bean for JSF pages that uses the RA Common Client Interface (CCI)
 * to submit trades to the EIS. */
@Named
@SessionScoped
@ConnectionFactoryDefinition(
    name = "java:comp/env/eis/TradeConnectionFactory",
    interfaceName = "javaeetutorial.trading.rar.api.TradeConnectionFactory",
    resourceAdapter = "#trading-rar",
    minPoolSize = 5,
    transactionSupport = 
            TransactionSupport.TransactionSupportLevel.NoTransaction
)
public class ResourceAccessBean implements Serializable {

    private static final long serialVersionUID = -3002431810375279862L;
    private static final Logger log = Logger.getLogger("TradeBean");
    
    @Resource(lookup = "java:comp/env/eis/TradeConnectionFactory")
    private TradeConnectionFactory connectionFactory;
    
    private TradeConnection connection = null;
    private final TradeOrder order;
    private TradeResponse response;
    private String infoBox = "";
    
    public ResourceAccessBean() {
        order = new TradeOrder();
        order.setNShares(1000);
        order.setTicker(TradeOrder.Ticker.YYYY);
        order.setOrderType(TradeOrder.OrderType.BUY);
        order.setOrderClass(TradeOrder.OrderClass.MARKET);
    }
    
    /* JSF navigation method (from index.xhtml) */
    public String connect() {
        String page = "index";
        if (connection == null) {
            try {
                log.info("[ResourceAccessBean] Getting connection from the RA");
                connection = connectionFactory.getConnection();
                page = "trade";
            } catch (ResourceException e) {
                log.info(e.getMessage());
            }
        }
        return page;
        
    }
    
    /* JSF navigation method (from trade.xhtml) */
    public String disconnect() {
        infoBox = "";
        try {
            connection.close();
            connection = null;
        } catch (ResourceException e) {
            log.info(e.getMessage());
        }
        return "index";
    }
    
    /* JSF interface method to submit a trade to the RA/EIS (in trade.xhtml) */
    public void submitTrade() {
        infoBox = "\n -->" + order.toString() + infoBox;
        /* Use the Common Client Interface */
        try {
            response = connection.submitOrder(order);
            infoBox = "\n <--" + response.toString() + infoBox;
        } catch (TradeProcessingException ex) {
            infoBox = "\n <-- ERROR " + ex.getMessage() + infoBox;
        }
        
    }
    
    /* Getters and setters */
    public String getHost() { return "localhost"; }
    public int getPort() { return 4004; }
    public TradeOrder getOrder() { return order; }
    public void setInfoBox(String infoBox) { this.infoBox = infoBox; }
    public String getInfoBox() { return infoBox; }
    public TradeOrder.OrderClass[] getOrderClassList() {
        return TradeOrder.OrderClass.values();
    }
    public TradeOrder.OrderType[] getOrderTypeList() {
        return TradeOrder.OrderType.values();
    }
    public TradeOrder.Ticker[] getTickerList() {
        return TradeOrder.Ticker.values();
    }
}
