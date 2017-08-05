/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.trading.rar.api;

/* Represents a trade order for the EIS */
public class TradeOrder {

    public enum OrderType { BUY, SELL };
    public enum OrderClass { MARKET };
    public enum Ticker { WWWW, YYYY, ZZZZ };
    
    private OrderType orderType;
    private int nShares;
    private Ticker ticker;
    private OrderClass orderClass;
    
    public TradeOrder() {
        orderType = OrderType.BUY;
        nShares = 100;
        ticker = Ticker.YYYY;
        orderClass = OrderClass.MARKET;
    }
    
    @Override
    public String toString() {
        return String.format("%s %d %s %s", orderType.toString(),
                                            nShares, ticker,
                                            orderClass.toString());
    }
    
    /* Getters and setters */
    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }
    public int getNShares() { return nShares; }
    public void setNShares(int nShares) { this.nShares = nShares; }
    public Ticker getTicker() { return ticker; }
    public void setTicker(Ticker ticker) { this.ticker = ticker; }
    public OrderClass getOrderClass() { return orderClass; }
    public void setOrderClass(OrderClass orderClass) { this.orderClass = orderClass; }
}
