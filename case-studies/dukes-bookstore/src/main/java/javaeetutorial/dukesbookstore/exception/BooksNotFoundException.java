/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.exception;

/**
 * <p>This application exception indicates that books have not been found.</p>
 */
public class BooksNotFoundException extends Exception {

    private static final long serialVersionUID = 4156679691884326238L;

    public BooksNotFoundException() {
    }

    public BooksNotFoundException(String msg) {
        super(msg);
    }
}
