/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.rsvp.util;

public enum ResponseEnum {
    ATTENDING("Attending"),
    NOT_ATTENDING("Not attending"),
    MAYBE_ATTENDING("Maybe"),
    NOT_RESPONDED("No response yet");
    
    private final String label;
    
    private ResponseEnum(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
}
