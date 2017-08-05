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
import javax.batch.api.listener.JobListener;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("InfoJobListener")
public class InfoJobListener implements JobListener {

    private static final Logger logger = Logger.getLogger("InfoJobListener");
    
    public InfoJobListener() { }
    
    @Override
    public void beforeJob() throws Exception {
        logger.log(Level.INFO, "The job is starting");
    }

    @Override
    public void afterJob() throws Exception {
        logger.log(Level.INFO, "The job has finished.");
    }
    
}
