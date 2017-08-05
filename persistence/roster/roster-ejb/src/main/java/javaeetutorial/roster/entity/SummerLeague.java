/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.roster.entity;

import java.io.Serializable;
import javaeetutorial.roster.util.IncorrectSportException;
import javax.persistence.Entity;

@Entity
public class SummerLeague extends League implements Serializable {
    private static final long serialVersionUID = 4846138039113922695L;
    
    /** Creates a new instance of SummerLeague */
    public SummerLeague() {
    }
    
    public SummerLeague(String id, String name, String sport) 
            throws IncorrectSportException {
        this.id = id;
        this.name = name;
        if (sport.equalsIgnoreCase("swimming") ||
                sport.equalsIgnoreCase("soccer") ||
                sport.equalsIgnoreCase("basketball") ||
                sport.equalsIgnoreCase("baseball")) {
            this.sport = sport;
        } else {
            throw new IncorrectSportException("Sport is not a summer sport.");
        }
    }
}
