/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.roster.util;

import java.io.Serializable;


public class LeagueDetails implements Serializable {
    private static final long serialVersionUID = 290368886584321980L;
    private String id;
    private String name;
    private String sport;

    public LeagueDetails(String id, String name, String sport) {

        this.id = id;
        this.name = name;
        this.sport = sport;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSport() {
        return sport;
    }

    @Override
    public String toString() {
        String s = id + " " + name + " " + sport;
        return s;
    }

}
