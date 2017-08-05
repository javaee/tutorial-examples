/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.exception;

/**
 * <p>This application exception indicates that an order cannot be
 * completed.</p>
 */
public class OrderException extends Exception {

    private static final long serialVersionUID = -5058707185180716794L;

    public OrderException() {
    }

    public OrderException(String msg) {
        super(msg);
    }
}
