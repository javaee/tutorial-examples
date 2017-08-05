/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.web.websocketbot.messages;

/* Represents a chat message */
public class ChatMessage extends Message {
    private String name;
    private String target;
    private String message;
    
    public ChatMessage(String name, String target, String message) {
        this.name = name;
        this.target = target;
        this.message = message;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTarget() {
        return target;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    /* For logging purposes */
    @Override
    public String toString() {
        return "[ChatMessage] " + name + "-" + target + "-" + message;
    }
}
