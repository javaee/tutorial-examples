/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.standalone.ejb;

import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ian
 */
public class StandaloneBeanTest {

    private EJBContainer ec;
    private Context ctx;
    private static final Logger logger = Logger.getLogger("standalone.ejb");

    public StandaloneBeanTest() {
    }

    @Before
    public void setUp() {
        ec = EJBContainer.createEJBContainer();
        ctx = ec.getContext();
    }

    @After
    public void tearDown() {
        if (ec != null) {
            ec.close();
        }
    }

    /**
     * Test of returnMessage method, of class StandaloneBean.
     * @throws java.lang.Exception
     */
    @Test
    public void testReturnMessage() throws Exception {
        logger.info("Testing standalone.ejb.StandaloneBean.returnMessage()");
        StandaloneBean instance = 
                (StandaloneBean) ctx.lookup("java:global/classes/StandaloneBean");
        String expResult = "Greetings!";
        String result = instance.returnMessage();
        assertEquals(expResult, result);
    }
}
