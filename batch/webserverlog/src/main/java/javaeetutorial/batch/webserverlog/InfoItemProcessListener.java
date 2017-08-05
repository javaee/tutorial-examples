/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.webserverlog;

import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.batch.webserverlog.items.LogLine;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("InfoItemProcessListener")
public class InfoItemProcessListener implements ItemProcessListener {
    
    private static final Logger logger = 
            Logger.getLogger("InfoItemProcessListener");
    
    public InfoItemProcessListener() { }

    @Override
    public void beforeProcess(Object o) throws Exception {
        LogLine logline = (LogLine) o;
        logger.log(Level.INFO, "Processing entry {0}", logline);
    }

    @Override
    public void afterProcess(Object o, Object o1) throws Exception { }

    @Override
    public void onProcessError(Object o, Exception excptn) throws Exception {
        LogLine logline = (LogLine) o;
        logger.log(Level.WARNING, "Error processing entry {0}", logline);
    }
    
}
