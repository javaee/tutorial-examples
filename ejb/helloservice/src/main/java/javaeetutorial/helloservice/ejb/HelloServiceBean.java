/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.helloservice.ejb;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * HelloServiceBean is a web service endpoint implemented as a stateless
 * session bean.
 */

@Stateless
@WebService
public class HelloServiceBean {
    private final String message = "Hello, ";
    
    public void HelloServiceBean() {}
    
    @WebMethod
    public String sayHello(String name) {
        return message + name + ".";
    }
}
