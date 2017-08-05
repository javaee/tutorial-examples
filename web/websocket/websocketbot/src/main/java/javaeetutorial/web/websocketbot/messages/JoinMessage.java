/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.web.websocketbot.messages;

/* Represents a join message for the chat */
public class JoinMessage extends Message {    
    private String name;
    
    public JoinMessage(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    /* For logging purposes */
    @Override
    public String toString() {
        return "[JoinMessage] " + name;
    }
}
