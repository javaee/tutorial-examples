/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.clientsessionmdb.appclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.clientsessionmdb.sb.PublisherRemote;
import javax.ejb.EJB;

/**
 * The MyAppClient class is the client program for this application. It calls
 * the publisher's publishNews method twice.
 */
public class MyAppClient {

    static final Logger logger = Logger.getLogger("MyAppClient");
    
    @EJB
    private static PublisherRemote publisher;

    public MyAppClient(String[] args) {
    }

    public static void main(String[] args) {
        MyAppClient client = new MyAppClient(args);
        client.doTest();
        System.exit(0);
    }

    public void doTest() {
        try {
            publisher.publishNews();
            publisher.publishNews();
            System.out.println("To view the bean output,");
            System.out.println(
                    " check <install_dir>/domains/domain1/logs/server.log.");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception:{0}", ex.toString());
        }
    }
}
