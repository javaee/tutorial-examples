/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.webserverlog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import javax.batch.api.chunk.ItemReader;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javaeetutorial.batch.webserverlog.items.LogLine;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/* Reads lines from the input log file */
@Dependent
@Named("LogLineReader")
public class LogLineReader implements ItemReader {

    private ItemNumberCheckpoint checkpoint;
    private String fileName;
    private BufferedReader breader;
    @Inject
    private JobContext jobCtx;

    public LogLineReader() {
    }

    @Override
    public void open(Serializable ckpt) throws Exception {
        /* Use the checkpoint if this is a restart */
        if (ckpt == null) {
            checkpoint = new ItemNumberCheckpoint();
        } else {
            checkpoint = (ItemNumberCheckpoint) ckpt;
        }

        /* Read from the log file included with the application
         * (webserverlog/WEB-INF/classes/log1.txt) */
        fileName = jobCtx.getProperties().getProperty("log_file_name");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(fileName);
        breader = new BufferedReader(new InputStreamReader(iStream));

        /* Continue where we left off if this is a restart */
        for (int i = 0; i < checkpoint.getLineNum(); i++) {
            breader.readLine();
        }
    }

    @Override
    public void close() throws Exception {
        breader.close();
    }

    @Override
    public Object readItem() throws Exception {
        /* Return a LogLine object */
        String entry = breader.readLine();
        if (entry != null) {
            checkpoint.nextLine();
            return new LogLine(entry);
        } else {
            return null;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return checkpoint;
    }
}
