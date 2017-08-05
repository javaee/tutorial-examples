/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukesbookstore.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * <p>Singleton bean that initializes the book database for the bookstore
 * example.</p>
 */
@Singleton
@Startup
public class ConfigBean {

    @EJB
    private BookRequestBean request;

    @PostConstruct
    public void createData() {
        request.createBook("201", "Duke", "",
                "My Early Years: Growing Up on *7",
                30.75, false, 2005, "What a cool book.", 20);
        request.createBook("202", "Jeeves", "",
                "Web Servers for Fun and Profit", 40.75, true,
                2010, "What a cool book.", 20);
        request.createBook("203", "Masterson", "Webster",
                "Web Components for Web Developers",
                27.75, false, 2010, "What a cool book.", 20);
        request.createBook("205", "Novation", "Kevin",
                "From Oak to Java: The Revolution of a Language",
                10.75, true, 2008, "What a cool book.", 20);
        request.createBook("206", "Thrilled", "Ben",
                "The Green Project: Programming for Consumer Devices",
                30.00, true, 2008, "What a cool book.", 20);
        request.createBook("207", "Coding", "Happy",
                "Java Intermediate Bytecodes", 30.95, true,
                2010, "What a cool book.", 20);

    }
}
