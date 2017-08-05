/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.roster.util;

public class IncorrectSportException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>IncorrectSportException</code> without detail message.
     */
    public IncorrectSportException() {
    }
    
    
    /**
     * Constructs an instance of <code>IncorrectSportException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IncorrectSportException(String msg) {
        super(msg);
    }
}
