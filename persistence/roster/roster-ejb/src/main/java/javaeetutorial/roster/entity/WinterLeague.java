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
public class WinterLeague extends League implements Serializable {
    private static final long serialVersionUID = 8942582153559426625L;
    
    /** Creates a new instance of WinterLeague */
    public WinterLeague() {
    }
    
    public WinterLeague(String id, String name, String sport) throws 
            IncorrectSportException {
        this.id = id;
        this.name = name;
        if (sport.equalsIgnoreCase("hockey") ||
                sport.equalsIgnoreCase("skiing") ||
                sport.equalsIgnoreCase("snowboarding")) {
            this.sport = sport;
        } else {
            throw new IncorrectSportException("Sport is not a winter sport.");
        }
    }
    
}
