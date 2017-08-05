/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
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
