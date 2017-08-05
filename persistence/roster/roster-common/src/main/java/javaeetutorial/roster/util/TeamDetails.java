/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.roster.util;

import java.io.Serializable;


public class TeamDetails implements Serializable {
    private static final long serialVersionUID = -1618941013515364318L;
    private String id;
    private String name;
    private String city;

    public TeamDetails(String id, String name, String city) {

        this.id = id;
        this.name = name;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        String s = id + " " + name + " " + city;

        return s;
    }

}
