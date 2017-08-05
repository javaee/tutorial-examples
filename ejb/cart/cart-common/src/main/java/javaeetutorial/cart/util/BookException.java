/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.cart.util;

public class BookException extends Exception {
    private static final long serialVersionUID = 6274585742564840905L;
    public BookException() {
    }

    public BookException(String msg) {
        super(msg);
    }
}
