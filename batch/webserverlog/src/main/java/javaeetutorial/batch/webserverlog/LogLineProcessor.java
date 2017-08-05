/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.webserverlog;

import java.util.Properties;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javaeetutorial.batch.webserverlog.items.LogLine;
import javaeetutorial.batch.webserverlog.items.LogFilteredLine;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/* Processes items from the log file
 * Filters only those items from mobile or tablet browsers,
 * depending on the properties specified in the job definition file.
 */
@Dependent
@Named("LogLineProcessor")
public class LogLineProcessor implements ItemProcessor {

    private String[] browsers;
    private int nbrowsers = 0;
    @Inject
    private JobContext jobCtx;

    public LogLineProcessor() {
    }

    @Override
    public Object processItem(Object item) {
        /* Obtain a list of browsers we are interested in */
        if (nbrowsers == 0) {
            Properties props = jobCtx.getProperties();
            nbrowsers = Integer.parseInt(props.getProperty("num_browsers"));
            browsers = new String[nbrowsers];
            for (int i = 1; i < nbrowsers + 1; i++) {
                browsers[i - 1] = props.getProperty("browser_" + i);
            }
        }

        LogLine logline = (LogLine) item;
        /* Filter for only the mobile/tablet browsers as specified */
        for (int i = 0; i < nbrowsers; i++) {
            if (logline.getBrowser().equals(browsers[i])) {
                /* The new items have fewer fields */
                return new LogFilteredLine(logline);
            }
        }
        return null;
    }
}
